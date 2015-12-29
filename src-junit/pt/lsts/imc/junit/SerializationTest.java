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
 *  
 * $Id:: LsfInspectorPanel.java 333 2013-01-02 11:11:44Z zepinto               $:
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

public class SerializationTest {

    @Test
    public void testMessageCreation() {
        IMCDefinition defs = IMCDefinition.getInstance();
        
        for (String abbrev: defs.getMessageNames()) {
            IMCMessage m = defs.create(abbrev);
            IMCUtil.fillWithRandomData(m);
            Assert.assertNotNull(m);
        }
    }
    
    @Test
    public void testLsfSerialization() throws Exception {
        IMCDefinition defs = IMCDefinition.getInstance();
        
        for (String abbrev: defs.getMessageNames()) {
            IMCMessage m = defs.create(abbrev);
            IMCUtil.fillWithRandomData(m);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream(), baos2 = new ByteArrayOutputStream();
            defs.serialize(m, baos1);
            byte[] data1 = baos1.toByteArray();            
            //IMCUtil.dumpAsHex(m, 40);
            IMCMessage unser = defs.nextMessage(new IMCInputStream(new ByteArrayInputStream(data1), defs));            
            defs.serialize(unser, baos2);
            byte[] data2 = baos2.toByteArray();
            //IMCUtil.dumpAsHex(unser, 40);
            Assert.assertArrayEquals(data1, data2);            
        }
    }
    
    public static void main(String[] args) throws Exception{
        new SerializationTest().testJsonSerialization();
    }
    
    @Test
    public void testJsonSerialization() throws Exception {
        IMCDefinition defs = IMCDefinition.getInstance();
        
        for (String abbrev: defs.getConcreteMessages()) {
            IMCMessage original = defs.create(abbrev);
            IMCUtil.fillWithRandomData(original);
            
            String json1 = original.asJSON(true);
            IMCMessage msg = IMCMessage.parseJson(json1);
            String json2 = msg.asJSON(true);
            Assert.assertEquals(json1, json2);
        }
    }
    
    @Test
    public void testXmlSerialization() throws Exception {
        IMCDefinition defs = IMCDefinition.getInstance();
        
        for (String abbrev: defs.getMessageNames()) {
            IMCMessage m = defs.create(abbrev);
            IMCUtil.fillWithRandomData(m);
            String xml = m.asXml(false);
            try {
                IMCMessage unser = IMCMessage.parseXml(xml);
                Assert.assertEquals(xml, unser.asXml(false));                
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            
            
        }
    }
}
