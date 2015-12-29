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
 *  IMC Message Communications Relay (472)<br/>
 *  In this maneuver, a vehicle drives to the center of two other<br/>
 *  systems (a, b) in order to be used as a communications relay.<br/>
 */

@SuppressWarnings("unchecked")
public class CommsRelay extends Maneuver {

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

	public static final int ID_STATIC = 472;

	public CommsRelay() {
		super(ID_STATIC);
	}

	public CommsRelay(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CommsRelay(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CommsRelay create(Object... values) {
		CommsRelay m = new CommsRelay();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CommsRelay clone(IMCMessage msg) throws Exception {

		CommsRelay m = new CommsRelay();
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

	public CommsRelay(double lat, double lon, float speed, SPEED_UNITS speed_units, int duration, int sys_a, int sys_b, float move_threshold) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setSpeed(speed);
		setSpeedUnits(speed_units);
		setDuration(duration);
		setSysA(sys_a);
		setSysB(sys_b);
		setMoveThreshold(move_threshold);
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
	public CommsRelay setLat(double lat) {
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
	public CommsRelay setLon(double lon) {
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
	public CommsRelay setSpeed(double speed) {
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
	public CommsRelay setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public CommsRelay setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public CommsRelay setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
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
	public CommsRelay setDuration(int duration) {
		values.put("duration", duration);
		return this;
	}

	/**
	 *  @return System A - uint16_t
	 */
	public int getSysA() {
		return getInteger("sys_a");
	}

	/**
	 *  @param sys_a System A
	 */
	public CommsRelay setSysA(int sys_a) {
		values.put("sys_a", sys_a);
		return this;
	}

	/**
	 *  @return System B - uint16_t
	 */
	public int getSysB() {
		return getInteger("sys_b");
	}

	/**
	 *  @param sys_b System B
	 */
	public CommsRelay setSysB(int sys_b) {
		values.put("sys_b", sys_b);
		return this;
	}

	/**
	 *  @return Move threshold (m) - fp32_t
	 */
	public double getMoveThreshold() {
		return getDouble("move_threshold");
	}

	/**
	 *  @param move_threshold Move threshold (m)
	 */
	public CommsRelay setMoveThreshold(double move_threshold) {
		values.put("move_threshold", move_threshold);
		return this;
	}

}
