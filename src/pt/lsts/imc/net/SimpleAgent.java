package pt.lsts.imc.net;

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

	public void stop() {
		proto.stop();
	}

	public static void main(String[] args) {
		new SimpleAgent();
	}
}
