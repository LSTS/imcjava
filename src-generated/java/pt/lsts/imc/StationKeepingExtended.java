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
 *  IMC Message Station Keeping Extended (720)<br/>
 *  The Station Keeping Extended maneuver makes the vehicle come to the surface<br/>
 *  and then enter a given circular perimeter around a waypoint coordinate<br/>
 *  for a certain amount of time. It extends the Station Keeping maneuver with the feature<br/>
 *  'Keep Safe', which allows for the vehicle to hold position underwater and popup periodically<br/>
 *  to communicate.<br/>
 */

public class StationKeepingExtended extends Maneuver {

	public static final short FLG_KEEP_SAFE = 0x01;

	public static final int ID_STATIC = 720;

	public StationKeepingExtended() {
		super(ID_STATIC);
	}

	public StationKeepingExtended(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StationKeepingExtended(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static StationKeepingExtended create(Object... values) {
		StationKeepingExtended m = new StationKeepingExtended();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static StationKeepingExtended clone(IMCMessage msg) throws Exception {

		StationKeepingExtended m = new StationKeepingExtended();
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

	public StationKeepingExtended(double lat, double lon, float z, ZUnits z_units, float radius, int duration, float speed, SpeedUnits speed_units, int popup_period, int popup_duration, short flags, String custom) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setRadius(radius);
		setDuration(duration);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setPopupPeriod(popup_period);
		setPopupDuration(popup_duration);
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
	public StationKeepingExtended setLat(double lat) {
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
	public StationKeepingExtended setLon(double lon) {
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
	public StationKeepingExtended setZ(double z) {
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
	public StationKeepingExtended setZUnits(ZUnits z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public StationKeepingExtended setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public StationKeepingExtended setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
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
	public StationKeepingExtended setRadius(double radius) {
		values.put("radius", radius);
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
	public StationKeepingExtended setDuration(int duration) {
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
	public StationKeepingExtended setSpeed(double speed) {
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
	public StationKeepingExtended setSpeedUnits(SpeedUnits speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public StationKeepingExtended setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public StationKeepingExtended setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @return PopUp Period (s) - uint16_t
	 */
	public int getPopupPeriod() {
		return getInteger("popup_period");
	}

	/**
	 *  @param popup_period PopUp Period (s)
	 */
	public StationKeepingExtended setPopupPeriod(int popup_period) {
		values.put("popup_period", popup_period);
		return this;
	}

	/**
	 *  @return PopUp Duration (s) - uint16_t
	 */
	public int getPopupDuration() {
		return getInteger("popup_duration");
	}

	/**
	 *  @param popup_duration PopUp Duration (s)
	 */
	public StationKeepingExtended setPopupDuration(int popup_duration) {
		values.put("popup_duration", popup_duration);
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
	public StationKeepingExtended setFlags(short flags) {
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
	public StationKeepingExtended setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public StationKeepingExtended setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
