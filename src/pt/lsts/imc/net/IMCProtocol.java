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
 * $Id:: IMCProtocol.java 333 2013-01-02 11:11:44Z zepinto                     $:
 */
package pt.lsts.imc.net;

import java.io.File;
import java.io.FileInputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;

import pt.lsts.imc.Announce;
import pt.lsts.imc.Announce.SYS_TYPE;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndex;
import pt.lsts.imc.state.ImcSysState;
import pt.lsts.neptus.messages.listener.Consume;
import pt.lsts.neptus.messages.listener.ImcConsumer;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

/**
 * This class implements the IMC protocol allowing sending / receiving messages and also discovery of IMC peers
 * <br/><br/>Example - wait until seacon-2 sends an EstimatedState message and print it to the screen:
 * <pre>IMCProtocol imc = new IMCProtocol(6001);
 *ImcSysState seacon2 = imc.state("lauv-seacon-2");
 *EstimatedState state = seacon2.pollEstimatedState(30000);
 *if (state != null)
 *    state.dump(System.out);
 * </pre>
 * @author zp
 */
public class IMCProtocol implements IMessageBus {

    protected UDPTransport discovery;
    protected UDPTransport comms;
    protected LinkedHashMap<Integer, IMCNode> announces = new LinkedHashMap<Integer, IMCNode>();
    protected int bindPort = 7001;
    protected int announceId = Announce.ID_STATIC;
    protected short localId = (short)System.getProperty("user.name").hashCode();
    protected LinkedHashMap<String, ImcSysState> sysStates = new LinkedHashMap<String, ImcSysState>();
    protected LinkedHashMap<Integer, String> sysNames = new LinkedHashMap<Integer, String>();
    protected LinkedHashMap<String, Integer> sysIds = new LinkedHashMap<String, Integer>();    
    protected String localName = "imcj_" + System.currentTimeMillis() / 500;

    protected LinkedHashMap<Integer, LinkedList<Integer>> acks = new LinkedHashMap<Integer, LinkedList<Integer>>();
    
    private MessageListener<MessageInfo, IMCMessage> messageListener = new MessageListener<MessageInfo, IMCMessage>() {
        @Override
        public void onMessage(MessageInfo info, IMCMessage msg) {
            if (msg.getMgid() == announceId) {
                int src_id = msg.getSrc();
                if (!announces.containsKey(src_id)) {
                    announces.put(src_id, new IMCNode(msg));                    
                    IMCDefinition.getInstance().getResolver().addEntry(msg.getSrc(), msg.getString("sys_name"));
                }
                else
                    announces.get(src_id).setLastAnnounce(msg);
            }
        }
    };

