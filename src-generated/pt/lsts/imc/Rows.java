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
 *  IMC Message Rows Maneuver (456)<br/>
 *  Rows maneuver (i.e: lawn mower type maneuver)<br/>
 */

public class Rows extends Maneuver {

	public static final int ID_STATIC = 456;

	public static final short FLG_SQUARE_CURVE = 0x01;
	public static final short FLG_CURVE_RIGHT = 0x02;

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

	public Rows() {
		super(ID_STATIC);
	}

	public Rows(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Rows(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Rows create(Object... values) {
		Rows m = new Rows();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Rows clone(IMCMessage msg) throws Exception {

		Rows m = new Rows();
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

	public Rows(int timeout, double lat, double lon, float z, Z_UNITS z_units, float speed, SPEED_UNITS speed_units, double bearing, double cross_angle, float width, float length, float hstep, short coff, short alternation, short flags, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setBearing(bearing);
		setCrossAngle(cross_angle);
		setWidth(width);
		setLength(length);
		setHstep(hstep);
		setCoff(coff);
		setAlternation(alternation);
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
	 *  @return Bearing (rad) - fp64_t
	 */
	public double getBearing() {
		return getDouble("bearing");
	}

	/**
	 *  @return Cross Angle (rad) - fp64_t
	 */
	public double getCrossAngle() {
		return getDouble("cross_angle");
	}

	/**
	 *  @return Width (m) - fp32_t
	 */
	public double getWidth() {
		return getDouble("width");
	}

	/**
	 *  @return Length (m) - fp32_t
	 */
	public double getLength() {
		return getDouble("length");
	}

	/**
	 *  @return Horizontal Step (m) - fp32_t
	 */
	public double getHstep() {
		return getDouble("hstep");
	}

	/**
	 *  @return Curve Offset (m) - uint8_t
	 */
	public short getCoff() {
		return (short) getInteger("coff");
	}

	/**
	 *  @return Alternation Parameter (%) - uint8_t
	 */
	public short getAlternation() {
		return (short) getInteger("alternation");
	}

	/**
	 *  Maneuver flags.<br/>
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
	public Rows setTimeout(int timeout) {
		values.put("timeout", timeout);
		return this;
	}

	/**
	 *  @param lat Latitude WGS-84 (rad)
	 */
	public Rows setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @param lon Longitude WGS-84 (rad)
	 */
	public Rows setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @param z Z Reference (m)
	 */
	public Rows setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @param z_units Z Units (enumerated)
	 */
	public Rows setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public Rows setZUnits(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public Rows setZUnits(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param speed Speed
	 */
	public Rows setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (enumerated)
	 */
	public Rows setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public Rows setSpeedUnits(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public Rows setSpeedUnits(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param bearing Bearing (rad)
	 */
	public Rows setBearing(double bearing) {
		values.put("bearing", bearing);
		return this;
	}

	/**
	 *  @param cross_angle Cross Angle (rad)
	 */
	public Rows setCrossAngle(double cross_angle) {
		values.put("cross_angle", cross_angle);
		return this;
	}

	/**
	 *  @param width Width (m)
	 */
	public Rows setWidth(double width) {
		values.put("width", width);
		return this;
	}

	/**
	 *  @param length Length (m)
	 */
	public Rows setLength(double length) {
		values.put("length", length);
		return this;
	}

	/**
	 *  @param hstep Horizontal Step (m)
	 */
	public Rows setHstep(double hstep) {
		values.put("hstep", hstep);
		return this;
	}

	/**
	 *  @param coff Curve Offset (m)
	 */
	public Rows setCoff(short coff) {
		values.put("coff", coff);
		return this;
	}

	/**
	 *  @param alternation Alternation Parameter (%)
	 */
	public Rows setAlternation(short alternation) {
		values.put("alternation", alternation);
		return this;
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public Rows setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @param custom Custom settings for maneuver (tuplelist)
	 */
	public Rows setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Rows setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
