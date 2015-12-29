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
 *  IMC Message Plan Specification (551)<br/>
 *  Identity and description of a plan's general parameters,<br/>
 *  associated with plan loading (i.e. load plan command in<br/>
 *  <code>PlanCommand</code>).<br/>
 *  A plan specification is defined by a plan identifier, a set of<br/>
 *  maneuver specifications and a start maneuver from that set.<br/>
 *  See the {@link PlanManeuver} message for details on maneuver<br/>
 *  specification.<br/>
 */

public class PlanSpecification extends IMCMessage {

	public static final int ID_STATIC = 551;

	public PlanSpecification() {
		super(ID_STATIC);
	}

	public PlanSpecification(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanSpecification(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanSpecification create(Object... values) {
		PlanSpecification m = new PlanSpecification();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanSpecification clone(IMCMessage msg) throws Exception {

		PlanSpecification m = new PlanSpecification();
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

	public PlanSpecification(String plan_id, String description, String vnamespace, java.util.Collection<PlanVariable> variables, String start_man_id, java.util.Collection<PlanManeuver> maneuvers, java.util.Collection<PlanTransition> transitions, java.util.Collection<IMCMessage> start_actions, java.util.Collection<IMCMessage> end_actions) {
		super(ID_STATIC);
		if (plan_id != null)
			setPlanId(plan_id);
		if (description != null)
			setDescription(description);
		if (vnamespace != null)
			setVnamespace(vnamespace);
		if (variables != null)
			setVariables(variables);
		if (start_man_id != null)
			setStartManId(start_man_id);
		if (maneuvers != null)
			setManeuvers(maneuvers);
		if (transitions != null)
			setTransitions(transitions);
		if (start_actions != null)
			setStartActions(start_actions);
		if (end_actions != null)
			setEndActions(end_actions);
	}

	/**
	 *  @return Plan ID - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan ID
	 */
	public PlanSpecification setPlanId(String plan_id) {
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
	public PlanSpecification setDescription(String description) {
		values.put("description", description);
		return this;
	}

	/**
	 *  @return Namespace - plaintext
	 */
	public String getVnamespace() {
		return getString("vnamespace");
	}

	/**
	 *  @param vnamespace Namespace
	 */
	public PlanSpecification setVnamespace(String vnamespace) {
		values.put("vnamespace", vnamespace);
		return this;
	}

	/**
	 *  @return Plan Variables - message-list
	 */
	public java.util.Vector<PlanVariable> getVariables() {
		try {
			return getMessageList("variables", PlanVariable.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param variables Plan Variables
	 */
	public PlanSpecification setVariables(java.util.Collection<PlanVariable> variables) {
		values.put("variables", variables);
		return this;
	}

	/**
	 *  @return Starting maneuver - plaintext
	 */
	public String getStartManId() {
		return getString("start_man_id");
	}

	/**
	 *  @param start_man_id Starting maneuver
	 */
	public PlanSpecification setStartManId(String start_man_id) {
		values.put("start_man_id", start_man_id);
		return this;
	}

	/**
	 *  @return Maneuvers - message-list
	 */
	public java.util.Vector<PlanManeuver> getManeuvers() {
		try {
			return getMessageList("maneuvers", PlanManeuver.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param maneuvers Maneuvers
	 */
	public PlanSpecification setManeuvers(java.util.Collection<PlanManeuver> maneuvers) {
		values.put("maneuvers", maneuvers);
		return this;
	}

	/**
	 *  @return Transitions - message-list
	 */
	public java.util.Vector<PlanTransition> getTransitions() {
		try {
			return getMessageList("transitions", PlanTransition.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param transitions Transitions
	 */
	public PlanSpecification setTransitions(java.util.Collection<PlanTransition> transitions) {
		values.put("transitions", transitions);
		return this;
	}

	/**
	 *  @return Start Actions - message-list
	 */
	public java.util.Vector<IMCMessage> getStartActions() {
		return getMessageList("start_actions");
	}

	/**
	 *  @param start_actions Start Actions
	 */
	public PlanSpecification setStartActions(java.util.Collection<IMCMessage> start_actions) {
		values.put("start_actions", start_actions);
		return this;
	}

	/**
	 *  @return End Actions - message-list
	 */
	public java.util.Vector<IMCMessage> getEndActions() {
		return getMessageList("end_actions");
	}

	/**
	 *  @param end_actions End Actions
	 */
	public PlanSpecification setEndActions(java.util.Collection<IMCMessage> end_actions) {
		values.put("end_actions", end_actions);
		return this;
	}

}
