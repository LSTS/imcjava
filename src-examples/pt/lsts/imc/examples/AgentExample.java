package pt.lsts.imc.examples;

import java.util.LinkedHashMap;

import pt.lsts.imc.EntityList;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.EntityList.OP;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.SimpleAgent;

public class AgentExample extends SimpleAgent {

	LinkedHashMap<String, EntityList> entities = new LinkedHashMap<String, EntityList>();

	@Consume
	public void on(EstimatedState state) {
		System.out.println("Received EstimatedState from "
				+ state.getSourceName());

		if (!entities.containsKey(""+state.getSourceName()))
			send(state.getSourceName(), new EntityList().setOp(OP.QUERY));
	}
	
	@Consume
	public void on(EntityList msg) {
		entities.put(msg.getSourceName(), msg);
		System.out.println("Got entities from "+msg.getSourceName());
	}

	public static void main(String[] args) {
		new AgentExample();
	}
}
