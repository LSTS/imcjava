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
 *  IMC Message Emergency Control State (555)<br/>
 */

public class EmergencyControlState extends IMCMessage {

	public enum STATE {
		NOT_CONFIGURED(0),
		DISABLED(1),
		ENABLED(2),
		ARMED(3),
		ACTIVE(4),
		STOPPING(5);

		protected long value;

		public long value() {
			return value;
		}

		STATE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 555;

	public EmergencyControlState() {
		super(ID_STATIC);
	}

	public EmergencyControlState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EmergencyControlState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EmergencyControlState create(Object... values) {
		EmergencyControlState m = new EmergencyControlState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EmergencyControlState clone(IMCMessage msg) throws Exception {

		EmergencyControlState m = new EmergencyControlState();
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

	public EmergencyControlState(STATE state, String plan_id, short comm_level) {
		super(ID_STATIC);
		setState(state);
		if (plan_id != null)
			setPlanId(plan_id);
		setCommLevel(comm_level);
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
	public EmergencyControlState setState(STATE state) {
		values.put("state", state.value());
		return this;
	}

	/**
	 *  @param state State (as a String)
	 */
	public EmergencyControlState setStateStr(String state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @param state State (integer value)
	 */
	public EmergencyControlState setStateVal(short state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @return Plan Id - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan Id
	 */
	public EmergencyControlState setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Communications Level (%) - uint8_t
	 */
	public short getCommLevel() {
		return (short) getInteger("comm_level");
	}

	/**
	 *  @param comm_level Communications Level (%)
	 */
	public EmergencyControlState setCommLevel(short comm_level) {
		values.put("comm_level", comm_level);
		return this;
	}

}
