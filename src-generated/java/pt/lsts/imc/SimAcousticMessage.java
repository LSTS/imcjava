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
 *  IMC Message Simulated Acoustic Message (207)<br/>
 *  Send an acoustic message.<br/>
 */

public class SimAcousticMessage extends IMCMessage {

	public static final short SAM_ACK = 0x01;
	public static final short SAM_DELAYED = 0x02;
	public static final short SAM_REPLY = 0x03;

	public static final int ID_STATIC = 207;

	public SimAcousticMessage() {
		super(ID_STATIC);
	}

	public SimAcousticMessage(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimAcousticMessage(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SimAcousticMessage create(Object... values) {
		SimAcousticMessage m = new SimAcousticMessage();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SimAcousticMessage clone(IMCMessage msg) throws Exception {

		SimAcousticMessage m = new SimAcousticMessage();
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

	public SimAcousticMessage(double lat, double lon, float depth, String sentence, double txtime, String modem_type, String sys_src, int seq, String sys_dst, short flags, byte[] data) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setDepth(depth);
		if (sentence != null)
			setSentence(sentence);
		setTxtime(txtime);
		if (modem_type != null)
			setModemType(modem_type);
		if (sys_src != null)
			setSysSrc(sys_src);
		setSeq(seq);
		if (sys_dst != null)
			setSysDst(sys_dst);
		setFlags(flags);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Latitude - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude
	 */
	public SimAcousticMessage setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude
	 */
	public SimAcousticMessage setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Depth - fp32_t
	 */
	public double getDepth() {
		return getDouble("depth");
	}

	/**
	 *  @param depth Depth
	 */
	public SimAcousticMessage setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Sentence - plaintext
	 */
	public String getSentence() {
		return getString("sentence");
	}

	/**
	 *  @param sentence Sentence
	 */
	public SimAcousticMessage setSentence(String sentence) {
		values.put("sentence", sentence);
		return this;
	}

	/**
	 *  @return Transmission Time (s) - fp64_t
	 */
	public double getTxtime() {
		return getDouble("txtime");
	}

	/**
	 *  @param txtime Transmission Time (s)
	 */
	public SimAcousticMessage setTxtime(double txtime) {
		values.put("txtime", txtime);
		return this;
	}

	/**
	 *  @return Modem Type - plaintext
	 */
	public String getModemType() {
		return getString("modem_type");
	}

	/**
	 *  @param modem_type Modem Type
	 */
	public SimAcousticMessage setModemType(String modem_type) {
		values.put("modem_type", modem_type);
		return this;
	}

	/**
	 *  @return Source system - plaintext
	 */
	public String getSysSrc() {
		return getString("sys_src");
	}

	/**
	 *  @param sys_src Source system
	 */
	public SimAcousticMessage setSysSrc(String sys_src) {
		values.put("sys_src", sys_src);
		return this;
	}

	/**
	 *  @return Sequence Id - uint16_t
	 */
	public int getSeq() {
		return getInteger("seq");
	}

	/**
	 *  @param seq Sequence Id
	 */
	public SimAcousticMessage setSeq(int seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return Destination System - plaintext
	 */
	public String getSysDst() {
		return getString("sys_dst");
	}

	/**
	 *  @param sys_dst Destination System
	 */
	public SimAcousticMessage setSysDst(String sys_dst) {
		values.put("sys_dst", sys_dst);
		return this;
	}

	/**
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public SimAcousticMessage setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @return Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Data
	 */
	public SimAcousticMessage setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
