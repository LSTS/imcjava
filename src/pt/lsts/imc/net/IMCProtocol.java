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
 * $Id:: IMCProtocol.java 333 2013-01-02 11:11:44Z zepinto                     $:
 */
package pt.lsts.imc.net;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import pt.lsts.imc.Announce;
import pt.lsts.imc.Announce.SYS_TYPE;
import pt.lsts.imc.EntityInfo;
import pt.lsts.imc.EntityList;
import pt.lsts.imc.EntityList.OP;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndex;
import pt.lsts.imc.lsf.LsfMessageLogger;
import pt.lsts.imc.state.ImcSystemState;
import pt.lsts.neptus.messages.listener.ImcConsumer;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageInfoImpl;
import pt.lsts.neptus.messages.listener.MessageListener;
import pt.lsts.neptus.messages.listener.Periodic;
import pt.lsts.neptus.messages.listener.PeriodicCallbacks;
import pt.lsts.util.NetworkUtilities;
import pt.lsts.util.WGS84Utilities;

/** This class implements the IMC protocol allowing sending / receiving messages and also discovery
 * of IMC peers
 * 
 * @author zp */
public class IMCProtocol implements IMessageBus, MessageListener<MessageInfo, IMCMessage> {
    protected UDPTransport discovery;
    protected UDPTransport comms;
    protected TcpTransport tcp;
    protected LinkedHashMap<String, IMCNode> nodes = new LinkedHashMap<String, IMCNode>();
    protected int bindPort = 7001;
    protected LinkedHashMap<String, ImcSystemState> sysStates = new LinkedHashMap<String, ImcSystemState>();
    protected String localName = "imcj_" + System.currentTimeMillis() / 500;
    protected int localId;
    protected SYS_TYPE sysType = SYS_TYPE.CCU;
    private HashSet<String> services = new HashSet<String>();
    private boolean quiet = false;
    private String autoConnect = null;
    private boolean connectOnHeartBeat = false;
    private Timer beater = new Timer();
    private IMessageLogger logger = null;
    private ExecutorService logExec = Executors.newSingleThreadExecutor();
    
    private final long initialTimeMillis = System.currentTimeMillis();
    private final long initialTimeNanos = System.nanoTime();
    
    private EstimatedState estState = null;

    public IMCProtocol(String localName, int localPort) {
        this(localName, localPort, 0x4000 + new Random().nextInt(0x1FFF), (SYS_TYPE) null);
    }

    public IMCProtocol(String localName, int localPort, int localId) {
        this(localName, localPort, localId, (SYS_TYPE) null);
    }

    public IMCProtocol(String localName, int localPort, int localId, SYS_TYPE sysType) {
        if (localId <= 0)
            this.localId = 0x4000 + new Random().nextInt(0x1FFF);
        else
        	this.localId = localId;
        
        if (sysType != null)
            this.sysType = sysType;
        
        IMCDefinition.getInstance();
        this.bindPort = localPort;
        comms = new UDPTransport(bindPort, 1);
        comms.setImcId(getLocalId());
        tcp = new TcpTransport();
        try {
            tcp.bind(bindPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        comms.setImcId(getLocalId());

        this.localName = localName;
        discoveryThread.start();

        beater.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ArrayList<IMCNode> peers = new ArrayList<IMCNode>();
                peers.addAll(nodes.values());
                for (IMCNode node : peers) {
                    if (node.isPeer()) {
                        IMCMessage hbeat = new Heartbeat();
                        sendMessage(node.getSysName(), hbeat);
                        logMessage(hbeat);
                    }
                }
            }
        }, 1000, 1000);

        try {
            while (discovery == null) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addMessageListener(this);
    }

    /** Create a new IMCProtocol instance and bind it to given local port
     * 
     * @param bindPort
     *            The port where to bind for listening to incoming messages (also advertised using
     *            multicast) */
    public IMCProtocol(int bindPort) {
        this("imcj_" + System.currentTimeMillis() / 500, bindPort);
    }

    public IMCProtocol() {
        this("imcj_" + System.currentTimeMillis() / 500, 8000 + (int) Math.random() * 1000);
    }

    @Override
    public void onMessage(MessageInfo info, IMCMessage msg) {
        msg.setMessageInfo(info);
        logMessage(msg);
        switch (msg.getMgid()) {
        case Announce.ID_STATIC:
            on((Announce) msg);
            break;
        case EntityInfo.ID_STATIC:
            on((EntityInfo) msg);
            break;
        case EntityList.ID_STATIC:
            on((EntityList) msg);
            break;
        case Heartbeat.ID_STATIC:
        	on((Heartbeat) msg);
        	break;
        default:
            msgReceived(msg);
            break;
        }
    }

