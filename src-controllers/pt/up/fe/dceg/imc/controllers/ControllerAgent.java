package pt.up.fe.dceg.imc.controllers;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import pt.up.fe.dceg.neptus.imc.Abort;
import pt.up.fe.dceg.neptus.imc.Announce.SYS_TYPE;
import pt.up.fe.dceg.neptus.imc.FollowReference;
import pt.up.fe.dceg.neptus.imc.IMCMessage;
import pt.up.fe.dceg.neptus.imc.IMCOutputStream;
import pt.up.fe.dceg.neptus.imc.PlanControl;
import pt.up.fe.dceg.neptus.imc.PlanControl.OP;
import pt.up.fe.dceg.neptus.imc.PlanManeuver;
import pt.up.fe.dceg.neptus.imc.PlanSpecification;
import pt.up.fe.dceg.neptus.imc.Reference;
import pt.up.fe.dceg.neptus.imc.net.IMCProtocol;
import pt.up.fe.dceg.neptus.imc.net.UDPTransport;
import pt.up.fe.dceg.neptus.messages.listener.MessageInfo;
import pt.up.fe.dceg.neptus.messages.listener.MessageListener;

public abstract class ControllerAgent implements MessageListener<MessageInfo, IMCMessage> {

	private UDPTransport transport = null;
	private Thread heartbeater;
	protected LinkedHashMap<Class<?>, Method> listenerMethods = new LinkedHashMap<Class<?>, Method>();
	private final boolean debug = false;
	private String host;
	private int port;
	private Timer controlTimer = null;

	public abstract int getTaskEntity();
	public abstract Reference guide();
	
	public final String getEntityName() {
		return getClass().getSimpleName();
	}
	
	private void buildListeners() {
		listenerMethods.clear();
		for (Method m : getClass().getMethods()) {
			if (m.getName().equals("consume") && m.getParameterTypes().length == 1) {
				Class<?> c = m.getParameterTypes()[0];
				listenerMethods.put(c, m);
			}
		}
		
		if(debug)
			System.out.println(listenerMethods);
	}
	
	public final void connect(String host, int port) {
		this.host = host;
		this.port = port;
		buildListeners();
		transport = new UDPTransport(9000+getTaskEntity(), 1);
		
		IMCProtocol.announce(getEntityName(), getTaskEntity(), SYS_TYPE.CCU, transport);
		transport.addMessageListener(this);
	}
	
	public final void startControlling() {
		
		if (transport == null) {
			System.err.println("Cannot start controlling without connecting first.");
			return;
		}
		
		PlanControl startPlan = new PlanControl();
        startPlan.setType(PlanControl.TYPE.REQUEST);
        startPlan.setOp(OP.START);
        startPlan.setPlanId(getEntityName()+"_plan");
        FollowReference man = new FollowReference();
        man.setControlEnt((short)0xFF);
        man.setControlSrc(transport.getImcId());
        man.setAltitudeInterval(1);
        man.setTimeout(5);
        man.setLoiterRadius(0);
        
        PlanSpecification spec = new PlanSpecification();
        spec.setPlanId(getEntityName()+"_plan");
        spec.setStartManId("Follow_"+getEntityName());
        PlanManeuver pm = new PlanManeuver();
        pm.setData(man);
        pm.setManeuverId("Follow_"+getEntityName());
        spec.setManeuvers(Arrays.asList(pm));
        startPlan.setArg(spec);
        startPlan.setRequestId(0);
        
        transport.sendMessage(host, port, startPlan);
        
        controlTimer = new Timer("Periodic Guidance Control", true);
        controlTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				IMCMessage guidance = guide();
				if (guidance != null)
					transport.sendMessage(host, port, guidance);				
			}
		}, 0, 2500);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
        	@Override
        	public void run() {
        		stopControlling();
        	}
        });
        
	}
	
	public void stopControlling() {
		
		Reference ref = new Reference(Reference.FLAG_MANDONE, null, null, 0, 0, 0);
		send(ref);
		controlTimer.cancel();
		controlTimer = null;
	}
	
	public final void send(IMCMessage message) {
		message.setSrcEnt(getTaskEntity());
		transport.sendMessage(host, port, message);
	}
	
	@Override
	public final void onMessage(MessageInfo info, IMCMessage msg) {
		for (Class<?> c = msg.getClass(); c != Object.class; c = c.getSuperclass()) {
			if (listenerMethods.containsKey(c)) {
				try {
					if (debug)
						System.out.println("consume("+c.getSimpleName()+") called");
					listenerMethods.get(c).invoke(this, msg);
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	public final void disconnect() {
		heartbeater.interrupt();
		transport.stop();
	}	
	
	public static void main(String[] args) throws Exception {
		Abort abt = new Abort();
		UDPTransport trans = new UDPTransport();
		trans.sendMessage("127.0.0.1", 6002, abt);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		abt.serialize(new IMCOutputStream(baos));
		byte[] buf = baos.toByteArray();
		for (int i = 0; i < buf.length; i++) {
			System.out.printf("%02X ", buf[i]);			
		}
		System.out.println("\n"+buf.length);
	}
}
