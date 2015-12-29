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
 * $Id:: ImcData.java 333 2013-01-02 11:11:44Z zepinto                         $:
 */
package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.gz.MultiMemberGZIPInputStream;

public class ImcData {

	protected Vector<IMCMessage> allMessages = new Vector<IMCMessage>();
	protected LinkedHashMap<Integer, Vector<IMCMessage>> messagesBySource = new LinkedHashMap<Integer, Vector<IMCMessage>>();
	protected LinkedHashMap<Integer, Vector<IMCMessage>> messagesByType = new LinkedHashMap<Integer, Vector<IMCMessage>>();
	protected boolean sorted = true;
	protected IMCDefinition defs;
	
	protected LinkedHashMap<Integer, String> entitiesById = new LinkedHashMap<Integer, String>();
	protected LinkedHashMap<String, Integer> entitiesByName = new LinkedHashMap<String, Integer>();
	
	public void load(File folder) throws Exception {
		if (!folder.isDirectory())
			if (folder.getName().contains(".lsf")) {
				load(folder.getParentFile());			
				return;
			}
			else
				throw new IOException("Given folder does not exist or is not a directory");
		
		//long prevSize = allMessages.size();
		
		if (new File(folder, "IMC.xml").canRead())
			defs = new IMCDefinition(new FileInputStream(new File(folder, "IMC.xml")));
		else
			defs = IMCDefinition.getInstance();
		
		InputStream is = null;
		if (new File(folder, "Data.lsf.gz").canRead() && !new File(folder, "Data.lsf").canRead()) {
			
			is = new MultiMemberGZIPInputStream(new FileInputStream(new File(folder, "Data.lsf.gz")));
			FileOutputStream fos = new FileOutputStream(new File(folder, "Data.lsf"));
			byte[] data = new byte[1024];
			
			while(is.available() > 0) {
				int read = is.read(data);
				if (read > 0)
					fos.write(data, 0, read);
			}
			fos.close();
				
		}
		MappedByteBuffer buff = null;
		if (new File(folder, "Data.lsf").canRead()) {
			FileInputStream fis = new FileInputStream(new File(folder, "Data.lsf"));
			FileChannel channel = fis.getChannel();
			buff = channel.map(MapMode.READ_ONLY, 0, new File(folder, "Data.lsf").length());
			fis.close();
		}
		else
			throw new Exception("Couldn't find Data.lsf file");
		
		double lastTime = 0;
		while (true) {
			try {
				IMCMessage m = defs.nextMessage(buff); 
				if (m.getTimestamp() < lastTime)
					m.setTimestamp(lastTime);
				lastTime = m.getTimestamp();
				addMessage(m);	
			} catch (Exception e) {
			    e.printStackTrace();
				break;
			}
			
		}
		IMCMessage m = getLast("EntityList");
		if (m != null) {
			LinkedHashMap<String, String> list = m.getTupleList("list");
			
			for (String key : list.keySet()) {
				int id = Integer.parseInt(list.get(key));
				entitiesById.put(id, key);
				entitiesByName.put(key, id);
			}
		}
		else {
			System.out.println("EntityList now found");
		}
	}
	
	private	IMCMessage dummy;
	
	public final IMCMessage getMessageBeforeOrAt(int type, double time) {
		if (dummy == null)
			dummy = defs.createHeader();
		
		dummy.setTimestamp(time);
		Vector<IMCMessage> msgs = messagesByType.get(type);
		int index = Collections.binarySearch(msgs, dummy);
		if (index <= 0)
			return msgs.firstElement();
		if (index >= msgs.size())
			return msgs.lastElement();
		
		if (msgs.get(index).getTimestamp() <= time)
			return msgs.get(index);
		else
			return msgs.get(index-1);
	}
	
	public final IMCMessage getMessageAfterOrAt(int type, double time) {
		if (dummy == null)
			dummy = defs.createHeader();
		
		dummy.setTimestamp(time);
		Vector<IMCMessage> msgs = messagesByType.get(type);
		int index = Collections.binarySearch(msgs, dummy);
		if (index <= 0)
			return msgs.firstElement();
		if (index >= msgs.size())
			return msgs.lastElement();
		
		if (msgs.get(index).getTimestamp() >= time)
			return msgs.get(index);
		else
			return msgs.get(index+1);
	}
	
	
	public Vector<IMCMessage> getAllMessagesOfType(int type, double timeStepSecs) {
		
		Vector<IMCMessage> msgs = new Vector<IMCMessage>();		
		
		
		double firstTime = Math.ceil(messagesByType.get(type).firstElement().getTimestamp());
		double lastTime = Math.floor(messagesByType.get(type).lastElement().getTimestamp());
		if (timeStepSecs > 0) {
			for (double time = firstTime; time <= lastTime; time += timeStepSecs) {
				IMCMessage m = getMessageBeforeOrAt(type, time);
				m.setTimestamp(time);
				msgs.add(m);
			}
		}
		
		return msgs;
	}
	
	public Vector<IMCMessage> getAllMessagesOfType(String abbrev, double timeStepSecs) {
		return getAllMessagesOfType(defs.getMessageId(abbrev), timeStepSecs);
	}
	
