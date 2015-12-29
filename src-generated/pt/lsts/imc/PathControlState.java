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
 *  IMC Message Path Control State (410)<br/>
 *  Path control state issued by Path Controller.<br/>
 */

public class PathControlState extends IMCMessage {

	public static final short FL_NEAR = 0x01;
	public static final short FL_LOITERING = 0x02;
	public static final short FL_NO_Z = 0x04;
	public static final short FL_3DTRACK = 0x08;
	public static final short FL_CCLOCKW = 0x10;

	public enum START_Z_UNITS {
		NONE(0),
		DEPTH(1),
		ALTITUDE(2),
		HEIGHT(3);

		protected long value;

		public long value() {
			return value;
		}

		START_Z_UNITS(long value) {
			this.value = value;
		}
	}

	public enum END_Z_UNITS {
		NONE(0),
		DEPTH(1),
		ALTITUDE(2),
		HEIGHT(3);

		protected long value;

		public long value() {
			return value;
		}

		END_Z_UNITS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 410;

	public PathControlState() {
		super(ID_STATIC);
	}

	public PathControlState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PathControlState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PathControlState create(Object... values) {
		PathControlState m = new PathControlState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PathControlState clone(IMCMessage msg) throws Exception {

		PathControlState m = new PathControlState();
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

	public PathControlState(long path_ref, double start_lat, double start_lon, float start_z, START_Z_UNITS start_z_units, double end_lat, double end_lon, float end_z, END_Z_UNITS end_z_units, float lradius, short flags, float x, float y, float z, float vx, float vy, float vz, float course_error, int eta) {
		super(ID_STATIC);
		setPathRef(path_ref);
		setStartLat(start_lat);
		setStartLon(start_lon);
		setStartZ(start_z);
		setStartZUnits(start_z_units);
		setEndLat(end_lat);
		setEndLon(end_lon);
		setEndZ(end_z);
		setEndZUnits(end_z_units);
		setLradius(lradius);
		setFlags(flags);
		setX(x);
		setY(y);
		setZ(z);
		setVx(vx);
		setVy(vy);
		setVz(vz);
		setCourseError(course_error);
		setEta(eta);
	}

	/**
	 *  @return Path Reference - uint32_t
	 */
	public long getPathRef() {
		return getLong("path_ref");
	}

	/**
	 *  @param path_ref Path Reference
	 */
	public PathControlState setPathRef(long path_ref) {
		values.put("path_ref", path_ref);
		return this;
	}

	/**
	 *  @return Start Point -- Latitude WGS-84 (rad) - fp64_t
	 */
	public double getStartLat() {
		return getDouble("start_lat");
	}

	/**
	 *  @param start_lat Start Point -- Latitude WGS-84 (rad)
	 */
	public PathControlState setStartLat(double start_lat) {
		values.put("start_lat", start_lat);
		return this;
	}

	/**
	 *  @return Start Point -- WGS-84 Longitude (rad) - fp64_t
	 */
	public double getStartLon() {
		return getDouble("start_lon");
	}

	/**
	 *  @param start_lon Start Point -- WGS-84 Longitude (rad)
	 */
	public PathControlState setStartLon(double start_lon) {
		values.put("start_lon", start_lon);
		return this;
	}

	/**
	 *  @return Start Point -- Z Reference (m) - fp32_t
	 */
	public double getStartZ() {
		return getDouble("start_z");
	}

	/**
	 *  @param start_z Start Point -- Z Reference (m)
	 */
	public PathControlState setStartZ(double start_z) {
		values.put("start_z", start_z);
		return this;
	}

	/**
	 *  @return Start Point -- Z Units (enumerated) - uint8_t
	 */
	public START_Z_UNITS getStartZUnits() {
		try {
			START_Z_UNITS o = START_Z_UNITS.valueOf(getMessageType().getFieldPossibleValues("start_z_units").get(getLong("start_z_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStartZUnitsStr() {
		return getString("start_z_units");
	}

	public short getStartZUnitsVal() {
		return (short) getInteger("start_z_units");
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (enumerated)
	 */
	public PathControlState setStartZUnits(START_Z_UNITS start_z_units) {
		values.put("start_z_units", start_z_units.value());
		return this;
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (as a String)
	 */
	public PathControlState setStartZUnitsStr(String start_z_units) {
		setValue("start_z_units", start_z_units);
		return this;
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (integer value)
	 */
	public PathControlState setStartZUnitsVal(short start_z_units) {
		setValue("start_z_units", start_z_units);
		return this;
	}

	/**
	 *  @return End Point -- Latitude WGS-84 (rad) - fp64_t
	 */
	public double getEndLat() {
		return getDouble("end_lat");
	}

	/**
	 *  @param end_lat End Point -- Latitude WGS-84 (rad)
	 */
	public PathControlState setEndLat(double end_lat) {
		values.put("end_lat", end_lat);
		return this;
	}

	/**
	 *  @return End Point -- WGS-84 Longitude (rad) - fp64_t
	 */
	public double getEndLon() {
		return getDouble("end_lon");
	}

	/**
	 *  @param end_lon End Point -- WGS-84 Longitude (rad)
	 */
	public PathControlState setEndLon(double end_lon) {
		values.put("end_lon", end_lon);
		return this;
	}

	/**
	 *  @return End Point -- Z Reference (m) - fp32_t
	 */
	public double getEndZ() {
		return getDouble("end_z");
	}

	/**
	 *  @param end_z End Point -- Z Reference (m)
	 */
	public PathControlState setEndZ(double end_z) {
		values.put("end_z", end_z);
		return this;
	}

	/**
	 *  @return End Point -- Z Units (enumerated) - uint8_t
	 */
	public END_Z_UNITS getEndZUnits() {
		try {
			END_Z_UNITS o = END_Z_UNITS.valueOf(getMessageType().getFieldPossibleValues("end_z_units").get(getLong("end_z_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getEndZUnitsStr() {
		return getString("end_z_units");
	}

	public short getEndZUnitsVal() {
		return (short) getInteger("end_z_units");
	}

	/**
	 *  @param end_z_units End Point -- Z Units (enumerated)
	 */
	public PathControlState setEndZUnits(END_Z_UNITS end_z_units) {
		values.put("end_z_units", end_z_units.value());
		return this;
	}

	/**
	 *  @param end_z_units End Point -- Z Units (as a String)
	 */
	public PathControlState setEndZUnitsStr(String end_z_units) {
		setValue("end_z_units", end_z_units);
		return this;
	}

	/**
	 *  @param end_z_units End Point -- Z Units (integer value)
	 */
	public PathControlState setEndZUnitsVal(short end_z_units) {
		setValue("end_z_units", end_z_units);
		return this;
	}

	/**
	 *  @return Loiter -- Radius (m) - fp32_t
	 */
	public double getLradius() {
		return getDouble("lradius");
	}

	/**
	 *  @param lradius Loiter -- Radius (m)
	 */
	public PathControlState setLradius(double lradius) {
		values.put("lradius", lradius);
		return this;
	}

	/**
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public PathControlState setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @return Along Track Position (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x Along Track Position (m)
	 */
	public PathControlState setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Cross Track Position (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Cross Track Position (m)
	 */
	public PathControlState setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Vertical Track Position (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Vertical Track Position (m)
	 */
	public PathControlState setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Along Track Velocity (m/s) - fp32_t
	 */
	public double getVx() {
		return getDouble("vx");
	}

	/**
	 *  @param vx Along Track Velocity (m/s)
	 */
	public PathControlState setVx(double vx) {
		values.put("vx", vx);
		return this;
	}

	/**
	 *  @return Cross Track Velocity (m/s) - fp32_t
	 */
	public double getVy() {
		return getDouble("vy");
	}

	/**
	 *  @param vy Cross Track Velocity (m/s)
	 */
	public PathControlState setVy(double vy) {
		values.put("vy", vy);
		return this;
	}

	/**
	 *  @return Vertical Track Velocity (m/s) - fp32_t
	 */
	public double getVz() {
		return getDouble("vz");
	}

	/**
	 *  @param vz Vertical Track Velocity (m/s)
	 */
	public PathControlState setVz(double vz) {
		values.put("vz", vz);
		return this;
	}

	/**
	 *  @return Course Error (rad) - fp32_t
	 */
	public double getCourseError() {
		return getDouble("course_error");
	}

	/**
	 *  @param course_error Course Error (rad)
	 */
	public PathControlState setCourseError(double course_error) {
		values.put("course_error", course_error);
		return this;
	}

	/**
	 *  @return Estimated Time to Arrival (ETA) (s) - uint16_t
	 */
	public int getEta() {
		return getInteger("eta");
	}

	/**
	 *  @param eta Estimated Time to Arrival (ETA) (s)
	 */
	public PathControlState setEta(int eta) {
		values.put("eta", eta);
		return this;
	}

}
