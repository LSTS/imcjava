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

import pt.lsts.imc.def.SensorType;

/**
 *  IMC Message Point Survey Capability (3011)<br/>
 *  This message describes an area surveying capability.<br/>
 */

public class CapabilityPointSurvey extends VehicleCapability {

	public static final int ID_STATIC = 3011;

	public CapabilityPointSurvey() {
		super(ID_STATIC);
	}

	public CapabilityPointSurvey(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CapabilityPointSurvey(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CapabilityPointSurvey create(Object... values) {
		CapabilityPointSurvey m = new CapabilityPointSurvey();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CapabilityPointSurvey clone(IMCMessage msg) throws Exception {

		CapabilityPointSurvey m = new CapabilityPointSurvey();
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

	public CapabilityPointSurvey(SensorType Sensor, float resolution, float duration) {
		super(ID_STATIC);
		setSensor(Sensor);
		setResolution(resolution);
		setDuration(duration);
	}

	/**
	 *  @return Sensor (enumerated) - uint8_t
	 */
	public SensorType getSensor() {
		try {
			SensorType o = SensorType.valueOf(getMessageType().getFieldPossibleValues("Sensor").get(getLong("Sensor")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSensorStr() {
		return getString("Sensor");
	}

	public short getSensorVal() {
		return (short) getInteger("Sensor");
	}

	/**
	 *  @param Sensor Sensor (enumerated)
	 */
	public CapabilityPointSurvey setSensor(SensorType Sensor) {
		values.put("Sensor", Sensor.value());
		return this;
	}

	/**
	 *  @param Sensor Sensor (as a String)
	 */
	public CapabilityPointSurvey setSensorStr(String Sensor) {
		setValue("Sensor", Sensor);
		return this;
	}

	/**
	 *  @param Sensor Sensor (integer value)
	 */
	public CapabilityPointSurvey setSensorVal(short Sensor) {
		setValue("Sensor", Sensor);
		return this;
	}

	/**
	 *  @return Resolution (px/m²) - fp32_t
	 */
	public double getResolution() {
		return getDouble("resolution");
	}

	/**
	 *  @param resolution Resolution (px/m²)
	 */
	public CapabilityPointSurvey setResolution(double resolution) {
		values.put("resolution", resolution);
		return this;
	}

	/**
	 *  @return Duration (s) - fp32_t
	 */
	public double getDuration() {
		return getDouble("duration");
	}

	/**
	 *  @param duration Duration (s)
	 */
	public CapabilityPointSurvey setDuration(double duration) {
		values.put("duration", duration);
		return this;
	}

}
