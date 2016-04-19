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
 *  IMC Message Historic Data Series (184)<br/>
 *  This message holds a list of inline data samples produced by one or more vehicles in the past.<br/>
 *  It is used to transfer data over disruption tolerant networks.<br/>
 */

public class HistoricData extends IMCMessage {

	public static final int ID_STATIC = 184;

	public HistoricData() {
		super(ID_STATIC);
	}

	public HistoricData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistoricData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static HistoricData create(Object... values) {
		HistoricData m = new HistoricData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static HistoricData clone(IMCMessage msg) throws Exception {

		HistoricData m = new HistoricData();
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

	public HistoricData(float base_lat, float base_lon, float base_time, java.util.Collection<RemoteData> data) {
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
	public HistoricData setBaseLat(double base_lat) {
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
	public HistoricData setBaseLon(double base_lon) {
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
	public HistoricData setBaseTime(double base_time) {
		values.put("base_time", base_time);
		return this;
	}

	/**
	 *  @return Data - message-list
	 */
	public java.util.Vector<RemoteData> getData() {
		try {
			return getMessageList("data", RemoteData.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param data Data
	 */
	public HistoricData setData(java.util.Collection<RemoteData> data) {
		values.put("data", data);
		return this;
	}

}
