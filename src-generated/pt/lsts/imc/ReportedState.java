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
 *  IMC Message Reported State (600)<br/>
 *  A vehicle state that is reported to other consoles (including PDAConsole). Source can be acoustic tracker, SMS, Wi-Fi, etc...<br/>
 */

public class ReportedState extends IMCMessage {

	public enum S_TYPE {
		WI_FI(0),
		TRACKER(1),
		SMS(2),
		ACOUSTIC_MODEM(3),
		UNKNOWN(254);

		protected long value;

		public long value() {
			return value;
		}

		S_TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 600;

	public ReportedState() {
		super(ID_STATIC);
	}

	public ReportedState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReportedState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ReportedState create(Object... values) {
		ReportedState m = new ReportedState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ReportedState clone(IMCMessage msg) throws Exception {

		ReportedState m = new ReportedState();
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

	public ReportedState(double lat, double lon, double depth, double roll, double pitch, double yaw, double rcp_time, String sid, S_TYPE s_type) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setDepth(depth);
		setRoll(roll);
		setPitch(pitch);
		setYaw(yaw);
		setRcpTime(rcp_time);
		if (sid != null)
			setSid(sid);
		setSType(s_type);
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
	public ReportedState setLat(double lat) {
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
	public ReportedState setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Depth (m) - fp64_t
	 */
	public double getDepth() {
		return getDouble("depth");
	}

	/**
	 *  @param depth Depth (m)
	 */
	public ReportedState setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Roll (rad) - fp64_t
	 */
	public double getRoll() {
		return getDouble("roll");
	}

	/**
	 *  @param roll Roll (rad)
	 */
	public ReportedState setRoll(double roll) {
		values.put("roll", roll);
		return this;
	}

	/**
	 *  @return Pitch (rad) - fp64_t
	 */
	public double getPitch() {
		return getDouble("pitch");
	}

	/**
	 *  @param pitch Pitch (rad)
	 */
	public ReportedState setPitch(double pitch) {
		values.put("pitch", pitch);
		return this;
	}

	/**
	 *  @return Yaw (rad) - fp64_t
	 */
	public double getYaw() {
		return getDouble("yaw");
	}

	/**
	 *  @param yaw Yaw (rad)
	 */
	public ReportedState setYaw(double yaw) {
		values.put("yaw", yaw);
		return this;
	}

	/**
	 *  @return Reception Time (s) - fp64_t
	 */
	public double getRcpTime() {
		return getDouble("rcp_time");
	}

	/**
	 *  @param rcp_time Reception Time (s)
	 */
	public ReportedState setRcpTime(double rcp_time) {
		values.put("rcp_time", rcp_time);
		return this;
	}

	/**
	 *  @return System Identifier - plaintext
	 */
	public String getSid() {
		return getString("sid");
	}

	/**
	 *  @param sid System Identifier
	 */
	public ReportedState setSid(String sid) {
		values.put("sid", sid);
		return this;
	}

	/**
	 *  @return Source Type (enumerated) - uint8_t
	 */
	public S_TYPE getSType() {
		try {
			S_TYPE o = S_TYPE.valueOf(getMessageType().getFieldPossibleValues("s_type").get(getLong("s_type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSTypeStr() {
		return getString("s_type");
	}

	public short getSTypeVal() {
		return (short) getInteger("s_type");
	}

	/**
	 *  @param s_type Source Type (enumerated)
	 */
	public ReportedState setSType(S_TYPE s_type) {
		values.put("s_type", s_type.value());
		return this;
	}

	/**
	 *  @param s_type Source Type (as a String)
	 */
	public ReportedState setSTypeStr(String s_type) {
		setValue("s_type", s_type);
		return this;
	}

	/**
	 *  @param s_type Source Type (integer value)
	 */
	public ReportedState setSTypeVal(short s_type) {
		setValue("s_type", s_type);
		return this;
	}

}
