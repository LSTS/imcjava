/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of IMC, LSTS, IMCJava nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL LABORATORIO DE SISTEMAS E TECNOLOGIA SUBAQUATICA
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pt.lsts.imc.ripples;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.neptus.messages.listener.PeriodicCallbacks;
import pt.lsts.util.PlanUtilities;
import pt.lsts.util.WGS84Utilities;

import com.firebase.client.DataSnapshot;


public class RipplesMain {
	
	private IMCProtocol proto;
	private LinkedHashMap<String, PlanSpecification> plans = new LinkedHashMap<String, PlanSpecification>();
	private static int count = 0;
	
	private LinkedHashMap<String, Long> planRequests = new LinkedHashMap<String, Long>();
	
	
	public RipplesMain(int port) {
		System.out.println("[Ripples] Binding to port "+port+"...");
		proto = new IMCProtocol("RipplesUpdater", port);
		proto.register(this);
		proto.setAutoConnect(".*");
		PeriodicCallbacks.register(this);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("[Ripples] Stopping IMC protocol.");
				proto.stop();
				PeriodicCallbacks.stopAll();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Consume
	public void on(Announce ann) {
		
		double lat = Math.toDegrees(ann.getLat());
		double lon = Math.toDegrees(ann.getLon());
		if (lat == 0 && lon == 0.0)
			return;
		
		DataSnapshot posOnline = FirebaseDB.get("assets/"+ann.getSysName()+"/position");
		Map<Object, Object> data = new LinkedHashMap<Object, Object>();
		
		if (posOnline != null && posOnline.getValue() != null)
			data.putAll((Map<Object,Object>)posOnline.getValue());
		data.put("latitude", lat);
		data.put("longitude", lon);
		FirebaseDB.setValue("assets/"+ann.getSysName()+"/position", data);
		FirebaseDB.setValue("assets/"+ann.getSourceName()+"/updated_at", ann.getTimestampMillis());
		FirebaseDB.setValue("assets/"+ann.getSourceName()+"/type", ann.getSysType().toString());		
	}

	@Consume
	public void on(PlanDB m) {
		if (m.getType() == PlanDB.TYPE.SUCCESS && m.getOp() == PlanDB.OP.GET) {
			plans.put(m.getPlanId(), (PlanSpecification)m.getArg());
			System.out.println("Received plan "+m.getPlanId()+" from "+m.getSourceName());
			Collection<double[]> locs = PlanUtilities.computeLocations((PlanSpecification)m.getArg());
			FirebaseDB.setValue("assets/"+m.getSourceName()+"/plan/path", locs);
		}
	}
	
	@Consume
	public void on(PlanControlState pcs) {
		
		if (pcs.getState() == PlanControlState.STATE.EXECUTING) {
			if (!pcs.getPlanId().isEmpty()) {
				FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan/id", pcs.getPlanId());
				if (!plans.containsKey(pcs.getPlanId())) {
					
					if (planRequests.containsKey(pcs.getSourceName())) {
						 long timeRequested = planRequests.get(pcs.getSourceName());
						 if (System.currentTimeMillis() - timeRequested < 5000)
							 return;
					}
					planRequests.put(pcs.getSourceName(), System.currentTimeMillis());
					
					PlanDB req = new PlanDB();
					req.setType(PlanDB.TYPE.REQUEST);
					req.setOp(PlanDB.OP.GET);
					req.setPlanId(pcs.getPlanId());
					req.setRequestId(count++);
					System.out.println("Requesting plan "+pcs.getPlanId()+" to "+pcs.getSourceName());
					proto.sendMessage(pcs.getSourceName(), req);
				}
			}
			if (pcs.getPlanProgress() > 0)
				FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan/progress", String.format("%.1f", pcs.getPlanProgress()));
			else
				FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan/progress", null);
		}
		else {
			FirebaseDB.setValue("assets/"+pcs.getSourceName()+"/plan", null);
			plans.remove(pcs.getPlanId());
		}
	}
	
	@Consume
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
