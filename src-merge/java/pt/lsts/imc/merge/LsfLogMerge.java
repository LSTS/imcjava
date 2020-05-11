/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: LsfLogMerge.java 339 2013-01-12 20:40:51Z zepinto                     $:
 */
package pt.lsts.imc.merge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.lsf.LsfIndex;

/**
 * This class merges and sorts two lsf files using only data on the disk
 * @author ribcar
 *
 */
public class LsfLogMerge {

    public static void filedelete(String File) {
        File file = new File(File);
        boolean exists = file.exists();
        if (!exists) {
            // It returns false if file does not exist
            System.out.println("The file does not exist : " + exists);
        }
        else {
            // It returns true if file exists
            boolean success = file.delete();
            if (!success) {
                System.out.println("Deletion failed.");
                System.exit(0);
            }
            else {
                System.out.println(File + "\nFile deleted.");
            }
        }
    }

    public static void writestream(String OutFile1, LsfIndex index1, LsfIndex index2) throws IOException {
        System.out.println("Creating Lsf...");
        File outfile = new File(OutFile1);
        IMCDefinition defs1 = index1.getDefinitions();
        IMCDefinition defs2 = index2.getDefinitions();
        IMCDefinition defs = defs2;
        
        if (defs1.getVersion().compareTo(defs2.getVersion()) > 0) {
            defs = defs1;
        }
       
        IMCOutputStream os = new IMCOutputStream(new FileOutputStream(outfile));
        int k = 0;
        int j = 0;
        int count = 0;
        double msgs1t = index1.timeOf(j);
        double msgs2t = index2.timeOf(k);
        while ((j < index1.getNumberOfMessages()) && (k < index2.getNumberOfMessages())) {
            if ((msgs1t >= msgs2t) && (k < index2.getNumberOfMessages())) {
                while ((msgs1t >= msgs2t) & (k < index2.getNumberOfMessages())) {
                    msgs2t = index2.timeOf(k);
                    msgs1t = index1.timeOf(j);
                    IMCMessage m = index2.getMessage(k);
                    m.serialize(defs, os);
                    // System.out.println(count+" message type 2 were written "+msgs1t+" - "+msgs2t+" - "+j+" - "+k+" - "+new
                    // Date(m.getTimestampMillis()));
                    count++;
                    k++;
                }
            }
            if ((msgs1t <= msgs2t) && (j < index1.getNumberOfMessages())) {
                while ((msgs1t <= msgs2t) && (j < index1.getNumberOfMessages())) {
                    msgs1t = index1.timeOf(j);
                    msgs1t = index1.timeOf(k);
                    IMCMessage m = index1.getMessage(j);
                    m.serialize(defs, os);
                    // System.out.println(count+" message type 1 were written "+msgs1t+" - "+msgs2t+" - "+j+" - "+k+" - "+new
                    // Date(m.getTimestampMillis()));
                    count++;
                    j++;
                }
            }
            if ((msgs1t <= msgs2t) && (j == index1.getNumberOfMessages()) && (k < index2.getNumberOfMessages())) {
                while ((k < index2.getNumberOfMessages())) {
                    msgs2t = index2.timeOf(k);
                    // msgs1t = index1.timeOf(j);
                    IMCMessage m = index2.getMessage(k);
                    m.serialize(defs, os);
                    // System.out.println(count+" message type 2 were written "+msgs1t+" - "+msgs2t+" - "+j+" - "+k+" - "+new
                    // Date(m.getTimestampMillis()));
                    count++;
                    k++;
                }
            }
            if ((msgs1t >= msgs2t) && (k == index2.getNumberOfMessages()) && (j < index1.getNumberOfMessages())) {
                while ((j < index1.getNumberOfMessages())) {
                    msgs1t = index1.timeOf(j);
                    // msgs1t = index1.timeOf(k);
                    IMCMessage m = index1.getMessage(j);
                    m.serialize(defs, os);
                    // System.out.println(count+" message type 1 were written "+msgs1t+" - "+msgs2t+" - "+j+" - "+k+" - "+new
                    // Date(m.getTimestampMillis()));
                    count++;
                    j++;
                }
            }
        }
        System.out.println(count + " messages were written" + " -> (" + j + " - " + k + ") = " + (j + k));
        os.close();
    }

    public static void NString(LsfIndex index, String stList) {
        List<String> arList = new ArrayList<String>();
        for (int i = 0; i < index.getNumberOfMessages(); i++) {
            IMCMessage m = index.getMessage(i);
            arList.add(m.getString(stList));
            // System.out.println("msg: "+(i+1)+" "+stList+": "+m.getString("src"));
        }
        HashSet<String> h = new HashSet<String>(arList);
        arList.clear();
        arList.addAll(h);
        System.out.println(stList + " - " + arList);
        System.out.println("number of different " + stList + " - " + arList.size());
    }
    
    public static void merge(LsfIndex idx1, LsfIndex idx2, OutputStream result, ProgressMonitor m) throws Exception {
        int src1 = idx1.sourceOf(0);
        int src2 = idx2.sourceOf(0);
        
        int i1 = 0, i2 = 0;
        long totalMessages = idx1.getNumberOfMessages() | idx2.getNumberOfMessages();
        
        while (i1 < idx1.getNumberOfMessages() || i2 < idx2.getNumberOfMessages()) {
            
            if (m != null)
                m.setProgress((int)(100 * (i1+i2)/totalMessages));
            
            if (i2 >= idx2.getNumberOfMessages()) {
                result.write(idx1.getMessageBytes(i1++));
                continue;
            }
            
            if (i1 >= idx1.getNumberOfMessages()) {
                result.write(idx2.getMessageBytes(i2++));
                continue;
            }
            
            if (idx1.sourceOf(i1) != src1) {
                result.write(idx1.getMessageBytes(i1++));
                continue;
            }
            
            if (idx2.sourceOf(i2) != src2) {
                result.write(idx2.getMessageBytes(i2++));
                continue;
            }
            
            if (idx1.timeOf(i1)  < idx2.timeOf(i2)) {
                result.write(idx1.getMessageBytes(i1++));
                continue;
            }
            else {
                result.write(idx2.getMessageBytes(i2++));
                continue;
            }            
        }        
    }
    
    public static void main(String[] args) throws Exception {
    
        ProgressMonitor monitor = new ProgressMonitor(null, "Merging", "Creating merged log...", 0, 100);
        
        String InFile1 = "";
        String InFile2 = "";
        String OutFile1 = "";
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select first log to be merged");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = chooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            InFile1 = new File(chooser.getSelectedFile(), "Data.lsf").getAbsolutePath();
        }
        JFileChooser chooser2 = new JFileChooser();
        chooser2.setDialogTitle("Select second log to be merged");
        chooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option2 = chooser2.showOpenDialog(null);
        if (option2 == JFileChooser.APPROVE_OPTION) {
            InFile2 = new File(chooser2.getSelectedFile(), "Data.lsf").getAbsolutePath();
        }
        JFileChooser chooser3 = new JFileChooser();
        chooser.setDialogTitle("Select destination for resulting log");
        int option3 = chooser3.showSaveDialog(null);
        if (option3 == JFileChooser.APPROVE_OPTION) {
            File dir = chooser3.getSelectedFile();
            OutFile1 = dir+".lsf";
        }
        
        
        
        LsfIndex index1 = new LsfIndex(new File(InFile1), null);
        LsfIndex index2 = new LsfIndex(new File(InFile2), null);

        filedelete(OutFile1);
        
        FileOutputStream fos = new FileOutputStream(OutFile1);
        
        merge(index1, index2, fos, monitor);
    }
}
