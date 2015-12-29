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
 *  IMC Message Cache Control (101)<br/>
 *  Control caching of messages to persistent storage.<br/>
 */

public class CacheControl extends IMCMessage {

	public enum OP {
		STORE(0),
		LOAD(1),
		CLEAR(2),
		COPY(3),
		COPY_COMPLETE(4);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 101;

	public CacheControl() {
		super(ID_STATIC);
	}

	public CacheControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CacheControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CacheControl create(Object... values) {
		CacheControl m = new CacheControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CacheControl clone(IMCMessage msg) throws Exception {

		CacheControl m = new CacheControl();
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

	public CacheControl(OP op, String snapshot, IMCMessage message) {
		super(ID_STATIC);
		setOp(op);
		if (snapshot != null)
			setSnapshot(snapshot);
		if (message != null)
			setMessage(message);
	}

	/**
	 *  @return Control Operation (enumerated) - uint8_t
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
	 *  @param op Control Operation (enumerated)
	 */
	public CacheControl setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Control Operation (as a String)
	 */
	public CacheControl setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Control Operation (integer value)
	 */
	public CacheControl setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Snapshot destination - plaintext
	 */
	public String getSnapshot() {
		return getString("snapshot");
	}

	/**
	 *  @param snapshot Snapshot destination
	 */
	public CacheControl setSnapshot(String snapshot) {
		values.put("snapshot", snapshot);
		return this;
	}

	/**
	 *  @return Message - message
	 */
	public IMCMessage getMessage() {
		return getMessage("message");
	}

	public <T extends IMCMessage> T getMessage(Class<T> clazz) throws Exception {
		return getMessage(clazz, "message");
	}

	/**
	 *  @param message Message
	 */
	public CacheControl setMessage(IMCMessage message) {
		values.put("message", message);
		return this;
	}

}
