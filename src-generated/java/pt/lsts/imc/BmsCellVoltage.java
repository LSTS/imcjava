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
 *  IMC Message BMS Cell Voltage (2043)<br/>
 *  Voltage of a single battery cell, used as a sub-message inside BmsData.<br/>
 */

public class BmsCellVoltage extends IMCMessage {

	public static final int ID_STATIC = 2043;

	public BmsCellVoltage() {
		super(ID_STATIC);
	}

	public BmsCellVoltage(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BmsCellVoltage(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static BmsCellVoltage create(Object... values) {
		BmsCellVoltage m = new BmsCellVoltage();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static BmsCellVoltage clone(IMCMessage msg) throws Exception {

		BmsCellVoltage m = new BmsCellVoltage();
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

	public BmsCellVoltage(short cell_number, float voltage) {
		super(ID_STATIC);
		setCellNumber(cell_number);
		setVoltage(voltage);
	}

	/**
	 *  @return Cell Number - uint8_t
	 */
	public short getCellNumber() {
		return (short) getInteger("cell_number");
	}

	/**
	 *  @param cell_number Cell Number
	 */
	public BmsCellVoltage setCellNumber(short cell_number) {
		values.put("cell_number", cell_number);
		return this;
	}

	/**
	 *  @return Voltage (V) - fp32_t
	 */
	public double getVoltage() {
		return getDouble("voltage");
	}

	/**
	 *  @param voltage Voltage (V)
	 */
	public BmsCellVoltage setVoltage(double voltage) {
		values.put("voltage", voltage);
		return this;
	}

}
