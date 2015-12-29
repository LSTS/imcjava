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
 *  IMC Message QueryEntityParameters (803)<br/>
 */

public class QueryEntityParameters extends IMCMessage {

	public static final int ID_STATIC = 803;

	public QueryEntityParameters() {
		super(ID_STATIC);
	}

	public QueryEntityParameters(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public QueryEntityParameters(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static QueryEntityParameters create(Object... values) {
		QueryEntityParameters m = new QueryEntityParameters();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static QueryEntityParameters clone(IMCMessage msg) throws Exception {

		QueryEntityParameters m = new QueryEntityParameters();
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

	public QueryEntityParameters(String name, String visibility, String scope) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		if (visibility != null)
			setVisibility(visibility);
		if (scope != null)
			setScope(scope);
	}

	/**
	 *  @return Entity Name - plaintext
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 *  @param name Entity Name
	 */
	public QueryEntityParameters setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Visibility - plaintext
	 */
	public String getVisibility() {
		return getString("visibility");
	}

	/**
	 *  @param visibility Visibility
	 */
	public QueryEntityParameters setVisibility(String visibility) {
		values.put("visibility", visibility);
		return this;
	}

	/**
	 *  @return Scope - plaintext
	 */
	public String getScope() {
		return getString("scope");
	}

	/**
	 *  @param scope Scope
	 */
	public QueryEntityParameters setScope(String scope) {
		values.put("scope", scope);
		return this;
	}

}
