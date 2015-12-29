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
 *  IMC Message Simulated State (50)<br/>
 *  This message presents the simulated state of the vehicle. The simulated<br/>
 *  state attempts to provide a realistic state interpretation of operating<br/>
 *  various kinds of vehicles.<br/>
 */

public class SimulatedState extends IMCMessage {

	public static final int ID_STATIC = 50;

	public SimulatedState() {
		super(ID_STATIC);
	}

	public SimulatedState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimulatedState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SimulatedState create(Object... values) {
		SimulatedState m = new SimulatedState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SimulatedState clone(IMCMessage msg) throws Exception {

		SimulatedState m = new SimulatedState();
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

	public SimulatedState(double lat, double lon, float height, float x, float y, float z, float phi, float theta, float psi, float u, float v, float w, float p, float q, float r, float svx, float svy, float svz) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setHeight(height);
		setX(x);
		setY(y);
		setZ(z);
		setPhi(phi);
		setTheta(theta);
		setPsi(psi);
		setU(u);
		setV(v);
		setW(w);
		setP(p);
		setQ(q);
		setR(r);
		setSvx(svx);
		setSvy(svy);
		setSvz(svz);
	}

	/**
	 *  @return Latitude (WGS-84) (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (WGS-84) (rad)
	 */
	public SimulatedState setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (WGS-84) (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (WGS-84) (rad)
	 */
	public SimulatedState setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Height (WGS-84) (m) - fp32_t
	 */
	public double getHeight() {
		return getDouble("height");
	}

	/**
	 *  @param height Height (WGS-84) (m)
	 */
	public SimulatedState setHeight(double height) {
		values.put("height", height);
		return this;
	}

	/**
	 *  @return Offset north (m) (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x Offset north (m) (m)
	 */
	public SimulatedState setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Offset east (m) (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Offset east (m) (m)
	 */
	public SimulatedState setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Offset down (m) (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Offset down (m) (m)
	 */
	public SimulatedState setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Rotation over x axis (rad) - fp32_t
	 */
	public double getPhi() {
		return getDouble("phi");
	}

	/**
	 *  @param phi Rotation over x axis (rad)
	 */
	public SimulatedState setPhi(double phi) {
		values.put("phi", phi);
		return this;
	}

	/**
	 *  @return Rotation over y axis (rad) - fp32_t
	 */
	public double getTheta() {
		return getDouble("theta");
	}

	/**
	 *  @param theta Rotation over y axis (rad)
	 */
	public SimulatedState setTheta(double theta) {
		values.put("theta", theta);
		return this;
	}

	/**
	 *  @return Rotation over z axis (rad) - fp32_t
	 */
	public double getPsi() {
		return getDouble("psi");
	}

	/**
	 *  @param psi Rotation over z axis (rad)
	 */
	public SimulatedState setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

	/**
	 *  @return Body-Fixed xx Linear Velocity (m/s) - fp32_t
	 */
	public double getU() {
		return getDouble("u");
	}

	/**
	 *  @param u Body-Fixed xx Linear Velocity (m/s)
	 */
	public SimulatedState setU(double u) {
		values.put("u", u);
		return this;
	}

	/**
	 *  @return Body-Fixed yy Linear Velocity (m/s) - fp32_t
	 */
	public double getV() {
		return getDouble("v");
	}

	/**
	 *  @param v Body-Fixed yy Linear Velocity (m/s)
	 */
	public SimulatedState setV(double v) {
		values.put("v", v);
		return this;
	}

	/**
	 *  @return Body-Fixed zz Linear Velocity (m/s) - fp32_t
	 */
	public double getW() {
		return getDouble("w");
	}

	/**
	 *  @param w Body-Fixed zz Linear Velocity (m/s)
	 */
	public SimulatedState setW(double w) {
		values.put("w", w);
		return this;
	}

	/**
	 *  @return Angular Velocity in x (rad/s) - fp32_t
	 */
	public double getP() {
		return getDouble("p");
	}

	/**
	 *  @param p Angular Velocity in x (rad/s)
	 */
	public SimulatedState setP(double p) {
		values.put("p", p);
		return this;
	}

	/**
	 *  @return Angular Velocity in y (rad/s) - fp32_t
	 */
	public double getQ() {
		return getDouble("q");
	}

	/**
	 *  @param q Angular Velocity in y (rad/s)
	 */
	public SimulatedState setQ(double q) {
		values.put("q", q);
		return this;
	}

	/**
	 *  @return Angular Velocity in z (rad/s) - fp32_t
	 */
	public double getR() {
		return getDouble("r");
	}

	/**
	 *  @param r Angular Velocity in z (rad/s)
	 */
	public SimulatedState setR(double r) {
		values.put("r", r);
		return this;
	}

	/**
	 *  @return Stream Velocity X (North) (m/s) - fp32_t
	 */
	public double getSvx() {
		return getDouble("svx");
	}

	/**
	 *  @param svx Stream Velocity X (North) (m/s)
	 */
	public SimulatedState setSvx(double svx) {
		values.put("svx", svx);
		return this;
	}

	/**
	 *  @return Stream Velocity Y (East) (m/s) - fp32_t
	 */
	public double getSvy() {
		return getDouble("svy");
	}

	/**
	 *  @param svy Stream Velocity Y (East) (m/s)
	 */
	public SimulatedState setSvy(double svy) {
		values.put("svy", svy);
		return this;
	}

	/**
	 *  @return Stream Velocity Z (Down) (m/s) - fp32_t
	 */
	public double getSvz() {
		return getDouble("svz");
	}

	/**
	 *  @param svz Stream Velocity Z (Down) (m/s)
	 */
	public SimulatedState setSvz(double svz) {
		values.put("svz", svz);
		return this;
	}

}
