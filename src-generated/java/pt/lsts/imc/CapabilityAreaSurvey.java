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
 *  IMC Message Area Survey Capability (3010)<br/>
 *  This message describes an area surveying capability.<br/>
 */

public class CapabilityAreaSurvey extends VehicleCapability {

	public static final int ID_STATIC = 3010;

	public CapabilityAreaSurvey() {
		super(ID_STATIC);
	}

	public CapabilityAreaSurvey(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CapabilityAreaSurvey(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CapabilityAreaSurvey create(Object... values) {
		CapabilityAreaSurvey m = new CapabilityAreaSurvey();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CapabilityAreaSurvey clone(IMCMessage msg) throws Exception {

		CapabilityAreaSurvey m = new CapabilityAreaSurvey();
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

	public CapabilityAreaSurvey(SensorType sensor, float resolution, float res_bathym_factor, float cov_rate, float cov_bathym_factor) {
		super(ID_STATIC);
		setSensor(sensor);
		setResolution(resolution);
		setResBathymFactor(res_bathym_factor);
		setCovRate(cov_rate);
		setCovBathymFactor(cov_bathym_factor);
	}

	/**
	 *  @return Sensor (enumerated) - uint8_t
	 */
	public SensorType getSensor() {
		try {
			SensorType o = SensorType.valueOf(getMessageType().getFieldPossibleValues("sensor").get(getLong("sensor")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSensorStr() {
		return getString("sensor");
	}

	public short getSensorVal() {
		return (short) getInteger("sensor");
	}

	/**
	 *  @param sensor Sensor (enumerated)
	 */
	public CapabilityAreaSurvey setSensor(SensorType sensor) {
		values.put("sensor", sensor.value());
		return this;
	}

	/**
	 *  @param sensor Sensor (as a String)
	 */
	public CapabilityAreaSurvey setSensorStr(String sensor) {
		setValue("sensor", sensor);
		return this;
	}

	/**
	 *  @param sensor Sensor (integer value)
	 */
	public CapabilityAreaSurvey setSensorVal(short sensor) {
		setValue("sensor", sensor);
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
	public CapabilityAreaSurvey setResolution(double resolution) {
		values.put("resolution", resolution);
		return this;
	}

	/**
	 *  @return Resolution Bathymetry Factor - fp32_t
	 */
	public double getResBathymFactor() {
		return getDouble("res_bathym_factor");
	}

	/**
	 *  @param res_bathym_factor Resolution Bathymetry Factor
	 */
	public CapabilityAreaSurvey setResBathymFactor(double res_bathym_factor) {
		values.put("res_bathym_factor", res_bathym_factor);
		return this;
	}

	/**
	 *  @return Coverage Rate (m²/s) - fp32_t
	 */
	public double getCovRate() {
		return getDouble("cov_rate");
	}

	/**
	 *  @param cov_rate Coverage Rate (m²/s)
	 */
	public CapabilityAreaSurvey setCovRate(double cov_rate) {
		values.put("cov_rate", cov_rate);
		return this;
	}

	/**
	 *  @return Coverage Bathymetry Factor - fp32_t
	 */
	public double getCovBathymFactor() {
		return getDouble("cov_bathym_factor");
	}

	/**
	 *  @param cov_bathym_factor Coverage Bathymetry Factor
	 */
	public CapabilityAreaSurvey setCovBathymFactor(double cov_bathym_factor) {
		values.put("cov_bathym_factor", cov_bathym_factor);
		return this;
	}

}
