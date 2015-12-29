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
package pt.lsts.imc.lsf;

import java.io.File;
import java.util.Iterator;

import pt.lsts.imc.IMCMessage;

public class LsfIndexEntityInterator implements Iterator<IMCMessage>, Iterable<IMCMessage>{

	protected String entityName;
    protected LsfIndex index;
    protected int nextIndex;
    
    public LsfIndexEntityInterator(LsfIndex index, String entityName) {
    	this.index = index;
    	this.entityName = entityName;
    	nextIndex = index.getNextMessageOfEntity(entityName, 0);    	
	}
    
	@Override
	public boolean hasNext() {
		return nextIndex != -1;
	}
	
	@Override
	public Iterator<IMCMessage> iterator() {
		return this;
	}
	
	@Override
	public IMCMessage next() {
		if (nextIndex == -1)
			return null;
		IMCMessage cur = index.getMessage(nextIndex); 
		nextIndex = index.getNextMessageOfEntity(entityName, nextIndex); 
		return cur;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static void main(String[] args) throws Exception {
		LsfIndex index = new LsfIndex(new File("/home/zp/workspace/logs/121224_multibeam_survey/Data.lsf"));
		for (IMCMessage m : index.iterateEntityMessages("Navigation"))
			System.out.println(m.getAbbrev());
	}
}