    public void connect(String system) {
        IMCNode node = nodes.get(system);
        if (node != null)
            node.setPeer(true);
        else
            setAutoConnect(autoConnect == null ? system : autoConnect + "|" + system);
    }

    public void disconnect(String system) {
        IMCNode node = nodes.get(system);
        if (node != null)
            node.setPeer(false);
    }

    private void on(Announce msg) {
        String src = msg.getSysName();

        IMCDefinition.getInstance().getResolver().addEntry(msg.getSrc(), msg.getSysName());

        if (!nodes.containsKey(src)) {

            boolean peer = false;

            // Check if this is a peer (a name we should auto connect to)
            if (this.autoConnect != null)
                peer = !src.equals(getLocalName()) && Pattern.matches(autoConnect, src);
            IMCNode node = new IMCNode(msg);
            node.setPeer(peer);
            nodes.put(src, node);

            if (peer)
                System.out.println("[IMCProtocol] New peer within range: " + msg.getSysName() + ", "
                        + node.address + ":" + node.port);

        } else
            nodes.get(src).setAnnounce(msg);
    }

    private void on(EntityList el) {
        if (el.getOp() == OP.REPORT) {
            IMCDefinition.getInstance().getResolver().setEntityMap(el.getSrc(), el.getList());
        }
    }

    private void msgReceived(IMCMessage msg) {
        String name = msg.getSourceName();
        if (msg.getMgid() == Announce.ID_STATIC)
            name = ((Announce) msg).getSysName();

        if (!sysStates.containsKey(name))
            sysStates.put(name, new ImcSystemState(IMCDefinition.getInstance()));

        sysStates.get(name).setMessage(msg);
    }

    private void on(EntityInfo el) {
        IMCDefinition.getInstance().getResolver().setEntityName(el.getSrc(), el.getId(),
                el.getLabel());
    }
    
    private void on(Heartbeat msg) {
    	if (connectOnHeartBeat) {
    		String name = msg.getSourceName();
    		if (nodes.containsKey(name)) {
    			boolean wasPeer = nodes.get(name).isPeer();
    			nodes.get(name).setPeer(true);
    			if (!wasPeer)
    				System.out.println("Activating transmission to "+name+".");    			
    		}    			    			
    	}
    }

    public final String getLocalName() {
        return localName;
    }

    public int getLocalId() {
        return localId;
    }
    
    public SYS_TYPE getSysType() {
        return sysType;
    }

    public void addService(String service) {
        services.add(service);
    }

    private String getUID() {
        return ""+(initialTimeMillis * 1000000 + (initialTimeNanos % 1000000));
    }

    protected Announce buildAnnounce(boolean includeLoopback) {
        Announce announce = new Announce();
        announce.setSysType(sysType);
        announce.setSysName(localName);
        announce.setSrc(localId);

        if (estState != null) {
            EstimatedState es = estState;
            double[] pos = WGS84Utilities.toLatLonDepth(es);
            announce.setLat(Math.toRadians(pos[0]));
            announce.setLon(Math.toRadians(pos[1]));
            announce.setHeight(-pos[2]);
        }
        
        String services = "imcjava://0.0.0.0/uid/" + getUID() + "/;";
        services += "imc+info://0.0.0.0/version/" + IMCDefinition.getInstance().getVersion() + "/;";
        
        Collection<String> netInt = NetworkUtilities.getNetworkInterfaces(includeLoopback);
        for (String itf : netInt) {
            services += "imc+udp://" + itf + ":" + bindPort + "/;";
            services += "imc+tcp://" + itf + ":" + bindPort + "/;";
        }
        for (String s : this.services)
            services += s + ";";

        if (services.length() > 0)
            services = services.substring(0, services.length() - 1);

        announce.setServices(services);

        return announce;
    }

