/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message SADC Readings (907)<br/>
 *  Readings from SADC board.<br/>
 */

public class SadcReadings extends IMCMessage {

	public enum GAIN {
		X1(0),
		X10(1),
		X100(2);

		protected long value;

		public long value() {
			return value;
		}

		GAIN(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 907;

	public SadcReadings() {
		super(ID_STATIC);
	}

	public SadcReadings(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SadcReadings(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SadcReadings create(Object... values) {
		SadcReadings m = new SadcReadings();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SadcReadings clone(IMCMessage msg) throws Exception {

		SadcReadings m = new SadcReadings();
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

	public SadcReadings(byte channel, int value, GAIN gain) {
		super(ID_STATIC);
		setChannel(channel);
		setValue(value);
		setGain(gain);
	}

	/**
	 *  @return Channel - int8_t
	 */
	public byte getChannel() {
		return (byte) getInteger("channel");
	}

	/**
	 *  @param channel Channel
	 */
	public SadcReadings setChannel(byte channel) {
		values.put("channel", channel);
		return this;
	}

	/**
	 *  @return Value - int32_t
	 */
	public int getValue() {
		return getInteger("value");
	}

	/**
	 *  @param value Value
	 */
	public SadcReadings setValue(int value) {
		values.put("value", value);
		return this;
	}

	/**
	 *  @return Gain (enumerated) - uint8_t
	 */
	public GAIN getGain() {
		try {
			GAIN o = GAIN.valueOf(getMessageType().getFieldPossibleValues("gain").get(getLong("gain")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getGainStr() {
		return getString("gain");
	}

	public short getGainVal() {
		return (short) getInteger("gain");
	}

	/**
	 *  @param gain Gain (enumerated)
	 */
	public SadcReadings setGain(GAIN gain) {
		values.put("gain", gain.value());
		return this;
	}

	/**
	 *  @param gain Gain (as a String)
	 */
	public SadcReadings setGainStr(String gain) {
		setValue("gain", gain);
		return this;
	}

	/**
	 *  @param gain Gain (integer value)
	 */
	public SadcReadings setGainVal(short gain) {
		setValue("gain", gain);
		return this;
	}

}
