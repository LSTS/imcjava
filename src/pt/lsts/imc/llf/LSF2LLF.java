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
 * $Id:: LSF2LLF.java 333 2013-01-02 11:11:44Z zepinto                         $:
 */
package pt.lsts.imc.llf;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.gz.MultiMemberGZIPInputStream;

public class LSF2LLF {

    protected Vector<IConverterListener> listeners = new Vector<IConverterListener>();
    protected boolean useSeparateThread = false;

    protected Exception error = null;
    protected boolean complete = false;
    protected boolean abortRqst = false;

    public void addListener(IConverterListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(IConverterListener listener) {
        listeners.remove(listener);
    }

    public void setUseSeparateThread(boolean useSeparateThread) {
        this.useSeparateThread = useSeparateThread;
    }

    public boolean isConversionDone() throws Exception {
        if (error != null)
            throw error;
        return complete;
    }

    public void abortConversion() {
        this.abortRqst = true;
    }

    public void convert(File dir) throws Exception {

        File data_lsf = null;

        for (File f : dir.listFiles()) {
            String filename = f.getName().toLowerCase();
            if (filename.endsWith(".lsf")) {
                data_lsf = f;
                break;
            }
        }
        if (data_lsf == null) {
        	for (File f : dir.listFiles()) {
                String filename = f.getName().toLowerCase();
                if (filename.endsWith(".lsf.gz")) {
                    data_lsf = f;
                    break;
                }
            }
        }
        
        if (data_lsf != null) {
            if (new File(data_lsf.getParent(), "IMC.xml").canRead())
                convert(new File(data_lsf.getParent(), "IMC.xml"), data_lsf);            
            else
                convert(IMCDefinition.getInstance(), data_lsf);
        }
        else {
            System.err.println("This program should be run inside a folder containing lsf files");
        }
    }

    public void convertInBackground(IMCDefinition defs, File data_lsf) throws Exception {
        final IMCDefinition d = defs;
        final File lsf = data_lsf;
        error = null;
        complete = false;
        Thread t = new Thread() {
            public void run() {
                try {
                    long size = lsf.length(), pos = 0, perc_parts = size / 100, percent = 0, msgcount = 0;
                    LLFMessageLogger logger = new LLFMessageLogger(lsf.getParent());

                    FileInputStream is = new FileInputStream(lsf);
                    MappedByteBuffer buff = is.getChannel().map(MapMode.READ_ONLY, 0, size);

                    IMCMessage message = null;
                    abortRqst = false;
                    while (buff.remaining() > 0 && !abortRqst) {
                        message = d.nextMessage(buff);
                        if (message != null) {
                            pos += message.getLong("size") + d.headerLength() + 2;
                            msgcount += logger.logMessage(message);
                            long p = pos / perc_parts;
                            if (p != percent) {
                                for (IConverterListener l : listeners)
                                    l.update(size, pos, msgcount);
                                percent = p;
                            }
                        }
                    }
                    logger.close();
                    is.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    error = e;
                }
                complete = true;
            };
        };
        t.start();
    }

    public void convert(IMCDefinition defs, File lsf) throws Exception {
       
        if (useSeparateThread) {
            convertInBackground(defs, lsf);
            return;
        }
        long size = lsf.length();
        FileInputStream fis = new FileInputStream(lsf);
        InputStream is = fis;
        if (lsf.getName().toLowerCase().endsWith("lsf.gz")) {
            is = new MultiMemberGZIPInputStream(is);
        }
        convert(defs, is, size, lsf.getParent());
    }

    public void convert(IMCDefinition defs, InputStream lsfIS, long size, String logDirectory) throws Exception {
        long pos = 0, perc_parts = size / 100, percent = 0, msgcount = 0;

        LLFMessageLogger logger = new LLFMessageLogger(logDirectory);
        logger.defs = defs;
        
        boolean isBufferOrStream = true;
        MappedByteBuffer buff = null;

        if (lsfIS instanceof FileInputStream) {
            FileInputStream is = (FileInputStream) lsfIS;
            FileChannel channel = is.getChannel();
            buff = channel.map(MapMode.READ_ONLY, 0, size);
            isBufferOrStream = true;
        }
        else {
        	isBufferOrStream = false;
        }        

        IMCMessage message = null;

        abortRqst = false;
        try {
            while (/* buff.remaining() > 0 && !abortRqst */true) {
                if (isBufferOrStream) {
                    if (!(buff.remaining() > 0 && !abortRqst))
                        break;
                }
                else {
                    if (!(lsfIS.available() > 0 && !abortRqst))
                        break;
                }
                if (isBufferOrStream)
                    message = defs.nextMessage(buff);
                else
                    message = defs.nextMessage(lsfIS);
                if (message != null) {
                    pos += message.getLong("size") + defs.headerLength() + 2;
                    msgcount += logger.logMessage(message);
                    long p = pos / perc_parts;
                    if (p != percent) {
                        for (IConverterListener l : listeners)
                            l.update(size, pos, msgcount);
                        percent = p;
                    }
                }
            }
        }
        catch (Exception e1) {
            if (!(e1 instanceof EOFException))
                throw e1;
        }
        finally {
            logger.close();
            // try { channel.close(); } catch (Exception e) {}
            try {
                lsfIS.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void convert(InputStream is, File data_lsf) throws Exception {
        IMCDefinition defs = new IMCDefinition(is);
        convert(defs, data_lsf);
    }

    public void convert(File imc_xml, File data_lsf) throws Exception {
        System.out.println("Converting " + data_lsf.getCanonicalPath() + " using definitions from "
                + imc_xml.getCanonicalPath() + "...");
        
        convert(new FileInputStream(imc_xml), data_lsf);
    }

    // @Deprecated
    // private static File testIfZipAndUnzipIt(File data_lsf, ProgressMonitor monitor) {
    // if (data_lsf.getName().toLowerCase().endsWith("lsf.gz")) {
    // FileInputStream fxInStream = null;
    // FileOutputStream fos = null;
    // try {
    // System.out.println("Unzipping " + data_lsf.getName() + "...");
    // if (monitor != null) {
    // monitor.setNote("Unzipping " + data_lsf.getName() + "...");
    // monitor.setProgress(10);
    // }
    // fxInStream = new FileInputStream(data_lsf);
    // GZIPInputStream gzDataLog = new MultiMemberGZIPInputStream(fxInStream);
    // File outFile = new File(data_lsf.getParentFile(), data_lsf.getName()+".tmplsf");
    // outFile.deleteOnExit();
    // fos = new FileOutputStream(outFile);
    // copyStreamToStream(gzDataLog, fos);
    // if (monitor != null) {
    // monitor.setNote("Output to " + outFile.getName());
    // monitor.setProgress(100);
    // }
    // return outFile;
    // } catch (Exception e) {
    // e.printStackTrace();
    // return data_lsf;
    // }
    // finally {
    // try { fos.close(); } catch (Exception e) { }
    // try { fxInStream.close(); } catch (Exception e) { }
    // }
    // }
    // else
    // return data_lsf;
    // }

    // private static boolean copyStreamToStream (InputStream inStream, OutputStream outStream) {
    // try {
    // byte[] extra = new byte[50000];
    //
    // int ret = 0;
    // @SuppressWarnings("unused")
    // int pos = 0;
    //
    // for (;;) {
    // ret = inStream.read(extra);
    // if (ret != -1) {
    // byte[] extra1 = new byte[ret];
    // System.arraycopy (extra, 0 , extra1, 0 , ret);
    // outStream.write (extra1);
    // outStream.flush();
    // pos =+ret;
    // }
    // else {
    // break;
    // }
    // }
    // // outStream.close(); //pdias - Tacking back again because some operations that use this don't want the stream
    // closed
    // return true;
    // }
    // catch (IOException e) {
    // // try {
    // // outStream.close(); //pdias - Tacking back again because some operations that use this don't want the stream
    // closed
    // // } catch (IOException e1) {
    // // }
    // return false;
    // }
    // }

    static void printOptions() {
        System.out.println(getOptions());
    }

    static String getOptions() {
        return "IMCJava - " + LSF2LLF.class.getSimpleName() + "\nCopyright (c) 2011 - Universidade do Porto"
                + " - FEUP LSTS. All rights reserved.\n" + "http://whale.fe.up.pt | lsts@fe.up.pt" + "\n\nOptions:\n"
                + "[[LSF-File | LSF.GZ-File] [IMC-Def-File]]\n";
    }

    public static void main(String[] args) throws Exception {
        final long startTime = System.currentTimeMillis();
        LSF2LLF converter = new LSF2LLF();
        converter.addListener(new IConverterListener() {
            public void update(long filesize, long curPosition, long messageCount) {
                System.out.print("\rProcessed " + String.format("%3d", curPosition * 100 / filesize) + "% ("
                        + messageCount + " msgs, " + LSF2LLFGui.parseToEngineeringRadix2Notation(curPosition, 1)
                        + "B)           ");
            }
        });

        if (args.length == 0)
            converter.convert(new File("."));
        else if (args.length == 1) {
            if ("-h".equalsIgnoreCase(args[0]) || "--help".equalsIgnoreCase(args[0])) {
                printOptions();
                return;
            }
            File data_lsf = new File(args[0]).getAbsoluteFile();
            if (data_lsf.isDirectory()) {
            	converter.convert(data_lsf);
            	return;
            }
            
            // data_lsf = testIfZipAndUnzipIt(data_lsf, null);
            if (new File(data_lsf.getParentFile(), "IMC.xml").canRead()) {
                System.out.println("Loading IMC definitions in "
                        + new File(data_lsf.getParentFile(), "IMC.xml").getCanonicalPath());
                converter.convert(new FileInputStream(new File(data_lsf.getParentFile(), "IMC.xml")), data_lsf);
            }
            else
                converter.convert(IMCDefinition.getInstance(), data_lsf);

        }
        else if (args.length == 2) {
            File data_lsf = new File(args[0]).getAbsoluteFile();
            // data_lsf = testIfZipAndUnzipIt(data_lsf, null);
            File imc_xml = new File(args[1]).getAbsoluteFile();
            converter.convert(new FileInputStream(imc_xml), data_lsf);
        }
        converter = null;
        System.out.println("\nGeneration time: " + ((float) (System.currentTimeMillis() - startTime) / 1000)
                + " seconds");
    }
}
