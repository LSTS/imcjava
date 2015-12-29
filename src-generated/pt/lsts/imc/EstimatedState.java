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
 *  IMC Message Estimated State (350)<br/>
 *  This message presents the estimated state of the vehicle.<br/>
 *  EstimatedState is a complete description of the system<br/>
 *  in terms of parameters such as position, orientation and<br/>
 *  velocities at a particular moment in time.<br/>
 *  The system position is given by a North-East-Down (NED)<br/>
 *  local tangent plane displacement (x, y, z) relative to<br/>
 *  an absolute WGS-84 coordinate (latitude, longitude,<br/>
 *  height above ellipsoid).<br/>
 *  The symbols for position and attitude as well as linear and<br/>
 *  angular velocities were chosen according to SNAME's notation (1950).<br/>
 *  The body-fixed reference frame and Euler angles are depicted<br/>
 *  next:<br/>
 *  Euler angles<br/>
 */

public class EstimatedState extends IMCMessage {

	public static final int ID_STATIC = 350;

	public EstimatedState() {
		super(ID_STATIC);
	}

	public EstimatedState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EstimatedState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EstimatedState create(Object... values) {
		EstimatedState m = new EstimatedState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EstimatedState clone(IMCMessage msg) throws Exception {

		EstimatedState m = new EstimatedState();
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

	public EstimatedState(double lat, double lon, float height, float x, float y, float z, float phi, float theta, float psi, float u, float v, float w, float vx, float vy, float vz, float p, float q, float r, float depth, float alt) {
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
		setVx(vx);
		setVy(vy);
		setVz(vz);
		setP(p);
		setQ(q);
		setR(r);
		setDepth(depth);
		setAlt(alt);
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
	public EstimatedState setLat(double lat) {
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
	public EstimatedState setLon(double lon) {
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
	public EstimatedState setHeight(double height) {
		values.put("height", height);
		return this;
	}

	/**
	 *  @return Offset north (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x Offset north (m)
	 */
	public EstimatedState setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Offset east (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Offset east (m)
	 */
	public EstimatedState setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Offset down (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Offset down (m)
	 */
	public EstimatedState setZ(double z) {
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
	public EstimatedState setPhi(double phi) {
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
	public EstimatedState setTheta(double theta) {
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
	public EstimatedState setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

	/**
	 *  @return Body-Fixed xx Velocity (m/s) - fp32_t
	 */
	public double getU() {
		return getDouble("u");
	}

	/**
	 *  @param u Body-Fixed xx Velocity (m/s)
	 */
	public EstimatedState setU(double u) {
		values.put("u", u);
		return this;
	}

	/**
	 *  @return Body-Fixed yy Velocity (m/s) - fp32_t
	 */
	public double getV() {
		return getDouble("v");
	}

	/**
	 *  @param v Body-Fixed yy Velocity (m/s)
	 */
	public EstimatedState setV(double v) {
		values.put("v", v);
		return this;
	}

	/**
	 *  @return Body-Fixed zz Velocity (m/s) - fp32_t
	 */
	public double getW() {
		return getDouble("w");
	}

	/**
	 *  @param w Body-Fixed zz Velocity (m/s)
	 */
	public EstimatedState setW(double w) {
		values.put("w", w);
		return this;
	}

	/**
	 *  @return Ground Velocity X (North) (m/s) - fp32_t
	 */
	public double getVx() {
		return getDouble("vx");
	}

	/**
	 *  @param vx Ground Velocity X (North) (m/s)
	 */
	public EstimatedState setVx(double vx) {
		values.put("vx", vx);
		return this;
	}

	/**
	 *  @return Ground Velocity Y (East) (m/s) - fp32_t
	 */
	public double getVy() {
		return getDouble("vy");
	}

	/**
	 *  @param vy Ground Velocity Y (East) (m/s)
	 */
	public EstimatedState setVy(double vy) {
		values.put("vy", vy);
		return this;
	}

	/**
	 *  @return Ground Velocity Z (Down) (m/s) - fp32_t
	 */
	public double getVz() {
		return getDouble("vz");
	}

	/**
	 *  @param vz Ground Velocity Z (Down) (m/s)
	 */
	public EstimatedState setVz(double vz) {
		values.put("vz", vz);
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
	public EstimatedState setP(double p) {
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
	public EstimatedState setQ(double q) {
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
	public EstimatedState setR(double r) {
		values.put("r", r);
		return this;
	}

	/**
	 *  @return Depth (m) - fp32_t
	 */
	public double getDepth() {
		return getDouble("depth");
	}

	/**
	 *  @param depth Depth (m)
	 */
	public EstimatedState setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Altitude (m) - fp32_t
	 */
	public double getAlt() {
		return getDouble("alt");
	}

	/**
	 *  @param alt Altitude (m)
	 */
	public EstimatedState setAlt(double alt) {
		values.put("alt", alt);
		return this;
	}

}
