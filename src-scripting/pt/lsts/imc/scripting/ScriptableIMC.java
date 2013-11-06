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
 * $Id:: ScriptableIMC.java 333 2013-01-02 11:11:44Z zepinto                   $:
 */
package pt.lsts.imc.scripting;

import java.awt.GraphicsEnvironment;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.script.ScriptException;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.net.UDPTransport;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

public class ScriptableIMC extends ScriptableObject {

    private static final long serialVersionUID = -5706118292185666437L;
    private UDPTransport udpTransport, announceTransport;
    private Scriptable msgs = null;
    private AnnounceListener listener;
    private MessageAggregator aggregator;
    private int port = -1;
    private LinkedHashMap<String, Integer> ports = new LinkedHashMap<String, Integer>();
    private LinkedHashMap<String, String> hosts = new LinkedHashMap<String, String>();
    private LinkedHashMap<Integer, String> peers = new LinkedHashMap<Integer, String>();
    private LinkedHashMap<String, LinkedHashMap<String, Integer>> entityLists = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
    private LinkedHashMap<String, LinkedHashMap<Integer, String>> entityListsReversed = new LinkedHashMap<String, LinkedHashMap<Integer, String>>();
    private Thread announcerThread = null;
    private LinkedHashMap<String, Vector<Function>> handlers = new LinkedHashMap<String, Vector<Function>>();
    protected String systype = "CCU";
    private String localSystemName = "_none_";
    protected String localname = null;


    public ScriptableIMC() {
        listener = new AnnounceListener(this, Context.getCurrentContext());
        aggregator = new MessageAggregator(this, Context.getCurrentContext());
        msgs = Context.getCurrentContext().newObject(this);
        ScriptableObject.putProperty(this, "msgs", msgs);
    }

    public String jsGet_name() {
        if (localname == null)
            return "kraken-" + System.getProperty("user.name");
        else
            return localname;
    }

    public int jsgetId() {
        return jsGet_name().hashCode() % (10000);
    }

    public Collection<String> jsGet_msgNames() {
        return IMCDefinition.getInstance().getMessageNames();

    }

