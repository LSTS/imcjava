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


/**
 *  IMC Message Sonar Pulse (2013)<br/>
 *  Information regarding a sent/received Sonar pulse.<br/>
 */

public class SonarPulse extends IMCMessage {

	public static final int ID_STATIC = 2013;

	public SonarPulse() {
		super(ID_STATIC);
	}

	public SonarPulse(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SonarPulse(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SonarPulse create(Object... values) {
		SonarPulse m = new SonarPulse();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SonarPulse clone(IMCMessage msg) throws Exception {

		SonarPulse m = new SonarPulse();
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

	public SonarPulse(int frequency, int pulse_length, int time_delay, int simulated_speed) {
		super(ID_STATIC);
		setFrequency(frequency);
		setPulseLength(pulse_length);
		setTimeDelay(time_delay);
		setSimulatedSpeed(simulated_speed);
	}

	/**
	 *  @return Frequency (hz) - int32_t
	 */
	public int getFrequency() {
		return getInteger("frequency");
	}

	/**
	 *  @param frequency Frequency (hz)
	 */
	public SonarPulse setFrequency(int frequency) {
		values.put("frequency", frequency);
		return this;
	}

	/**
	 *  @return Pulse Length (ms) - int32_t
	 */
	public int getPulseLength() {
		return getInteger("pulse_length");
	}

	/**
	 *  @param pulse_length Pulse Length (ms)
	 */
	public SonarPulse setPulseLength(int pulse_length) {
		values.put("pulse_length", pulse_length);
		return this;
	}

	/**
	 *  @return Time Delay (ms) - int32_t
	 */
	public int getTimeDelay() {
		return getInteger("time_delay");
	}

	/**
	 *  @param time_delay Time Delay (ms)
	 */
	public SonarPulse setTimeDelay(int time_delay) {
		values.put("time_delay", time_delay);
		return this;
	}

	/**
	 *  @return Simulated Speed (m/s) - int32_t
	 */
	public int getSimulatedSpeed() {
		return getInteger("simulated_speed");
	}

	/**
	 *  @param simulated_speed Simulated Speed (m/s)
	 */
	public SonarPulse setSimulatedSpeed(int simulated_speed) {
		values.put("simulated_speed", simulated_speed);
		return this;
	}

}
