/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Asset Report  (525)<br/>
 *  This message is represents an Asset position / status.<br/>
 */

public class AssetReport extends IMCMessage {

	public enum MEDIUM {
		WIFI(1),
		SATELLITE(2),
		ACOUSTIC(3),
		SMS(4);

		protected long value;

		public long value() {
			return value;
		}

		MEDIUM(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 525;

	public AssetReport() {
		super(ID_STATIC);
	}

	public AssetReport(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AssetReport(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AssetReport create(Object... values) {
		AssetReport m = new AssetReport();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AssetReport clone(IMCMessage msg) throws Exception {

		AssetReport m = new AssetReport();
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

	public AssetReport(String name, double report_time, MEDIUM medium, double lat, double lon, float depth, float alt, float sog, float cog, java.util.Collection<IMCMessage> msgs) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		setReportTime(report_time);
		setMedium(medium);
		setLat(lat);
		setLon(lon);
		setDepth(depth);
		setAlt(alt);
		setSog(sog);
		setCog(cog);
		if (msgs != null)
			setMsgs(msgs);
	}

	/**
	 *  @return Asset Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Asset Name
	 */
	public AssetReport setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Report Timestamp (s) - fp64_t
	 */
	public double getReportTime() {
		return getDouble("report_time");
	}

	/**
	 *  @param report_time Report Timestamp (s)
	 */
	public AssetReport setReportTime(double report_time) {
		values.put("report_time", report_time);
		return this;
	}

	/**
	 *  @return Medium (enumerated) - uint8_t
	 */
	public MEDIUM getMedium() {
		try {
			MEDIUM o = MEDIUM.valueOf(getMessageType().getFieldPossibleValues("medium").get(getLong("medium")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getMediumStr() {
		return getString("medium");
	}

	public short getMediumVal() {
		return (short) getInteger("medium");
	}

	/**
	 *  @param medium Medium (enumerated)
	 */
	public AssetReport setMedium(MEDIUM medium) {
		values.put("medium", medium.value());
		return this;
	}

	/**
	 *  @param medium Medium (as a String)
	 */
	public AssetReport setMediumStr(String medium) {
		setValue("medium", medium);
		return this;
	}

	/**
	 *  @param medium Medium (integer value)
	 */
	public AssetReport setMediumVal(short medium) {
		setValue("medium", medium);
		return this;
	}

	/**
	 *  @return Latitude (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (rad)
	 */
	public AssetReport setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (rad)
	 */
	public AssetReport setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Depth (m) - fp32_t
	 */
	public double getDepth() {
		return getDouble("depth");
	}

	/**
	 *  @param depth Depth (m)
	 */
	public AssetReport setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Altitude (m) - fp32_t
	 */
	public double getAlt() {
		return getDouble("alt");
	}

	/**
	 *  @param alt Altitude (m)
	 */
	public AssetReport setAlt(double alt) {
		values.put("alt", alt);
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
	public AssetReport setSog(double sog) {
		values.put("sog", sog);
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
	public AssetReport setCog(double cog) {
		values.put("cog", cog);
		return this;
	}

	/**
	 *  @return Additional Info - message-list
	 */
	public java.util.Vector<IMCMessage> getMsgs() {
		return getMessageList("msgs");
	}

	/**
	 *  @param msgs Additional Info
	 */
	public AssetReport setMsgs(java.util.Collection<IMCMessage> msgs) {
		values.put("msgs", msgs);
		return this;
	}

}
