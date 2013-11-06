package pt.lsts.imc.agents;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import pt.lsts.imc.AgentContext;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.ImcAgent;

import com.google.common.eventbus.Subscribe;

public class WebServerAgent extends AbstractHandler implements ImcAgent {

	int count = 0;
	@Override
	public void onStart(AgentContext conn) {
		Server server = new Server(9090);
		server.setHandler(this);
		try {
			server.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStop() {

	}

	@Subscribe
	public void on(IMCMessage m) {
		count++;
	}

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {

		response.setContentType("text/html;charset=utf-8");
		
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.getWriter().println("<h1>Processed "+count+" messages</h1>");
		response.getWriter().println(request.toString()+"<br/>");
		response.getWriter().println(request.getProtocol());
	}
}
