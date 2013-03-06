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
 * $Id:: LsfMessageIterator.java 333 2013-01-02 11:11:44Z zepinto              $:
 */
package pt.up.fe.dceg.neptus.imc.lsf;

import java.util.Iterator;

import pt.up.fe.dceg.neptus.imc.IMCMessage;

public class LsfMessageIterator implements Iterator<IMCMessage> {
    
    
    public static Iterable<IMCMessage> getIterable(LsfIndex index, int msgType) {
        
        final LsfIndex idx = index;
        final int type = msgType;
        return new Iterable<IMCMessage>() {
            
            @Override
            public Iterator<IMCMessage> iterator() {
                return new LsfMessageIterator(idx, type);
            }
        };
    }
    
    LsfIndex index;
    int msgType;
    int curIndex = -1;
    
    public LsfMessageIterator(LsfIndex index, int msgType) {
        this.index = index;
        this.msgType = msgType;
        this.curIndex = index.getFirstMessageOfType(msgType);
    }
    
    @Override
    public boolean hasNext() {
        
        return curIndex == -1 || index.getNextMessageOfType(msgType, curIndex) == -1;
    }
    
    @Override
    public IMCMessage next() {
        curIndex = index.getNextMessageOfType(msgType, curIndex);
        if (curIndex != -1)
            return index.getMessage(curIndex);
        return null;
    }
    
    @Override
    public void remove() {
        // Not implemented
    }
    

}
