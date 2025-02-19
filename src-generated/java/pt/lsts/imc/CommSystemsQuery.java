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
 *  IMC Message Communication Systems Query (189)<br/>
 *  Presence of Communication Interfaces query.<br/>
 */

public class CommSystemsQuery extends IMCMessage {

	public static final short CIQ_QUERY = 0x01;
	public static final short CIQ_REPLY = 0x02;

	public static final int CIQ_ACOUSTIC = 0x0001;
	public static final int CIQ_SATELLITE = 0x0002;
	public static final int CIQ_GSM = 0x0004;
	public static final int CIQ_MOBILE = 0x0008;
	public static final int CIQ_RADIO = 0x0010;

	public enum MODEL {
		UNKNOWN(0),
		M3DR(1),
		RDFXXXXPTP(2);

		protected long value;

		public long value() {
			return value;
		}

		MODEL(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 189;

	public CommSystemsQuery() {
		super(ID_STATIC);
	}

	public CommSystemsQuery(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CommSystemsQuery(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CommSystemsQuery create(Object... values) {
		CommSystemsQuery m = new CommSystemsQuery();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CommSystemsQuery clone(IMCMessage msg) throws Exception {

		CommSystemsQuery m = new CommSystemsQuery();
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

	public CommSystemsQuery(short type, int comm_interface, MODEL model, String list) {
		super(ID_STATIC);
		setType(type);
		setCommInterface(comm_interface);
		setModel(model);
		if (list != null)
			setList(list);
	}

	/**
	 *  @return Type (bitfield) - uint8_t
	 */
	public short getType() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Type (bitfield)
	 */
	public CommSystemsQuery setType(short type) {
		values.put("type", type);
		return this;
	}

	/**
	 *  @return Communication Interface (bitfield) - uint16_t
	 */
	public int getCommInterface() {
		return getInteger("comm_interface");
	}

	/**
	 *  @param comm_interface Communication Interface (bitfield)
	 */
	public CommSystemsQuery setCommInterface(int comm_interface) {
		values.put("comm_interface", comm_interface);
		return this;
	}

	/**
	 *  @return Model (enumerated) - uint16_t
	 */
	public MODEL getModel() {
		try {
			MODEL o = MODEL.valueOf(getMessageType().getFieldPossibleValues("model").get(getLong("model")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getModelStr() {
		return getString("model");
	}

	public int getModelVal() {
		return getInteger("model");
	}

	/**
	 *  @param model Model (enumerated)
	 */
	public CommSystemsQuery setModel(MODEL model) {
		values.put("model", model.value());
		return this;
	}

	/**
	 *  @param model Model (as a String)
	 */
	public CommSystemsQuery setModelStr(String model) {
		setValue("model", model);
		return this;
	}

	/**
	 *  @param model Model (integer value)
	 */
	public CommSystemsQuery setModelVal(int model) {
		setValue("model", model);
		return this;
	}

	/**
	 *  @return System List (list) - plaintext
	 */
	public String getList() {
		return getString("list");
	}

	/**
	 *  @param list System List (list)
	 */
	public CommSystemsQuery setList(String list) {
		values.put("list", list);
		return this;
	}

}
