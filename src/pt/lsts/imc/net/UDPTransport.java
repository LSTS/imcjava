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
 * $Id:: UDPTransport.java 333 2013-01-02 11:11:44Z zepinto                    $:
 */
package pt.lsts.imc.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageInfoImpl;
import pt.lsts.neptus.messages.listener.MessageListener;

public class UDPTransport {

    LinkedHashMap<MessageListener<MessageInfo, IMCMessage>, HashSet<Integer>> messageListeners = new LinkedHashMap<MessageListener<MessageInfo, IMCMessage>, HashSet<Integer>>();
    LinkedHashMap<Integer, HashSet<MessageListener<MessageInfo, IMCMessage>>> messagesListened = new LinkedHashMap<Integer, HashSet<MessageListener<MessageInfo, IMCMessage>>>();

    private final LinkedBlockingQueue<MessagePacket> receptions = new LinkedBlockingQueue<MessagePacket>(100);
    private final LinkedBlockingQueue<SendRequest> sendmessageList = new LinkedBlockingQueue<SendRequest>(100);

    private Thread sockedListenerThread = null;
    private Thread dispacherThread = null;
    private final Vector<Thread> senderThreads = new Vector<Thread>();	
    private int numberOfSenderThreads = 1;

    private DatagramSocket sock;

    private final LinkedHashMap<String, InetAddress> solvedAddresses = new LinkedHashMap<String, InetAddress>();

    private int bindPort = 6001;

    private int timeoutMillis = 100;
    private int maxBufferSize = 65507;

    private boolean purging = false;

    private boolean broadcastEnable = false;
    private boolean broadcastActive = false;

    private boolean multicastEnable = false;
    private boolean multicastActive = false;

    private String multicastAddress = "224.0.75.69";

    private boolean isOnBindError = false;
    private boolean isMessageInfoNeeded = true;
    private int receptionCount = 0;
    
    private int imc_id = (short)System.getProperty("user.name").hashCode();
    
    protected IMCDefinition definition;

    public IMCDefinition getDefinition() {
        if (definition == null)
            definition = IMCDefinition.getInstance();

        return definition;
    }

    public void setDefinition(IMCDefinition definition) {
        this.definition = definition;
    }

    public UDPTransport (IMCDefinition defs) {
        setDefinition(defs);
        initialize();
    }

    /**
     * 
     */
    public UDPTransport () {
        initialize();
    }

    /**
     * @param numberOfSenderThreads
     */
    public UDPTransport(int numberOfSenderThreads) {
        this.numberOfSenderThreads = numberOfSenderThreads;
        initialize();
    }

    /**
     * @param bindPort
     * @param numberOfSenderThreads
     */
    public UDPTransport (int bindPort, int numberOfSenderThreads) {
        setBindPort(bindPort);
        initialize();
    }

    public UDPTransport (boolean isBroadcastEnable, int bindPort, int numberOfSenderThreads) {
        setBindPort(bindPort);
        setBroadcastEnable(isBroadcastEnable);
        initialize();
    }

    /**
     * @param multicastAddress
     * @param bindPort
     * @param numberOfSenderThreads
     */
    public UDPTransport (String multicastAddress, int bindPort, int numberOfSenderThreads) {
        setNumberOfSenderThreads(numberOfSenderThreads);
        setBindPort(bindPort);
        setMulticastAddress(multicastAddress);
        setMulticastEnable(true);
        initialize();
    }

	/**
	 * @param multicastAddress
	 * @param bindPort
	 * @param numberOfSenderThreads
	 */
	public UDPTransport(boolean isBroadcastEnable, boolean isMulticastEnable,
			int bindPort, int numberOfSenderThreads) {
		setNumberOfSenderThreads(numberOfSenderThreads);
		setBindPort(bindPort);
		setBroadcastEnable(isBroadcastEnable);
		if (isMulticastEnable) {
			setMulticastAddress(multicastAddress);
			setMulticastEnable(isMulticastEnable);
		}
		initialize();
	}

    /**
     * @param multicastAddress
     * @param bindPort
     */
    public UDPTransport (String multicastAddress, int bindPort) {
        this(multicastAddress, bindPort, 1);
    }

    /**
     * 
     */
    private void initialize() {
        createReceivers();
        createSenders();
    }

    /**
     * @return the isOnBindError
     */
    public boolean isOnBindError() {
        return isOnBindError;
    }

    /**
     * @param isOnBindError the isOnBindError to set
     */
    private void setOnBindError(boolean isOnBindError) {
        this.isOnBindError = isOnBindError;
    }


    /**
     * @return
     */
    public int getBindPort() {
        return bindPort;
    }

