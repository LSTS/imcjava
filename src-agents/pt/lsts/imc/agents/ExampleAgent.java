/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.agents;

import java.io.ByteArrayOutputStream;

import pt.lsts.imc.Abort;
import pt.lsts.imc.AgentContext;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.ImcAgent;
import pt.lsts.imc.annotations.Agent;
import pt.lsts.imc.annotations.Periodic;

import com.google.common.eventbus.Subscribe;

@Agent(name="test")
public class ExampleAgent implements ImcAgent {

    private AgentContext ctx;
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    IMCOutputStream out = new IMCOutputStream(baos);
    public String getName() {
        return "Example Agent";
    }

    // Called when the agent becomes active
    public void onStart(AgentContext ctx) {
        this.ctx = ctx;
        System.out.println("Agent is now running in "+ctx);
    }
    
    // Called when an agent is stopping
    public void onStop() {
        System.out.println("onStop()");
    }
    
    public void onMigrationSuccessLocal(String destination) {
    	System.out.println("Agent migrated successfully to "+destination);
    	ctx.removeAgent(this);
    }
    
    public void onMigrationSuccessDestination(AgentContext newCtx) {
    	System.out.println("Agent migrated successfully to "+newCtx);
    }
    
    public void onMigrationError(String destination, Exception e) {
    	System.out.println("Agent failed to migrate to "+destination+" because of a "+e.getClass().getSimpleName());
    }

    
    
    @Subscribe
    public void on(EstimatedState state) {
       System.out.println("GOT ESTIMATED STATE!");
    }
    
    @Periodic(millisBetweenUpdates=10000)
    public void sendAbort() {
    	//ctx.send(new Abort());
    }
}
