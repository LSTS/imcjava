package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.ImcStringDefs;

/**
 * @author zp
 * 
 */
class LsfMessageLogger {

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
        	FileUtils.write(new File(outputDir.getAbsolutePath() + "/IMC.xml"), ImcStringDefs.getDefinitions());
            iosTmp = new IMCOutputStream(new FileOutputStream(new File(outputDir, "Data.lsf")));
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
}
