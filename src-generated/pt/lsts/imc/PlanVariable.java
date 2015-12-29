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
 *  IMC Message Plan Variable (561)<br/>
 *  A plan variable.<br/>
 */

public class PlanVariable extends IMCMessage {

	public enum TYPE {
		BOOLEAN(0),
		NUMBER(1),
		TEXT(2),
		MESSAGE(3);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum ACCESS {
		INPUT(0),
		OUTPUT(1),
		LOCAL(2);

		protected long value;

		public long value() {
			return value;
		}

		ACCESS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 561;

	public PlanVariable() {
		super(ID_STATIC);
	}

	public PlanVariable(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanVariable(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanVariable create(Object... values) {
		PlanVariable m = new PlanVariable();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanVariable clone(IMCMessage msg) throws Exception {

		PlanVariable m = new PlanVariable();
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

	public PlanVariable(String name, String value, TYPE type, ACCESS access) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		if (value != null)
			setValue(value);
		setType(type);
		setAccess(access);
	}

	/**
	 *  @return Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Name
	 */
	public PlanVariable setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Value - plaintext
	 */
	public String getValue() {
		return getString("value");
	}

	/**
	 *  @param value Value
	 */
	public PlanVariable setValue(String value) {
		values.put("value", value);
		return this;
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
	public PlanVariable setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public PlanVariable setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public PlanVariable setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Access Type (enumerated) - uint8_t
	 */
	public ACCESS getAccess() {
		try {
			ACCESS o = ACCESS.valueOf(getMessageType().getFieldPossibleValues("access").get(getLong("access")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAccessStr() {
		return getString("access");
	}

	public short getAccessVal() {
		return (short) getInteger("access");
	}

	/**
	 *  @param access Access Type (enumerated)
	 */
	public PlanVariable setAccess(ACCESS access) {
		values.put("access", access.value());
		return this;
	}

	/**
	 *  @param access Access Type (as a String)
	 */
	public PlanVariable setAccessStr(String access) {
		setValue("access", access);
		return this;
	}

	/**
	 *  @param access Access Type (integer value)
	 */
	public PlanVariable setAccessVal(short access) {
		setValue("access", access);
		return this;
	}

}
