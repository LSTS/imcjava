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
 *  IMC Message Plan Generation (562)<br/>
 *  This message is used to order the generation of plans based on<br/>
 *  id and set of parameters.<br/>
 */

public class PlanGeneration extends IMCMessage {

	public enum CMD {
		GENERATE(0),
		EXECUTE(1);

		protected long value;

		public long value() {
			return value;
		}

		CMD(long value) {
			this.value = value;
		}
	}

	public enum OP {
		REQUEST(0),
		ERROR(1),
		SUCCESS(2);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 562;

	public PlanGeneration() {
		super(ID_STATIC);
	}

	public PlanGeneration(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanGeneration(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanGeneration create(Object... values) {
		PlanGeneration m = new PlanGeneration();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanGeneration clone(IMCMessage msg) throws Exception {

		PlanGeneration m = new PlanGeneration();
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

	public PlanGeneration(CMD cmd, OP op, String plan_id, String params) {
		super(ID_STATIC);
		setCmd(cmd);
		setOp(op);
		if (plan_id != null)
			setPlanId(plan_id);
		if (params != null)
			setParams(params);
	}

	/**
	 *  @return Command (enumerated) - uint8_t
	 */
	public CMD getCmd() {
		try {
			CMD o = CMD.valueOf(getMessageType().getFieldPossibleValues("cmd").get(getLong("cmd")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCmdStr() {
		return getString("cmd");
	}

	public short getCmdVal() {
		return (short) getInteger("cmd");
	}

	/**
	 *  @param cmd Command (enumerated)
	 */
	public PlanGeneration setCmd(CMD cmd) {
		values.put("cmd", cmd.value());
		return this;
	}

	/**
	 *  @param cmd Command (as a String)
	 */
	public PlanGeneration setCmdStr(String cmd) {
		setValue("cmd", cmd);
		return this;
	}

	/**
	 *  @param cmd Command (integer value)
	 */
	public PlanGeneration setCmdVal(short cmd) {
		setValue("cmd", cmd);
		return this;
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
	public PlanGeneration setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public PlanGeneration setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public PlanGeneration setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Plan Identifier - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan Identifier
	 */
	public PlanGeneration setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Parameters (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getParams() {
		return getTupleList("params");
	}

	/**
	 *  @param params Parameters (tuplelist)
	 */
	public PlanGeneration setParams(java.util.LinkedHashMap<String, ?> params) {
		String val = encodeTupleList(params);
		values.put("params", val);
		return this;
	}

	public PlanGeneration setParams(String params) {
		values.put("params", params);
		return this;
	}

}
