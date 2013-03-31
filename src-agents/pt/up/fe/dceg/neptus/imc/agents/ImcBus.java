package pt.up.fe.dceg.neptus.imc.agents;

import pt.up.fe.dceg.neptus.imc.AgentContext;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import pt.up.fe.dceg.neptus.imc.ImcAgent;
import pt.up.fe.dceg.neptus.imc.net.UDPTransport;
import pt.up.fe.dceg.neptus.messages.listener.MessageInfo;
import pt.up.fe.dceg.neptus.messages.listener.MessageListener;

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
		ctx.send(msg);
	}
	
	@Subscribe
	public void on(IMCMessage msg) {
		transport.sendMessage(remote_host, remote_port, msg);
	}

	@Override
	public void onStop() {
		transport.stop();
	}
}
