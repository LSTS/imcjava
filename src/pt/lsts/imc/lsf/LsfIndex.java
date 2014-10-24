/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 */
package pt.lsts.imc.lsf;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;

import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

/**
 * This class processes multiple lsf log files and generates a respective indexes which
 * can be used for a more efficient access to the messages in the log<br/>
 * 
 * @author zp
 * @author pdias
 */
public class LsfIndex {

	Vector<SingleLsfIndex> indexes = new Vector<SingleLsfIndex>();
	
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

		int numMessages = getNumberOfMessages();
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

	public LsfIndex(File lsfFile, LsfIndexListener listener, IMCDefinition defs)
			throws Exception {
		indexes.clear();
		SingleLsfIndex index = new SingleLsfIndex(lsfFile, listener);
		indexes.add(index);		
	}
	
	
	public SingleLsfIndex lsfIndexOf(int index) {
		int count = 0;
		for (SingleLsfIndex i : indexes) {
			if (index < count+i.getNumberOfMessages())
				return i;
			else
				count += i.getNumberOfMessages();
		}
		if (!indexes.isEmpty())
			return indexes.lastElement();
		return null;
	}
	
	public int messagesToSkip(int index) {
		int count = 0;
		for (SingleLsfIndex i : indexes) {
			if (count+i.getNumberOfMessages() < index)
				count += i.getNumberOfMessages();
			else
				return count;
		}
		return 0;
	}

	public LsfIndex(File[] lsfFiles, LsfIndexListener listener) throws Exception {
		indexes.clear();
		for (File f : lsfFiles) {
			try {
				if (listener != null)
					listener.updateStatus("Loading "+f.getAbsolutePath());
				SingleLsfIndex index = new SingleLsfIndex(f, listener);
				if (listener != null)
					listener.updateStatus("Finished loading "+f.getAbsolutePath());
				addIndex(index);
			}
			catch (Exception e) {
				e.printStackTrace();				
			}
		}
	}
	
	public LsfIndex(File[] lsfFiles) throws Exception {
		indexes.clear();
		for (File f : lsfFiles) {
			try {
				SingleLsfIndex index = new SingleLsfIndex(f);
				addIndex(index);
			}
			catch (Exception e) {
				e.printStackTrace();				
			}
		}
	}

	public LsfIndex(File lsfFile) throws Exception {
		indexes.clear();
		SingleLsfIndex index = new SingleLsfIndex(lsfFile);
		addIndex(index);	
	}	
	
	public void addIndex(SingleLsfIndex index) {
		for (int i = 0; i < indexes.size(); i++) {
			if (indexes.get(i).getStartTime() > index.getStartTime()) {
				indexes.add(i, index);
				return;
			}
		}
		indexes.add(index);
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
		if (!indexes.isEmpty())
			return lsfIndexOf(ind).getMessageBytes(ind - messagesToSkip(ind));
		return null;
	}

