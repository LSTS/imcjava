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
 *  IMC Message Vehicle Formation Participant (467)<br/>
 *  Definition of a vehicle participant in a VehicleFormation maneuver.<br/>
 */

public class VehicleFormationParticipant extends IMCMessage {

	public static final int ID_STATIC = 467;

	public VehicleFormationParticipant() {
		super(ID_STATIC);
	}

	public VehicleFormationParticipant(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VehicleFormationParticipant(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VehicleFormationParticipant create(Object... values) {
		VehicleFormationParticipant m = new VehicleFormationParticipant();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VehicleFormationParticipant clone(IMCMessage msg) throws Exception {

		VehicleFormationParticipant m = new VehicleFormationParticipant();
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

	public VehicleFormationParticipant(int vid, float off_x, float off_y, float off_z) {
		super(ID_STATIC);
		setVid(vid);
		setOffX(off_x);
		setOffY(off_y);
		setOffZ(off_z);
	}

	/**
	 *  @return ID (IMC address) - uint16_t
	 */
	public int getVid() {
		return getInteger("vid");
	}

	/**
	 *  @param vid ID (IMC address)
	 */
	public VehicleFormationParticipant setVid(int vid) {
		values.put("vid", vid);
		return this;
	}

	/**
	 *  @return Formation offset -- Along-track (m) - fp32_t
	 */
	public double getOffX() {
		return getDouble("off_x");
	}

	/**
	 *  @param off_x Formation offset -- Along-track (m)
	 */
	public VehicleFormationParticipant setOffX(double off_x) {
		values.put("off_x", off_x);
		return this;
	}

	/**
	 *  @return Formation offset -- Cross-track (m) - fp32_t
	 */
	public double getOffY() {
		return getDouble("off_y");
	}

	/**
	 *  @param off_y Formation offset -- Cross-track (m)
	 */
	public VehicleFormationParticipant setOffY(double off_y) {
		values.put("off_y", off_y);
		return this;
	}

	/**
	 *  @return Formation offset -- Depth/Altitude (m) - fp32_t
	 */
	public double getOffZ() {
		return getDouble("off_z");
	}

	/**
	 *  @param off_z Formation offset -- Depth/Altitude (m)
	 */
	public VehicleFormationParticipant setOffZ(double off_z) {
		values.put("off_z", off_z);
		return this;
	}

}
