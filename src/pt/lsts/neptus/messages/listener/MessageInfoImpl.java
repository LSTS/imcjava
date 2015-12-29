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
 * $Id:: MessageInfoImpl.java 334 2013-01-02 11:20:47Z zepinto                 $:
 */
package pt.lsts.neptus.messages.listener;

import java.io.PrintStream;
import java.util.Hashtable;

/**
 * @author pdias
 *
 */
public class MessageInfoImpl implements MessageInfo {

//	private long timeReceivedNanos = -1;
//	private long timeSentNanos = -1;
	private double timeReceivedSec = -1;
	private double timeSentSec = -1;
	
	private Hashtable<String, Object> properties = new Hashtable<String, Object>();

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getTimeReceivedSec()
	 */
	@Override
	public double getTimeReceivedSec() {
		return timeReceivedSec;
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setTimeReceivedSec(double)
	 */
	@Override
	public void setTimeReceivedSec(double timeReceived) {
		timeReceivedSec = timeReceived;
	}
	
	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getTimeReceived()
	 */
	@Override
	public long getTimeReceivedNanos() {
		return (long) (this.timeReceivedSec * 1E9);
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setTimeReceived(long)
	 */
	@Override
	public void setTimeReceivedNanos(long timeReceived) {
		this.timeReceivedSec = timeReceived / 1E9;
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getTimeSentSec()
	 */
	@Override
	public double getTimeSentSec() {
		return timeSentSec;
	}
	
	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setTimeSentSec(double)
	 */
	@Override
	public void setTimeSentSec(double timeSent) {
		timeSentSec = timeSent;
	}
	
	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getTimeSent()
	 */
	@Override
	public long getTimeSentNanos() {
		return (long) (this.timeSentSec * 1E9);
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setTimeSent(long)
	 */
	@Override
	public void setTimeSentNanos(long timeSent) {
		this.timeSentSec = timeSent / 1E9;
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getProperty(java.lang.String)
	 */
	@Override
	public String getProperty(String name) {
		Object prop = this.properties.get(name);
		return (prop == null)?null:prop.toString();
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setProperty(java.lang.String, java.lang.String)
	 */
	@Override
	public void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

	public void setProperty(String name, Object value) {
		this.properties.put(name, value);
	}
	
	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getPublisher()
	 */
	@Override
	public String getPublisher() {
		return getProperty(PUBLISHER_KEY);
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getPublisherInetAddress()
	 */
	@Override
	public String getPublisherInetAddress() {
		return getProperty(PUBLISHER_INET_ADDRESS_KEY);
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#getPublisherPort()
	 */
	@Override
	public int getPublisherPort() {
		try {
			return Integer.parseInt(getProperty(PUBLISHER_PORT_KEY));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setPublisher(java.lang.String)
	 */
	@Override
	public void setPublisher(String value) {
		setProperty(PUBLISHER_KEY, value);
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setPublisherInetAddress(java.lang.String)
	 */
	@Override
	public void setPublisherInetAddress(String value) {
		setProperty(PUBLISHER_INET_ADDRESS_KEY, value);
	}

	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#setPublisherPort(short)
	 */
	@Override
	public void setPublisherPort(int value) {
		setProperty(PUBLISHER_PORT_KEY, value);
	}
	
	/* (non-Javadoc)
	 * @see pt.up.fe.dceg.neptus.messages.listener.MessageInfo#dump()
	 */
	@Override
	public void dump(PrintStream out) {
		out.println("__MessageInfo Properties_______");
		for (String key : properties.keySet()) {
			out.println("  " + key + " :: " + getProperty(key));
		}
		out.println("_______________________________");
	}
}
