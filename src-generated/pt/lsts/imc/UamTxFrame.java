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
 *  IMC Message UamTxFrame (814)<br/>
 */

public class UamTxFrame extends IMCMessage {

	public static final short UTF_ACK = 0x01;
	public static final short UTF_DELAYED = 0x02;

	public static final int ID_STATIC = 814;

	public UamTxFrame() {
		super(ID_STATIC);
	}

	public UamTxFrame(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UamTxFrame(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UamTxFrame create(Object... values) {
		UamTxFrame m = new UamTxFrame();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UamTxFrame clone(IMCMessage msg) throws Exception {

		UamTxFrame m = new UamTxFrame();
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

	public UamTxFrame(int seq, String sys_dst, short flags, byte[] data) {
		super(ID_STATIC);
		setSeq(seq);
		if (sys_dst != null)
			setSysDst(sys_dst);
		setFlags(flags);
		if (data != null)
			setData(data);
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
	public UamTxFrame setSeq(int seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return Destination System - plaintext
	 */
	public String getSysDst() {
		return getString("sys_dst");
	}

	/**
	 *  @param sys_dst Destination System
	 */
	public UamTxFrame setSysDst(String sys_dst) {
		values.put("sys_dst", sys_dst);
		return this;
	}

	/**
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public UamTxFrame setFlags(short flags) {
		values.put("flags", flags);
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
	public UamTxFrame setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
