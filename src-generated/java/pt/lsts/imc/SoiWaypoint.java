/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message SOI Waypoint (850)<br/>
 */

public class SoiWaypoint extends IMCMessage {

	public static final int ID_STATIC = 850;

	public SoiWaypoint() {
		super(ID_STATIC);
	}

	public SoiWaypoint(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SoiWaypoint(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SoiWaypoint create(Object... values) {
		SoiWaypoint m = new SoiWaypoint();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SoiWaypoint clone(IMCMessage msg) throws Exception {

		SoiWaypoint m = new SoiWaypoint();
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

	public SoiWaypoint(float lat, float lon, long eta, int duration) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setEta(eta);
		setDuration(duration);
	}

	/**
	 *  @return Latitude (°) - fp32_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (°)
	 */
	public SoiWaypoint setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (°) - fp32_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (°)
	 */
	public SoiWaypoint setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Time Of Arrival - uint32_t
	 */
	public long getEta() {
		return getLong("eta");
	}

	/**
	 *  @param eta Time Of Arrival
	 */
	public SoiWaypoint setEta(long eta) {
		values.put("eta", eta);
		return this;
	}

	/**
	 *  @return Duration (s) - uint16_t
	 */
	public int getDuration() {
		return getInteger("duration");
	}

	/**
	 *  @param duration Duration (s)
	 */
	public SoiWaypoint setDuration(int duration) {
		values.put("duration", duration);
		return this;
	}

}
