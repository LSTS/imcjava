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
 *  IMC Message Remote Actions Request (304)<br/>
 *  This message is used as query to request for the possible remote<br/>
 *  actions (operation=QUERY and the list is empty in this<br/>
 *  case). The vehicle responds using the same message type<br/>
 *  returning the tuplelist with the pairs: Action,Type<br/>
 *  (operation=REPORT). The type of action can be Axis, Hat or<br/>
 *  Button.<br/>
 */

public class RemoteActionsRequest extends IMCMessage {

	public enum OP {
		REPORT(0),
		QUERY(1);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 304;

	public RemoteActionsRequest() {
		super(ID_STATIC);
	}

	public RemoteActionsRequest(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RemoteActionsRequest(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static RemoteActionsRequest create(Object... values) {
		RemoteActionsRequest m = new RemoteActionsRequest();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static RemoteActionsRequest clone(IMCMessage msg) throws Exception {

		RemoteActionsRequest m = new RemoteActionsRequest();
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

	public RemoteActionsRequest(OP op, String actions) {
		super(ID_STATIC);
		setOp(op);
		if (actions != null)
			setActions(actions);
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
	public RemoteActionsRequest setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op operation (as a String)
	 */
	public RemoteActionsRequest setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op operation (integer value)
	 */
	public RemoteActionsRequest setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Actions (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getActions() {
		return getTupleList("actions");
	}

	/**
	 *  @param actions Actions (tuplelist)
	 */
	public RemoteActionsRequest setActions(java.util.LinkedHashMap<String, ?> actions) {
		String val = encodeTupleList(actions);
		values.put("actions", val);
		return this;
	}

	public RemoteActionsRequest setActions(String actions) {
		values.put("actions", actions);
		return this;
	}

}
