/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message BMS Register (2044)<br/>
 *  Raw SBS register value, used as a sub-message inside BmsData.<br/>
 *  Allows returning arbitrary register data in a structured way<br/>
 *  without requiring protocol changes for each new register.<br/>
 */

public class BmsRegister extends IMCMessage {

	public static final int ID_STATIC = 2044;

	public BmsRegister() {
		super(ID_STATIC);
	}

	public BmsRegister(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BmsRegister(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static BmsRegister create(Object... values) {
		BmsRegister m = new BmsRegister();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static BmsRegister clone(IMCMessage msg) throws Exception {

		BmsRegister m = new BmsRegister();
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

	public BmsRegister(short reg, byte[] value) {
		super(ID_STATIC);
		setReg(reg);
		if (value != null)
			setValue(value);
	}

	/**
	 *  @return Register - uint8_t
	 */
	public short getReg() {
		return (short) getInteger("reg");
	}

	/**
	 *  @param reg Register
	 */
	public BmsRegister setReg(short reg) {
		values.put("reg", reg);
		return this;
	}

	/**
	 *  @return Value - rawdata
	 */
	public byte[] getValue() {
		return getRawData("value");
	}

	/**
	 *  @param value Value
	 */
	public BmsRegister setValue(byte[] value) {
		values.put("value", value);
		return this;
	}

}
