package pt.lsts.imc.net;

import pt.lsts.imc.IMCMessage;

/**
 * This class can be extended by stand-alone programs that want to interface with
 * an IMC network
 * 
 * @author zp
 *
 */
public class SimpleAgent {

	private Thread t = null;
	private IMCProtocol proto;
	{
		String name = getClass().getSimpleName();
		proto = new IMCProtocol(name, name.hashCode() % 100 + 6200);
		proto.register(this);
	}

	public boolean send(String destination, IMCMessage m) {
		return proto.sendMessage(destination, m);		
	}

	public void stop() {
		proto.stop();
		if (t != null)
			t.interrupt();
	}

	public static void main(String[] args) {
		new SimpleAgent();
	}

}
