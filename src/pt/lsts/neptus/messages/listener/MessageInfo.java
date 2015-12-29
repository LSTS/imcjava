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
 * $Id:: MessageInfo.java 334 2013-01-02 11:20:47Z zepinto                     $:
 */
package pt.lsts.neptus.messages.listener;

import java.io.PrintStream;

/**
 * @author pdias
 * 
 */
public interface MessageInfo {

	public static final String PUBLISHER_KEY = "Publisher";
	public static final String PUBLISHER_INET_ADDRESS_KEY = "PublisherInetAddress";
	public static final String PUBLISHER_PORT_KEY = "PublisherPort";
	public static final String SUBSCRIBER_KEY = "Subscriber";
	public static final String NOT_TO_LOG_MSG_KEY = "NotToLogMessage";
	public static final String ENVELOPED_MSG_KEY = "Enveloped";
    public static final String WEB_FETCH_MSG_KEY = "Web Fetch";
    public static final String TRANSPORT_MSG_KEY = "Transport";

	public double getTimeSentSec();
	public void setTimeSentSec(double timeSent);

	public long getTimeSentNanos();
	public void setTimeSentNanos(long timeSent);

	public double getTimeReceivedSec();
	public void setTimeReceivedSec(double timeReceived);

	public long getTimeReceivedNanos();
	public void setTimeReceivedNanos(long timeReceived);

	public String getProperty(String name);
	public void setProperty(String name, String value);

	/**
	 * @return the publisher of the message. Should be the same 
	 * as {@link #getProperty(String)} with name {@value #PUBLISHER_KEY}
	 */
	public String getPublisher();
	/**
	 * Sets the publisher of the message. Should be the same 
	 * as {@link #setProperty(String, String))} with name {@value #PUBLISHER_KEY}
	 */
	public void setPublisher(String value);

	public String getPublisherInetAddress();
	public void setPublisherInetAddress(String value);

	public int getPublisherPort();
	public void setPublisherPort(int value);

	public void dump(PrintStream out);
}
