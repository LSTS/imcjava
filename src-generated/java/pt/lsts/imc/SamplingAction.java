/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Sampling Action (723)<br/>
 *  This message is used to execute a sampling action.<br/>
 */

public class SamplingAction extends IMCMessage {

	public enum ACTION {
		REPORT(0),
		COMMAND(1);

		protected long value;

		public long value() {
			return value;
		}

		ACTION(long value) {
			this.value = value;
		}
	}

	public enum TYPE {
		STATE_IDLE(0),
		STATE_STARTING(1),
		STATE_STOPPING(2),
		STATE_PAUSED(3),
		STATE_RESUMING(4),
		STATE_SAMPLING(5),
		STATE_ERROR(6),
		CMD_QUERY_STATE(7),
		CMD_START(8),
		CMD_STOP(9),
		CMD_PAUSE(10),
		CMD_RESUME(11);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 723;

	public SamplingAction() {
		super(ID_STATIC);
	}

	public SamplingAction(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SamplingAction(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SamplingAction create(Object... values) {
		SamplingAction m = new SamplingAction();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SamplingAction clone(IMCMessage msg) throws Exception {

		SamplingAction m = new SamplingAction();
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

	public SamplingAction(ACTION action, TYPE type, String description) {
		super(ID_STATIC);
		setAction(action);
		setType(type);
		if (description != null)
			setDescription(description);
	}

	/**
	 *  @return Action (enumerated) - uint8_t
	 */
	public ACTION getAction() {
		try {
			ACTION o = ACTION.valueOf(getMessageType().getFieldPossibleValues("action").get(getLong("action")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getActionStr() {
		return getString("action");
	}

	public short getActionVal() {
		return (short) getInteger("action");
	}

	/**
	 *  @param action Action (enumerated)
	 */
	public SamplingAction setAction(ACTION action) {
		values.put("action", action.value());
		return this;
	}

	/**
	 *  @param action Action (as a String)
	 */
	public SamplingAction setActionStr(String action) {
		setValue("action", action);
		return this;
	}

	/**
	 *  @param action Action (integer value)
	 */
	public SamplingAction setActionVal(short action) {
		setValue("action", action);
		return this;
	}

	/**
	 *  @return Type (enumerated) - uint8_t
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
	 *  @param type Type (enumerated)
	 */
	public SamplingAction setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public SamplingAction setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public SamplingAction setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Description - plaintext
	 */
	public String getDescription() {
		return getString("description");
	}

	/**
	 *  @param description Description
	 */
	public SamplingAction setDescription(String description) {
		values.put("description", description);
		return this;
	}

}
