/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2016, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.control;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.lsts.imc.Announce;
import pt.lsts.imc.DesiredSpeed;
import pt.lsts.imc.DesiredSpeed.SPEED_UNITS;
import pt.lsts.imc.DesiredZ;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.FollowRefState;
import pt.lsts.imc.FollowReference;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.Reference;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.imc.state.ImcSystemState;
import pt.lsts.util.WGS84Utilities;

public class ControlLink {

	private static IMCProtocol proto = null;
	private static ScheduledThreadPoolExecutor executor = null;
	private Reference lastReference = null;
	private String vehicle;

	// private boolean acousticLink = false;

	public static String[] listVehicles(Announce.SYS_TYPE type) {

		Vector<String> validVehicles = new Vector<String>();
		// String[] sys = getImc().lookupService("x");
		for (String s : getImc().systems()) {
			if (getImc().state(s).last(Announce.class).getSysType() == type) {
				if (getImc().state(s).last(VehicleState.class) != null
						&& getImc().state(s).last(VehicleState.class)
								.getOpMode() == VehicleState.OP_MODE.SERVICE)
					validVehicles.add(s);
			}
		}

		return validVehicles.toArray(new String[0]);
	}

	public static ControlLink acquire(String vehicle, long timeoutMillis)
			throws Exception {

		ImcSystemState state = getImc(vehicle).state(vehicle);

		long startTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - startTime < timeoutMillis) {
			state = getImc().state(vehicle);
			if (state != null && state.last(VehicleState.class) != null && state.last(EstimatedState.class) != null)
				break;
			Thread.sleep(100);
		}
		if (System.currentTimeMillis() - startTime >= timeoutMillis)
			throw new Exception("Vehicle " + vehicle
					+ " is not currently connected");

		if (state.last(VehicleState.class) == null
				|| state.last(VehicleState.class).getOpMode() != VehicleState.OP_MODE.SERVICE)
			throw new Exception("Vehicle " + vehicle
					+ " cannot be associated with this controller");

		PlanControl pc = new PlanControl();
		pc.setPlanId("control_link");
		pc.setOp(OP.START);
		pc.setType(TYPE.REQUEST);
		pc.setRequestId(1000);

		FollowReference man = new FollowReference();
		man.setControlSrc(getImc().getLocalId());
		man.setControlEnt((short) 255);
		man.setLoiterRadius(10);
		man.setTimeout(60);

		PlanManeuver pm = new PlanManeuver("1", man, null, null);
		PlanSpecification ps = new PlanSpecification();
		ps.setPlanId("control_link");
		ps.setStartManId("1");
		ps.setManeuvers(Arrays.asList(pm));
		pc.setArg(ps);

		if (!proto.sendMessage(vehicle, pc)) {
			throw new Exception("Could not send plan start command to "
					+ vehicle);
		}

		ControlLink link = new ControlLink(vehicle);

