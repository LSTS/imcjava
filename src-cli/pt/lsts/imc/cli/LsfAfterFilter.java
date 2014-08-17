package pt.lsts.imc.cli;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.lsf.UnserializedMessage;

public class LsfAfterFilter implements LsfStreamFilter {

	double after = -1;

	@Override
	public boolean apply(CommandLine cmd) throws Exception {
		String aftString = cmd.getOptionValue("a");
		if (aftString == null)
			return false;
		try {
			after = Double.parseDouble(aftString);
		} catch (Exception e) {
			throw new Exception("Invalid argument for '-a' option.");
		}		
		return true;
	}

	@Override
	public void createOptions(Options options) {
		options.addOption("a", "after", true,
				"Retain only the messages with timestamps after given unix time (in seconds)");
		}

	@Override
	public void filter(IMCInputStream input, IMCOutputStream output) {
		UnserializedMessage msg;
		int entityInfoId = input.getImcDefinition().getMessageId("EntityInfo");
		
		LinkedHashMap<Long, UnserializedMessage> entityInfos = new LinkedHashMap<Long, UnserializedMessage>();
		boolean firstMessage = true;
		
		while (true) {
			try {
				msg = UnserializedMessage.readMessage(input.getImcDefinition(),
						input);
				double time = msg.getTimestamp();
				
				if (time >= after) {
					if (firstMessage && !entityInfos.isEmpty()) {
						Vector<UnserializedMessage> infos = new Vector<UnserializedMessage>();
						infos.addAll(entityInfos.values());
						Collections.sort(infos);
						for (UnserializedMessage m : infos)
							output.write(m.getData());						
						entityInfos.clear();
					}
					output.write(msg.getData());
					firstMessage = false;
				}
				else if (msg.getMgId() == entityInfoId) {
					entityInfos.put(msg.getHash(), msg);
				}
			}

			catch (Exception e) {
				break;
			}
		}
	}
}
