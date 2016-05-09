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
 *  IMC Message Takeoff Maneuver (488)<br/>
 *  Automatic takeoff for UAVs.<br/>
 *  This maneuver specifies a target waypoint where to takeoff.<br/>
 *  Takeoff direction is set from the direction the plane is pointing when the auto takeoff command is started.<br/>
 *  It will remain that way until the vehicle reaches the target z reference. After that it will go to the target waypoint.<br/>
 */

@SuppressWarnings("unchecked")
public class Takeoff extends Maneuver {

	public enum UAV_TYPE {
		FIXEDWING(0),
		COPTER(1),
		VTOL(2);

		protected long value;

		public long value() {
			return value;
		}

		UAV_TYPE(long value) {
			this.value = value;
		}
	}

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

	public static final int ID_STATIC = 488;

	public Takeoff() {
		super(ID_STATIC);
	}

	public Takeoff(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Takeoff(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Takeoff create(Object... values) {
		Takeoff m = new Takeoff();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Takeoff clone(IMCMessage msg) throws Exception {

		Takeoff m = new Takeoff();
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

	public Takeoff(UAV_TYPE uav_type, double land_lat, double land_lon, float takeoff_z, Z_UNITS z_units, float takeoff_pitch, String custom) {
		super(ID_STATIC);
		setUavType(uav_type);
		setLandLat(land_lat);
		setLandLon(land_lon);
		setTakeoffZ(takeoff_z);
		setZUnits(z_units);
		setTakeoffPitch(takeoff_pitch);
		if (custom != null)
			setCustom(custom);
	}

	/**
	 *  @return UAV Type (enumerated) - uint8_t
	 */
	public UAV_TYPE getUavType() {
		try {
			UAV_TYPE o = UAV_TYPE.valueOf(getMessageType().getFieldPossibleValues("uav_type").get(getLong("uav_type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getUavTypeStr() {
		return getString("uav_type");
	}

	public short getUavTypeVal() {
		return (short) getInteger("uav_type");
	}

	/**
	 *  @param uav_type UAV Type (enumerated)
	 */
	public Takeoff setUavType(UAV_TYPE uav_type) {
		values.put("uav_type", uav_type.value());
		return this;
	}

	/**
	 *  @param uav_type UAV Type (as a String)
	 */
	public Takeoff setUavTypeStr(String uav_type) {
		setValue("uav_type", uav_type);
		return this;
	}

	/**
	 *  @param uav_type UAV Type (integer value)
	 */
	public Takeoff setUavTypeVal(short uav_type) {
		setValue("uav_type", uav_type);
		return this;
	}

	/**
	 *  @return Latitude WGS-84 (rad) - fp64_t
	 */
	public double getLandLat() {
		return getDouble("land_lat");
	}

	/**
	 *  @param land_lat Latitude WGS-84 (rad)
	 */
	public Takeoff setLandLat(double land_lat) {
		values.put("land_lat", land_lat);
		return this;
	}

	/**
	 *  @return Longitude WGS-84 (rad) - fp64_t
	 */
	public double getLandLon() {
		return getDouble("land_lon");
	}

	/**
	 *  @param land_lon Longitude WGS-84 (rad)
	 */
	public Takeoff setLandLon(double land_lon) {
		values.put("land_lon", land_lon);
		return this;
	}

	/**
	 *  @return Z Reference (m) - fp32_t
	 */
	public double getTakeoffZ() {
		return getDouble("takeoff_z");
	}

	/**
	 *  @param takeoff_z Z Reference (m)
	 */
	public Takeoff setTakeoffZ(double takeoff_z) {
		values.put("takeoff_z", takeoff_z);
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
	public Takeoff setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public Takeoff setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public Takeoff setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @return Pitch Angle (rad) - fp32_t
	 */
	public double getTakeoffPitch() {
		return getDouble("takeoff_pitch");
	}

	/**
	 *  @param takeoff_pitch Pitch Angle (rad)
	 */
	public Takeoff setTakeoffPitch(double takeoff_pitch) {
		values.put("takeoff_pitch", takeoff_pitch);
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
	public Takeoff setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Takeoff setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
