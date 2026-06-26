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
 *  IMC Message Query BMS Data (2041)<br/>
 *  Request detailed battery management system data from a vehicle.<br/>
 *  The vehicle replies with one or more BmsData messages depending<br/>
 *  on the requested operation and pack index.  If pack_idx is 0xFF<br/>
 *  the vehicle returns data for every detected battery pack.<br/>
 *  The operation field selects which class of data is returned,<br/>
 *  allowing future extension without protocol changes.<br/>
 */

public class QueryBmsData extends IMCMessage {

	public enum OP {
		READ_TELEMETRY(0),
		READ_CELL_VOLTAGES(1),
		READ_STATUS(2),
		READ_ALL(3),
		READ_RAW(4),
		WRITE_RAW(5),
		SET_FET(6);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 2041;

	public QueryBmsData() {
		super(ID_STATIC);
	}

	public QueryBmsData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public QueryBmsData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static QueryBmsData create(Object... values) {
		QueryBmsData m = new QueryBmsData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static QueryBmsData clone(IMCMessage msg) throws Exception {

		QueryBmsData m = new QueryBmsData();
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

	public QueryBmsData(OP op, short pack_idx, short sbs_register, byte[] data) {
		super(ID_STATIC);
		setOp(op);
		setPackIdx(pack_idx);
		setSbsRegister(sbs_register);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Operation (enumerated) - uint8_t
	 */
	public OP getOp() {
		try {
			OP o = OP.valueOf(getMessageType().getFieldPossibleValues("op").get(getLong("op")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getOpStr() {
		return getString("op");
	}

	public short getOpVal() {
		return (short) getInteger("op");
	}

	/**
	 *  @param op Operation (enumerated)
	 */
	public QueryBmsData setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public QueryBmsData setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public QueryBmsData setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Pack Index - uint8_t
	 */
	public short getPackIdx() {
		return (short) getInteger("pack_idx");
	}

	/**
	 *  @param pack_idx Pack Index
	 */
	public QueryBmsData setPackIdx(short pack_idx) {
		values.put("pack_idx", pack_idx);
		return this;
	}

	/**
	 *  @return SBS Register - uint8_t
	 */
	public short getSbsRegister() {
		return (short) getInteger("sbs_register");
	}

	/**
	 *  @param sbs_register SBS Register
	 */
	public QueryBmsData setSbsRegister(short sbs_register) {
		values.put("sbs_register", sbs_register);
		return this;
	}

	/**
	 *  @return Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Data
	 */
	public QueryBmsData setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
