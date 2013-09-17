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
	private double[] drifterVelocity = {0.25, 0.25};
	private long start = System.currentTimeMillis();
	int path_index = -1;
	double[][] path;
	double[] destination = null;
	
	public TwirlSurvey(double latDegrees, double lonDegrees, double minz, double maxz, double speed, double width) {
		this.latDegrees = latDegrees;
		this.lonDegrees = lonDegrees;
		this.minZ = minz;
		this.maxZ = maxz;
		this.speed.setValue(speed);
		this.z.setValue(maxz);
		
		path = new double[][] {
				{width/2, -width/2},
				{width/2, width/2},
				{-width/2, width/2},
				{-width/2, -width/2},
				{width/2, -width/2}
		};
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
		
		double[] mypos = WGS84Utilities.WGS84displace(
				Math.toDegrees(estimatedState.getLat()),
				Math.toDegrees(estimatedState.getLon()), 
				estimatedState.getDepth(), estimatedState.getX(), estimatedState.getY(), 0);
		
		if (destination == null || WGS84Utilities.distance(destination[0], destination[1], mypos[0], mypos[1]) < 10) {
			path_index++;
			destination = WGS84Utilities.WGS84displace(latDegrees, lonDegrees, 0, drifterVelocity[0]*ellapsedTime + path[path_index][0], drifterVelocity[1] * ellapsedTime + path[path_index][1], 0);	
		}
		
		
		
		Reference ref = new Reference();
		
		if (estimatedState.getDepth() > maxZ && descend) {
			descend = false;
			z.setValue(minZ);
		}
		
		if (estimatedState.getDepth() < minZ && !descend) {
			descend = true;
			z.setValue(maxZ);
		}
		
		ref.setLat(Math.toRadians(destination[0]));
		ref.setLon(Math.toRadians(destination[1]));
		ref.setZ(z);
		ref.setSpeed(speed);		
		ref.setRadius(0);

		return ref;
	}

	public void consume(EstimatedState state) {
		estimatedState = state;
	}

	public static void main(String[] args) throws Exception {
		TwirlSurvey survey = new TwirlSurvey(41.183713, -8.703822, 2, 10, 1.25, 120);
		survey.connect("127.0.0.1", 6002);
		Thread.sleep(5000);
		survey.startControlling();		
	}
}
