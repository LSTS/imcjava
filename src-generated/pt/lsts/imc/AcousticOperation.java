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
 *  IMC Message Acoustic Operation (211)<br/>
 *  Acoustic operation.<br/>
 */

public class AcousticOperation extends IMCMessage {

	public enum OP {
		ABORT(0),
		ABORT_IP(1),
		ABORT_TIMEOUT(2),
		ABORT_ACKED(3),
		RANGE(4),
		RANGE_IP(5),
		RANGE_TIMEOUT(6),
		RANGE_RECVED(7),
		BUSY(8),
		UNSUPPORTED(9),
		NO_TXD(10),
		MSG(11),
		MSG_QUEUED(12),
		MSG_IP(13),
		MSG_DONE(14),
		MSG_FAILURE(15),
		MSG_SHORT(16);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 211;

	public AcousticOperation() {
		super(ID_STATIC);
	}

	public AcousticOperation(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AcousticOperation(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AcousticOperation create(Object... values) {
		AcousticOperation m = new AcousticOperation();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AcousticOperation clone(IMCMessage msg) throws Exception {

		AcousticOperation m = new AcousticOperation();
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

	public AcousticOperation(OP op, String system, float range, IMCMessage msg) {
		super(ID_STATIC);
		setOp(op);
		if (system != null)
			setSystem(system);
		setRange(range);
		if (msg != null)
			setMsg(msg);
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
	public AcousticOperation setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public AcousticOperation setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public AcousticOperation setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return System - plaintext
	 */
	public String getSystem() {
		return getString("system");
	}

	/**
	 *  @param system System
	 */
	public AcousticOperation setSystem(String system) {
		values.put("system", system);
		return this;
	}

	/**
	 *  @return Range (m) - fp32_t
	 */
	public double getRange() {
		return getDouble("range");
	}

	/**
	 *  @param range Range (m)
	 */
	public AcousticOperation setRange(double range) {
		values.put("range", range);
		return this;
	}

	/**
	 *  @return Message To Send - message
	 */
	public IMCMessage getMsg() {
		return getMessage("msg");
	}

	public <T extends IMCMessage> T getMsg(Class<T> clazz) throws Exception {
		return getMessage(clazz, "msg");
	}

	/**
	 *  @param msg Message To Send
	 */
	public AcousticOperation setMsg(IMCMessage msg) {
		values.put("msg", msg);
		return this;
	}

}
