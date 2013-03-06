/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: IMCNode.java 333 2013-01-02 11:11:44Z zepinto                         $:
 */
package pt.up.fe.dceg.neptus.imc.net;

import pt.up.fe.dceg.neptus.imc.IMCMessage;

public class IMCNode {

	protected int imcId = 0xFFFF;
	protected String sys_name;
	protected String sys_type;
	protected long last_heard;
	protected IMCMessage lastAnnounce;
	protected String address;
	protected int port;
	
	public int getImcId() {
		return imcId;
	}

	public void setImcId(int imcId) {
		this.imcId = imcId;
	}

	public String getSys_name() {
		return sys_name;
	}

	public void setSys_name(String sys_name) {
		this.sys_name = sys_name;
	}

	public String getSys_type() {
		return sys_type;
	}

	public void setSys_type(String sys_type) {
		this.sys_type = sys_type;
	}

	public long getLast_heard() {
		return last_heard;
	}

	public void setLast_heard(long last_heard) {
		this.last_heard = last_heard;
	}

	public IMCMessage getLastAnnounce() {
		return lastAnnounce;
	}

	public void setLastAnnounce(IMCMessage lastAnnounce) {
		this.lastAnnounce = lastAnnounce;		
		this.sys_name = lastAnnounce.getString("sys_name");
		this.sys_type = lastAnnounce.getString("sys_type");
		this.imcId = lastAnnounce.getHeader().getInteger("src");
		this.last_heard = System.currentTimeMillis();
		
		String[] services = lastAnnounce.getString("services").split(";");
		for (String serv : services) {
			if (serv.startsWith("imc+udp://")) {
				String s = serv.substring(10);
				s = s.replaceAll("/", "");
				String[] parts = s.split(":");
				this.address = parts[0];
				this.port = Integer.parseInt(parts[1]);
							
			}
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public IMCNode(IMCMessage announceMessage) {
		setLastAnnounce(announceMessage);
	}

	protected long getAgeMillis() {
		return System.currentTimeMillis() - last_heard; 
	}
	
}
