/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2019, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.mavadapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pt.lsts.imc.Acceleration;
import pt.lsts.imc.AngularVelocity;
import pt.lsts.imc.ApmStatus;
import pt.lsts.imc.AutopilotMode;
import pt.lsts.imc.Current;
import pt.lsts.imc.DesiredHeading;
import pt.lsts.imc.DesiredPitch;
import pt.lsts.imc.DesiredRoll;
import pt.lsts.imc.DesiredZ;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.EstimatedStreamVelocity;
import pt.lsts.imc.FuelLevel;
import pt.lsts.imc.GpsFix;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IndicatedSpeed;
import pt.lsts.imc.MagneticField;
import pt.lsts.imc.PathControlState;
import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanControlState.STATE;
import pt.lsts.imc.Pressure;
import pt.lsts.imc.RSSI;
import pt.lsts.imc.Temperature;
import pt.lsts.imc.Throttle;
import pt.lsts.imc.TrueSpeed;
import pt.lsts.imc.VehicleMedium;
import pt.lsts.imc.VehicleMedium.MEDIUM;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.VehicleState.OP_MODE;
import pt.lsts.imc.Voltage;
import pt.lsts.imc.adapter.ImcAdapter;
import pt.lsts.imc.def.SystemType;
import pt.lsts.neptus.messages.listener.Periodic;
import pt.lsts.util.WGS84Utilities;

/**
 * @author Manuel R.
 */
public class MAVAdapter extends ImcAdapter {

	private Set<IMCMessage> msgs = Collections.synchronizedSet(new HashSet<IMCMessage>());
	private Set<IMCMessage> toDelete = Collections.synchronizedSet(new HashSet<IMCMessage>());
	private VehicleState vehicleState = new VehicleState();
	private VehicleMedium medium = new VehicleMedium();
	private GpsFix gpsFix = new GpsFix();
	private FuelLevel fuel = new FuelLevel();
	private Current current = new Current();
	private Voltage voltage = new Voltage();
	private AutopilotMode autopilot = new AutopilotMode();
	private RSSI rssi = new RSSI();
	private Acceleration accel = new Acceleration();
	private AngularVelocity angular = new AngularVelocity();
	private MagneticField magnetic = new MagneticField();
	private Pressure pressure = new Pressure();
	private Temperature temperature = new Temperature();
	private ApmStatus apmStatus = new ApmStatus();
	private IndicatedSpeed indSpeed = new IndicatedSpeed();
	private TrueSpeed trueSpeed  = new TrueSpeed();
	private Throttle throttle = new Throttle();
	private DesiredRoll desRoll = new DesiredRoll();
	private DesiredPitch desPitch = new DesiredPitch();
	private DesiredHeading desHeading = new DesiredHeading();
	private DesiredZ desZ = new DesiredZ();
	
	private EstimatedState estimatedState = new EstimatedState();
	private EstimatedStreamVelocity estimatedSV = new EstimatedStreamVelocity();

	protected PlanControlState planControlState = new PlanControlState();
	private PathControlState pathControlState = new PathControlState();

	protected double latRads = 0, lonRads = 0, height = 0, depth = 0;
	protected double rollRads = 0, pitchRads = 0, yawRads = 0, speed = 0;
	protected double startLat = 0, startLon = 0, startTime = 0;

	public MAVAdapter(String name, int imcid, SystemType systemType) {
		super(name, imcid, 7010, systemType);

		planControlState.setState(STATE.BLOCKED);
		vehicleState.setOpMode(OP_MODE.BOOT);
		medium.setMedium(MEDIUM.UNKNOWN);
	}

	/**
	 * Every 1s, all messages waiting to be sent are published to the IMC network
	 */
	@Periodic(1000)
	public void sendNewMsgs() {
		synchronized (msgs) {
			for (IMCMessage m : msgs) {
				if (m != null)
					dispatch(m);

				//flag to be deleted
				toDelete.add(m);
			}

			Iterator<IMCMessage> it = toDelete.iterator();
			while(it.hasNext()) {
				//remove dispatched message
				IMCMessage toRemove = it.next();
				msgs.remove(toRemove);
			}
		}
	}

	/**
	 * Every 1s, the PlanControlState is sent to the IMC network
	 */
	@Periodic(1000)
	protected void sendPlanControlState() {
		dispatch(planControlState);
	}

