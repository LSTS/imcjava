package pt.lsts.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Vector;

import pt.lsts.imc.CommsRelay;
import pt.lsts.imc.CompassCalibration;
import pt.lsts.imc.Elevator;
import pt.lsts.imc.FollowPath;
import pt.lsts.imc.FollowTrajectory;
import pt.lsts.imc.Goto;
import pt.lsts.imc.Loiter;
import pt.lsts.imc.Maneuver;
import pt.lsts.imc.PathPoint;
import pt.lsts.imc.PlanManeuver;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.PlanTransition;
import pt.lsts.imc.PopUp;
import pt.lsts.imc.Rows;
import pt.lsts.imc.StationKeeping;
import pt.lsts.imc.TrajectoryPoint;
import pt.lsts.imc.YoYo;

public class PlanUtilities {

	public static Collection<double[]> computeLocations(PlanSpecification plan) {
		LinkedHashMap<String, Maneuver> maneuvers = new LinkedHashMap<String, Maneuver>();
		LinkedHashMap<String, String> transitions = new LinkedHashMap<String, String>();

		for (PlanManeuver m : plan.getManeuvers())
			maneuvers.put(m.getManeuverId(), m.getData());

		for (PlanTransition pt : plan.getTransitions()) {
			if (transitions.containsKey(pt.getSourceMan())) {
				System.err
						.println("This should be used only in sequential plans");
				continue;
			}
			transitions.put(pt.getSourceMan(), pt.getDestMan());
		}

		Vector<String> visited = new Vector<String>();
		String man = plan.getStartManId();
		Vector<double[]> locations = new Vector<double[]>();

		while (man != null) {
			if (visited.contains(man)) {
				System.err.println("This should not be used in cyclic plans");
				return locations;
			}
			Maneuver m = maneuvers.get(man);
			locations.addAll(computeLocations(m));
			man = transitions.get(man);
		}
		
		return locations;
	}

	private static Collection<double[]> computeSingleLoc(Maneuver m) {
		return Arrays.asList(new double[] { Math.toDegrees(m.getDouble("lat")),
				Math.toDegrees(m.getDouble("lon")) });
	}

	private static Collection<double[]> computePath(FollowPath man) {
		double refLat = Math.toDegrees(man.getLat()), refLon = Math
				.toDegrees(man.getLon());
		Collection<PathPoint> path = man.getPoints();

		Vector<double[]> ret = new Vector<double[]>();
		for (PathPoint p : path)
			ret.add(WGS84Utilities.WGS84displace(refLat, refLon, 0, p.getX(),
					p.getY(), 0));

		return ret;
	}

	private static Collection<double[]> computePath(FollowTrajectory man) {
		double refLat = Math.toDegrees(man.getLat()), refLon = Math
				.toDegrees(man.getLon());
		Collection<TrajectoryPoint> path = man.getPoints();

		Vector<double[]> ret = new Vector<double[]>();
		for (TrajectoryPoint p : path)
			ret.add(WGS84Utilities.WGS84displace(refLat, refLon, 0, p.getX(),
					p.getY(), 0));

		return ret;
	}

	/**
	 * XY Coordinate conversion considering a rotation angle. (Eduardo Marques)
	 * 
	 * @param angleRadians
	 *            angle
	 * @param x
	 *            original x value on entry, rotated x value on exit.
	 * @param y
	 *            original y value on entry, rotated y value on exit.
	 * @param clockwiseRotation
	 *            clockwiseRotation rotation or not
	 */
	private static double[] rotate(double angleRadians, double x, double y,
			boolean clockwiseRotation) {
		double sina = Math.sin(angleRadians), cosa = Math.cos(angleRadians);
		double[] xy = { 0, 0 };
		if (clockwiseRotation) {
			xy[0] = x * cosa + y * sina;
			xy[1] = -x * sina + y * cosa;
		} else {
			xy[0] = x * cosa - y * sina;
			xy[1] = x * sina + y * cosa;
		}
		return xy;
	}

	private static Vector<double[]> calcRowsPoints(double width, double length,
			double hstep, double alternationPercent, double curvOff,
			boolean squareCurve, double bearingRad, double crossAngleRadians,
			boolean invertY) {
		width = Math.abs(width);
		length = Math.abs(length);
		hstep = Math.abs(hstep);

		boolean direction = true;
		Vector<double[]> newPoints = new Vector<double[]>();
		double[] point = { -curvOff, 0, 0, -1 };
		newPoints.add(point);

		double x2;
		for (double y = 0; y <= width; y += hstep) {
			if (direction) {
				x2 = length + curvOff;
			} else {
				x2 = -curvOff;
			}
			direction = !direction;

			double hstepDelta = 0;
			if (direction)
				hstepDelta = hstep * (1 - alternationPercent);
			point = new double[] { x2, y - hstepDelta, 0, -1 };
			newPoints.add(point);

			if (y + hstep <= width) {
				double hstepAlt = hstep;
				if (!direction)
					hstepAlt = hstep * alternationPercent;
				point = new double[] {
						x2 + (squareCurve ? 0 : 1)
								* (direction ? curvOff : -curvOff),
						y + hstepAlt, 0, -1 };
				newPoints.add(point);
			}
		}

		for (double[] pt : newPoints) {
			double[] res = rotate(-crossAngleRadians, pt[0], 0, false);
			pt[0] = res[0];
			pt[1] = pt[1] + res[1];
			if (invertY)
				pt[1] = -pt[1];
			res = rotate(bearingRad + (!invertY ? -1 : 1) * -crossAngleRadians,
					pt[0], pt[1], false);
			pt[0] = res[0];
			pt[1] = res[1];
		}

		return newPoints;
	}

	private static Collection<double[]> computePath(Rows man) {
		double refLat = Math.toDegrees(man.getLat()), refLon = Math
				.toDegrees(man.getLon());
		boolean squareCurve = (man.getFlags() & Rows.FLG_SQUARE_CURVE) != 0;
		boolean invertY = (man.getFlags() & Rows.FLG_CURVE_RIGHT) == 0;
		Vector<double[]> offsetPoints = calcRowsPoints(man.getWidth(),
				man.getLength(), man.getHstep(), man.getAlternation()/100.0,
				man.getCoff(), squareCurve, man.getBearing(),
				man.getCrossAngle(), invertY);
		
		Vector<double[]> ret = new Vector<double[]>();
		for (double p[] : offsetPoints)
			ret.add(WGS84Utilities.WGS84displace(refLat, refLon, 0, p[0],
					p[1], 0));

		return ret;

	}

	public static Collection<double[]> computeLocations(Maneuver m) {
		switch (m.getMgid()) {

		case Goto.ID_STATIC:
		case YoYo.ID_STATIC:
		case Loiter.ID_STATIC:
		case CompassCalibration.ID_STATIC:
		case StationKeeping.ID_STATIC:
		case CommsRelay.ID_STATIC:
			return computeSingleLoc(m);
		case Elevator.ID_STATIC:
			if ((((Elevator) m).getFlags() & Elevator.FLG_CURR_POS) == 0)
				return computeSingleLoc(m);
			else
				return new Vector<double[]>();
		case PopUp.ID_STATIC:
			if ((((PopUp) m).getFlags() & PopUp.FLG_CURR_POS) == 0)
				return computeSingleLoc(m);
			else
				return new Vector<double[]>();
		case FollowPath.ID_STATIC:
			return computePath((FollowPath) m);
		case FollowTrajectory.ID_STATIC:
			return computePath((FollowTrajectory) m);
		case Rows.ID_STATIC:
			return computePath((Rows)m);
		default:
			return new Vector<double[]>();
		}
	}
}
