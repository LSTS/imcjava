package pt.up.fe.dceg.imc.controllers;

import pt.up.fe.dceg.neptus.imc.DesiredSpeed;
import pt.up.fe.dceg.neptus.imc.DesiredSpeed.SPEED_UNITS;
import pt.up.fe.dceg.neptus.imc.DesiredZ;
import pt.up.fe.dceg.neptus.imc.DesiredZ.Z_UNITS;
import pt.up.fe.dceg.neptus.imc.EstimatedState;
import pt.up.fe.dceg.neptus.imc.PathControlState;
import pt.up.fe.dceg.neptus.imc.Reference;
import pt.up.fe.dceg.neptus.util.WGS84Utilities;

public class TwirlSurvey extends ControllerAgent {

	private EstimatedState estimatedState = null;
	private DesiredSpeed desiredSpeed = new DesiredSpeed(1.25, SPEED_UNITS.METERS_PS);
	private DesiredZ z = new DesiredZ(0, Z_UNITS.DEPTH);
	private PathControlState pathControlState;
	private double speed;
	private boolean descend = true;
	private double[] drifterVelocity = {0.2, 0.2};
	private long start = 0;
	private double loiteringTime = 0;
	private long lastLoiterTime = -1;
	
	@Parameter
	public double latDegrees;
	
	@Parameter
	public double lonDegrees;
	
	@Parameter
	public double radius;
	
	@Parameter
	public double minZ;
	
	@Parameter
	public double maxZ;
	
	@Parameter
	public double vX;
	
	@Parameter
	public double vY;
	
	public TwirlSurvey(double latDegrees, double lonDegrees, double minz, double maxz, double speed, double radius) {
		this.latDegrees = latDegrees;
		this.lonDegrees = lonDegrees;
		this.minZ = minz;
		this.maxZ = maxz;
		this.radius = radius;
		this.speed = speed;
		this.desiredSpeed.setValue(speed);
		this.z.setValue(maxz);
	}
	
	public void initController() {
		this.desiredSpeed.setValue(speed);
		this.z.setValue(maxZ);		
	}
	
	@Override
	public int getTaskEntity() {
		return 1;
	}
	
	@Override
	public Reference guide() {

		if (estimatedState == null)
			return null;		
		
		double ellapsedTime = loiteringTime;//(System.currentTimeMillis() - start)/1000.0;
		
		if (pathControlState != null) {
			boolean loitering = (pathControlState.getFlags() & PathControlState.FL_LOITERING) != 0;
			
			if (System.currentTimeMillis() - lastLoiterTime < 10000 && loitering)
				loiteringTime += (System.currentTimeMillis() - lastLoiterTime) / 1000.0;
			
			if (loitering)
				lastLoiterTime = System.currentTimeMillis();
		}
		
		double[] dest = WGS84Utilities.WGS84displace(latDegrees, lonDegrees, 0, drifterVelocity[0]*ellapsedTime, drifterVelocity[1] * ellapsedTime, 0);
		
		if (start == 0 && pathControlState != null && ((pathControlState.getFlags() & PathControlState.FL_LOITERING) != 0))
			start = System.currentTimeMillis();
		
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
		ref.setSpeed(desiredSpeed);		
		ref.setRadius(radius);

		return ref;
	}

	public void consume(EstimatedState state) {
		estimatedState = state;
	}
	
	public void consume(PathControlState pcs) {
		this.pathControlState = pcs;
	}

	public static void main(String[] args) throws Exception {
		if (args.length > 0)
			System.out.println(args[0]);
		TwirlSurvey survey = new TwirlSurvey(41.180606, -8.706406, 2, 5, 1.25, 50);
		survey.connect("10.0.2.30", 6002);
		Thread.sleep(5000);
		survey.startControlling();				
	}
}
