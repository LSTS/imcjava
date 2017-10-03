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
 *  IMC Message SOI Plan (851)<br/>
 */

public class SoiPlan extends IMCMessage {

	public static final int ID_STATIC = 851;

	public SoiPlan() {
		super(ID_STATIC);
	}

	public SoiPlan(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SoiPlan(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SoiPlan create(Object... values) {
		SoiPlan m = new SoiPlan();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SoiPlan clone(IMCMessage msg) throws Exception {

		SoiPlan m = new SoiPlan();
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

	public SoiPlan(int plan_id, java.util.Collection<SoiWaypoint> waypoints) {
		super(ID_STATIC);
		setPlanId(plan_id);
		if (waypoints != null)
			setWaypoints(waypoints);
	}

	/**
	 *  @return Plan Identifier - uint16_t
	 */
	public int getPlanId() {
		return getInteger("plan_id");
	}

	/**
	 *  @param plan_id Plan Identifier
	 */
	public SoiPlan setPlanId(int plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Waypoints - message-list
	 */
	public java.util.Vector<SoiWaypoint> getWaypoints() {
		try {
			return getMessageList("waypoints", SoiWaypoint.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param waypoints Waypoints
	 */
	public SoiPlan setWaypoints(java.util.Collection<SoiWaypoint> waypoints) {
		values.put("waypoints", waypoints);
		return this;
	}

}
