/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2014, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message PopUp Maneuver (451)<br/>
 *  The Pop Up maneuver makes the vehicle come to the surface at a<br/>
 *  specific waypoint. This maneuver is restricted to underwater vehicles.<br/>
 */

public class PopUp extends Maneuver {

	public static final int ID_STATIC = 451;

	public static final short FLG_CURR_POS = 0x01;
	public static final short FLG_WAIT_AT_SURFACE = 0x02;
	public static final short FLG_STATION_KEEP = 0x04;

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

	public PopUp() {
		super(ID_STATIC);
	}

	public PopUp(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PopUp(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PopUp create(Object... values) {
		PopUp m = new PopUp();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PopUp clone(IMCMessage msg) throws Exception {

		PopUp m = new PopUp();
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

	public PopUp(long plan_ref, String id, String memento, int timeout, double lat, double lon, float z, Z_UNITS z_units, float speed, SPEED_UNITS speed_units, int duration, float radius, short flags, String custom) {
		super(ID_STATIC);
		setPlanRef(plan_ref);
		if (id != null)
			setId(id);
		if (memento != null)
			setMemento(memento);
		setTimeout(timeout);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setDuration(duration);
		setRadius(radius);
		setFlags(flags);
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
	 *  @return Latitude WGS-84 (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @return Longitude WGS-84 (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @return Z Reference (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  Units of the z reference.<br/>
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

	/**
	 *  @return Speed - fp32_t
	 */
	public double getSpeed() {
		return getDouble("speed");
	}

	/**
	 *  Speed units.<br/>
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

	/**
	 *  @return Duration (s) - uint16_t
	 */
	public int getDuration() {
		return getInteger("duration");
	}

	/**
	 *  @return Radius (m) - fp32_t
	 */
	public double getRadius() {
		return getDouble("radius");
	}

	/**
	 *  Flags of the maneuver.<br/>
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @return Custom settings for maneuver (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getCustom() {
		return getTupleList("custom");
	}

	/**
	 *  @param timeout Timeout (s)
	 */
	public PopUp setTimeout(int timeout) {
		values.put("timeout", timeout);
		return this;
	}

	/**
	 *  @param lat Latitude WGS-84 (rad)
	 */
	public PopUp setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @param lon Longitude WGS-84 (rad)
	 */
	public PopUp setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @param z Z Reference (m)
	 */
	public PopUp setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @param z_units Z Units (enumerated)
	 */
	public PopUp setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public PopUp setZUnits(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public PopUp setZUnits(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param speed Speed
	 */
	public PopUp setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (enumerated)
	 */
	public PopUp setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public PopUp setSpeedUnits(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public PopUp setSpeedUnits(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param duration Duration (s)
	 */
	public PopUp setDuration(int duration) {
		values.put("duration", duration);
		return this;
	}

	/**
	 *  @param radius Radius (m)
	 */
	public PopUp setRadius(double radius) {
		values.put("radius", radius);
		return this;
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public PopUp setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @param custom Custom settings for maneuver (tuplelist)
	 */
	public PopUp setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public PopUp setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
