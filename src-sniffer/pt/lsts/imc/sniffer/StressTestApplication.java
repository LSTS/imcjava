/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: StressTestApplication.java 333 2013-01-02 11:11:44Z zepinto           $:
 */
package pt.lsts.imc.sniffer;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.net.UDPTransport;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

public class StressTestApplication {


	protected int freq = 1000;
	protected String messageToSend = "EstimatedState";
	protected String fieldToFillWithTimeStamp = "x";
	protected String fieldToFillWithCounter = "y";
	protected String fieldToFillWithFrequency = "z";
	protected long counter = 0;
	protected String destination = "193.137.157.76";
	protected int port = 6001;


	public void startReceiving() throws Exception {		
	}

	public void receive() {
		final UDPTransport transport = new UDPTransport(6001, 1);
		transport.addMessageListener(new MessageListener<MessageInfo, IMCMessage>() {

			//long millisStart = System.currentTimeMillis();
			//long count = 0;

			@Override
			public void onMessage(MessageInfo info, IMCMessage message) {				
				//	System.out.println(message.getDouble(fieldToFillWithCounter));			
			}
		});

		new Thread(new Runnable() {
			long lastTime = System.currentTimeMillis();
			@Override
			public void run() {
				while (true) {
					try {						
						lastTime = System.currentTimeMillis();
						transport.printStatistics();
						Thread.sleep(lastTime+1000 - System.currentTimeMillis());
						
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			};				
		}).start();
			
		while (true) 
		try {
			Thread.sleep(1000);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}



	public StressTestApplication(boolean sendApp) throws Exception {


		if (!sendApp) {
			receive();
			System.exit(0);
		}

		UDPTransport transport = new UDPTransport(1);
		IMCMessage msg = IMCDefinition.getInstance().create(messageToSend);		
		int messagesLeft = freq;
		long startMillis = 0;

		while (true) {
			if (System.currentTimeMillis() > startMillis+1000) {
				startMillis = System.currentTimeMillis();
				messagesLeft = freq;			
				System.out.println(counter);
			}
			if (messagesLeft > 0) {
				if (fieldToFillWithTimeStamp != null)
					msg.setValue(fieldToFillWithTimeStamp, System.currentTimeMillis()/1000.0);
				if (fieldToFillWithCounter != null)
					msg.setValue(fieldToFillWithCounter, counter++);
				if (fieldToFillWithFrequency != null)
					msg.setValue(fieldToFillWithFrequency, freq);

				transport.sendMessage(destination, port, msg);
				messagesLeft--;
			}
			long millisLeft = (startMillis+1000) - System.currentTimeMillis();
			//System.out.println(millisLeft+", "+messagesLeft);

			if (messagesLeft > 0 && millisLeft > 0)
				Thread.sleep(millisLeft/messagesLeft);
		}		
	}

	public static void main(String[] args) throws Exception {
		new StressTestApplication(true);
	}


}