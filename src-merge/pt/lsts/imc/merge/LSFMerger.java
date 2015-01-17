/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: LSFMerger.java 333 2013-01-02 11:11:44Z zepinto                       $:
 */
package pt.lsts.imc.merge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import pt.lsts.imc.IMCDefinition;

/**
 * @author Ricardo Silva
 */
public class LSFMerger {

    private static String outFilename = "Data.lsf";
    private static File outputDir = null;
    private static File outputFileIMC = null;

    private final static int BUFSIZE = 4096;
    private static String IMCVersion = null;

    private static void usage() {
        System.err.println("usage: LSMerger [-o <output dir>] <list of dirs/files>");
    }

    public static void displayFileContents(File file, Boolean flagIMC, Boolean flagData) {
        if (file.getName().toString().compareTo("Data.lsf") == 0) {
            flagData = true;
            try {
                FileInputStream instream = new FileInputStream(file);
                File outputFile = new File(outputDir.toString() + "/" + outFilename);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileOutputStream outstream = new FileOutputStream(outputFile, true);
                byte[] buf = new byte[BUFSIZE];
                int len;
                while ((len = instream.read(buf)) > 0) {
                    outstream.write(buf, 0, len);
                }
                instream.close();
                outstream.close();
                System.out.println("     file:" + file.getCanonicalPath());
            }
            catch (IOException ioe) {
                System.err.println("Exception has been thrown" + ioe);
            }
        }
        else if (file.getName().toString().compareTo("Data.lsf.gz") == 0) {
            flagData = true;
            try {

                GZIPInputStream ginstream = new GZIPInputStream(new FileInputStream(file));
                File outputFile = new File(outputDir.toString() + "/" + outFilename);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileOutputStream outstream = new FileOutputStream(outputFile, true);
                byte[] buf = new byte[BUFSIZE];
                int len;
                while ((len = ginstream.read(buf)) > 0) {
                    outstream.write(buf, 0, len);
                }
                ginstream.close();
                outstream.close();
                System.out.println("     file:" + file.getCanonicalPath());
            }
            catch (IOException ioe) {
                System.err.println("Exception has been thrown" + ioe);
            }

        }
        else if (outputFileIMC == null && file.getName().toString().compareTo("IMC.xml") == 0) {
            flagIMC = true;
            try {
                FileInputStream instream = new FileInputStream(file);
                File outputFileIMC = new File(outputDir.toString() + "/IMC.xml");
                if (!outputFileIMC.exists()) {
                    outputFileIMC.createNewFile();
                    FileOutputStream outstream = new FileOutputStream(outputFileIMC, false);
                    byte[] buf = new byte[BUFSIZE];
                    int len;
                    while ((len = instream.read(buf)) > 0) {
                        outstream.write(buf, 0, len);
                    }
                    instream.close();
                    outstream.close();
                    System.out.println("     file:" + file.getCanonicalPath());

                    try {
                        IMCVersion = IMCDefinition.versionOfFile(file);
                        System.out.println("IMCDefinition version currently in use: "+IMCVersion);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // Já tenho as definições de IMC em uso. Preciso de comparar.
                    try {
                        if (IMCVersion.compareTo(IMCDefinition.versionOfFile(file))!=0) {
                            //Converter entre tipos de mensagem
                            System.err.println("The IMCDefinition versions difer!");
                            System.exit(1);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException ioe) {
                System.err.println("Exception has been thrown" + ioe);
            }
        }
    }

    public static void displayDirectoryContents(File dir) {
        Boolean flagIMC = false, flagData = false;
        try {
            File[] files = dir.listFiles();

            if (files.length != 0) {
                int fdCount = 0;
                /*
                 * Verificar se files contem um ficheiro Data.lsf ou Data.lsf.gz Caso contrário emitir msg de erro
                 */
                for (File file : files) {
                    if (file.isDirectory()) {
                        System.out.println("directory:" + file.getCanonicalPath());
                        fdCount++;
                        displayDirectoryContents(file);
                    }
                    else {
                        displayFileContents(file, flagIMC, flagData);
                        fdCount++;
                    }
                }
                if ((!flagIMC || !flagData) && fdCount != files.length) {
                    System.out.println("The log directory: " + dir.toString()
                            + "\nIs non empty. And do not contain a file Data.lsf or Data.lsf.gz");
                    System.exit(1);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // File outputDir = null;

        LinkedList<File> logsPath = new LinkedList<File>();

        if (args.length > 2) {
            if (!args[0].equals("-o")) {
                usage();
                System.exit(1);
            }
            outputDir = new File(args[1]);
            if (!outputDir.isDirectory()) {

                System.err.println("Output dir does not exists!");
                usage();
                System.exit(1);
            }

            File f1 = new File(outputDir, "IMC.xml");
            File f2 = new File(outputDir, "Data.lsf");
            File f3 = new File(outputDir, "lsf.index");

            if (f1.exists())
                f1.delete();
            if (f2.exists())
                f2.delete();
            if (f3.exists())
                f3.delete();

            for (int i = 2; i < args.length; i++)
                logsPath.add(new File(args[i]));
        }
        else {
            usage();
            System.exit(1);
        }

        if (logsPath.size() != 0) {
            for (File f : logsPath) {
                if (f.isDirectory()) {
                    displayDirectoryContents(f);
                }
                else {
                    displayFileContents(f, false, false);
                }
            }
        }
    }
}

class Tuple {
    public double timestamp;
    public int checksum;
    public int index;

    public Tuple(double timestamp, int checksum, int index) {
        this.checksum = checksum;
        this.timestamp = timestamp;
        this.index = index;
    }
}

class Entry implements Comparable<Entry> {
    public double timestamp;
    public int index;

    public Entry(int index, double time) {
        this.index = index;
        this.timestamp = time;
    }

    @Override
    public int compareTo(Entry o) {
        return ((Double) timestamp).compareTo(o.timestamp);
    }
}