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
 * $Id::                                                                       $:
 */
package pt.lsts.imc.lsf;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;

import pt.lsts.imc.Current;
import pt.lsts.imc.IMCDefinition;

/**
 * 
 * @author zp
 *
 */
public class LsfIterator<T> implements Iterator<T>, Iterable<T>{

    protected Class<T> clazz;
    protected String msgName;
    protected int nextIndex;
    protected LsfIndex index;
    protected double secondsBetweenMessages;
    protected LinkedHashMap<Integer, Double> entityTimeStamps = new LinkedHashMap<Integer, Double>();    
    protected int curIndex;
    
    public LsfIterator(LsfIndex index, Class<T> clazz, int fromIndex, long millisBetweenMessages) {
    	this.index = index;
        this.clazz = clazz;
        this.secondsBetweenMessages = millisBetweenMessages / 1000.0;
        this.msgName = clazz.getSimpleName();
        this.nextIndex = index.getNextMessageOfType(msgName, fromIndex);
        this.curIndex = fromIndex;        		
    }
    
    public LsfIterator(LsfIndex index, Class<T> clazz, long millisBetweenMessages) {
        this(index, clazz, 0, millisBetweenMessages);
    }    

    public LsfIterator(LsfIndex index, Class<T> clazz, int fromIndex) {
        this(index, clazz, fromIndex, 0);
    }

    public LsfIterator(LsfIndex index, Class<T> clazz) {
        this(index, clazz, 0);
    }

    @Override
    public boolean hasNext() {
        return nextIndex != -1;
    }

    @Override
    public T next() {
        if (nextIndex == -1)
            return null;
        try {
            T o = index.getMessage(nextIndex, clazz);
            curIndex = nextIndex;
            if (secondsBetweenMessages == 0) {
                nextIndex = index.getNextMessageOfType(msgName, nextIndex);
            }
            else {
                while (nextIndex != -1) {
                    int hashcode = index.hashOf(nextIndex);
                    if (!entityTimeStamps.containsKey(hashcode) ||
                            index.timeOf(nextIndex) - entityTimeStamps.get(hashcode) > secondsBetweenMessages) {
                        break;
                    }
                    nextIndex = index.getNextMessageOfType(msgName, nextIndex);
                }
                if (nextIndex != -1)
                    entityTimeStamps.put(index.hashOf(nextIndex), index.timeOf(nextIndex));
            }
            return o;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public int getCurrentIndex() {
		return curIndex;
	}

	public static void main(String[] args) throws Exception {
        LsfIndex index = new LsfIndex(new File("/home/zp/Desktop/log-imc5/Data.lsf"),
                IMCDefinition.getInstance());

        for (int i = index.getFirstMessageOfType(Current.ID_STATIC); i != -1; 
                i = index.getNextMessageOfType(Current.ID_STATIC, i)) {
            
            System.out.println(index.sourceOf(i)+", "+index.entityOf(i)+", "+index.hashOf(i));
        }
    }
}
