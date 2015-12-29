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
 * $Id:: IMCGraph.java 392 2013-02-28 17:26:14Z zepinto@gmail.com              $:
 */
package pt.lsts.dot;

import java.awt.Color;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JFileChooser;

import pt.lsts.colormap.ColorMap;
import pt.lsts.colormap.ColorMapFactory;
import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.TransportBindings;
import pt.lsts.imc.Announce.SYS_TYPE;
import pt.lsts.imc.lsf.LsfIndex;

/**
 * This class parses an LSF log an generates communication graphs
 * 
 * @author Carlos Ribeiro
 * 
 */
public class IMCGraph {

    LsfIndex index;

    public IMCGraph(LsfIndex index) {

        this.index = index;
    }

    protected LinkedHashMap<Integer, String> getEntitiesToTasks() {
        LinkedHashMap<Integer, String> entitiesToTasks = new LinkedHashMap<Integer, String>();

        int type = index.getDefinitions().getMessageId("EntityInfo");

        for (int i = index.getFirstMessageOfType("EntityInfo"); i != -1; i = index.getNextMessageOfType(type, i)) {
            IMCMessage msg = index.getMessage(i);
            entitiesToTasks.put(msg.getInteger("id"), msg.getString("component"));
        }

        return entitiesToTasks;
    }

    protected LinkedHashMap<String, Integer> getTasksToEntities() {
        LinkedHashMap<String, Integer> entitiesToTasks = new LinkedHashMap<String, Integer>();

        int type = index.getDefinitions().getMessageId("EntityInfo");

        for (int i = index.getFirstMessageOfType("EntityInfo"); i != -1; i = index.getNextMessageOfType(type, i)) {
            IMCMessage msg = index.getMessage(i);
            entitiesToTasks.put(msg.getString("component"), msg.getInteger("id"));
        }

        return entitiesToTasks;
    }

    protected LinkedHashMap<Integer, Vector<String>> getMessageConsumers() {
        LinkedHashMap<Integer, Vector<String>> msgConsumers = new LinkedHashMap<Integer, Vector<String>>();

        int type = index.getDefinitions().getMessageId("TransportBindings");
        if (index.getFirstMessageOfType("TransportBindings") == -1) {
            return null;
        }
        for (int i = index.getFirstMessageOfType(type); i != -1; i = index.getNextMessageOfType(type, i)) {
            IMCMessage msg = index.getMessage(i);
            int msg_id = msg.getInteger("message_id");
            String consumer = msg.getString("consumer");

            Vector<String> consumers = msgConsumers.get(msg_id);
            if (consumers == null) {
                consumers = new Vector<String>();
                msgConsumers.put(msg_id, consumers);
            }
            consumers.add(consumer);
        }

        return msgConsumers;
    }

    public GraphViz generateInterface(String taskName) {
        int src_ent = getTasksToEntities().get(taskName);

        Vector<String> generated = new Vector<String>();
        Vector<String> received = new Vector<String>();

        for (int i = 0; i < index.getNumberOfMessages(); i++) {
            if (index.entityOf(i) == src_ent) {
                String name = index.getDefinitions().getMessageName(index.typeOf(i));
                if (!generated.contains(name))
                    generated.add(name);
            }
        }

        for (int msgType : getMessageConsumers().keySet()) {
            if (getMessageConsumers().get(msgType).contains(taskName))
                received.add(index.getDefinitions().getMessageName(msgType));
        }

        String dot_source = "digraph  {\n\tnode [shape=box]\n";
        dot_source += ("\t\"" + taskName + "\"\n");
        dot_source += ("\tnode [shape=plaintext]\n");

        for (String gen : generated)
            dot_source += "\t" + gen + "\n";

        dot_source += "\n\n";

        for (String rec : received)
            if (!generated.contains(rec))
                dot_source += "\t" + rec + "\n";

        dot_source += "\n\n";

        for (String gen : generated)
            dot_source += "\t\"" + taskName + "\" -> " + gen + "\n";

        dot_source += "\n\n";

        for (String rec : received)
            dot_source += "\t\"" + rec + "\" -> \"" + taskName + "\"\n";

        dot_source += "\n\n";

        dot_source += "}\n";

        GraphViz graph = new GraphViz();
        graph.addln(graph.start_graph());

        graph.add(dot_source);
        graph.addln(graph.end_graph());
        return graph;
    }

