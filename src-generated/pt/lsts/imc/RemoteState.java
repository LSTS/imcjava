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
 *  IMC Message Remote State (750)<br/>
 *  State summary for a remote vehicle.<br/>
 */

public class RemoteState extends IMCMessage {

	public static final int ID_STATIC = 750;

	public RemoteState() {
		super(ID_STATIC);
	}

	public RemoteState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RemoteState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static RemoteState create(Object... values) {
		RemoteState m = new RemoteState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static RemoteState clone(IMCMessage msg) throws Exception {

		RemoteState m = new RemoteState();
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

	public RemoteState(float lat, float lon, short depth, float speed, float psi) {
		super(ID_STATIC);
		setLat(lat);
		setLon(lon);
		setDepth(depth);
		setSpeed(speed);
		setPsi(psi);
	}

	/**
	 *  @return Latitude WGS-84 (rad) - fp32_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude WGS-84 (rad)
	 */
	public RemoteState setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude WGS-84 (rad) - fp32_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude WGS-84 (rad)
	 */
	public RemoteState setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Depth (m) - uint8_t
	 */
	public short getDepth() {
		return (short) getInteger("depth");
	}

	/**
	 *  @param depth Depth (m)
	 */
	public RemoteState setDepth(short depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Speed (m/s) - fp32_t
	 */
	public double getSpeed() {
		return getDouble("speed");
	}

	/**
	 *  @param speed Speed (m/s)
	 */
	public RemoteState setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @return Heading (rad) - fp32_t
	 */
	public double getPsi() {
		return getDouble("psi");
	}

	/**
	 *  @param psi Heading (rad)
	 */
	public RemoteState setPsi(double psi) {
		values.put("psi", psi);
		return this;
	}

}
