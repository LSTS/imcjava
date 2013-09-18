package pt.up.fe.dceg.imc.controllers;

import pt.up.fe.dceg.neptus.imc.DesiredSpeed;
import pt.up.fe.dceg.neptus.imc.DesiredSpeed.SPEED_UNITS;
import pt.up.fe.dceg.neptus.imc.DesiredZ;
import pt.up.fe.dceg.neptus.imc.DesiredZ.Z_UNITS;
import pt.up.fe.dceg.neptus.imc.EstimatedState;
import pt.up.fe.dceg.neptus.imc.Reference;
import pt.up.fe.dceg.neptus.util.WGS84Utilities;

public class TwirlSurvey extends ControllerAgent {

	private EstimatedState estimatedState = null;
	private DesiredSpeed speed = new DesiredSpeed(1.25, SPEED_UNITS.METERS_PS);
	private DesiredZ z = new DesiredZ(0, Z_UNITS.DEPTH);
	private double latDegrees, lonDegrees, minZ, maxZ;
	private boolean descend = true;
	private double[] drifterVelocity = {0.1, -0.16};
	private long start = System.currentTimeMillis();
	private double radius;
	
	public TwirlSurvey(double latDegrees, double lonDegrees, double minz, double maxz, double speed, double radius) {
		this.latDegrees = latDegrees;
		this.lonDegrees = lonDegrees;
		this.minZ = minz;
		this.maxZ = maxz;
		this.radius = radius;
		this.speed.setValue(speed);
		this.z.setValue(maxz);
	}
	
	@Override
	public int getTaskEntity() {
		return 1;
	}

	@Override
	public Reference guide() {

		if (estimatedState == null)
			return null;
		
		double ellapsedTime = (System.currentTimeMillis() - start)/1000.0;		
		
		double[] dest = WGS84Utilities.WGS84displace(latDegrees, lonDegrees, 0, drifterVelocity[0]*ellapsedTime, drifterVelocity[1] * ellapsedTime, 0);
		
		Reference ref = new Reference();
		
		boolean nearBottom = estimatedState.getAlt() != -1 && estimatedState.getAlt() < 3;
		
		if (descend && (estimatedState.getDepth() > maxZ || nearBottom)) {
			descend = false;
			z.setValue(minZ);
		}
		else if (!descend && estimatedState.getDepth() < minZ) {
			descend = true;
			z.setValue(maxZ);
		}
		
		ref.setLat(Math.toRadians(dest[0]));
		ref.setLon(Math.toRadians(dest[1]));
		ref.setZ(z);
		ref.setSpeed(speed);		
		ref.setRadius(radius);

		return ref;
	}

	public void consume(EstimatedState state) {
		estimatedState = state;
	}

	public static void main(String[] args) throws Exception {
		TwirlSurvey survey = new TwirlSurvey(41.180606, -8.706406, 2, 5, 1.3, 150);
		survey.connect("127.0.0.1", 6002);
		Thread.sleep(5000);
		survey.startControlling();		
	}
}
