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
 *  IMC Message Formation (484)<br/>
 *  The "Formation" is a controller to execute a maneuver with a team of vehicles.<br/>
 *  It defines the:<br/>
 *  - Vehicles included in the formation group<br/>
 *  - Vehicles relative positions inside the formation<br/>
 *  - Reference frame where the relative positions are defined<br/>
 *  - Formation shape configuration<br/>
 *  - Plan (set of maneuvers) to be followed by the formation center<br/>
 *  - Plan contrains (virtual leader speed and bank limits)<br/>
 *  - Supervision settings<br/>
 *  The formation reference frame may be:<br/>
 *  - Earth Fixed: Where the vehicles relative position do not depend on the followed path.<br/>
 *  This results in all UAVs following the same path with an offset relative to each other;<br/>
 *  - Path Fixed:  Where the vehicles relative position depends on the followed path,<br/>
 *  changing the inter-vehicle offset direction with the path direction.<br/>
 *  - Path Curved:  Where the vehicles relative position depends on the followed path,<br/>
 *  changing the inter-vehicle offset direction with the path direction and direction<br/>
 *  change rate.<br/>
 *  An offset in the xx axis results in a distance over the curved path line.<br/>
 *  An offset in the yy axis results in an offset of the vehicle path line relative to the<br/>
 *  formation center path line.<br/>
 */

public class Formation extends IMCMessage {

