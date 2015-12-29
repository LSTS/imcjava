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
 *  IMC Message Leader State (563)<br/>
 *  This message defines the formation leader state.<br/>
 *  LeaderState is a complete description of the leader state<br/>
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

public class LeaderState extends IMCMessage {

	public enum OP {
		REQUEST(0),
		SET(1),
		REPORT(2);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 563;

	public LeaderState() {
		super(ID_STATIC);
	}

	public LeaderState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LeaderState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LeaderState create(Object... values) {
		LeaderState m = new LeaderState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LeaderState clone(IMCMessage msg) throws Exception {

		LeaderState m = new LeaderState();
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

	public LeaderState(String group_name, OP op, double lat, double lon, float height, float x, float y, float z, float phi, float theta, float psi, float vx, float vy, float vz, float p, float q, float r, float svx, float svy, float svz) {
		super(ID_STATIC);
		if (group_name != null)
			setGroupName(group_name);
		setOp(op);
		setLat(lat);
		setLon(lon);
		setHeight(height);
		setX(x);
		setY(y);
		setZ(z);
		setPhi(phi);
		setTheta(theta);
		setPsi(psi);
		setVx(vx);
		setVy(vy);
		setVz(vz);
		setP(p);
		setQ(q);
		setR(r);
		setSvx(svx);
		setSvy(svy);
		setSvz(svz);
	}

	/**
	 *  @return Group Name - plaintext
	 */
	public String getGroupName() {
		return getString("group_name");
	}

	/**
	 *  @param group_name Group Name
	 */
	public LeaderState setGroupName(String group_name) {
		values.put("group_name", group_name);
		return this;
	}

	/**
	 *  @return Action on the leader state (enumerated) - uint8_t
	 */
	public OP getOp() {
		try {
			OP o = OP.valueOf(getMessageType().getFieldPossibleValues("op").get(getLong("op")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getOpStr() {
		return getString("op");
	}

	public short getOpVal() {
		return (short) getInteger("op");
	}

	/**
	 *  @param op Action on the leader state (enumerated)
	 */
	public LeaderState setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Action on the leader state (as a String)
	 */
	public LeaderState setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Action on the leader state (integer value)
	 */
	public LeaderState setOpVal(short op) {
		setValue("op", op);
		return this;
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
	public LeaderState setLat(double lat) {
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
	public LeaderState setLon(double lon) {
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
	public LeaderState setHeight(double height) {
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
	public LeaderState setX(double x) {
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
	public LeaderState setY(double y) {
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
	public LeaderState setZ(double z) {
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
	public LeaderState setPhi(double phi) {
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
	public LeaderState setTheta(double theta) {
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
	public LeaderState setPsi(double psi) {
		values.put("psi", psi);
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
	public LeaderState setVx(double vx) {
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
	public LeaderState setVy(double vy) {
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
	public LeaderState setVz(double vz) {
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
	public LeaderState setP(double p) {
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
	public LeaderState setQ(double q) {
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
	public LeaderState setR(double r) {
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
	public LeaderState setSvx(double svx) {
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
	public LeaderState setSvy(double svy) {
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
	public LeaderState setSvz(double svz) {
		values.put("svz", svz);
		return this;
	}

}
