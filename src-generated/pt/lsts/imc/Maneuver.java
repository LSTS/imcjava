/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2014, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Supertype Maneuver (Maneuver)<br/>
 *  Messages belonging to this type: <ul>
 *  <li>{@link Goto}</li>
 *  <li>{@link PopUp}</li>
 *  <li>{@link Teleoperation}</li>
 *  <li>{@link Loiter}</li>
 *  <li>{@link IdleManeuver}</li>
 *  <li>{@link LowLevelControl}</li>
 *  <li>{@link Rows}</li>
 *  <li>{@link FollowPath}</li>
 *  <li>{@link YoYo}</li>
 *  <li>{@link StationKeeping}</li>
 *  <li>{@link Elevator}</li>
 *  <li>{@link FollowTrajectory}</li>
 *  <li>{@link CustomManeuver}</li>
 *  <li>{@link VehicleFormation}</li>
 *  <li>{@link CompassCalibration}</li>
 *  <li>{@link CoverArea}</li>
 *  <li>{@link FollowReference}</li>
 *  <li>{@link CommsRelay}</li>
 *  <li>{@link FormationPlanExecution}</li>
 *  <li>{@link Dislodge}</li>
 *  </ul>
 */
public class Maneuver extends IMCMessage {

	public Maneuver(IMCDefinition defs, int id) {
		super(defs, id);
	}

	public Maneuver(int id) {
		super(id);
	}

	public Maneuver(int id, Object... values) {
		super(id, values);
	}

	public Maneuver(IMCMessage msg) throws Exception {
		super(msg.getMgid());
		getHeader().values.putAll(msg.getHeader().values);
		values.putAll(msg.values);
	}

	/**
	 *  @return Plan Reference - uint32_t
	 */
	public long getPlanRef() {
		return getLong("plan_ref");
	}

	/**
	 *  @param plan_ref Plan Reference
	 */
	public Maneuver setPlanRef(long plan_ref) {
		values.put("plan_ref", plan_ref);
		return this;
	}

	/**
	 *  @return Maneuver ID - plaintext
	 */
	public String getId() {
		return getString("id");
	}

	/**
	 *  @param id Maneuver ID
	 */
	public Maneuver setId(String id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Memento - plaintext
	 */
	public String getMemento() {
		return getString("memento");
	}

	/**
	 *  @param memento Memento
	 */
	public Maneuver setMemento(String memento) {
		values.put("memento", memento);
		return this;
	}

}
