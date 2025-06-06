/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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

import java.io.ByteArrayInputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.MessagePart;
import pt.lsts.imc.net.IMCFragmentHandler;

public class ImcFragmentsTest {
	 	@Test
	    public void test() throws Exception {
	 		IMCDefinition defs = IMCDefinition.getInstance();
	 		IMCFragmentHandler handler = new IMCFragmentHandler(IMCDefinition.getInstance());
	 		
	        for (String abbrev: defs.getMessageNames()) {
	            IMCMessage m = defs.create(abbrev);
	            IMCUtil.fillWithRandomData(m);
	            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
	            defs.serialize(m, baos1);
	            byte[] data1 = baos1.toByteArray();            
	            IMCMessage unser = defs.nextMessage(new IMCInputStream(new ByteArrayInputStream(data1), defs));
	            MessagePart[] parts = handler.fragment(unser, 100);
	            IMCMessage res = null;
	            for (MessagePart p : parts) {
	    			res = handler.setFragment(p);
	    		}
	            
	            Assert.assertNotNull(res);
	            Assert.assertEquals(res.toString(), unser.toString());
	            
	        }	
	    }
}
