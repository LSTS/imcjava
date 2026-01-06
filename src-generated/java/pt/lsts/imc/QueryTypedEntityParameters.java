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
 *  IMC Message Query Typed Entity Parameters (2016)<br/>
 *  This message can be used to query/report the entities and respective parameters in the system<br/>
 */

public class QueryTypedEntityParameters extends IMCMessage {

	public enum OP {
		REQUEST(0),
		REPLY(1);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 2016;

	public QueryTypedEntityParameters() {
		super(ID_STATIC);
	}

	public QueryTypedEntityParameters(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public QueryTypedEntityParameters(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static QueryTypedEntityParameters create(Object... values) {
		QueryTypedEntityParameters m = new QueryTypedEntityParameters();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static QueryTypedEntityParameters clone(IMCMessage msg) throws Exception {

		QueryTypedEntityParameters m = new QueryTypedEntityParameters();
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

	public QueryTypedEntityParameters(OP op, long request_id, String entity_name, java.util.Collection<TypedEntityParametersOptions> parameters) {
		super(ID_STATIC);
		setOp(op);
		setRequestId(request_id);
		if (entity_name != null)
			setEntityName(entity_name);
		if (parameters != null)
			setParameters(parameters);
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
	public QueryTypedEntityParameters setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public QueryTypedEntityParameters setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public QueryTypedEntityParameters setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Request identitier - uint32_t
	 */
	public long getRequestId() {
		return getLong("request_id");
	}

	/**
	 *  @param request_id Request identitier
	 */
	public QueryTypedEntityParameters setRequestId(long request_id) {
		values.put("request_id", request_id);
		return this;
	}

	/**
	 *  @return Entity Name - plaintext
	 */
	public String getEntityName() {
		return getString("entity_name");
	}

	/**
	 *  @param entity_name Entity Name
	 */
	public QueryTypedEntityParameters setEntityName(String entity_name) {
		values.put("entity_name", entity_name);
		return this;
	}

	/**
	 *  @return Parameters - message-list
	 */
	public java.util.Vector<TypedEntityParametersOptions> getParameters() {
		try {
			return getMessageList("parameters", TypedEntityParametersOptions.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param parameters Parameters
	 */
	public QueryTypedEntityParameters setParameters(java.util.Collection<TypedEntityParametersOptions> parameters) {
		values.put("parameters", parameters);
		return this;
	}

}
