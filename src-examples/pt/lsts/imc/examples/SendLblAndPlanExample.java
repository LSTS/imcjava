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

import pt.lsts.imc.Abort;
import pt.lsts.imc.Goto;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.imc.types.PlanSpecificationAdapter;

public class SendLblAndPlanExample {

	public static void main(String[] args) throws Exception {
		IMCProtocol protocol = new IMCProtocol(6001);
		while (protocol.announceAgeMillis("lauv-seacon-4") > 10000) {
			Thread.sleep(1000);
			System.out.println("Waiting for an announce from LAUV-SEACON-4...");
		}
		PlanSpecificationAdapter adapter = new PlanSpecificationAdapter();

		Goto gt = new Goto()
			.setLat(Math.toRadians(41))
			.setLon(Math.toRadians(-8))
			.setZ(2)
			.setZUnits(Goto.Z_UNITS.DEPTH)
			.setSpeed(1000)
			.setSpeedUnits(Goto.SPEED_UNITS.RPM);
		
		adapter.addManeuver("goto1", gt);
		
		Goto gt2 = new Goto(gt)
			.setLat(Math.toRadians(41.0001));
		
		adapter.addManeuver("goto2", gt2);

		adapter.addTransition("goto1", "goto2", "ManeuverIsDone", null);
		IMCMessage msg = adapter.getData(IMCDefinition.getInstance());
		protocol.sendMessage("lauv-seacon-4", msg);
		System.out.println(msg.getSrc());
		System.out.println("sent: ");
		System.out.println(msg.asXml(false));

		protocol.sendMessage("lauv-seacon-4", new Abort());
		protocol.stop();
	}

}
