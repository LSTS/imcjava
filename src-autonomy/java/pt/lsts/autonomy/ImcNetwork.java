/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.autonomy;

import java.util.ArrayList;

import pt.lsts.imc.Announce;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.OP;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.def.SystemType;
import pt.lsts.imc.net.ConnectFilter;
import pt.lsts.imc.net.IMCNode;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.util.PlanUtilities;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class ImcNetwork extends IMCProtocol {

	public ImcNetwork() {
		setAutoConnect(ConnectFilter.ALWAYS);
	}
	
	public String[] systemsOfType(SystemType type) {
		ArrayList<String> peers = new ArrayList<>();
		for (IMCNode nd : nodes.values()) {
            if (nd.isPeer()) {
                if (type == null || type.equals(((Announce)nd.getLastAnnounce()).getSysType())) {
                	peers.add(nd.getSysName());
                }
            }            
        }
		return peers.toArray(new String[0]);		
	}
	
	public boolean startPlan(PlanSpecification plan, String vehicle) {
		PlanControl pc = new PlanControl();
		pc.setPlanId(plan.getPlanId());
		pc.setArg(plan);
		pc.setType(TYPE.REQUEST);
		pc.setOp(OP.START);
		pc.setInfo("Plan for "+vehicle);
		
		return sendMessage(vehicle, pc);
	}
	
	public boolean stop(String vehicle) {
		PlanControl pc = new PlanControl();
		pc.setType(TYPE.REQUEST);
		pc.setOp(OP.STOP);
		
		return sendMessage(vehicle, pc);
	}
	
	public double[] positionOf(String vehicle) {
		try {
			return WGS84Utilities.toLatLonDepth(state(vehicle).last(EstimatedState.class));
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static void main(String[] args) throws Exception {
		ImcNetwork imc = new ImcNetwork();
		Thread.sleep(30000);
		
		String[] auvs = imc.systemsOfType(SystemType.UUV);
		double[] latitudes = new double[] {
				41.1,
				41.12,
				41.1
		};
		
		double[] longitudes = new double[] {
				-8,
				-8.03,
				-8
		};
		
		for (String auv : auvs) {
			PlanSpecification spec = PlanUtilities.createPlan(auv+"_plan", 1.2f, -2f, latitudes, longitudes);
			imc.stop(auv);
			imc.startPlan(spec, auv);
		}
	}
}
