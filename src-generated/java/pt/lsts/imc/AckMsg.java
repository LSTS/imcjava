/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Acknowledged Message (2040)<br/>
 *  Generic acknowledgment message for request-response communication patterns.<br/>
 *  Can be used by any task to acknowledge receipt and result of a command or request.<br/>
 */

public class AckMsg extends IMCMessage {

	public enum STATUS {
		SUCCESS(0),
		FAILURE(1),
		IN_PROGRESS(2),
		TIMEOUT(3),
		INVALID(4),
		NOT_SUPPORTED(5);

		protected long value;

		public long value() {
			return value;
		}

		STATUS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 2040;

	public AckMsg() {
		super(ID_STATIC);
	}

	public AckMsg(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AckMsg(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AckMsg create(Object... values) {
		AckMsg m = new AckMsg();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AckMsg clone(IMCMessage msg) throws Exception {

		AckMsg m = new AckMsg();
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

	public AckMsg(IMCMessage original, String text, STATUS status) {
		super(ID_STATIC);
		if (original != null)
			setOriginal(original);
		if (text != null)
			setText(text);
		setStatus(status);
	}

	/**
	 *  @return Original Message - message
	 */
	public IMCMessage getOriginal() {
		return getMessage("original");
	}

	public <T extends IMCMessage> T getOriginal(Class<T> clazz) throws Exception {
		return getMessage(clazz, "original");
	}

	/**
	 *  @param original Original Message
	 */
	public AckMsg setOriginal(IMCMessage original) {
		values.put("original", original);
		return this;
	}

	/**
	 *  @return text - plaintext
	 */
	public String getText() {
		return getString("text");
	}

	/**
	 *  @param text text
	 */
	public AckMsg setText(String text) {
		values.put("text", text);
		return this;
	}

	/**
	 *  @return StatusEnum (enumerated) - uint8_t
	 */
	public STATUS getStatus() {
		try {
			STATUS o = STATUS.valueOf(getMessageType().getFieldPossibleValues("status").get(getLong("status")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStatusStr() {
		return getString("status");
	}

	public short getStatusVal() {
		return (short) getInteger("status");
	}

	/**
	 *  @param status StatusEnum (enumerated)
	 */
	public AckMsg setStatus(STATUS status) {
		values.put("status", status.value());
		return this;
	}

	/**
	 *  @param status StatusEnum (as a String)
	 */
	public AckMsg setStatusStr(String status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @param status StatusEnum (integer value)
	 */
	public AckMsg setStatusVal(short status) {
		setValue("status", status);
		return this;
	}

}
