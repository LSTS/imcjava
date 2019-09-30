/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.adapter;

import java.util.ArrayList;
import java.util.Collection;

import pt.lsts.imc.Abort;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanControlState.LAST_OUTCOME;
import pt.lsts.imc.PlanControlState.STATE;
import pt.lsts.imc.PlanDB;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.VehicleState.OP_MODE;
import pt.lsts.imc.def.SystemType;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.PojoConfig;
import pt.lsts.imc.state.Parameter;
import pt.lsts.neptus.messages.listener.Periodic;
import pt.lsts.util.PlanUtilities;
import pt.lsts.util.WGS84Utilities;

/**
 * This class is a simple implementation of an IMC vehicle adapter
 * @author zp
 */
public class VehicleAdapter extends ImcAdapter {
	
	@Parameter
	public double lat = 41;
	
	@Parameter
	public double lon = -8;
	
	@Parameter
	public double heading = 0; 
	
	
	protected double latRads = 0, lonRads = 0, height = 0, depth = 0;
	protected double rollRads = 0, pitchRads = 0, yawRads = 0, speed = 0;
	protected double startLat = 0, startLon = 0, startTime = 0;
	protected PlanDbManager planDbManager = new PlanDbManager();
	protected PlanControlState planControl = new PlanControlState();

	// loaded plan;
	protected ArrayList<double[]> waypoints = new ArrayList<>();
	protected int waypoint = -1;
	protected Double targetLat = null, targetLon = null;
	
	/**
	 * Class constructor. Announces sent to the network will use given settings.
	 * @param name The name of the system
	 * @param imcid The IMC ID of the system
	 */
	public VehicleAdapter(String name, int imcid) {
		super(name, imcid, 7010, SystemType.UUV);
		planControl.setState(STATE.READY);
	}
	
	public void init() {
		setPosition(lat, lon, 0, 0);
		setEuler(0, 0, heading);
		setSpeed(1.25);
	}
	
	/**
	 * Change the vehicle's position
	 * @param latDegs Latitude of the system, in degrees
	 * @param lonDegs Longitude of the system, in degrees
	 * @param height Height above WGS84 ellipsoid
	 * @param depth Depth below water
	 */
	public void setPosition(double latDegs, double lonDegs, double height, double depth) {
		this.startLat = this.latRads = Math.toRadians(latDegs);
		this.startLon = this.lonRads = Math.toRadians(lonDegs);
		this.height = height;
		this.depth = depth;
		this.startTime = System.currentTimeMillis()/1000.0;
	}
	
	/**
	 * Change the vehicle's attitude
	 * @param rollDegs Roll angle in degrees
	 * @param pitchDegs Pitch angle in degrees
	 * @param yawDegs Yaw angle in degrees
	 */
	public void setEuler(double rollDegs, double pitchDegs, double yawDegs) {
		this.rollRads = Math.toRadians(rollDegs);
		this.pitchRads = Math.toRadians(pitchDegs);
		this.yawRads = Math.toRadians(yawDegs);
	}
	
	/**
	 * Change the vehicle's speed. This will also be integrated to update the vehicle position
	 * @param speed The vehicle speed, in m/s.
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	/**
	 * Method called whenever an Abort message is sent (from Neptus). 
	 * It will interrupt ongoing plan if applicable. 
	 * @param abort Abort message sent by operator
	 */
	@Consume
	protected void on(Abort abort) {
		System.err.println("Received abort message!");
		planControl.setPlanId("");
		planControl.setState(STATE.READY);
	}
	
	/**
	 * Method called whenever a PlanDB request is sent by the operator
	 * @param request The request to be handled by the vehicle
	 */
	@Consume
	protected void on(PlanDB request) {
		IMCMessage response = planDbManager.query(request);
		dispatch(response);		
	}
	
