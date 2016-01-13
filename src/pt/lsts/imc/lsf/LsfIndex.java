/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2016, Laboratório de Sistemas e Tecnologia Subaquática
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of IMC, LSTS, IMCJava nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL LABORATORIO DE SISTEMAS E TECNOLOGIA SUBAQUATICA
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)  
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 * $Id:: LsfIndex.java 392 2013-02-28 17:26:14Z zepinto@gmail.com              $:
 */
package pt.lsts.imc.lsf;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.Vector;

import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCFieldType;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCMessageType;
import pt.lsts.imc.gz.MultiMemberGZIPInputStream;
import pt.lsts.imc.net.UDPTransport;

/**
 * This class processes a lsf log file and generates a respective index which
 * can be used for a more efficient access to the messages in the log<br/>
 * 
 * @author zp
 * @author pdias
 */
public class LsfIndex {

	/*
	 * IMPORTANT NOTE ON MappedByteBuffer: You'll notice that there is no unmap(
	 * )method. Once established, a mapping remains in effect until the
	 * MappedByteBufferobject is garbage collected. Unlike locks, mapped buffers
	 * are not tied to the channel that created them. Closing the associated
	 * FileChanneldoes not destroy the mapping; only disposal of the buffer
	 * object itself breaks the mapping.
	 */

	protected static final String FILENAME = "mra/lsf.index";

	protected IMCDefinition defs;

	protected File lsfFile;
	// protected MappedByteBuffer lsfBuffer;
	protected BigByteBuffer buffer;
	protected LinkedHashMap<Long, MappedByteBuffer> buffers = new LinkedHashMap<Long, MappedByteBuffer>();

	protected double startTime, curTime, endTime;
	protected MappedByteBuffer index;
	protected long indexSize;
	protected int numMessages;
	protected int generatorSrcId;

	protected RandomAccessFile lsfInputStream;
	protected FileInputStream indexInputStream;
	protected FileChannel lsfChannel;
	protected FileChannel indexChannel;
	protected LsfIndexListener listener = null;

	protected int HEADER_SIZE = 12;
	protected int ENTRY_SIZE = 14;
	protected int OFFSET_OF_TIME = 0, OFFSET_OF_MGID = 4, OFFSET_OF_POS = 6;
	
	// cache indexes for first and last occurrence of messages
	private LinkedHashMap<Integer, Integer> firstMessagesOfType = new LinkedHashMap<Integer, Integer>();
	private LinkedHashMap<Integer, Integer> lastMessagesOfType = new LinkedHashMap<Integer, Integer>();

	/*
	 * [Header]: 12 bytes 0 - 'I' 1 - 'D' 2 - 'X' 3 - '1' 4 - start timestamp
	 * (double)
	 * 
	 * [Entry]: 10 bytes 0 - time (int) 4 - mgid (short) 6 - pos (long)
	 */

	/**
	 * This method calculates an hashcode based on source and entity of the
	 * message at index i
	 * 
	 * @param i
	 *            The index of the message to calculate hashcode
	 * @return The calculated hashcode
	 */
	public int hashOf(int i) {
		return (sourceOf(i) << 8) | entityOf(i);
	}

	public int advanceToTime(int startIndex, double timestamp) {
		int desired_source = sourceOf(0);
		int curIndex = startIndex;

		while (curIndex < numMessages) {
			if (sourceOf(curIndex) != desired_source) {
				curIndex++;
				continue;
			}
			if (timeOf(curIndex) >= timestamp)
				return curIndex;
			curIndex++;
		}
		return -1;
	}

