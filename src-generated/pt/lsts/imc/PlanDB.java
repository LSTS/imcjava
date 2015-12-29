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
 *  IMC Message Plan DB (556)<br/>
 *  Request/reply to plan database.<br/>
 */

public class PlanDB extends IMCMessage {

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
		SET(0),
		DEL(1),
		GET(2),
		GET_INFO(3),
		CLEAR(4),
		GET_STATE(5),
		GET_DSTATE(6),
		BOOT(7);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 556;

	public PlanDB() {
		super(ID_STATIC);
	}

	public PlanDB(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanDB(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanDB create(Object... values) {
		PlanDB m = new PlanDB();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanDB clone(IMCMessage msg) throws Exception {

		PlanDB m = new PlanDB();
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

	public PlanDB(TYPE type, OP op, int request_id, String plan_id, IMCMessage arg, String info) {
		super(ID_STATIC);
		setType(type);
		setOp(op);
		setRequestId(request_id);
		if (plan_id != null)
			setPlanId(plan_id);
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
	public PlanDB setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public PlanDB setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public PlanDB setTypeVal(short type) {
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
	public PlanDB setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public PlanDB setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public PlanDB setOpVal(short op) {
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
	public PlanDB setRequestId(int request_id) {
		values.put("request_id", request_id);
		return this;
	}

	/**
	 *  @return Plan ID - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan ID
	 */
	public PlanDB setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Argument - message
	 */
	public IMCMessage getArg() {
		return getMessage("arg");
	}

	public <T extends IMCMessage> T getArg(Class<T> clazz) throws Exception {
		return getMessage(clazz, "arg");
	}

	/**
	 *  @param arg Argument
	 */
	public PlanDB setArg(IMCMessage arg) {
		values.put("arg", arg);
		return this;
	}

	/**
	 *  @return Complementary Information - plaintext
	 */
	public String getInfo() {
		return getString("info");
	}

	/**
	 *  @param info Complementary Information
	 */
	public PlanDB setInfo(String info) {
		values.put("info", info);
		return this;
	}

}
