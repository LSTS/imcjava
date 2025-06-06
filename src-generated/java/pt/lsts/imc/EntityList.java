/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Entity List (5)<br/>
 *  This message describes the names and identification numbers of<br/>
 *  all entities in the system.<br/>
 */

public class EntityList extends IMCMessage {

	public enum OP {
		REPORT(0),
		QUERY(1),
		RELOAD(2);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 5;

	public EntityList() {
		super(ID_STATIC);
	}

	public EntityList(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntityList(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EntityList create(Object... values) {
		EntityList m = new EntityList();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EntityList clone(IMCMessage msg) throws Exception {

		EntityList m = new EntityList();
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

	public EntityList(OP op, String list) {
		super(ID_STATIC);
		setOp(op);
		if (list != null)
			setList(list);
	}

	/**
	 *  @return operation (enumerated) - uint8_t
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
	 *  @param op operation (enumerated)
	 */
	public EntityList setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op operation (as a String)
	 */
	public EntityList setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op operation (integer value)
	 */
	public EntityList setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return list (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getList() {
		return getTupleList("list");
	}

	/**
	 *  @param list list (tuplelist)
	 */
	public EntityList setList(java.util.LinkedHashMap<String, ?> list) {
		String val = encodeTupleList(list);
		values.put("list", val);
		return this;
	}

	public EntityList setList(String list) {
		values.put("list", list);
		return this;
	}

}
