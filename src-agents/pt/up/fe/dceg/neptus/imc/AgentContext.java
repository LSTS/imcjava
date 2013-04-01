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
package pt.up.fe.dceg.neptus.imc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import pt.up.fe.dceg.neptus.imc.agents.ExampleAgent;
import pt.up.fe.dceg.neptus.imc.agents.ExecuteTrajectory;
import pt.up.fe.dceg.neptus.imc.agents.ImcBus;
import pt.up.fe.dceg.neptus.imc.agents.WebServerAgent;
import pt.up.fe.dceg.neptus.imc.annotations.Periodic;
import pt.up.fe.dceg.neptus.imc.annotations.Property;

import com.google.common.eventbus.EventBus;

public class AgentContext {

    protected EventBus internalBus;
    protected ScheduledThreadPoolExecutor pool;
    protected Vector<ImcAgent> agents = new Vector<ImcAgent>();
    
    public AgentContext() {
        internalBus = new EventBus();
        pool = new ScheduledThreadPoolExecutor(3);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                pool.shutdownNow();
                
                for (ImcAgent a : agents)
                    a.onStop();                
            }
        });
    }
    
    protected boolean installAgent(ImcAgent agent) {
        agents.add(agent);
        return bootstrap(agent);        
    }
    
    protected boolean bootstrap(final ImcAgent agent) {
        internalBus.register(agent);
        for (final Method method : agent.getClass().getMethods()) {
            Periodic per = method.getAnnotation(Periodic.class);
            if (per != null) {
                pool.scheduleAtFixedRate(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            method.invoke(agent);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, per.millisBetweenUpdates(), per.millisBetweenUpdates(), TimeUnit.MILLISECONDS);
            }
        }
        
        for (Field f : agent.getClass().getFields()) {
            Property p = f.getAnnotation(Property.class);
            //FIXME
        }
        
        agent.onStart(this);
        
        return true;
    }
    
    public void send(Object event) {
        internalBus.post(event);
    }    
    
    public static void main(String[] args) {
    	IMCDefinition.getInstance();
    	
        AgentContext framework = new AgentContext();
        framework.installAgent(new ExampleAgent());        
        framework.installAgent(new ImcBus(6006, "127.0.0.1", 6002));
        framework.installAgent(new ExecuteTrajectory());
        framework.installAgent(new WebServerAgent());
    }

}
