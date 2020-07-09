/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.process;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TimeZone;

import javax.swing.UIManager;

import pt.lsts.imc.Announce;
import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.SetEntityParameters;
import pt.lsts.imc.VehicleMedium;
import pt.lsts.imc.VehicleMedium.MEDIUM;
import pt.lsts.imc.def.SystemType;
import pt.lsts.imc.lsf.batch.LsfBatch;
import pt.lsts.imc.net.Consume;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class DistanceTravelled {

	private LinkedHashMap<Integer, Announce> announces = new LinkedHashMap<Integer, Announce>();
	private LinkedHashMap<Integer, EstimatedState> states = new LinkedHashMap<Integer, EstimatedState>();
	private LinkedHashMap<Integer, VehicleMedium> mediums = new LinkedHashMap<Integer, VehicleMedium>();
	private LinkedHashMap<Integer, Double> distanceTravelled = new LinkedHashMap<Integer, Double>();
	private LinkedHashMap<Integer, HashSet<String>> activePayloads = new LinkedHashMap<Integer, HashSet<String>>();
	private LinkedHashMap<String, Double> payloadsDistance = new LinkedHashMap<String, Double>();
	
	private ArrayList<String> payloadsOfInterest = new ArrayList<String>();
	{
		payloadsOfInterest.add("Camera");
		payloadsOfInterest.add("Multibeam");
		payloadsOfInterest.add("Sidescan");		
	}
	
	boolean hasPayload(int id, String payload) {
		return activePayloads.getOrDefault(id, new HashSet<String>()).contains(payload);
	}
	
	String getPayloadString(int id) {
		String ret = "";
		HashSet<String> payloads = activePayloads.getOrDefault(id, new HashSet<String>());
		
		for (String s : payloadsOfInterest)
			if (payloads.contains(s))
				ret += s.substring(0, 1);
		
		return ret;		
	}
	
	String vehicleName(int id) {
		Announce ann = announces.getOrDefault(id, null);
		return (ann != null? ann.getSysName() : ""+id);
	}
	
	@Consume
	void on(Announce msg) {
		if (msg.getSysType() == SystemType.UAV) {// && isInWater(msg.getSrc())) {
			announces.put(msg.getSrc(), msg);			
		}
	}

	@Consume
	void on(SetEntityParameters msg) {
		if (!payloadsOfInterest.contains(msg.getName()))
			return;
		
		for (EntityParameter p : msg.getParams()) {
			if (p.getName().equals("Active")) {
				if (p.getValue().equals("true"))
					activatePayload(msg.getSrc(), msg.getName());
				else
					deactivatePayload(msg.getSrc(), msg.getName());
			}
		}
	}

	@Consume
	void on(VehicleMedium msg) {
		mediums.put(msg.getSrc(), msg);
	}

	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
	{
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	@Consume
	void on(EstimatedState msg) {
		int id = msg.getSrc();

		if (!isUAV(id))// || !isInWater(id))
			return;

		states.putIfAbsent(id, msg);

		EstimatedState lastState = states.getOrDefault(id, null);

		if (msg.getTimestamp() - lastState.getTimestamp() < 1)
			return;

		double[] prevLoc = WGS84Utilities.toLatLonDepth(lastState);
		double[] curLoc = WGS84Utilities.toLatLonDepth(msg);
		double distance = WGS84Utilities.distance(prevLoc[0], prevLoc[1], curLoc[0], curLoc[1]);

		addDistance(id, distance);
		
		addPosition(sdf.format(msg.getDate()), vehicleName(id), curLoc[0], curLoc[1], msg.getDepth(), msg.getAlt(), getPayloadString(id));
		states.put(id, msg);
	}

	boolean isUAV(int id) {
		return announces.containsKey(id);
	}

	boolean isInWater(int id) {
		VehicleMedium vState = mediums.getOrDefault(id, null);
		
		if (vState == null)
			return false;
		return vState.getMedium() == MEDIUM.WATER || vState.getMedium() == MEDIUM.UNDERWATER;
	}
	
	boolean isAtSurface(int id) {
		VehicleMedium vState = mediums.getOrDefault(id, null);

		if (vState == null)
			return false;
		return vState.getMedium() == MEDIUM.WATER;
	}
	
	boolean isUnderwater(int id) {
		VehicleMedium vState = mediums.getOrDefault(id, null);

		if (vState == null)
			return false;
		return vState.getMedium() == MEDIUM.UNDERWATER;
	}
	

	void activatePayload(int id, String payload) {
		HashSet<String> payloads = activePayloads.getOrDefault(id, new HashSet<String>());
		payloads.add(payload);
		activePayloads.put(id, payloads);
		
	}

	void deactivatePayload(int id, String payload) {
		HashSet<String> payloads = activePayloads.getOrDefault(id, new HashSet<String>());
		payloads.remove(payload);
		activePayloads.put(id, payloads);		
	}

	void addPosition(String timestamp, String vehicle, double lat, double lon, double depth, double alt, String payload) {
		//System.out.println(timestamp+", "+vehicle+", "+lat+", "+lon+", "+depth+", "+alt+", "+payload);
	}
	
	void addDistance(int id, double distance) {
		Double curDist = distanceTravelled.getOrDefault(id, 0d);

		if (distance > 5)
			return;

		curDist += distance;
		distanceTravelled.put(id, curDist);
		
		for (String s : payloadsOfInterest) {
			if (hasPayload(id, s)) {
				double pDist = payloadsDistance.getOrDefault(s, 0d);
				pDist += distance;
				payloadsDistance.put(s, pDist);
			}
		}
		
		if (isAtSurface(id)) {
			double wDist = payloadsDistance.getOrDefault("surface", 0d);
			wDist += distance;
			payloadsDistance.put("surface", wDist);
		}
		else if (isUnderwater(id)) {
			double uDist = payloadsDistance.getOrDefault("underwater", 0d);
			uDist += distance;
			payloadsDistance.put("underwater", uDist);
		}				
	}
	
	void summary() {
		System.out.println(distanceTravelled);
		System.out.println(payloadsDistance);
	}

	public static void main(String[] args) {
		String dir;
		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			if(args.length < 1)
				return;
			dir = args[0];
			System.out.println(dir);
			LsfBatch batch = LsfBatch.selectDir(dir);
			DistanceTravelled processor = new DistanceTravelled();
			batch.process(processor);
			processor.summary();
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
