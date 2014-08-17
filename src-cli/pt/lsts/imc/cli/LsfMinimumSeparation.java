package pt.lsts.imc.cli;

import java.util.LinkedHashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.lsf.UnserializedMessage;

public class LsfMinimumSeparation implements LsfStreamFilter {

	private double minSep = 0;
	private LinkedHashMap<Long, Double> lastMessageTimestamps = new LinkedHashMap<Long, Double>();

	@Override
	public boolean apply(CommandLine cmd) throws Exception {
		String sepString = cmd.getOptionValue("s");
		if (sepString == null)
			return false;
		
		try {
			minSep = Double.parseDouble(sepString);
		} catch (Exception e) {
			throw new Exception("Invalid argument for '-s' option.");
		}
		return true;
	}

	@Override
	public void createOptions(Options options) {
		options.addOption("s", "minimum-separation", true,
				"Minimum separation between periodic messages (in seconds)");
	}

	@Override
	public void filter(IMCInputStream input, IMCOutputStream output) {
		UnserializedMessage msg;
		lastMessageTimestamps.clear();
		while (true) {
			try {
				msg = UnserializedMessage.readMessage(input.getImcDefinition(),
						input);
				long hash = msg.getHash();
				double time = msg.getTimestamp();
				if (lastMessageTimestamps.containsKey(hash)
						&& input.getImcDefinition().getType(msg.getMgId())
								.hasFlag("periodic")) {
					double diff = time - lastMessageTimestamps.get(hash);
					if (diff >= 0 && diff < minSep) {
						continue;
					}
				}
				lastMessageTimestamps.put(hash, time);
				output.write(msg.getData());
			}

			catch (Exception e) {
				break;
			}
		}
	}
}
