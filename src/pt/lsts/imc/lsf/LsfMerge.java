package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;


public class LsfMerge {

	public void merge(File[] files, File destination) throws Exception {
		
		OutputStream fos = new FileOutputStream(destination);
		System.out.println("Writing to "+destination.getAbsolutePath()+"...");
		IMCDefinition defs = null;
		File pivot = files[0].getParentFile();
		if (new File(pivot, "IMC.xml").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml"));
		else if (new File(pivot, "IMC.xml.gz").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml.gz"));
		else
			defs = IMCDefinition.getInstance();
		
		LinkedHashMap<File, IMCInputStream> inputs = new LinkedHashMap<File, IMCInputStream>(); 
		LinkedHashMap<File, UnserializedMessage> messages = new LinkedHashMap<File, UnserializedMessage>(); 
		LinkedHashMap<File, Double> timestamps = new LinkedHashMap<File, Double>(); 
		
		for (File f : files) {
			IMCInputStream input;
			if (getExtension(f).equals("lsf")) {
				input = new IMCInputStream(new FileInputStream(f)); 		
			}
			else if (getExtension(f).equals("lsf.gz")) {
				input = new IMCInputStream(new GZIPInputStream(new FileInputStream(f)));			
			}
			else {
				System.err.println("Unrecognized file type: "+f.getAbsolutePath());
				continue;
			}
			inputs.put(f, input);
		}
		
		System.out.println("Merging "+inputs.size()+" files...");
		double curTime = Double.MAX_VALUE;
		
		
		for (File f : inputs.keySet()) {
			UnserializedMessage msg = UnserializedMessage.readMessage(defs, inputs.get(f));
			double time = msg.getTimestamp();
			curTime = Math.min(curTime, time);
			messages.put(f, msg);
			timestamps.put(f, time);
		}
		
		while (!inputs.isEmpty()) {
			double minTime = Double.MAX_VALUE;
			File minFile = null;
			for (File f : timestamps.keySet()) {
				if (timestamps.get(f) < minTime) {
					minFile = f;
					minTime = timestamps.get(f);
				}
			}
			fos.write(messages.get(minFile).getData());
			try {
				UnserializedMessage msg = UnserializedMessage.readMessage(defs, inputs.get(minFile));
				messages.put(minFile, msg);
				timestamps.put(minFile, msg.getTimestamp());
			}
			catch (Exception e) {
				e.printStackTrace();
				inputs.remove(minFile);
				timestamps.remove(minFile);
			}
		}
		fos.close();
	}
	
	public static void main(String[] args) throws Exception {
		LsfMerge merge = new LsfMerge();
		
		if (args.length < 2) {
			System.out.println("Usage: lsfmerge <destination.lsf> <file1.lsf> [<file2.lsf> ...]");
			System.exit(1);
		}
		
		File[] files = new File[args.length - 1];
		for (int i = 1; i < args.length; i++)
			files[i-1] = new File(args[i]);
		
		merge.merge(files, new File(args[0]));
		System.exit(0);
	}
	
	private String getExtension(File f) {
		return f.getAbsolutePath().replaceAll("^[^.]*\\.(.*)$", "$1");
	}

}
