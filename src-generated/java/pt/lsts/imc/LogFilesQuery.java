/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Log Files Query (910)<br/>
 *  Files Query Operations.<br/>
 */

public class LogFilesQuery extends IMCMessage {

	public enum TYPE {
		FETCH(0),
		QUERY(1),
		CLEAR(2),
		CANCEL(3);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 910;

	public LogFilesQuery() {
		super(ID_STATIC);
	}

	public LogFilesQuery(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogFilesQuery(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LogFilesQuery create(Object... values) {
		LogFilesQuery m = new LogFilesQuery();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LogFilesQuery clone(IMCMessage msg) throws Exception {

		LogFilesQuery m = new LogFilesQuery();
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

	public LogFilesQuery(int req_id, TYPE type, long init, long end) {
		super(ID_STATIC);
		setReqId(req_id);
		setType(type);
		setInit(init);
		setEnd(end);
	}

	/**
	 *  @return Request Id - uint16_t
	 */
	public int getReqId() {
		return getInteger("req_id");
	}

	/**
	 *  @param req_id Request Id
	 */
	public LogFilesQuery setReqId(int req_id) {
		values.put("req_id", req_id);
		return this;
	}

	/**
	 *  @return Request Type (enumerated) - uint8_t
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
	 *  @param type Request Type (enumerated)
	 */
	public LogFilesQuery setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Request Type (as a String)
	 */
	public LogFilesQuery setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Request Type (integer value)
	 */
	public LogFilesQuery setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Interval Beginning - uint32_t
	 */
	public long getInit() {
		return getLong("init");
	}

	/**
	 *  @param init Interval Beginning
	 */
	public LogFilesQuery setInit(long init) {
		values.put("init", init);
		return this;
	}

	/**
	 *  @return Interval End - uint32_t
	 */
	public long getEnd() {
		return getLong("end");
	}

	/**
	 *  @param end Interval End
	 */
	public LogFilesQuery setEnd(long end) {
		values.put("end", end);
		return this;
	}

}
