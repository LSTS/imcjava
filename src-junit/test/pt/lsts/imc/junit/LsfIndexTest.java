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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import junit.framework.Assert;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndex;

/**
 * @author zp
 *
 */
public class LsfIndexTest {

	public List<File> samples() {
		ArrayList<File> ret = new ArrayList<File>();
		
		File samples = new File("samples");
		if (!samples.exists() || !samples.isDirectory()) {
			Assert.fail("Samples directory not found at "+samples.getAbsolutePath());
		}
		for (File log : samples.listFiles()) {
			if (log.isDirectory() && new File(log, "Data.lsf").canRead()) {
				ret.add(new File(log, "Data.lsf"));
			}
		}
		return ret;
	}
	
	@Test
	public void parseLsfTest() throws Exception {
		for (File log : samples()) {
			long start = System.currentTimeMillis();
			LsfIndex index = new LsfIndex(log);
			Assert.assertEquals(true, index.containsMessagesOfType("EstimatedState", "Announce"));
			Assert.assertEquals(false, index.containsMessagesOfType("TrexOperation"));
			long end = System.currentTimeMillis();
			System.out.println("Log with "+index.getNumberOfMessages()+" messages processed in "+(end-start)+" milliseconds.");
		}
	}
	
	@Test
	public void getMessageAtTimeTest() throws Exception {		
		LsfIndex index = new LsfIndex(new File("samples/auv/Data.lsf"));
		double endTime = index.getEndTime() - 0.5;
		double startTime = index.getStartTime() + 0.5;
		Random r = new Random(System.currentTimeMillis());
		for (int i = 0; i < 50; i++) {
			double time = startTime + (endTime-startTime) * r.nextDouble();
			IMCMessage msgAt = index.getMessageAt("EstimatedState", time);
			Assert.assertNotNull(msgAt);
			IMCMessage msgBefore = index.getMessageBeforeOrAt("EstimatedState", index.getNumberOfMessages()-1, time);
			Assert.assertNotNull(msgBefore);
			IMCMessage msgAfter = index.getMessageAtOrAfter("EstimatedState", 0, time);
			Assert.assertNotNull(msgAfter);
			Assert.assertTrue("Before <= At", msgBefore.getTimestamp() <= msgAt.getTimestamp());
			Assert.assertTrue("At <= After", msgAt.getTimestamp() <= msgAfter.getTimestamp());
			Assert.assertEquals(msgBefore.getTimestamp(), msgAfter.getTimestamp(), 0.5);
		}		
	}
	
	
}
