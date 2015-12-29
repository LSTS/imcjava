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
 * $Id:: IMCConnection.java 333 2013-01-02 11:11:44Z zepinto                   $:
 */
package pt.lsts.imc.net;

import java.util.Arrays;

import pt.lsts.imc.IMCMessage;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

public class IMCConnection {

    protected static IMCProtocol proto = null;
    protected String remoteSystem;
    protected boolean running = true;

    public IMCConnection(String remoteSystem) {
        if (proto == null)
            proto = new IMCProtocol(6005);
        
        this.remoteSystem = remoteSystem;

        Thread t = new Thread() {
            public void run() {

                while (running) {
                    long lastSent = System.currentTimeMillis();
                    if (isConnected())
                        IMCConnection.proto.sendHeartbeat(IMCConnection.this.remoteSystem);

                    try {
                        Thread.sleep(1000 - (System.currentTimeMillis() - lastSent));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
        };
        t.start();
    }

    public boolean send(IMCMessage m) {
        m.setTimestamp(System.currentTimeMillis() / 1000.0);
        return proto.sendMessage(remoteSystem, m);
    }

    public void stop() {
        running = false;
    }

    public boolean isConnected() {
        if (!running || proto.getNode(remoteSystem) == null || proto.getNode(remoteSystem).getAddress() == null)
            return false;
        return proto.getNode(remoteSystem).getAgeMillis() < 30000;
    }

    protected IMCMessage message;

    public synchronized IMCMessage recv(String abbrev, long timeoutMillis) {
        message = null;
        long targetMillis = System.currentTimeMillis() + timeoutMillis;

        try {
            MessageListener<MessageInfo, IMCMessage> listener = new MessageListener<MessageInfo, IMCMessage>() {

                @Override
                public synchronized void onMessage(MessageInfo info, IMCMessage msg) {
                    try {
                        if (proto.getNode(remoteSystem) == null
                                || proto.getNode(remoteSystem).getImcId() != msg.getSrc())
                            return;

                        message = msg;
                        proto.comms.removeMessageListener(this);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            proto.comms.addListener(listener, Arrays.asList(abbrev));

            while (message == null && System.currentTimeMillis() < targetMillis) {
                try {
                    Thread.sleep(10);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (message != null) {
                try {
                    Class<?> c = Class.forName("pt.up.fe.dceg.neptus.imc." + abbrev);
                    return (IMCMessage) c.getConstructor(IMCMessage.class).newInstance(message);
                }
                catch (Exception e) {
                    return message;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized <T extends IMCMessage> T recv(Class<T> clazz, long timeoutMillis) {
        message = null;
        long targetMillis = System.currentTimeMillis() + timeoutMillis;

        try {
            MessageListener<MessageInfo, IMCMessage> listener = new MessageListener<MessageInfo, IMCMessage>() {

                @Override
                public synchronized void onMessage(MessageInfo info, IMCMessage msg) {
                    try {
                        if (proto.getNode(remoteSystem) == null
                                || proto.getNode(remoteSystem).getImcId() != msg.getSrc())
                            return;

                        message = msg;
                        proto.comms.removeMessageListener(this);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            proto.comms.addListener(listener, Arrays.asList(clazz.getSimpleName()));
            while (message == null && System.currentTimeMillis() < targetMillis) {
                try {
                    Thread.sleep(10);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (message != null) {
                return clazz.getConstructor(IMCMessage.class).newInstance(message);
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        IMCConnection seacon3 = new IMCConnection("lauv-seacon-3");
        IMCConnection seacon1 = new IMCConnection("lauv-seacon-1");

        while(true) {
            if (seacon1.isConnected()) {
                System.out.println("Seacon-1 is connected");
                System.out.println(seacon1.recv("EstimatedState", 5000));
                
            }
            else {
                System.out.println("Seacon-1 disconnected");
            }
            
            if (seacon3.isConnected()) {
                System.out.println("Seacon-3 is connected");
                System.out.println(seacon3.recv("EstimatedState", 5000));
            }
            else {
                System.out.println("Seacon-3 disconnected");
            }
            
            Thread.sleep(100);
        }      
    }
}
