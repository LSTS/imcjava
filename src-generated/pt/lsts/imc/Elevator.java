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
 *  IMC Message Elevator Maneuver (462)<br/>
 *  The Elevator maneuver specifies a vehicle to reach a target<br/>
 *  waypoint using a cruise altitude/depth and then ascend or<br/>
 *  descend to another target altitude/depth. The ascent/descent<br/>
 *  slope and radius can also be optionally specified.<br/>
 */

@SuppressWarnings("unchecked")
public class Elevator extends Maneuver {

	public static final short FLG_CURR_POS = 0x01;

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

	public static final int ID_STATIC = 462;

	public Elevator() {
		super(ID_STATIC);
	}

	public Elevator(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Elevator(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Elevator create(Object... values) {
		Elevator m = new Elevator();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Elevator clone(IMCMessage msg) throws Exception {

		Elevator m = new Elevator();
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

	public Elevator(int timeout, short flags, double lat, double lon, float start_z, START_Z_UNITS start_z_units, float end_z, END_Z_UNITS end_z_units, float radius, float speed, SPEED_UNITS speed_units, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setFlags(flags);
		setLat(lat);
		setLon(lon);
		setStartZ(start_z);
		setStartZUnits(start_z_units);
		setEndZ(end_z);
		setEndZUnits(end_z_units);
		setRadius(radius);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		if (custom != null)
			setCustom(custom);
	}

	/**
	 *  @return Timeout (s) - uint16_t
	 */
	public int getTimeout() {
		return getInteger("timeout");
	}

	/**
	 *  @param timeout Timeout (s)
	 */
	public Elevator setTimeout(int timeout) {
		values.put("timeout", timeout);
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
	public Elevator setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @return Latitude WGS-84 (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude WGS-84 (rad)
	 */
	public Elevator setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude WGS-84 (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude WGS-84 (rad)
	 */
	public Elevator setLon(double lon) {
		values.put("lon", lon);
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
	public Elevator setStartZ(double start_z) {
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
	public Elevator setStartZUnits(START_Z_UNITS start_z_units) {
		values.put("start_z_units", start_z_units.value());
		return this;
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (as a String)
	 */
	public Elevator setStartZUnitsStr(String start_z_units) {
		setValue("start_z_units", start_z_units);
		return this;
	}

	/**
	 *  @param start_z_units Start Point -- Z Units (integer value)
	 */
	public Elevator setStartZUnitsVal(short start_z_units) {
		setValue("start_z_units", start_z_units);
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
	public Elevator setEndZ(double end_z) {
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
	public Elevator setEndZUnits(END_Z_UNITS end_z_units) {
		values.put("end_z_units", end_z_units.value());
		return this;
	}

	/**
	 *  @param end_z_units End Point -- Z Units (as a String)
	 */
	public Elevator setEndZUnitsStr(String end_z_units) {
		setValue("end_z_units", end_z_units);
		return this;
	}

	/**
	 *  @param end_z_units End Point -- Z Units (integer value)
	 */
	public Elevator setEndZUnitsVal(short end_z_units) {
		setValue("end_z_units", end_z_units);
		return this;
	}

	/**
	 *  @return Radius (m) - fp32_t
	 */
	public double getRadius() {
		return getDouble("radius");
	}

	/**
	 *  @param radius Radius (m)
	 */
	public Elevator setRadius(double radius) {
		values.put("radius", radius);
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
	public Elevator setSpeed(double speed) {
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
	public Elevator setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public Elevator setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public Elevator setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @return Custom settings for maneuver (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getCustom() {
		return getTupleList("custom");
	}

	/**
	 *  @param custom Custom settings for maneuver (tuplelist)
	 */
	public Elevator setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Elevator setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
