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
 *  IMC Message Values If (2018)<br/>
 *  This message is used to describe the ValuesIf content of a TypedEntityParameter.<br/>
 */

public class ValuesIf extends IMCMessage {

	public static final int ID_STATIC = 2018;

	public ValuesIf() {
		super(ID_STATIC);
	}

	public ValuesIf(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ValuesIf(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ValuesIf create(Object... values) {
		ValuesIf m = new ValuesIf();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ValuesIf clone(IMCMessage msg) throws Exception {

		ValuesIf m = new ValuesIf();
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

	public ValuesIf(String param, String value, String values_list) {
		super(ID_STATIC);
		if (param != null)
			setParam(param);
		if (value != null)
			setValue(value);
		if (values_list != null)
			setValuesList(values_list);
	}

	/**
	 *  @return Param - plaintext
	 */
	public String getParam() {
		return getString("param");
	}

	/**
	 *  @param param Param
	 */
	public ValuesIf setParam(String param) {
		values.put("param", param);
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
	public ValuesIf setValue(String value) {
		values.put("value", value);
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
	public ValuesIf setValuesList(String values_list) {
		values.put("values_list", values_list);
		return this;
	}

}
