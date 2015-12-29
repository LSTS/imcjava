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
 *  IMC Message Device State (282)<br/>
 *  Location of a specific device in the system infrastructure.<br/>
 */

public class DeviceState extends IMCMessage {

	public static final int ID_STATIC = 282;

	public DeviceState() {
		super(ID_STATIC);
	}

	public DeviceState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DeviceState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DeviceState create(Object... values) {
		DeviceState m = new DeviceState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DeviceState clone(IMCMessage msg) throws Exception {

		DeviceState m = new DeviceState();
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

	public DeviceState(float x, float y, float z, float phi, float theta, float psi) {
		super(ID_STATIC);
		setX(x);
		setY(y);
		setZ(z);
		setPhi(phi);
		setTheta(theta);
		setPsi(psi);
	}

	/**
	 *  @return Device Position - X (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x Device Position - X (m)
	 */
	public DeviceState setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Device Position - Y (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Device Position - Y (m)
	 */
	public DeviceState setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Device Position - Z (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Device Position - Z (m)
	 */
	public DeviceState setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Device Rotation - X (rad) - fp32_t
	 */
	public double getPhi() {
		return getDouble("phi");
	}

	/**
	 *  @param phi Device Rotation - X (rad)
	 */
	public DeviceState setPhi(double phi) {
		values.put("phi", phi);
		return this;
	}

	/**
	 *  @return Device Rotation - Y (rad) - fp32_t
	 */
	public double getTheta() {
		return getDouble("theta");
	}

	/**
	 *  @param theta Device Rotation - Y (rad)
	 */
	public DeviceState setTheta(double theta) {
		values.put("theta", theta);
		return this;
	}

	/**
	 *  @return Device Rotation - Z (rad) - fp32_t
	 */
	public double getPsi() {
		return getDouble("psi");
	}

	/**
	 *  @param psi Device Rotation - Z (rad)
	 */
	public DeviceState setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

}
