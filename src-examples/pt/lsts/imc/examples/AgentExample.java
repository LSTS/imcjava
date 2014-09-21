package pt.lsts.imc.examples;

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.SimpleAgent;

/**
 * Simple stand-alone program to listen to IMC messages. The trick is to extend
 * SimpleAgent which will make this program announce itself and receive messages
 * from other peers in the network.
 * 
 * @see SimpleAgent
 * 
 * @author zp
 *
 */
public class AgentExample extends SimpleAgent {

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss.SSS");

	/**
	 * This method will be called whenever a LogBookEntry arrives from the
	 * selected vehicles and entities
	 * 
	 * @param log
	 *            The message
	 */
	@Consume(Source = { "lauv-seacon-1", "lauv-xplore-1" }, Entity = "Plan Engine")
	public void on(LogBookEntry log) {
		System.out.printf("[%s - %s]\n   %8s [%s] >> %s\n", sdf
				.format(new Date()), log.getSourceName(), log.getType()
				.toString(), log.getContext(), log.getText());
	}

	public static void main(String[] args) {
		new AgentExample();
	}
}
