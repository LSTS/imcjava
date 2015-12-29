/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2016, Laboratório de Sistemas e Tecnologia Subaquática
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the names of IMC, LSTS, IMCJava nor the names of its 
 *       contributors may be used to endorse or promote products derived from 
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL LABORATORIO DE SISTEMAS E TECNOLOGIA SUBAQUATICA
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package pt.lsts.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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
import pt.lsts.util.PlanUtilities.Waypoint.TYPE;

/**
 * This class provides some utility methods to work with IMC plans
 * 
 * @author zp
 */
public class PlanUtilities {


	/**
	 * This method calculates the maneuver sequence present in a plan. In case
	 * of cyclic plans, it will retrieve the first sequence of maneuvers that
	 * include one repeated maneuver.
	 * 
	 * @param plan
	 *            The plan to parsed.         
	 * @return a maneuver sequence.
	 * @see #getManeuverSequence(PlanSpecification)
	 */
	public static List<Maneuver> getManeuverCycleOrSequence(PlanSpecification plan) {
		ArrayList<Maneuver> ret = new ArrayList<Maneuver>();

		LinkedHashMap<String, Maneuver> maneuvers = new LinkedHashMap<String, Maneuver>();
		LinkedHashMap<String, String> transitions = new LinkedHashMap<String, String>();

		for (PlanManeuver m : plan.getManeuvers())
			maneuvers.put(m.getManeuverId(), m.getData());

		for (PlanTransition pt : plan.getTransitions()) {
			if (transitions.containsKey(pt.getSourceMan()))				
				continue;
			transitions.put(pt.getSourceMan(), pt.getDestMan());
		}

		Vector<String> visited = new Vector<String>();
		String man = plan.getStartManId();

		while (man != null) {
			if (visited.contains(man)) {
				ret.add(maneuvers.get(man));
				return ret;
			}
			visited.add(man);
			Maneuver m = maneuvers.get(man);
			ret.add(m);
			man = transitions.get(man);
		}

		return ret;
		
	}
	/**
	 * This method calculates the maneuver sequence present in a plan. In case
	 * of cyclic plans, it will retrieve the first non-repeating sequence of
	 * maneuvers.
	 * 
	 * @param plan
	 *            The plan to parsed.
	 * @return a maneuver sequence.
	 */
	public static List<Maneuver> getManeuverSequence(PlanSpecification plan) {
		ArrayList<Maneuver> ret = new ArrayList<Maneuver>();

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

		while (man != null) {
			if (visited.contains(man)) {
				System.err.println("This should not be used in cyclic plans");
				return ret;
			}
			visited.add(man);
			Maneuver m = maneuvers.get(man);
			ret.add(m);
			man = transitions.get(man);
		}

		return ret;
	}

	/**
	 * Given a PlanSpecification message, computes its list of WGS84 locations
	 * 
	 * @param plan
	 *            a PlanSpecification message
	 * @return a Collection of locations of the type double[2] = {latitude,
	 *         longitude}. <br/>
	 *         Latitude and Longitude are represented in decimal degrees.
	 */
	public static Collection<double[]> computeLocations(PlanSpecification plan) {
		ArrayList<double[]> locations = new ArrayList<double[]>();

		for (Maneuver m : getManeuverCycleOrSequence(plan))
			locations.addAll(computeLocations(m));

		return locations;
	}

	/**
	 * This method parses an IMC plan and calculates its waypoints.
	 * 
	 * @param plan
	 *            An IMC plan to be parsed
	 * @return A list of waypoints found in the plan.
	 * @see PlanUtilities.Waypoint
	 */
	public static List<Waypoint> computeWaypoints(PlanSpecification plan) {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

		for (Maneuver m : getManeuverCycleOrSequence(plan))
			waypoints.addAll(computeWaypoints(m));

		return waypoints;
	}

