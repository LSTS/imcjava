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
 *  IMC Message Message Fragment (877)<br/>
 */

public class MessagePart extends IMCMessage {

	public static final int ID_STATIC = 877;

	public MessagePart() {
		super(ID_STATIC);
	}

	public MessagePart(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MessagePart(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static MessagePart create(Object... values) {
		MessagePart m = new MessagePart();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static MessagePart clone(IMCMessage msg) throws Exception {

		MessagePart m = new MessagePart();
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

	public MessagePart(short uid, short frag_number, short num_frags, byte[] data) {
		super(ID_STATIC);
		setUid(uid);
		setFragNumber(frag_number);
		setNumFrags(num_frags);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Transmission Unique Id - uint8_t
	 */
	public short getUid() {
		return (short) getInteger("uid");
	}

	/**
	 *  @param uid Transmission Unique Id
	 */
	public MessagePart setUid(short uid) {
		values.put("uid", uid);
		return this;
	}

	/**
	 *  @return Fragment Number - uint8_t
	 */
	public short getFragNumber() {
		return (short) getInteger("frag_number");
	}

	/**
	 *  @param frag_number Fragment Number
	 */
	public MessagePart setFragNumber(short frag_number) {
		values.put("frag_number", frag_number);
		return this;
	}

	/**
	 *  @return Total Number of fragments - uint8_t
	 */
	public short getNumFrags() {
		return (short) getInteger("num_frags");
	}

	/**
	 *  @param num_frags Total Number of fragments
	 */
	public MessagePart setNumFrags(short num_frags) {
		values.put("num_frags", num_frags);
		return this;
	}

	/**
	 *  @return Fragment Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Fragment Data
	 */
	public MessagePart setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
