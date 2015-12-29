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
package pt.lsts.imc.net;

import pt.lsts.imc.IMCMessage;
import pt.lsts.neptus.messages.listener.PeriodicCallbacks;

/**
 * This class can be extended by stand-alone programs that want to interface with
 * an IMC network
 * 
 * @author zp
 *
 */
public class SimpleAgent {

	private Thread t = null;
	private IMCProtocol proto;
	{
		String name = getClass().getSimpleName();
		proto = new IMCProtocol(name, name.hashCode() % 100 + 6200);
		proto.register(this);
		PeriodicCallbacks.register(this);
	}

	public boolean broadcast(IMCMessage m) {
		return proto.broadcast(m);
	}
		
	public boolean send(String destination, IMCMessage m) {
		return proto.sendMessage(destination, m);		
	}

	public void stop() {
		proto.stop();
		if (t != null)
			t.interrupt();
		PeriodicCallbacks.stopAll();
	}

	public static void main(String[] args) {
		new SimpleAgent();
	}

}
