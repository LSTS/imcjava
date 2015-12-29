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
 */
package pt.lsts.imc;

/**
 *  IMC Message CCU Event (606)<br/>
 *  This message is used to signal events among running CCUs.<br/>
 */

public class CcuEvent extends IMCMessage {

	public enum TYPE {
		LOG_ENTRY(1),
		PLAN_ADDED(2),
		PLAN_REMOVED(3),
		PLAN_CHANGED(4),
		MAP_FEATURE_ADDED(5),
		MAP_FEATURE_REMOVED(6),
		MAP_FEATURE_CHANGED(7),
		TELEOPERATION_STARTED(8),
		TELEOPERATION_ENDED(9);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 606;

	public CcuEvent() {
		super(ID_STATIC);
	}

	public CcuEvent(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CcuEvent(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CcuEvent create(Object... values) {
		CcuEvent m = new CcuEvent();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CcuEvent clone(IMCMessage msg) throws Exception {

		CcuEvent m = new CcuEvent();
		if (msg == null)
			return m;
		if(msg.definitions != m.definitions){
			msg = msg.cloneMessage();
			IMCUtil.updateMessage(msg, m.definitions);
		}
		else if (msg.getMgid()!=m.getMgid())
			throw new Exception("Argument "+msg.getAbbrev()+" is incompatible with message "+m.getAbbrev());

		m.getHeader().values.putAll(msg.getHeader().values);
		m.values.putAll(msg.values);
		return m;
	}

	public CcuEvent(TYPE type, String id, IMCMessage arg) {
		super(ID_STATIC);
		setType(type);
		if (id != null)
			setId(id);
		if (arg != null)
			setArg(arg);
	}

	/**
	 *  @return Event Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Event Type (enumerated)
	 */
	public CcuEvent setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Event Type (as a String)
	 */
	public CcuEvent setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Event Type (integer value)
	 */
	public CcuEvent setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Identifier - plaintext
	 */
	public String getId() {
		return getString("id");
	}

	/**
	 *  @param id Identifier
	 */
	public CcuEvent setId(String id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Additional Data - message
	 */
	public IMCMessage getArg() {
		return getMessage("arg");
	}

	public <T extends IMCMessage> T getArg(Class<T> clazz) throws Exception {
		return getMessage(clazz, "arg");
	}

	/**
	 *  @param arg Additional Data
	 */
	public CcuEvent setArg(IMCMessage arg) {
		values.put("arg", arg);
		return this;
	}

}