    private Thread discoveryThread = new Thread() {
        public void run() {

            int port = 30100;

            while (true) {
                discovery = new UDPTransport("224.0.75.69", port);
                discovery.setImcId(localId);
                discovery.addMessageListener(IMCProtocol.this);
                if (discovery.isOnBindError()) {
                    discovery.stop();
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    port++;
                    if (port > 30104) {
                        System.err.println("no available ports to listen to advertisements.");
                        port = 30100;
                    }
                } else
                    break;
            }
            if (!quiet)
                System.out.println("[IMCProtocol] Discovery thread bound to port " + port + ".");

            long lastSent = System.currentTimeMillis();
            while (true) {
                Announce announce = buildAnnounce(true);
                logMessage(announce);

                for (int p = 30100; p < 30105; p++) {
                    discovery.sendMessage("224.0.75.69", p, announce);
                    discovery.sendMessage("255.255.255.255", p, announce);

                    for (IMCNode node : nodes.values())
                        discovery.sendMessage(node.address, p, announce);
                }

                lastSent = System.currentTimeMillis();
                try {
                    Thread.sleep(10000 - (System.currentTimeMillis() - lastSent));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    };

    /** Retrieve time elapsed since last announce of given system name
     * 
     * @param name
     *            The name of the system
     * @return Time, in milliseconds since last announce has been received from the given system.
     *         <br/>
     *         In the case the system has not announced itself yet, -1 is returned. */
    public long announceAgeMillis(String name) {
        IMCNode node = getNode(name);
        if (node == null)
            return -1;
        return node.getAgeMillis();
    }

    protected IMCNode getNode(String sys_name) {
        for (IMCNode node : nodes.values()) {
            if (node.getSysName().equals(sys_name))
                return node;
        }
        return null;
    }

    protected boolean sendHeartbeat(String remoteSystem) {
        IMCNode node = getNode(remoteSystem);
        if (node == null || node.getAddress() == null)
            return false;

        Heartbeat msg = IMCDefinition.getInstance().create(Heartbeat.class, "src", localId, "dst",
                node.getImcId(), "timestamp", System.currentTimeMillis() / 1000.0);

        boolean result = comms.sendMessage(node.getAddress(), node.getPort(), msg);
        logMessage(msg);
        return result;
    }

    /** Send a message to all known (via received announces) systems.
     * 
     * @param msg
     *            The message to be sent.
     * @return <code>true</code> if the message was tentatively sent to at least one system. */
    public boolean broadcast(IMCMessage msg) {
        msg.setValue("src", localId);
        boolean sent = false;
        for (IMCNode nd : nodes.values()) {
            if (nd.address != null) {
                fillUp(msg, nd.getSysName());
//                msg.setValue("dst", nd.getImcId());
//                msg.setTimestamp(System.currentTimeMillis() / 1000.0);
                comms.sendMessage(nd.address, nd.port, msg);
                logMessage(msg);
            }
            sent = true;
        }
        return sent;
    }

    /** Send a message to the peers that this proto should auto-connect to.
     * 
     * @param msg
     *            The message to be sent
     * @return <code>true</code> if the message was tentatively sent to at least one peer. */
    public boolean sendToPeers(IMCMessage msg) {
        msg.setValue("src", localId);
        boolean sent = false;
        for (IMCNode nd : nodes.values()) {
            if (nd.address != null && nd.isPeer()) {
                fillUp(msg, nd.getSysName());
                comms.sendMessage(nd.address, nd.port, msg);
                logMessage(msg);
            }
            sent = true;
        }
        return sent;
    }

    /** Send message to a remote system, specifying its name.
     * 
     * @param sysName
     *            The name of the system where to send the message
     * @param msg
     *            The message to be sent to the system
     * @return <strong>true</strong> if the message was sent or <strong>false</strong> if no such
     *         system is known yet. */
    public boolean sendMessage(String sysName, IMCMessage msg) {

        fillUp(msg, sysName);
        for (IMCNode nd : nodes.values()) {

            if (sysName.equals(nd.getSysName())) {
                if (nd.address != null) {
                    msg.setValue("dst", nd.getImcId());
                    comms.sendMessage(nd.getAddress(), nd.getPort(), msg);
                    logMessage(msg);
                    return true;
                } else
                    return false;
            }
        }

        return false;
    }

    /** This method tries to send a message to given destination with reliability. If the message is
     * not acknowledged by the remote host, this method will thrown an Exception.
     * 
     * @param sysName
     *            The name of the destination of this message
     * @param msg
     *            The message to send to the destination
     * @param timeoutMillis
     *            Maximum amount of time, in milliseconds to wait for delivery.
     * @return <code>true</code> on success.
     * @throws Exception
     *             In case the destination is not known, is nor currently reachable or there was an
     *             error in the communication. */
    public boolean sendReliably(String sysName, IMCMessage msg, int timeoutMillis)
            throws Exception {
        fillUp(msg, sysName);

        Vector<Future<Boolean>> tries = new Vector<Future<Boolean>>();
        for (IMCNode nd : nodes.values()) {
            if (nd.getSysName().equals(sysName)) {
                if (nd.getTcpAddress() != null) {
                    msg.setValue("dst", nd.getImcId());
                    tries.add(tcp.send(nd.getTcpAddress(), nd.getTcpPort(), msg, timeoutMillis));
                }
            }
        }

        if (tries.isEmpty())
            throw new Exception("Destination not reachable");
        for (Future<Boolean> t : tries)
            if (t.get())
                return true;

        throw new Exception("Destination not reachable");
    }

    private void fillUp(IMCMessage msg, String dst) {
        msg.setValue("src", localId);
        msg.setTimestamp(System.currentTimeMillis() / 1000.0);
        msg.setValue("dst", IMCDefinition.getInstance().getResolver().resolve(dst));
        
        if (msg instanceof EstimatedState) {
            estState = (EstimatedState) msg;
        }
    }

    private LinkedHashMap<Object, ImcConsumer> pojoSubscribers = new LinkedHashMap<Object, ImcConsumer>();

    /** Register a POJO consumer.
     * 
     * @see ImcConsumer */
    public void register(Object consumer) {
        unregister(consumer);

        PeriodicCallbacks.register(consumer);

        ImcConsumer listener = ImcConsumer.create(consumer);

        if (listener.getTypesToListen() == null)
            addMessageListener(listener, new ArrayList<String>());
        else if (listener.getTypesToListen().isEmpty())
            return;
        else
            addMessageListener(listener, listener.getTypesToListen());

        pojoSubscribers.put(consumer, listener);
    }

    /** Unregister a previously registered POJO consumer. */
    public void unregister(Object consumer) {
        if (pojoSubscribers.containsKey(consumer))
            removeMessageListener(pojoSubscribers.get(consumer));
        pojoSubscribers.remove(consumer);

        PeriodicCallbacks.unregister(consumer);
    }

    /** Add a listener to be called whenever messages of certain types are received
     * 
     * @param listener
     *            The listener to be added
     * @param typesToListen
     *            The list of message abbreviated names to be observed by this listener */
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> listener,
            String... typesToListen) {
        addMessageListener(listener, Arrays.asList(typesToListen));
    }

    /** Add a listener to be called whenever messages of certain types are received
     * 
     * @param l
     *            The listener to be added
     * @param typesToListen
     *            Collection of abbreviated names to be observed by this listener */
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l,
            Collection<String> typesToListen) {
        comms.addListener(l, typesToListen);
        discovery.addListener(l, typesToListen);
        tcp.addMessageListener(l);
    }

    /** Remove a previously added message listener
     * 
     * @param l
     *            The listener to be removed from the observers */
    public void removeMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        comms.removeMessageListener(l);
        discovery.removeMessageListener(l);
        tcp.removeMessageListener(l);
    }

