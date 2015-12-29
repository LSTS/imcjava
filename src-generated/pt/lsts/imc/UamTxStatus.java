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
 *  IMC Message UamTxStatus (816)<br/>
 */

public class UamTxStatus extends IMCMessage {

	public enum VALUE {
		DONE(0),
		FAILED(1),
		CANCELED(2),
		BUSY(3),
		INV_ADDR(4),
		IP(5);

		protected long value;

		public long value() {
			return value;
		}

		VALUE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 816;

	public UamTxStatus() {
		super(ID_STATIC);
	}

	public UamTxStatus(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UamTxStatus(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UamTxStatus create(Object... values) {
		UamTxStatus m = new UamTxStatus();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UamTxStatus clone(IMCMessage msg) throws Exception {

		UamTxStatus m = new UamTxStatus();
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

	public UamTxStatus(int seq, VALUE value, String error) {
		super(ID_STATIC);
		setSeq(seq);
		setValue(value);
		if (error != null)
			setError(error);
	}

	/**
	 *  @return Sequence Id - uint16_t
	 */
	public int getSeq() {
		return getInteger("seq");
	}

	/**
	 *  @param seq Sequence Id
	 */
	public UamTxStatus setSeq(int seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return Value (enumerated) - uint8_t
	 */
	public VALUE getValue() {
		try {
			VALUE o = VALUE.valueOf(getMessageType().getFieldPossibleValues("value").get(getLong("value")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getValueStr() {
		return getString("value");
	}

	public short getValueVal() {
		return (short) getInteger("value");
	}

	/**
	 *  @param value Value (enumerated)
	 */
	public UamTxStatus setValue(VALUE value) {
		values.put("value", value.value());
		return this;
	}

	/**
	 *  @param value Value (as a String)
	 */
	public UamTxStatus setValueStr(String value) {
		setValue("value", value);
		return this;
	}

	/**
	 *  @param value Value (integer value)
	 */
	public UamTxStatus setValueVal(short value) {
		setValue("value", value);
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
	public UamTxStatus setError(String error) {
		values.put("error", error);
		return this;
	}

}