	public enum TYPE {
		REQUEST(0),
		REPORT(1);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum OP {
		START(0),
		STOP(1),
		READY(2),
		EXECUTING(3),
		FAILURE(4);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public enum REFERENCE_FRAME {
		EARTH_FIXED(0),
		PATH_FIXED(1),
		PATH_CURVED(2);

		protected long value;

		public long value() {
			return value;
		}

		REFERENCE_FRAME(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 484;

	public Formation() {
		super(ID_STATIC);
	}

	public Formation(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Formation(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Formation create(Object... values) {
		Formation m = new Formation();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Formation clone(IMCMessage msg) throws Exception {

		Formation m = new Formation();
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

	public Formation(String formation_name, TYPE type, OP op, String group_name, String plan_id, String description, REFERENCE_FRAME reference_frame, java.util.Collection<VehicleFormationParticipant> participants, float leader_bank_lim, float leader_speed_min, float leader_speed_max, float leader_alt_min, float leader_alt_max, float pos_sim_err_lim, float pos_sim_err_wrn, int pos_sim_err_timeout, float converg_max, int converg_timeout, int comms_timeout, float turb_lim, String custom) {
		super(ID_STATIC);
		if (formation_name != null)
			setFormationName(formation_name);
		setType(type);
		setOp(op);
		if (group_name != null)
			setGroupName(group_name);
		if (plan_id != null)
			setPlanId(plan_id);
		if (description != null)
			setDescription(description);
		setReferenceFrame(reference_frame);
		if (participants != null)
			setParticipants(participants);
		setLeaderBankLim(leader_bank_lim);
		setLeaderSpeedMin(leader_speed_min);
		setLeaderSpeedMax(leader_speed_max);
		setLeaderAltMin(leader_alt_min);
		setLeaderAltMax(leader_alt_max);
		setPosSimErrLim(pos_sim_err_lim);
		setPosSimErrWrn(pos_sim_err_wrn);
		setPosSimErrTimeout(pos_sim_err_timeout);
		setConvergMax(converg_max);
		setConvergTimeout(converg_timeout);
		setCommsTimeout(comms_timeout);
		setTurbLim(turb_lim);
		if (custom != null)
			setCustom(custom);
	}

	/**
	 *  @return Formation Name - plaintext
	 */
	public String getFormationName() {
		return getString("formation_name");
	}

	/**
	 *  @param formation_name Formation Name
	 */
	public Formation setFormationName(String formation_name) {
		values.put("formation_name", formation_name);
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
	public Formation setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public Formation setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public Formation setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Operation (enumerated) - uint8_t
	 */
	public OP getOp() {
		try {
			OP o = OP.valueOf(getMessageType().getFieldPossibleValues("op").get(getLong("op")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getOpStr() {
		return getString("op");
	}

	public short getOpVal() {
		return (short) getInteger("op");
	}

	/**
	 *  @param op Operation (enumerated)
	 */
	public Formation setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public Formation setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public Formation setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Target Group Name - plaintext
	 */
	public String getGroupName() {
		return getString("group_name");
	}

	/**
	 *  @param group_name Target Group Name
	 */
	public Formation setGroupName(String group_name) {
		values.put("group_name", group_name);
		return this;
	}

	/**
	 *  @return Formation Plan ID - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Formation Plan ID
	 */
	public Formation setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Plan Description - plaintext
	 */
	public String getDescription() {
		return getString("description");
	}

	/**
	 *  @param description Plan Description
	 */
	public Formation setDescription(String description) {
		values.put("description", description);
		return this;
	}

	/**
	 *  @return Formation Reference Frame (enumerated) - uint8_t
	 */
	public REFERENCE_FRAME getReferenceFrame() {
		try {
			REFERENCE_FRAME o = REFERENCE_FRAME.valueOf(getMessageType().getFieldPossibleValues("reference_frame").get(getLong("reference_frame")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getReferenceFrameStr() {
		return getString("reference_frame");
	}

	public short getReferenceFrameVal() {
		return (short) getInteger("reference_frame");
	}

	/**
	 *  @param reference_frame Formation Reference Frame (enumerated)
	 */
	public Formation setReferenceFrame(REFERENCE_FRAME reference_frame) {
		values.put("reference_frame", reference_frame.value());
		return this;
	}

	/**
	 *  @param reference_frame Formation Reference Frame (as a String)
	 */
	public Formation setReferenceFrameStr(String reference_frame) {
		setValue("reference_frame", reference_frame);
		return this;
	}

	/**
	 *  @param reference_frame Formation Reference Frame (integer value)
	 */
	public Formation setReferenceFrameVal(short reference_frame) {
		setValue("reference_frame", reference_frame);
		return this;
	}

	/**
	 *  @return Formation Participants - message-list
	 */
	public java.util.Vector<VehicleFormationParticipant> getParticipants() {
		try {
			return getMessageList("participants", VehicleFormationParticipant.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param participants Formation Participants
	 */
	public Formation setParticipants(java.util.Collection<VehicleFormationParticipant> participants) {
		values.put("participants", participants);
		return this;
	}

	/**
	 *  @return Formation Leader Bank Limit (rad) - fp32_t
	 */
	public double getLeaderBankLim() {
		return getDouble("leader_bank_lim");
	}

	/**
	 *  @param leader_bank_lim Formation Leader Bank Limit (rad)
	 */
	public Formation setLeaderBankLim(double leader_bank_lim) {
		values.put("leader_bank_lim", leader_bank_lim);
		return this;
	}

	/**
	 *  @return Formation Leader Minimum Speed (m/s) - fp32_t
	 */
	public double getLeaderSpeedMin() {
		return getDouble("leader_speed_min");
	}

	/**
	 *  @param leader_speed_min Formation Leader Minimum Speed (m/s)
	 */
	public Formation setLeaderSpeedMin(double leader_speed_min) {
		values.put("leader_speed_min", leader_speed_min);
		return this;
	}

	/**
	 *  @return Formation Leader Maximum Speed (m/s) - fp32_t
	 */
	public double getLeaderSpeedMax() {
		return getDouble("leader_speed_max");
	}

	/**
	 *  @param leader_speed_max Formation Leader Maximum Speed (m/s)
	 */
	public Formation setLeaderSpeedMax(double leader_speed_max) {
		values.put("leader_speed_max", leader_speed_max);
		return this;
	}

	/**
	 *  @return Formation Leader Minimum Altitude (m) - fp32_t
	 */
	public double getLeaderAltMin() {
		return getDouble("leader_alt_min");
	}

	/**
	 *  @param leader_alt_min Formation Leader Minimum Altitude (m)
	 */
	public Formation setLeaderAltMin(double leader_alt_min) {
		values.put("leader_alt_min", leader_alt_min);
		return this;
	}

	/**
	 *  @return Formation Leader Maximum Altitude (m) - fp32_t
	 */
	public double getLeaderAltMax() {
		return getDouble("leader_alt_max");
	}

	/**
	 *  @param leader_alt_max Formation Leader Maximum Altitude (m)
	 */
	public Formation setLeaderAltMax(double leader_alt_max) {
		values.put("leader_alt_max", leader_alt_max);
		return this;
	}

	/**
	 *  @return Position mismatch limit (m) - fp32_t
	 */
	public double getPosSimErrLim() {
		return getDouble("pos_sim_err_lim");
	}

	/**
	 *  @param pos_sim_err_lim Position mismatch limit (m)
	 */
	public Formation setPosSimErrLim(double pos_sim_err_lim) {
		values.put("pos_sim_err_lim", pos_sim_err_lim);
		return this;
	}

	/**
	 *  @return Position mismatch threshold (m) - fp32_t
	 */
	public double getPosSimErrWrn() {
		return getDouble("pos_sim_err_wrn");
	}

	/**
	 *  @param pos_sim_err_wrn Position mismatch threshold (m)
	 */
	public Formation setPosSimErrWrn(double pos_sim_err_wrn) {
		values.put("pos_sim_err_wrn", pos_sim_err_wrn);
		return this;
	}

	/**
	 *  @return Position mismatch time-out (s) - uint16_t
	 */
	public int getPosSimErrTimeout() {
		return getInteger("pos_sim_err_timeout");
	}

	/**
	 *  @param pos_sim_err_timeout Position mismatch time-out (s)
	 */
	public Formation setPosSimErrTimeout(int pos_sim_err_timeout) {
		values.put("pos_sim_err_timeout", pos_sim_err_timeout);
		return this;
	}

	/**
	 *  @return Convergence threshold (m) - fp32_t
	 */
	public double getConvergMax() {
		return getDouble("converg_max");
	}

	/**
	 *  @param converg_max Convergence threshold (m)
	 */
	public Formation setConvergMax(double converg_max) {
		values.put("converg_max", converg_max);
		return this;
	}

	/**
	 *  @return Convergence time-out (s) - uint16_t
	 */
	public int getConvergTimeout() {
		return getInteger("converg_timeout");
	}

	/**
	 *  @param converg_timeout Convergence time-out (s)
	 */
	public Formation setConvergTimeout(int converg_timeout) {
		values.put("converg_timeout", converg_timeout);
		return this;
	}

	/**
	 *  @return Communications time-out (s) - uint16_t
	 */
	public int getCommsTimeout() {
		return getInteger("comms_timeout");
	}

	/**
	 *  @param comms_timeout Communications time-out (s)
	 */
	public Formation setCommsTimeout(int comms_timeout) {
		values.put("comms_timeout", comms_timeout);
		return this;
	}

	/**
	 *  @return Turbulence limit (m/s) - fp32_t
	 */
	public double getTurbLim() {
		return getDouble("turb_lim");
	}

	/**
	 *  @param turb_lim Turbulence limit (m/s)
	 */
	public Formation setTurbLim(double turb_lim) {
		values.put("turb_lim", turb_lim);
		return this;
	}

	/**
	 *  @return Custom settings for maneuver (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getCustom() {
		return getTupleList("custom");
	}

	/**
	 *  @param custom Custom settings for maneuver (tuplelist)
	 */
	public Formation setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Formation setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
