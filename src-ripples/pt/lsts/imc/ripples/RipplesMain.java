package pt.lsts.imc.ripples;
import java.util.LinkedHashMap;

import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.util.WGS84Utilities;

import com.google.common.eventbus.Subscribe;


public class RipplesMain {
	
	private IMCProtocol proto;
	public RipplesMain(int port) {
		System.out.println("[Ripples] Binding to port "+port+"...");
		proto = new IMCProtocol("FireImc", port);
		proto.register(this);		
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("[Ripples] Stopping IMC protocol.");
				proto.stop();
			}
		});
	}
	
	@Subscribe
	public void on(Announce ann) {
		
		double lat = Math.toDegrees(ann.getLat());
		double lon = Math.toDegrees(ann.getLon());
		if (lat == 0 && lon == 0.0)
			return;
		
		FirebaseDB.setValue("assets/"+ann.getSysName()+"/position/latitude", lat);
		FirebaseDB.setValue("assets/"+ann.getSysName()+"/position/longitude", lon);
		FirebaseDB.setValue("assets/"+ann.getSourceName()+"/updated_at", ann.getTimestampMillis());
		
	}
	
	@Subscribe
	public void on(LogBookEntry entry) {
		if (entry.getType() != LogBookEntry.TYPE.INFO && entry.getType() != LogBookEntry.TYPE.DEBUG) {
			LinkedHashMap<String, Object> logEntry = new LinkedHashMap<String, Object>();
			logEntry.put("src", entry.getSourceName());
			logEntry.put("entry", entry.getText());
			logEntry.put("context", entry.getContext());
			FirebaseDB.setValue("logbook/"+entry.getTimestampMillis(), logEntry);
		}
	}

	@Subscribe
	public void on(PlanControlState pcs) {
		
		if (pcs.getState() == PlanControlState.STATE.EXECUTING) {
			if (!pcs.getPlanId().isEmpty()) {
				FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan/id", pcs.getPlanId());				
			}
			if (pcs.getPlanProgress() > 0)
				FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan/progress", String.format("%.1f", pcs.getPlanProgress()));
			else
				FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan/progress", null);
		}
		else {
			FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan", null);
		}
	}
	
	@Subscribe
	public void on(EstimatedState s) {
		double[] pos = WGS84Utilities.toLatLonDepth(s);
		LinkedHashMap<String, Object> position = new LinkedHashMap<String, Object>();
		position.put("latitude", pos[0]);
		position.put("longitude", pos[1]);
		if (pos[0] == 0 && pos[1] == 0)
			return;
		
		if (s.getAlt() != -1)
			position.put("altitude", s.getAlt());
		if (s.getDepth() != -1)
			position.put("depth", s.getDepth());
		position.put("heading", Math.toDegrees(s.getPsi()));
		position.put("speed", s.getU());
		FirebaseDB.setValue("assets/"+s.getSourceName()+"/position", position);
		FirebaseDB.setValue("assets/"+s.getSourceName()+"/updated_at", s.getTimestampMillis());
	}
	

	public static void main(String[] args) throws Exception {
		int port = 6456;
		
		if (args.length > 0)
			port = Integer.parseInt(args[0]);
		
		new RipplesMain(port);
		
		while(true) {
			Thread.sleep(1000);
		}
	}
}
