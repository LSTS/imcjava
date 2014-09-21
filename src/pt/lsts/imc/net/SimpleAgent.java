package pt.lsts.imc.net;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

public class SimpleAgent {

	private IMCProtocol proto;

	public SimpleAgent() {
		String name = getClass().getSimpleName();
		
		proto = new IMCProtocol(name, name.hashCode() % 100 + 6200);
		proto.register(this);
		Thread t = new Thread() {
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			};
		};
		t.start();
	}

	public boolean send(String destination, IMCMessage m) {
		return proto.sendMessage(destination, m);
	}
	
	public String resolve(int imcId) {
		return IMCDefinition.getInstance().getResolver().resolve(imcId);
	}
	
	public String resolve(int imcId, int entity) {
		return IMCDefinition.getInstance().getResolver()
				.resolveEntity(imcId, entity);
	}

	public void stop() {
		proto.stop();
	}

	public static void main(String[] args) {
		new SimpleAgent();
	}
}