	/**
	 * Every 1s, the VehicleMedium is sent to the IMC network
	 */
	@Periodic(1000)
	protected void sendVehicleMedium() {
		dispatch(medium); //TODO: srcEnt needs to be changed -> Medium Sensor
	}

	/**
	 * Every 1s, the VehicleState is published to the IMC network
	 */
	@Periodic(1000)
	protected void sendVehicleState() {
		if (planControlState == null)
			return;

		if (planControlState.getState() == STATE.READY)
			vehicleState.setOpMode(OP_MODE.SERVICE);
		else if (planControlState.getState() == STATE.EXECUTING)
			vehicleState.setOpMode(OP_MODE.MANEUVER);

		dispatch(vehicleState);
	}


	/**
	 * Integrate vehicle's speed at 10Hz
	 */
	@Periodic(100)
	protected void updatePosition() {
		if (startTime == 0 || speed == 0)
			return;

		double ellapsedTime = System.currentTimeMillis()/1000.0 - startTime;
		double northing = Math.cos(yawRads) * (ellapsedTime * speed);
		double easting = Math.sin(yawRads) * (ellapsedTime * speed);				
		double pos[] = WGS84Utilities.WGS84displace(Math.toDegrees(latRads), Math.toDegrees(lonRads), 0, northing,
				easting, 0);
		setPosition(pos[0], pos[1], height, depth);
	}	

	public void setVehicleState(OP_MODE mode) {
		vehicleState.setOpMode(mode);
	}

	public void setVehicleMedium(VehicleMedium.MEDIUM vehicleMedium) {
		medium.setMedium(vehicleMedium);
	}

	public void setAutopilotMode(AutopilotMode auto) {
		this.autopilot = auto;
	}

	public void setGpsFix(GpsFix gps) {
		this.gpsFix = gps;
	}

	public void setFuel(FuelLevel fuel) {
		this.fuel = fuel;
	}	

	public void setCurrent(Current current) {
		this.current = current;
	}

	public void setVoltage(Voltage voltage) {
		this.voltage = voltage;
	}