    /** Add a global message listener that will be call on <strong>ALL</strong> incoming messages
     * 
     * @param l
     *            The global listener to be added to the list of observers */
    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        comms.addMessageListener(l);
        discovery.addMessageListener(l);
        tcp.addMessageListener(l);
    }

    /** Add a listener that will be called once and then removed from the list of observers
     * 
     * @param listener
     *            The listener to be added as a single-shot listener
     * @param typeToListen
     *            The type of message to be listen to */
    public void addSingleShotListener(MessageListener<MessageInfo, IMCMessage> listener,
            String typeToListen) {

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

    /** Retrieve a list of known system names (from which an announce has been received)
     * 
     * @return list of known system names */
    public String[] systems() {
        return sysStates.keySet().toArray(new String[0]);
    }

    public String[] lookupService(String serviceName) {
        Vector<String> systems = new Vector<String>();

        for (String sys : systems()) {
            Announce last = state(sys).last(Announce.class);
            System.out.println(last.getServices());
        }
        return systems.toArray(new String[0]);
    }

    /** Retrieve the continuously updated state of the given system
     * 
     * @param name
     *            The system for which to retrieve the state
     * @return The existing system state or a newly created state (inactive) if that system is not
     *         yet known */
    public ImcSystemState state(String name) {
        if (!sysStates.containsKey(name)) {
            sysStates.put(name, new ImcSystemState(IMCDefinition.getInstance()));
        }
        return sysStates.get(name);
    }

    protected Thread replayThread = null;

    /** Replay an LSF log folder
     * 
     * @param dirToReplay
     *            The folder where the files Data.lsf and IMC.xml can be found
     * @param speed
     *            The time multiplier (1.0 = real time)
     * @throws Exception
     *             In the case the folder cannot be read or any other IO errors */
    public void startReplay(String dirToReplay, double speed) throws Exception {

        final LsfIndex index = new LsfIndex(new File(dirToReplay, "Data.lsf"),
                IMCDefinition.getInstance(new FileInputStream(new File(dirToReplay, "IMC.xml"))));

        final double sec = 1000.0 * speed;
        replayThread = new Thread() {
            @Override
            public void run() {

                int src = index.sourceOf(0);
                double start = index.timeOf(0);
                long startMillis = System.currentTimeMillis();

                for (int i = 0; i < index.getNumberOfMessages(); i++) {
                    double curTime = (System.currentTimeMillis() - startMillis) / sec + start;
                    IMCMessage m = index.getMessage(i);
                    if (m.getSrc() == src) {
                        while (m.getTimestamp() > curTime) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                return;
                            }
                            curTime = (System.currentTimeMillis() - startMillis) / sec + start;
                        }
                    }
                    MessageInfoImpl mi = new MessageInfoImpl();
                    mi.setTimeSentSec(m.getTimestamp());
                    mi.setTimeReceivedSec(System.currentTimeMillis() / 1000.0);
                    mi.setPublisherInetAddress("");
                    mi.setPublisherPort(-1);
                    onMessage(mi, m);
                }
            }
        };
        replayThread.start();
    }

    /** Stop replaying */
    public void stopReplay() {
        if (replayThread != null)
            replayThread.interrupt();
    }

    /** Stop this IMCProtocol instance (closes all sockets) */
    public void stop() {
        stopReplay();
        beater.cancel();
        PeriodicCallbacks.stopAll();

        if (discoveryThread != null)
            discoveryThread.interrupt();

        if (comms != null)
            comms.stop();

        if (discovery != null)
            discovery.stop();

        tcp.shutdown();
        logExec.shutdown();
    }

    /** @param autoConnect
     *            the autoConnect to set */
    public void setAutoConnect(String autoConnect) {

        if (autoConnect != null) {
            for (IMCNode node : nodes.values()) {
                boolean peer = Pattern.matches(autoConnect, node.getSysName());
                if (peer)
                    node.setPeer(true);
            }
        }

        this.autoConnect = autoConnect;
    }
    
    public void setConnectOnHeartBeat() {
    	autoConnect = null;
    	connectOnHeartBeat = true;
    	for (IMCNode node : nodes.values())
    		node.setPeer(false);    	
    }

    /** This method blocks until a system whose name matches a regular expression is found on the
     * network or <code>null</code> if time has expired.
     * 
     * @param systemExpr
     *            The regular expression to look for
     * @param timeoutMillis
     *            The maximum amount of time to block
     * @return The name of the system found */
    public String waitFor(String systemExpr, long timeoutMillis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMillis) {
            for (String s : systems()) {
                if (s.matches(systemExpr))
                    return s;
            }
            try {
                Thread.sleep(333);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** Change the active message logger (no logger is activated by default)
     * 
     * @param logger
     *            The logger that will handle all sent / received messages.
     * @see #setLsfMessageLogger() */
    public void setMessageLogger(IMessageLogger logger) {
        this.logger = logger;
    }

    /** Activate Lsf message logging
     * 
     * @see #setMessageLogger(IMessageLogger) */
    public void setLsfMessageLogger() {
        logger = new IMessageLogger() {
            @Override
            public void logMessage(IMCMessage message) throws Exception {
                LsfMessageLogger.log(message);
            }
        };
    }

    private void logMessage(final IMCMessage msg) {
        final IMessageLogger curLogger = logger;

        if (curLogger == null)
            return;

        logExec.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    curLogger.logMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
	 * @return the autoConnect
	 */
	public String getAutoConnect() {
		return autoConnect;
	}

	public static void main(String[] args) throws Exception {

        final IMCProtocol proto = new IMCProtocol(7001);
        proto.connect("lauv-seacon-1");
        Object o = new Object() {

            @Consume
            public void on(EstimatedState msg) {
                System.out.println("STATE: " + msg.getAbbrev());
            }

            @Consume
            public void on(Announce msg) {
                System.out.println("ANNOUNCE: " + msg.getAbbrev());
            }

            @Periodic(1000)
            private void periodic2() {
                System.out.println("PERIODIC 2 " + System.currentTimeMillis());

            }

            @Periodic(500)
            private void periodic() {
                System.out.println("PERIODIC START " + System.currentTimeMillis());
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
                System.out.println("PERIODIC END " + System.currentTimeMillis());
            }
        };

        Object o2 = new Object() {
            @Periodic(1000)
            private void periodic2() {
                proto.broadcast(new Heartbeat());
                System.out.println("OBJECT 2 " + System.currentTimeMillis());
            }
        };
        System.out.println("Registering");
        proto.register(o);
        proto.register(o2);
        Thread.sleep(30000);
        System.out.println("Unregistering");
        proto.unregister(o);
        Thread.sleep(30000);
        System.out.println("Stopping");
        proto.stop();
    }
}
