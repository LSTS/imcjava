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
 *  IMC Message Plan Control State (560)<br/>
 *  State of plan control.<br/>
 */

public class PlanControlState extends IMCMessage {

	public enum STATE {
		BLOCKED(0),
		READY(1),
		INITIALIZING(2),
		EXECUTING(3);

		protected long value;

		public long value() {
			return value;
		}

		STATE(long value) {
			this.value = value;
		}
	}

	public enum LAST_OUTCOME {
		NONE(0),
		SUCCESS(1),
		FAILURE(2);

		protected long value;

		public long value() {
			return value;
		}

		LAST_OUTCOME(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 560;

	public PlanControlState() {
		super(ID_STATIC);
	}

	public PlanControlState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanControlState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanControlState create(Object... values) {
		PlanControlState m = new PlanControlState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanControlState clone(IMCMessage msg) throws Exception {

		PlanControlState m = new PlanControlState();
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

	public PlanControlState(STATE state, String plan_id, int plan_eta, float plan_progress, String man_id, int man_type, int man_eta, LAST_OUTCOME last_outcome) {
		super(ID_STATIC);
		setState(state);
		if (plan_id != null)
			setPlanId(plan_id);
		setPlanEta(plan_eta);
		setPlanProgress(plan_progress);
		if (man_id != null)
			setManId(man_id);
		setManType(man_type);
		setManEta(man_eta);
		setLastOutcome(last_outcome);
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
	public PlanControlState setState(STATE state) {
		values.put("state", state.value());
		return this;
	}

	/**
	 *  @param state State (as a String)
	 */
	public PlanControlState setStateStr(String state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @param state State (integer value)
	 */
	public PlanControlState setStateVal(short state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @return Plan -- ID - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan -- ID
	 */
	public PlanControlState setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Plan -- ETA (s) - int32_t
	 */
	public int getPlanEta() {
		return getInteger("plan_eta");
	}

	/**
	 *  @param plan_eta Plan -- ETA (s)
	 */
	public PlanControlState setPlanEta(int plan_eta) {
		values.put("plan_eta", plan_eta);
		return this;
	}

	/**
	 *  @return Plan -- Progress (%) - fp32_t
	 */
	public double getPlanProgress() {
		return getDouble("plan_progress");
	}

	/**
	 *  @param plan_progress Plan -- Progress (%)
	 */
	public PlanControlState setPlanProgress(double plan_progress) {
		values.put("plan_progress", plan_progress);
		return this;
	}

	/**
	 *  @return Maneuver -- ID - plaintext
	 */
	public String getManId() {
		return getString("man_id");
	}

	/**
	 *  @param man_id Maneuver -- ID
	 */
	public PlanControlState setManId(String man_id) {
		values.put("man_id", man_id);
		return this;
	}

	/**
	 *  @return Maneuver -- Type - uint16_t
	 */
	public int getManType() {
		return getInteger("man_type");
	}

	/**
	 *  @param man_type Maneuver -- Type
	 */
	public PlanControlState setManType(int man_type) {
		values.put("man_type", man_type);
		return this;
	}

	/**
	 *  @return Maneuver -- ETA (s) - int32_t
	 */
	public int getManEta() {
		return getInteger("man_eta");
	}

	/**
	 *  @param man_eta Maneuver -- ETA (s)
	 */
	public PlanControlState setManEta(int man_eta) {
		values.put("man_eta", man_eta);
		return this;
	}

	/**
	 *  @return Last Plan Outcome (enumerated) - uint8_t
	 */
	public LAST_OUTCOME getLastOutcome() {
		try {
			LAST_OUTCOME o = LAST_OUTCOME.valueOf(getMessageType().getFieldPossibleValues("last_outcome").get(getLong("last_outcome")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getLastOutcomeStr() {
		return getString("last_outcome");
	}

	public short getLastOutcomeVal() {
		return (short) getInteger("last_outcome");
	}

	/**
	 *  @param last_outcome Last Plan Outcome (enumerated)
	 */
	public PlanControlState setLastOutcome(LAST_OUTCOME last_outcome) {
		values.put("last_outcome", last_outcome.value());
		return this;
	}

	/**
	 *  @param last_outcome Last Plan Outcome (as a String)
	 */
	public PlanControlState setLastOutcomeStr(String last_outcome) {
		setValue("last_outcome", last_outcome);
		return this;
	}

	/**
	 *  @param last_outcome Last Plan Outcome (integer value)
	 */
	public PlanControlState setLastOutcomeVal(short last_outcome) {
		setValue("last_outcome", last_outcome);
		return this;
	}

}