	/**
	 * Retrieve the type of message at given index
	 * 
	 * @param messageNumber
	 *            The index of the message
	 * @return The IMC type (id) for the message at given index
	 */
	public int typeOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).typeOf(messageNumber - messagesToSkip(messageNumber));
		return -1;
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
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).timeOf(messageNumber - messagesToSkip(messageNumber));
		return Double.NaN;
	}

	public synchronized boolean isBigEndian(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).isBigEndian(messageNumber - messagesToSkip(messageNumber));
		return false;
	}

	public String sourceNameOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).sourceNameOf(messageNumber - messagesToSkip(messageNumber));
		return null;
	}

	public String entityNameOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).entityNameOf(messageNumber - messagesToSkip(messageNumber));
		return null;
	}

	public int sizeOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).sizeOf(messageNumber - messagesToSkip(messageNumber));
		return 0;
	}

	public synchronized int sourceOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).sourceOf(messageNumber - messagesToSkip(messageNumber));
		return -1;
	}

	public synchronized int entityOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).entityOf(messageNumber - messagesToSkip(messageNumber));
		return -1;
	}

	public synchronized int fieldIdOf(int messageNumber) {
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).fieldIdOf(messageNumber - messagesToSkip(messageNumber));
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
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).positionOf(messageNumber - messagesToSkip(messageNumber));
		return -1;
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
		
		if (!indexes.isEmpty())
			return lsfIndexOf(messageNumber).getMessage(messageNumber - messagesToSkip(messageNumber));
		return null;
	}

	
	
	/**
	 * @return Total number of messages in the log
	 */
	public int getNumberOfMessages() {
		int count = 0;
		for (SingleLsfIndex i : indexes)
			count += i.getNumberOfMessages();
		return count;
		
	}
	
	public int getFirstMessageOfType(String abbrev) {
		int first = -1;
		int count = 0;
		for (SingleLsfIndex i : indexes) {
			first = i.getFirstMessageOfType(abbrev);
			if (first != -1)
				return count + first;
			count += i.getNumberOfMessages();
		}
		return -1;
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
		for (int i = 0; i < getNumberOfMessages(); i++) {
			if (typeOf(i) == type)
				return i;
		}
		return -1;
	}

	public int getLastMessageOfType(String abbrev) {
		int type = getDefinitions().getMessageId(abbrev);
		return getLastMessageOfType(type);
	}

	public int getLastMessageOfType(int type) {
		int numMessages = getNumberOfMessages();
		for (int i = numMessages - 1; i >= 0; i--) {
			if (typeOf(i) == type)
				return i;
		}
		return -1;
	}

	public int getNextMessageOfType(int type, int startIndex) {
		if (startIndex == -1)
			return -1;

		int numMessages = getNumberOfMessages();
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
		int numMessages = getNumberOfMessages();
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

	public int getPreviousMessageOfType(int type, int startIndex) {
		if (startIndex == -1)
			return getLastMessageOfType(type);

		for (int i = startIndex - 1; i >= 0; i--) {
			if (typeOf(i) == type)
				return i;
		}
		return -1;
	}

	public int getNextMessageOfType(String abbrev, int startIndex) {
		int type = getDefinitions().getMessageId(abbrev);
		return getNextMessageOfType(type, startIndex);
	}

	public Iterable<IMCMessage> messagesOfType(int type) {
		return getIterator(getDefinitions().getMessageName(type));
	}

	public Iterable<IMCMessage> messagesOfType(String abbrev) {
		return messagesOfType(getDefinitions().getMessageId(abbrev));
	}

	/**
	 * Retrieve the IMC definitions used by this LsfIndex object
	 * 
	 * @return the IMC definitions used by this LsfIndex object
	 */
	public IMCDefinition getDefinitions() {
		if (indexes.isEmpty())
			return IMCDefinition.getInstance();
		else
			return indexes.firstElement().getDefinitions();
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

		int type = getDefinitions().getMessageId("EntityInfo");
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
			
			if (getDefinitions() != IMCDefinition.getInstance())
				getDefinitions().getResolver().setEntityName(src, einfo.getInteger("id"),
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

		int type = getDefinitions().getMessageId("Announce");
		for (int i = getFirstMessageOfType(type); i != -1; i = getNextMessageOfType(
				type, i + 1)) {
			IMCMessage m = getMessage(i);
			String sys_name = m.getString("sys_name");
			int src = m.getInteger("src");

			if (!sysNames.containsKey(src))
				sysNames.put(src, sys_name);

			getDefinitions().getResolver().addEntry(src, sys_name);

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
		for (SingleLsfIndex i : indexes)
			i.cleanup();
		indexes.clear();
	}

	public Collection<Announce> getAvailableSystems() {
		LinkedHashMap<String, Announce> announces = new LinkedHashMap<String, Announce>();
		for (SingleLsfIndex i : indexes) {
			for (Announce a : i.getAvailableSystems())
				announces.put(a.getSysName(), a);
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

	public double getStartTime() {
		return timeOf(0);
	}

	public double getEndTime() {
		int idx = getNumberOfMessages() - 1;
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
		if (timestamp < getStartTime() || timestamp > getEndTime())
			return -1;

		int msgType = getDefinitions().getMessageId(type);
		int finderPos = (int) (((timestamp - getStartTime()) / (getEndTime() - getStartTime())) * getNumberOfMessages());

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

	/**
	 * @return the lsfFile
	 */
	public File getLsfFile() {
		
		if (indexes.isEmpty())
			return null;
		return indexes.firstElement().getLsfFile();
	}

	public static void main(String[] args) throws Exception {
		LsfIndex i = new LsfIndex(new File("/home/zp/Desktop/logs").listFiles());
		System.out.println(i.getNumberOfMessages());
		System.out.println(i.getMessage(i.getNumberOfMessages()-1));
	}
}
