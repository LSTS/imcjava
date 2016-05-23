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
 *  IMC Message USBL Position Extended (899)<br/>
 *  This message contains information, collected using USBL, about a<br/>
 *  target's position.<br/>
 */

public class UsblPositionExtended extends IMCMessage {

	public static final int ID_STATIC = 899;

	public UsblPositionExtended() {
		super(ID_STATIC);
	}

	public UsblPositionExtended(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UsblPositionExtended(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UsblPositionExtended create(Object... values) {
		UsblPositionExtended m = new UsblPositionExtended();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UsblPositionExtended clone(IMCMessage msg) throws Exception {

		UsblPositionExtended m = new UsblPositionExtended();
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

	public UsblPositionExtended(String target, float x, float y, float z, float n, float e, float d, float phi, float theta, float psi, float accuracy) {
		super(ID_STATIC);
		if (target != null)
			setTarget(target);
		setX(x);
		setY(y);
		setZ(z);
		setN(n);
		setE(e);
		setD(d);
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
	public UsblPositionExtended setTarget(String target) {
		values.put("target", target);
		return this;
	}

	/**
	 *  @return X (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x X (m)
	 */
	public UsblPositionExtended setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Y (m)
	 */
	public UsblPositionExtended setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z (m)
	 */
	public UsblPositionExtended setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return N (m) - fp32_t
	 */
	public double getN() {
		return getDouble("n");
	}

	/**
	 *  @param n N (m)
	 */
	public UsblPositionExtended setN(double n) {
		values.put("n", n);
		return this;
	}

	/**
	 *  @return E (m) - fp32_t
	 */
	public double getE() {
		return getDouble("e");
	}

	/**
	 *  @param e E (m)
	 */
	public UsblPositionExtended setE(double e) {
		values.put("e", e);
		return this;
	}

	/**
	 *  @return D (m) - fp32_t
	 */
	public double getD() {
		return getDouble("d");
	}

	/**
	 *  @param d D (m)
	 */
	public UsblPositionExtended setD(double d) {
		values.put("d", d);
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
	public UsblPositionExtended setPhi(double phi) {
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
	public UsblPositionExtended setTheta(double theta) {
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
	public UsblPositionExtended setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

	/**
	 *  @return Accuracy (m) - fp32_t
	 */
	public double getAccuracy() {
		return getDouble("accuracy");
	}

	/**
	 *  @param accuracy Accuracy (m)
	 */
	public UsblPositionExtended setAccuracy(double accuracy) {
		values.put("accuracy", accuracy);
		return this;
	}

}
