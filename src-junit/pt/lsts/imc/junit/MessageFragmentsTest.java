/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.MessagePart;
import pt.lsts.imc.TextMessage;
import pt.lsts.imc.net.IMCFragmentHandler;
import pt.lsts.imc.net.IMCProtocol;

public class MessageFragmentsTest {

	@Test
	public void sendFragmentedMessageToConnectedVehicles() throws Exception {
		
		IMCProtocol proto = new IMCProtocol(6006);
		Thread.sleep(10000);
		IMCFragmentHandler handler = new IMCFragmentHandler(IMCDefinition.getInstance());
		
		TextMessage bigMessage = new TextMessage("imcjava", 
				"As armas e os Barões assinalados\n"+ 
				"Que da Ocidental praia Lusitana\n"+  
				"Por mares nunca de antes navegados \n"+ 
				"Passaram ainda além da Taprobana, \n"+ 
				"Em perigos e guerras esforçados \n"+ 
				"Mais do que prometia a força humana, \n"+ 
				"E entre gente remota edificaram \n"+ 
				"Novo Reino, que tanto sublimaram; \n"+ 
				"\n"+ 
				"E também as memórias gloriosas \n"+ 
				"Daqueles Reis que foram dilatando \n"+ 
				"A Fé, o Império, e as terras viciosas \n"+ 
				"De África e de Ásia andaram devastando, \n"+ 
				"E aqueles que por obras valerosas \n"+ 
				"Se vão da lei da Morte libertando, \n"+ 
				"Cantando espalharei por toda parte, \n"+ 
				"Se a tanto me ajudar o engenho e arte. \n");
		
		ArrayList<MessagePart> parts = new ArrayList<MessagePart>();
		parts.addAll(Arrays.asList(handler.fragment(bigMessage, 50)));
		
		Random r = new Random(System.currentTimeMillis());
		
		while(!parts.isEmpty()) {
			int index = r.nextInt(parts.size()); 
			for (String s : proto.systems())
				if (proto.sendMessage(s, parts.get(index))) {
					parts.get(index).dump(System.out);
					parts.remove(index);
				}
			Thread.sleep(500);
		}
		
	}
}
