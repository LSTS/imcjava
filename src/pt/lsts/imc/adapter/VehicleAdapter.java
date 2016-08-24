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
package pt.lsts.imc.adapter;

import pt.lsts.imc.Abort;
import pt.lsts.imc.Announce.SYS_TYPE;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.VehicleState;
import pt.lsts.imc.VehicleState.OP_MODE;
import pt.lsts.imc.net.Consume;
import pt.lsts.neptus.messages.listener.Periodic;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class VehicleAdapter extends ImcAdapter {
	
	protected double latRads = 0, lonRads = 0, height = 0, depth = 0;
	protected double rollRads = 0, pitchRads = 0, yawRads = 0, speed = 0;
	protected double startLat = 0, startLon = 0, startTime = 0;
	
	
	public VehicleAdapter(String name, int imcid) {
		super(name, imcid, 7010, SYS_TYPE.UUV);
	}
	
	public void setPosition(double latDegs, double lonDegs, double height, double depth) {
		this.startLat = this.latRads = Math.toRadians(latDegs);
		this.startLon = this.lonRads = Math.toRadians(lonDegs);
		this.height = height;
		this.depth = depth;
		this.startTime = System.currentTimeMillis()/1000.0;
	}
	
	public void setEuler(double rollDegs, double pitchDegs, double yawDegs) {
		this.rollRads = Math.toRadians(rollDegs);
		this.pitchRads = Math.toRadians(pitchDegs);
		this.yawRads = Math.toRadians(yawDegs);
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	@Consume
	void on(Abort abort) {
		System.err.println("Received abort message!");
	}
	
	@Consume
	void on(PlanControl command) {
		System.out.println(command);
	}	
		
	@Periodic(500)
	void sendEstimatedState() {
		EstimatedState state = new EstimatedState();
		state.setLat(latRads);
		state.setLon(lonRads);
		state.setPhi(rollRads);
		state.setTheta(pitchRads);
		state.setPsi(yawRads);
		state.setDepth(depth);
		state.setHeight(height);
		dispatch(state);
	}
	
	@Periodic(1000)
	void sendVehicleState() {
		VehicleState state = new VehicleState();
		state.setOpMode(OP_MODE.SERVICE);
		dispatch(state);
	}
	
	@Periodic(333)
	void updatePosition() {
		if (startTime == 0 || speed == 0)
			return;
		
		double ellapsedTime = System.currentTimeMillis()/1000.0 - startTime;
		double northing = Math.cos(yawRads) * (ellapsedTime * speed);
		double easting = Math.sin(yawRads) * (ellapsedTime * speed);				
		double pos[] = WGS84Utilities.WGS84displace(Math.toDegrees(latRads), Math.toDegrees(lonRads), 0, northing,
				easting, 0);
		setPosition(pos[0], pos[1], height, depth);
	}

	
	public static void main(String[] args) throws Exception {
		VehicleAdapter adapter = new VehicleAdapter("lauv-seacon-3", 0x0017);
		double startLat = 41, startLon = -8;
		adapter.setPosition(startLat, startLon, 0, 0);
		adapter.setEuler(0, 0, -45);
		adapter.setSpeed(0.7);
	}

}
