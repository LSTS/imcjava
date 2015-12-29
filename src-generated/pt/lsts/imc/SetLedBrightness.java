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
 *  IMC Message Set LED Brightness (314)<br/>
 *  Control the brightness of an LED (Light-Emitting Diode). The<br/>
 *  recipient of this message shall set the intensity of the LED to<br/>
 *  the desired 'value' and reply with 'LedBrightness'.<br/>
 */

public class SetLedBrightness extends IMCMessage {

	public static final int ID_STATIC = 314;

	public SetLedBrightness() {
		super(ID_STATIC);
	}

	public SetLedBrightness(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SetLedBrightness(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SetLedBrightness create(Object... values) {
		SetLedBrightness m = new SetLedBrightness();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SetLedBrightness clone(IMCMessage msg) throws Exception {

		SetLedBrightness m = new SetLedBrightness();
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

	public SetLedBrightness(String name, short value) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		setValue(value);
	}

	/**
	 *  @return Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Name
	 */
	public SetLedBrightness setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Value - uint8_t
	 */
	public short getValue() {
		return (short) getInteger("value");
	}

	/**
	 *  @param value Value
	 */
	public SetLedBrightness setValue(short value) {
		values.put("value", value);
		return this;
	}

}
