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
 *  IMC Message Rows Coverage (488)<br/>
 *  Rows coverage (i.e: lawn mower type maneuver) but with adaptive cover<br/>
 */

@SuppressWarnings("unchecked")
public class RowsCoverage extends Maneuver {

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

	public static final int ID_STATIC = 488;

	public RowsCoverage() {
		super(ID_STATIC);
	}

	public RowsCoverage(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RowsCoverage(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static RowsCoverage create(Object... values) {
		RowsCoverage m = new RowsCoverage();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static RowsCoverage clone(IMCMessage msg) throws Exception {

		RowsCoverage m = new RowsCoverage();
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

	public RowsCoverage(double lat, double lon, float z, Z_UNITS z_units, float speed, SPEED_UNITS speed_units, double bearing, double cross_angle, float width, float length, short coff, float angAperture, int range, short overlap, short flags, String custom) {
		super(ID_STATIC);
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
		setCoff(coff);
		setAngAperture(angAperture);
		setRange(range);
		setOverlap(overlap);
		setFlags(flags);
		if (custom != null)
			setCustom(custom);
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
	public RowsCoverage setLat(double lat) {
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
	public RowsCoverage setLon(double lon) {
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
	public RowsCoverage setZ(double z) {
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
	public RowsCoverage setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public RowsCoverage setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public RowsCoverage setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
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
	public RowsCoverage setSpeed(double speed) {
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
	public RowsCoverage setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public RowsCoverage setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public RowsCoverage setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
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
	public RowsCoverage setBearing(double bearing) {
		values.put("bearing", bearing);
		return this;
	}

	/**
	 *  @return Cross Angle (rad) - fp64_t
	 */
	public double getCrossAngle() {
		return getDouble("cross_angle");
	}

	/**
	 *  @param cross_angle Cross Angle (rad)
	 */
	public RowsCoverage setCrossAngle(double cross_angle) {
		values.put("cross_angle", cross_angle);
		return this;
	}

	/**
	 *  @return Width (m) - fp32_t
	 */
	public double getWidth() {
		return getDouble("width");
	}

	/**
	 *  @param width Width (m)
	 */
	public RowsCoverage setWidth(double width) {
		values.put("width", width);
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
	public RowsCoverage setLength(double length) {
		values.put("length", length);
		return this;
	}

	/**
	 *  @return Curve Offset (m) - uint8_t
	 */
	public short getCoff() {
		return (short) getInteger("coff");
	}

	/**
	 *  @param coff Curve Offset (m)
	 */
	public RowsCoverage setCoff(short coff) {
		values.put("coff", coff);
		return this;
	}

	/**
	 *  @return Angular Aperture (rad) - fp32_t
	 */
	public double getAngAperture() {
		return getDouble("angAperture");
	}

	/**
	 *  @param angAperture Angular Aperture (rad)
	 */
	public RowsCoverage setAngAperture(double angAperture) {
		values.put("angAperture", angAperture);
		return this;
	}

	/**
	 *  @return Range (m) - uint16_t
	 */
	public int getRange() {
		return getInteger("range");
	}

	/**
	 *  @param range Range (m)
	 */
	public RowsCoverage setRange(int range) {
		values.put("range", range);
		return this;
	}

	/**
	 *  @return Overlap (%) - uint8_t
	 */
	public short getOverlap() {
		return (short) getInteger("overlap");
	}

	/**
	 *  @param overlap Overlap (%)
	 */
	public RowsCoverage setOverlap(short overlap) {
		values.put("overlap", overlap);
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
	public RowsCoverage setFlags(short flags) {
		values.put("flags", flags);
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
	public RowsCoverage setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public RowsCoverage setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
