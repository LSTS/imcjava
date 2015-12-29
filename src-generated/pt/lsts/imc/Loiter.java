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
 *  IMC Message Loiter Maneuver (453)<br/>
 *  The Loiter maneuver makes the vehicle circle around a specific<br/>
 *  waypoint with fixed depth reference.<br/>
 */

@SuppressWarnings("unchecked")
public class Loiter extends Maneuver {

	public enum Z_UNITS {
		NONE(0),
		DEPTH(1),
		ALTITUDE(2),
		HEIGHT(3);

		protected long value;

		public long value() {
			return value;
		}

		Z_UNITS(long value) {
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

	public enum TYPE {
		DEFAULT(0),
		CIRCULAR(1),
		RACETRACK(2),
		EIGHT(3),
		HOVER(4);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum DIRECTION {
		VDEP(0),
		CLOCKW(1),
		CCLOCKW(2),
		IWINDCURR(3);

		protected long value;

		public long value() {
			return value;
		}

		DIRECTION(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 453;

	public Loiter() {
		super(ID_STATIC);
	}

	public Loiter(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Loiter(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Loiter create(Object... values) {
		Loiter m = new Loiter();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Loiter clone(IMCMessage msg) throws Exception {

		Loiter m = new Loiter();
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

	public Loiter(int timeout, double lat, double lon, float z, Z_UNITS z_units, int duration, float speed, SPEED_UNITS speed_units, TYPE type, float radius, float length, double bearing, DIRECTION direction, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setDuration(duration);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setType(type);
		setRadius(radius);
		setLength(length);
		setBearing(bearing);
		setDirection(direction);
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
	public Loiter setTimeout(int timeout) {
		values.put("timeout", timeout);
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
	public Loiter setLat(double lat) {
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
	public Loiter setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Z Reference (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z Reference (m)
	 */
	public Loiter setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Z Units (enumerated) - uint8_t
	 */
	public Z_UNITS getZUnits() {
		try {
			Z_UNITS o = Z_UNITS.valueOf(getMessageType().getFieldPossibleValues("z_units").get(getLong("z_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getZUnitsStr() {
		return getString("z_units");
	}

	public short getZUnitsVal() {
		return (short) getInteger("z_units");
	}

	/**
	 *  @param z_units Z Units (enumerated)
	 */
	public Loiter setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public Loiter setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public Loiter setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @return Duration (s) - uint16_t
	 */
	public int getDuration() {
		return getInteger("duration");
	}

	/**
	 *  @param duration Duration (s)
	 */
	public Loiter setDuration(int duration) {
		values.put("duration", duration);
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
	public Loiter setSpeed(double speed) {
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
	public Loiter setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public Loiter setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public Loiter setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @return Loiter Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Loiter Type (enumerated)
	 */
	public Loiter setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Loiter Type (as a String)
	 */
	public Loiter setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Loiter Type (integer value)
	 */
	public Loiter setTypeVal(short type) {
		setValue("type", type);
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
	public Loiter setRadius(double radius) {
		values.put("radius", radius);
		return this;
	}

	/**
	 *  @return Length (m) - fp32_t
	 */
	public double getLength() {
		return getDouble("length");
	}

	/**
	 *  @param length Length (m)
	 */
	public Loiter setLength(double length) {
		values.put("length", length);
		return this;
	}

	/**
	 *  @return Bearing (rad) - fp64_t
	 */
	public double getBearing() {
		return getDouble("bearing");
	}

	/**
	 *  @param bearing Bearing (rad)
	 */
	public Loiter setBearing(double bearing) {
		values.put("bearing", bearing);
		return this;
	}

	/**
	 *  @return Direction (enumerated) - uint8_t
	 */
	public DIRECTION getDirection() {
		try {
			DIRECTION o = DIRECTION.valueOf(getMessageType().getFieldPossibleValues("direction").get(getLong("direction")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getDirectionStr() {
		return getString("direction");
	}

	public short getDirectionVal() {
		return (short) getInteger("direction");
	}

	/**
	 *  @param direction Direction (enumerated)
	 */
	public Loiter setDirection(DIRECTION direction) {
		values.put("direction", direction.value());
		return this;
	}

	/**
	 *  @param direction Direction (as a String)
	 */
	public Loiter setDirectionStr(String direction) {
		setValue("direction", direction);
		return this;
	}

	/**
	 *  @param direction Direction (integer value)
	 */
	public Loiter setDirectionVal(short direction) {
		setValue("direction", direction);
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
	public Loiter setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Loiter setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
