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
 *  IMC Message Telemetry Message (190)<br/>
 *  Message to handle telemetry transmissions.<br/>
 */

public class TelemetryMsg extends IMCMessage {

	public static final short TM_NAK = 0x00;
	public static final short TM_AK = 0x01;

	public enum TYPE {
		TX(1),
		RX(2),
		TXSTATUS(3);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum CODE {
		CODE_UNK(0),
		CODE_REPORT(1),
		CODE_IMC(2),
		CODE_RAW(3);

		protected long value;

		public long value() {
			return value;
		}

		CODE(long value) {
			this.value = value;
		}
	}

	public enum STATUS {
		NONE(0),
		DONE(1),
		FAILED(2),
		QUEUED(3),
		TRANSMIT(4),
		EXPIRED(5),
		EMPTY(6),
		INV_ADDR(7),
		INV_SIZE(8);

		protected long value;

		public long value() {
			return value;
		}

		STATUS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 190;

	public TelemetryMsg() {
		super(ID_STATIC);
	}

	public TelemetryMsg(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TelemetryMsg(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TelemetryMsg create(Object... values) {
		TelemetryMsg m = new TelemetryMsg();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TelemetryMsg clone(IMCMessage msg) throws Exception {

		TelemetryMsg m = new TelemetryMsg();
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

	public TelemetryMsg(TYPE type, long req_id, int ttl, CODE code, String destination, String Source, short acknowledge, STATUS status, byte[] data) {
		super(ID_STATIC);
		setType(type);
		setReqId(req_id);
		setTtl(ttl);
		setCode(code);
		if (destination != null)
			setDestination(destination);
		if (Source != null)
			setSource(Source);
		setAcknowledge(acknowledge);
		setStatus(status);
		if (data != null)
			setData(data);
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
	public TelemetryMsg setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public TelemetryMsg setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public TelemetryMsg setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Request Identifier - uint32_t
	 */
	public long getReqId() {
		return getLong("req_id");
	}

	/**
	 *  @param req_id Request Identifier
	 */
	public TelemetryMsg setReqId(long req_id) {
		values.put("req_id", req_id);
		return this;
	}

	/**
	 *  @return Time to live (s) - uint16_t
	 */
	public int getTtl() {
		return getInteger("ttl");
	}

	/**
	 *  @param ttl Time to live (s)
	 */
	public TelemetryMsg setTtl(int ttl) {
		values.put("ttl", ttl);
		return this;
	}

	/**
	 *  @return Code (enumerated) - uint8_t
	 */
	public CODE getCode() {
		try {
			CODE o = CODE.valueOf(getMessageType().getFieldPossibleValues("code").get(getLong("code")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCodeStr() {
		return getString("code");
	}

	public short getCodeVal() {
		return (short) getInteger("code");
	}

	/**
	 *  @param code Code (enumerated)
	 */
	public TelemetryMsg setCode(CODE code) {
		values.put("code", code.value());
		return this;
	}

	/**
	 *  @param code Code (as a String)
	 */
	public TelemetryMsg setCodeStr(String code) {
		setValue("code", code);
		return this;
	}

	/**
	 *  @param code Code (integer value)
	 */
	public TelemetryMsg setCodeVal(short code) {
		setValue("code", code);
		return this;
	}

	/**
	 *  @return Destination Identifier - plaintext
	 */
	public String getDestination() {
		return getString("destination");
	}

	/**
	 *  @param destination Destination Identifier
	 */
	public TelemetryMsg setDestination(String destination) {
		values.put("destination", destination);
		return this;
	}

	/**
	 *  @return Source Identifier - plaintext
	 */
	public String getSource() {
		return getString("Source");
	}

	/**
	 *  @param Source Source Identifier
	 */
	public TelemetryMsg setSource(String Source) {
		values.put("Source", Source);
		return this;
	}

	/**
	 *  @return Acknowledge (bitfield) - uint8_t
	 */
	public short getAcknowledge() {
		return (short) getInteger("acknowledge");
	}

	/**
	 *  @param acknowledge Acknowledge (bitfield)
	 */
	public TelemetryMsg setAcknowledge(short acknowledge) {
		values.put("acknowledge", acknowledge);
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
	public TelemetryMsg setStatus(STATUS status) {
		values.put("status", status.value());
		return this;
	}

	/**
	 *  @param status Status (as a String)
	 */
	public TelemetryMsg setStatusStr(String status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @param status Status (integer value)
	 */
	public TelemetryMsg setStatusVal(short status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @return Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Data
	 */
	public TelemetryMsg setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