	public void setRSSI(RSSI rssi) {
		this.rssi = rssi;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * Change the vehicle's position
	 * @param latDegs Latitude of the system, in rads
	 * @param lonDegs Longitude of the system, in rads
	 * @param height Height above WGS84 ellipsoid
	 * @param depth Depth below water
	 */
	public void setPosition(double lat, double lon, double height, double depth) {
		this.startLat = this.latRads = lat;
		this.startLon = this.lonRads = lon;
		this.height = height;
		this.depth = depth;
		this.startTime = System.currentTimeMillis()/1000.0;
	}

	/**
	 * Change the vehicle's attitude
	 * @param rollDegs Roll angle in rads
	 * @param pitchDegs Pitch angle in rads
	 * @param yawDegs Yaw angle in rads
	 */
	public void setEuler(double rollRads, double pitchRads, double yawRads) {
		this.rollRads = rollRads;
		this.pitchRads = pitchRads;
		this.yawRads = yawRads;
	}

	/**
	 * @return the vehicleState
	 */
	public VehicleState getVehicleState() {
		return vehicleState;
	}

	/**
	 * @param vehicleState the vehicleState to set
	 */
	public void setVehicleState(VehicleState vehicleState) {
		this.vehicleState = vehicleState;
	}

	/**
	 * @return the medium
	 */
	public VehicleMedium getMedium() {
		return medium;
	}

	/**
	 * @param medium the medium to set
	 */
	public void setMedium(VehicleMedium medium) {
		this.medium = medium;
	}

	/**
	 * @return the gps
	 */
	public GpsFix getGps() {
		return gpsFix;
	}

	/**
	 * @param gps the gps to set
	 */
	public void setGps(GpsFix gps) {
		this.gpsFix = gps;
	}

	/**
	 * @return the autopilot
	 */
	public AutopilotMode getAutopilot() {
		return autopilot;
	}

	/**
	 * @param autopilot the autopilot to set
	 */
	public void setAutopilot(AutopilotMode autopilot) {
		this.autopilot = autopilot;
	}

	/**
	 * @return the rssi
	 */
	public RSSI getRssi() {
		return rssi;
	}

	/**
	 * @return the accel
	 */
	public Acceleration getAccel() {
		return accel;
	}

	/**
	 * @param accel the accel to set
	 */
	public void setAccel(Acceleration accel) {
		this.accel = accel;
	}

	/**
	 * @return the angular
	 */
	public AngularVelocity getAngular() {
		return angular;
	}

	/**
	 * @param angular the angular to set
	 */
	public void setAngular(AngularVelocity angular) {
		this.angular = angular;
	}

	/**
	 * @return the magnetic
	 */
	public MagneticField getMagnetic() {
		return magnetic;
	}

	/**
	 * @param magnetic the magnetic to set
	 */
	public void setMagnetic(MagneticField magnetic) {
		this.magnetic = magnetic;
	}

	/**
	 * @return the pressure
	 */
	public Pressure getPressure() {
		return pressure;
	}

	/**
	 * @param pressure the pressure to set
	 */
	public void setPressure(Pressure pressure) {
		this.pressure = pressure;
	}

	/**
	 * @return the temperature
	 */
	public Temperature getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(Temperature temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the apmStatus
	 */
	public ApmStatus getApmStatus() {
		return apmStatus;
	}

	/**
	 * @param apmStatus the apmStatus to set
	 */
	public void setApmStatus(ApmStatus apmStatus) {
		this.apmStatus = apmStatus;
	}

	/**
	 * @return the indSpeed
	 */
	public IndicatedSpeed getIndSpeed() {
		return indSpeed;
	}

	/**
	 * @param indSpeed the indSpeed to set
	 */
	public void setIndSpeed(IndicatedSpeed indSpeed) {
		this.indSpeed = indSpeed;
	}

	/**
	 * @return the trueSpeed
	 */
	public TrueSpeed getTrueSpeed() {
		return trueSpeed;
	}

	/**
	 * @param trueSpeed the trueSpeed to set
	 */
	public void setTrueSpeed(TrueSpeed trueSpeed) {
		this.trueSpeed = trueSpeed;
	}

	/**
	 * @return the throttle
	 */
	public Throttle getThrottle() {
		return throttle;
	}

	/**
	 * @param throttle the throttle to set
	 */
	public void setThrottle(Throttle throttle) {
		this.throttle = throttle;
	}

	/**
	 * @return the estimatedState
	 */
	public EstimatedState getEstimatedState() {
		return estimatedState;
	}

	/**
	 * @param estimatedState the estimatedState to set
	 */
	public void setEstimatedState(EstimatedState estimatedState) {
		this.estimatedState = estimatedState;
	}

	/**
	 * @return the estimatedSV
	 */
	public EstimatedStreamVelocity getEstimatedSV() {
		return estimatedSV;
	}

	/**
	 * @param estimatedSV the estimatedSV to set
	 */
	public void setEstimatedSV(EstimatedStreamVelocity estimatedSV) {
		this.estimatedSV = estimatedSV;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @return the depth
	 */
	public double getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(double depth) {
		this.depth = depth;
	}

	/**
	 * @return the fuel
	 */
	public FuelLevel getFuel() {
		return fuel;
	}

	/**
	 * @return the current
	 */
	public Current getCurrent() {
		return current;
	}

	/**
	 * @return the voltage
	 */
	public Voltage getVoltage() {
		return voltage;
	}

	public void addMessageToList(IMCMessage... newMessage) {
		synchronized (msgs) {
			for (IMCMessage m : newMessage)
				this.msgs.add(m);
		}
	}

	/**
	 * @return the planControlState
	 */
	public PlanControlState getPlanControlState() {
		return planControlState;
	}

	public DesiredRoll getDesRoll() {
		return desRoll;
	}

	public void setDesRoll(DesiredRoll desRoll) {
		this.desRoll = desRoll;
	}

	public DesiredPitch getDesPitch() {
		return desPitch;
	}

	public void setDesPitch(DesiredPitch desPitch) {
		this.desPitch = desPitch;
	}

	public DesiredHeading getDesHeading() {
		return desHeading;
	}

	public void setDesHeading(DesiredHeading desHeading) {
		this.desHeading = desHeading;
	}

	public DesiredZ getDesZ() {
		return desZ;
	}

	public void setDesZ(DesiredZ desZ) {
		this.desZ = desZ;
	}

	public PathControlState getPathControlState() {
		return pathControlState;
	}

	public void setPathControlState(PathControlState pathControlState) {
		this.pathControlState = pathControlState;
	}
}