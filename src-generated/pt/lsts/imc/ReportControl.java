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
 *  IMC Message Report Control (513)<br/>
 *  This message is sent to trigger reports to a destination system.<br/>
 */

public class ReportControl extends IMCMessage {

	public static final short CI_ACOUSTIC = 0x01;
	public static final short CI_SATELLITE = 0x02;
	public static final short CI_GSM = 0x04;
	public static final short CI_MOBILE = 0x08;

	public enum OP {
		REQUEST_START(0),
		STARTED(1),
		REQUEST_STOP(2),
		STOPPED(3),
		REQUEST_REPORT(4),
		REPORT_SENT(5);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 513;

	public ReportControl() {
		super(ID_STATIC);
	}

	public ReportControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReportControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ReportControl create(Object... values) {
		ReportControl m = new ReportControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ReportControl clone(IMCMessage msg) throws Exception {

		ReportControl m = new ReportControl();
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

	public ReportControl(OP op, short comm_interface, int period, String sys_dst) {
		super(ID_STATIC);
		setOp(op);
		setCommInterface(comm_interface);
		setPeriod(period);
		if (sys_dst != null)
			setSysDst(sys_dst);
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
	public ReportControl setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public ReportControl setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public ReportControl setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Communication Interface (bitfield) - uint8_t
	 */
	public short getCommInterface() {
		return (short) getInteger("comm_interface");
	}

	/**
	 *  @param comm_interface Communication Interface (bitfield)
	 */
	public ReportControl setCommInterface(short comm_interface) {
		values.put("comm_interface", comm_interface);
		return this;
	}

	/**
	 *  @return Period (s) - uint16_t
	 */
	public int getPeriod() {
		return getInteger("period");
	}

	/**
	 *  @param period Period (s)
	 */
	public ReportControl setPeriod(int period) {
		values.put("period", period);
		return this;
	}

	/**
	 *  @return Destination System - plaintext
	 */
	public String getSysDst() {
		return getString("sys_dst");
	}

	/**
	 *  @param sys_dst Destination System
	 */
	public ReportControl setSysDst(String sys_dst) {
		values.put("sys_dst", sys_dst);
		return this;
	}

}
