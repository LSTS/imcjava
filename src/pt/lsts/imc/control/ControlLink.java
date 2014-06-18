package pt.lsts.imc.control;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.FollowReference;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.imc.state.ImcSysState;

public class ControlLink {

	private static IMCProtocol proto = null;
	
	private String vehicle;
	
	public static ControlLink acquire(String vehicle, long timeoutMillis) throws Exception {
		ImcSysState state = getImc().state(vehicle);
		
		long startTime = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - startTime < timeoutMillis) {
			state = getImc().state(vehicle);
			if (state != null && state.lastVehicleState() != null)
				break;
			Thread.sleep(100);			
		}
		if (System.currentTimeMillis() - startTime >= timeoutMillis )
			throw new Exception ("Vehicle "+vehicle+" is not currently connected");

		if (state.lastVehicleState() == null || state.lastVehicleState().getOpMode() != VehicleState.OP_MODE.SERVICE)
			throw new Exception ("Vehicle "+vehicle+" cannot be associated with this controller");
		
		PlanControl pc = new PlanControl();
		pc.setPlanId("control_link");
		pc.setOp(OP.START);
		pc.setType(TYPE.REQUEST);
		pc.setRequestId(1000);
		FollowReference man = new FollowReference();
		man.setControlSrc(getImc().getLocalId());
		man.setControlEnt((short)255);
		man.setLoiterRadius(10);
		pc.setArg(man);
		
		if (!proto.sendMessage(vehicle, pc)) {
			throw new Exception ("Could not send plan start command to "+vehicle);
		}
		
		ControlLink link = new ControlLink(vehicle);
		return link;
	}
	
	public ControlLink(String vehicle) {
		getImc().register(this);
	}
	
	public void guide(double lat, double lon, double z, double speed) {
		
	}
	
	public EstimatedState getState() {
		return getImc().state(vehicle).lastEstimatedState();
	}
	
	private static IMCProtocol getImc() {
		if (proto == null)
			proto = new IMCProtocol();
		return proto;
	}
	
	
	public static void main(String[] args) throws Exception {
		ControlLink.acquire("lauv-xplore-1", 20000);
	}
}
