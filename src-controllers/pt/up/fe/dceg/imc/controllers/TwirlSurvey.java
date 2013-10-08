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
	
	public TwirlSurvey(double latDegrees, double lonDegrees, double minz, double maxz, double speed, double radius, double dvn, double dve) {
		this.latDegrees = latDegrees;
		this.lonDegrees = lonDegrees;
		this.minZ = minz;
		this.maxZ = maxz;
		this.radius = radius;
		this.speed = speed;
		this.desiredSpeed.setValue(speed);
		this.z.setValue(maxz);
		drifterVelocity[0] = dvn;
		drifterVelocity[1] = dve;
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
		
		
			
		if (args.length < 8)
		{
			System.err.println("Usage: ./twirl <lat_degs> <lon_degs> <radius> <min_z> <max_z> <speed_mps> <dv_n> <dv_e>");
			System.err.println("\t<lat_degs>: Latitude, in degrees of the center of the survey");
			System.err.println("\t<lon_degs>: Longitude, in degrees of the center of the survey");
			System.err.println("\t<radius>: Radius in meters of the circunference around the drifter");
			System.err.println("\t<min_z>: The minimum depth, in meters, to use in the yoyo pattern");
			System.err.println("\t<max_z>: The maximum depth, in meters, to use in the yoyo pattern");
			System.err.println("\t<speed_mps>: The desired vehicle speed");
			System.err.println("\t<dv_n>: Drifter x velocity component (towards North)");
			System.err.println("\t<dv_e>: Drifter y velocity component (towards East)");
			return;
		}
		
		double lat_deg = Double.parseDouble(args[0]);
		double lon_deg = Double.parseDouble(args[1]);
		double radius = Double.parseDouble(args[2]);
		double min_z = Double.parseDouble(args[3]);
		double max_z = Double.parseDouble(args[4]);
		double speed = Double.parseDouble(args[5]);
		double dv_n = Double.parseDouble(args[6]);
		double dv_e = Double.parseDouble(args[7]);
		
		if (args.length < 9 || !args[8].equals("-y")) {
			System.out.println("Parameters: ");
			System.out.println("\tlat_deg: "+lat_deg);
			System.out.println("\tlon_deg: "+lon_deg);
			System.out.println("\tradius: "+radius);
			System.out.println("\tmin_z: "+min_z);
			System.out.println("\tmax_z: "+max_z);
			System.out.println("\tspeed: "+speed);
			System.out.println("\tdv_n: "+dv_n);
			System.out.println("\tdv_e: "+dv_e);
			
			System.out.print("Proceed (Y/N)? ");
			int read = System.in.read();
			if (read != 89 && read != 121) {
				System.out.println("Cancelled by the user ("+read+")");
				return;
			}
		}
			
		TwirlSurvey survey = new TwirlSurvey(lat_deg, lon_deg, min_z, max_z, speed, radius, dv_n, dv_e);
		survey.connect("127.0.0.1", 6002);
		Thread.sleep(5000);
		survey.startControlling();				
	}
}