	/**
	 * Similar to {@link #computeLocations(Maneuver)} but in this case returning
	 * waypoint structures
	 * 
	 * @param m
	 *            The Maneuver to be converted to a list of waypoints
	 * @return a Collection of waypoints
	 * @see PlanUtilities.Waypoint
	 */
	public static Collection<Waypoint> computeWaypoints(Maneuver m) {
		ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
		Collection<double[]> path = null;
		Waypoint start = getStartLocation(m);

		if (start == null)
			return waypoints;

		start.setSpeed(getSpeed(m));
		
		switch (m.getMgid()) {
		case Goto.ID_STATIC:
		case YoYo.ID_STATIC:
		case PopUp.ID_STATIC:
			start.setType(TYPE.REGULAR);			
			waypoints.add(start);
			return waypoints;
		case Loiter.ID_STATIC:
			if (((Loiter) m).getDirection() == Loiter.DIRECTION.CLOCKW)
				start.setType(TYPE.LOITER_CW);
			else
				start.setType(TYPE.LOITER_CCW);
			waypoints.add(start);
			return waypoints;
		case CompassCalibration.ID_STATIC:
			if (((CompassCalibration) m).getDirection() == CompassCalibration.DIRECTION.CLOCKW)
				start.setType(TYPE.LOITER_CW);
			else
				start.setType(TYPE.LOITER_CCW);
			waypoints.add(start);
			return waypoints;
		case StationKeeping.ID_STATIC:
			start.setType(TYPE.STATION_KEEP);
			waypoints.add(start);
			return waypoints;
		case CommsRelay.ID_STATIC:
			start.setType(TYPE.OTHER);
			waypoints.add(start);
			return waypoints;
		case Elevator.ID_STATIC:
			start.setType(TYPE.LOITER_CW);
			waypoints.add(start);
			Waypoint end = start.copy();
			end.setSpeed(getSpeed(m));
			end.setDepth(Float.NaN);
			end.setAltitude(Float.NaN);
			end.setHeight(Float.NaN);
			Elevator elev = (Elevator) m;
			switch (elev.getEndZUnits()) {
			case ALTITUDE:
				end.setAltitude((float) elev.getEndZ());
				break;
			case DEPTH:
				end.setDepth((float) elev.getEndZ());
				break;
			case HEIGHT:
				end.setHeight((float) elev.getEndZ());
				break;
			default:
				break;
			}
			waypoints.add(end);
			return waypoints;
		case FollowPath.ID_STATIC:
			path = computePath((FollowPath) m);
			break;
		case FollowTrajectory.ID_STATIC:
			path = computePath((FollowTrajectory) m);
			break;
		case Rows.ID_STATIC:
			path = computePath((Rows) m);
			break;
		default:
			// return empty set of waypoints for other maneuvers
			return waypoints;
		}

		start.setType(TYPE.REGULAR);
		for (double[] p : path) {
			Waypoint wpt = start.copy();
			wpt.setLatitude(p[0]);
			wpt.setLongitude(p[1]);
			wpt.setSpeed(getSpeed(m));
			if (!Float.isNaN(wpt.getDepth()))
				wpt.setDepth((float) (wpt.getDepth() + p[2]));
			if (!Float.isNaN(wpt.getAltitude()))
				wpt.setAltitude((float) (wpt.getAltitude() + p[2]));
			if (!Float.isNaN(wpt.getHeight()))
				wpt.setHeight((float) (wpt.getHeight() + p[2]));
			waypoints.add(wpt);
		}

		return waypoints;
	}

	public static float getSpeed(Maneuver m) {
		if (m.getTypeOf("speed") == null)
			return 0;
		
		if (m.getTypeOf("speed_units") == null)
			return 0;
		if (m.getString("speed_units").equals("RPM"))
			return m.getFloat("speed") / 900.0f;		
		else if (m.getString("speed_units").equals("METERS_PS"))
			return m.getFloat("speed");
		return 0;
	}
	
