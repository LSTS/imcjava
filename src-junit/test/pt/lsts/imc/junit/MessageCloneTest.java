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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import pt.lsts.imc.EntityParameter;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.SetEntityParameters;

/**
 * @author pdias
 *
 */
public class MessageCloneTest {
    @Test
    public void test() {
        IMCDefinition def = IMCDefinition.getInstance();
        System.out.println(def.getSyncWord());
        
        SetEntityParameters sepMsg = new SetEntityParameters();
        sepMsg.setTimestamp(1533670103.47600000);
        sepMsg.setName("LBL");
        Collection<EntityParameter> epCollection = new ArrayList<>();
        EntityParameter epMsg = new EntityParameter();
        epMsg.setName("Active");
        epMsg.setValue("true");
        epCollection.add(epMsg);
        sepMsg.setParams(epCollection);
        
        SetEntityParameters sepMsgClone = sepMsg.cloneMessageTyped();
        assertNotSame(sepMsg, sepMsgClone);
        EntityParameter epO = ((SetEntityParameters) sepMsg).getParams().get(0);
        EntityParameter epC = ((SetEntityParameters) sepMsgClone).getParams().get(0);
        assertNotSame(epO, epC);

        assertTrue("Instance of is the same", sepMsgClone instanceof SetEntityParameters);
        assertTrue("Instance param of is the same", ((SetEntityParameters) sepMsgClone).getParams().get(0) instanceof EntityParameter);
    }
}
