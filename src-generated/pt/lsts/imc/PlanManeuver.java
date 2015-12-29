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
 *  IMC Message Plan Maneuver (552)<br/>
 *  Named plan maneuver.<br/>
 */

public class PlanManeuver extends IMCMessage {

	public static final int ID_STATIC = 552;

	public PlanManeuver() {
		super(ID_STATIC);
	}

	public PlanManeuver(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanManeuver(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanManeuver create(Object... values) {
		PlanManeuver m = new PlanManeuver();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanManeuver clone(IMCMessage msg) throws Exception {

		PlanManeuver m = new PlanManeuver();
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

	public PlanManeuver(String maneuver_id, Maneuver data, java.util.Collection<IMCMessage> start_actions, java.util.Collection<IMCMessage> end_actions) {
		super(ID_STATIC);
		if (maneuver_id != null)
			setManeuverId(maneuver_id);
		if (data != null)
			setData(data);
		if (start_actions != null)
			setStartActions(start_actions);
		if (end_actions != null)
			setEndActions(end_actions);
	}

	/**
	 *  @return Maneuver ID - plaintext
	 */
	public String getManeuverId() {
		return getString("maneuver_id");
	}

	/**
	 *  @param maneuver_id Maneuver ID
	 */
	public PlanManeuver setManeuverId(String maneuver_id) {
		values.put("maneuver_id", maneuver_id);
		return this;
	}

	/**
	 *  @return Maneuver Specification - message
	 */
	public Maneuver getData() {
		try {
			IMCMessage obj = getMessage("data");
			if (obj instanceof Maneuver)
				return (Maneuver) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param data Maneuver Specification
	 */
	public PlanManeuver setData(Maneuver data) {
		values.put("data", data);
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
	public PlanManeuver setStartActions(java.util.Collection<IMCMessage> start_actions) {
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
	public PlanManeuver setEndActions(java.util.Collection<IMCMessage> end_actions) {
		values.put("end_actions", end_actions);
		return this;
	}

}
