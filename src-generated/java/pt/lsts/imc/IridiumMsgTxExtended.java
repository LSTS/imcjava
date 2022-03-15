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
 *  IMC Message Transmit Iridium Message (Extended) (2005)<br/>
 */

public class IridiumMsgTxExtended extends IMCMessage {

	public static final int ID_STATIC = 2005;

	public IridiumMsgTxExtended() {
		super(ID_STATIC);
	}

	public IridiumMsgTxExtended(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IridiumMsgTxExtended(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static IridiumMsgTxExtended create(Object... values) {
		IridiumMsgTxExtended m = new IridiumMsgTxExtended();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static IridiumMsgTxExtended clone(IMCMessage msg) throws Exception {

		IridiumMsgTxExtended m = new IridiumMsgTxExtended();
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

	public IridiumMsgTxExtended(int req_id, int ttl, long expiration, String destination, byte[] data) {
		super(ID_STATIC);
		setReqId(req_id);
		setTtl(ttl);
		setExpiration(expiration);
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
	public IridiumMsgTxExtended setReqId(int req_id) {
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
	public IridiumMsgTxExtended setTtl(int ttl) {
		values.put("ttl", ttl);
		return this;
	}

	/**
	 *  @return Expiration Time (s) - uint32_t
	 */
	public long getExpiration() {
		return getLong("expiration");
	}

	/**
	 *  @param expiration Expiration Time (s)
	 */
	public IridiumMsgTxExtended setExpiration(long expiration) {
		values.put("expiration", expiration);
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
	public IridiumMsgTxExtended setDestination(String destination) {
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
	public IridiumMsgTxExtended setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
