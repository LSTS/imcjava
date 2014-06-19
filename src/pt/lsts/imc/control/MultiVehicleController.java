package pt.lsts.imc.control;

import pt.lsts.imc.Announce.SYS_TYPE;

public class MultiVehicleController {

	public static void main(String[] args) throws Exception {
		
		
		String[] vehicles = ControlLink.listVehicles(SYS_TYPE.UUV);
		
		while (vehicles.length < 2) {
			System.out.println("Waiting for auvs...");
			Thread.sleep(1000);
			vehicles = ControlLink.listVehicles(SYS_TYPE.UUV);
		}
		
		ControlLink l1 = ControlLink.acquire(vehicles[0], 1000);
		ControlLink l2 = ControlLink.acquire(vehicles[1], 1000);
		
		l1.guide(41, -8, 2, 1.3);
		l2.stop();
		//l2.guide(41, -8, 2, 1.3);
		
	}
	
}
