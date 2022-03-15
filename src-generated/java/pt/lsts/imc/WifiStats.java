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


/**
 *  IMC Message WiFi Statistics (2011)<br/>
 *  This message is used to log wifi connection statistics, heavily influenced by the stats available in ubiquiti radios.<br/>
 */

public class WifiStats extends IMCMessage {

	public static final int ID_STATIC = 2011;

	public WifiStats() {
		super(ID_STATIC);
	}

	public WifiStats(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WifiStats(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static WifiStats create(Object... values) {
		WifiStats m = new WifiStats();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static WifiStats clone(IMCMessage msg) throws Exception {

		WifiStats m = new WifiStats();
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

	public WifiStats(String mac, String ip, short ccq, short noise_floor, short signal, int rssi, short rx_rate, short tx_rate, short tx_latency, short tx_power, long rx_count, long tx_count, short distance) {
		super(ID_STATIC);
		if (mac != null)
			setMac(mac);
		if (ip != null)
			setIp(ip);
		setCcq(ccq);
		setNoiseFloor(noise_floor);
		setSignal(signal);
		setRssi(rssi);
		setRxRate(rx_rate);
		setTxRate(tx_rate);
		setTxLatency(tx_latency);
		setTxPower(tx_power);
		setRxCount(rx_count);
		setTxCount(tx_count);
		setDistance(distance);
	}

	/**
	 *  @return MAC Address - plaintext
	 */
	public String getMac() {
		return getString("mac");
	}

	/**
	 *  @param mac MAC Address
	 */
	public WifiStats setMac(String mac) {
		values.put("mac", mac);
		return this;
	}

	/**
	 *  @return IP Address - plaintext
	 */
	public String getIp() {
		return getString("ip");
	}

	/**
	 *  @param ip IP Address
	 */
	public WifiStats setIp(String ip) {
		values.put("ip", ip);
		return this;
	}

	/**
	 *  @return Client Connection Quality (%) - uint8_t
	 */
	public short getCcq() {
		return (short) getInteger("ccq");
	}

	/**
	 *  @param ccq Client Connection Quality (%)
	 */
	public WifiStats setCcq(short ccq) {
		values.put("ccq", ccq);
		return this;
	}

	/**
	 *  @return Noise Floor (db) - int16_t
	 */
	public short getNoiseFloor() {
		return (short) getInteger("noise_floor");
	}

	/**
	 *  @param noise_floor Noise Floor (db)
	 */
	public WifiStats setNoiseFloor(short noise_floor) {
		values.put("noise_floor", noise_floor);
		return this;
	}

	/**
	 *  @return Signal (db) - int16_t
	 */
	public short getSignal() {
		return (short) getInteger("signal");
	}

	/**
	 *  @param signal Signal (db)
	 */
	public WifiStats setSignal(short signal) {
		values.put("signal", signal);
		return this;
	}

	/**
	 *  @return RSSI - uint16_t
	 */
	public int getRssi() {
		return getInteger("rssi");
	}

	/**
	 *  @param rssi RSSI
	 */
	public WifiStats setRssi(int rssi) {
		values.put("rssi", rssi);
		return this;
	}

	/**
	 *  @return Reception Data Rate (mbps) - int16_t
	 */
	public short getRxRate() {
		return (short) getInteger("rx_rate");
	}

	/**
	 *  @param rx_rate Reception Data Rate (mbps)
	 */
	public WifiStats setRxRate(short rx_rate) {
		values.put("rx_rate", rx_rate);
		return this;
	}

	/**
	 *  @return Transmission Data Rate (mbps) - int16_t
	 */
	public short getTxRate() {
		return (short) getInteger("tx_rate");
	}

	/**
	 *  @param tx_rate Transmission Data Rate (mbps)
	 */
	public WifiStats setTxRate(short tx_rate) {
		values.put("tx_rate", tx_rate);
		return this;
	}

	/**
	 *  @return Transmission Latency (s) - int16_t
	 */
	public short getTxLatency() {
		return (short) getInteger("tx_latency");
	}

	/**
	 *  @param tx_latency Transmission Latency (s)
	 */
	public WifiStats setTxLatency(short tx_latency) {
		values.put("tx_latency", tx_latency);
		return this;
	}

	/**
	 *  @return Transmission Power (s) - int16_t
	 */
	public short getTxPower() {
		return (short) getInteger("tx_power");
	}

	/**
	 *  @param tx_power Transmission Power (s)
	 */
	public WifiStats setTxPower(short tx_power) {
		values.put("tx_power", tx_power);
		return this;
	}

	/**
	 *  @return Reception Counter (byte) - uint32_t
	 */
	public long getRxCount() {
		return getLong("rx_count");
	}

	/**
	 *  @param rx_count Reception Counter (byte)
	 */
	public WifiStats setRxCount(long rx_count) {
		values.put("rx_count", rx_count);
		return this;
	}

	/**
	 *  @return Transmission Counter (byte) - uint32_t
	 */
	public long getTxCount() {
		return getLong("tx_count");
	}

	/**
	 *  @param tx_count Transmission Counter (byte)
	 */
	public WifiStats setTxCount(long tx_count) {
		values.put("tx_count", tx_count);
		return this;
	}

	/**
	 *  @return Distance (m) - int16_t
	 */
	public short getDistance() {
		return (short) getInteger("distance");
	}

	/**
	 *  @param distance Distance (m)
	 */
	public WifiStats setDistance(short distance) {
		values.put("distance", distance);
		return this;
	}

}
