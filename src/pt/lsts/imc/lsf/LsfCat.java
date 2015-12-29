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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;

public class LsfCat {

	protected LinkedHashMap<Long, Double> lastMessageTimestamps = new LinkedHashMap<Long, Double>();
	
	private String getExtension(File f) {
		return f.getAbsolutePath().replaceAll("^[^.]*\\.(.*)$", "$1");
	}

	public void concatenate(File[] files, File destination, long minimumSeparationMillis, boolean append) throws Exception {

		if (files == null || files.length == 0)
			return;

		double minSeparation = minimumSeparationMillis / 1000.0;

		OutputStream fos = new FileOutputStream(destination, append);
		if (append)
			System.out.println("Appending to "+destination.getAbsolutePath()+"...");
		else
			System.out.println("Writing to "+destination.getAbsolutePath()+"...");

		IMCDefinition defs = null;
		File pivot = files[0].getParentFile();
		if (new File(pivot, "IMC.xml").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml"));
		else if (new File(pivot, "IMC.xml.gz").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml.gz"));
		else
			defs = IMCDefinition.getInstance();

		for (File f : files) {
			IMCInputStream input;

			if (getExtension(f).equals("lsf")) {
				input = new IMCInputStream(new FileInputStream(f), defs); 
			}
			else if (getExtension(f).equals("lsf.gz")) {
				input = new IMCInputStream(new GZIPInputStream(new FileInputStream(f)), defs);
			}
			else {
				System.err.println("Unrecognized file type: "+f.getAbsolutePath());
				continue;
			}
			System.out.println("Processing "+f.getAbsolutePath()+"...");
			long dropCount = 0;
			long writeCount = 0;
			UnserializedMessage msg;
			lastMessageTimestamps.clear();
			while(true) {
				try {
					msg = UnserializedMessage.readMessage(defs, input);
					long hash = msg.getHash();
					double time = msg.getTimestamp();
					if (lastMessageTimestamps.containsKey(hash) && defs.getType(msg.getMgId()).hasFlag("periodic")) {
						double diff = time - lastMessageTimestamps.get(hash);
						if (diff >= 0 && diff < minSeparation) {
							dropCount++;
							continue;
						}
					}
					writeCount++;
					lastMessageTimestamps.put(hash, time);
					fos.write(msg.getData());					
				}

				catch (Exception e) {
					break;
				}
			}
			System.out.println("Wrote "+writeCount+" and dropped "+dropCount+" messages");
			input.close();
		}
		fos.close();
	}

	public static void main(String[] args) throws Exception {
		LsfCat cat = new LsfCat();
		
		if (args.length < 2) {
			System.out.println("Usage: lsfcat <destination.lsf> <file1.lsf> [<file2.lsf> ...]");
			System.exit(1);
		}
		
		File[] files = new File[args.length - 1];
		for (int i = 1; i < args.length; i++)
			files[i-1] = new File(args[i]);
		
		cat.concatenate(files, new File(args[0]), 1000, false);
		System.exit(0);
	}

}
