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
 *  IMC Message GPS Fix RTK (293)<br/>
 *  Report of an RTK-GPS fix.<br/>
 */

public class GpsFixRtk extends IMCMessage {

	public static final int RFV_VALID_TIME = 0x0001;
	public static final int RFV_VALID_BASE = 0x0002;
	public static final int RFV_VALID_POS = 0x0004;
	public static final int RFV_VALID_VEL = 0x0008;

	public enum TYPE {
		NONE(0),
		OBS(1),
		FLOAT(2),
		FIXED(3);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 293;

	public GpsFixRtk() {
		super(ID_STATIC);
	}

	public GpsFixRtk(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GpsFixRtk(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GpsFixRtk create(Object... values) {
		GpsFixRtk m = new GpsFixRtk();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GpsFixRtk clone(IMCMessage msg) throws Exception {

		GpsFixRtk m = new GpsFixRtk();
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

	public GpsFixRtk(int validity, TYPE type, long tow, double base_lat, double base_lon, float base_height, float n, float e, float d, float v_n, float v_e, float v_d, short satellites, int iar_hyp, float iar_ratio) {
		super(ID_STATIC);
		setValidity(validity);
		setType(type);
		setTow(tow);
		setBaseLat(base_lat);
		setBaseLon(base_lon);
		setBaseHeight(base_height);
		setN(n);
		setE(e);
		setD(d);
		setVN(v_n);
		setVE(v_e);
		setVD(v_d);
		setSatellites(satellites);
		setIarHyp(iar_hyp);
		setIarRatio(iar_ratio);
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
	public GpsFixRtk setValidity(int validity) {
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
	public GpsFixRtk setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public GpsFixRtk setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public GpsFixRtk setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return GPS Time of Week - uint32_t
	 */
	public long getTow() {
		return getLong("tow");
	}

	/**
	 *  @param tow GPS Time of Week
	 */
	public GpsFixRtk setTow(long tow) {
		values.put("tow", tow);
		return this;
	}

	/**
	 *  @return Base Latitude WGS-84 (rad) - fp64_t
	 */
	public double getBaseLat() {
		return getDouble("base_lat");
	}

	/**
	 *  @param base_lat Base Latitude WGS-84 (rad)
	 */
	public GpsFixRtk setBaseLat(double base_lat) {
		values.put("base_lat", base_lat);
		return this;
	}

	/**
	 *  @return Base Longitude WGS-84 (rad) - fp64_t
	 */
	public double getBaseLon() {
		return getDouble("base_lon");
	}

	/**
	 *  @param base_lon Base Longitude WGS-84 (rad)
	 */
	public GpsFixRtk setBaseLon(double base_lon) {
		values.put("base_lon", base_lon);
		return this;
	}

	/**
	 *  @return Base Height above WGS-84 ellipsoid (m) - fp32_t
	 */
	public double getBaseHeight() {
		return getDouble("base_height");
	}

	/**
	 *  @param base_height Base Height above WGS-84 ellipsoid (m)
	 */
	public GpsFixRtk setBaseHeight(double base_height) {
		values.put("base_height", base_height);
		return this;
	}

	/**
	 *  @return Position North (m) - fp32_t
	 */
	public double getN() {
		return getDouble("n");
	}

	/**
	 *  @param n Position North (m)
	 */
	public GpsFixRtk setN(double n) {
		values.put("n", n);
		return this;
	}

	/**
	 *  @return Position East (m) - fp32_t
	 */
	public double getE() {
		return getDouble("e");
	}

	/**
	 *  @param e Position East (m)
	 */
	public GpsFixRtk setE(double e) {
		values.put("e", e);
		return this;
	}

	/**
	 *  @return Position Down (m) - fp32_t
	 */
	public double getD() {
		return getDouble("d");
	}

	/**
	 *  @param d Position Down (m)
	 */
	public GpsFixRtk setD(double d) {
		values.put("d", d);
		return this;
	}

	/**
	 *  @return Velocity North (m/s) - fp32_t
	 */
	public double getVN() {
		return getDouble("v_n");
	}

	/**
	 *  @param v_n Velocity North (m/s)
	 */
	public GpsFixRtk setVN(double v_n) {
		values.put("v_n", v_n);
		return this;
	}

	/**
	 *  @return Velocity East (m/s) - fp32_t
	 */
	public double getVE() {
		return getDouble("v_e");
	}

	/**
	 *  @param v_e Velocity East (m/s)
	 */
	public GpsFixRtk setVE(double v_e) {
		values.put("v_e", v_e);
		return this;
	}

	/**
	 *  @return Velocity Down (m/s) - fp32_t
	 */
	public double getVD() {
		return getDouble("v_d");
	}

	/**
	 *  @param v_d Velocity Down (m/s)
	 */
	public GpsFixRtk setVD(double v_d) {
		values.put("v_d", v_d);
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
	public GpsFixRtk setSatellites(short satellites) {
		values.put("satellites", satellites);
		return this;
	}

	/**
	 *  @return IAR Hypotheses - uint16_t
	 */
	public int getIarHyp() {
		return getInteger("iar_hyp");
	}

	/**
	 *  @param iar_hyp IAR Hypotheses
	 */
	public GpsFixRtk setIarHyp(int iar_hyp) {
		values.put("iar_hyp", iar_hyp);
		return this;
	}

	/**
	 *  @return IAR Ratio - fp32_t
	 */
	public double getIarRatio() {
		return getDouble("iar_ratio");
	}

	/**
	 *  @param iar_ratio IAR Ratio
	 */
	public GpsFixRtk setIarRatio(double iar_ratio) {
		values.put("iar_ratio", iar_ratio);
		return this;
	}

}