	public void load(File lsfFile, IMCDefinition defs) throws Exception {
		this.lsfFile = lsfFile;
		if (!lsfFile.getName().endsWith(".lsf")
				&& !lsfFile.getName().endsWith(".lsf.gz")) {
			throw new Exception("The file is not lsf!");
		}
		if (defs == null) {
			if (new File(lsfFile.getParent(), "IMC.xml.gz").canRead()) {
				defs = new IMCDefinition(new File(lsfFile.getParent(),
						"IMC.xml.gz"));
			} else if (new File(lsfFile.getParent(), "IMC.xml").canRead()) {
				defs = new IMCDefinition(new File(lsfFile.getParent(),
						"IMC.xml"));
			} else {
				defs = IMCDefinition.getInstance();
			}
		}

		this.defs = defs;

		if (lsfFile.getName().endsWith(".lsf.gz")) {

			MultiMemberGZIPInputStream mmgis = new MultiMemberGZIPInputStream(
					new FileInputStream(this.lsfFile));
			File outFile = new File(this.lsfFile.getAbsolutePath().replaceAll(
					"\\.gz$", ""));
			outFile.createNewFile();
			FileOutputStream outStream = new FileOutputStream(outFile);
			try {
				byte[] extra = new byte[50000];
				int ret = 0;
				for (;;) {
					ret = mmgis.read(extra);
					if (ret != -1) {
						byte[] extra1 = new byte[ret];
						System.arraycopy(extra, 0, extra1, 0, ret);
						outStream.write(extra1);
						outStream.flush();
					} else {
						break;
					}
				}
				this.lsfFile = outFile;
			} catch (IOException e) {
				e.printStackTrace();
			}
			outStream.close();
			mmgis.close();
		}

		// FIXME
		lsfInputStream = new RandomAccessFile(lsfFile, "r");
		lsfChannel = lsfInputStream.getChannel();
		buffer = new BigByteBuffer(lsfChannel, lsfFile.length());

		loadIndex();

		listener = null;
	}

	public LsfIndex(File lsfFile, IMCDefinition defs, LsfIndexListener listener)
			throws Exception {
		this.listener = listener;
		load(lsfFile, defs);
	}

	/**
	 * Class constructor. Creates a new index from given lsf File and IMC
	 * definitions.<br/>
	 * This method will first look for an lsf.index file and load it. If none is
	 * found, the index is generated first.
	 * 
	 * @param lsfFile
	 *            The file, in lsf format with the messages log
	 * @param defs
	 *            The IMC definitions for that log
	 * @throws Exception
	 *             If the log file is not valid / cannot be read.
	 */
	public LsfIndex(File lsfFile, IMCDefinition defs) throws Exception {
		this(lsfFile, defs, new LsfIndexListener() {

			@Override
			public void updateStatus(String messageToDisplay) {
				System.out.println("[LsfIndex] " + messageToDisplay);

			}
		});
	}

	public LsfIndex(File lsfFile) throws Exception {
		if (lsfFile.isDirectory()) {
			if (new File(lsfFile.getParentFile(), "Data.lsf").canRead())
				lsfFile = new File(lsfFile.getParentFile(), "Data.lsf");
			else if (new File(lsfFile.getParentFile(), "Data.lsf.gz").canRead())
				lsfFile = new File(lsfFile.getParentFile(), "Data.lsf.gz");
		}
		IMCDefinition defs = null;

		try {
			defs = new IMCDefinition(new FileInputStream(new File(
					lsfFile.getParent(), "IMC.xml")));
		} catch (Exception e) {
			defs = IMCDefinition.getInstance();
		}
		this.lsfFile = lsfFile;

		load(lsfFile, defs);
	}

	protected void loadIndex() throws Exception {
		checkIndex();
		new File(lsfFile.getParent(), "mra").mkdirs();

		indexInputStream = new FileInputStream(new File(lsfFile.getParent(),
				FILENAME));
		indexSize = new File(lsfFile.getParent(), FILENAME).length();
		indexChannel = indexInputStream.getChannel();
		index = indexChannel.map(MapMode.READ_ONLY, 0, indexSize);
		if (index.get() != 'I' || index.get() != 'D' || index.get() != 'X'
				|| index.get() != '1') {
			throw new Exception(
					"The index file is not valid. Please regenerate the index.");
		}
		curTime = startTime = index.getDouble();
		numMessages = (int) indexSize / ENTRY_SIZE;
		endTime = getEndTime();
		generatorSrcId = getMessage(0).getHeader().getInteger("src");
	}

	/**
	 * This method retrieves the bytes associated with the message at given
	 * index
	 * 
	 * @param ind
	 *            The index of the message
	 * @return The bytes, in the log file respective to the message
	 */
	public byte[] getMessageBytes(int ind) {
		int len;
		if (ind < getNumberOfMessages() - 1)
			len = (int) (positionOf(ind + 1) - positionOf(ind));
		else
			len = (int) (lsfFile.length() - positionOf(ind));
		byte[] arr = new byte[len];

		buffer.position(positionOf(ind));
		buffer.getBuffer().get(arr);
		return arr;
	}

	/**
	 * Retrieve the type of message at given index
	 * 
	 * @param messageNumber
	 *            The index of the message
	 * @return The IMC type (id) for the message at given index
	 */
	public int typeOf(int messageNumber) {
		if (messageNumber > numMessages)
			return -1;
		return index.getShort(HEADER_SIZE + messageNumber * ENTRY_SIZE
				+ OFFSET_OF_MGID) & 0xFFFF;
	}

