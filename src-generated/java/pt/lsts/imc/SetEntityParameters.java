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
 *  IMC Message SetEntityParameters (804)<br/>
 */

public class SetEntityParameters extends IMCMessage {

	public static final int ID_STATIC = 804;

	public SetEntityParameters() {
		super(ID_STATIC);
	}

	public SetEntityParameters(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SetEntityParameters(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SetEntityParameters create(Object... values) {
		SetEntityParameters m = new SetEntityParameters();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SetEntityParameters clone(IMCMessage msg) throws Exception {

		SetEntityParameters m = new SetEntityParameters();
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

	public SetEntityParameters(String name, java.util.Collection<EntityParameter> params) {
		super(ID_STATIC);
		if (name != null)
			setName(name);
		if (params != null)
			setParams(params);
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
	public SetEntityParameters setName(String name) {
		values.put("name", name);
		return this;
	}

	/**
	 *  @return Parameters - message-list
	 */
	public java.util.Vector<EntityParameter> getParams() {
		try {
			return getMessageList("params", EntityParameter.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param params Parameters
	 */
	public SetEntityParameters setParams(java.util.Collection<EntityParameter> params) {
		values.put("params", params);
		return this;
	}

}
