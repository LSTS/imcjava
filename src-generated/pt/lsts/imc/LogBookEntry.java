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
 *  IMC Message Log Book Entry (103)<br/>
 *  Human readable message reporting an event of interest.<br/>
 */

public class LogBookEntry extends IMCMessage {

	public enum TYPE {
		INFO(0),
		WARNING(1),
		ERROR(2),
		CRITICAL(3),
		DEBUG(4);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 103;

	public LogBookEntry() {
		super(ID_STATIC);
	}

	public LogBookEntry(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogBookEntry(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LogBookEntry create(Object... values) {
		LogBookEntry m = new LogBookEntry();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LogBookEntry clone(IMCMessage msg) throws Exception {

		LogBookEntry m = new LogBookEntry();
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

	public LogBookEntry(TYPE type, double htime, String context, String text) {
		super(ID_STATIC);
		setType(type);
		setHtime(htime);
		if (context != null)
			setContext(context);
		if (text != null)
			setText(text);
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
	public LogBookEntry setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public LogBookEntry setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public LogBookEntry setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Timestamp (s) - fp64_t
	 */
	public double getHtime() {
		return getDouble("htime");
	}

	/**
	 *  @param htime Timestamp (s)
	 */
	public LogBookEntry setHtime(double htime) {
		values.put("htime", htime);
		return this;
	}

	/**
	 *  @return Context - plaintext
	 */
	public String getContext() {
		return getString("context");
	}

	/**
	 *  @param context Context
	 */
	public LogBookEntry setContext(String context) {
		values.put("context", context);
		return this;
	}

	/**
	 *  @return Text - plaintext
	 */
	public String getText() {
		return getString("text");
	}

	/**
	 *  @param text Text
	 */
	public LogBookEntry setText(String text) {
		values.put("text", text);
		return this;
	}

}
