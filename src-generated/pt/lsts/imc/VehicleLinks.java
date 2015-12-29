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
 *  IMC Message Vehicle Links (650)<br/>
 *  This message is sent by the TREX task which gives further information to a TREX instance about connected IMC nodes<br/>
 */

public class VehicleLinks extends IMCMessage {

	public static final int ID_STATIC = 650;

	public VehicleLinks() {
		super(ID_STATIC);
	}

	public VehicleLinks(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VehicleLinks(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VehicleLinks create(Object... values) {
		VehicleLinks m = new VehicleLinks();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VehicleLinks clone(IMCMessage msg) throws Exception {

		VehicleLinks m = new VehicleLinks();
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

	public VehicleLinks(String localname, java.util.Collection<Announce> links) {
		super(ID_STATIC);
		if (localname != null)
			setLocalname(localname);
		if (links != null)
			setLinks(links);
	}

	/**
	 *  @return Local Name - plaintext
	 */
	public String getLocalname() {
		return getString("localname");
	}

	/**
	 *  @param localname Local Name
	 */
	public VehicleLinks setLocalname(String localname) {
		values.put("localname", localname);
		return this;
	}

	/**
	 *  @return Active Links - message-list
	 */
	public java.util.Vector<Announce> getLinks() {
		try {
			return getMessageList("links", Announce.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param links Active Links
	 */
	public VehicleLinks setLinks(java.util.Collection<Announce> links) {
		values.put("links", links);
		return this;
	}

}