	/**
	 * Handler for PlanControl requests
	 * @param command The command sent by the operator
	 */
	@Consume
	protected void on(PlanControl command) {
		PlanControl reply = new PlanControl();
		try {
			reply.copyFrom(command);
		}
		catch (Exception e) { }
		
		planDbManager.setPlanControl(command);
		if (command.getType() == PlanControl.TYPE.REQUEST && command.getOp() == PlanControl.OP.START) {
			PlanSpecification spec = planDbManager.getSpec(command.getPlanId());
			if (spec == null) {
				reply.setType(PlanControl.TYPE.FAILURE);
				err("Unknown plan id: "+command.getPlanId());
			}
			else {
				Collection<double[]> locs = PlanUtilities.computeLocations(spec);
				
				System.out.println("Plan waypoints: "); 
				for (double[] loc : locs) {
					
					System.out.println("   * "+loc[0]+", "+loc[1]);
				}
				
				planControl.setPlanId(command.getPlanId());
				planControl.setState(STATE.EXECUTING);
				reply.setType(PlanControl.TYPE.SUCCESS);
				
				synchronized (waypoints) {
					waypoints.clear();
					waypoints.addAll(locs);
					if (!waypoints.isEmpty())
						waypoint = 0;
					else
						waypoint = -1;
				}
			}
		}
		else if (command.getType() == PlanControl.TYPE.REQUEST && command.getOp() == PlanControl.OP.STOP) {
			planControl.setPlanId("");
			planControl.setState(STATE.READY);
			reply.setType(PlanControl.TYPE.SUCCESS);
		}		
	}	
		
	/**
	 * Every 500ms, the current state is published to the IMC network
	 */
	@Periodic(500)
	protected void sendEstimatedState() {
		EstimatedState state = new EstimatedState();
		state.setLat(latRads);
		state.setLon(lonRads);
		state.setPhi(rollRads);
		state.setTheta(pitchRads);
		state.setPsi(yawRads);
		state.setDepth(depth);
		state.setHeight(height);
		state.setU(speed);
		dispatch(state);
	}
	
	/**
	 * Every 1s, the VehicleState is published to the IMC network
	 */
	@Periodic(1000)
	protected void sendVehicleState() {
		VehicleState state = new VehicleState();

		if (planControl == null)
			return;
		
		if (planControl.getState() == STATE.READY)
			state.setOpMode(OP_MODE.SERVICE);
		else if (planControl.getState() == STATE.EXECUTING)
			state.setOpMode(OP_MODE.MANEUVER);
		
		dispatch(state);
	}
	
	/**
	 * Every 1s, the PlanControlState is sent to the IMC network
	 */
	@Periodic(1000)
	protected void sendPlanControlState() {
		dispatch(planControl);
	}
	
	/**
	 * Integrate vehicle's speed at 10Hz
	 */
	@Periodic(100)
	protected void updatePosition() {
	
		if (startTime == 0 || speed == 0)
			return;
		
		double ellapsedTime = System.currentTimeMillis()/1000.0 - startTime;
		double northing = Math.cos(yawRads) * (ellapsedTime * speed);
		double easting = Math.sin(yawRads) * (ellapsedTime * speed);				
		double pos[] = WGS84Utilities.WGS84displace(Math.toDegrees(latRads), Math.toDegrees(lonRads), 0, northing,
				easting, 0);
		
		// executing a plan?
		if (waypoint >= 0) {
			double[] target = waypoints.get(waypoint);
			double[] offsets = WGS84Utilities.WGS84displacement(pos[0], pos[1], 0, target[0], target[1], 0);
			
			if (WGS84Utilities.distance(pos[0], pos[1], target[0], target[1]) <= speed) {
				inf("Arrived at waypoint #"+waypoint);
				advanceWaypoint();
			}
			yawRads = Math.atan2(offsets[1], offsets[0]);
			 
		}
		
		setPosition(pos[0], pos[1], height, depth);
	}
	
	private void advanceWaypoint() {
		if (waypoint < waypoints.size()-1)
			waypoint++;
		else {
			waypoint = -1;
			inf("Plan completed");
			planControl.setPlanId("");
			planControl.setState(STATE.READY);
			planControl.setLastOutcome(LAST_OUTCOME.SUCCESS);
			
		}
	}

	/**
	 * This example starts an existing vehicle and updates its position.
	 */
	public static void main(String[] args) throws Exception {
		VehicleAdapter adapter = new VehicleAdapter("lauv-simulator-1", 0x00D1);
		PojoConfig.cliParams(adapter, args);
		adapter.init();
	}
}
