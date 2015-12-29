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
 *  IMC Message SMS State (159)<br/>
 */

public class SmsState extends IMCMessage {

	public enum STATE {
		ACCEPTED(0),
		REJECTED(1),
		INTERRUPTED(2),
		COMPLETED(3),
		IDLE(4),
		TRANSMITTING(5),
		RECEIVING(6);

		protected long value;

		public long value() {
			return value;
		}

		STATE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 159;

	public SmsState() {
		super(ID_STATIC);
	}

	public SmsState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SmsState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SmsState create(Object... values) {
		SmsState m = new SmsState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SmsState clone(IMCMessage msg) throws Exception {

		SmsState m = new SmsState();
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

	public SmsState(long seq, STATE state, String error) {
		super(ID_STATIC);
		setSeq(seq);
		setState(state);
		if (error != null)
			setError(error);
	}

	/**
	 *  @return Sequence Number - uint32_t
	 */
	public long getSeq() {
		return getLong("seq");
	}

	/**
	 *  @param seq Sequence Number
	 */
	public SmsState setSeq(long seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return State (enumerated) - uint8_t
	 */
	public STATE getState() {
		try {
			STATE o = STATE.valueOf(getMessageType().getFieldPossibleValues("state").get(getLong("state")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStateStr() {
		return getString("state");
	}

	public short getStateVal() {
		return (short) getInteger("state");
	}

	/**
	 *  @param state State (enumerated)
	 */
	public SmsState setState(STATE state) {
		values.put("state", state.value());
		return this;
	}

	/**
	 *  @param state State (as a String)
	 */
	public SmsState setStateStr(String state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @param state State (integer value)
	 */
	public SmsState setStateVal(short state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @return Error Message - plaintext
	 */
	public String getError() {
		return getString("error");
	}

	/**
	 *  @param error Error Message
	 */
	public SmsState setError(String error) {
		values.put("error", error);
		return this;
	}

}
