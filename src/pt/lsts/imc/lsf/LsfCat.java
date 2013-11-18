package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;

public class LsfCat {

	protected LinkedHashMap<Long, Double> lastMessageTimestamps = new LinkedHashMap<Long, Double>();
	
	private String getExtension(File f) {
		return f.getAbsolutePath().replaceAll("^[^.]*\\.(.*)$", "$1");
	}

	public void concatenate(File[] files, File destination, long minimumSeparationMillis, boolean append) throws Exception {

		if (files == null || files.length == 0)
			return;

		double minSeparation = minimumSeparationMillis / 1000.0;

		OutputStream fos = new FileOutputStream(destination, append);
		if (append)
			System.out.println("Appending to "+destination.getAbsolutePath()+"...");
		else
			System.out.println("Writing to "+destination.getAbsolutePath()+"...");

		IMCDefinition defs = null;
		File pivot = files[0].getParentFile();
		if (new File(pivot, "IMC.xml").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml"));
		else if (new File(pivot, "IMC.xml.gz").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml.gz"));
		else
			defs = IMCDefinition.getInstance();

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
			System.out.println("Processing "+f.getAbsolutePath()+"...");
			long dropCount = 0;
			long writeCount = 0;
			UnserializedMessage msg;
			lastMessageTimestamps.clear();
			while(true) {
				try {
					msg = UnserializedMessage.readMessage(defs, input);
					long hash = msg.getHash();
					double time = msg.getTimestamp();
					if (lastMessageTimestamps.containsKey(hash) && defs.getType(msg.getMgId()).hasFlag("periodic")) {
						double diff = time - lastMessageTimestamps.get(hash);
						if (diff >= 0 && diff < minSeparation) {
							//System.out.println((long)(msg.getTimestamp()*1000)+" Dropping "+defs.getType(msg.getMgId()).getFullName());
							dropCount++;
							continue;
						}
					}
					writeCount++;
					lastMessageTimestamps.put(hash, time);
					//System.out.println((long)(msg.getTimestamp()*1000)+" writing "+defs.getType(msg.getMgId()).getFullName());
					fos.write(msg.getData());					
				}

				catch (Exception e) {
					e.printStackTrace();
					break;
				}
			}
			System.out.println("Wrote "+writeCount+" and dropped "+dropCount+" messages");
			input.close();
		}
		fos.close();
	}

	public static void main(String[] args) throws Exception {
		LsfCat cat = new LsfCat();
		
		if (args.length < 2) {
			System.out.println("Usage: lsfcat <destination.lsf> <file1.lsf> [<file2.lsf> ...]");
			System.exit(1);
		}
		
		File[] files = new File[args.length - 1];
		for (int i = 1; i < args.length; i++)
			files[i-1] = new File(args[i]);
		
		cat.concatenate(files, new File(args[0]), 1000, false);
		System.exit(0);
	}

}
