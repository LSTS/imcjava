package pt.lsts.imc.net;

import java.util.Random;

import pt.lsts.imc.Abort;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.Goto;
import pt.lsts.imc.Goto.SPEED_UNITS;
import pt.lsts.imc.Goto.Z_UNITS;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanControlState.STATE;
import pt.lsts.imc.state.ImcSystemState;
import pt.lsts.util.WGS84Utilities;

public class IMCVehicle {

	private static IMCProtocol proto = null;
	private String vehicle;

	public IMCVehicle(String vehicle) {
		this.vehicle = vehicle;
		if (proto == null)
			proto = new IMCProtocol();
	}

	public ImcSystemState state() {
		return proto.state(vehicle);
	}

	public void abort() {
		proto.sendMessage(vehicle, new Abort());
	}

	public void send(IMCMessage m) {
		proto.sendMessage(vehicle, m);
	}

	int req_id = new Random().nextInt(30000);
	public void move(double latDegs, double lonDegs, double depth, double speed) {
		PlanControl pc = new PlanControl()
				.setOp(OP.START)
				.setType(TYPE.REQUEST)
				.setPlanId(
						String.format("go(lat=%f,lon=%f,depth=%f,speed=%f)",
								latDegs, lonDegs, depth, speed)).setFlags(0).setRequestId(++req_id);
		pc.setArg(new Goto().setLat(Math.toRadians(latDegs))
				.setLon(Math.toRadians(lonDegs)).setZ(depth)
				.setZUnits(Z_UNITS.DEPTH).setSpeed(speed)
				.setSpeedUnits(SPEED_UNITS.METERS_PS));
		send(pc);
	}
	
	public boolean isConnected() {
		if (state().last(EstimatedState.class) == null)
			return false;
		
		return (System.currentTimeMillis() - state().last(EstimatedState.class).getTimestampMillis()) < 30000;
	}

	public boolean isIdle() {
		PlanControlState pcs = state().poll(PlanControlState.class, 5000);
		if (pcs == null)
			return false;
		if (pcs.getState() == STATE.READY)
			return true;
		else
			return false;
	}

	public double[] position(long timeoutMillis) {
		EstimatedState state = (EstimatedState) recv("EstimatedState",
				timeoutMillis);
		if (state == null)
			return null;
		return WGS84Utilities.toLatLonDepth(state);
	}

	public synchronized IMCMessage recv(String abbrev, long timeoutMillis) {
		return state().poll(abbrev, timeoutMillis);
	}
	
	public static void main(String[] args) throws Exception {
		IMCVehicle v = new IMCVehicle("lauv-seacon-1");
		while (!v.isConnected())			
			Thread.sleep(100);
		Thread.sleep(10000);
		v.move(41, -8, 1, 1.2);
		Thread.sleep(10000);
		v.move(41, -8.001, 4, 1.2);
	}
}
