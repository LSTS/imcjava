/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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

import pt.lsts.imc.def.ZUnits;
import pt.lsts.imc.def.SpeedUnits;

/**
 *  IMC Message Magnetometer Maneuver (499)<br/>
 *  Magnetometer calibration maneuver (i.e: one square pattern<br/>
 *  in one direction, a second square in the opposite direction)<br/>
 */

public class Magnetometer extends Maneuver {

	public enum DIRECTION {
		CLOCKW_FIRST(0),
		CCLOCKW_FIRST(1);

		protected long value;

		public long value() {
			return value;
		}

		DIRECTION(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 499;

	public Magnetometer() {
		super(ID_STATIC);
	}

	public Magnetometer(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Magnetometer(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Magnetometer create(Object... values) {
		Magnetometer m = new Magnetometer();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Magnetometer clone(IMCMessage msg) throws Exception {

		Magnetometer m = new Magnetometer();
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

	public Magnetometer(int timeout, double lat, double lon, float z, ZUnits z_units, float speed, SpeedUnits speed_units, double bearing, float width, DIRECTION direction, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setBearing(bearing);
		setWidth(width);
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
	public Magnetometer setTimeout(int timeout) {
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
	public Magnetometer setLat(double lat) {
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
	public Magnetometer setLon(double lon) {
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
	public Magnetometer setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Z Units (enumerated) - uint8_t
	 */
	public ZUnits getZUnits() {
		try {
			ZUnits o = ZUnits.valueOf(getMessageType().getFieldPossibleValues("z_units").get(getLong("z_units")));
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
	public Magnetometer setZUnits(ZUnits z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public Magnetometer setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public Magnetometer setZUnitsVal(short z_units) {
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
	public Magnetometer setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @return Speed Units (enumerated) - uint8_t
	 */
	public SpeedUnits getSpeedUnits() {
		try {
			SpeedUnits o = SpeedUnits.valueOf(getMessageType().getFieldPossibleValues("speed_units").get(getLong("speed_units")));
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
	public Magnetometer setSpeedUnits(SpeedUnits speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public Magnetometer setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public Magnetometer setSpeedUnitsVal(short speed_units) {
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
	public Magnetometer setBearing(double bearing) {
		values.put("bearing", bearing);
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
	public Magnetometer setWidth(double width) {
		values.put("width", width);
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
	public Magnetometer setDirection(DIRECTION direction) {
		values.put("direction", direction.value());
		return this;
	}

	/**
	 *  @param direction Direction (as a String)
	 */
	public Magnetometer setDirectionStr(String direction) {
		setValue("direction", direction);
		return this;
	}

	/**
	 *  @param direction Direction (integer value)
	 */
	public Magnetometer setDirectionVal(short direction) {
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
	public Magnetometer setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Magnetometer setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
