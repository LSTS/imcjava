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

import pt.lsts.imc.Distance;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCMessage;

public class IndexScanner {

	LsfIndex index;
	int curIndex;

	public IndexScanner(LsfIndex index) {
		this.index = index;
	}

	public IMCMessage next() {
		if (curIndex >= index.getNumberOfMessages())
			return null;
		return index.getMessage(++curIndex);
	}

	public IMCMessage previous() {
		if (curIndex <= 0)
			return null;
		return index.getMessage(--curIndex);
	}

	public <T extends IMCMessage> T next(Class<T> type) {
		if (curIndex >= index.getNumberOfMessages())
			return null;

		int idx = index.getNextMessageOfType(type.getSimpleName(), curIndex);
		if (idx == -1)
			return null;
		curIndex = idx + 1;
		try {
			return index.getMessage(idx, type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T extends IMCMessage> T previous(Class<T> type) {
		if (curIndex >= index.getNumberOfMessages())
			return null;

		int idx = index.getPreviousMessageOfType(index.getDefinitions()
				.getMessageId(type.getSimpleName()), curIndex);
		if (idx == -1)
			return null;
		curIndex = idx - 1;
		try {
			return index.getMessage(idx, type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T extends IMCMessage> T next(Class<T> type, String entity) {
		int ent = index.getEntityId(entity);
		if (ent == -1)
			return null;

		int i = index.getNextMessageOfEntity(index.getDefinitions()
				.getMessageId(type.getSimpleName()), ent, curIndex);
		if (i == -1)
			return null;
		curIndex = i + 1;
		try {
			return index.getMessage(i, type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setIndex(int msgIndex) throws Exception {
		if (msgIndex >= index.getNumberOfMessages())
			throw new Exception(
					"Given index is bigger than number of messages in the log");
		if (msgIndex < 0)
			throw new Exception("Given index must be greater or equal to 0");
		curIndex = msgIndex;
	}

	public int getIndex() {
		return curIndex;
	}

	public void setTime(double time) throws Exception {
		if (time > index.getEndTime())
			throw new Exception("Given time is after index end time");

		curIndex = index.getFirstMessageAtOrAfter(time);
	}

	public static void main(String[] args) throws Exception {
		IndexScanner scanner = new IndexScanner(new LsfIndex(new File("/home/zp/Desktop/logs/20131128/112056_np1_bathym_3m/Data.lsf")));
		scanner.setTime(1385638207);
		
		while (true) {
			scanner.next(Distance.class, "DVL Beam 3").dump(System.out);
			scanner.previous(EstimatedState.class).dump(System.out);
		}
	}

}
