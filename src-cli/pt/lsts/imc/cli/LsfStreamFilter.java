package pt.lsts.imc.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCOutputStream;

public interface LsfStreamFilter {
	public void filter(IMCInputStream input, IMCOutputStream output);
	public void createOptions(Options options);
	public boolean apply(CommandLine commandLine) throws Exception;
}
