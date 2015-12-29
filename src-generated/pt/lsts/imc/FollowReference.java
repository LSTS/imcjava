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
 *  IMC Message Follow Reference Maneuver (478)<br/>
 *  This maneuver follows a reference given by an external entity.<br/>
 */

@SuppressWarnings("unchecked")
public class FollowReference extends Maneuver {

	public static final int ID_STATIC = 478;

	public FollowReference() {
		super(ID_STATIC);
	}

	public FollowReference(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FollowReference(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FollowReference create(Object... values) {
		FollowReference m = new FollowReference();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FollowReference clone(IMCMessage msg) throws Exception {

		FollowReference m = new FollowReference();
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

	public FollowReference(int control_src, short control_ent, float timeout, float loiter_radius, float altitude_interval) {
		super(ID_STATIC);
		setControlSrc(control_src);
		setControlEnt(control_ent);
		setTimeout(timeout);
		setLoiterRadius(loiter_radius);
		setAltitudeInterval(altitude_interval);
	}

	/**
	 *  @return Controlling Source - uint16_t
	 */
	public int getControlSrc() {
		return getInteger("control_src");
	}

	/**
	 *  @param control_src Controlling Source
	 */
	public FollowReference setControlSrc(int control_src) {
		values.put("control_src", control_src);
		return this;
	}

	/**
	 *  @return Controlling Entity - uint8_t
	 */
	public short getControlEnt() {
		return (short) getInteger("control_ent");
	}

	/**
	 *  @param control_ent Controlling Entity
	 */
	public FollowReference setControlEnt(short control_ent) {
		values.put("control_ent", control_ent);
		return this;
	}

	/**
	 *  @return Reference Update Timeout - fp32_t
	 */
	public double getTimeout() {
		return getDouble("timeout");
	}

	/**
	 *  @param timeout Reference Update Timeout
	 */
	public FollowReference setTimeout(double timeout) {
		values.put("timeout", timeout);
		return this;
	}

	/**
	 *  @return Loiter Radius - fp32_t
	 */
	public double getLoiterRadius() {
		return getDouble("loiter_radius");
	}

	/**
	 *  @param loiter_radius Loiter Radius
	 */
	public FollowReference setLoiterRadius(double loiter_radius) {
		values.put("loiter_radius", loiter_radius);
		return this;
	}

	/**
	 *  @return Altitude Interval - fp32_t
	 */
	public double getAltitudeInterval() {
		return getDouble("altitude_interval");
	}

	/**
	 *  @param altitude_interval Altitude Interval
	 */
	public FollowReference setAltitudeInterval(double altitude_interval) {
		values.put("altitude_interval", altitude_interval);
		return this;
	}

}