    public GraphViz generateSystemsGraph() {
        Collection<Announce> systems = index.getAvailableSystems();

        for (Announce an : systems)
            index.getDefinitions().getResolver().addEntry(an.getSrc(), an.getSysName());
        
        int my_id = 0; // TODO
        Vector<Announce>  ccus = index.getSystemsOfType(SYS_TYPE.CCU);

        LinkedHashMap<String, Vector<Integer>> incomingMessages = new LinkedHashMap<String, Vector<Integer>>();
        Vector<String> messagesSentToUDP = new Vector<String>();
        
        int tbindings = index.getDefinitions().getMessageId("TransportBindings");
        
        for (int i = index.getFirstMessageOfType(tbindings); i != -1; i = index.getNextMessageOfType(tbindings, i)) {
            
            my_id = index.sourceOf(i);
            
            try {
                TransportBindings msg = new TransportBindings();
                msg.copyFrom(index.getMessage(i));
                
                if (msg.getConsumer().equalsIgnoreCase("Transports.UDP")) {
                    messagesSentToUDP.add(index.getDefinitions().getMessageName((int)msg.getMessageId()));
                }
                else {
//                    System.err.println(msg.get_consumer());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < index.getNumberOfMessages(); i++) {
            if (index.sourceOf(i) != my_id) {
                String source = index.sourceNameOf(i);
                
//                System.out.println(source);
/*
                try {
                    Integer.parseInt(source);
                    continue;
                }
                catch (Exception e) {
                    
                }*/
                if (!incomingMessages.containsKey(source))
                    incomingMessages.put(source, new Vector<Integer>());

                if (!incomingMessages.get(source).contains(index.typeOf(i)))
                    incomingMessages.get(source).add(index.typeOf(i));               
            }
        }
        
        GraphViz graph = new GraphViz();
     
        graph.addln(graph.start_graph());
        String dottitle = "labelloc=t,label=\"Networked systems\"," + "rankdir=LR," + "ratio=auto" + ";"; // subestituir

        graph.addln(dottitle);

//        int j = 0;
        
        String my_name = index.sourceNameOf(my_id).replaceAll("\\W", "_");
        
        graph.addln();
  
        boolean useOriginalNames = false;
        
        if (useOriginalNames)
            graph.addln(my_name+"[label=\""+index.sourceNameOf(my_id)+"\"];");    //descomentar para remover "_" dos nos
        for (Announce name : systems) {            
            String txt = name.getSysName().replaceAll("\\W", "_");
            graph.addln(txt);
            if (useOriginalNames)
                graph.addln(txt+"[label=\""+name.getSysName()+"\"];");      //descomentar para remover "_" dos nos
        }
        
        graph.addln();
        
        for (String sys_name : incomingMessages.keySet()) {
            Vector<Integer> sent = incomingMessages.get(sys_name);
            String txt = sys_name.replaceAll("\\W", "_");
            
            for (Integer i : sent) {
                if (useOriginalNames)
                    graph.addln(txt+"[label=\""+sys_name+"\"];");     //descomentar para remover "_" dos nos
                graph.addln(txt +" -> "+my_name+"[label=\""+index.getDefinitions().getMessageName(i)+"\"];");   
            }
        }
        
        graph.addln();
        
        for (String m : messagesSentToUDP) {
            
            for (Announce an : ccus) {
                if (useOriginalNames)
                    graph.addln(an.getSysName().replaceAll("\\W", "_")+"[label=\""+an.getSysName()+"\"];");      //descomentar para remover "_" dos nos
                graph.addln(my_name+" -> "+an.getSysName().replaceAll("\\W", "_")+"[label=\""+m+"\"];");
            }
            
        }
        
        
        return graph;
    }


    public GraphViz generateGraph(Collection<String> ignoredTasks, Collection<String> ignoredMessages,
            Collection<String> LogFilter) throws Exception {

        LinkedHashMap<Integer, String> entitiesToTasks = new LinkedHashMap<Integer, String>();
        LinkedHashMap<Integer, Vector<String>> msgConsumers = new LinkedHashMap<Integer, Vector<String>>();
        LinkedHashMap<Integer, Vector<Integer>> msgsGenerated = new LinkedHashMap<Integer, Vector<Integer>>();
        int mainVehicle = -1;

        //generate entitiesToTasks
        int type = index.getDefinitions().getMessageId("EntityInfo");

        for (int i = index.getFirstMessageOfType("EntityInfo"); i != -1; i = index.getNextMessageOfType(type, i)) {
            IMCMessage msg = index.getMessage(i);
            if (msg.getString("component").contains("Core")) {
                entitiesToTasks.put(msg.getInteger("id"), index.sourceNameOf(i));
                continue;
            }
            entitiesToTasks.put(msg.getInteger("id"), msg.getString("component"));
            mainVehicle = msg.getSrc();
        }

        for (int i = index.getFirstMessageOfType("Announce"); i != -1; i = index.getNextMessageOfType("Announce", i)) {
            IMCMessage msg = index.getMessage(i);
            index.getDefinitions().getResolver().addEntry(msg.getSrc(), msg.getString("sys_name"));
        }

        //generate msgConsumers
        type = index.getDefinitions().getMessageId("TransportBindings");
        if (index.getFirstMessageOfType("TransportBindings") == -1) {
            throw new Exception("There are no TransportBindings messages.");
        }
        for (int i = index.getFirstMessageOfType(type); i != -1; i = index.getNextMessageOfType(type, i)) {
            IMCMessage msg = index.getMessage(i);
            int msg_id = msg.getInteger("message_id");
            String consumer = msg.getString("consumer");

            Vector<String> consumers = msgConsumers.get(msg_id);
            if (consumers == null) {
                consumers = new Vector<String>();
                msgConsumers.put(msg_id, consumers);
            }
            consumers.add(consumer);
        }

        //generate msgsGenerated
        for (int i = 0; i < index.getNumberOfMessages(); i++) {
            IMCMessage msg = index.getMessage(i);
            int src_ent = msg.getHeader().getInteger("src_ent");
            int src = msg.getSrc();
            if (src != mainVehicle) {
                entitiesToTasks.put(-src, index.sourceNameOf(i));
                src_ent = -src;
            }

            if (src_ent != 255) {                
                Vector<Integer> vec = msgsGenerated.get(src_ent);
                if (vec == null) {
                    vec = new Vector<Integer>();
                    msgsGenerated.put(src_ent, vec);
                }
                if (!vec.contains(msg.getMgid()))
                    vec.add(msg.getMgid());
            }
            else {
                //System.err.println("src_ent of "+msg.getAbbrevName()+" is 255:");
                //msg.dump(System.err);
            }
        }

        // usar linguagem dot para criar grafo

        LinkedHashMap<Integer, String> vertice1 = new LinkedHashMap<Integer, String>();
        LinkedHashMap<Integer, String> vertice2 = new LinkedHashMap<Integer, String>();
        LinkedHashMap<Integer, String> edge = new LinkedHashMap<Integer, String>();
        LinkedHashMap<String, Integer> vertices = new LinkedHashMap<String, Integer>();
        LinkedHashMap<String, Integer> highlighted = new LinkedHashMap<String, Integer>();
        Vector<String> source_ccus = new Vector<String>();

        GraphViz graph = new GraphViz();
        graph.addln(graph.start_graph());
        String dottitle = "labelloc=t,label=\"" + LogFilter + "\"," + "rankdir=LR," + "ratio=auto" + ";"; // subestituir
        // "EntityInfo"
        // por titulo
        graph.addln(dottitle);

        int j = 0;
        for (String name : entitiesToTasks.values()) {

            boolean ignore = false;
            for (String regexp : ignoredTasks)
                if (name.matches(regexp))
                    ignore = true;

            if (!ignore) {
                vertices.put(name, j);
                j++;
            }
        }

        Vector<Announce>  ccus = index.getSystemsOfType(SYS_TYPE.CCU);
        for(Announce an : ccus)
            source_ccus.add(an.getSysName());
        //Create hashmaps of nodes and edges
        int i = 0;
        for (int entId : msgsGenerated.keySet()) {
            String task = entitiesToTasks.get(entId);
            for (int msg : msgsGenerated.get(entId)) {
                if (!msgConsumers.containsKey(msg))
                    continue;
                for (String consumer : msgConsumers.get(msg)) {
                    String msgname = index.getDefinitions().getMessageName(msg);
                    Integer v1 = vertices.get(task);
                    Integer v2 = vertices.get(consumer);
                    //fix Daemon without ids
                    if (task.equals("Daemon") && consumer.equals("Daemon")) {
                        vertice1.put(i, task);
                        vertice2.put(i, consumer);
                        edge.put(i, msgname);
                        i++;
                        continue;
                    }
                    if (task.equals("Daemon") && v2 != null) {
                        vertice1.put(i, task);
                        vertice2.put(i, consumer);
                        edge.put(i, msgname);
                        i++;
                        continue;
                    }
                    if (v1 != null && consumer.equals("Daemon")) {
                        vertice1.put(i, task);
                        vertice2.put(i, consumer);
                        edge.put(i, msgname);
                        i++;
                        continue;
                    }
                    //end of fix Daemon without ids
                    //test ids
                    if (v1 == null || v2 == null) {
                        System.err.println("Edge between " + v1 + " (" + task + ") and " + v2 + " (" + consumer
                        + ") is not possible of msgsGenerated entId "+msg+".");
                       continue;
                   }
                    //end of test ids
                    //exchange "Transports.UDP" for the source CCUS
                    if (task.equals("Transports.UDP") && consumer.equals("Transports.UDP")) {
                        for (String source : source_ccus){
                        vertice1.put(i, source);
                        vertice2.put(i, source);
                        edge.put(i, msgname);
                        i++;
                        }
                        continue;
                    }
                    if (task.equals("Transports.UDP") && !consumer.equals("Transports.UDP")) {
                        for (String source : source_ccus){
                        vertice1.put(i, source);
                        vertice2.put(i, consumer);
                        edge.put(i, msgname);
                        i++;
                        }
                        continue;
                    }
                    if (consumer.equals("Transports.UDP") && !task.equals("Transports.UDP")) {
                        for (String source : source_ccus){
                        vertice1.put(i, task);
                        vertice2.put(i, source);
                        edge.put(i, msgname);
                        i++;
                        }
                        continue;
                    }
                  //End of exchange "Transports.UDP" for the source CCUS
                    vertice1.put(i, task);
                    vertice2.put(i, consumer);
                    edge.put(i, msgname);
                    i++;
                }

            }
        }

        //connect nodes and edges, put them on one hashmap
        for (int s : vertice1.keySet()) {
            String v1 = vertice1.get(s);
            String v2 = vertice2.get(s);
            String arco = edge.get(s);
            if (!LogFilter.contains("All"))
                if (!LogFilter.contains(arco))
                    continue;
            String Nv1IChar = v1.replaceAll("\\W", "_");
            String Nv2IChar = v2.replaceAll("\\W", "_");
            String dotgraph = Nv1IChar + " -> " + Nv2IChar + " [ label=" + "\"" + arco + "\"";
            // if (!dotvector.contains(dotgraph)) {
            // dotvector.add(dotgraph);
            // graph.addln(dotgraph);
            // }

            //build highlights
            Integer nedege = highlighted.get(dotgraph);
            if (nedege == null) {
                highlighted.put(dotgraph, 1);
            }

            else {
                nedege++;
                highlighted.put(dotgraph, nedege);
            }

        }
        int maxValueInMap = (Collections.max(highlighted.values()));
        ColorMap cp = ColorMapFactory.createBlueToRedColorMap();
        int nedges = 0;
        for (Entry<String, Integer> entry : highlighted.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Color c = cp.getColor((double)value / (double)maxValueInMap);
            String HEX = RGBtoHex(c.getRed(), c.getGreen(), c.getBlue());
            graph.addln(key + " color=" + "\"" + HEX + "\"];");
            nedges++;
        }
        
        //build legend
        graph.addln("{"); 
        graph.addln("rank = sink");
        graph.addln("subgraph clusterLegend {"); 
        graph.addln("rank = sink");
        graph.addln("label=\"Legend\";");
        graph.addln("node [shape=record, style=filled];");
            if (maxValueInMap <= 5 ){
                for(int k = 1; k <= maxValueInMap ; k++){
                    Color c = cp.getColor((double)k / (double)maxValueInMap);
                    String HEX = RGBtoHex(c.getRed(), c.getGreen(), c.getBlue());
                    graph.addln("\""+ k +" messages\""+ "[fillcolor=" + "\"" + HEX + "\"];");
                }
            }
            else for(int k = 1; k <= 5 ; k++){
                double colord = ((maxValueInMap / 5) * k);
                int colori = (int)colord;
                Color c = cp.getColor(colord / maxValueInMap);
                String HEX = RGBtoHex(c.getRed(), c.getGreen(), c.getBlue());
                graph.addln("\""+ colori +" messages\""+ "[fillcolor=" + "\"" + HEX + "\"];");
            }
        
        //close dot_graph
        graph.addln("}");
        graph.addln("}");
        graph.addln(graph.end_graph());
        System.out.println(graph.getDotSource());
        System.out.println(nedges+" of diferent edges created");
        return graph;
    }

    private static String toHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }

        return builder.toString().toUpperCase();
    }

