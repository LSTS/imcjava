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
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.LogBookEntry.TYPE;
import pt.lsts.imc.net.IMCProtocol;

/**
 * @author zp
 *
 */
public class ImcAdapter {

	private IMCProtocol imc;
	
	public ImcAdapter(String systemName, int imcId, int bindPort, SYS_TYPE systemType) {
		imc = new IMCProtocol(systemName, bindPort, imcId, systemType);
		imc.setConnectOnHeartBeat();
		imc.register(this);
	}
	
	public void dispatch(IMCMessage message) {
		imc.sendToPeers(message);
	}
	
	private void report(LogBookEntry.TYPE type, String message) {
		LogBookEntry entry = new LogBookEntry();
		entry.setType(type);
		entry.setText(message);
		entry.setHtime(System.currentTimeMillis()/1000.0);
		dispatch(entry);
	}
	
	public void inf(String text) {
		report(TYPE.INFO, text);
	}
	
	public void err(String text) {
		report(TYPE.ERROR, text);		
	}
	
	public void war(String text) {
		report(TYPE.WARNING, text);
	}
	
	public void debug(String text) {
		report(TYPE.DEBUG, text);
	}

	public static void main(String[] args) throws Exception {
		ImcAdapter adapter = new ImcAdapter("DummyVehicle", 0x8043, 7009, SYS_TYPE.UUV);
		while (true) {
			adapter.dispatch(new Abort());	
			Thread.sleep(3000);
		}	
	}
	
}