	/**
	 * Compute the start location for a given maneuver
	 * 
	 * @param m
	 *            The maneuver
	 * @return The first waypoint for the maneuver or <code>null</code> if no
	 *         waypoint can be calculated.
	 */
	public static Waypoint getStartLocation(Maneuver m) {
		Waypoint wpt = new Waypoint();
		if (m.getTypeOf("lat") == null)
			return null;
		wpt.setLatitude(Math.toDegrees(m.getDouble("lat")));
		wpt.setLongitude(Math.toDegrees(m.getDouble("lon")));
		wpt.setRadius(m.getFloat("radius"));
		wpt.setTime(m.getFloat("duration"));
		wpt.setDepth(Float.NaN);
		wpt.setAltitude(Float.NaN);
		wpt.setHeight(Float.NaN);

		String zfield = "z", zunitsField = "z_units";
		if (m.getTypeOf("start_z") != null) {
			zfield = "start_z";
			zunitsField = "start_z_units";
		}

		if (m.getTypeOf(zfield) != null && m.getTypeOf(zunitsField) != null) {
			if ("ALTITUDE".equals(m.getString(zunitsField)))
				wpt.setAltitude(m.getFloat(zfield));
			else if ("DEPTH".equals(m.getString(zunitsField)))
				wpt.setDepth(m.getFloat(zfield));
			else if ("HEIGHT".equals(m.getString(zunitsField)))
				wpt.setHeight(m.getFloat(zfield));
		}
		return wpt;
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
					p.getY(), p.getZ()));

		return ret;
	}

	private static Collection<double[]> computePath(FollowTrajectory man) {
		double refLat = Math.toDegrees(man.getLat()), refLon = Math
				.toDegrees(man.getLon());
		Collection<TrajectoryPoint> path = man.getPoints();

		Vector<double[]> ret = new Vector<double[]>();
		for (TrajectoryPoint p : path)
			ret.add(WGS84Utilities.WGS84displace(refLat, refLon, 0, p.getX(),
					p.getY(), p.getZ()));

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
				man.getLength(), man.getHstep(), man.getAlternation() / 100.0,
				man.getCoff(), squareCurve, man.getBearing(),
				man.getCrossAngle(), invertY);

		Vector<double[]> ret = new Vector<double[]>();
		for (double p[] : offsetPoints)
			ret.add(WGS84Utilities.WGS84displace(refLat, refLon, 0, p[0], p[1],
					0));

		return ret;

	}

	/**
	 * Compute all the locations for a given maneuver message
	 * 
	 * @param m
	 *            The maneuver
	 * @return a Collection of locations of the type double[2] = {latitude,
	 *         longitude}. <br/>
	 *         Latitude and Longitude are represented in decimal degrees.
	 */
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
			return computePath((Rows) m);
		default:
			return new Vector<double[]>();
		}
	}


	/**
	 * Check if Plan is Cyclic (last maneuver transits to first one)
	 * 
	 * @param plan
	 *            The plan
	 * @return true if last maneuver transits to first one, false otherwise
	 */
	public static boolean isCyclic(PlanSpecification plan){
		String maneuverFirstID = plan.getManeuvers().get(0).getManeuverId();
		String maneuverLastID = plan.getManeuvers().get(plan.getManeuvers().size()-1).getManeuverId();
		
		for (PlanTransition pt : plan.getTransitions()) {
			if (pt.getSourceMan().equals(maneuverLastID)
					&& pt.getDestMan().equals(maneuverFirstID)){
				return true;
			}
		}
		return false;
	}

	/**
	 * This inner class represents a IMC plan waypoint. <br/>
	 * An IMC maneuver may contain more than one waypoints.
	 * 
	 * @author zp
	 *
	 */
	public static class Waypoint {
		private double latitude, longitude;
		private float altitude, depth, height, radius, time, speed;

		public enum TYPE {
			// Go directly to the waypoint
			REGULAR,
			// Move around the waypoint clockwise
			LOITER_CW,
			// Move around the waypoint counter-clockwise
			LOITER_CCW,
			// Stop at the waypoint
			STATION_KEEP,
			// Other waypoint behavior
			OTHER
		}

		private TYPE type = TYPE.OTHER;

		/**
		 * @return the latitude in degrees of the waypoint
		 */
		public double getLatitude() {
			return latitude;
		}

		/**
		 * @param latitude
		 *            the latitude in degrees of the waypoint
		 */
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		/**
		 * @return the longitude in degrees of the waypoint
		 */
		public double getLongitude() {
			return longitude;
		}

		/**
		 * @param longitude
		 *            the longitude in degrees of the waypoint
		 */
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		/**
		 * @return the altitude in meters or {@link java.lang.Float#NaN} if not
		 *         set
		 */
		public float getAltitude() {

			return altitude;
		}

		/**
		 * @param altitude
		 *            the altitude in meters or {@link java.lang.Float#NaN} to
		 *            unset
		 */
		public void setAltitude(float altitude) {
			this.altitude = altitude;
		}

		/**
		 * @return the depth in meters or {@link java.lang.Float#NaN} if not set
		 */
		public float getDepth() {
			return depth;
		}

		/**
		 * @param depth
		 *            the depth in meters or {@link java.lang.Float#NaN} to
		 *            unset
		 */
		public void setDepth(float depth) {
			this.depth = depth;
		}

		/**
		 * @return the WGS84 height in meters or {@link java.lang.Float#NaN} if
		 *         not set
		 */
		public float getHeight() {
			return height;
		}

		/**
		 * @param height
		 *            the WGS84 height in meters or {@link java.lang.Float#NaN}
		 *            to unset
		 */
		public void setHeight(float height) {
			this.height = height;
		}

		/**
		 * @return the radius the radius in meters or 0 if not applicable
		 */
		public float getRadius() {
			return radius;
		}

		/**
		 * @param radius
		 *            the radius in meters or 0 if not applicable
		 */
		public void setRadius(float radius) {
			this.radius = radius;
		}

		/**
		 * @return the time in seconds to stay at this waypoint
		 */
		public float getTime() {
			return time;
		}

		/**
		 * @param time
		 *            time in seconds to stay at this waypoint
		 */
		public void setTime(float time) {
			this.time = time;
		}

		/**
		 * @return this waypoint type
		 */
		public TYPE getType() {
			return type;
		}

		/**
		 * @param type
		 *            the type of waypoint to set
		 */
		public void setType(TYPE type) {
			this.type = type;
		}

		/**
		 * @return the speed in meters per second
		 */
		public float getSpeed() {
			return speed;
		}

		/**
		 * @param speed the speed to set in meters per second
		 */
		public void setSpeed(float speed) {
			this.speed = speed;
		}

		public Waypoint copy() {
			Waypoint copy = new Waypoint();
			copy.setType(getType());
			copy.setLatitude(getLatitude());
			copy.setLongitude(getLongitude());
			copy.setAltitude(getAltitude());
			copy.setDepth(getDepth());
			copy.setHeight(getHeight());
			copy.setRadius(getRadius());
			copy.setTime(getTime());
			return copy;
		}
	}
}
