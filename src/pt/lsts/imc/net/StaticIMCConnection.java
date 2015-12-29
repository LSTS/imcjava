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
 * $Id:: StaticIMCConnection.java 333 2013-01-02 11:11:44Z zepinto             $:
 */
package pt.lsts.imc.net;

import java.util.Arrays;

import pt.lsts.imc.EntityList;
import pt.lsts.imc.EntityList.OP;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.state.ImcSystemState;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

/**
 * This class provides a simplified interface for sending and receiving IMC
 * messages using a static (fixed ip/port) peer-to-peer connection through UDP.
 * Example:<br/>
 * <code><pre>
 *  // Listen for IMC messages on port 6070 and all messages sent will go to 127.0.0.1:6002
 *  StaticIMCConnection conn = new StaticIMCConnection("127.0.0.1", 6002, 6070);
 *  
 *  // activate listening for messages
 *  conn.setPolling(true); 
 *  
 *  // wait up to 1 second to receive a message of type "EstimatedState"
 *  IMCMessage m = conn.recv("EstimatedState", 1000); 
 *  
 *  if (m != null)
 *      m.dump(System.out);     // Print out the message if it was received
 *  
 *  // Print all incoming Temperature messages to screen
 *  while(true) {
 *      System.out.println(conn.poll("Temperature"));
 *  }
 * </pre></code>
 */
public class StaticIMCConnection {

	private String remoteHost;
	private int remotePort;
	private UDPTransport trans;
	private boolean polling = false;
	private ImcSystemState state;
	
	private MessageListener<MessageInfo, IMCMessage> pollingListener = new MessageListener<MessageInfo, IMCMessage>() {
		@Override
		public void onMessage(MessageInfo info, IMCMessage msg) {
			state.setMessage(msg);
		}
	};

	/**
	 * Create a connection to a remote IMC host
	 * 
	 * @param remoteHost
	 *            The IP address of the remote host. Examples:
	 *            <code>"127.0.0.1"</code>, <code>"192.168.0.109"</code>.
	 * @param remotePort
	 *            The remote port to connect to.
	 * @param localPort
	 *            The local port to bind to and listen for incoming messages
	 */
	public StaticIMCConnection(String remoteHost, int remotePort, int localPort) {

		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.state = new ImcSystemState(IMCDefinition.getInstance());
		trans = new UDPTransport(localPort, 1);
		trans.sendMessage(remoteHost, remotePort, new Heartbeat());
		trans.sendMessage(remoteHost, remotePort, new EntityList(OP.QUERY, null));
	}

	/**
	 * Send an IMC message to the remote host
	 * 
	 * @param m
	 *            The message to be sent
	 * @return <code>true</code> if no error was found while sending. Since we
	 *         are using UDP no guarantee is given in terms of successful
	 *         message delivery, all we know is that the message was sent out.
	 */
	public boolean send(IMCMessage m) {
		m.setTimestamp(System.currentTimeMillis() / 1000.0);
		return trans.sendMessage(remoteHost, remotePort, m);
	}

	/**
	 * Stop this connection and stops listening for messages.
	 */
	public void stop() {
		trans.stop();
	}

	/**
	 * Activates/deactivates polling mechanism for listening for incoming
	 * messages. <br/>
	 * When active, previous message of any type can be returned instantaneously
	 * by using the method {@link #poll(String)}
	 * 
	 * @param polling
	 *            Whether to use polling of messages.
	 */
	public void setPolling(boolean polling) {
		boolean before = this.polling;
		this.polling = polling;
		state.clear();

		if (polling && !before) {
			trans.addMessageListener(pollingListener);
		} else if (!polling) {
			trans.removeMessageListener(pollingListener);
		}
	}

	private IMCMessage message;

	/**
	 * Synchronously wait for a message of a specific type
	 * 
	 * @param abbrev
	 *            The message type you want to receive. Examples:
	 *            <code>"Heartbeat"</code>, <code>EstimatedState</code>, etc.
	 * @param timeoutMillis
	 *            Maximum amount of time to wait for a message to be received in
	 *            milliseconds after which <code>null</code> will be returned
	 *            ([] in Matlab).
	 * @return A message of given type or <code>null</code> if no new message of
	 *         that type was received in <code>timeoutMillis</code> milliseconds
	 */
	public synchronized IMCMessage recv(String abbrev, long timeoutMillis) {
		message = null;
		long targetMillis = System.currentTimeMillis() + timeoutMillis;

		MessageListener<MessageInfo, IMCMessage> listener = new MessageListener<MessageInfo, IMCMessage>() {

			@Override
			public synchronized void onMessage(MessageInfo info,
					IMCMessage msg) {
				try {
					message = msg;
					trans.removeMessageListener(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		trans.addListener(listener, Arrays.asList(abbrev));

		while (message == null && System.currentTimeMillis() < targetMillis) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return message;
	}

	/**
	 * Retrieve the last received message of a given type. This method also
	 * (re)activates polling if it is not currently active.
	 * 
	 * @param abbrev
	 *            The type of message to be retrieved. Examples:
	 *            <code>"Heartbeat"</code>, <code>EstimatedState</code>, etc.
	 * @return The last received message of given type or <code>null</code> if
	 *         no message of that type was yet received in this connection.
	 * @see #setPolling(boolean)
	 */
	public IMCMessage poll(String abbrev) {
		if (!polling)
			setPolling(true);

		return state.get(abbrev);
	}

	/**
	 * Receive a message of given class. Similar to {@link #recv(String, long)}
	 * but the message gets cast to selected class.
	 * 
	 * @param clazz
	 *            The class of the message to be received.
	 * @param timeoutMillis
	 *            Amount of time to wait for a new message, in milliseconds.
	 * @return A message of given class or <code>null</code> if no new message
	 *         of that type was received in <code>timeoutMillis</code>
	 *         milliseconds
	 * @see #recv(String, long)
	 */
	public synchronized <T extends IMCMessage> T recv(Class<T> clazz,
			long timeoutMillis) {
		message = null;
		long targetMillis = System.currentTimeMillis() + timeoutMillis;

		try {
			MessageListener<MessageInfo, IMCMessage> listener = new MessageListener<MessageInfo, IMCMessage>() {

				@Override
				public synchronized void onMessage(MessageInfo info,
						IMCMessage msg) {
					try {
						message = msg;
						trans.removeMessageListener(this);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};

			trans.addListener(listener, Arrays.asList(clazz.getSimpleName()));
			while (message == null && System.currentTimeMillis() < targetMillis) {
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (message != null) {
				return clazz.getConstructor(IMCMessage.class).newInstance(
						message);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retrieve the state of this connection
	 * @return the state
	 * @see ImcSystemState
	 */
	public ImcSystemState state() {
		return state;
	}

	public static void main(String[] args) throws Exception {
		IMCProtocol proto = new IMCProtocol(6006);
		while(true) {
			System.out.println(proto.state("lauv-xtreme-2").last(EstimatedState.class));			
			Thread.sleep(3000);
		}
	}
}
