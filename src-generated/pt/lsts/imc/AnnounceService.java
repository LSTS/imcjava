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
 *  IMC Message Announce Service (152)<br/>
 *  Announcement about the existence of a service.<br/>
 */

public class AnnounceService extends IMCMessage {

	public static final short SRV_TYPE_EXTERNAL = 0x01;
	public static final short SRV_TYPE_LOCAL = 0x02;

	public static final int ID_STATIC = 152;

	public AnnounceService() {
		super(ID_STATIC);
	}

	public AnnounceService(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AnnounceService(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AnnounceService create(Object... values) {
		AnnounceService m = new AnnounceService();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AnnounceService clone(IMCMessage msg) throws Exception {

		AnnounceService m = new AnnounceService();
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

	public AnnounceService(String service, short service_type) {
		super(ID_STATIC);
		if (service != null)
			setService(service);
		setServiceType(service_type);
	}

	/**
	 *  @return Service - plaintext
	 */
	public String getService() {
		return getString("service");
	}

	/**
	 *  @param service Service
	 */
	public AnnounceService setService(String service) {
		values.put("service", service);
		return this;
	}

	/**
	 *  @return ServiceType (bitfield) - uint8_t
	 */
	public short getServiceType() {
		return (short) getInteger("service_type");
	}

	/**
	 *  @param service_type ServiceType (bitfield)
	 */
	public AnnounceService setServiceType(short service_type) {
		values.put("service_type", service_type);
		return this;
	}

}
