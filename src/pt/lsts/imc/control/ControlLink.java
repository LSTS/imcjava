package pt.lsts.imc.control;

import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.lsts.imc.Announce;
import pt.lsts.imc.DesiredSpeed;
import pt.lsts.imc.DesiredSpeed.SPEED_UNITS;
import pt.lsts.imc.AcousticOperation;
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
import pt.lsts.imc.state.ImcSysState;
import pt.lsts.util.WGS84Utilities;

public class ControlLink {

	private static IMCProtocol proto = null;
	private static ScheduledThreadPoolExecutor executor = null;
	private Reference lastReference = null;
	private String vehicle;
	private boolean acousticLink = false;
	
	public static String[] listVehicles(Announce.SYS_TYPE type) {
		
		Vector<String> validVehicles = new Vector<String>();
		String[] sys = getImc().lookupService("x");
		for (String s : getImc().systems()) {
			if (getImc().state(s).lastAnnounce().getSysType() == type) {
				if (getImc().state(s).lastVehicleState() != null && getImc().state(s).lastVehicleState().getOpMode() == VehicleState.OP_MODE.SERVICE)
					validVehicles.add(s);
			}
		}
		
		return validVehicles.toArray(new String[0]);
	}
	
	public static ControlLink acquire(String vehicle, long timeoutMillis)
			throws Exception {
		
		ImcSysState state = getImc().state(vehicle);

		long startTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - startTime < timeoutMillis) {
			state = getImc().state(vehicle);
			if (state != null && state.lastVehicleState() != null)
				break;
			Thread.sleep(100);
		}
		if (System.currentTimeMillis() - startTime >= timeoutMillis)
			throw new Exception("Vehicle " + vehicle
					+ " is not currently connected");

		if (state.lastVehicleState() == null
				|| state.lastVehicleState().getOpMode() != VehicleState.OP_MODE.SERVICE)
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
							ControlLink.this.vehicle).lastEstimatedState();
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
				//System.out.println(arrivedXY() + ", " + arrivedZ());
			}
		}, 5, 5, TimeUnit.SECONDS);
	}

	private void sendReference() {
		
		try {
			getImc().sendMessage(ControlLink.this.vehicle,
					lastReference);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void guide(double lat_degs, double lon_degs, double z_meters, double speed_mps) {
		DesiredZ desZ = null;
		if (!Double.isNaN(z_meters)) {
			if (z_meters != 0)
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
		proto = null;
	}

	public boolean arrived() {
		try {
			FollowRefState refState = getImc().state(vehicle)
					.lastFollowRefState();
			if (refState.getReference().isNull())
				return false;
			// FIXME check if followed reference is last sent one
			return (refState.getProximity() & FollowRefState.PROX_XY_NEAR) != 0
					&& (refState.getProximity() & FollowRefState.PROX_Z_NEAR) != 0;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean arrivedXY() {
		try {
			FollowRefState refState = getImc().state(vehicle)
					.lastFollowRefState();
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
			FollowRefState refState = getImc().state(vehicle)
					.lastFollowRefState();
			if (refState.getReference().isNull())
				return false;
			// FIXME check if followed reference is last sent one
			return (refState.getProximity() & FollowRefState.PROX_Z_NEAR) != 0;
		} catch (Exception e) {
			return false;
		}
	}

	public EstimatedState getState() {
		return getImc().state(vehicle).lastEstimatedState();
	}

	private static IMCProtocol getImc() {
		if (proto == null)
			proto = new IMCProtocol();
		return proto;
	}

	private static ScheduledThreadPoolExecutor getExec() {
		if (executor == null)
			executor = new ScheduledThreadPoolExecutor(1);
		return executor;
	}

	public static void main(String[] args) throws Exception {
		ControlLink xp1 = ControlLink.acquire("lauv-xplore-1", 20000);
		System.out.println(xp1);
	}
}
