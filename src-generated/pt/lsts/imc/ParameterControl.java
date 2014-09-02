/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 *                                                                             $:
 */

// Source generated by IMCJava from IMC version 5.4.1
package pt.lsts.imc;

/**
 *  IMC Message Parameters Control (11)<br/>
 *  This message is used to control the configuration in<br/>
 *  IMC-compatible systems.<br/>
 */

public class ParameterControl extends IMCMessage {

	public static final int ID_STATIC = 11;

	public enum OP {
		SET_PARAMS(1),
		SAVE_PARAMS(2),
		RESET_PARAMS(3),
		QUERY_PARAMS(4),
		PARAMS_LIST(5);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public ParameterControl() {
		super(ID_STATIC);
	}

	public ParameterControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ParameterControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ParameterControl create(Object... values) {
		ParameterControl m = new ParameterControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ParameterControl clone(IMCMessage msg) throws Exception {

		ParameterControl m = new ParameterControl();
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

	public ParameterControl(OP op, java.util.Collection<Parameter> params) {
		super(ID_STATIC);
		setOp(op);
		if (params != null)
			setParams(params);
	}

	/**
	 *  The operation to be done to the configuration is encoded here.<br/>
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

	/**
	 *  @return Parameters - message-list
	 */
	public java.util.Vector<Parameter> getParams() {
		try {
			return getMessageList("params", Parameter.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param op Operation (enumerated)
	 */
	public ParameterControl setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public ParameterControl setOp(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public ParameterControl setOp(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param params Parameters
	 */
	public ParameterControl setParams(java.util.Collection<Parameter> params) {
		values.put("params", params);
		return this;
	}

}