	public Vector<IMCMessage> getMessagesFrom(int source) {
		return messagesBySource.get(source);
	}
	
	public Collection<Integer> getSources() {
		return messagesBySource.keySet();
	}
	
	public Collection<Integer> getMessageTypes() {
		return messagesByType.keySet();
	}
	
	public void sortAllMessages() {
		Collections.sort(allMessages);
		
		for (Integer key : messagesBySource.keySet())
			Collections.sort(messagesBySource.get(key));
		
		for (Integer key : messagesByType.keySet())
			Collections.sort(messagesByType.get(key));
	}
	
	public IMCMessage getLastofType(int type) {
		if (messagesByType.containsKey(type))
			return messagesByType.get(type).lastElement();		
		return null;
	}
	
	public IMCMessage getLastofType(int type, int sourceEntity) {
		if (messagesByType.containsKey(type)) {
			Vector<IMCMessage> msgs = messagesByType.get(type);
			
			for (int i = msgs.size()-1; i >= 0; i--) {
				if (msgs.get(i).getHeader().getInteger("src_ent") == sourceEntity)
					return msgs.get(i);
			}			
		}
			
		return null;
	}
	
	
	
	public IMCMessage getLast(String messageAbbrev, String entityId) throws Exception {
		int type = defs.getMessageId(messageAbbrev);
		LinkedHashMap<String, String> entityList = getLast("EntityList").getTupleList("list");
		
		return getLastofType(type, Integer.parseInt(entityList.get(entityId)));
	}
	
	public IMCMessage getLast(String messageAbbrev) {
		int type = defs.getMessageId(messageAbbrev);
		return getLastofType(type);
	}
	
	public IMCMessage getFirst(String messageAbbrev) {
		int type = defs.getMessageId(messageAbbrev);
		return getFirstofType(type);
	}
	
	public IMCMessage getFirstofType(int type) {
		if (messagesByType.containsKey(type))
			return messagesByType.get(type).firstElement();		
			
		return null;
	}
	
	public void addMessageInOrder(IMCMessage m) {
		if (!sorted) {
			System.err.println("Cannot add message in order to unsorted data structure");
			addMessage(m);
		}
		
		int index;
		int src = m.getHeader().getInteger("src");
		int type = m.getHeader().getInteger("mgid");

		if (!messagesBySource.containsKey(src))
			messagesBySource.put(src, new Vector<IMCMessage>());
		
		if (!messagesByType.containsKey(type))
			messagesByType.put(type, new Vector<IMCMessage>());
		
		index = Collections.binarySearch(allMessages, m);
		allMessages.add(index, m);		
		
		index = Collections.binarySearch(messagesBySource.get(src), m);
		messagesBySource.get(src).add(index, m);

		index = Collections.binarySearch(messagesByType.get(type), m);
		messagesByType.get(type).add(index, m);
	}
	
	public void addMessage(IMCMessage m) {
		
		int src = m.getHeader().getInteger("src");
		int type = m.getHeader().getInteger("mgid");
		
		if (!messagesBySource.containsKey(src))
			messagesBySource.put(src, new Vector<IMCMessage>());
		
		if (!messagesByType.containsKey(type))
			messagesByType.put(type, new Vector<IMCMessage>());
		
		allMessages.add(m);
		messagesBySource.get(src).add(m);
		messagesByType.get(type).add(m);
		
		sorted = false;
	}
	
	public void asLsf(File output) throws IOException {
		IMCOutputStream ios = new IMCOutputStream(new FileOutputStream(output));
		
		for (IMCMessage m : allMessages) {
			m.serialize(defs, ios);
		}
				
		ios.close();
	}
	
	public int getEntityId(String systemName) {
		return entitiesByName.get(systemName);
	}
	
	public String getEntityName(int systemId) {
		return entitiesById.get(systemId);
	}
	
	public double getAvg(String message, String field, String entity, double startTime, double endTime) {
		int type = defs.getMessageId(message);
		int src_ent = getEntityId(entity);
		Vector<IMCMessage> msgs = messagesByType.get(type);		
		
		if (dummy == null)
			dummy = defs.createHeader();
		
		dummy.setTimestamp(startTime);		
		int index = Collections.binarySearch(msgs, dummy);
		index = Math.abs(index);
		double sum = 0;
		int count = 0;
		for (int i = index; i < msgs.size(); i++) {
			IMCMessage msg = msgs.get(i);			
			if (msg.getTimestamp() > endTime)
				break;
				
			
			if (msg.getHeader().getInteger("src_ent") == src_ent) {
				count ++;
				sum += msgs.get(i).getDouble(field);
			}
		}
		if (count == 0)
			return -1;
		else
			return sum/count;		
	}
	
	public static void main(String[] args) throws Exception {
		ImcData data = new ImcData();
		System.out.println('2');
		data.load(new File("/home/zp/Desktop/143900_btrack_5m_2nd"));
		System.out.println('3');
		data.asLsf(new File("/home/zp/Desktop/Data.lsf"));
		System.out.println('4');
	}
}