	/**
	 * Retrieve the time, in seconds since January 1st 1970 UTC of the given
	 * message
	 * 
	 * @param messageNumber
	 *            The index of the message in the log
	 * @return Time, in seconds since January 1st 1970 UTC of the given message
	 */
	public double timeOf(int messageNumber) {
		if (messageNumber > numMessages)
			return Double.NaN;
		return startTime
				+ index.getInt(HEADER_SIZE + messageNumber * ENTRY_SIZE
						+ OFFSET_OF_TIME) / 1000.0;
	}

	public synchronized boolean isBigEndian(int messageNumber) {
		// offset for the sync number in the header is 0
		if (messageNumber < 0 || messageNumber >= numMessages)
			return false;

		buffer.position(positionOf(messageNumber) + 0);
		return !((buffer.getBuffer().get() & 0xFF) == 0xFE);
	}

	public String sourceNameOf(int messageNumber) {
		int src = sourceOf(messageNumber);
		if (src == -1)
			return null;

		return defs.getResolver().resolve(src);
	}

	public String entityNameOf(int messageNumber) {
		int src = sourceOf(messageNumber);
		int src_ent = entityOf(messageNumber);

		return getEntityName(src, src_ent);
	}

	public int sizeOf(int messageNumber) {
		if (messageNumber < getNumberOfMessages() - 1)
			return (int) (positionOf(messageNumber + 1) - positionOf(messageNumber));
		else
			return (int) (lsfFile.length() - positionOf(messageNumber));
	}

	public synchronized int sourceOf(int messageNumber) {
		if (messageNumber < 0 || messageNumber >= numMessages)
			return -1;

		// offset for the sync number in the header is 0
		buffer.position(positionOf(messageNumber) + 0);
		boolean bigEndian = !((buffer.getBuffer().get() & 0xFF) == 0xFE);

		// offset for the source in the header is 14
		buffer.position(positionOf(messageNumber) + 14);

		if (bigEndian)
			return buffer.getBuffer().getShort() & 0xFFFF;
		else
			return Short.reverseBytes(buffer.getBuffer().getShort()) & 0xFFFF;
	}

	public synchronized int entityOf(int messageNumber) {
		if (messageNumber < 0 || messageNumber >= numMessages)
			return -1;

		// offset for src_ent in the header is 16
		buffer.position(positionOf(messageNumber) + 16);

		return buffer.getBuffer().get() & 0xFF;
	}

	public synchronized int fieldIdOf(int messageNumber) {
		if (messageNumber < 0 || messageNumber >= numMessages)
			return -1;

		IMCMessageType type = defs.getType(typeOf(messageNumber));

		if (type.getOffsetOf("id") == 0
				&& type.getFieldType("id") == IMCFieldType.TYPE_UINT8) {
			buffer.position(positionOf(messageNumber) + defs.headerLength());
			return buffer.getBuffer().get() & 0xFF;
		}
		return -1;
	}

	/**
	 * Retrieve the offset in the lsf File for message at given index
	 * 
	 * @param messageNumber
	 *            The index of the message
	 * @return The offset in the lsf File for message at given index
	 */
	public long positionOf(int messageNumber) {
		if (messageNumber > numMessages)
			return -1;
		return index.getLong(HEADER_SIZE + messageNumber * ENTRY_SIZE
				+ OFFSET_OF_POS);
	}

	@SuppressWarnings("unchecked")
	public synchronized <T> T getMessage(int messageNumber, Class<T> clazz)
			throws Exception {
		IMCMessage m = getMessage(messageNumber);
		if (m.getClass() == clazz)
			return (T) m;
		T n = clazz.getConstructor(IMCDefinition.class).newInstance(
				getDefinitions());
		((IMCMessage) n).copyFrom(m);
		return n;
	}

	/**
	 * Get an iterator to traverse all messages in the log from a selected
	 * entity name
	 * 
	 * @param entityName The entity to be iterated
	 * @return A message iterator that 
	 */
	public Iterable<IMCMessage> iterateEntityMessages(String entityName) {
		return new LsfIndexEntityInterator(this, entityName);
	}

	public Iterable<IMCMessage> getIterator(String msgType) {
		return new LsfGenericIterator(this, msgType, 0, 0);
	}

	public Iterable<IMCMessage> getIterator(String msgType, int fromIndex) {
		return new LsfGenericIterator(this, msgType, fromIndex, 0);
	}

