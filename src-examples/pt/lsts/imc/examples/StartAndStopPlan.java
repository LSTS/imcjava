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
package pt.lsts.imc.examples;

import pt.lsts.imc.Goto;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.net.ConnectFilter;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.util.PlanUtilities;

public class StartAndStopPlan {

	public static void main(String[] args) throws Exception {
		String vehicle = "lauv-seacon-2";
		String planId = "test_plan";
		
		IMCProtocol protocol = new IMCProtocol(6001);
		protocol.setAutoConnect(ConnectFilter.VEHICLES_ONLY);
		
		// Wait until the connection with vehicle is established
		while(!protocol.state(vehicle).isActive()) {
			Thread.sleep(1000);
			System.out.println("Waiting for "+vehicle+"...");
		}
		
		// Create goto maneuver
		Goto gt = new Goto()
				.setLat(Math.toRadians(41))
				.setLon(Math.toRadians(-8))
				.setZ(2)
				.setZUnits(Goto.Z_UNITS.DEPTH)
				.setSpeed(1000)
				.setSpeedUnits(Goto.SPEED_UNITS.RPM);
		
		// Create another goto maneuver based on previous one
		Goto gt2 = new Goto(gt)
				.setLat(Math.toRadians(41.0001));
		
		// Create a plan start request with the 2 maneuvers
		PlanControl cmd = new PlanControl();
		cmd.setArg(PlanUtilities.createPlan(planId, gt, gt2));
		cmd.setOp(OP.START);
		cmd.setRequestId(1);
		cmd.setType(TYPE.REQUEST);
		
		System.out.println("Starting plan "+planId+" on "+vehicle);
		// Send plan start cmd to vehicle
		protocol.sendMessage(vehicle, cmd);
		
		// wait 30 seconds
		Thread.sleep(30000);
		
		// Create a plan stop command
		cmd = new PlanControl();
		cmd.setPlanId(planId);
		cmd.setOp(OP.STOP);
		cmd.setRequestId(2);
		cmd.setType(TYPE.REQUEST);
		
		System.out.println("Stopping plan "+planId+" on "+vehicle);
		// Send plan stop cmd to vehicle
		protocol.sendMessage(vehicle, cmd);
		
		System.out.println("Exiting.");
		// Stop IMC connection
		protocol.stop();
	}

}
