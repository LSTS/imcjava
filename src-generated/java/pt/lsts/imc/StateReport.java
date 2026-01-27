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
 *  IMC Message State Report (514)<br/>
 *  Concise representation of entire system state.<br/>
 */

public class StateReport extends IMCMessage {

	public static final int ID_STATIC = 514;

	public StateReport() {
		super(ID_STATIC);
	}

	public StateReport(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StateReport(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static StateReport create(Object... values) {
		StateReport m = new StateReport();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static StateReport clone(IMCMessage msg) throws Exception {

		StateReport m = new StateReport();
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

	public StateReport(long stime, float latitude, float longitude, int altitude, int depth, int heading, short speed, byte fuel, byte exec_state, int plan_checksum) {
		super(ID_STATIC);
		setStime(stime);
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
		setDepth(depth);
		setHeading(heading);
		setSpeed(speed);
		setFuel(fuel);
		setExecState(exec_state);
		setPlanChecksum(plan_checksum);
	}

	/**
	 *  @return Time Stamp (s) - uint32_t
	 */
	public long getStime() {
		return getLong("stime");
	}

	/**
	 *  @param stime Time Stamp (s)
	 */
	public StateReport setStime(long stime) {
		values.put("stime", stime);
		return this;
	}

	/**
	 *  @return Latitude (°) - fp32_t
	 */
	public double getLatitude() {
		return getDouble("latitude");
	}

	/**
	 *  @param latitude Latitude (°)
	 */
	public StateReport setLatitude(double latitude) {
		values.put("latitude", latitude);
		return this;
	}

	/**
	 *  @return Longitude (°) - fp32_t
	 */
	public double getLongitude() {
		return getDouble("longitude");
	}

	/**
	 *  @param longitude Longitude (°)
	 */
	public StateReport setLongitude(double longitude) {
		values.put("longitude", longitude);
		return this;
	}

	/**
	 *  @return Altitude (dm) - uint16_t
	 */
	public int getAltitude() {
		return getInteger("altitude");
	}

	/**
	 *  @param altitude Altitude (dm)
	 */
	public StateReport setAltitude(int altitude) {
		values.put("altitude", altitude);
		return this;
	}

	/**
	 *  @return Depth (dm) - uint16_t
	 */
	public int getDepth() {
		return getInteger("depth");
	}

	/**
	 *  @param depth Depth (dm)
	 */
	public StateReport setDepth(int depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Heading - uint16_t
	 */
	public int getHeading() {
		return getInteger("heading");
	}

	/**
	 *  @param heading Heading
	 */
	public StateReport setHeading(int heading) {
		values.put("heading", heading);
		return this;
	}

	/**
	 *  @return Speed (cm/s) - int16_t
	 */
	public short getSpeed() {
		return (short) getInteger("speed");
	}

	/**
	 *  @param speed Speed (cm/s)
	 */
	public StateReport setSpeed(short speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @return Fuel (%) - int8_t
	 */
	public byte getFuel() {
		return (byte) getInteger("fuel");
	}

	/**
	 *  @param fuel Fuel (%)
	 */
	public StateReport setFuel(byte fuel) {
		values.put("fuel", fuel);
		return this;
	}

	/**
	 *  @return Execution State (%) - int8_t
	 */
	public byte getExecState() {
		return (byte) getInteger("exec_state");
	}

	/**
	 *  @param exec_state Execution State (%)
	 */
	public StateReport setExecState(byte exec_state) {
		values.put("exec_state", exec_state);
		return this;
	}

	/**
	 *  @return Plan Checksum - uint16_t
	 */
	public int getPlanChecksum() {
		return getInteger("plan_checksum");
	}

	/**
	 *  @param plan_checksum Plan Checksum
	 */
	public StateReport setPlanChecksum(int plan_checksum) {
		values.put("plan_checksum", plan_checksum);
		return this;
	}

}
