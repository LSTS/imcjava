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
 */
package pt.lsts.imc.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pt.lsts.imc.Abort;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageInfoImpl;
import pt.lsts.neptus.messages.listener.MessageListener;

public class TcpTransport {

	protected boolean bound = false;
	protected ServerSocket socket;
	protected ExecutorService executor = Executors.newCachedThreadPool();
	
	protected LinkedHashMap<MessageListener<MessageInfo, IMCMessage>, HashSet<Integer>> messageListeners = new LinkedHashMap<MessageListener<MessageInfo, IMCMessage>, HashSet<Integer>>();
	protected LinkedHashMap<Integer, HashSet<MessageListener<MessageInfo, IMCMessage>>> messagesListened = new LinkedHashMap<Integer, HashSet<MessageListener<MessageInfo, IMCMessage>>>();

	protected void dispatch(IMCMessage msg) {
		MessageInfoImpl info = new MessageInfoImpl();
		info.setTimeReceivedSec(System.currentTimeMillis() / 1000.0);

		Vector<MessageListener<MessageInfo, IMCMessage>> listeners = new Vector<MessageListener<MessageInfo, IMCMessage>>();
		listeners.addAll(messageListeners.keySet());
		for (MessageListener<MessageInfo, IMCMessage> lst : listeners) {
			try {
				if (messageListeners.containsKey(lst) && (messageListeners.get(lst).isEmpty()
						|| messageListeners.get(lst).contains(msg.getHeader().getInteger("mgid"))))
					lst.onMessage(info, msg);

			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
		}
	}
	
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l, Collection<Integer> typesToListen) {
    	HashSet<Integer> types = new HashSet<Integer>();
        types.addAll(typesToListen);

        for (int id : typesToListen) {
            if (!messagesListened.containsKey(id))
                messagesListened.put(id, new HashSet<MessageListener<MessageInfo, IMCMessage>>());
            messagesListened.get(id).add(l);
        }		

        messageListeners.put(l, types);
    }	

    public void addListener(MessageListener<MessageInfo, IMCMessage> l, Collection<String> typesToListen) {
        Vector<Integer> types = new Vector<Integer>();
        for (String s : typesToListen) {
            try {
                types.add(IMCDefinition.getInstance().getMessageId(s));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }		
        addMessageListener(l, types);		
    }

    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        messageListeners.put(l, new HashSet<Integer>()); // empty means all
        for (String s : IMCDefinition.getInstance().getMessageNames()) {
            try {
                int id = IMCDefinition.getInstance().getMessageId(s);
                if (!messagesListened.containsKey(id))
                    messagesListened.put(id, new HashSet<MessageListener<MessageInfo, IMCMessage>>());
                messagesListened.get(id).add(l);
            }
            catch (Exception e) {
                e.printStackTrace();
            }			
        }
    }

    public void removeMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        messageListeners.remove(l);
    }

	public void shutdown() {
		bound = false;
		executor.shutdown();		
		try {
			socket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Future<Boolean> send(final String host, final int port, final IMCMessage msg, final int timeoutMillis) {		
		return executor.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				Socket socket = new Socket(host, port);
				socket.setSoTimeout(timeoutMillis);
				IMCOutputStream ios = new IMCOutputStream(socket.getOutputStream());
				msg.serialize(ios);
				socket.close();
				return true;
			}
		});
	}
	
	public static void sendMessage(final String host, final int port, final IMCMessage msg, final int timeoutMillis)
			throws Exception {
		Socket socket = new Socket(host, port);
		socket.setSoTimeout(timeoutMillis);
		IMCOutputStream ios = new IMCOutputStream(socket.getOutputStream());
		msg.serialize(ios);
		socket.close();
	}

	public void bind(int port) throws Exception {
		bound = true;
		socket = new ServerSocket(port);
		Thread serverSocket = new Thread("TCP Server") {
			public void run() {
				while (bound) {
					try {
						Socket connection = socket.accept();
						ClientHandler handler = new ClientHandler(TcpTransport.this, connection);
						handler.start();
					}
					catch (Exception e) {

					}
				}
				try {
					socket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		serverSocket.start();
	}

	class ClientHandler extends Thread {

		protected TcpTransport transport;
		protected Socket clientConnection;
		protected IMCInputStream input;
		public ClientHandler(TcpTransport transport, Socket clientConnection) throws IOException {
			this.transport = transport;
			this.clientConnection = clientConnection;
			input = new IMCInputStream(clientConnection.getInputStream(), IMCDefinition.getInstance());
		}

		@Override
		public void run() {
			while (!clientConnection.isClosed()) {
				try {
					IMCMessage msg = IMCDefinition.getInstance().nextMessage(input);
					if (msg != null)
						transport.dispatch(msg);
				}
				catch (EOFException e) {
					try {
						clientConnection.close();						
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		TcpTransport transport = new TcpTransport();
		transport.bind(9001);
		long start = System.currentTimeMillis();
		int count = 0;
		while(System.currentTimeMillis() - start < 1000) {
			transport.send("localhost", 6001, new Abort(), 5000);
			count++;
		}
		System.out.println(count);
		transport.shutdown();
	}
}
