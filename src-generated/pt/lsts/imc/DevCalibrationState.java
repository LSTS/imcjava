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
 *  IMC Message Device Calibration State (13)<br/>
 *  State of the calibration procedure.<br/>
 */

public class DevCalibrationState extends IMCMessage {

	public static final short DCS_PREVIOUS_NOT_SUPPORTED = 0x01;
	public static final short DCS_NEXT_NOT_SUPPORTED = 0x02;
	public static final short DCS_WAITING_CONTROL = 0x04;
	public static final short DCS_ERROR = 0x08;
	public static final short DCS_COMPLETED = 0x10;

	public static final int ID_STATIC = 13;

	public DevCalibrationState() {
		super(ID_STATIC);
	}

	public DevCalibrationState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DevCalibrationState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DevCalibrationState create(Object... values) {
		DevCalibrationState m = new DevCalibrationState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DevCalibrationState clone(IMCMessage msg) throws Exception {

		DevCalibrationState m = new DevCalibrationState();
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

	public DevCalibrationState(short total_steps, short step_number, String step, short flags) {
		super(ID_STATIC);
		setTotalSteps(total_steps);
		setStepNumber(step_number);
		if (step != null)
			setStep(step);
		setFlags(flags);
	}

	/**
	 *  @return Total Steps - uint8_t
	 */
	public short getTotalSteps() {
		return (short) getInteger("total_steps");
	}

	/**
	 *  @param total_steps Total Steps
	 */
	public DevCalibrationState setTotalSteps(short total_steps) {
		values.put("total_steps", total_steps);
		return this;
	}

	/**
	 *  @return Current Step Number - uint8_t
	 */
	public short getStepNumber() {
		return (short) getInteger("step_number");
	}

	/**
	 *  @param step_number Current Step Number
	 */
	public DevCalibrationState setStepNumber(short step_number) {
		values.put("step_number", step_number);
		return this;
	}

	/**
	 *  @return Description - plaintext
	 */
	public String getStep() {
		return getString("step");
	}

	/**
	 *  @param step Description
	 */
	public DevCalibrationState setStep(String step) {
		values.put("step", step);
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
	public DevCalibrationState setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

}
