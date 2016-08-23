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
 *  IMC Message Autonomous Section (493)<br/>
 *  This maneuver triggers an external controller that will guide the vehicle during a specified duration<br/>
 *  of time or until it relinquishes control using (ManeuverDone). The external controller is allowed to<br/>
 *  drive the vehicle only inside the specified boundaries.<br/>
 */

@SuppressWarnings("unchecked")
public class AutonomousSection extends Maneuver {

	public static final short ENFORCE_DEPTH = 0x01;
	public static final short ENFORCE_ALTITUDE = 0x02;
	public static final short ENFORCE_TIMEOUT = 0x04;
	public static final short ENFORCE_AREA2D = 0x08;

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

	public static final int ID_STATIC = 493;

	public AutonomousSection() {
		super(ID_STATIC);
	}

	public AutonomousSection(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AutonomousSection(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AutonomousSection create(Object... values) {
		AutonomousSection m = new AutonomousSection();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AutonomousSection clone(IMCMessage msg) throws Exception {

		AutonomousSection m = new AutonomousSection();
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

	public AutonomousSection(double lat, double lon, float speed, SPEED_UNITS speed_units, short limits, double max_depth, double min_alt, double time_limit, java.util.Collection<PolygonVertex> area_limits, String controller, String custom) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setLimits(limits);
		setMaxDepth(max_depth);
		setMinAlt(min_alt);
		setTimeLimit(time_limit);
		if (area_limits != null)
			setAreaLimits(area_limits);
		if (controller != null)
			setController(controller);
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
	public AutonomousSection setLat(double lat) {
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
	public AutonomousSection setLon(double lon) {
		values.put("lon", lon);
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
	public AutonomousSection setSpeed(double speed) {
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
	public AutonomousSection setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public AutonomousSection setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public AutonomousSection setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @return Enforced Limits (bitfield) - uint8_t
	 */
	public short getLimits() {
		return (short) getInteger("limits");
	}

	/**
	 *  @param limits Enforced Limits (bitfield)
	 */
	public AutonomousSection setLimits(short limits) {
		values.put("limits", limits);
		return this;
	}

	/**
	 *  @return Maximum depth (m) - fp64_t
	 */
	public double getMaxDepth() {
		return getDouble("max_depth");
	}

	/**
	 *  @param max_depth Maximum depth (m)
	 */
	public AutonomousSection setMaxDepth(double max_depth) {
		values.put("max_depth", max_depth);
		return this;
	}

	/**
	 *  @return Minimum altitude (m) - fp64_t
	 */
	public double getMinAlt() {
		return getDouble("min_alt");
	}

	/**
	 *  @param min_alt Minimum altitude (m)
	 */
	public AutonomousSection setMinAlt(double min_alt) {
		values.put("min_alt", min_alt);
		return this;
	}

	/**
	 *  @return Time Limit (s) - fp64_t
	 */
	public double getTimeLimit() {
		return getDouble("time_limit");
	}

	/**
	 *  @param time_limit Time Limit (s)
	 */
	public AutonomousSection setTimeLimit(double time_limit) {
		values.put("time_limit", time_limit);
		return this;
	}

	/**
	 *  @return Area Limits - message-list
	 */
	public java.util.Vector<PolygonVertex> getAreaLimits() {
		try {
			return getMessageList("area_limits", PolygonVertex.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param area_limits Area Limits
	 */
	public AutonomousSection setAreaLimits(java.util.Collection<PolygonVertex> area_limits) {
		values.put("area_limits", area_limits);
		return this;
	}

	/**
	 *  @return Controller - plaintext
	 */
	public String getController() {
		return getString("controller");
	}

	/**
	 *  @param controller Controller
	 */
	public AutonomousSection setController(String controller) {
		values.put("controller", controller);
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
	public AutonomousSection setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public AutonomousSection setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
