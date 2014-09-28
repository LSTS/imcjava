package pt.lsts.imc.net;

import pt.lsts.imc.IMCMessage;
import pt.lsts.neptus.messages.listener.PeriodicCallbacks;

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
		PeriodicCallbacks.register(this);
	}

	public boolean broadcast(IMCMessage m) {
		return proto.broadcast(m);
	}
		
	public boolean send(String destination, IMCMessage m) {
		return proto.sendMessage(destination, m);		
	}

	public void stop() {
		proto.stop();
		if (t != null)
			t.interrupt();
		PeriodicCallbacks.stopAll();
	}

	public static void main(String[] args) {
		new SimpleAgent();
	}

}
