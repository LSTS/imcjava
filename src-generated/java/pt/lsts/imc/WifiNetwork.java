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
 *  IMC Message WiFi Network (2012)<br/>
 *  This message is used to log wifi networks in the surroundings.<br/>
 */

public class WifiNetwork extends IMCMessage {

	public static final int ID_STATIC = 2012;

	public WifiNetwork() {
		super(ID_STATIC);
	}

	public WifiNetwork(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WifiNetwork(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static WifiNetwork create(Object... values) {
		WifiNetwork m = new WifiNetwork();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static WifiNetwork clone(IMCMessage msg) throws Exception {

		WifiNetwork m = new WifiNetwork();
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

	public WifiNetwork(String essid, String mac, short signal, short noise, byte ccq, short channel, float freq, String security) {
		super(ID_STATIC);
		if (essid != null)
			setEssid(essid);
		if (mac != null)
			setMac(mac);
		setSignal(signal);
		setNoise(noise);
		setCcq(ccq);
		setChannel(channel);
		setFreq(freq);
		if (security != null)
			setSecurity(security);
	}

	/**
	 *  @return ESSID - plaintext
	 */
	public String getEssid() {
		return getString("essid");
	}

	/**
	 *  @param essid ESSID
	 */
	public WifiNetwork setEssid(String essid) {
		values.put("essid", essid);
		return this;
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
	public WifiNetwork setMac(String mac) {
		values.put("mac", mac);
		return this;
	}

	/**
	 *  @return Signal Level (db) - int16_t
	 */
	public short getSignal() {
		return (short) getInteger("signal");
	}

	/**
	 *  @param signal Signal Level (db)
	 */
	public WifiNetwork setSignal(short signal) {
		values.put("signal", signal);
		return this;
	}

	/**
	 *  @return Noise Level (db) - int16_t
	 */
	public short getNoise() {
		return (short) getInteger("noise");
	}

	/**
	 *  @param noise Noise Level (db)
	 */
	public WifiNetwork setNoise(short noise) {
		values.put("noise", noise);
		return this;
	}

	/**
	 *  @return CCQ (%) - int8_t
	 */
	public byte getCcq() {
		return (byte) getInteger("ccq");
	}

	/**
	 *  @param ccq CCQ (%)
	 */
	public WifiNetwork setCcq(byte ccq) {
		values.put("ccq", ccq);
		return this;
	}

	/**
	 *  @return Wifi Channel - uint8_t
	 */
	public short getChannel() {
		return (short) getInteger("channel");
	}

	/**
	 *  @param channel Wifi Channel
	 */
	public WifiNetwork setChannel(short channel) {
		values.put("channel", channel);
		return this;
	}

	/**
	 *  @return Wifi Frequency - fp32_t
	 */
	public double getFreq() {
		return getDouble("freq");
	}

	/**
	 *  @param freq Wifi Frequency
	 */
	public WifiNetwork setFreq(double freq) {
		values.put("freq", freq);
		return this;
	}

	/**
	 *  @return Security - plaintext
	 */
	public String getSecurity() {
		return getString("security");
	}

	/**
	 *  @param security Security
	 */
	public WifiNetwork setSecurity(String security) {
		values.put("security", security);
		return this;
	}

}
