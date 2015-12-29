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
 *  IMC Message GPS Fix (253)<br/>
 *  Report of a GPS fix.<br/>
 */

public class GpsFix extends IMCMessage {

	public static final int GFV_VALID_DATE = 0x0001;
	public static final int GFV_VALID_TIME = 0x0002;
	public static final int GFV_VALID_POS = 0x0004;
	public static final int GFV_VALID_COG = 0x0008;
	public static final int GFV_VALID_SOG = 0x0010;
	public static final int GFV_VALID_HACC = 0x0020;
	public static final int GFV_VALID_VACC = 0x0040;
	public static final int GFV_VALID_HDOP = 0x0080;
	public static final int GFV_VALID_VDOP = 0x0100;

	public enum TYPE {
		STANDALONE(0),
		DIFFERENTIAL(1),
		DEAD_RECKONING(2),
		MANUAL_INPUT(3),
		SIMULATION(4);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 253;

	public GpsFix() {
		super(ID_STATIC);
	}

	public GpsFix(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GpsFix(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GpsFix create(Object... values) {
		GpsFix m = new GpsFix();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GpsFix clone(IMCMessage msg) throws Exception {

		GpsFix m = new GpsFix();
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

	public GpsFix(int validity, TYPE type, int utc_year, short utc_month, short utc_day, float utc_time, double lat, double lon, float height, short satellites, float cog, float sog, float hdop, float vdop, float hacc, float vacc) {
		super(ID_STATIC);
		setValidity(validity);
		setType(type);
		setUtcYear(utc_year);
		setUtcMonth(utc_month);
		setUtcDay(utc_day);
		setUtcTime(utc_time);
		setLat(lat);
		setLon(lon);
		setHeight(height);
		setSatellites(satellites);
		setCog(cog);
		setSog(sog);
		setHdop(hdop);
		setVdop(vdop);
		setHacc(hacc);
		setVacc(vacc);
	}

	/**
	 *  @return Validity (bitfield) - uint16_t
	 */
	public int getValidity() {
		return getInteger("validity");
	}

	/**
	 *  @param validity Validity (bitfield)
	 */
	public GpsFix setValidity(int validity) {
		values.put("validity", validity);
		return this;
	}

	/**
	 *  @return Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Type (enumerated)
	 */
	public GpsFix setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public GpsFix setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public GpsFix setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return UTC Year - uint16_t
	 */
	public int getUtcYear() {
		return getInteger("utc_year");
	}

	/**
	 *  @param utc_year UTC Year
	 */
	public GpsFix setUtcYear(int utc_year) {
		values.put("utc_year", utc_year);
		return this;
	}

	/**
	 *  @return UTC Month - uint8_t
	 */
	public short getUtcMonth() {
		return (short) getInteger("utc_month");
	}

	/**
	 *  @param utc_month UTC Month
	 */
	public GpsFix setUtcMonth(short utc_month) {
		values.put("utc_month", utc_month);
		return this;
	}

	/**
	 *  @return UTC Day - uint8_t
	 */
	public short getUtcDay() {
		return (short) getInteger("utc_day");
	}

	/**
	 *  @param utc_day UTC Day
	 */
	public GpsFix setUtcDay(short utc_day) {
		values.put("utc_day", utc_day);
		return this;
	}

	/**
	 *  @return UTC Time of Fix (s) - fp32_t
	 */
	public double getUtcTime() {
		return getDouble("utc_time");
	}

	/**
	 *  @param utc_time UTC Time of Fix (s)
	 */
	public GpsFix setUtcTime(double utc_time) {
		values.put("utc_time", utc_time);
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
	public GpsFix setLat(double lat) {
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
	public GpsFix setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Height above WGS-84 ellipsoid (m) - fp32_t
	 */
	public double getHeight() {
		return getDouble("height");
	}

	/**
	 *  @param height Height above WGS-84 ellipsoid (m)
	 */
	public GpsFix setHeight(double height) {
		values.put("height", height);
		return this;
	}

	/**
	 *  @return Number of Satellites - uint8_t
	 */
	public short getSatellites() {
		return (short) getInteger("satellites");
	}

	/**
	 *  @param satellites Number of Satellites
	 */
	public GpsFix setSatellites(short satellites) {
		values.put("satellites", satellites);
		return this;
	}

	/**
	 *  @return Course Over Ground (rad) - fp32_t
	 */
	public double getCog() {
		return getDouble("cog");
	}

	/**
	 *  @param cog Course Over Ground (rad)
	 */
	public GpsFix setCog(double cog) {
		values.put("cog", cog);
		return this;
	}

	/**
	 *  @return Speed Over Ground (m/s) - fp32_t
	 */
	public double getSog() {
		return getDouble("sog");
	}

	/**
	 *  @param sog Speed Over Ground (m/s)
	 */
	public GpsFix setSog(double sog) {
		values.put("sog", sog);
		return this;
	}

	/**
	 *  @return Horizontal Dilution of Precision - fp32_t
	 */
	public double getHdop() {
		return getDouble("hdop");
	}

	/**
	 *  @param hdop Horizontal Dilution of Precision
	 */
	public GpsFix setHdop(double hdop) {
		values.put("hdop", hdop);
		return this;
	}

	/**
	 *  @return Vertical Dilution of Precision - fp32_t
	 */
	public double getVdop() {
		return getDouble("vdop");
	}

	/**
	 *  @param vdop Vertical Dilution of Precision
	 */
	public GpsFix setVdop(double vdop) {
		values.put("vdop", vdop);
		return this;
	}

	/**
	 *  @return Horizontal Accuracy Estimate (m) - fp32_t
	 */
	public double getHacc() {
		return getDouble("hacc");
	}

	/**
	 *  @param hacc Horizontal Accuracy Estimate (m)
	 */
	public GpsFix setHacc(double hacc) {
		values.put("hacc", hacc);
		return this;
	}

	/**
	 *  @return Vertical Accuracy Estimate (m) - fp32_t
	 */
	public double getVacc() {
		return getDouble("vacc");
	}

	/**
	 *  @param vacc Vertical Accuracy Estimate (m)
	 */
	public GpsFix setVacc(double vacc) {
		values.put("vacc", vacc);
		return this;
	}

}
