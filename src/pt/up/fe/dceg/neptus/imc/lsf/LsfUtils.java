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
 * $Id:: LsfUtils.java 340 2013-01-18 11:02:42Z zepinto                        $:
 */
package pt.up.fe.dceg.neptus.imc.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.ProgressMonitor;

import pt.up.fe.dceg.neptus.imc.IMCDefinition;
import pt.up.fe.dceg.neptus.imc.IMCMessage;

/**
 * This class provides some utility methods for dealing with LSF log files
 * @author zp
 *
 */
public class LsfUtils {
    
    /**
     * Check consistency of a specified LSF file
     * @param lsfFile The file to be checked
     * @param defs The definitions used to generate the LSF
     * @throws Exception In case the file cannot be read properly
     */
    public static void checkLsf(File lsfFile, IMCDefinition defs) throws Exception {
        long synchword = defs.getSyncWord();        
        
        FileInputStream is = new FileInputStream(lsfFile);
        FileChannel channel = is.getChannel();
        is.close();

        MappedByteBuffer buff = channel.map(MapMode.READ_ONLY, 0, lsfFile.length());
        
        long sync = buff.getShort() & 0xFFFF;
        if (sync == defs.getSwappedWord()) {
            System.out.println("File is little endian");
            buff.order(ByteOrder.LITTLE_ENDIAN);
        }
        else if (sync != defs.getSyncWord()) {
            System.out.printf("Invalid sync at start of file: 0x%04X.\n",sync);
        }
        else {
            System.out.println("File is big endian");
        }
        long lastPos = -1;
        long pos = 0;

        while(pos < lsfFile.length()) {
            
            if (pos != 0 && (buff.getShort() & 0xFFFF) != synchword) {
                System.err.printf("Invalid synchword at %d: 0x%04X.\n", pos, (buff.getShort() & 0xFFFF));
                if (lastPos != -1) {
                    System.err.println("Dumping previous message: ");
                    debugMessageAt(lsfFile, defs, lastPos);
                    return;
                }
            }
            int type = (buff.getShort() & 0xFFFF);
            
            if (defs.getMessageName(type) == null) {
                System.err.printf("Invalid message type at %d: %d.\n", pos, type);
                System.err.println("Message dump: ");
                debugMessageAt(lsfFile, defs, pos);
                return;
            }
            
            else {
                System.out.printf("[%010d] %s\n", pos, defs.getMessageName(type));
            }
            
            lastPos = pos;
            int size = (buff.getShort() & 0xFFFF) + defs.headerLength();
            
            pos += size +2;
            buff.position((int) pos);
        }
        
    }
    
    /**
     * Used by {@link #checkLsf(File, IMCDefinition)}
     */
    protected static void debugMessageAt(File lsfFile, IMCDefinition defs, long pos) throws Exception {
        FileInputStream fis = new FileInputStream(lsfFile);
        fis.skip(pos);
        byte[] header = new byte[defs.headerLength()];
        fis.read(header);
        
        System.err.print("Header: (length = "+defs.headerLength()+")\n\t");
        for (int i = 0; i < defs.headerLength(); i++) {
            System.err.printf("%02X ", header[i] & 0XFF);
        }
        System.err.println();
        boolean bigEndian = header[0] == (defs.getSwappedWord() & 0xFF);
        int size;
        
        if (bigEndian)
            size = (header[4] << 8) + header[5];
        else
            size = (header[5] << 8) + header[4];
        
        System.err.print("Payload: (length = "+size+")\n\t");
        for (int i = 0; i < size; i++) {
            System.err.printf("%02X ", fis.read());
        }
        System.err.println();
        

        System.err.print("Footer: (length = 2)\n\t");
        for (int i = 0; i < 2; i++) {
            System.err.printf("%02X ", fis.read());
        }
        System.err.println();
        
        fis.close();
    }
    
