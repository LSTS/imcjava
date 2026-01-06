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
 *  IMC Message Typed Entity Parameter (2017)<br/>
 *  Entity parameter with all the data that defines an entity parameter.<br/>
 */

public class TypedEntityParameter extends TypedEntityParametersOptions {

	public enum TYPE {
		BOOL(1),
		INT(2),
		FLOAT(3),
		STRING(4),
		LIST_BOOL(5),
		LIST_INT(6),
		LIST_FLOAT(7),
		LIST_STRING(8);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum VISIBILITY {
		USER(0),
		DEVELOPER(1),
		USER_NOT_EDITABLE(2),
		DEVELOPER_NOT_EDITABLE(3);

		protected long value;

		public long value() {
			return value;
		}

		VISIBILITY(long value) {
			this.value = value;
		}
	}

	public enum SCOPE {
		GLOBAL(0),
		IDLE(1),
		PLAN(2),
		MANEUVER(3);

		protected long value;

		public long value() {
			return value;
		}

		SCOPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 2017;

	public TypedEntityParameter() {
		super(ID_STATIC);
	}

	public TypedEntityParameter(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TypedEntityParameter(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TypedEntityParameter create(Object... values) {
		TypedEntityParameter m = new TypedEntityParameter();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TypedEntityParameter clone(IMCMessage msg) throws Exception {

		TypedEntityParameter m = new TypedEntityParameter();
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

	public TypedEntityParameter(String name, TYPE type, String default_value, String units, String description, String values_list, float min_value, float max_value, short list_min_size, short list_max_size, java.util.Collection<ValuesIf> values_if_list, VISIBILITY visibility, SCOPE scope) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		setType(type);
		if (default_value != null)
			setDefaultValue(default_value);
		if (units != null)
			setUnits(units);
		if (description != null)
			setDescription(description);
		if (values_list != null)
			setValuesList(values_list);
		setMinValue(min_value);
		setMaxValue(max_value);
		setListMinSize(list_min_size);
		setListMaxSize(list_max_size);
		if (values_if_list != null)
			setValuesIfList(values_if_list);
		setVisibility(visibility);
		setScope(scope);
	}

	/**
	 *  @return Parameter Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Parameter Name
	 */
	public TypedEntityParameter setName(String name) {
		values.put("name", name);
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
	public TypedEntityParameter setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public TypedEntityParameter setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public TypedEntityParameter setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Default Value - plaintext
	 */
	public String getDefaultValue() {
		return getString("default_value");
	}

	/**
	 *  @param default_value Default Value
	 */
	public TypedEntityParameter setDefaultValue(String default_value) {
		values.put("default_value", default_value);
		return this;
	}

	/**
	 *  @return Units - plaintext
	 */
	public String getUnits() {
		return getString("units");
	}

	/**
	 *  @param units Units
	 */
	public TypedEntityParameter setUnits(String units) {
		values.put("units", units);
		return this;
	}

	/**
	 *  @return Description - plaintext
	 */
	public String getDescription() {
		return getString("description");
	}

	/**
	 *  @param description Description
	 */
	public TypedEntityParameter setDescription(String description) {
		values.put("description", description);
		return this;
	}

	/**
	 *  @return Values List - plaintext
	 */
	public String getValuesList() {
		return getString("values_list");
	}

	/**
	 *  @param values_list Values List
	 */
	public TypedEntityParameter setValuesList(String values_list) {
		values.put("values_list", values_list);
		return this;
	}

	/**
	 *  @return Min Value - fp32_t
	 */
	public double getMinValue() {
		return getDouble("min_value");
	}

	/**
	 *  @param min_value Min Value
	 */
	public TypedEntityParameter setMinValue(double min_value) {
		values.put("min_value", min_value);
		return this;
	}

	/**
	 *  @return Max Value - fp32_t
	 */
	public double getMaxValue() {
		return getDouble("max_value");
	}

	/**
	 *  @param max_value Max Value
	 */
	public TypedEntityParameter setMaxValue(double max_value) {
		values.put("max_value", max_value);
		return this;
	}

	/**
	 *  @return List Min Size - uint8_t
	 */
	public short getListMinSize() {
		return (short) getInteger("list_min_size");
	}

	/**
	 *  @param list_min_size List Min Size
	 */
	public TypedEntityParameter setListMinSize(short list_min_size) {
		values.put("list_min_size", list_min_size);
		return this;
	}

	/**
	 *  @return List Max Size - uint8_t
	 */
	public short getListMaxSize() {
		return (short) getInteger("list_max_size");
	}

	/**
	 *  @param list_max_size List Max Size
	 */
	public TypedEntityParameter setListMaxSize(short list_max_size) {
		values.put("list_max_size", list_max_size);
		return this;
	}

	/**
	 *  @return Values If List - message-list
	 */
	public java.util.Vector<ValuesIf> getValuesIfList() {
		try {
			return getMessageList("values_if_list", ValuesIf.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param values_if_list Values If List
	 */
	public TypedEntityParameter setValuesIfList(java.util.Collection<ValuesIf> values_if_list) {
		values.put("values_if_list", values_if_list);
		return this;
	}

	/**
	 *  @return Visibility (enumerated) - uint8_t
	 */
	public VISIBILITY getVisibility() {
		try {
			VISIBILITY o = VISIBILITY.valueOf(getMessageType().getFieldPossibleValues("visibility").get(getLong("visibility")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getVisibilityStr() {
		return getString("visibility");
	}

	public short getVisibilityVal() {
		return (short) getInteger("visibility");
	}

	/**
	 *  @param visibility Visibility (enumerated)
	 */
	public TypedEntityParameter setVisibility(VISIBILITY visibility) {
		values.put("visibility", visibility.value());
		return this;
	}

	/**
	 *  @param visibility Visibility (as a String)
	 */
	public TypedEntityParameter setVisibilityStr(String visibility) {
		setValue("visibility", visibility);
		return this;
	}

	/**
	 *  @param visibility Visibility (integer value)
	 */
	public TypedEntityParameter setVisibilityVal(short visibility) {
		setValue("visibility", visibility);
		return this;
	}

	/**
	 *  @return Scope (enumerated) - uint8_t
	 */
	public SCOPE getScope() {
		try {
			SCOPE o = SCOPE.valueOf(getMessageType().getFieldPossibleValues("scope").get(getLong("scope")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getScopeStr() {
		return getString("scope");
	}

	public short getScopeVal() {
		return (short) getInteger("scope");
	}

	/**
	 *  @param scope Scope (enumerated)
	 */
	public TypedEntityParameter setScope(SCOPE scope) {
		values.put("scope", scope.value());
		return this;
	}

	/**
	 *  @param scope Scope (as a String)
	 */
	public TypedEntityParameter setScopeStr(String scope) {
		setValue("scope", scope);
		return this;
	}

	/**
	 *  @param scope Scope (integer value)
	 */
	public TypedEntityParameter setScopeVal(short scope) {
		setValue("scope", scope);
		return this;
	}

}
