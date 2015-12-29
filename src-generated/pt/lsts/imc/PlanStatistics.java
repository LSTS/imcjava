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
 *  IMC Message Plan Statistics (564)<br/>
 */

public class PlanStatistics extends IMCMessage {

	public static final short PRP_BASIC = 0x00;
	public static final short PRP_NONLINEAR = 0x01;
	public static final short PRP_INFINITE = 0x02;
	public static final short PRP_CYCLICAL = 0x04;
	public static final short PRP_ALL = 0x07;

	public enum TYPE {
		PREPLAN(0),
		INPLAN(1),
		POSTPLAN(2);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 564;

	public PlanStatistics() {
		super(ID_STATIC);
	}

	public PlanStatistics(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanStatistics(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanStatistics create(Object... values) {
		PlanStatistics m = new PlanStatistics();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanStatistics clone(IMCMessage msg) throws Exception {

		PlanStatistics m = new PlanStatistics();
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

	public PlanStatistics(String plan_id, TYPE type, short properties, String durations, String distances, String actions, String fuel) {
		super(ID_STATIC);
		if (plan_id != null)
			setPlanId(plan_id);
		setType(type);
		setProperties(properties);
		if (durations != null)
			setDurations(durations);
		if (distances != null)
			setDistances(distances);
		if (actions != null)
			setActions(actions);
		if (fuel != null)
			setFuel(fuel);
	}

	/**
	 *  @return Plan Identifier - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan Identifier
	 */
	public PlanStatistics setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
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
	public PlanStatistics setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public PlanStatistics setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public PlanStatistics setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Properties (bitfield) - uint8_t
	 */
	public short getProperties() {
		return (short) getInteger("properties");
	}

	/**
	 *  @param properties Properties (bitfield)
	 */
	public PlanStatistics setProperties(short properties) {
		values.put("properties", properties);
		return this;
	}

	/**
	 *  @return Durations (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getDurations() {
		return getTupleList("durations");
	}

	/**
	 *  @param durations Durations (tuplelist)
	 */
	public PlanStatistics setDurations(java.util.LinkedHashMap<String, ?> durations) {
		String val = encodeTupleList(durations);
		values.put("durations", val);
		return this;
	}

	public PlanStatistics setDurations(String durations) {
		values.put("durations", durations);
		return this;
	}

	/**
	 *  @return Distances (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getDistances() {
		return getTupleList("distances");
	}

	/**
	 *  @param distances Distances (tuplelist)
	 */
	public PlanStatistics setDistances(java.util.LinkedHashMap<String, ?> distances) {
		String val = encodeTupleList(distances);
		values.put("distances", val);
		return this;
	}

	public PlanStatistics setDistances(String distances) {
		values.put("distances", distances);
		return this;
	}

	/**
	 *  @return Actions (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getActions() {
		return getTupleList("actions");
	}

	/**
	 *  @param actions Actions (tuplelist)
	 */
	public PlanStatistics setActions(java.util.LinkedHashMap<String, ?> actions) {
		String val = encodeTupleList(actions);
		values.put("actions", val);
		return this;
	}

	public PlanStatistics setActions(String actions) {
		values.put("actions", actions);
		return this;
	}

	/**
	 *  @return Fuel (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getFuel() {
		return getTupleList("fuel");
	}

	/**
	 *  @param fuel Fuel (tuplelist)
	 */
	public PlanStatistics setFuel(java.util.LinkedHashMap<String, ?> fuel) {
		String val = encodeTupleList(fuel);
		values.put("fuel", val);
		return this;
	}

	public PlanStatistics setFuel(String fuel) {
		values.put("fuel", fuel);
		return this;
	}

}
