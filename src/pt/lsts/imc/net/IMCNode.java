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
 * $Id:: IMCNode.java 333 2013-01-02 11:11:44Z zepinto                         $:
 */
package pt.lsts.imc.net;

import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCMessage;

public class IMCNode {

	protected long last_heard;
	protected Announce lastAnnounce;
	protected boolean peer = false;
	protected String address;
	protected int port;

	protected int tcpport = -1;
	protected String tcpAddress = null;

	public int getImcId() {
		return lastAnnounce != null ? lastAnnounce.getSrc() : 0;
	}

	public String getSysName() {
		return lastAnnounce != null ? lastAnnounce.getSysName() : null;
	}

	public String getSysType() {
		return lastAnnounce != null ? lastAnnounce.getSysType().name() : null;
	}

	public long getLast_heard() {
		return lastAnnounce != null ? lastAnnounce.getTimestampMillis() : 0;
	}

	public IMCMessage getLastAnnounce() {
		return lastAnnounce;
	}

	private Pattern pUdp = Pattern
			.compile("imc\\+udp\\:\\/\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\:(\\d+)/");
	private Pattern pTcp = Pattern
			.compile("imc\\+tcp\\:\\/\\/(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\:(\\d+)/");

	public void setAnnounce(Announce announce) {

		long lastHeard = getAgeMillis();

		// only process relevant announces
		if (lastAnnounce != null && lastHeard < 1000)
			return;
		
		boolean processed = processAnnounce(announce);
		
		if (processed) {
			lastAnnounce = announce;
			this.last_heard = System.currentTimeMillis();
		}		
	}

	/**
	 * @param announce
	 *            The announce to be processed
	 * @return <code>true</code> if could find an address from this announce
	 */
	private boolean processAnnounce(Announce announce) {
		String[] services = announce.getString("services").split(";");
		String newAddress;

		LinkedHashSet<String> udpAddresses = new LinkedHashSet<String>();
		LinkedHashSet<String> tcpAddresses = new LinkedHashSet<String>();

		for (String serv : services) {
			Matcher mUdp = pUdp.matcher(serv);
			if (mUdp.matches()) {
				newAddress = mUdp.group(1) + "." + mUdp.group(2) + "."
						+ mUdp.group(3) + "." + mUdp.group(4);
				udpAddresses.add(newAddress);
				this.port = Integer.parseInt(mUdp.group(5));
			}

			Matcher mTcp = pTcp.matcher(serv);
			if (mTcp.matches()) {
				newAddress = mTcp.group(1) + "." + mTcp.group(2) + "."
						+ mTcp.group(3) + "." + mTcp.group(4);
				this.tcpport = Integer.parseInt(mTcp.group(5));
				tcpAddresses.add(newAddress);
			}
		}

		if (udpAddresses.size() == 1) {
			address = udpAddresses.iterator().next();
		} else if (udpAddresses.contains(announce.getMessageInfo()
				.getPublisherInetAddress())) {
			address = announce.getMessageInfo().getPublisherInetAddress();
		} else if (udpAddresses.size() > 1) {
			address = udpAddresses.iterator().next();
		} else {
			return false;
		}

		if (tcpAddresses.size() == 1) {
			tcpAddress = tcpAddresses.iterator().next();
		} else if (tcpAddresses.contains(announce.getMessageInfo()
				.getPublisherInetAddress())) {
			tcpAddress = announce.getMessageInfo().getPublisherInetAddress();
		} else if (tcpAddresses.size() > 1) {
			tcpAddress = tcpAddresses.iterator().next();
		}

		return true;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
		if (tcpAddress == null)
			tcpAddress = address;
	}

	public void setTcpAddress(String address) {
		this.tcpAddress = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
		if (tcpport == -1)
			tcpport = port;
	}

	public void setTcpPort(int port) {
		this.tcpport = port;
	}

	public int getTcpPort() {
		return tcpport;
	}

	public String getTcpAddress() {
		return tcpAddress;
	}

	public IMCNode(Announce announceMessage) {
		setAnnounce(announceMessage);
	}

	protected long getAgeMillis() {
		return System.currentTimeMillis() - last_heard;
	}

	/**
	 * @return the peer
	 */
	public boolean isPeer() {
		return getSysName() != null && peer;
	}

	/**
	 * @param peer
	 *            the peer to set
	 */
	public void setPeer(boolean peer) {
		this.peer = peer;
	}
	
	public boolean isConnected() {
		return getAgeMillis() < 60000;
	}

}
