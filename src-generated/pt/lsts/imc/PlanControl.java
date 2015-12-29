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
 *  IMC Message Plan Control (559)<br/>
 *  Plan control request/reply.<br/>
 */

public class PlanControl extends IMCMessage {

	public static final int FLG_CALIBRATE = 0x0001;
	public static final int FLG_IGNORE_ERRORS = 0x0002;

	public enum TYPE {
		REQUEST(0),
		SUCCESS(1),
		FAILURE(2),
		IN_PROGRESS(3);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum OP {
		START(0),
		STOP(1),
		LOAD(2),
		GET(3);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 559;

	public PlanControl() {
		super(ID_STATIC);
	}

	public PlanControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanControl create(Object... values) {
		PlanControl m = new PlanControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanControl clone(IMCMessage msg) throws Exception {

		PlanControl m = new PlanControl();
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

	public PlanControl(TYPE type, OP op, int request_id, String plan_id, int flags, IMCMessage arg, String info) {
		super(ID_STATIC);
		setType(type);
		setOp(op);
		setRequestId(request_id);
		if (plan_id != null)
			setPlanId(plan_id);
		setFlags(flags);
		if (arg != null)
			setArg(arg);
		if (info != null)
			setInfo(info);
	}

	/**
	 *  @return Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Type (enumerated)
	 */
	public PlanControl setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public PlanControl setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public PlanControl setTypeVal(short type) {
		setValue("type", type);
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
	public PlanControl setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public PlanControl setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public PlanControl setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Request ID - uint16_t
	 */
	public int getRequestId() {
		return getInteger("request_id");
	}

	/**
	 *  @param request_id Request ID
	 */
	public PlanControl setRequestId(int request_id) {
		values.put("request_id", request_id);
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
	public PlanControl setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Flags (bitfield) - uint16_t
	 */
	public int getFlags() {
		return getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public PlanControl setFlags(int flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @return Request/Reply Argument - message
	 */
	public IMCMessage getArg() {
		return getMessage("arg");
	}

	public <T extends IMCMessage> T getArg(Class<T> clazz) throws Exception {
		return getMessage(clazz, "arg");
	}

	/**
	 *  @param arg Request/Reply Argument
	 */
	public PlanControl setArg(IMCMessage arg) {
		values.put("arg", arg);
		return this;
	}

	/**
	 *  @return Complementary Info - plaintext
	 */
	public String getInfo() {
		return getString("info");
	}

	/**
	 *  @param info Complementary Info
	 */
	public PlanControl setInfo(String info) {
		values.put("info", info);
		return this;
	}

}
