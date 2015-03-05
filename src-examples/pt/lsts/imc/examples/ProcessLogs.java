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
package pt.lsts.imc.examples;

import java.io.File;

import pt.lsts.imc.PlanControlState;
import pt.lsts.imc.PlanControlState.STATE;
import pt.lsts.imc.lsf.LsfIndex;
import pt.lsts.imc.lsf.LsfIterator;

/**
 * @author zp
 *
 */
public class ProcessLogs {

	public static void main(String[] args) throws Exception {
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-1/20141210/142821_mvplanner_lauv-noptilus-1/Data.lsf"));
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-2/20141210/142819_mvplanner_lauv-noptilus-2/Data.lsf"));
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-3/20141210/142819_mvplanner_lauv-noptilus-3/Data.lsf"));
        
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-1/20141210/152321_mvplanner_lauv-noptilus-1/Data.lsf"));
        LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-3/20141210/152314_mvplanner_lauv-noptilus-3/Data.lsf"));
        
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-1/20141210/160736_mvplanner_lauv-noptilus-1/Data.lsf"));
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-2/20141210/160747_mvplanner_lauv-noptilus-2/Data.lsf"));
        //LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/logs/lauv-noptilus-3/20141210/160801_mvplanner_lauv-noptilus-3/Data.lsf"));
        
        LsfIterator<PlanControlState> it = index.getIterator(PlanControlState.class);
        PlanControlState lastState = null;
        PlanControlState state = null;
        while (it.hasNext()) {
            state = it.next();
            
            if (state.getState() != STATE.EXECUTING)
                continue;
            if (lastState == null)
                lastState = state;          
            
            if (!state.getManId().equals(lastState.getManId())) {
            	System.out.println(lastState.getSourceName()+"."+lastState.getManId()+" ("+index.getDefinitions().getMessageName(lastState.getManType())+"): "+(state.getTimestamp() - lastState.getTimestamp()));
                lastState = state;
            }
        }
        
        System.out.println(state.getSourceName()+"."+state.getManId()+" ("+index.getDefinitions().getMessageName(state.getManType())+"): "+(state.getTimestamp() - lastState.getTimestamp()));
    }
}