    /**
     * Change the IMC definitions of a given LSF file, generates a new one
     * @param originalDir The folder of the original LSF file
     * @param destinationDir The destination folder, containing the new IMC definitions
     * @param messagesToFilter Messages to filtered out (optional)
     * @param m ProgressMonitor to be updated according to the progress of this process (optional)
     * @throws Exception In case some file cannot be read / written to.
     */
    public static void translate(File originalDir, File destinationDir, Vector<String> messagesToFilter, ProgressMonitor m) throws Exception {

        if (m != null)
            m.setMaximum(100);
        
        if (messagesToFilter == null)
            messagesToFilter = new Vector<String>();
        
        LsfIndex index = new LsfIndex(new File(originalDir, "Data.lsf"));

        IMCDefinition destDefs = new IMCDefinition(new FileInputStream(new File(destinationDir, "IMC.xml")));

        FileOutputStream fos = new FileOutputStream(new File(destinationDir, "Data.lsf"));
        int lastPercent = -1;

        System.out.println("Translating from "+originalDir+" to "+destinationDir);
        for (int i = 0; i < index.getNumberOfMessages(); i++) {
            
            if (messagesToFilter.contains(index.getDefinitions().getMessageName(index.typeOf(i))))
                continue;
            
            IMCMessage original = index.getMessage(i);

            if (!destDefs.messageExists(original.getAbbrev())) {
                System.err.println("Dropping message "+original.getAbbrev()+" because it doesn't exists in destination IMC.");
                continue;
            }

            IMCMessage result = original.cloneMessage(destDefs);
            destDefs.serialize(result, fos);
            int percent = (i*100 / index.getNumberOfMessages());
            if (percent != lastPercent) {
                if (m != null) {
                    m.setNote(percent+"% done...");
                    m.setProgress(percent);
                }
                else
                    System.out.println(percent+"% done...");
                
                lastPercent = percent;
            }

        }
        if (m != null)
            m.close();
        fos.close();
    }

    /**
     * Merge two or more LSF files into one, interleaving data according to time stamps
     * @param sources The source lsf indexes to be processed
     * @param destinationDir The folder where the resulting LSF file will be stored at
     * @param messagesToFilter Messages to be filtered out (optional)
     * @param m ProgressMonitor to be updated according to the progress of this process (optional)
     * @throws Exception In case some file cannot be read / written to.
     */
    public static void merge(LsfIndex[] sources, File destinationDir, Vector<String> messagesToFilter, ProgressMonitor m) throws Exception {

        if (m != null)
            m.setMaximum(100);
        
        if (messagesToFilter == null)
            messagesToFilter = new Vector<String>();

        destinationDir.mkdirs();
        FileOutputStream fos = new FileOutputStream(new File(destinationDir, "Data.lsf"));

        FileInputStream fis = new FileInputStream(new File(sources[0].getLsfFile().getParent(), "IMC.xml"));
        FileOutputStream fosXml = new FileOutputStream(new File(destinationDir, "IMC.xml"));
        byte[] buffer = new byte[1024];
        while (fis.read(buffer) != -1) {
            fosXml.write(buffer);
        }
        fosXml.close();
        fis.close();

        int[] srcId = new int[sources.length];
        int[] curIdx = new int[sources.length];        
        double curTime[] = new double[sources.length];
        long totalMessages = 0, processedMessages = 0;

        for (int i = 0; i < sources.length; i++) {
            srcId[i] = sources[i].sourceOf(0);
            curIdx[i] = 0;
            totalMessages += sources[i].getNumberOfMessages();            
        }

        int progress = -1;

        while (processedMessages < totalMessages) {

            int newProgress = (int)(100 * processedMessages/totalMessages);
            if (newProgress != progress) {
                progress = newProgress;
                if (m != null)
                    m.setProgress(progress);
                else
                    System.out.println(progress+"% done...");
            }

            for (int i = 0; i < sources.length; i++) {
                if (curIdx[i] >= sources[i].getNumberOfMessages())
                    curTime[i] = Double.MAX_VALUE;
                else if (sources[i].sourceOf(curIdx[i]) != srcId[i])
                    curTime[i] = 0;
                else
                    curTime[i] = sources[i].timeOf(curIdx[i]);
            }

            int minTimeSource = 0;
            double minTime = Double.MAX_VALUE;
            for (int i = 0; i < sources.length; i++) {
                if (curTime[i] < minTime) {
                    minTime = curTime[i];
                    minTimeSource = i;
                }
            }

            String msgName = sources[minTimeSource].getDefinitions().getMessageName(
                    sources[minTimeSource].typeOf(curIdx[minTimeSource]));

            LsfIndex lsfIndex = sources[minTimeSource];
            int idx = curIdx[minTimeSource];
            curIdx[minTimeSource] = curIdx[minTimeSource] + 1;
            processedMessages++;

            if (messagesToFilter.contains(msgName)) {
                continue;
            }

            byte[] msgBytes = lsfIndex.getMessageBytes(idx);
            fos.write(msgBytes);
        }
        fos.close();
        if (m != null)
            m.close();
        else {
            System.out.println("done!");
        }                
    }
    
