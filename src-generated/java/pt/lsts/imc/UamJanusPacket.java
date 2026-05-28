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
 *  IMC Message UamJanusPacket (819)<br/>
 *  This message is used to send and receive Janus packets over the acoustic channel, agnostic of underlying modem.<br/>
 */

public class UamJanusPacket extends IMCMessage {

	public static final short JANUSBL_MOBILE = 0x01;
	public static final short JANUSBL_REPEAT_INTERVAL = 0x02;
	public static final short JANUSBL_RESERVATION_TIME = 0x04;
	public static final short JANUSBL_DECODE_CAPABILITY = 0x08;
	public static final short JANUSBL_FORWARD_CAPABILITY = 0x16;

	public enum OP {
		SEND_REQ(0),
		BASELINE_RECV(1),
		UNPACK_REQ(2),
		UNPACK_REPLY(3),
		UNPACK_ERROR(4),
		SEND_SUCCESS(5),
		SEND_ERROR(6);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 819;

	public UamJanusPacket() {
		super(ID_STATIC);
	}

	public UamJanusPacket(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UamJanusPacket(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UamJanusPacket create(Object... values) {
		UamJanusPacket m = new UamJanusPacket();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UamJanusPacket clone(IMCMessage msg) throws Exception {

		UamJanusPacket m = new UamJanusPacket();
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

	public UamJanusPacket(int seq, OP op, short baseline_flags, float time, short class_user_id, short application_type, byte[] adb, String error, short length, byte[] cargo) {
		super(ID_STATIC);
		setSeq(seq);
		setOp(op);
		setBaselineFlags(baseline_flags);
		setTime(time);
		setClassUserId(class_user_id);
		setApplicationType(application_type);
		if (adb != null)
			setAdb(adb);
		if (error != null)
			setError(error);
		setLength(length);
		if (cargo != null)
			setCargo(cargo);
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
	public UamJanusPacket setSeq(int seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return Operation (enumerated) - uint8_t
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
	 *  @param op Operation (enumerated)
	 */
	public UamJanusPacket setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public UamJanusPacket setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public UamJanusPacket setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Baseline Packet Flags (bitfield) - uint8_t
	 */
	public short getBaselineFlags() {
		return (short) getInteger("baseline_flags");
	}

	/**
	 *  @param baseline_flags Baseline Packet Flags (bitfield)
	 */
	public UamJanusPacket setBaselineFlags(short baseline_flags) {
		values.put("baseline_flags", baseline_flags);
		return this;
	}

	/**
	 *  @return Time (s) - fp32_t
	 */
	public double getTime() {
		return getDouble("time");
	}

	/**
	 *  @param time Time (s)
	 */
	public UamJanusPacket setTime(double time) {
		values.put("time", time);
		return this;
	}

	/**
	 *  @return Class User Id - uint8_t
	 */
	public short getClassUserId() {
		return (short) getInteger("class_user_id");
	}

	/**
	 *  @param class_user_id Class User Id
	 */
	public UamJanusPacket setClassUserId(short class_user_id) {
		values.put("class_user_id", class_user_id);
		return this;
	}

	/**
	 *  @return Application Type - uint8_t
	 */
	public short getApplicationType() {
		return (short) getInteger("application_type");
	}

	/**
	 *  @param application_type Application Type
	 */
	public UamJanusPacket setApplicationType(short application_type) {
		values.put("application_type", application_type);
		return this;
	}

	/**
	 *  @return Application Data Block - rawdata
	 */
	public byte[] getAdb() {
		return getRawData("adb");
	}

	/**
	 *  @param adb Application Data Block
	 */
	public UamJanusPacket setAdb(byte[] adb) {
		values.put("adb", adb);
		return this;
	}

	/**
	 *  @return Error - plaintext
	 */
	public String getError() {
		return getString("error");
	}

	/**
	 *  @param error Error
	 */
	public UamJanusPacket setError(String error) {
		values.put("error", error);
		return this;
	}

	/**
	 *  @return Length - uint8_t
	 */
	public short getLength() {
		return (short) getInteger("length");
	}

	/**
	 *  @param length Length
	 */
	public UamJanusPacket setLength(short length) {
		values.put("length", length);
		return this;
	}

	/**
	 *  @return Janus Cargo - rawdata
	 */
	public byte[] getCargo() {
		return getRawData("cargo");
	}

	/**
	 *  @param cargo Janus Cargo
	 */
	public UamJanusPacket setCargo(byte[] cargo) {
		values.put("cargo", cargo);
		return this;
	}

}
