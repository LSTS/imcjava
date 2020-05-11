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
 *  IMC Message Transmission Request (515)<br/>
 *  Request data to be sent over a specified communication mean.<br/>
 */

public class TransmissionRequest extends IMCMessage {

	public enum COMM_MEAN {
		WIFI(0),
		ACOUSTIC(1),
		SATELLITE(2),
		GSM(3),
		ANY(4),
		ALL(5);

		protected long value;

		public long value() {
			return value;
		}

		COMM_MEAN(long value) {
			this.value = value;
		}
	}

	public enum DATA_MODE {
		INLINEMSG(0),
		TEXT(1),
		RAW(2),
		ABORT(3),
		RANGE(4),
		REVERSE_RANGE(5);

		protected long value;

		public long value() {
			return value;
		}

		DATA_MODE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 515;

	public TransmissionRequest() {
		super(ID_STATIC);
	}

	public TransmissionRequest(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TransmissionRequest(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TransmissionRequest create(Object... values) {
		TransmissionRequest m = new TransmissionRequest();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TransmissionRequest clone(IMCMessage msg) throws Exception {

		TransmissionRequest m = new TransmissionRequest();
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

	public TransmissionRequest(int req_id, COMM_MEAN comm_mean, String destination, double deadline, float range, DATA_MODE data_mode, IMCMessage msg_data, String txt_data, byte[] raw_data) {
		super(ID_STATIC);
		setReqId(req_id);
		setCommMean(comm_mean);
		if (destination != null)
			setDestination(destination);
		setDeadline(deadline);
		setRange(range);
		setDataMode(data_mode);
		if (msg_data != null)
			setMsgData(msg_data);
		if (txt_data != null)
			setTxtData(txt_data);
		if (raw_data != null)
			setRawData(raw_data);
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
	public TransmissionRequest setReqId(int req_id) {
		values.put("req_id", req_id);
		return this;
	}

	/**
	 *  @return Communication Mean (enumerated) - uint8_t
	 */
	public COMM_MEAN getCommMean() {
		try {
			COMM_MEAN o = COMM_MEAN.valueOf(getMessageType().getFieldPossibleValues("comm_mean").get(getLong("comm_mean")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCommMeanStr() {
		return getString("comm_mean");
	}

	public short getCommMeanVal() {
		return (short) getInteger("comm_mean");
	}

	/**
	 *  @param comm_mean Communication Mean (enumerated)
	 */
	public TransmissionRequest setCommMean(COMM_MEAN comm_mean) {
		values.put("comm_mean", comm_mean.value());
		return this;
	}

	/**
	 *  @param comm_mean Communication Mean (as a String)
	 */
	public TransmissionRequest setCommMeanStr(String comm_mean) {
		setValue("comm_mean", comm_mean);
		return this;
	}

	/**
	 *  @param comm_mean Communication Mean (integer value)
	 */
	public TransmissionRequest setCommMeanVal(short comm_mean) {
		setValue("comm_mean", comm_mean);
		return this;
	}

	/**
	 *  @return Destination System - plaintext
	 */
	public String getDestination() {
		return getString("destination");
	}

	/**
	 *  @param destination Destination System
	 */
	public TransmissionRequest setDestination(String destination) {
		values.put("destination", destination);
		return this;
	}

	/**
	 *  @return Deadline - fp64_t
	 */
	public double getDeadline() {
		return getDouble("deadline");
	}

	/**
	 *  @param deadline Deadline
	 */
	public TransmissionRequest setDeadline(double deadline) {
		values.put("deadline", deadline);
		return this;
	}

	/**
	 *  @return Range (m) - fp32_t
	 */
	public double getRange() {
		return getDouble("range");
	}

	/**
	 *  @param range Range (m)
	 */
	public TransmissionRequest setRange(double range) {
		values.put("range", range);
		return this;
	}

	/**
	 *  @return Data Mode (enumerated) - uint8_t
	 */
	public DATA_MODE getDataMode() {
		try {
			DATA_MODE o = DATA_MODE.valueOf(getMessageType().getFieldPossibleValues("data_mode").get(getLong("data_mode")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getDataModeStr() {
		return getString("data_mode");
	}

	public short getDataModeVal() {
		return (short) getInteger("data_mode");
	}

	/**
	 *  @param data_mode Data Mode (enumerated)
	 */
	public TransmissionRequest setDataMode(DATA_MODE data_mode) {
		values.put("data_mode", data_mode.value());
		return this;
	}

	/**
	 *  @param data_mode Data Mode (as a String)
	 */
	public TransmissionRequest setDataModeStr(String data_mode) {
		setValue("data_mode", data_mode);
		return this;
	}

	/**
	 *  @param data_mode Data Mode (integer value)
	 */
	public TransmissionRequest setDataModeVal(short data_mode) {
		setValue("data_mode", data_mode);
		return this;
	}

	/**
	 *  @return Message Data - message
	 */
	public IMCMessage getMsgData() {
		return getMessage("msg_data");
	}

	public <T extends IMCMessage> T getMsgData(Class<T> clazz) throws Exception {
		return getMessage(clazz, "msg_data");
	}

	/**
	 *  @param msg_data Message Data
	 */
	public TransmissionRequest setMsgData(IMCMessage msg_data) {
		values.put("msg_data", msg_data);
		return this;
	}

	/**
	 *  @return Text Data - plaintext
	 */
	public String getTxtData() {
		return getString("txt_data");
	}

	/**
	 *  @param txt_data Text Data
	 */
	public TransmissionRequest setTxtData(String txt_data) {
		values.put("txt_data", txt_data);
		return this;
	}

	/**
	 *  @return Raw Data - rawdata
	 */
	public byte[] getRawData() {
		return getRawData("raw_data");
	}

	/**
	 *  @param raw_data Raw Data
	 */
	public TransmissionRequest setRawData(byte[] raw_data) {
		values.put("raw_data", raw_data);
		return this;
	}

}