    private static Collection<String> getNetworkInterfaces(boolean includeLoopback) {
        Vector<String> itfs = new Vector<String>();
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                try {
                    if (ni.isLoopback() && !includeLoopback)
                        continue;
                }
                catch (Exception e) {
                    continue;
                }

                Enumeration<InetAddress> adrs = ni.getInetAddresses();
                while (adrs.hasMoreElements()) {
                    InetAddress addr = adrs.nextElement();
                    if (addr instanceof Inet4Address)
                        itfs.add(addr.getHostAddress());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return itfs;
    }

    public final String getLocalName() {
        return localName;
    }
    
    public int getLocalId() {
    	return localId;
    }
    
    public Announce buildAnnounce() {
    	Announce announce = new Announce();
        announce.setSysType(SYS_TYPE.CCU);
        announce.setSysName(localName);
        announce.setSrc(localId);

        String services = "";

        for (String itf : getNetworkInterfaces(true)) {
            services += "imc+udp://" + itf + ":" + bindPort + "/;";
        }
        if (services.length() > 0)
            services = services.substring(0, services.length() - 1);

        announce.setServices(services);
        
        return announce;
    }

    private Thread discoveryThread = new Thread() {

    	{
    		setDaemon(true);
    	}
        public void run() {

            int port = 30100;

            while (true) {
                // system.out.println("[IMCTransport] Trying to bind to port " + port + "...");
                discovery = new UDPTransport(true, true, port, 1);
                discovery.setImcId(localId);
                if (discovery.isOnBindError()) {
                    port++;
                    if (port > 30104)
                        port = 30100;
                }
                else
                    break;
                System.out.println("[IMCTransport] Announcer thread bound to port " + port + "...");
            }

            discovery.addMessageListener(messageListener);

            final Announce announce = buildAnnounce();

            long lastSent = System.currentTimeMillis();
            while (true) {
                for (int p = 30100; p < 30105; p++)
                    discovery.sendMessage("224.0.75.69", p, announce);

                lastSent = System.currentTimeMillis();

                try {
                    Thread.sleep(10000-(System.currentTimeMillis()-lastSent));
                }
                catch (InterruptedException e) {
                    break;
                }
            }
        }
    };
    
    public static void announce(String sysname, int sysid, SYS_TYPE type, UDPTransport transport) {
    	final Announce announce = new Announce();
        announce.setSysType(type);
        announce.setSysName(sysname);
        announce.setSrc(sysid);

        String services = "";

        for (String itf : getNetworkInterfaces(true)) {
            services += "imc+udp://" + itf + ":" + transport.getBindPort() + "/;";
        }
        if (services.length() > 0)
            services = services.substring(0, services.length() - 1);

        announce.setServices(services);

        for (int p = 30100; p < 30105; p++)
        	transport.sendMessage("224.0.75.69", p, announce);                
    }
    
    public static Thread heartBeatThread(final String host, final int port, final UDPTransport transport) {
    	Thread heartbeater = new Thread("Heartbeater to "+host) {
    		public void run() {
    			while (true) {
    				
    				Heartbeat beat = new Heartbeat();
    				
    				transport.sendMessage(host, port, beat);
    				
    				try {
    					Thread.sleep(1000);
    					System.out.println(beat);
    				}
    				catch (InterruptedException e) {
    					return;
    				}
    			}
    		};
    	};
    	return heartbeater;
    }

    /**
     * Retrieve time elapsed since last announce of given system name
     * @param name The name of the system
     * @return Time, in milliseconds since last announce has been received from the given system. <br/>
     * In the case the system has not announced itself yet, -1 is returned.
     */
    public long announceAgeMillis(String name) {
        for (IMCNode node : announces.values()) {
            if (node.getSys_name().equals(name))
                return node.getAgeMillis();
        }
        return -1;
    }


    IMCNode getNode(String sys_name) {
        for (IMCNode node : announces.values()) {
            if (node.getSys_name().equals(sys_name))
                return node;
        }
        return null;
    }

    public boolean sendHeartbeat(String remoteSystem) {
        IMCNode node = getNode(remoteSystem);
        if (node == null || node.getAddress() == null)
            return false;

        Heartbeat msg = IMCDefinition.getInstance().create(Heartbeat.class, "src", localId, "dst", node.getImcId(),
                "timestamp", System.currentTimeMillis() / 1000.0);                

        return comms.sendMessage(node.getAddress(), node.getPort(), msg);
    }
    
    
    public IMCProtocol(String localName, int localPort) {
    	IMCDefinition.getInstance();
        this.bindPort = localPort;
        comms = new UDPTransport(bindPort, 1);
        this.localName = localName;
        discoveryThread.start();

        try {
            while (discovery == null) { 
                Thread.sleep(500);
            }
        }
        catch (Exception e) {       
            e.printStackTrace();
        }
        addMessageListener(new MessageListener<MessageInfo, IMCMessage>() {

            @Override
            public void onMessage(MessageInfo info, IMCMessage msg) {
                setMessage(msg);
                post(msg);
            }
        });
    }
    

    /**
     * Create a new IMCProtocol instance and bind it to given local port
     * @param bindPort The port where to bind for listening to incoming messages (also advertised using multicast)
     */
    public IMCProtocol(int bindPort) {
    	this("imcj_" + System.currentTimeMillis() / 500, bindPort);
    }

    
    public IMCProtocol() {
    	this("imcj_" + System.currentTimeMillis() / 500, 8000+(int)Math.random()*1000);
    }
    
    /**
     * Send message to a remote system
     * @param sysName The name of the system where to send the message
     * @param msg The message to be sent to the system
     * @return <strong>true</strong> if the message was sent or <strong>false</strong> if no such system is known yet.
     */
    public boolean sendMessage(String sysName, IMCMessage msg) {
        msg.setValue("src", localId);
        msg.setTimestamp(System.currentTimeMillis() / 1000.0);
        msg.setValue("dst", IMCDefinition.getInstance().getResolver().resolve(sysName));
        for (IMCNode nd : announces.values()) {
            if (nd.sys_name.equals(sysName)) {
                if (nd.address != null) {
                    msg.setValue("dst", nd.imcId);
                    comms.sendMessage(nd.address, nd.port, msg);
                    post(msg);
                    return true;
                }
                else
                    return false;
            }
        }
        
        
        return false;
    }
    
    private LinkedHashMap<Object, ImcConsumer.Itf> pojoSubscribers = new LinkedHashMap<Object, ImcConsumer.Itf>();
    
    public void register(Object consumer) {
    	unregister(consumer);
    	
    	ImcConsumer.Itf listener = ImcConsumer.create(consumer);
    	
    	if (listener.getTypesToListen() == null)
    		addMessageListener(listener, new ArrayList<String>());
    	else if (listener.getTypesToListen().isEmpty())
    		return;
    	else
    		addMessageListener(listener, listener.getTypesToListen());
    	
    	pojoSubscribers.put(consumer, listener);
    }
    
    public void unregister(Object consumer) {
    	if (pojoSubscribers.containsKey(consumer))
    		removeMessageListener(pojoSubscribers.get(consumer));
    	pojoSubscribers.remove(consumer);
    }
    
    
    public void post(Object event) {
    	//FIXME post locally
    	if (event instanceof IMCMessage) {
    		
    	}
    }

    /**
     * Add a listener to be called whenever messages of certain types are received
     * @param listener The listener to be added
     * @param typesToListen The list of message abbreviated names to be observed by this listener
     */
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> listener, String ... typesToListen) {
        addMessageListener(listener, Arrays.asList(typesToListen));
    }

