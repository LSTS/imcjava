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
package pt.lsts.imc.examples;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.net.ConnectFilter;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.neptus.messages.listener.Periodic;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class PrintTelemetry {

	private LinkedHashMap<Integer, EstimatedState> estates = new LinkedHashMap<Integer, EstimatedState>();
	
	@Consume
	public void on(EstimatedState state) {
		synchronized (estates) {
			estates.put(state.getSrc(), state);
		}
	}
	
	@Periodic(3000)
	public void every3secs() {
		
		ArrayList<EstimatedState> states = new ArrayList<EstimatedState>(); 
		
		synchronized (estates) {
			states.addAll(estates.values());
			estates.clear();
		}
		System.out.println(new Date());
		for (EstimatedState state : states) {
			double lld[] = WGS84Utilities.toLatLonDepth(state);
			System.out.printf("\t[%s] \tLAT: %.5f, LON: %.5f, DEPTH: %.1f, ALT: %.1f\n", state.getSourceName(), lld[0],
					lld[1], state.getDepth(), state.getAlt());
		}
		System.out.println();
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		PrintTelemetry tel = new PrintTelemetry();
		IMCProtocol proto = new IMCProtocol();
		proto.setAutoConnect(ConnectFilter.VEHICLES_ONLY);
		proto.register(tel);
		
		Thread.sleep(120 * 1000);
		
		proto.stop();
	}
	
}
