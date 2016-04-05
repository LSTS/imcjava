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
 *  IMC Message Scheduled Goto (487)<br/>
 *  This maneuver is used to command the vehicle to arrive at some destination at<br/>
 *  a specified absolute time.<br/>
 *  The vehicle's speed will vary according to environment conditions and/or maneuver start time.<br/>
 */

@SuppressWarnings("unchecked")
public class ScheduledGoto extends Maneuver {

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

	public enum TRAVEL_Z_UNITS {
		NONE(0),
		DEPTH(1),
		ALTITUDE(2),
		HEIGHT(3);

		protected long value;

		public long value() {
			return value;
		}

		TRAVEL_Z_UNITS(long value) {
			this.value = value;
		}
	}

	public enum DELAYED {
		RESUME(0),
		SKIP(1),
		FAIL(2);

		protected long value;

		public long value() {
			return value;
		}

		DELAYED(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 487;

	public ScheduledGoto() {
		super(ID_STATIC);
	}

	public ScheduledGoto(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ScheduledGoto(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ScheduledGoto create(Object... values) {
		ScheduledGoto m = new ScheduledGoto();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ScheduledGoto clone(IMCMessage msg) throws Exception {

		ScheduledGoto m = new ScheduledGoto();
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

	public ScheduledGoto(double arrival_time, double lat, double lon, float z, Z_UNITS z_units, float travel_z, TRAVEL_Z_UNITS travel_z_units, DELAYED delayed) {
		super(ID_STATIC);
		setArrivalTime(arrival_time);
		setLat(lat);
		setLon(lon);
		setZ(z);
		setZUnits(z_units);
		setTravelZ(travel_z);
		setTravelZUnits(travel_z_units);
		setDelayed(delayed);
	}

	/**
	 *  @return Time of arrival (s) - fp64_t
	 */
	public double getArrivalTime() {
		return getDouble("arrival_time");
	}

	/**
	 *  @param arrival_time Time of arrival (s)
	 */
	public ScheduledGoto setArrivalTime(double arrival_time) {
		values.put("arrival_time", arrival_time);
		return this;
	}

	/**
	 *  @return Destination Latitude WGS-84 (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Destination Latitude WGS-84 (rad)
	 */
	public ScheduledGoto setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Destination Longitude WGS-84 (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Destination Longitude WGS-84 (rad)
	 */
	public ScheduledGoto setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Destination Z Reference (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Destination Z Reference (m)
	 */
	public ScheduledGoto setZ(double z) {
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
	public ScheduledGoto setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public ScheduledGoto setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public ScheduledGoto setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @return Travel Z Reference (m) - fp32_t
	 */
	public double getTravelZ() {
		return getDouble("travel_z");
	}

	/**
	 *  @param travel_z Travel Z Reference (m)
	 */
	public ScheduledGoto setTravelZ(double travel_z) {
		values.put("travel_z", travel_z);
		return this;
	}

	/**
	 *  @return Travel Z Units (enumerated) - uint8_t
	 */
	public TRAVEL_Z_UNITS getTravelZUnits() {
		try {
			TRAVEL_Z_UNITS o = TRAVEL_Z_UNITS.valueOf(getMessageType().getFieldPossibleValues("travel_z_units").get(getLong("travel_z_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTravelZUnitsStr() {
		return getString("travel_z_units");
	}

	public short getTravelZUnitsVal() {
		return (short) getInteger("travel_z_units");
	}

	/**
	 *  @param travel_z_units Travel Z Units (enumerated)
	 */
	public ScheduledGoto setTravelZUnits(TRAVEL_Z_UNITS travel_z_units) {
		values.put("travel_z_units", travel_z_units.value());
		return this;
	}

	/**
	 *  @param travel_z_units Travel Z Units (as a String)
	 */
	public ScheduledGoto setTravelZUnitsStr(String travel_z_units) {
		setValue("travel_z_units", travel_z_units);
		return this;
	}

	/**
	 *  @param travel_z_units Travel Z Units (integer value)
	 */
	public ScheduledGoto setTravelZUnitsVal(short travel_z_units) {
		setValue("travel_z_units", travel_z_units);
		return this;
	}

	/**
	 *  @return Delayed Behavior (enumerated) - uint8_t
	 */
	public DELAYED getDelayed() {
		try {
			DELAYED o = DELAYED.valueOf(getMessageType().getFieldPossibleValues("delayed").get(getLong("delayed")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getDelayedStr() {
		return getString("delayed");
	}

	public short getDelayedVal() {
		return (short) getInteger("delayed");
	}

	/**
	 *  @param delayed Delayed Behavior (enumerated)
	 */
	public ScheduledGoto setDelayed(DELAYED delayed) {
		values.put("delayed", delayed.value());
		return this;
	}

	/**
	 *  @param delayed Delayed Behavior (as a String)
	 */
	public ScheduledGoto setDelayedStr(String delayed) {
		setValue("delayed", delayed);
		return this;
	}

	/**
	 *  @param delayed Delayed Behavior (integer value)
	 */
	public ScheduledGoto setDelayedVal(short delayed) {
		setValue("delayed", delayed);
		return this;
	}

}
