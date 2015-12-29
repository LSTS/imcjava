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
 *  IMC Message Extended Receive Signal Strength Information (183)<br/>
 *  Measure of the RSSI by a networking device.<br/>
 *  Indicates the gain or loss in the signal strenght due to the transmission<br/>
 *  and reception equipment and the transmission medium and distance.<br/>
 */

public class ExtendedRSSI extends IMCMessage {

	public enum UNITS {
		DB(0),
		PERCENTAGE(1);

		protected long value;

		public long value() {
			return value;
		}

		UNITS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 183;

	public ExtendedRSSI() {
		super(ID_STATIC);
	}

	public ExtendedRSSI(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ExtendedRSSI(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ExtendedRSSI create(Object... values) {
		ExtendedRSSI m = new ExtendedRSSI();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ExtendedRSSI clone(IMCMessage msg) throws Exception {

		ExtendedRSSI m = new ExtendedRSSI();
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

	public ExtendedRSSI(float value, UNITS units) {
		super(ID_STATIC);
		setValue(value);
		setUnits(units);
	}

	/**
	 *  @return Value - fp32_t
	 */
	public double getValue() {
		return getDouble("value");
	}

	/**
	 *  @param value Value
	 */
	public ExtendedRSSI setValue(double value) {
		values.put("value", value);
		return this;
	}

	/**
	 *  @return RSSI Units (enumerated) - uint8_t
	 */
	public UNITS getUnits() {
		try {
			UNITS o = UNITS.valueOf(getMessageType().getFieldPossibleValues("units").get(getLong("units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getUnitsStr() {
		return getString("units");
	}

	public short getUnitsVal() {
		return (short) getInteger("units");
	}

	/**
	 *  @param units RSSI Units (enumerated)
	 */
	public ExtendedRSSI setUnits(UNITS units) {
		values.put("units", units.value());
		return this;
	}

	/**
	 *  @param units RSSI Units (as a String)
	 */
	public ExtendedRSSI setUnitsStr(String units) {
		setValue("units", units);
		return this;
	}

	/**
	 *  @param units RSSI Units (integer value)
	 */
	public ExtendedRSSI setUnitsVal(short units) {
		setValue("units", units);
		return this;
	}

}
