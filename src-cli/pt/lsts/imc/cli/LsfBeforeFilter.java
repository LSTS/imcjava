package pt.lsts.imc.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.lsf.UnserializedMessage;

public class LsfBeforeFilter implements LsfStreamFilter {

	double before = -1;

	@Override
	public boolean apply(CommandLine cmd) throws Exception {
		String befString = cmd.getOptionValue("b");
		if (befString == null)
			return false;		
		try {
			before = Double.parseDouble(befString);			
		} catch (Exception e) {
			throw new Exception("Invalid argument for '-b' option.");
		}
		return true;
	}

	@Override
	public void createOptions(Options options) {
		options.addOption("b", "before", true,
				"Retain only the messages with timestamps before given unix time (in seconds)");
	}

	@Override
	public void filter(IMCInputStream input, IMCOutputStream output) {
		UnserializedMessage msg;
		while (true) {
			try {
				msg = UnserializedMessage.readMessage(input.getImcDefinition(),
						input);
				double time = msg.getTimestamp();
				if (time < before)
					output.write(msg.getData());
			}

			catch (Exception e) {
				break;
			}
		}
	}
}