    /**
     * Merge two or more LSF log folders into one, interleaving data according to time stamps
     * @param srcFolders The folders with LSF log files to be processed 
     * @param outputFolder The folder where the resulting LSF file will be stored at
     * @param messagesToFilter Messages to be filtered out (optional)
     * @param m ProgressMonitor to be updated according to the progress of this process (optional)
     * @throws Exception In case some file cannot be read / written to.
     */ 
    public static void merge(File[] srcFolders, File outputFolder, Vector<String> messagesToFilter, ProgressMonitor m) throws Exception {
        Vector<LsfIndex> indexes = new Vector<LsfIndex>();
        
        for (File f : srcFolders) {
            if (new File(f, "Data.lsf").canRead())
                indexes.add(new LsfIndex(new File(f, "Data.lsf")));
            else if (new File(f, "Data.lsf.gz").canRead())
                indexes.add(new LsfIndex(new File(f, "Data.lsf.gz")));
        }
        
        merge(indexes.toArray(new LsfIndex[0]), outputFolder, messagesToFilter, m); 
    }
    
    /**
     * Generate some statistics for a given LSF file
     * @see LogStats
     * @param index The index to be processed
     * @return The log statistics.
     */
    public static LogStats getStats(LsfIndex index) {
        return new LogStats(index);
    }

    /**
     * A class used to hold log statistics
     * @author zp
     */
    public static class LogStats {
        protected LinkedHashMap<String, Double> msgFrequencies = new LinkedHashMap<String, Double>();
        protected LinkedHashMap<String, Long> msgWeights = new LinkedHashMap<String, Long>();
        protected LinkedHashMap<String, Long> entityWeights = new LinkedHashMap<String, Long>();

        /**
         * Class constructor
         * @param index The index to be processed
         */
        public LogStats(LsfIndex index) {

            LinkedHashMap<String, Long> countByType = new LinkedHashMap<String, Long>();

            for (int i = 0; i < index.getNumberOfMessages(); i++) {
                String type = index.getDefinitions().getMessageName(index.typeOf(i));
                String entity = index.entityNameOf(i);
                if (!msgWeights.containsKey(type)) {
                    msgWeights.put(type, (long)index.sizeOf(i));
                    countByType.put(type, 1l);
                }
                else {
                    msgWeights.put(type, index.sizeOf(i) + msgWeights.get(type));
                    countByType.put(type, countByType.get(i)+1);
                }


                if (!entityWeights.containsKey(entity)) {
                    entityWeights.put(entity, (long)index.sizeOf(i));
                }
                else {
                    entityWeights.put(type, index.sizeOf(i) + entityWeights.get(type));
                }
            }
            
            double logLength = index.getEndTime() - index.getStartTime();
            
            for (String type : countByType.keySet()) {
                msgFrequencies.put(type, countByType.get(type)/logLength);
            }
        }

        /**
         * @return the msgFrequencies
         */
        public final LinkedHashMap<String, Double> getMsgFrequencies() {
            return msgFrequencies;
        }

        /**
         * @return the msgWeights
         */
        public final LinkedHashMap<String, Long> getMsgWeights() {
            return msgWeights;
        }

        /**
         * @return the entityWeights
         */
        public final LinkedHashMap<String, Long> getEntityWeights() {
            return entityWeights;
        }
    }

    
    
    public static void main(String[] args) throws Exception {
        checkLsf(new File("/home/zp/Desktop/20120622/concatenated/Data.lsf"), IMCDefinition.getInstance());
    }
    
}
