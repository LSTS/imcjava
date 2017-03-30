/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2017, Laboratório de Sistemas e Tecnologia Subaquática
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
 */
package pt.lsts.autonomy;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

import pt.lsts.imc.Abort;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.neptus.messages.listener.ImcConsumer;
import pt.lsts.neptus.messages.listener.MessageInfoImpl;
import pt.lsts.neptus.messages.listener.PeriodicCallbacks;

/**
 * @author zp
 *
 */
public class TcpClient extends Thread {

	private Socket socket = null;
	private String host;
	private int port;
	private HashSet<ImcConsumer> consumers = new HashSet<ImcConsumer>();
	private boolean connected = false;
	private IMCInputStream input = null;
	private IMCOutputStream output = null;

	public int remoteSrc = 0;
	public int localSrc = 0x555;

	public TcpClient() throws IOException {		
	}
	
	void connect(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		socket = new Socket(host, port);
		input = new IMCInputStream(socket.getInputStream(), IMCDefinition.getInstance());
		output = new IMCOutputStream(socket.getOutputStream());
		connected = true;
	}

	@Override
	public void run() {
		while (connected) {
			synchronized (socket) {
				try {
					while (input.available() > IMCDefinition.getInstance().headerLength()) {
						IMCMessage m = IMCDefinition.getInstance().nextMessage(input);
						if (m != null)
							dispatch(m);
					}
				} catch (Exception e) {
					try {
						socket.close();
						socket = null;
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				return;
			}
		}
	}

	public void send(IMCMessage m) throws IOException {
		m.setDst(remoteSrc);
		m.setSrc(localSrc);
		
		
		synchronized (socket) {
			try {
				output.writeMessage(m);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void dispatch(IMCMessage m) {
		if (remoteSrc == 0)
			remoteSrc = m.getSrc();
		MessageInfoImpl mi = new MessageInfoImpl();
		mi.setPublisherInetAddress(host);
		mi.setPublisherPort(port);
		mi.setPublisher(m.getSourceName());
		mi.setTimeReceivedSec(System.currentTimeMillis() / 1000.0);

		for (ImcConsumer consumer : consumers) {
			consumer.onMessage(mi, m);
		}
	}

	public synchronized void register(Object pojo) {
		PeriodicCallbacks.register(pojo);
		consumers.add(ImcConsumer.create(pojo));
	}

	public synchronized void unregister(Object pojo) {
		PeriodicCallbacks.unregister(pojo);
		ImcConsumer c = null;
		for (ImcConsumer consumer : consumers) {
			if (consumer.getPojo() == pojo) {
				c = consumer;
				break;
			}
		}

		if (c != null)
			consumers.remove(c);
	}

	public static void main(String[] args) throws Exception {
		TcpClient client = new TcpClient();
		client.connect("127.0.0.1", 6002);
		client.start();
		Thread.sleep(5000);
		client.send(new Abort());
		client.interrupt();
	}
}
