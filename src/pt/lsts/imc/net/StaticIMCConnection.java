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
 * $Id:: StaticIMCConnection.java 333 2013-01-02 11:11:44Z zepinto             $:
 */
package pt.lsts.imc.net;

import java.util.Arrays;
import java.util.LinkedHashMap;

import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCMessage;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

public class StaticIMCConnection {

    protected String remoteHost;
    protected int localPort;
    protected int remotePort;
    protected boolean running = true;
    
    protected LinkedHashMap<Integer, IMCMessage> lastMessagesByType = new LinkedHashMap<Integer, IMCMessage>();
    protected boolean polling = false;
    protected MessageListener<MessageInfo, IMCMessage> pollingListener = new MessageListener<MessageInfo, IMCMessage>() {
        @Override
        public void onMessage(MessageInfo info, IMCMessage msg) {
            lastMessagesByType.put(msg.getMgid(), msg);
        }
    };
    
    protected UDPTransport trans;

    public StaticIMCConnection(String remoteHost, int remotePort, int localPort) {

        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.localPort = localPort;

        trans = new UDPTransport(localPort, 1);
        trans.sendMessage(remoteHost, remotePort, new Heartbeat());
    }

    public boolean send(IMCMessage m) {
        m.setTimestamp(System.currentTimeMillis() / 1000.0);
        return trans.sendMessage(remoteHost, remotePort, m);
    }

    public void stop() {
        running = false;
        trans.stop();
    }
    
    
    public void setPolling(boolean polling) {
        boolean before = this.polling;
        this.polling = polling;
        lastMessagesByType.clear();
        // if we started polling just now
        if (polling && ! before) {
            trans.addMessageListener(pollingListener);
        }
        else if (!polling) {
           trans.removeMessageListener(pollingListener); 
        }
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
                        message = msg;
                        trans.removeMessageListener(this);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            trans.addListener(listener, Arrays.asList(abbrev));

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
    
    public IMCMessage poll(String abbrev) {
        if (!polling)
            setPolling(true);            
        
        int id = trans.getDefinition().getMessageId(abbrev);
        if (id == -1)
            return null;
        
        IMCMessage msg = lastMessagesByType.get(id);
        if (msg == null)
            return null;
        try {
            Class<?> c = Class.forName("pt.up.fe.dceg.neptus.imc." + abbrev);
            return (IMCMessage) c.getConstructor(IMCMessage.class).newInstance(msg);
        }
        catch (Exception e) {
            return msg;
        }
    }

    public synchronized <T extends IMCMessage> T recv(Class<T> clazz, long timeoutMillis) {
        message = null;
        long targetMillis = System.currentTimeMillis() + timeoutMillis;

        try {
            MessageListener<MessageInfo, IMCMessage> listener = new MessageListener<MessageInfo, IMCMessage>() {

                @Override
                public synchronized void onMessage(MessageInfo info, IMCMessage msg) {
                    try {
                        message = msg;
                        trans.removeMessageListener(this);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            trans.addListener(listener, Arrays.asList(clazz.getSimpleName()));
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
        StaticIMCConnection seacon3 = new StaticIMCConnection("127.0.0.1", 6002, 6969);
        while(true) {
            System.out.println(seacon3.poll("EstimatedState"));
            System.out.println(seacon3.poll("EntityState"));
            Thread.sleep(100);
        }      
    }
}
