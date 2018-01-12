/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática
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

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.LogBookEntry.TYPE;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.SimpleAgent;
import pt.lsts.neptus.messages.listener.Periodic;

/**
 * Simple stand-alone program to listen to IMC messages. The trick is to extend
 * SimpleAgent which will make this program announce itself and receive messages
 * from other peers in the network.
 * 
 * @see SimpleAgent
 * 
 * @author zp
 *
 */
public class AgentExample extends SimpleAgent {

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss.SSS");

	/**
	 * This method will be called whenever a LogBookEntry arrives from the
	 * selected vehicles and entities
	 * 
	 * @param log
	 *            The message
	 */
	@Consume(Source = { "lauv-seacon-1", "lauv-xplore-1" }, Entity = "Plan Engine")
	public void on(LogBookEntry log) {
		System.out.printf("[%s - %s]\n   %8s [%s] >> %s\n", sdf
				.format(new Date()), log.getSourceName(), log.getType()
				.toString(), log.getContext(), log.getText());
	}

	/**
	 * This method will be called every 10 seconds
	 */
	@Periodic(10 * 1000)
	public void doIt() {
		
		// Send a message to all known systems (including ourself)
		broadcast(new LogBookEntry(TYPE.DEBUG,
				System.currentTimeMillis() / 1000.0,
				getClass().getSimpleName(), "10 seconds have passed"));
	}

	public static void main(String[] args) {
		new AgentExample();
	}
}
