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
 */
package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.ImcStringDefs;

/**
 * @author zp
 * 
 */
public class LsfMessageLogger {

    private static LsfMessageLogger instance = null;
    private IMCOutputStream ios = null;
    protected SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd'/'HHmmss");
    {
    	fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    protected String logPath = null;

    
    private String logBaseDir = "log/messages/";

    private LsfMessageLogger() {
        changeLog();

        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (LsfMessageLogger.this) {
                            IMCOutputStream copy = ios;
                            ios = null;
                            copy.close(); 
                        }                                               
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLogDir() {
        return logPath;
    }

    public static String getLogDirSingleton() {
        return instance.logPath;
    }
    
    public static boolean changeLogSingleton() {
        return instance.changeLog();
    }

    /**
     * @return
     */
    public boolean changeLog() {
        logPath = logBaseDir + fmt.format(new Date());
        
        File outputDir = new File(logPath);
        outputDir.mkdirs();
        
        IMCOutputStream iosTmp = null;
        try {
        	OutputStream fos = new GZIPOutputStream(new FileOutputStream(new File(outputDir.getAbsolutePath() + "/IMC.xml.gz")));
        	fos.write(ImcStringDefs.getDefinitions().getBytes());
        	fos.close();
        	iosTmp = new IMCOutputStream(new GZIPOutputStream(new FileOutputStream(new File(outputDir, "Data.lsf.gz"))));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (iosTmp != null) {
            synchronized (this) {
                if (ios != null) {
                    try {
                        ios.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ios = iosTmp;    
            }            
            return true;
        }
        return false;
    }

    private static LsfMessageLogger getInstance() {
        if (instance == null)
            instance = new LsfMessageLogger();

        return instance;
    }

    private synchronized boolean logMessage(IMCMessage msg) {
        try {
            if (ios != null)
                ios.writeMessage(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean log(IMCMessage msg) {
        return getInstance().logMessage(msg);
    }
    
    public static void main(String[] args) throws Exception {
		while(true) {
			Thread.sleep(100);
			LsfMessageLogger.log(new EstimatedState());
		}
	}

    public static void changeLogBaseDir(String newPath){
        getInstance().logBaseDir = newPath;
    }
}
