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
 *  IMC Message Desired Path (406)<br/>
 *  Perform path control.<br/>
 *  The path is specified by two WGS-84 waypoints, respective<br/>
 *  altitude / depth settings, optional loitering at the end point,<br/>
 *  and some control flags.<br/>
 *  The start and end waypoints are defined by the specified end point fields<br/>
 *  ('end_{lat/lon/z}') plus:<br/>
 *  1. the start waypoint fields ('start_{lat|lon|z}') if the<br/>
 *  'START' flag (bit in 'flags' field) is set; or<br/>
 *  2. the end point of the previous path recently tracked; or<br/>
 *  3. the current location is the 'DIRECT' flag is set or if<br/>
 *  the tracker has been idle for some time.<br/>
 *  Altitude and depth control can be performed as follows:<br/>
 *  1. by default, the tracker will just transmit an altitude/depth<br/>
 *  reference value equal to 'end_z' to the appropriate controller;<br/>
 *  2. if the 'NO_Z' flag is set no altitude/depth control will take<br/>
 *  place, hence they can be controlled independently;<br/>
 *  3. if the '3DTRACK' flag is set, 3D-tracking will be done<br/>
 *  (if supported by the active controller).<br/>
 *  Loitering can be specified at the end point with a certain<br/>
 *  radius ('lradius'), duration ('lduration'), and clockwise or<br/>
 *  counter-clockwise direction ('CCLOCKW' flag).<br/>
 */

@SuppressWarnings("unchecked")
public class DesiredPath extends ControlCommand {

	public static final short FL_START = 0x01;
	public static final short FL_DIRECT = 0x02;
	public static final short FL_NO_Z = 0x04;
	public static final short FL_3DTRACK = 0x08;
	public static final short FL_CCLOCKW = 0x10;
	public static final short FL_LOITER_CURR = 0x20;
	public static final short FL_TAKEOFF = 0x40;
	public static final short FL_LAND = 0x80;

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

	public enum SPEED_UNITS {
		METERS_PS(0),
		RPM(1),
		PERCENTAGE(2);

		protected long value;

		public long value() {
			return value;
		}

		SPEED_UNITS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 406;

	public DesiredPath() {
		super(ID_STATIC);
	}

	public DesiredPath(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DesiredPath(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DesiredPath create(Object... values) {
		DesiredPath m = new DesiredPath();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DesiredPath clone(IMCMessage msg) throws Exception {

		DesiredPath m = new DesiredPath();
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

	public DesiredPath(long path_ref, double start_lat, double start_lon, float start_z, START_Z_UNITS start_z_units, double end_lat, double end_lon, float end_z, END_Z_UNITS end_z_units, float speed, SPEED_UNITS speed_units, float lradius, short flags) {
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
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setLradius(lradius);
		setFlags(flags);
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
	public DesiredPath setPathRef(long path_ref) {
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
	public DesiredPath setStartLat(double start_lat) {
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
	public DesiredPath setStartLon(double start_lon) {
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
	public DesiredPath setStartZ(double start_z) {
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
	public DesiredPath setStartZUnits(START_Z_UNITS start_z_units) {
		values.put("start_z_units", start_z_units.value());
		return this;
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (as a String)
	 */
	public DesiredPath setStartZUnitsStr(String start_z_units) {
		setValue("start_z_units", start_z_units);
		return this;
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (integer value)
	 */
	public DesiredPath setStartZUnitsVal(short start_z_units) {
		setValue("start_z_units", start_z_units);
		return this;
	}

	/**
	 *  @return End Point -- WGS84 Latitude (rad) - fp64_t
	 */
	public double getEndLat() {
		return getDouble("end_lat");
	}

	/**
	 *  @param end_lat End Point -- WGS84 Latitude (rad)
	 */
	public DesiredPath setEndLat(double end_lat) {
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
	public DesiredPath setEndLon(double end_lon) {
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
	public DesiredPath setEndZ(double end_z) {
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
	public DesiredPath setEndZUnits(END_Z_UNITS end_z_units) {
		values.put("end_z_units", end_z_units.value());
		return this;
	}

	/**
	 *  @param end_z_units End Point -- Z Units (as a String)
	 */
	public DesiredPath setEndZUnitsStr(String end_z_units) {
		setValue("end_z_units", end_z_units);
		return this;
	}

	/**
	 *  @param end_z_units End Point -- Z Units (integer value)
	 */
	public DesiredPath setEndZUnitsVal(short end_z_units) {
		setValue("end_z_units", end_z_units);
		return this;
	}

	/**
	 *  @return Speed - fp32_t
	 */
	public double getSpeed() {
		return getDouble("speed");
	}

	/**
	 *  @param speed Speed
	 */
	public DesiredPath setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @return Speed Units (enumerated) - uint8_t
	 */
	public SPEED_UNITS getSpeedUnits() {
		try {
			SPEED_UNITS o = SPEED_UNITS.valueOf(getMessageType().getFieldPossibleValues("speed_units").get(getLong("speed_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSpeedUnitsStr() {
		return getString("speed_units");
	}

	public short getSpeedUnitsVal() {
		return (short) getInteger("speed_units");
	}

	/**
	 *  @param speed_units Speed Units (enumerated)
	 */
	public DesiredPath setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public DesiredPath setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public DesiredPath setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
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
	public DesiredPath setLradius(double lradius) {
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
	public DesiredPath setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

}
