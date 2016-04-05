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
 *  IMC Message Compressed Historic Data Series (185)<br/>
 *  This message holds a list of inline data samples produced by one or more vehicles in the past.<br/>
 *  It is used to transfer data over disruption tolerant networks.<br/>
 */

public class CompressedHistory extends IMCMessage {

	public static final int ID_STATIC = 185;

	public CompressedHistory() {
		super(ID_STATIC);
	}

	public CompressedHistory(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CompressedHistory(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CompressedHistory create(Object... values) {
		CompressedHistory m = new CompressedHistory();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CompressedHistory clone(IMCMessage msg) throws Exception {

		CompressedHistory m = new CompressedHistory();
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

	public CompressedHistory(float base_lat, float base_lon, float base_time, byte[] data) {
		super(ID_STATIC);
		setBaseLat(base_lat);
		setBaseLon(base_lon);
		setBaseTime(base_time);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Base Latitude (°) - fp32_t
	 */
	public double getBaseLat() {
		return getDouble("base_lat");
	}

	/**
	 *  @param base_lat Base Latitude (°)
	 */
	public CompressedHistory setBaseLat(double base_lat) {
		values.put("base_lat", base_lat);
		return this;
	}

	/**
	 *  @return Base Longitude (°) - fp32_t
	 */
	public double getBaseLon() {
		return getDouble("base_lon");
	}

	/**
	 *  @param base_lon Base Longitude (°)
	 */
	public CompressedHistory setBaseLon(double base_lon) {
		values.put("base_lon", base_lon);
		return this;
	}

	/**
	 *  @return Base Timestamp (s) - fp32_t
	 */
	public double getBaseTime() {
		return getDouble("base_time");
	}

	/**
	 *  @param base_time Base Timestamp (s)
	 */
	public CompressedHistory setBaseTime(double base_time) {
		values.put("base_time", base_time);
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
	public CompressedHistory setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
