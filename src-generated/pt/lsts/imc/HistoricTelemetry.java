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
 *  IMC Message Historic Telemetry (108)<br/>
 *  This message is used to store historic (transmitted afterwards) telemetry information.<br/>
 */

public class HistoricTelemetry extends IMCMessage {

	public static final int ID_STATIC = 108;

	public HistoricTelemetry() {
		super(ID_STATIC);
	}

	public HistoricTelemetry(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistoricTelemetry(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static HistoricTelemetry create(Object... values) {
		HistoricTelemetry m = new HistoricTelemetry();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static HistoricTelemetry clone(IMCMessage msg) throws Exception {

		HistoricTelemetry m = new HistoricTelemetry();
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

	public HistoricTelemetry(float altitude, int roll, int pitch, int yaw, short speed) {
		super(ID_STATIC);
		setAltitude(altitude);
		setRoll(roll);
		setPitch(pitch);
		setYaw(yaw);
		setSpeed(speed);
	}

	/**
	 *  @return Altitude (m) - fp32_t
	 */
	public double getAltitude() {
		return getDouble("altitude");
	}

	/**
	 *  @param altitude Altitude (m)
	 */
	public HistoricTelemetry setAltitude(double altitude) {
		values.put("altitude", altitude);
		return this;
	}

	/**
	 *  @return Roll - uint16_t
	 */
	public int getRoll() {
		return getInteger("roll");
	}

	/**
	 *  @param roll Roll
	 */
	public HistoricTelemetry setRoll(int roll) {
		values.put("roll", roll);
		return this;
	}

	/**
	 *  @return Pitch - uint16_t
	 */
	public int getPitch() {
		return getInteger("pitch");
	}

	/**
	 *  @param pitch Pitch
	 */
	public HistoricTelemetry setPitch(int pitch) {
		values.put("pitch", pitch);
		return this;
	}

	/**
	 *  @return Yaw - uint16_t
	 */
	public int getYaw() {
		return getInteger("yaw");
	}

	/**
	 *  @param yaw Yaw
	 */
	public HistoricTelemetry setYaw(int yaw) {
		values.put("yaw", yaw);
		return this;
	}

	/**
	 *  @return Speed (dm) - int16_t
	 */
	public short getSpeed() {
		return (short) getInteger("speed");
	}

	/**
	 *  @param speed Speed (dm)
	 */
	public HistoricTelemetry setSpeed(short speed) {
		values.put("speed", speed);
		return this;
	}

}