		return link;
	}

	public ControlLink(String vehicle) {
		this.vehicle = vehicle;

		getExec().scheduleAtFixedRate(new Runnable() {
			public void run() {

				if (lastReference == null) {
					EstimatedState lastState = getImc().state(
							ControlLink.this.vehicle)
							.last(EstimatedState.class);
					if (lastState != null && lastState.getLat() != 0) {
						double[] lld = WGS84Utilities.toLatLonDepth(lastState);
						Reference ref = new Reference();
						ref.setFlags(Reference.FLAG_LOCATION);

						ref.setLat(Math.toRadians(lld[0]));
						ref.setLon(Math.toRadians(lld[1]));
						ref.setZ(new DesiredZ(0, DesiredZ.Z_UNITS.DEPTH));
						lastReference = ref;
					} else {
						return;
					}
				}
				sendReference();
				// System.out.println(arrivedXY() + ", " + arrivedZ());
			}
		}, 5, 5, TimeUnit.SECONDS);
	}

	private void sendReference() {

		try {
			getImc().sendMessage(ControlLink.this.vehicle, lastReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void move(double north, double east, double z, double speed) {
		double curPosition[] = getPosition();
		double[] newPos = WGS84Utilities.WGS84displace(curPosition[0], curPosition[1], 0, north, east, 0);
		guide(newPos[0], newPos[1], z, speed);		
	}

	public void guide(double lat_degs, double lon_degs, double z_meters,
			double speed_mps) {
		DesiredZ desZ = null;
		if (!Double.isNaN(z_meters)) {
			if (z_meters >= 0)
				desZ = new DesiredZ((float) z_meters, DesiredZ.Z_UNITS.DEPTH);
			else
				desZ = new DesiredZ((float) -z_meters,
						DesiredZ.Z_UNITS.ALTITUDE);
		}

		DesiredSpeed desSpeed = null;
		if (!Double.isNaN(speed_mps)) {
			desSpeed = new DesiredSpeed(speed_mps, SPEED_UNITS.METERS_PS);
		}

		Reference ref = new Reference();
		short flags = Reference.FLAG_LOCATION;
		if (desZ != null) {
			flags |= Reference.FLAG_Z;
			ref.setZ(desZ);
		}
		if (desSpeed != null) {
			flags |= Reference.FLAG_SPEED;
			ref.setSpeed(desSpeed);
		}
		ref.setFlags(flags);

		ref.setLat(Math.toRadians(lat_degs));
		ref.setLon(Math.toRadians(lon_degs));
		lastReference = ref;
		sendReference();
	}

	public void stop() {
		Reference ref = new Reference();
		ref.setFlags(Reference.FLAG_MANDONE);
		lastReference = ref;
		sendReference();
	}

	public void shutdown() {
		proto.stop();
		executor.shutdown();
		executor = null;
		proto = null;
	}

	public boolean arrived() {
		try {
			FollowRefState refState = getImc().state(vehicle).last(
					FollowRefState.class);
			if (refState.getReference().isNull())
				return false;
			// FIXME check if followed reference is last sent one
			if (lastReference.getLat() != refState.getReference().getLat())
				return false;
			if (lastReference.getLon() != refState.getReference().getLon())
				return false;
			
			
			return (refState.getProximity() & FollowRefState.PROX_XY_NEAR) != 0
					&& (refState.getProximity() & FollowRefState.PROX_Z_NEAR) != 0;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean arrivedXY() {
		try {
			FollowRefState refState = getImc().state(vehicle).last(
					FollowRefState.class);
			if (refState.getReference().isNull())
				return false;
			// FIXME check if followed reference is last sent one
			return (refState.getProximity() & FollowRefState.PROX_XY_NEAR) != 0;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean arrivedZ() {
		try {
			FollowRefState refState = getImc().state(vehicle).last(
					FollowRefState.class);
			if (refState.getReference().isNull())
				return false;
			// FIXME check if followed reference is last sent one
			return (refState.getProximity() & FollowRefState.PROX_Z_NEAR) != 0;
		} catch (Exception e) {
			return false;
		}
	}

	public double[] getPosition() {
		return WGS84Utilities.toLatLonDepth(getImc().state(vehicle).last(
				EstimatedState.class));
	}

	public EstimatedState getState() {
		return getImc().state(vehicle).last(EstimatedState.class);
	}
	
	private static IMCProtocol getImc() {
		if (proto == null) {
			proto = new IMCProtocol();
			proto.setAutoConnect("");
		}
			return proto;
	}
	
	private static IMCProtocol getImc(String vehicle) {
		if (proto == null) {
			proto = new IMCProtocol();
			proto.setAutoConnect(vehicle);
		}
		else {
			proto.setAutoConnect(proto.getAutoConnect()+"|"+vehicle);
		}
		return proto;
	}

	private static ScheduledThreadPoolExecutor getExec() {
		if (executor == null)
			executor = new ScheduledThreadPoolExecutor(1);
		return executor;
	}

	public static void main(String[] args) throws Exception {
		String vehicle = "lauv-seacon-2";
		int timeoutMillis = 20000;
		ControlLink auv = ControlLink.acquire(vehicle, timeoutMillis);
		if (auv == null) {
			System.err.println("Not possible to acquire control of "+vehicle);
			System.exit(1);
		}
		
		while (true) {
			auv.move(0, -100, 2, 1.2);
			while(!auv.arrived())
				Thread.sleep(1000);
			auv.move(0, 100, 2, 1.2);
			while(!auv.arrived())
				Thread.sleep(1000);			
		}
		
	}
}
