package pt.lsts.imc.agents;

import pt.lsts.imc.AgentContext;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.ImcAgent;
import pt.lsts.imc.net.UDPTransport;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

import com.google.common.eventbus.Subscribe;

public class ImcBus implements ImcAgent, MessageListener<MessageInfo, IMCMessage> {

	protected UDPTransport transport = new UDPTransport();
	protected int local_port, remote_port;
	protected String remote_host;
	protected AgentContext ctx = null;
	
	public ImcBus(int local_port, String remote_host, int remote_port) {
		this.local_port = local_port;
		this.remote_host = remote_host;
		this.remote_port = remote_port;		
	}
	
	@Override
	public void onStart(AgentContext conn) {
		this.ctx = conn;
		transport = new UDPTransport(local_port, 1);		
		transport.addMessageListener(this);		
	}
	
	@Override
	public void onMessage(MessageInfo info, IMCMessage msg) {		
		msg.setDst(-1);
		ctx.send(msg);
	}
	
	@Subscribe
	public void on(final IMCMessage msg) {
		if (msg.getDst() != -1) {
			transport.sendMessage(remote_host, remote_port, msg);
		}
	}

	@Override
	public void onStop() {
		transport.stop();
	}
}