    /**
     * @param bindPort
     */
    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
    }

    /**
     * @return the multicastAddress
     */
    public String getMulticastAddress() {
        return multicastAddress;
    }

    /**
     * @param multicastAddress the multicastAddress to set
     */
    public void setMulticastAddress(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    /**
     * @return the multicastEnable
     */
    public boolean isMulticastEnable() {
        return multicastEnable;
    }

    /**
     * @param multicastEnable the multicastEnable to set
     */
    public void setMulticastEnable(boolean multicastEnable) {
        this.multicastEnable = multicastEnable;
    }

    /**
     * @return the multicastActive
     */
    protected boolean isMulticastActive() {
        return multicastActive;
    }

    /**
     * @param multicastActive the multicastActive to set
     */
    protected void setMulticastActive(boolean multicastActive) {
        this.multicastActive = multicastActive;
    }

    /**
     * @return the broadcastEnable
     */
    public boolean isBroadcastEnable() {
        return broadcastEnable;
    }

    /**
     * @param broadcastEnable the broadcastEnable to set
     */
    public void setBroadcastEnable(boolean broadcastEnable) {
        this.broadcastEnable = broadcastEnable;
    }

    /**
     * @return the broadcastActive
     */
    protected boolean isBroadcastActive() {
        return broadcastActive;
    }

    /**
     * @param broadcastActive the broadcastActive to set
     */
    protected void setBroadcastActive(boolean broadcastActive) {
        this.broadcastActive = broadcastActive;
    }

    /**
     * @return the numberOfSenderThreads
     */
    public int getNumberOfSenderThreads() {
        return numberOfSenderThreads;
    }

    /**
     * @param numberOfSenderThreads the numberOfSenderThreads to set
     */
    public void setNumberOfSenderThreads(int numberOfSenderThreads) {
        this.numberOfSenderThreads = numberOfSenderThreads;
    }

    /**
     * @return the timeoutMillis
     */
    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * @param timeoutMillis the timeoutMillis to set
     */
    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    /**
     * @return the maxBufferSize
     */
    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    /**
     * @param maxBufferSize the maxBufferSize to set
     */
    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    /**
     * @param multicastAddress
     * @return
     */
    protected InetAddress resolveAddress(String multicastAddress)
            throws UnknownHostException {
        if (!solvedAddresses.containsKey(multicastAddress)) {
            solvedAddresses.put(multicastAddress, InetAddress
                    .getByName(multicastAddress));
        }
        return solvedAddresses.get(multicastAddress);
    }


    /**
     * @return
     */
    public boolean reStart() {
        if (!(!isStopping() && !isRunning()))
            return false;
        purging = false;
        createReceivers();
        createSenders();
        return true;
    }


    /**
     * Interrupts all the sending threads abruptly.
     * @see {@link #purge()}
     */
    public void stop() {
        if (isRunning()) {
            purging = true;

            if (sockedListenerThread != null) {
                sockedListenerThread.interrupt();
                sockedListenerThread = null;
            }
            synchronized (receptions) {
                receptions.clear();
            }
            if (dispacherThread != null) {
                dispacherThread.interrupt();
                dispacherThread = null;
            }

            if (sockedListenerThread != null) {
                sockedListenerThread.interrupt();
                sockedListenerThread = null;
            }

            int size = senderThreads.size();
            for (int i = 0; i < size; i++) {
                senderThreads.get(0).interrupt();
                senderThreads.remove(0); //shifts the right elements to the left 
            }
        }
    }

    /**
     * Stops accepting new messages but waits until all the buffered 
     * messages are sent to the network before stopping the sending thread(s).
     */
    public void purge() {
        purging = true;
        while (!receptions.isEmpty() || !sendmessageList.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stop();
    }

    /**
     * @return
     */
    public boolean isRunning() {
        if (senderThreads.size() > 0)
            return true;

        if (sockedListenerThread == null && dispacherThread == null)
            return false;
        return true;
    }

    /**
     * @return
     */
    public boolean isStopping() {
        if (isRunning() && purging)
            return true;
        return false;
    }



    /**
     * 
     */
    private void createSenders() {
        senderThreads.clear();
        for (int i = 0; i < this.numberOfSenderThreads; i++) {
            if (i == 0)
                senderThreads.add(getSenderThread(sock));
            else
                senderThreads.add(getSenderThread(null));
        }
    }


    /**
     * 
     */
    private void createReceivers() {
        setOnBindError(false);
        getSocketListenerThread();
        getDispacherThread();
        
        //addMessageListener(acksAggregator, Arrays.asList(AckedMessage.ID_STATIC));
    }

    /**
     * @return
     */
    private Thread getSocketListenerThread() {
        if (sockedListenerThread == null) {
            Thread listenerThread = new Thread(UDPTransport.class.getSimpleName() + ": Listener Thread " + this.hashCode()) {			
                byte[] sBuffer = new byte[maxBufferSize];
                //DatagramSocket sock;
                String multicastGroup = "";

                @Override
				public synchronized void start() {
                    //ConfigFetch.logPub.info("Listener Thread Started");
                    try {
                        boolean useMulticast = isMulticastEnable();
                        if (bindPort != 0)
                            sock = (!useMulticast)?new DatagramSocket(bindPort):new MulticastSocket(bindPort);
                            else
                                sock = (!useMulticast)?new DatagramSocket():new MulticastSocket();

                                if (useMulticast) {
                                	multicastGroup = getMulticastAddress();
                                	((MulticastSocket)sock).joinGroup(resolveAddress(multicastGroup));                                	
                                    
                                }
                                setMulticastActive(useMulticast);
                                sock.setSoTimeout(0);
//                                if (isBroadcastEnable()) {
//                                    try {
//                                        sock.setBroadcast(true);
//                                        setBroadcastActive(true);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                        setBroadcastActive(false);
//                                    }
//                                }
                    } catch (Exception e) {
                        setOnBindError(true);
                        return;
                    }
                    super.start();			
                }

                @Override
				public void run() {
                    try {
                        while (!purging) {
                            DatagramPacket packet = new DatagramPacket (sBuffer, sBuffer.length);
                            try {
                                sock.setSoTimeout(2000);
                                sock.receive(packet);
                                try {
                                    // in case you wonder... creating a copy is strictly necessary
                                    byte[] data = new byte[packet.getLength()];                               
                                    System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());

                                    InetSocketAddress addr = ( isMessageInfoNeeded ? (InetSocketAddress) packet.getSocketAddress() : null);

                                    MessagePacket mp = new MessagePacket(getDefinition(), data, packet.getLength(), addr, System.currentTimeMillis());
                                    if (!mp.validCRC())
                                        System.err.println("Discarding message with invalid CRC");
                                    else 
                                    {
                                        receptions.offer(mp);

                                    }
                                    receptionCount++;
                                } catch (Exception e) {
                                    e.printStackTrace();									
                                }
                            }
                            catch (SocketTimeoutException e) {
                                //e.printStackTrace();
                                continue;
                            } catch (IOException e) {
                                e.printStackTrace();							
                            }
                            Thread.yield();
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();												
                    }

                    if (isMulticastActive()) {
                        try {
                            ((MulticastSocket)sock).leaveGroup(resolveAddress(multicastGroup));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    sock.disconnect();
                    sock.close();
                    sock = null;
                }

            };
            listenerThread.setPriority(Thread.MIN_PRIORITY);
            listenerThread.setDaemon(true);
            listenerThread.start();
            sockedListenerThread = listenerThread;
        }
        return sockedListenerThread;
    }
    
    /**
     * @param req
     * @param info
     * @return
     */
    public void dispatchMessage(IMCMessage req, MessageInfo info) {
        Vector<MessageListener<MessageInfo,IMCMessage>> listeners = new Vector<MessageListener<MessageInfo,IMCMessage>>();
        listeners.addAll(messageListeners.keySet());
        for (MessageListener<MessageInfo,IMCMessage> lst : listeners) {
            try {
                if (messageListeners.containsKey(lst) && (messageListeners.get(lst).isEmpty() || messageListeners.get(lst).contains(req.getHeader().getInteger("mgid"))))
                    lst.onMessage(info,req);    

            } catch (Exception e) {
                e.printStackTrace();
            }
            catch (Error e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    private Thread getDispacherThread() {
        if (dispacherThread == null) {
            Thread listenerThread = new Thread(UDPTransport.class.getSimpleName() + ": Dispacher Thread " + this.hashCode()) {			
                @Override
				public void run() {
                    try {
                        while (!(purging && receptions.isEmpty())) {

                            MessagePacket packet = receptions.take();							

                            if (!messagesListened.containsKey(packet.mgid))
                                continue;

                            IMCMessage req;
                            try {
                                req = getDefinition().parseMessage(packet.data);
                            }
                            catch (IOException e1) {
                                e1.printStackTrace();
//                                System.err.println("[IMCTransport] "+e1.getMessage()+" in packet from "+packet.getAddress().toString().substring(1));
                                continue; // Ignore this message
                            }
                            MessageInfo info = new MessageInfoImpl();
                            if(isMessageInfoNeeded) {
                                info.setPublisher(packet.getAddress().getAddress().getHostAddress());
                                info.setPublisherInetAddress(packet.getAddress().getAddress().getHostAddress());
                                info.setPublisherPort(packet.getAddress().getPort());
                                info.setTimeReceivedNanos((long)req.getTimestamp() * (long)1E6);
                                info.setTimeSentNanos(packet.getTimestampMillis() * (long)1E6);
                            }
                            dispatchMessage(req, info);
                        }
                    }
                    catch (InterruptedException e) {
                        //System.err.println("Receiver thread stopped.");
                    }
                    catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            };
            listenerThread.setDaemon(true);
            listenerThread.setPriority(Thread.MIN_PRIORITY);
            listenerThread.start();
            dispacherThread = listenerThread;
        }
        return dispacherThread;
    }


    /**
     * @return
     */
    private Thread getSenderThread(final DatagramSocket sockToUseAlreadyOpen) {
        Thread senderThread = new Thread(UDPTransport.class.getSimpleName() + ": Sender Thread " + this.hashCode()) {

            DatagramSocket sock;
            DatagramPacket dgram;
            SendRequest req;
            
            @Override
			public synchronized void start() {
                try {
                    if (sockToUseAlreadyOpen != null)
                        sock = sockToUseAlreadyOpen;
                    else
                        sock = new DatagramSocket();
                    super.start();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }

            @Override
			public void run() {
                try {
                    while (!(purging && sendmessageList.isEmpty())) {
                        req = sendmessageList.take();
                        
                        try {
                            ByteArrayOutputStream buff = new ByteArrayOutputStream();
                            getDefinition().serialize(req.message, new IMCOutputStream(getDefinition(), buff));
                            byte[] data = buff.toByteArray();
                            dgram = new DatagramPacket(data, data.length, req.destination, req.port);
                            if (!sock.isClosed())
                            	sock.send(dgram);
                        } catch (Exception e) {                            
                            e.printStackTrace();
                        }
                    }
                }
                catch (InterruptedException e) {
                    //System.err.println("Sender thread stopped.");                   
                }


            }
        };
        senderThread.setDaemon(true);
        senderThread.setPriority(Thread.MIN_PRIORITY);
        senderThread.start();
        return senderThread;
    }

    public void setIsMessageInfoNeeded(boolean b) {
        isMessageInfoNeeded = b;
    }

    /**
     * Sends a message to the network
     * @param destination A valid hostname like "whale.fe.up.pt" or "127.0.0.1"
     * @param port The destination's port
     * @return true meaning that the message was put on the send queue, and 
     *          false if it was not put on the send queue.
     */
    public boolean sendMessage(String destination, int port, IMCMessage message) {
    	message.setSrc(imc_id);
    	if (purging) {
            System.err.println("Not accepting any more messages. Terminating");
            return false;
        }
        sendmessageList.add(new SendRequest(destination, port, message));
        return true;
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
                types.add(getDefinition().getMessageId(s));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }		
        addMessageListener(l, types);		
    }

    public void addMessageListener(MessageListener<MessageInfo, IMCMessage> l) {
        messageListeners.put(l, new HashSet<Integer>()); // empty means all
        for (String s : getDefinition().getMessageNames()) {
            try {
                int id = getDefinition().getMessageId(s);
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
        for (int id : messagesListened.keySet())
            messagesListened.get(id).remove(l);		
    }


    class SendRequest {

        IMCMessage message;
        InetAddress destination;
        int port;

        public SendRequest(String destination, int port, IMCMessage message) {
            try {
                this.destination = resolveAddress(destination);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.port = port;
            this.message = message;
        }
    }

    public void printStatistics() {
        int rCount = receptionCount;
        System.out.println(System.currentTimeMillis()+","+receptions.size()+","+receptionCount+","+sendmessageList.size());
        receptionCount = receptionCount-rCount;
    }


    protected static DatagramSocket anonymousSocket = null;

    public static void sendMessage(IMCMessage m, String destination, int port) throws Exception {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        IMCDefinition.getInstance().serialize(m, new IMCOutputStream(buff));
        byte[] data = buff.toByteArray();
        InetAddress addr = InetAddress.getByName(destination);
        DatagramPacket dgram = new DatagramPacket(data, data.length, addr, port);
        if (anonymousSocket == null)
            anonymousSocket = new DatagramSocket();
        anonymousSocket.send(dgram);
    }

    /**
	 * @return the imc_id
	 */
	public int getImcId() {
		return imc_id;
	}

	public void setImcId(int imc_id) {
		this.imc_id = imc_id;
	}

	public static void main(String[] args) throws Exception {
        UDPTransport transport = new UDPTransport(6002, 1);
        transport.addMessageListener(new MessageListener<MessageInfo, IMCMessage>() {			
            @Override
            public void onMessage(MessageInfo info, IMCMessage message) {
                System.out.println(message.toString());
            }
        });
        transport.sendMessage("127.0.0.1", 6002, IMCDefinition.getInstance().create("EstimatedState", "x", 10.3, "y", 45));
        //Thread.sleep(100);
        //transport.stop();
    }
}
