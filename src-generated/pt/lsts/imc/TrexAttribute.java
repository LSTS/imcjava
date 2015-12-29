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
 *  IMC Message TREX Attribute (656)<br/>
 */

public class TrexAttribute extends IMCMessage {

	public enum ATTR_TYPE {
		BOOL(1),
		INT(2),
		FLOAT(3),
		STRING(4),
		ENUM(5);

		protected long value;

		public long value() {
			return value;
		}

		ATTR_TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 656;

	public TrexAttribute() {
		super(ID_STATIC);
	}

	public TrexAttribute(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrexAttribute(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TrexAttribute create(Object... values) {
		TrexAttribute m = new TrexAttribute();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TrexAttribute clone(IMCMessage msg) throws Exception {

		TrexAttribute m = new TrexAttribute();
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

	public TrexAttribute(String name, ATTR_TYPE attr_type, String min, String max) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		setAttrType(attr_type);
		if (min != null)
			setMin(min);
		if (max != null)
			setMax(max);
	}

	/**
	 *  @return Attribute Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Attribute Name
	 */
	public TrexAttribute setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Attribute type (enumerated) - uint8_t
	 */
	public ATTR_TYPE getAttrType() {
		try {
			ATTR_TYPE o = ATTR_TYPE.valueOf(getMessageType().getFieldPossibleValues("attr_type").get(getLong("attr_type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAttrTypeStr() {
		return getString("attr_type");
	}

	public short getAttrTypeVal() {
		return (short) getInteger("attr_type");
	}

	/**
	 *  @param attr_type Attribute type (enumerated)
	 */
	public TrexAttribute setAttrType(ATTR_TYPE attr_type) {
		values.put("attr_type", attr_type.value());
		return this;
	}

	/**
	 *  @param attr_type Attribute type (as a String)
	 */
	public TrexAttribute setAttrTypeStr(String attr_type) {
		setValue("attr_type", attr_type);
		return this;
	}

	/**
	 *  @param attr_type Attribute type (integer value)
	 */
	public TrexAttribute setAttrTypeVal(short attr_type) {
		setValue("attr_type", attr_type);
		return this;
	}

	/**
	 *  @return Minimum - plaintext
	 */
	public String getMin() {
		return getString("min");
	}

	/**
	 *  @param min Minimum
	 */
	public TrexAttribute setMin(String min) {
		values.put("min", min);
		return this;
	}

	/**
	 *  @return Maximum - plaintext
	 */
	public String getMax() {
		return getString("max");
	}

	/**
	 *  @param max Maximum
	 */
	public TrexAttribute setMax(String max) {
		values.put("max", max);
		return this;
	}

}
