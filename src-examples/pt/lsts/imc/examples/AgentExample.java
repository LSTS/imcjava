package pt.lsts.imc.examples;

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.LogBookEntry.TYPE;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.SimpleAgent;
import pt.lsts.neptus.messages.listener.Periodic;

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

	/**
	 * This method will be called every 10 seconds
	 */
	@Periodic(millisBetweenUpdates = 10 * 1000)
	public void doIt() {
		
		// Send a message to all known systems (including ourself)
		broadcast(new LogBookEntry(TYPE.DEBUG,
				System.currentTimeMillis() / 1000.0,
				getClass().getSimpleName(), "10 seconds have passed"));
	}

	public static void main(String[] args) {
		new AgentExample();
	}
}
