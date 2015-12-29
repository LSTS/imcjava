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
 *  IMC Message Compass Calibration Maneuver (475)<br/>
 *  This maneuver is a mix between the Loiter maneuver and the YoYo maneuver.<br/>
 *  The vehicle cirlcles around a specific waypoint with a variable Z<br/>
 *  reference between a minimum and maximum value.<br/>
 */

@SuppressWarnings("unchecked")
public class CompassCalibration extends Maneuver {

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

	public static final int ID_STATIC = 475;

	public CompassCalibration() {
		super(ID_STATIC);
	}

	public CompassCalibration(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CompassCalibration(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CompassCalibration create(Object... values) {
		CompassCalibration m = new CompassCalibration();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CompassCalibration clone(IMCMessage msg) throws Exception {

		CompassCalibration m = new CompassCalibration();
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

	public CompassCalibration(int timeout, double lat, double lon, float z, Z_UNITS z_units, float pitch, float amplitude, int duration, float speed, SPEED_UNITS speed_units, float radius, DIRECTION direction, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setPitch(pitch);
		setAmplitude(amplitude);
		setDuration(duration);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setRadius(radius);
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
	public CompassCalibration setTimeout(int timeout) {
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
	public CompassCalibration setLat(double lat) {
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
	public CompassCalibration setLon(double lon) {
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
	public CompassCalibration setZ(double z) {
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
	public CompassCalibration setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public CompassCalibration setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public CompassCalibration setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @return Pitch (rad) - fp32_t
	 */
	public double getPitch() {
		return getDouble("pitch");
	}

	/**
	 *  @param pitch Pitch (rad)
	 */
	public CompassCalibration setPitch(double pitch) {
		values.put("pitch", pitch);
		return this;
	}

	/**
	 *  @return Amplitude (m) - fp32_t
	 */
	public double getAmplitude() {
		return getDouble("amplitude");
	}

	/**
	 *  @param amplitude Amplitude (m)
	 */
	public CompassCalibration setAmplitude(double amplitude) {
		values.put("amplitude", amplitude);
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
	public CompassCalibration setDuration(int duration) {
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
	public CompassCalibration setSpeed(double speed) {
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
	public CompassCalibration setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public CompassCalibration setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public CompassCalibration setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
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
	public CompassCalibration setRadius(double radius) {
		values.put("radius", radius);
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
	public CompassCalibration setDirection(DIRECTION direction) {
		values.put("direction", direction.value());
		return this;
	}

	/**
	 *  @param direction Direction (as a String)
	 */
	public CompassCalibration setDirectionStr(String direction) {
		setValue("direction", direction);
		return this;
	}

	/**
	 *  @param direction Direction (integer value)
	 */
	public CompassCalibration setDirectionVal(short direction) {
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
	public CompassCalibration setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public CompassCalibration setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
