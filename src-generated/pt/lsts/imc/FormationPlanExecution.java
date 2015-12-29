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
 *  IMC Message Formation Plan Execution (477)<br/>
 *  A "Formation Plan" is a maneuver specifying a plan for a team of vehicles.<br/>
 *  The maneuver defines:<br/>
 *  - Vehicles included in the formation group<br/>
 *  - Formation shape configuration<br/>
 *  - Plan (set of maneuvers) to be followed by the formation center<br/>
 *  - Speed at which that plan is followed<br/>
 *  - Path contrains (virtual leader bank limit)<br/>
 *  - Supervision settings<br/>
 */

@SuppressWarnings("unchecked")
public class FormationPlanExecution extends Maneuver {

	public static final int ID_STATIC = 477;

	public FormationPlanExecution() {
		super(ID_STATIC);
	}

	public FormationPlanExecution(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormationPlanExecution(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormationPlanExecution create(Object... values) {
		FormationPlanExecution m = new FormationPlanExecution();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormationPlanExecution clone(IMCMessage msg) throws Exception {

		FormationPlanExecution m = new FormationPlanExecution();
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

	public FormationPlanExecution(String group_name, String formation_name, String plan_id, String description, float leader_speed, float leader_bank_lim, float pos_sim_err_lim, float pos_sim_err_wrn, int pos_sim_err_timeout, float converg_max, int converg_timeout, int comms_timeout, float turb_lim, String custom) {
		super(ID_STATIC);
		if (group_name != null)
			setGroupName(group_name);
		if (formation_name != null)
			setFormationName(formation_name);
		if (plan_id != null)
			setPlanId(plan_id);
		if (description != null)
			setDescription(description);
		setLeaderSpeed(leader_speed);
		setLeaderBankLim(leader_bank_lim);
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
	 *  @return Target Group Name - plaintext
	 */
	public String getGroupName() {
		return getString("group_name");
	}

	/**
	 *  @param group_name Target Group Name
	 */
	public FormationPlanExecution setGroupName(String group_name) {
		values.put("group_name", group_name);
		return this;
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
	public FormationPlanExecution setFormationName(String formation_name) {
		values.put("formation_name", formation_name);
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
	public FormationPlanExecution setPlanId(String plan_id) {
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
	public FormationPlanExecution setDescription(String description) {
		values.put("description", description);
		return this;
	}

	/**
	 *  @return Formation Leader Flight Airspeed (m/s) - fp32_t
	 */
	public double getLeaderSpeed() {
		return getDouble("leader_speed");
	}

	/**
	 *  @param leader_speed Formation Leader Flight Airspeed (m/s)
	 */
	public FormationPlanExecution setLeaderSpeed(double leader_speed) {
		values.put("leader_speed", leader_speed);
		return this;
	}

	/**
	 *  @return Formation leader flight bank limit (m/s) - fp32_t
	 */
	public double getLeaderBankLim() {
		return getDouble("leader_bank_lim");
	}

	/**
	 *  @param leader_bank_lim Formation leader flight bank limit (m/s)
	 */
	public FormationPlanExecution setLeaderBankLim(double leader_bank_lim) {
		values.put("leader_bank_lim", leader_bank_lim);
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
	public FormationPlanExecution setPosSimErrLim(double pos_sim_err_lim) {
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
	public FormationPlanExecution setPosSimErrWrn(double pos_sim_err_wrn) {
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
	public FormationPlanExecution setPosSimErrTimeout(int pos_sim_err_timeout) {
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
	public FormationPlanExecution setConvergMax(double converg_max) {
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
	public FormationPlanExecution setConvergTimeout(int converg_timeout) {
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
	public FormationPlanExecution setCommsTimeout(int comms_timeout) {
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
	public FormationPlanExecution setTurbLim(double turb_lim) {
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
	public FormationPlanExecution setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public FormationPlanExecution setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
