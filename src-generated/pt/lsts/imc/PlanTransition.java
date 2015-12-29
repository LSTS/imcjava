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
 *  IMC Message Plan Transition (553)<br/>
 *  Describes a plan transition within a plan specification. A<br/>
 *  transition states the vehicle conditions that must be met to<br/>
 *  signal the transition, the maneuver that should be started as a<br/>
 *  result, and an optional set of actions triggered by the<br/>
 *  transition.<br/>
 */

public class PlanTransition extends IMCMessage {

	public static final int ID_STATIC = 553;

	public PlanTransition() {
		super(ID_STATIC);
	}

	public PlanTransition(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanTransition(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanTransition create(Object... values) {
		PlanTransition m = new PlanTransition();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanTransition clone(IMCMessage msg) throws Exception {

		PlanTransition m = new PlanTransition();
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

	public PlanTransition(String source_man, String dest_man, String conditions, java.util.Collection<IMCMessage> actions) {
		super(ID_STATIC);
		if (source_man != null)
			setSourceMan(source_man);
		if (dest_man != null)
			setDestMan(dest_man);
		if (conditions != null)
			setConditions(conditions);
		if (actions != null)
			setActions(actions);
	}

	/**
	 *  @return Source - plaintext
	 */
	public String getSourceMan() {
		return getString("source_man");
	}

	/**
	 *  @param source_man Source
	 */
	public PlanTransition setSourceMan(String source_man) {
		values.put("source_man", source_man);
		return this;
	}

	/**
	 *  @return Destination Maneuver Name - plaintext
	 */
	public String getDestMan() {
		return getString("dest_man");
	}

	/**
	 *  @param dest_man Destination Maneuver Name
	 */
	public PlanTransition setDestMan(String dest_man) {
		values.put("dest_man", dest_man);
		return this;
	}

	/**
	 *  @return Transition conditions - plaintext
	 */
	public String getConditions() {
		return getString("conditions");
	}

	/**
	 *  @param conditions Transition conditions
	 */
	public PlanTransition setConditions(String conditions) {
		values.put("conditions", conditions);
		return this;
	}

	/**
	 *  @return Transition actions - message-list
	 */
	public java.util.Vector<IMCMessage> getActions() {
		return getMessageList("actions");
	}

	/**
	 *  @param actions Transition actions
	 */
	public PlanTransition setActions(java.util.Collection<IMCMessage> actions) {
		values.put("actions", actions);
		return this;
	}

}
