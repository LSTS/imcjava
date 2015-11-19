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
package pt.lsts.imc.lsf.batch;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.util.TreeSet;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.UamRxFrame;
import pt.lsts.imc.UamTxFrame;
import pt.lsts.imc.gz.MultiMemberGZIPInputStream;
import pt.lsts.imc.lsf.UnserializedMessage;

/**
 * @author zp
 *
 */
public class LsfBatch {

	private TreeSet<LsfLog> logs = new TreeSet<>();

	public void addRecursively(File root) {

		LsfLog log = LsfLog.create(root);
		if (log != null) {
			logs.add(log);
			System.out.println("Added " + root.getAbsolutePath());
		}

		for (File f : root.listFiles()) {
			if (f.isDirectory()) {
				addRecursively(f);
			}
		}
	}
	
	public UnserializedMessage next() {
		LsfLog lower = logs.pollFirst();
		UnserializedMessage msg = lower.curMessage;
		try {
			lower.curMessage = UnserializedMessage.readMessage(lower.definitions, lower.input);
			logs.add(lower);
		}
		catch (EOFException e) {
			// expected...
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	public LsfBatch(File root) {
		addRecursively(root);
	}

	static class LsfLog implements Comparable<LsfLog> {
		public IMCDefinition definitions;
		public DataInput input;
		public UnserializedMessage curMessage;

		private LsfLog() {
		}

		public static LsfLog create(File root) {
			LsfLog log = new LsfLog();
			try {
				if (new File(root, "IMC.xml").canRead())
					log.definitions = new IMCDefinition(new File(root, "IMC.xml"));
				else if (new File(root, "IMC.xml.gz").canRead()) {
					log.definitions = new IMCDefinition(
							new MultiMemberGZIPInputStream(new FileInputStream(new File(root, "IMC.xml.gz"))));
				} else
					return null;

				if (new File(root, "Data.lsf").canRead())
					log.input = new DataInputStream(new FileInputStream(new File(root, "Data.lsf")));
				else if (new File(root, "Data.lsf.gz").canRead())
					log.input = new DataInputStream(
							new MultiMemberGZIPInputStream(new FileInputStream(new File(root, "Data.lsf.gz"))));
				else
					return null;
				log.curMessage = UnserializedMessage.readMessage(log.definitions, log.input);

				return log;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public int compareTo(LsfLog o) {
			return curMessage.compareTo(o.curMessage);
		}
	}

	public static void main(String[] args) {
		LsfBatch batch = new LsfBatch(new File("/home/zp/Desktop/to_upload_20151115"));
		
		while (true) {
			UnserializedMessage msg = batch.next();
			switch (msg.getMgId()) {
			case UamTxFrame.ID_STATIC:
			case UamRxFrame.ID_STATIC:
				try {
					System.out.print(msg.deserialize()+",");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				break;			
			default:
				break;
			}
		}
	}
}
