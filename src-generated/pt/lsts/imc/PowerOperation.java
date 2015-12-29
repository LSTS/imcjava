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
 *  IMC Message Power Operation (308)<br/>
 *  This message allows controlling the system's power lines.<br/>
 */

public class PowerOperation extends IMCMessage {

	public enum OP {
		PWR_DOWN(0),
		PWR_DOWN_IP(1),
		PWR_DOWN_ABORTED(2),
		SCHED_PWR_DOWN(3),
		PWR_UP(4),
		PWR_UP_IP(5),
		SCHED_PWR_UP(6);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 308;

	public PowerOperation() {
		super(ID_STATIC);
	}

	public PowerOperation(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PowerOperation(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PowerOperation create(Object... values) {
		PowerOperation m = new PowerOperation();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PowerOperation clone(IMCMessage msg) throws Exception {

		PowerOperation m = new PowerOperation();
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

	public PowerOperation(OP op, float time_remain, double sched_time) {
		super(ID_STATIC);
		setOp(op);
		setTimeRemain(time_remain);
		setSchedTime(sched_time);
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
	public PowerOperation setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public PowerOperation setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public PowerOperation setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Time Remaining (s) - fp32_t
	 */
	public double getTimeRemain() {
		return getDouble("time_remain");
	}

	/**
	 *  @param time_remain Time Remaining (s)
	 */
	public PowerOperation setTimeRemain(double time_remain) {
		values.put("time_remain", time_remain);
		return this;
	}

	/**
	 *  @return Scheduled Time (s) - fp64_t
	 */
	public double getSchedTime() {
		return getDouble("sched_time");
	}

	/**
	 *  @param sched_time Scheduled Time (s)
	 */
	public PowerOperation setSchedTime(double sched_time) {
		values.put("sched_time", sched_time);
		return this;
	}

}
