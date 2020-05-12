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
 *  IMC Message SMS Transmission Status (518)<br/>
 *  Reply sent in response to a SMS sending request.<br/>
 */

public class SmsStatus extends IMCMessage {

	public enum STATUS {
		QUEUED(0),
		SENT(1),
		INPUT_FAILURE(101),
		ERROR(102);

		protected long value;

		public long value() {
			return value;
		}

		STATUS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 518;

	public SmsStatus() {
		super(ID_STATIC);
	}

	public SmsStatus(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SmsStatus(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SmsStatus create(Object... values) {
		SmsStatus m = new SmsStatus();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SmsStatus clone(IMCMessage msg) throws Exception {

		SmsStatus m = new SmsStatus();
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

	public SmsStatus(int req_id, STATUS status, String info) {
		super(ID_STATIC);
		setReqId(req_id);
		setStatus(status);
		if (info != null)
			setInfo(info);
	}

	/**
	 *  @return Request Identifier - uint16_t
	 */
	public int getReqId() {
		return getInteger("req_id");
	}

	/**
	 *  @param req_id Request Identifier
	 */
	public SmsStatus setReqId(int req_id) {
		values.put("req_id", req_id);
		return this;
	}

	/**
	 *  @return Status (enumerated) - uint8_t
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
	 *  @param status Status (enumerated)
	 */
	public SmsStatus setStatus(STATUS status) {
		values.put("status", status.value());
		return this;
	}

	/**
	 *  @param status Status (as a String)
	 */
	public SmsStatus setStatusStr(String status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @param status Status (integer value)
	 */
	public SmsStatus setStatusVal(short status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @return Information - plaintext
	 */
	public String getInfo() {
		return getString("info");
	}

	/**
	 *  @param info Information
	 */
	public SmsStatus setInfo(String info) {
		values.put("info", info);
		return this;
	}

}