    /**
     * Add a listener to be called whenever messages of certain types are received
     * @param l The listener to be added
     * @param typesToListen Collection of abbreviated names to be observed by this listener
     */
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l, Collection<String> typesToListen) {
        comms.addListener(l, typesToListen);
        discovery.addListener(l, typesToListen);
    }

    /**
     * Remove a previously added message listener
     * @param l The listener to be removed from the observers
     */
    public void removeMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        comms.removeMessageListener(l);
        discovery.removeMessageListener(l);
    }

    /**
     * Add a global message listener that will be call on <strong>ALL</strong> incoming messages
     * @param l The global listener to be added to the list of observers
     */
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        comms.addMessageListener(l);
        discovery.addMessageListener(l);
    }

    /**
     * Add a listener that will be called once and then removed from the list of observers
     * @param listener The listener to be added as a single-shot listener
     * @param typeToListen The type of message to be listen to
     */
    public void addSingleShotListener(MessageListener<MessageInfo, IMCMessage> listener, String typeToListen) {

        final MessageListener<MessageInfo, IMCMessage> list = listener;
        MessageListener<MessageInfo, IMCMessage> singleShot = new MessageListener<MessageInfo, IMCMessage>() {
            public void onMessage(MessageInfo info, IMCMessage msg) {
                comms.removeMessageListener(this);
                discovery.removeMessageListener(this);                
                list.onMessage(info, msg);
            }
        };

        comms.addListener(singleShot, Arrays.asList(typeToListen));
        discovery.addListener(singleShot, Arrays.asList(typeToListen));        
    }

    /**
     * Retrieve a list of known system names (from which an announce has been received)
     * @return list of known system names
     */
    public String[] systems() {
        return sysIds.keySet().toArray(new String[0]);
    }

    protected void setMessage(IMCMessage message) {
        if (message.getMgid() == Announce.ID_STATIC && message.getSrc() != getLocalId()) {  
            sysNames.put(message.getSrc(), message.getString("sys_name"));
            sysIds.put(message.getString("sys_name"), message.getSrc());
            
            sendMessage(message.getString("sys_name"), buildAnnounce());
            //sendMessage(message.getString("sys_name"), buildAnnounce());
            sendMessage(message.getString("sys_name"), new Heartbeat());
            //sendMessage(message.getString("sys_name"), new Heartbeat());     
            //sendMessage(message.getString("sys_name"), new Heartbeat());
        }

        String sysName = sysNames.get(message.getSrc());
        if (sysName == null)
            return;

        if (!sysStates.containsKey(sysName))
            sysStates.put(sysName, new ImcSysState());

        sysStates.get(sysName).setMessage(message);
    }
    
    public String[] lookupService(String serviceName) {
    	Vector<String> systems = new Vector<String>();
    	
    	for (String sys : systems()) {
    		Announce last = state(sys).lastAnnounce();
    		System.out.println(last.getServices());
    	}
    	return systems.toArray(new String[0]);
    }

    /**
     * Retrieve the continuously updated state of the given system
     * @param name The system for which to retrieve the state
     * @return The existing system state or a newly created state (inactive) if that system is not yet known
     */
    public ImcSysState state(String name) {
        if (!sysStates.containsKey(name)) {
            sysStates.put(name, new ImcSysState());
        }
        return sysStates.get(name);
    }

    protected Thread replayThread = null;

    /**
     * Replay an LSF log folder
     * @param dirToReplay The folder where the files Data.lsf and IMC.xml can be found
     * @param speed The time multiplier (1.0 = real time)
     * @throws Exception In the case the folder cannot be read or any other IO errors
     */
    public void startReplay(String dirToReplay, double speed) throws Exception {

        final LsfIndex index = new LsfIndex(new File(dirToReplay, "Data.lsf"), IMCDefinition.getInstance(
                new FileInputStream(new File(dirToReplay, "IMC.xml"))));

        final double sec = 1000.0 * speed;
        replayThread = new Thread() {
            @Override
            public void run() {

                int src = index.sourceOf(0);
                double start = index.timeOf(0);
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < index.getNumberOfMessages(); i++) {
                    double curTime = (System.currentTimeMillis() - startMillis)/sec + start;
                    IMCMessage m = index.getMessage(i);
                    if (m.getSrc() == src) {
                        while (m.getTimestamp()> curTime) {
                            try {
                                Thread.sleep(10);
                            }
                            catch (InterruptedException e) {
                                return;
                            }
                            curTime = (System.currentTimeMillis() - startMillis)/sec + start;
                        }
                    }
                    messageListener.onMessage(null, m);
                }
            }
        };
        replayThread.start();
    }

    /**
     * Stop replaying
     */
    public void stopReplay() {
        if (replayThread != null)
            replayThread.interrupt();
    }

    /**
     * Stop this IMCProtocol instance (closes all sockets)
     */
    public void stop() {
        stopReplay();

        if (discoveryThread != null)
            discoveryThread.interrupt();

        if (comms != null)
            comms.stop();

        if (discovery != null)
            discovery.stop();
    }

    public static void main(String[] args) throws Exception {

    	IMCProtocol proto = new IMCProtocol(7001);
    	proto.register(new Object() {
    		
    		@Consume
    		public void on(EstimatedState state) {
    			System.out.println("Got an estimated state from "+state.getSourceName());
    		}
    	});
    	
    	Thread.sleep(30000);
    }
}
