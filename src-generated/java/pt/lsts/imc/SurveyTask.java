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
 *  IMC Message Survey Task (3101)<br/>
 *  This message is used to describe an area surveying task.<br/>
 */

public class SurveyTask extends TaskAdminArgs {

	public static final int ID_STATIC = 3101;

	public SurveyTask() {
		super(ID_STATIC);
	}

	public SurveyTask(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SurveyTask(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SurveyTask create(Object... values) {
		SurveyTask m = new SurveyTask();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SurveyTask clone(IMCMessage msg) throws Exception {

		SurveyTask m = new SurveyTask();
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

	public SurveyTask(int task_id, int feature_id, SensorType sensor, float resolution, double deadline) {
		super(ID_STATIC);
		setTaskId(task_id);
		setFeatureId(feature_id);
		setSensor(sensor);
		setResolution(resolution);
		setDeadline(deadline);
	}

	/**
	 *  @return Task Identifier - uint16_t
	 */
	public int getTaskId() {
		return getInteger("task_id");
	}

	/**
	 *  @param task_id Task Identifier
	 */
	public SurveyTask setTaskId(int task_id) {
		values.put("task_id", task_id);
		return this;
	}

	/**
	 *  @return Geo Feature Identifier - uint16_t
	 */
	public int getFeatureId() {
		return getInteger("feature_id");
	}

	/**
	 *  @param feature_id Geo Feature Identifier
	 */
	public SurveyTask setFeatureId(int feature_id) {
		values.put("feature_id", feature_id);
		return this;
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
	public SurveyTask setSensor(SensorType sensor) {
		values.put("sensor", sensor.value());
		return this;
	}

	/**
	 *  @param sensor Sensor (as a String)
	 */
	public SurveyTask setSensorStr(String sensor) {
		setValue("sensor", sensor);
		return this;
	}

	/**
	 *  @param sensor Sensor (integer value)
	 */
	public SurveyTask setSensorVal(short sensor) {
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
	public SurveyTask setResolution(double resolution) {
		values.put("resolution", resolution);
		return this;
	}

	/**
	 *  @return Deadline (s) - fp64_t
	 */
	public double getDeadline() {
		return getDouble("deadline");
	}

	/**
	 *  @param deadline Deadline (s)
	 */
	public SurveyTask setDeadline(double deadline) {
		values.put("deadline", deadline);
		return this;
	}

}