    public static String RGBtoHex(int r, int g, int b) {
        return "#" + toHexValue(r) + toHexValue(g) + toHexValue(b);
    }

    public static void main(String[] args) throws Exception {

        //        ColorBar cb = new ColorBar();
        //        cb.setCmap(ColorMapFactory.createJetColorMap());
        //        JFrame frame = new JFrame();
        //        frame.getContentPane().add(cb);
        //        frame.setVisible(true);

        String InFile1 = "/home/zp/Desktop/logs/TREX/Data.lsf";
        String InFile2 = "/home/zp/Desktop/logs/TREX/IMC.xml";

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            InFile1 = new File(chooser.getSelectedFile(), "Data.lsf").getAbsolutePath();
            InFile2 = new File(chooser.getSelectedFile(), "IMC.xml").getAbsolutePath();
        }

        LsfIndex index = new LsfIndex(new File(InFile1), new IMCDefinition(new File(InFile2)));

        IMCGraph p = new IMCGraph(index);
        // GraphViz g = p.generateInterface("Autonomy.TREX");
//        GraphViz g = p.generateGraph(Arrays.asList(""), Arrays.asList(""), Arrays.asList("EntityList"));
        GraphViz g = p.generateSystemsGraph();//para testar generateSystemsGraph
        g.showGraph("Communication graph");                                                              //abre em janela
        
//      descomentar o codigo que se segue para escrever para um ficheiro
        
//      String Type = "png"; //formatos suportados :dot, gv, gif, fig, pdf, ps, svg, png, plain;
//      String OutputFile = "/tmp/out"; //caminho do ficheiro de saida com nome do ficheiro sem tipo de ficheiro
//      g.writeGraphToFile( g.getGraph( g.getDotSource(), Type ), new File(OutputFile + "." + Type) );  //cria ficheiro
        System.out.println("end");
    }
}