	public Iterable<IMCMessage> getIterator(String msgType, int fromIndex,
			long timestepMillis) {
		return new LsfGenericIterator(this, msgType, fromIndex, timestepMillis);
	}

	public <T> LsfIterator<T> getIterator(Class<T> msgType) {
		return new LsfIterator<T>(this, msgType);
	}

	public <T> LsfIterator<T> getIterator(Class<T> msgType, int fromIndex) {
		return new LsfIterator<T>(this, msgType, fromIndex);
	}

	public <T> LsfIterator<T> getIterator(Class<T> msgType,
			long millisBetweenMessages) {
		return new LsfIterator<T>(this, msgType, millisBetweenMessages);
	}

	/**
	 * Deserializes and retrieves the message at given index
	 * 
	 * @param messageNumber
	 *            The message index
	 * @return The IMCMessage corresponding to the deserialization of the
	 *         message at given index
	 */
	public synchronized IMCMessage getMessage(int messageNumber) {
		if (messageNumber > numMessages)
			return null;

		buffer.position(positionOf(messageNumber));

		try {
			return defs.nextMessage(buffer.getBuffer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return Total number of messages in the log
	 */
	public int getNumberOfMessages() {
		return numMessages;
	}

	protected void createIndex() throws Exception {
		if (listener != null)
			listener.updateStatus("Creating mra/lsf.index...");

		buffer.position(0);
		new File(lsfFile.getParent(), "mra").mkdirs();
		new File(lsfFile.getParent(), FILENAME).delete();
		long progress = 0;
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(new File(lsfFile.getParent(), FILENAME))));

		double startTime = 0, time;
		long pos, newPos;
		int mgid, size, sync;
		int counter = 0;

		long len = lsfFile.length();

		while (buffer.getBuffer().remaining() > defs.headerLength()) {
			pos = buffer.position();
			if (pos == 0) {
				sync = buffer.getBuffer().getShort() & 0xFFFF;
				if (sync == defs.getSwappedWord())
					buffer.order(ByteOrder.LITTLE_ENDIAN);
			} else
				buffer.position(buffer.position() + 2);

			mgid = buffer.getBuffer().getShort() & 0xFFFF;
			size = buffer.getBuffer().getShort() & 0xFFFF;
			time = buffer.getBuffer().getDouble();

			if (pos == 0) {
				dos.write(new byte[] { 'I', 'D', 'X', '1' });
				dos.writeDouble(time);
				startTime = time;
			}

			dos.writeInt((int) ((time - startTime) * 1000.0));
			dos.writeShort(mgid);
			dos.writeLong(pos);

			counter++;
			newPos = buffer.position() + (defs.headerLength() - 12) + size;
			if (newPos > len - HEADER_SIZE)
				break;
			else
				buffer.position(newPos);

			long prog = (long) (pos * 100.0 / len);
			if (prog != progress) {
				if (listener != null)
					listener.updateStatus("Creating lsf.index... " + prog + "%");
				progress = prog;
			}
		}

		dos.close();
		System.out.println(counter + " messages indexed.");
		listener = null;
	}

	/**
	 * Same as {@link #startReplay(long) startReplay(1)}
	 */
	public void startReplay() {
		startReplay(1, "127.0.0.1", 6002);
	}

	/**
	 * Just for testing purposes. This method will start broadcasting all
	 * messages in the log according to time separations.
	 * 
	 * @param timeMultiplier
	 *            If 1.0 is used, the separation between messages will be
	 *            approximately the same as real-time. If this value is higher,
	 *            the replay will be done faster in the same proportion.
	 */
	public void startReplay(long timeMultiplier, String host, int port) {
		long localStartTime = System.currentTimeMillis();
		long lastPrint = 0;
		UDPTransport transport = new UDPTransport(defs);

		TimeZone tz = TimeZone.getTimeZone("UTC");

		DateFormat dfGMT = DateFormat.getTimeInstance(DateFormat.DEFAULT);
		dfGMT.setTimeZone(tz);

		for (int i = 0; i < numMessages - 1; i++) {
			long curTime = ((long) (1000 / timeMultiplier * (timeOf(i) - startTime)))
					+ localStartTime;
			long sleep_millis = curTime - System.currentTimeMillis();
			IMCMessage m = getMessage(i);

			transport.sendMessage("127.0.0.1", 6002, m);
			int src = m.getHeader().getInteger("src");

			if (src == generatorSrcId && sleep_millis > 5
					&& timeOf(i + 1) > timeOf(i)) {
				try {
					Thread.sleep(sleep_millis);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			long time = 1000 * (long) timeOf(i);
			if (src == generatorSrcId && time > lastPrint) {
				System.out.println(dfGMT.format(new Date(time)));
				lastPrint = time;
			}
		}
	}

	protected void checkIndex() throws Exception {
		if (!new File(lsfFile.getParent(), FILENAME).canRead())
			createIndex();
		else {
			if (listener != null)
				listener.updateStatus("Using existing lsf.index...");
		}

	}

	public int getFirstMessageOfType(String abbrev) {
		int type = defs.getMessageId(abbrev);
		return getFirstMessageOfType(type);
	}

	public <T> T getFirst(Class<T> clazz) {
		int index = getFirstMessageOfType(clazz.getSimpleName());
		if (index != -1) {
			try {
				return getMessage(index, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public <T> T getLast(Class<T> clazz) {
		int index = getLastMessageOfType(clazz.getSimpleName());
		if (index != -1) {
			try {
				return getMessage(index, clazz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	

	public int getFirstMessageOfType(int type) {
		if (firstMessagesOfType.containsKey(type))
			return firstMessagesOfType.get(type);
		
		for (int i = 0; i < numMessages; i++) {
			if (typeOf(i) == type) {
				firstMessagesOfType.put(type, i);
				return i;
			}
		}
		firstMessagesOfType.put(type, -1);
		return -1;
	}

	public int getLastMessageOfType(String abbrev) {
		int type = defs.getMessageId(abbrev);
		return getLastMessageOfType(type);
	}

	public int getLastMessageOfType(int type) {
		if (lastMessagesOfType.containsKey(type))
			return lastMessagesOfType.get(type);
		
		for (int i = numMessages - 1; i >= 0; i--) {
			if (typeOf(i) == type) {
				lastMessagesOfType.put(type, i);
				return i;
			}
		}
		lastMessagesOfType.put(type, -1);
		return -1;
	}

	public int getNextMessageOfType(int type, int startIndex) {
		if (startIndex == -1)
			return -1;

		for (int i = startIndex + 1; i < numMessages; i++) {
			if (typeOf(i) == type)
				return i;
		}
		return -1;
	}

	/**
	 * Retrieves the next message in the index that matches the given entity
	 * name
	 * 
	 * @param entity
	 *            The entity to look for
	 * @param startIndex
	 *            Only messages after this index will be tested
	 * @return The first message, with <code>index > startIndex</code> that has
	 *         a matching entity name.
	 */
	public int getNextMessageOfEntity(String entity, int startIndex) {
		if (startIndex == -1)
			return -1;

		for (int i = startIndex + 1; i < numMessages; i++) {
			if (entityNameOf(i).equals(entity))
				return i;
		}
		return -1;
	}

	/**
	 * This method returns the index of the next message with given type and
	 * source entity
	 * 
	 * @param type
	 *            The type of the message to look for
	 * @param entity
	 *            The source entity (src_ent field in the header) to look for
	 * @param startIndex
	 *            The index exactly before the index from where to start looking
	 * @return The index of the found message or -1 if no such message was found
	 */
	public int getNextMessageOfEntity(int type, int entity, int startIndex) {
		int curindex = startIndex;
		while (curindex != -1) {
			int next = getNextMessageOfType(type, curindex);
			if (next == -1)
				return -1;
			if (entityOf(next) == entity)
				return next;
			curindex = next;
		}
		return -1;
	}

	/**
	 * Retrieve all the entities generating the given message
	 * 
	 * @param message
	 *            The message
	 * @return A collection of entities that generated the given message in this
	 *         log
	 */
	public Collection<Integer> entitiesOfMessage(String message) {
		HashSet<Integer> ret = new HashSet<Integer>();
		for (int i = getFirstMessageOfType(message); i != -1; i = getNextMessageOfType(
				message, i))
			ret.add(entityOf(i));
		return ret;
	}

	public int getPreviousMessageOfType(String type, int lastIndex) {
		int mgid = defs.getMessageId(type);
		return getPreviousMessageOfType(mgid, lastIndex);		
	}
	
	public int getPreviousMessageOfType(int type, int lastIndex) {
		if (lastIndex == -1)
			return -1;

		lastIndex = Math.min(getNumberOfMessages(), lastIndex);
		
		for (int i = lastIndex - 1; i >= 0; i--) {
			if (typeOf(i) == type)
				return i;
		}
		return -1;
	}

	public int getNextMessageOfType(String abbrev, int startIndex) {
		int type = defs.getMessageId(abbrev);
		return getNextMessageOfType(type, startIndex);
	}

	public Iterable<IMCMessage> messagesOfType(int type) {
		return getIterator(defs.getMessageName(type));
	}

	public Iterable<IMCMessage> messagesOfType(String abbrev) {
		return messagesOfType(defs.getMessageId(abbrev));
	}

	/**
	 * Retrieve the IMC definitions used by this LsfIndex object
	 * 
	 * @return the IMC definitions used by this LsfIndex object
	 */
	public IMCDefinition getDefinitions() {
		return defs;
	}

	public boolean hasMultipleVehicles() {
		if (systemEntityNames == null)
			loadEntities();
		return systemEntityNames.size() > 1;
	}

	public boolean containsMessagesOfType(String... messageTypes) {
		for (String msg : messageTypes) {
			if (getFirstMessageOfType(msg) == -1)
				return false;
		}
		return true;
	}

	/**
	 * Retrieve the name of an entity for a specific vehicle.<br/>
	 * This method uses the EntityInfo messages in the log to calculate this
	 * value.
	 * 
	 * @param src
	 *            The IMC id of the system
	 * @param src_ent
	 *            The id of the entity to be resolved
	 * @return The name of the entity with id <i>src_ent</i> in the the vehicle
	 *         with id <i>src</i>.
	 */
	public String getEntityName(int src, int src_ent) {
		if (src_ent == 255)
			return "*";

		if (systemEntityNames == null)
			loadEntities();

		if (systemEntityNames.containsKey(src)) {
			return systemEntityNames.get(src).get(src_ent);
		}
		return "" + src_ent;
	}

	public String getSystemName(int sysId) {
		if (sysNames == null)
			loadEntities();

		if (!sysNames.containsKey(sysId))
			return sysId + "";
		else
			return sysNames.get(sysId);
	}

	/**
	 * This method uses the EntityInfo messages present in the log to calculate
	 * the name the of the given entity id.<br/>
	 * This method should be used only for single vehicle logs. Otherwise
	 * {@link #getEntityName(Integer, Integer)} should be used instead.
	 * 
	 * @param entityId
	 *            The (numeric) id of the entity to resolve
	 * @return The resolved name of the entity
	 */
	public String getEntityName(int entityId) {
		if (systemEntityNames == null)
			loadEntities();

		for (LinkedHashMap<Integer, String> ents : systemEntityNames.values()) {
			if (ents.containsKey(entityId))
				return ents.get(entityId);
		}

		return "" + entityId;
	}

	/**
	 * Look for an entity name in this log
	 * 
	 * @param entityName
	 *            The name of the entity to look for
	 * @return The entity id or <code>255</code> if the entity was not found
	 */
	public int getEntityId(String entityName) {
		if (systemEntityIds == null)
			loadEntities();

		for (LinkedHashMap<String, Integer> ents : systemEntityIds.values()) {
			if (ents.containsKey(entityName))
				return ents.get(entityName);
		}
		return 255;
	}

	protected void loadEntities() {

		loadSystems();

		int type = defs.getMessageId("EntityInfo");
		for (int i = getFirstMessageOfType(type); i != -1; i = getNextMessageOfType(
				type, i)) {
			IMCMessage einfo = getMessage(i);

			int src = einfo.getInteger("src");

			if (!(systemEntityIds.containsKey(src))) {
				systemEntityIds.put(src, new LinkedHashMap<String, Integer>());
				systemEntityNames
						.put(src, new LinkedHashMap<Integer, String>());
			}

			systemEntityIds.get(src).put(einfo.getString("label"),
					einfo.getInteger("id"));
			systemEntityNames.get(src).put(einfo.getInteger("id"),
					einfo.getString("label"));
			
			if (defs != IMCDefinition.getInstance())
				defs.getResolver().setEntityName(src, einfo.getInteger("id"),
						einfo.getString("label"));
		}
		
		type = defs.getMessageId("EntityList");
		for (int i = getFirstMessageOfType(type); i != -1; i = getNextMessageOfType(
				type, i)) {
			IMCMessage einfo = getMessage(i);

			int src = einfo.getInteger("src");

			if (!(systemEntityIds.containsKey(src))) {
				systemEntityIds.put(src, new LinkedHashMap<String, Integer>());
				systemEntityNames
						.put(src, new LinkedHashMap<Integer, String>());
			}
			
			LinkedHashMap<String, String> entities = einfo.getTupleList("list");
			
			for (Entry<String, String> entry : entities.entrySet()) {
				systemEntityIds.get(src).put(entry.getKey(),
						Integer.parseInt(entry.getValue()));
				systemEntityNames.get(src).put(Integer.parseInt(entry.getValue()),
						entry.getKey());
			}
			
			if (defs != IMCDefinition.getInstance())
				defs.getResolver().setEntityName(src, einfo.getInteger("id"),
						einfo.getString("label"));
		}
	}

	protected LinkedHashMap<Integer, LinkedHashMap<Integer, String>> systemEntityNames = null;
	protected LinkedHashMap<Integer, LinkedHashMap<String, Integer>> systemEntityIds = null;
	protected LinkedHashMap<Integer, String> sysNames = null;

	protected boolean isMultiSystemLog = false;

	protected void loadSystems() {

		sysNames = new LinkedHashMap<Integer, String>();
		systemEntityIds = new LinkedHashMap<Integer, LinkedHashMap<String, Integer>>();
		systemEntityNames = new LinkedHashMap<Integer, LinkedHashMap<Integer, String>>();

		int type = defs.getMessageId("Announce");
		for (int i = getFirstMessageOfType(type); i != -1; i = getNextMessageOfType(
				type, i + 1)) {
			IMCMessage m = getMessage(i);
			String sys_name = m.getString("sys_name");
			int src = m.getInteger("src");

			if (!sysNames.containsKey(src))
				sysNames.put(src, sys_name);

			defs.getResolver().addEntry(src, sys_name);

			if (src < 0x4001) {
				systemEntityNames
						.put(src, new LinkedHashMap<Integer, String>());
				systemEntityIds.put(src, new LinkedHashMap<String, Integer>());
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		cleanup();
		super.finalize();
	}

	/**
	 * This must be called to break the mapping and free memory.
	 */
	public void cleanup() {
		if (buffer != null)
			buffer = null;
		if (index != null)
			index = null;
		if (lsfChannel != null) {
			try {
				lsfChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (indexChannel != null) {
			try {
				indexChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (lsfInputStream != null) {
			try {
				lsfInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (indexInputStream != null) {
			try {
				indexInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.gc(); // To force the unmapping
	}

	public Collection<Announce> getAvailableSystems() {
		int announce_id = getDefinitions().getMessageId("Announce");
		LinkedHashMap<String, Announce> announces = new LinkedHashMap<String, Announce>();

		for (int i = getFirstMessageOfType(announce_id); i != -1; i = getNextMessageOfType(
				announce_id, i)) {
			try {
				Announce an = new Announce();
				an.copyFrom(getMessage(i));
				announces.put(an.getSysName(), an);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return announces.values();
	}

	public <T> T getNext(double timestamp, Class<T> clazz) {
		return getFirst(clazz);
	}

	public Vector<Announce> getSystemsOfType(Announce.SYS_TYPE type) {
		Vector<Announce> announces = new Vector<Announce>();

		for (Announce an : getAvailableSystems()) {
			if (an.getSysType() == type)
				announces.add(an);
		}

		return announces;
	}

	public int getMessageAtOrAfer(int type, int entity, int startIndex,
			double timestamp) {
		for (int i = getNextMessageOfType(type, startIndex); i != -1; i = getNextMessageOfType(
				type, i)) {
			if (entity != 255 && entityOf(i) != entity)
				continue;
			if (timeOf(i) < timestamp)
				continue;
			return i;
		}
		return -1;
	}
	
	public int getMessageBeforeOrAt(int type, int entity, int lastIndex, double timestamp) {
		for (int i = getPreviousMessageOfType(type, lastIndex); i >= 0; i = getPreviousMessageOfType(type, i)) {
			if (entity != 255 && entityOf(i) != entity)
				continue;
			if (timeOf(i) > timestamp)
				continue;
			return i;
		}
		return -1;
	}


	public double getStartTime() {
		return timeOf(0);
	}

	public double getEndTime() {
		int idx = numMessages - 1;
		return timeOf(idx);
	}

	public IMCMessage getMessageAtOrAfter(String type, String entity,
			int startIndex, double timestamp) {
		if (entity != null) {
			int entityId = getEntityId(entity);
			return getMessageAtOrAfter(type, startIndex, entityId, timestamp);
		} else {
			return getMessageAtOrAfter(type, startIndex, timestamp);
		}
	}
	
	public IMCMessage getMessageBeforeOrAt(String type, String entity, int lastIndex, double timestamp) {
		if (entity != null) {
			int entityId = getEntityId(entity);
			return getMessageBeforeOrAt(type, entityId, lastIndex, timestamp);
		} else {
			return getMessageBeforeOrAt(type, 0xFF, lastIndex, timestamp);
		}
	}
	
	protected LinkedHashMap<Integer, IndexCache> latestRetrievals = new LinkedHashMap<Integer, LsfIndex.IndexCache>();

	class IndexCache implements Comparable<IndexCache> {
		public int index;
		public Double timestamp;

		@Override
		public int compareTo(IndexCache o) {
			return timestamp.compareTo(o.timestamp);
		}

		public IndexCache(int index, double time) {
			this.index = index;
			this.timestamp = time;
		}
	}

	public int getMsgIndexAt(String type, double timestamp) {
		if (timestamp < startTime || timestamp > endTime)
			return -1;

		int msgType = defs.getMessageId(type);
		int finderPos = (int) (((timestamp - startTime) / (endTime - startTime)) * numMessages);

		if (latestRetrievals.get(msgType) != null) {
			if (Math.abs(latestRetrievals.get(msgType).timestamp - timestamp) <= 1)
				finderPos = latestRetrievals.get(msgType).index;
		}

		for (int i = getPreviousMessageOfType(msgType, finderPos); i != -1
				&& timeOf(i) > timestamp; i = getPreviousMessageOfType(msgType,
				i)) {
			finderPos = i;
		}

		for (int i = getNextMessageOfType(msgType, finderPos); i != -1
				&& timeOf(i) < timestamp; i = getNextMessageOfType(msgType, i)) {
			finderPos = i;
		}

		if (finderPos != -1)
			latestRetrievals.put(msgType, new IndexCache(finderPos,
					timeOf(finderPos)));

		return finderPos;

	}

	private int binarySearch(double targetTime, int startIndex, int endIndex) {
		if (endIndex <= startIndex)
			return startIndex;

		int pivot = (endIndex - startIndex) / 2 + startIndex;

		if (timeOf(pivot) < targetTime)
			return binarySearch(targetTime, pivot + 1, endIndex);
		else if (timeOf(pivot) >= targetTime)
			return binarySearch(targetTime, startIndex, pivot - 1);
		else
			return pivot;
	}

	public int getFirstMessageAtOrAfter(double timestamp) {
		return binarySearch(timestamp, 0, getNumberOfMessages() - 1);
	}

	public IMCMessage getMessageAt(String type, double timestamp) {

		int idx = getMsgIndexAt(type, timestamp);
		if (idx == -1)
			return null;

		return getMessage(idx);
	}

	public <T extends IMCMessage> T nextMessageOfType(Class<T> type,
			int startIndex) {
		int i = getNextMessageOfType(type.getSimpleName(), startIndex);
		if (i == -1)
			return null;
		try {
			return getMessage(i, type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public IMCMessage getMessageBeforeOrAt(String type, int lastIndex,
			double timestamp) {
		return getMessageBeforeOrAt(type, 0xFF, lastIndex, timestamp);
	}
	
	public IMCMessage getMessageAtOrAfter(String type, int startIndex,
			double timestamp) {
		return getMessageAtOrAfter(type, startIndex, 0xFF, timestamp);
	}

	public IMCMessage getMessageAtOrAfter(String type, int startIndex,
			int entity, double timestamp) {
		for (int i = getNextMessageOfType(type, startIndex); i != -1; i = getNextMessageOfType(
				type, i)) {
			if (entity != 255 && entityOf(i) != entity)
				continue;
			if (timeOf(i) < timestamp)
				continue;
			return getMessage(i);
		}
		return null;
	}
	
	public IMCMessage getMessageBeforeOrAt(String type, int entityId, int lastIndex, double timestamp) {
		int mgid = defs.getMessageId(type);
		int idx = getMessageBeforeOrAt(mgid, entityId, lastIndex, timestamp);
		if (idx == -1)
			return null;
		else
			return getMessage(idx);
	}

	/**
	 * @return the lsfFile
	 */
	public File getLsfFile() {
		return lsfFile;
	}

	public static void main(String[] args) throws Exception {
		LsfIndex index = new LsfIndex(new File(""));
		double endTime = index.getEndTime();
		double startTime = index.getStartTime();
		double pivot = (endTime + startTime) / 2.0;
		System.out.printf("%f --> %f --> %f\n", endTime, pivot, startTime);
		
		System.out.println(index.getMessageBeforeOrAt("EstimatedState", index.getNumberOfMessages(), pivot));
		System.out.println(index.getMessageAtOrAfter("EstimatedState", 0, pivot));
		
	}
}
