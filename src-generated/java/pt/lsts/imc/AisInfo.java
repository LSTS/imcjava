/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message AIS Info (912)<br/>
 *  Message containing static or dynamic AIS data received onboard the vehicle.<br/>
 */

public class AisInfo extends IMCMessage {

	public static final int ID_STATIC = 912;

	public AisInfo() {
		super(ID_STATIC);
	}

	public AisInfo(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AisInfo(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AisInfo create(Object... values) {
		AisInfo m = new AisInfo();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AisInfo clone(IMCMessage msg) throws Exception {

		AisInfo m = new AisInfo();
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

	public AisInfo(String msg_type, String sensor_class, String mmsi, String callsign, String name, short nav_status, short type_and_cargo, double lat, double lon, float course, float speed, float dist, float a, float b, float c, float d, float draught) {
		super(ID_STATIC);
		if (msg_type != null)
			setMsgType(msg_type);
		if (sensor_class != null)
			setSensorClass(sensor_class);
		if (mmsi != null)
			setMmsi(mmsi);
		if (callsign != null)
			setCallsign(callsign);
		if (name != null)
			setName(name);
		setNavStatus(nav_status);
		setTypeAndCargo(type_and_cargo);
		setLat(lat);
		setLon(lon);
		setCourse(course);
		setSpeed(speed);
		setDist(dist);
		setA(a);
		setB(b);
		setC(c);
		setD(d);
		setDraught(draught);
	}

	/**
	 *  @return Message Type - plaintext
	 */
	public String getMsgType() {
		return getString("msg_type");
	}

	/**
	 *  @param msg_type Message Type
	 */
	public AisInfo setMsgType(String msg_type) {
		values.put("msg_type", msg_type);
		return this;
	}

	/**
	 *  @return Class - plaintext
	 */
	public String getSensorClass() {
		return getString("sensor_class");
	}

	/**
	 *  @param sensor_class Class
	 */
	public AisInfo setSensorClass(String sensor_class) {
		values.put("sensor_class", sensor_class);
		return this;
	}

	/**
	 *  @return MMSI - plaintext
	 */
	public String getMmsi() {
		return getString("mmsi");
	}

	/**
	 *  @param mmsi MMSI
	 */
	public AisInfo setMmsi(String mmsi) {
		values.put("mmsi", mmsi);
		return this;
	}

	/**
	 *  @return Callsign - plaintext
	 */
	public String getCallsign() {
		return getString("callsign");
	}

	/**
	 *  @param callsign Callsign
	 */
	public AisInfo setCallsign(String callsign) {
		values.put("callsign", callsign);
		return this;
	}

	/**
	 *  @return Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Name
	 */
	public AisInfo setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Navigation Status - uint8_t
	 */
	public short getNavStatus() {
		return (short) getInteger("nav_status");
	}

	/**
	 *  @param nav_status Navigation Status
	 */
	public AisInfo setNavStatus(short nav_status) {
		values.put("nav_status", nav_status);
		return this;
	}

	/**
	 *  @return Type and Cargo - uint8_t
	 */
	public short getTypeAndCargo() {
		return (short) getInteger("type_and_cargo");
	}

	/**
	 *  @param type_and_cargo Type and Cargo
	 */
	public AisInfo setTypeAndCargo(short type_and_cargo) {
		values.put("type_and_cargo", type_and_cargo);
		return this;
	}

	/**
	 *  @return Latitude (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (rad)
	 */
	public AisInfo setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (rad)
	 */
	public AisInfo setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Course (°) - fp32_t
	 */
	public double getCourse() {
		return getDouble("course");
	}

	/**
	 *  @param course Course (°)
	 */
	public AisInfo setCourse(double course) {
		values.put("course", course);
		return this;
	}

	/**
	 *  @return Speed (kn) - fp32_t
	 */
	public double getSpeed() {
		return getDouble("speed");
	}

	/**
	 *  @param speed Speed (kn)
	 */
	public AisInfo setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @return Distance (m) - fp32_t
	 */
	public double getDist() {
		return getDouble("dist");
	}

	/**
	 *  @param dist Distance (m)
	 */
	public AisInfo setDist(double dist) {
		values.put("dist", dist);
		return this;
	}

	/**
	 *  @return Size A Length (m) - fp32_t
	 */
	public double getA() {
		return getDouble("a");
	}

	/**
	 *  @param a Size A Length (m)
	 */
	public AisInfo setA(double a) {
		values.put("a", a);
		return this;
	}

	/**
	 *  @return Size B Length (m) - fp32_t
	 */
	public double getB() {
		return getDouble("b");
	}

	/**
	 *  @param b Size B Length (m)
	 */
	public AisInfo setB(double b) {
		values.put("b", b);
		return this;
	}

	/**
	 *  @return Size C Width (m) - fp32_t
	 */
	public double getC() {
		return getDouble("c");
	}

	/**
	 *  @param c Size C Width (m)
	 */
	public AisInfo setC(double c) {
		values.put("c", c);
		return this;
	}

	/**
	 *  @return Size D Width (m) - fp32_t
	 */
	public double getD() {
		return getDouble("d");
	}

	/**
	 *  @param d Size D Width (m)
	 */
	public AisInfo setD(double d) {
		values.put("d", d);
		return this;
	}

	/**
	 *  @return Draught (m) - fp32_t
	 */
	public double getDraught() {
		return getDouble("draught");
	}

	/**
	 *  @param draught Draught (m)
	 */
	public AisInfo setDraught(double draught) {
		values.put("draught", draught);
		return this;
	}

}
