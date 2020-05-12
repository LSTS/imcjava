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
 *  IMC Message TCP Transmission Request (521)<br/>
 *  Request data to be sent over a TCP connection<br/>
 */

public class TCPRequest extends IMCMessage {

	public static final int ID_STATIC = 521;

	public TCPRequest() {
		super(ID_STATIC);
	}

	public TCPRequest(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TCPRequest(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TCPRequest create(Object... values) {
		TCPRequest m = new TCPRequest();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TCPRequest clone(IMCMessage msg) throws Exception {

		TCPRequest m = new TCPRequest();
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

	public TCPRequest(int req_id, String destination, double timeout, IMCMessage msg_data) {
		super(ID_STATIC);
		setReqId(req_id);
		if (destination != null)
			setDestination(destination);
		setTimeout(timeout);
		if (msg_data != null)
			setMsgData(msg_data);
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
	public TCPRequest setReqId(int req_id) {
		values.put("req_id", req_id);
		return this;
	}

	/**
	 *  @return Destination - plaintext
	 */
	public String getDestination() {
		return getString("destination");
	}

	/**
	 *  @param destination Destination
	 */
	public TCPRequest setDestination(String destination) {
		values.put("destination", destination);
		return this;
	}

	/**
	 *  @return Timeout (s) - fp64_t
	 */
	public double getTimeout() {
		return getDouble("timeout");
	}

	/**
	 *  @param timeout Timeout (s)
	 */
	public TCPRequest setTimeout(double timeout) {
		values.put("timeout", timeout);
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
	public TCPRequest setMsgData(IMCMessage msg_data) {
		values.put("msg_data", msg_data);
		return this;
	}

}
