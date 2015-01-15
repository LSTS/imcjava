package pt.lsts.imc.junit;

import org.junit.Test;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.Maneuver;
import pt.lsts.util.PlanUtilities;

public class PlanUtilitiesTest {

	@Test
	public void planToWaypointsTest() {
		for (String maneuver : IMCDefinition.getInstance().subtypesOf("Maneuver")) {
			Maneuver man = (Maneuver) IMCDefinition.getInstance().create(maneuver);
			IMCUtil.fillWithRandomData(man);
			PlanUtilities.computeWaypoints(man);			
		}
	}
}
