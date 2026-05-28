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
 *  IMC Message LBL Beacon Configuration Extended (204)<br/>
 *  Position and configuration of an LBL transponder (beacon). The LBL transponder may be fixed or mobile, depending on TTL value.<br/>
 */

public class LblBeaconExtended extends IMCMessage {

	public static final int ID_STATIC = 204;

	public LblBeaconExtended() {
		super(ID_STATIC);
	}

	public LblBeaconExtended(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LblBeaconExtended(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LblBeaconExtended create(Object... values) {
		LblBeaconExtended m = new LblBeaconExtended();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LblBeaconExtended clone(IMCMessage msg) throws Exception {

		LblBeaconExtended m = new LblBeaconExtended();
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

	public LblBeaconExtended(String beacon, double lat, double lon, float depth, short query_channel, short reply_channel, short transponder_delay, double ctime, int ttl, float vx, float vy, float vz) {
		super(ID_STATIC);
		if (beacon != null)
			setBeacon(beacon);
		setLat(lat);
		setLon(lon);
		setDepth(depth);
		setQueryChannel(query_channel);
		setReplyChannel(reply_channel);
		setTransponderDelay(transponder_delay);
		setCtime(ctime);
		setTtl(ttl);
		setVx(vx);
		setVy(vy);
		setVz(vz);
	}

	/**
	 *  @return Beacon Name - plaintext
	 */
	public String getBeacon() {
		return getString("beacon");
	}

	/**
	 *  @param beacon Beacon Name
	 */
	public LblBeaconExtended setBeacon(String beacon) {
		values.put("beacon", beacon);
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
	public LblBeaconExtended setLat(double lat) {
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
	public LblBeaconExtended setLon(double lon) {
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
	public LblBeaconExtended setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Interrogation channel - uint8_t
	 */
	public short getQueryChannel() {
		return (short) getInteger("query_channel");
	}

	/**
	 *  @param query_channel Interrogation channel
	 */
	public LblBeaconExtended setQueryChannel(short query_channel) {
		values.put("query_channel", query_channel);
		return this;
	}

	/**
	 *  @return Reply channel - uint8_t
	 */
	public short getReplyChannel() {
		return (short) getInteger("reply_channel");
	}

	/**
	 *  @param reply_channel Reply channel
	 */
	public LblBeaconExtended setReplyChannel(short reply_channel) {
		values.put("reply_channel", reply_channel);
		return this;
	}

	/**
	 *  @return Transponder delay (ms) - uint8_t
	 */
	public short getTransponderDelay() {
		return (short) getInteger("transponder_delay");
	}

	/**
	 *  @param transponder_delay Transponder delay (ms)
	 */
	public LblBeaconExtended setTransponderDelay(short transponder_delay) {
		values.put("transponder_delay", transponder_delay);
		return this;
	}

	/**
	 *  @return Configuration Timestamp (s) - fp64_t
	 */
	public double getCtime() {
		return getDouble("ctime");
	}

	/**
	 *  @param ctime Configuration Timestamp (s)
	 */
	public LblBeaconExtended setCtime(double ctime) {
		values.put("ctime", ctime);
		return this;
	}

	/**
	 *  @return Time to live (s) - uint16_t
	 */
	public int getTtl() {
		return getInteger("ttl");
	}

	/**
	 *  @param ttl Time to live (s)
	 */
	public LblBeaconExtended setTtl(int ttl) {
		values.put("ttl", ttl);
		return this;
	}

	/**
	 *  @return Ground Velocity X (North) (m/s) - fp32_t
	 */
	public double getVx() {
		return getDouble("vx");
	}

	/**
	 *  @param vx Ground Velocity X (North) (m/s)
	 */
	public LblBeaconExtended setVx(double vx) {
		values.put("vx", vx);
		return this;
	}

	/**
	 *  @return Ground Velocity Y (East) (m/s) - fp32_t
	 */
	public double getVy() {
		return getDouble("vy");
	}

	/**
	 *  @param vy Ground Velocity Y (East) (m/s)
	 */
	public LblBeaconExtended setVy(double vy) {
		values.put("vy", vy);
		return this;
	}

	/**
	 *  @return Ground Velocity Z (Down) (m/s) - fp32_t
	 */
	public double getVz() {
		return getDouble("vz");
	}

	/**
	 *  @param vz Ground Velocity Z (Down) (m/s)
	 */
	public LblBeaconExtended setVz(double vz) {
		values.put("vz", vz);
		return this;
	}

}