    public boolean jsFunction_send(ScriptableMessage message, String destination, Integer port) throws ScriptException {
        IMCMessage msg = message.getAsMessage();

        if (hosts.containsKey(destination)) {
            String name = destination;
            destination = hosts.get(destination);
            port = ports.get(name);

            for (int key : peers.keySet()) {
                if (peers.get(key).equals(destination)) {
                    msg.getHeader().setValue("dst", key);
                    break;
                }
            }
        }

        msg.getHeader().setValue("src", jsgetId());

        if (port == 0) {
            throw new ScriptException("Cannot send message to unspecified port");
        }

        if (udpTransport != null)
            return udpTransport.sendMessage(destination, port, msg);
        else {
            try {
                UDPTransport.sendMessage(msg, destination, port);
            }
            catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    public Scriptable jsFunction_waitFor(String messageName, int timeoutSeconds) {
        SpecificListener specific = new SpecificListener();
        final UDPTransport transport = messageName.equals("Announce") ? announceTransport : udpTransport;

        transport.addListener(specific, Arrays.asList(messageName));

        long startMillis = System.currentTimeMillis();

        while (specific.getMessage() == null && System.currentTimeMillis() - startMillis < timeoutSeconds * 1000) {
            try {
                Thread.sleep(250);
            }
            catch (InterruptedException e) {
                transport.removeMessageListener(specific);
                return null;
            }
            if (specific.getMessage() != null) {
                Scriptable scope = ScriptableObject.getTopLevelScope(this);
                Context cx = Context.getCurrentContext();
                ScriptableMessage sm = (ScriptableMessage) cx.newObject(scope, "Message", new Object[] { messageName });
                sm.setMessage(specific.getMessage());
                transport.removeMessageListener(specific);
                return sm;
            }
        }

        return null;
    }

    public boolean jsFunction_inspect(ScriptableMessage message) {
        if (GraphicsEnvironment.isHeadless())
            return false;

        JDialog dialog = new JDialog();
        IMCMessage msg = message.getOriginal() == null ? message.getAsMessage() : message.getOriginal();

        dialog.add(new JScrollPane(new JEditorPane("text/html", IMCUtil.getAsHtml(msg))));

        dialog.setTitle("Inspecting " + msg.getAbbrev());
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 500);
        dialog.setVisible(true);

        return true;
    }

    public void jsFunction_stopDiscovery() {
        if (announceTransport != null)
            announceTransport.stop();

        if (announcerThread != null) {
            announcerThread.interrupt();
        }

        announceTransport = null;
        announcerThread = null;
    }



    public void jsFunction_startDiscovery(String localname, String systype) {

        this.systype = ""+systype;
        this.localname = ""+localname;

        if (systype.equals("undefined"))
            this.systype = null;
        if (localname.equals("undefined"))
            this.localname = null;

        if (announceTransport != null)
            announceTransport.stop();

        announceTransport = new UDPTransport("224.0.75.69", 30103);
        // announceTransport.setIsMessageInfoNeeded(false);
        announceTransport.addListener(listener, Arrays.asList("Announce"));

        if (announcerThread != null) {
            announcerThread.interrupt();
        }

        announcerThread = new Thread(new Runnable() {

            @Override
            public void run() {

                long millisBetweenAnnounce = 10000;
                long millisBetweenHBeat = 1000;
                long millisCount = 0;
                IMCMessage hbeat = IMCDefinition.getInstance().create("Heartbeat");
                hbeat.getHeader().setValue("src", jsgetId());
                hbeat.getHeader().setValue("src_ent", 255);
                hbeat.getHeader().setValue("dst_ent", 255);
                hbeat.getHeader().setValue("dst", 255);

                String services = "kraken://0.0.0.0/uid/" + System.currentTimeMillis();
                try {

                    if (udpTransport != null) {
                        Enumeration<NetworkInterface> nintf = NetworkInterface.getNetworkInterfaces();
                        while (nintf.hasMoreElements()) {
                            NetworkInterface ni = nintf.nextElement();
                            if (ni.isUp()) {
                                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                                    if (ia.getAddress() instanceof Inet4Address)
                                        services += ";imc+udp://" + ia.getAddress().getHostAddress() + ":" + port
                                        + "/;";
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


                while (announceTransport != null && announceTransport.isRunning()) {
                    try {

                        if (udpTransport != null) {
                            for (String peer : ScriptableIMC.this.peers.values()) {
                                if (!hosts.containsKey(peer) || !ports.containsKey(peer))
                                    continue;
                                hbeat.setTimestamp(System.currentTimeMillis() / 1000.0);
                                udpTransport.sendMessage(ScriptableIMC.this.hosts.get(peer),
                                        ScriptableIMC.this.ports.get(peer), hbeat);
                            }
                        }

                        if (millisCount > millisBetweenAnnounce) {

                            IMCMessage announce = IMCDefinition.getInstance().create("Announce", "sys_name",
                                    jsGet_name(), "sys_type", (ScriptableIMC.this.systype != null)? ScriptableIMC.this.systype : "CCU" , "services", services);

                            announce.getHeader().setValue("src", jsgetId());
                            announce.getHeader().setValue("src_ent", 255);
                            announce.getHeader().setValue("dst_ent", 255);
                            announce.getHeader().setValue("dst", 255);
                            announce.getHeader().setValue("time", System.currentTimeMillis() / 1000.0);
                            for (int port = 30100; port < 30105; port++) {
                                announceTransport.sendMessage("224.0.75.69", port, announce);
                            }
                            millisCount = 0;
                        }

                        Thread.sleep(millisBetweenHBeat);
                        millisCount += millisBetweenHBeat;
                    }
                    catch (InterruptedException e) {
                        break;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        announcerThread.start();
    }

    public int jsGet_port() {
        return port;
    }

    public boolean jsFunction_stopListening() {

        if (udpTransport != null)
            udpTransport.stop();
        else
            return false;

        udpTransport = null;
        handlers.clear();
        return true;
    }

    public boolean jsFunction_startListening(int port) throws ScriptException {
        this.port = port;

        if (udpTransport != null)
            udpTransport.stop();

        udpTransport = new UDPTransport(port, 2);
        udpTransport.addMessageListener(aggregator);

        return true;
    }

    public boolean jsFunction_stop() throws ScriptException {
        jsFunction_stopDiscovery();
        return jsFunction_stopListening();
    }

    public boolean jsFunction_bind(Object arg1, Object arg2) throws ScriptException {
        if (arg1 instanceof String && arg2 instanceof Function)
            return bind(arg1.toString(), (Function) arg2);
        if (arg1 instanceof Function)
            return bind((Function) arg1);

        throw new ScriptException("Invalid argument types");
    }

    public boolean jsFunction_unbind(Object arg1, Object arg2) throws ScriptException {
        if (arg1 instanceof String && arg2 instanceof Function)
            return unbind(arg1.toString(), (Function) arg2);
        if (arg1 instanceof Function)
            return unbind((Function) arg1);

        throw new ScriptException("Invalid argument types");
    }

    protected boolean bind(Function handler) {
        if (!handlers.containsKey("*"))
            handlers.put("*", new Vector<Function>());
        handlers.get("*").add(handler);
        return true;
    }

    protected boolean bind(String messageName, Function handler) {
        if (!handlers.containsKey(messageName))
            handlers.put(messageName, new Vector<Function>());

        if (!handlers.get(messageName).contains(handler))
            handlers.get(messageName).add(handler);
        else
            return false;

        return true;
    }

    protected boolean unbind(String messageName, Function handler) {
        if (!handlers.containsKey(messageName))
            return false;

        handlers.get(messageName).remove(handler);
        return true;
    }

    protected boolean unbind(Function handler) {
        for (Vector<Function> v : handlers.values())
            v.remove(handler);
        return true;
    }

    @Override
    public String getClassName() {
        return "IMC";
    }

    public String getLocalSystemName() {
        return localSystemName;
    }

    public void setLocalSystemName(String localSystemName) {
        this.localSystemName = localSystemName;
    }

    public String jsFunction_getEntityName(String system_name, int entity_id) {
        synchronized (entityListsReversed) {
            if (!entityListsReversed.containsKey(system_name))
                return "unknown";
            else if (!entityListsReversed.get(system_name).containsKey(entity_id))
                return "unknown";

            return entityListsReversed.get(system_name).get(entity_id);
        }
    }

    public int jsFunction_getEntityId(String system_name, String entity_name) {
        synchronized (entityLists) {
            if (!entityLists.containsKey(system_name))
                return -1;
            else if (!entityLists.get(system_name).containsKey(entity_name))
                return -1;

            return entityLists.get(system_name).get(entity_name);
        }
    }



    static class MessageAggregator implements MessageListener<MessageInfo, IMCMessage> {

        protected ScriptableIMC imc;
        protected Context context;

        public MessageAggregator(ScriptableIMC imc, Context context) {
            this.imc = imc;
            this.context = context;
        }

        @Override
        public void onMessage(MessageInfo info, IMCMessage msg) {

            // if entitylist message is received, set the system's entities
            if (msg.getAbbrev().equals("EntityList") && msg.getString("op").equals("REPORT")) {
                if (imc.peers.containsKey(msg.getHeader().getInteger("src"))) {
                    String sysname = imc.peers.get(msg.getHeader().getInteger("src"));
                    // set entities...
                    LinkedHashMap<String, String> list = msg.getTupleList("list");

                    synchronized (imc.entityLists) {
                        imc.entityLists.put(sysname, new LinkedHashMap<String, Integer>());
                        for (String key : list.keySet())
                            imc.entityLists.get(sysname).put(key, Integer.parseInt(list.get(key)));
                    }

                    synchronized (imc.entityListsReversed) {
                        imc.entityListsReversed.put(sysname, new LinkedHashMap<Integer, String>());
                        for (String key : list.keySet())
                            imc.entityListsReversed.get(sysname).put(Integer.parseInt(list.get(key)), key);
                    }
                }
            }

            ScriptableMessage sm = (ScriptableMessage) context.newObject(imc, "Message",
                    new Object[] { msg.getAbbrev() });
            sm.setMessage(msg);
            ScriptableObject.putProperty(sm, "_ip", info.getPublisherInetAddress());
            ScriptableObject.putProperty(sm, "_source", imc.peers.get(msg.getHeader().getInteger("src")));
            HashSet<Function> msgHandlers = new HashSet<Function>();
            if (imc.handlers.containsKey("*"))
                msgHandlers.addAll(imc.handlers.get("*"));

            if (imc.handlers.containsKey(msg.getAbbrev()))
                msgHandlers.addAll(imc.handlers.get(msg.getAbbrev()));
            Context.enter();

            for (Function f : msgHandlers) {
                synchronized (f) {
                    f.call(context, imc, imc, new Object[] { sm });
                }
            }
            Context.exit();

            // find entity name and source name
            String src = null;
            String entityName = null;
            src = imc.peers.get(msg.getHeader().getInteger("src"));
            if (src != null)
                entityName = imc.jsFunction_getEntityName(src, msg.getHeader().getInteger("src_ent"));

            // if is a local message put it in the imc object            
            ScriptableObject.putProperty(imc, msg.getAbbrev(), sm);

            // also put it inside respective property
            if (imc.getLocalSystemName().equals(src)) {
                if (entityName != null && !entityName.equals("unknown")) {
                    if (ScriptableObject.getProperty(imc.getParentScope(), entityName) == ScriptableObject.NOT_FOUND) {
                        Scriptable obj = context.newObject(imc.getParentScope());
                        ScriptableObject.putConstProperty(imc.getParentScope(), entityName, obj);       
                    }
                    Scriptable s = (Scriptable) ScriptableObject.getProperty(imc.getParentScope(), entityName);
                    ScriptableObject.putProperty(s, msg.getAbbrev(), sm);
                }


            }
            else {
                // Create peer if it doesn't exist
                if (ScriptableObject.getProperty(imc.msgs, src) == ScriptableObject.NOT_FOUND) {
                    Scriptable obj = context.newObject(imc.msgs);
                    ScriptableObject.putConstProperty(imc.msgs, src, obj);
                }

                Scriptable s = (Scriptable) ScriptableObject.getProperty(imc.msgs, src);
                ScriptableObject.putProperty(s, msg.getAbbrev(), sm);

                // also put it inside respective property
                if (entityName != null && !entityName.equals("unknown")) {
                    Object obj = ScriptableObject.getProperty(s, entityName);
                    if (obj == ScriptableObject.NOT_FOUND) {
                        obj = context.newObject(s);
                        ScriptableObject.putProperty(s, entityName, obj);       
                    }
                }
            }
        }
    }

    static class AnnounceListener implements MessageListener<MessageInfo, IMCMessage> {

        protected ScriptableIMC imc;
        protected Context context;

        public AnnounceListener(ScriptableIMC imc, Context context) {
            this.imc = imc;
            this.context = context;
        }

        @Override
        public void onMessage(MessageInfo info, IMCMessage msg) {

            if (imc.jsgetId() == msg.getHeader().getInteger("src"))
                return;

            int src = msg.getHeader().getInteger("src");
            String name = msg.getString("sys_name");
            imc.peers.put(src, name);

            try {
                String[] services = msg.getString("services").split(";");
                for (String serv : services) {
                    if (serv.startsWith("imc+udp://")) {
                        String ipport = serv.substring("imc+udp://".length()).split(",")[0];
                        ipport = ipport.replace("/", "");
                        imc.hosts.put(name, ipport.split(":")[0]);
                        imc.ports.put(name, Integer.valueOf(ipport.split(":")[1]));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            imc.aggregator.onMessage(info, msg);
        }
    }

    class SpecificListener implements MessageListener<MessageInfo, IMCMessage> {

        protected IMCMessage message = null;

        public IMCMessage getMessage() {
            return message;
        }

        @Override
        public void onMessage(MessageInfo info, IMCMessage msg) {
            this.message = msg;
        }
    }
}