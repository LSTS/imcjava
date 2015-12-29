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
 *  IMC Message PWM (316)<br/>
 *  Properties of a PWM signal channel.<br/>
 */

public class PWM extends IMCMessage {

	public static final int ID_STATIC = 316;

	public PWM() {
		super(ID_STATIC);
	}

	public PWM(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PWM(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PWM create(Object... values) {
		PWM m = new PWM();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PWM clone(IMCMessage msg) throws Exception {

		PWM m = new PWM();
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

	public PWM(short id, long period, long duty_cycle) {
		super(ID_STATIC);
		setId(id);
		setPeriod(period);
		setDutyCycle(duty_cycle);
	}

	/**
	 *  @return Channel Identifier - uint8_t
	 */
	public short getId() {
		return (short) getInteger("id");
	}

	/**
	 *  @param id Channel Identifier
	 */
	public PWM setId(short id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Period (µs) - uint32_t
	 */
	public long getPeriod() {
		return getLong("period");
	}

	/**
	 *  @param period Period (µs)
	 */
	public PWM setPeriod(long period) {
		values.put("period", period);
		return this;
	}

	/**
	 *  @return Duty Cycle (µs) - uint32_t
	 */
	public long getDutyCycle() {
		return getLong("duty_cycle");
	}

	/**
	 *  @param duty_cycle Duty Cycle (µs)
	 */
	public PWM setDutyCycle(long duty_cycle) {
		values.put("duty_cycle", duty_cycle);
		return this;
	}

}
