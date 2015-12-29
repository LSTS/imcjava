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
 *  IMC Message Entity Activation State (14)<br/>
 *  State of entity activation/deactivation.<br/>
 */

public class EntityActivationState extends IMCMessage {

	public enum STATE {
		INACTIVE(0),
		ACTIVE(1),
		ACT_IP(2),
		ACT_DONE(3),
		ACT_FAIL(4),
		DEACT_IP(5),
		DEACT_DONE(6),
		DEACT_FAIL(7);

		protected long value;

		public long value() {
			return value;
		}

		STATE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 14;

	public EntityActivationState() {
		super(ID_STATIC);
	}

	public EntityActivationState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntityActivationState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EntityActivationState create(Object... values) {
		EntityActivationState m = new EntityActivationState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EntityActivationState clone(IMCMessage msg) throws Exception {

		EntityActivationState m = new EntityActivationState();
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

	public EntityActivationState(STATE state, String error) {
		super(ID_STATIC);
		setState(state);
		if (error != null)
			setError(error);
	}

	/**
	 *  @return State (enumerated) - uint8_t
	 */
	public STATE getState() {
		try {
			STATE o = STATE.valueOf(getMessageType().getFieldPossibleValues("state").get(getLong("state")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStateStr() {
		return getString("state");
	}

	public short getStateVal() {
		return (short) getInteger("state");
	}

	/**
	 *  @param state State (enumerated)
	 */
	public EntityActivationState setState(STATE state) {
		values.put("state", state.value());
		return this;
	}

	/**
	 *  @param state State (as a String)
	 */
	public EntityActivationState setStateStr(String state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @param state State (integer value)
	 */
	public EntityActivationState setStateVal(short state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @return Error - plaintext
	 */
	public String getError() {
		return getString("error");
	}

	/**
	 *  @param error Error
	 */
	public EntityActivationState setError(String error) {
		values.put("error", error);
		return this;
	}

}
