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
 * $Id:: ImcSystemState.java 334 2013-01-02 11:20:47Z zepinto                  $:
 */
package pt.lsts.imc.state;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

import pt.lsts.imc.EntityInfo;
import pt.lsts.imc.EntityList;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndex;

/**
 * This class aggregates the last messages for a given IMCSystem and allows
 * querying of data from that state
 * 
 * @author zp
 */
public class ImcSystemState {

	protected LinkedHashMap<String, IMCMessage> lastMessages = new LinkedHashMap<String, IMCMessage>();
	// protected HashBiMap<Integer, String> entities = HashBiMap.create();
	LinkedHashMap<Integer, String> entities = new LinkedHashMap<Integer, String>();
	LinkedHashMap<String, Integer> entitiesInverted = new LinkedHashMap<String, Integer>();
	protected IMCDefinition definitions = null;
	protected HashSet<String> receivedMessages = new HashSet<String>();
	protected boolean gotData = false;
	protected long lastReceivedTimestamp = 0;
	protected boolean ignoreEntities = false;

	/**
	 * This method waits (blocks) and retrieves the next message of given type
	 * 
	 * @param messageType
	 *            The abbreviated name of the message to be retrieved
	 * @param timeoutMillis
	 *            The maximum number of milliseconds to block
	 * @return The received IMCMessage or <strong>null</strong> if no message
	 *         has been received for <strong>timeoutMillis</strong> milliseconds
	 */
	public IMCMessage poll(String messageType, long timeoutMillis) {
		long timeout = System.currentTimeMillis() + timeoutMillis;

		long lastTime = System.currentTimeMillis();
		IMCMessage msg = get(messageType);
		if (msg != null)
			lastTime = msg.getTimestampMillis();

		while (System.currentTimeMillis() < timeout) {
			IMCMessage m = get(messageType);

			if (m != null && m.getTimestampMillis() != lastTime)
				return m;
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * This method waits (blocks) and retrieves the next message of given type
	 * 
	 * @param messageType
	 *            The abbreviated name of the message to be retrieved
	 * @param timeoutMillis
	 *            The maximum number of milliseconds to block
	 * @return The received IMCMessage or <strong>null</strong> if no message
	 *         has been received for <strong>timeoutMillis</strong> milliseconds
	 */
	public IMCMessage poll(String messageType, String entityName,
			long timeoutMillis) {
		long timeout = System.currentTimeMillis() + timeoutMillis;

		int msgId = definitions.getMessageId(messageType);

		if (msgId == -1)
			return null;

		long lastTime = System.currentTimeMillis();
		IMCMessage msg = get(messageType);
		if (msg != null)
			lastTime = msg.getTimestampMillis();

		while (System.currentTimeMillis() < timeout) {
			IMCMessage m = get(messageType);

			if (m != null && m.getTimestampMillis() != lastTime
					&& m.getSrcEnt() == entitiesInverted.get(entityName))
				return m;
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Retrieve the last received message of a certain type
	 * 
	 * @param msgType
	 *            The class to be received
	 * @return The last received message of given type or
	 *         <code>null</null> if no such message exists.
	 */
	public <T extends IMCMessage> T last(Class<T> msgType) {
		IMCMessage m = get(msgType.getSimpleName());
		if (m != null)
			try {
				T msg = IMCDefinition.getInstance().create(msgType);
				msg.copyFrom(m);
				return msg;
			} catch (Exception e) {
			}
		return null;
	}

	/**
	 * Retrieve the last received message of a certain type and specific entity
	 * 
	 * @param msgType
	 *            The class to be received
	 * @param entity
	 *            The entity that should have produced the message
	 * @return The last received message of given type or
	 *         <code>null</null> if no such message exists.
	 */
	public <T extends IMCMessage> T last(Class<T> msgType, String entity) {
		IMCMessage m = get(definitions.getMessageId(msgType.getSimpleName()),
				entitiesInverted.get(entity));

		if (m != null)
			try {
				T msg = IMCDefinition.getInstance().create(msgType);
				msg.copyFrom(m);
				return msg;
			} catch (Exception e) {
			}
		return null;
	}

	/**
	 * Wait for the next message of a certain type
	 * 
	 * @param msgType
	 *            The type to be expected
	 * @param timeoutMillis
	 *            The maximum amount of time to wait for the message
	 * @return The newly received message or
	 *         <code>null</null> if no such message was received.
	 */
	public <T extends IMCMessage> T poll(Class<T> msgType, long timeoutMillis) {
		IMCMessage m = poll(msgType.getSimpleName(), timeoutMillis);
		if (m != null)
			try {
				T msg = IMCDefinition.getInstance().create(msgType);
				msg.copyFrom(m);
				return msg;
			} catch (Exception e) {
			}
		return null;
	}

	/**
	 * Wait for the next message of a certain type, coming from a specific
	 * entity
	 * 
	 * @param msgType
	 *            The type to be expected
	 * @param entityName
	 *            The name of the entity that should be producing the message
	 * @param timeoutMillis
	 *            The maximum amount of time to wait for the message
	 * @return The newly received message or
	 *         <code>null</null> if no such message was received.
	 */
	public <T extends IMCMessage> T poll(Class<T> msgType, String entityName,
			long timeoutMillis) {
		IMCMessage m = poll(msgType.getSimpleName(), entityName, timeoutMillis);
		if (m != null)
			try {
				T msg = IMCDefinition.getInstance().create(msgType);
				msg.copyFrom(m);
				return msg;
			} catch (Exception e) {
			}
		return null;
	}

	/**
	 * Verify if a system was already visible in the network
	 * 
	 * @return <strong>false</strong> if this state is empty (no messages were
	 *         received)
	 */
	public boolean isActive() {
		return gotData && millisSinceLastMessage() < 30000;
	}

	/**
	 * Class constructor
	 * 
	 * @param definitions
	 *            The IMC definitions to be used
	 */
	public ImcSystemState(IMCDefinition definitions) {
		this.definitions = definitions;
	}

	/**
	 * This method resets the current entities for this ImcState.<br/>
	 * It clears the current entities map and then calls
	 * {@link #putEntityList(EntityList)}
	 * 
	 * @param entities
	 *            An {@link EntityList} IMC message
	 */
	void setEntityList(IMCMessage msg) {
		entities.clear();
		putEntityList(msg);
	}

	/**
	 * This method adds all entities to the existing map of entity names
	 * 
	 * @param entities
	 *            An {@link EntityList} IMC message
	 * @see #setEntityList(EntityList)
	 */
	void putEntityList(IMCMessage msg) {
		if (!msg.getString("op").equals("REPORT") || ignoreEntities)
			return;

		LinkedHashMap<String, String> map = msg.getTupleList("list");

		for (String key : map.keySet()) {
			try {
				int id = Integer.parseInt(map.get(key));
				entities.put(id, key);
				entitiesInverted.put(key, id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		entities.put(IMCMessage.DEFAULT_ENTITY_ID, "*");
		entitiesInverted.put("*", IMCMessage.DEFAULT_ENTITY_ID);
	}

	/**
	 * Update this state by appending this message
	 * 
	 * @param msg
	 *            The message to be integrated into this IMC state
	 */
	public void setMessage(IMCMessage msg) {
		if (msg.getMgid() == EntityList.ID_STATIC && !ignoreEntities) {
			setEntityList(msg);
		} else if (msg.getMgid() == EntityInfo.ID_STATIC && ignoreEntities) {
			entities.put(msg.getInteger("id"), msg.getString("label"));
			entitiesInverted.put(msg.getString("label"), msg.getInteger("id"));
		}

		if (entities.containsKey((int) msg.getSrcEnt())) {
			lastMessages
					.put(msg.getAbbrev() + "."
							+ entities.get((int) msg.getSrcEnt()), msg);
		}
		lastMessages.put(msg.getAbbrev() + "." + msg.getSrcEnt(), msg);

		receivedMessages.add(msg.getAbbrev());
		lastMessages.put(msg.getAbbrev(), msg);

		lastReceivedTimestamp = System.currentTimeMillis();
		gotData = true;
	}

	/**
	 * Clears this state and becomes inactive
	 */
	public void clear() {
		gotData = false;
		receivedMessages.clear();
		lastMessages.clear();
		entities.clear();
		entitiesInverted.clear();
	}

	/**
	 * Retrieve all messages of the given type in this state (divided by entity
	 * that created them)
	 * 
	 * @param messageAbbrev
	 *            The name of the message to be retrieved
	 * @return A map from Strings to messages as follows:<br/>
	 *         <blockquote> "MessageAbbrev" -> IMCMessage<br/>
	 *         "MessageAbbrev.Entity1" -> IMCMessage<br/>
	 *         "MessageAbbrev.Entity2" -> IMCMessage<br/>
	 *         ... </blockquote> Thus, the last received message (from all
	 *         entities) will be repeated in the "MessageAbbrev" entry of the
	 *         HashMap
	 */
	public final LinkedHashMap<String, IMCMessage> lastMessagesOfType(
			String messageAbbrev) {
		LinkedHashMap<String, IMCMessage> ret = new LinkedHashMap<String, IMCMessage>();

		for (String key : lastMessages.keySet()) {
			if (key.startsWith(messageAbbrev))
				ret.put(key, lastMessages.get(key));
		}
		return ret;
	}

	public Object expr(String expression) {
		String parts[] = expression.split("\\.");

		if (parts.length == 1) {
			return get(expression);
		} else if (parts.length == 2) {
			return get(parts[0] + ".*." + parts[1], Object.class);
		} else {
			return get(expression, Object.class);
		}
	}

	/**
	 * Retrieve the last received message of given type (independently of the
	 * originating entity)
	 * 
	 * @param messageAbbrev
	 *            The name of the message to be retrieved
	 * @return the last received message of given type (independently of the
	 *         originating entity)
	 */
	public final IMCMessage get(String messageAbbrev) {
		return lastMessages.get(messageAbbrev);
	}

	/**
	 * Retrieve the last message of a given type
	 * 
	 * @param msgId
	 *            The imc id of the message type
	 * @return the last received message of given type (independently of the
	 *         originating entity)
	 */
	public final IMCMessage get(int msgId) {
		return lastMessages.get(definitions.getMessageName(msgId));
	}

	/**
	 * Retrieve the last message of given type, generated in given entity
	 * 
	 * @param msgId
	 *            The imc id of the message type
	 * @param entityId
	 *            The numeric id of the entity
	 * @return the last received message of given type (independently of the
	 *         originating entity)
	 */
	public final IMCMessage get(int msgId, int entityId) {
		if (entities.containsKey(entityId)) {
			return lastMessages.get(definitions.getMessageName(msgId) + "."
					+ entities.get(entityId));
		}
		return null;
	}

	/**
	 * Retrieve the name of the entity with given id (numeric)
	 * 
	 * @param entityId
	 *            The numeric id of the entity
	 * @return The name of the entity or the numeric value converted to String
	 *         if no entity with that id is recognized
	 */
	public String getEntityName(int entityId) {
		if (entities.containsKey(entityId))
			return entities.get(entityId);
		return "" + entityId;
	}

	/**
	 * Retrieve the value of a given field
	 * 
	 * @param msgId
	 *            The imc id of the message type
	 * @param entityId
	 *            The numeric id of the entity
	 * @param field
	 *            The name of the field to be retrieved
	 * @return The value of the requested field (null if not found)
	 */
	public final Object get(int msgId, int entityId, String field) {
		IMCMessage msg = get(msgId, entityId);
		if (msg == null)
			return null;

		return msg.getValue(field);
	}

	/**
	 * Retrieve the value of a given field
	 * 
	 * @param msgId
	 *            The imc id of the message type
	 * @param field
	 *            The name of the field to be retrieved
	 * @return The value of the field in the last received message of given type
	 *         (independently of originating entity)
	 */
	public final Object get(int msgId, String field) {
		IMCMessage msg = get(msgId);
		if (msg != null)
			return msg.getValue(field);
		return null;
	}

	/**
	 * Retrieve the value of the field in the last received message, casting it
	 * to desired type
	 * 
	 * @param msgId
	 *            The imc id of the message type
	 * @param field
	 *            The name of the field to be retrieved
	 * @param type
	 *            The value of the field in the last received message of given
	 *            type (independently of originating entity)
	 * @return
	 */
	public final <T> T get(int msgId, String field, Class<T> type)
			throws ClassCastException {
		IMCMessage msg = get(msgId);
		if (msg != null)
			return msg.get(field, type);
		return null;
	}

	/**
	 * Retrieve data from this state
	 * 
	 * @param query
	 *            The query to be performed. Valid queries:
	 *            <ul>
	 *            <li>"MessageAbbrev" -> retrieve the last message of type
	 *            "MessageAbbrev"
	 *            <li>"MessageAbbrev.EntityName" -> retrieve the last message of
	 *            type "MessageAbbrev" generated by "EntityName"
	 *            <li>"MessageAbbrev.EntityName.FieldName" -> retrieve the value
	 *            of the field named "FieldName" from the last message of type
	 *            "MessageAbbrev" generated by "EntityName"
	 *            <li>"MessageAbbrev.*.FieldName" -> retrieve the value of the
	 *            field named "FieldName" from the last message of type
	 *            "MessageAbbrev"
	 *            </ul>
	 * @param type
	 *            The desired type of the return
	 * @return The requested data, or <strong>null</strong> if no messages /
	 *         fields match the query.
	 */
	@SuppressWarnings("unchecked")
	public final <T> T get(String query, Class<T> type) {
		// System.out.println(lastMessages);
		String parts[] = query.split("\\.");
		if (parts.length == 1 && type == IMCMessage.class) {
			return (T) get(parts[0]);
		} else if (parts.length == 1 && type == IMCMessage[].class) {
			HashSet<IMCMessage> result = new HashSet<IMCMessage>();

			for (String m : lastMessages.keySet()) {
				if (m.startsWith(query))
					result.add(lastMessages.get(m));
			}
			return (T) result.toArray(new IMCMessage[0]);
		} else if (parts.length == 2 && type == IMCMessage.class) {
			return (T) lastMessages.get(parts[0] + "." + parts[1]);
		} else if (parts.length == 3) {
			String message = parts[0] + "." + parts[1];
			if (parts[1].equals("*"))
				message = parts[0];
			if (lastMessages.containsKey(message)) {
				return lastMessages.get(message).get(parts[2], type);
			}
		}
		return null;
	}

	/**
	 * Same as get(query, Double.class)
	 * 
	 * @see {@linkplain #get(String, Class)}
	 */
	public double getDouble(String query) {
		return get(query, Double.class);
	}

	/**
	 * Same as get(query, String.class)
	 * 
	 * @see {@linkplain #get(String, Class)}
	 */
	public String getString(String query) {
		return get(query, String.class);
	}

	/**
	 * Same as get(query, Integer.class)
	 * 
	 * @see {@linkplain #get(String, Class)}
	 */
	public int getInteger(String query) {
		return get(query, Integer.class);
	}

	/**
	 * Same as get(query, Long.class)
	 * 
	 * @see {@linkplain #get(String, Class)}
	 */
	public long getLong(String query) {
		return get(query, Long.class);
	}

	/**
	 * Same as get(query, String.class)
	 * 
	 * @see {@linkplain #get(String, Class)}
	 */
	public byte[] getRawData(String query) {
		return get(query, byte[].class);
	}

	/**
	 * Same as get(query, IMCMessage.class)
	 * 
	 * @see {@linkplain #get(String, Class)}
	 */
	public IMCMessage getMessage(String query) {
		return get(query, IMCMessage.class);
	}

	/**
	 * Retrieve a list of received message types
	 * 
	 * @return a list of received message types
	 */
	public final Collection<String> availableMessages() {
		return receivedMessages;
	}

	/**
	 * Get the definitions being used to generate this state
	 * 
	 * @return the definitions being used to generate this state
	 */
	public final IMCDefinition getDefinitions() {
		return definitions;
	}

	public boolean isIgnoreEntities() {
		return ignoreEntities;
	}

	public void setIgnoreEntities(boolean ignoreEntities) {
		this.ignoreEntities = ignoreEntities;
	}
	
	public long millisSinceLastMessage() {
		return System.currentTimeMillis() - lastReceivedTimestamp;
	}

	public static void main(String[] args) throws Exception {
		ImcSystemState state = new ImcSystemState(IMCDefinition.getInstance());
		LsfIndex index = new LsfIndex(new File(
				"/home/zp/Desktop/112538_rows_wreck_-3m/Data.lsf"),
				IMCDefinition.getInstance());

		for (int i = 0; i < index.getNumberOfMessages(); i++) {
			state.setMessage(index.getMessage(i));
			if (Math.random() > 0.99) {
				System.out.println(state.last(EstimatedState.class,
						"Navigation").getX());
			}
		}
	}
}
