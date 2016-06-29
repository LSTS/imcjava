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
 *  IMC Message Sample Maneuver (489)<br/>
 *  A "Sample" is a maneuver specifying a movement of the vehicle to a<br/>
 *  target waypoint. The waypoint is described by the WGS-84<br/>
 *  waypoint coordinate and target Z reference.<br/>
 *  Mandatory parameters defined for a "Goto" are<br/>
 *  timeout, speed and speed units.<br/>
 */

@SuppressWarnings("unchecked")
public class Sample extends Maneuver {

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

	public enum SYRINGE0 {
		FALSE(0),
		TRUE(1);

		protected long value;

		public long value() {
			return value;
		}

		SYRINGE0(long value) {
			this.value = value;
		}
	}

	public enum SYRINGE1 {
		FALSE(0),
		TRUE(1);

		protected long value;

		public long value() {
			return value;
		}

		SYRINGE1(long value) {
			this.value = value;
		}
	}

	public enum SYRINGE2 {
		FALSE(0),
		TRUE(1);

		protected long value;

		public long value() {
			return value;
		}

		SYRINGE2(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 489;

	public Sample() {
		super(ID_STATIC);
	}

	public Sample(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Sample(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Sample create(Object... values) {
		Sample m = new Sample();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Sample clone(IMCMessage msg) throws Exception {

		Sample m = new Sample();
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

	public Sample(int timeout, double lat, double lon, float z, Z_UNITS z_units, float speed, SPEED_UNITS speed_units, SYRINGE0 syringe0, SYRINGE1 syringe1, SYRINGE2 syringe2, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setSyringe0(syringe0);
		setSyringe1(syringe1);
		setSyringe2(syringe2);
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
	public Sample setTimeout(int timeout) {
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
	public Sample setLat(double lat) {
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
	public Sample setLon(double lon) {
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
	public Sample setZ(double z) {
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
	public Sample setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public Sample setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public Sample setZUnitsVal(short z_units) {
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
	public Sample setSpeed(double speed) {
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
	public Sample setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public Sample setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public Sample setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @return Syringe0 (enumerated) - uint8_t
	 */
	public SYRINGE0 getSyringe0() {
		try {
			SYRINGE0 o = SYRINGE0.valueOf(getMessageType().getFieldPossibleValues("syringe0").get(getLong("syringe0")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSyringe0Str() {
		return getString("syringe0");
	}

	public short getSyringe0Val() {
		return (short) getInteger("syringe0");
	}

	/**
	 *  @param syringe0 Syringe0 (enumerated)
	 */
	public Sample setSyringe0(SYRINGE0 syringe0) {
		values.put("syringe0", syringe0.value());
		return this;
	}

	/**
	 *  @param syringe0 Syringe0 (as a String)
	 */
	public Sample setSyringe0Str(String syringe0) {
		setValue("syringe0", syringe0);
		return this;
	}

	/**
	 *  @param syringe0 Syringe0 (integer value)
	 */
	public Sample setSyringe0Val(short syringe0) {
		setValue("syringe0", syringe0);
		return this;
	}

	/**
	 *  @return Syringe1 (enumerated) - uint8_t
	 */
	public SYRINGE1 getSyringe1() {
		try {
			SYRINGE1 o = SYRINGE1.valueOf(getMessageType().getFieldPossibleValues("syringe1").get(getLong("syringe1")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSyringe1Str() {
		return getString("syringe1");
	}

	public short getSyringe1Val() {
		return (short) getInteger("syringe1");
	}

	/**
	 *  @param syringe1 Syringe1 (enumerated)
	 */
	public Sample setSyringe1(SYRINGE1 syringe1) {
		values.put("syringe1", syringe1.value());
		return this;
	}

	/**
	 *  @param syringe1 Syringe1 (as a String)
	 */
	public Sample setSyringe1Str(String syringe1) {
		setValue("syringe1", syringe1);
		return this;
	}

	/**
	 *  @param syringe1 Syringe1 (integer value)
	 */
	public Sample setSyringe1Val(short syringe1) {
		setValue("syringe1", syringe1);
		return this;
	}

	/**
	 *  @return Syringe2 (enumerated) - uint8_t
	 */
	public SYRINGE2 getSyringe2() {
		try {
			SYRINGE2 o = SYRINGE2.valueOf(getMessageType().getFieldPossibleValues("syringe2").get(getLong("syringe2")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSyringe2Str() {
		return getString("syringe2");
	}

	public short getSyringe2Val() {
		return (short) getInteger("syringe2");
	}

	/**
	 *  @param syringe2 Syringe2 (enumerated)
	 */
	public Sample setSyringe2(SYRINGE2 syringe2) {
		values.put("syringe2", syringe2.value());
		return this;
	}

	/**
	 *  @param syringe2 Syringe2 (as a String)
	 */
	public Sample setSyringe2Str(String syringe2) {
		setValue("syringe2", syringe2);
		return this;
	}

	/**
	 *  @param syringe2 Syringe2 (integer value)
	 */
	public Sample setSyringe2Val(short syringe2) {
		setValue("syringe2", syringe2);
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
	public Sample setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Sample setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
