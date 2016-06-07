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
 *  IMC Message USBL Angles Extended (898)<br/>
 *  This message contains information, collected using USBL, about the<br/>
 *  bearing and elevation of a target.<br/>
 */

public class UsblAnglesExtended extends IMCMessage {

	public static final int ID_STATIC = 898;

	public UsblAnglesExtended() {
		super(ID_STATIC);
	}

	public UsblAnglesExtended(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UsblAnglesExtended(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UsblAnglesExtended create(Object... values) {
		UsblAnglesExtended m = new UsblAnglesExtended();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UsblAnglesExtended clone(IMCMessage msg) throws Exception {

		UsblAnglesExtended m = new UsblAnglesExtended();
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

	public UsblAnglesExtended(String target, float lbearing, float lelevation, float bearing, float elevation, float phi, float theta, float psi, float accuracy) {
		super(ID_STATIC);
		if (target != null)
			setTarget(target);
		setLbearing(lbearing);
		setLelevation(lelevation);
		setBearing(bearing);
		setElevation(elevation);
		setPhi(phi);
		setTheta(theta);
		setPsi(psi);
		setAccuracy(accuracy);
	}

	/**
	 *  @return Target - plaintext
	 */
	public String getTarget() {
		return getString("target");
	}

	/**
	 *  @param target Target
	 */
	public UsblAnglesExtended setTarget(String target) {
		values.put("target", target);
		return this;
	}

	/**
	 *  @return Local Bearing (rad) - fp32_t
	 */
	public double getLbearing() {
		return getDouble("lbearing");
	}

	/**
	 *  @param lbearing Local Bearing (rad)
	 */
	public UsblAnglesExtended setLbearing(double lbearing) {
		values.put("lbearing", lbearing);
		return this;
	}

	/**
	 *  @return Local Elevation (rad) - fp32_t
	 */
	public double getLelevation() {
		return getDouble("lelevation");
	}

	/**
	 *  @param lelevation Local Elevation (rad)
	 */
	public UsblAnglesExtended setLelevation(double lelevation) {
		values.put("lelevation", lelevation);
		return this;
	}

	/**
	 *  @return Bearing (rad) - fp32_t
	 */
	public double getBearing() {
		return getDouble("bearing");
	}

	/**
	 *  @param bearing Bearing (rad)
	 */
	public UsblAnglesExtended setBearing(double bearing) {
		values.put("bearing", bearing);
		return this;
	}

	/**
	 *  @return Elevation (rad) - fp32_t
	 */
	public double getElevation() {
		return getDouble("elevation");
	}

	/**
	 *  @param elevation Elevation (rad)
	 */
	public UsblAnglesExtended setElevation(double elevation) {
		values.put("elevation", elevation);
		return this;
	}

	/**
	 *  @return Roll Angle (rad) - fp32_t
	 */
	public double getPhi() {
		return getDouble("phi");
	}

	/**
	 *  @param phi Roll Angle (rad)
	 */
	public UsblAnglesExtended setPhi(double phi) {
		values.put("phi", phi);
		return this;
	}

	/**
	 *  @return Pitch Angle (rad) - fp32_t
	 */
	public double getTheta() {
		return getDouble("theta");
	}

	/**
	 *  @param theta Pitch Angle (rad)
	 */
	public UsblAnglesExtended setTheta(double theta) {
		values.put("theta", theta);
		return this;
	}

	/**
	 *  @return Yaw Angle (rad) - fp32_t
	 */
	public double getPsi() {
		return getDouble("psi");
	}

	/**
	 *  @param psi Yaw Angle (rad)
	 */
	public UsblAnglesExtended setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

	/**
	 *  @return Accuracy (rad) - fp32_t
	 */
	public double getAccuracy() {
		return getDouble("accuracy");
	}

	/**
	 *  @param accuracy Accuracy (rad)
	 */
	public UsblAnglesExtended setAccuracy(double accuracy) {
		values.put("accuracy", accuracy);
		return this;
	}

}
