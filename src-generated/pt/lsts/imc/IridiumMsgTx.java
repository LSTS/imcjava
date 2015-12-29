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
 *  IMC Message Transmit Iridium Message (171)<br/>
 */

public class IridiumMsgTx extends IMCMessage {

	public static final int ID_STATIC = 171;

	public IridiumMsgTx() {
		super(ID_STATIC);
	}

	public IridiumMsgTx(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IridiumMsgTx(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static IridiumMsgTx create(Object... values) {
		IridiumMsgTx m = new IridiumMsgTx();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static IridiumMsgTx clone(IMCMessage msg) throws Exception {

		IridiumMsgTx m = new IridiumMsgTx();
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

	public IridiumMsgTx(int req_id, int ttl, String destination, byte[] data) {
		super(ID_STATIC);
		setReqId(req_id);
		setTtl(ttl);
		if (destination != null)
			setDestination(destination);
		if (data != null)
			setData(data);
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
	public IridiumMsgTx setReqId(int req_id) {
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
	public IridiumMsgTx setTtl(int ttl) {
		values.put("ttl", ttl);
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
	public IridiumMsgTx setDestination(String destination) {
		values.put("destination", destination);
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
	public IridiumMsgTx setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
