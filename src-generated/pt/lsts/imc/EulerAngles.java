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
 *  IMC Message Euler Angles (254)<br/>
 *  Report of spatial orientation according to SNAME's notation<br/>
 *  (1950).<br/>
 */

public class EulerAngles extends IMCMessage {

	public static final int ID_STATIC = 254;

	public EulerAngles() {
		super(ID_STATIC);
	}

	public EulerAngles(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EulerAngles(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EulerAngles create(Object... values) {
		EulerAngles m = new EulerAngles();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EulerAngles clone(IMCMessage msg) throws Exception {

		EulerAngles m = new EulerAngles();
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

	public EulerAngles(double time, double phi, double theta, double psi, double psi_magnetic) {
		super(ID_STATIC);
		setTime(time);
		setPhi(phi);
		setTheta(theta);
		setPsi(psi);
		setPsiMagnetic(psi_magnetic);
	}

	/**
	 *  @return Device Time (s) - fp64_t
	 */
	public double getTime() {
		return getDouble("time");
	}

	/**
	 *  @param time Device Time (s)
	 */
	public EulerAngles setTime(double time) {
		values.put("time", time);
		return this;
	}

	/**
	 *  @return Roll Angle (rad) - fp64_t
	 */
	public double getPhi() {
		return getDouble("phi");
	}

	/**
	 *  @param phi Roll Angle (rad)
	 */
	public EulerAngles setPhi(double phi) {
		values.put("phi", phi);
		return this;
	}

	/**
	 *  @return Pitch Angle (rad) - fp64_t
	 */
	public double getTheta() {
		return getDouble("theta");
	}

	/**
	 *  @param theta Pitch Angle (rad)
	 */
	public EulerAngles setTheta(double theta) {
		values.put("theta", theta);
		return this;
	}

	/**
	 *  @return Yaw Angle (True) (rad) - fp64_t
	 */
	public double getPsi() {
		return getDouble("psi");
	}

	/**
	 *  @param psi Yaw Angle (True) (rad)
	 */
	public EulerAngles setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

	/**
	 *  @return Yaw Angle (Magnetic) (rad) - fp64_t
	 */
	public double getPsiMagnetic() {
		return getDouble("psi_magnetic");
	}

	/**
	 *  @param psi_magnetic Yaw Angle (Magnetic) (rad)
	 */
	public EulerAngles setPsiMagnetic(double psi_magnetic) {
		values.put("psi_magnetic", psi_magnetic);
		return this;
	}

}
