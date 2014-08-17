package pt.lsts.imc.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.lsf.UnserializedMessage;

public class LsfFilter {

	private IMCInputStream getInputStream(File f) throws Exception {
		try {
			IMCDefinition defs = getImcDefinition(f);
			if (defs == null)
				defs = IMCDefinition.getInstance();

			if (f.isDirectory() && new File(f, "Data.lsf").canRead())
				f = new File(f, "Data.lsf");
			else if (f.isDirectory() && new File(f, "Data.lsf.gz").canRead())
				f = new File(f, "Data.lsf.gz");

			if (f.canRead()) {
				if (f.getName().endsWith("lsf")) {
					return new IMCInputStream(new FileInputStream(f), defs);
				} else if (f.getName().endsWith("lsf.gz")) {
					return new IMCInputStream(new GZIPInputStream(
							new FileInputStream(f)), defs);
				}
			}
		} catch (Exception e) {
		}
		throw new Exception("File '" + f + "' is not supported");
	}

	private IMCDefinition getImcDefinition(File f) throws Exception {

		File dir;

		if (!f.exists())
			return IMCDefinition.getInstance();
		if (f.isDirectory())
			dir = f;
		else
			dir = f.getParentFile();

		if (new File(dir, "IMC.xml").canRead()) {
			return new IMCDefinition(new File(dir, "IMC.xml"));
		} else if (new File(dir, "IMC.xml.gz").canRead()) {
			return new IMCDefinition(new GZIPInputStream(new FileInputStream(
					new File(dir, "IMC.xml.gz"))));
		}

		return IMCDefinition.getInstance();
	}
	
	private OutputStream createOutput(final CommandLine cmd) throws Exception {
		if (cmd.hasOption("o")) {
			return new FileOutputStream(cmd.getOptionValue("o"));
		}
		return System.out;
	}

	private IMCInputStream createInput(final CommandLine cmd) throws Exception {
		// Check if the user wants to read from standard input
		if (cmd.getArgs().length == 1 && cmd.getArgs()[0].equals("-"))
			return new IMCInputStream(System.in, IMCDefinition.getInstance());

		// If we are handling a single file, just return the input stream for it
		if (cmd.getArgs().length == 1)
			return getInputStream(new File(cmd.getArgs()[0]));

		final PipedOutputStream pipeOut = new PipedOutputStream();
		final PipedInputStream pipeIn = new PipedInputStream(pipeOut);

		Thread t = new Thread() {
			
			LinkedHashMap<File, IMCInputStream> inputs = new LinkedHashMap<File, IMCInputStream>();
			LinkedHashMap<File, UnserializedMessage> messages = new LinkedHashMap<File, UnserializedMessage>();
			LinkedHashMap<File, Double> timestamps = new LinkedHashMap<File, Double>();
			double curTime = Double.MAX_VALUE;

			public void run() {

				for (String arg : cmd.getArgs()) {
					File f = new File(arg);
					try {
						IMCInputStream iis = getInputStream(f);
						inputs.put(f, iis);

						UnserializedMessage msg = UnserializedMessage
								.readMessage(iis.getImcDefinition(), iis);
						double time = msg.getTimestamp();
						curTime = Math.min(curTime, time);
						messages.put(f, msg);
						timestamps.put(f, time);
					} catch (Exception e) {
						System.err.println("File '" + f + "' is not valid.");
					}
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
					try {
						pipeOut.write(messages.get(minFile).getData());
						UnserializedMessage msg = UnserializedMessage.readMessage(inputs.get(minFile).getImcDefinition(), inputs.get(minFile));
						messages.put(minFile, msg);
						timestamps.put(minFile, msg.getTimestamp());
					}
					catch (Exception e) {
						inputs.remove(minFile);
						timestamps.remove(minFile);						
					}
				}
				try {
					pipeOut.close();
				}
				catch (Exception e) {
					
				}
			};
		};
		t.setDaemon(true);
		t.start();		
		
		return new IMCInputStream(pipeIn, getImcDefinition(new File(cmd.getArgs()[0])));
	}

	private void printHelp(Options op) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("lsffilter [OPTION]... [FILE]...", op);
	}

	public LsfFilter(String[] args) {
		CommandLineParser parser = new BasicParser();
		Vector<LsfStreamFilter> filters = new Vector<LsfStreamFilter>();
		filters.add(new LsfAfterFilter());
		filters.add(new LsfBeforeFilter());
		filters.add(new LsfMinimumSeparation());
		Options op = new Options();

		op.addOption("r", "stdin", false, "Read messages from standard input");
		op.addOption("o", "output", false, "Output lsf data to this file");
		op.addOption("h", "help", false, "Print this help text");

		for (LsfStreamFilter f : filters)
			f.createOptions(op);

		CommandLine cmd = null;
		try {
			cmd = parser.parse(op, args);

			if (cmd.hasOption("h")) {
				printHelp(op);
				return;
			}
			
			IMCInputStream input = createInput(cmd);
			OutputStream output = createOutput(cmd);
			PipedOutputStream sink = new PipedOutputStream();
			
			for (LsfStreamFilter f : filters) {
				try {
					if (f.apply(cmd)) {
						
						IMCOutputStream ios = new IMCOutputStream(sink);
						
						
						f.filter(input, ios);
					}
				} catch (Exception e) {
					System.err.println("Invalid arguments: " + e.getMessage());
					printHelp(op);
					return;
				}
			}
			
			

		} catch (Exception e) {
			System.err.println("Invalid arguments: " + e.getMessage());
			printHelp(op);
			return;
		}

		
	}

	public static void main(String[] args) {
		new LsfFilter(args);
	}
}
