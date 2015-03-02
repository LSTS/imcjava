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
package pt.lsts.imc.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.zip.GZIPInputStream;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;


public class LsfMerge {

	public void merge(File[] files, File destination) throws Exception {
		
		OutputStream fos = new FileOutputStream(destination);
		System.out.println("Writing to "+destination.getAbsolutePath()+"...");
		IMCDefinition defs = null;
		File pivot = files[0].getParentFile();
		if (new File(pivot, "IMC.xml").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml"));
		else if (new File(pivot, "IMC.xml.gz").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml.gz"));
		else
			defs = IMCDefinition.getInstance();
		
		LinkedHashMap<File, IMCInputStream> inputs = new LinkedHashMap<File, IMCInputStream>(); 
		LinkedHashMap<File, UnserializedMessage> messages = new LinkedHashMap<File, UnserializedMessage>(); 
		LinkedHashMap<File, Double> timestamps = new LinkedHashMap<File, Double>(); 
		
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
			inputs.put(f, input);
		}
		
		System.out.println("Merging "+inputs.size()+" files...");
		double curTime = Double.MAX_VALUE;
		
		
		for (File f : inputs.keySet()) {
			UnserializedMessage msg = UnserializedMessage.readMessage(defs, inputs.get(f));
			double time = msg.getTimestamp();
			curTime = Math.min(curTime, time);
			messages.put(f, msg);
			timestamps.put(f, time);
		}
		
		while (!inputs.isEmpty()) {
			double minTime = Double.MAX_VALUE;
			File minFile = null;
			for (File f : timestamps.keySet()) {
				if (timestamps.get(f) < minTime) {
					minFile = f;
					minTime = timestamps.get(f);
				}
			}
			fos.write(messages.get(minFile).getData());
			try {
				UnserializedMessage msg = UnserializedMessage.readMessage(defs, inputs.get(minFile));
				messages.put(minFile, msg);
				timestamps.put(minFile, msg.getTimestamp());
			}
			catch (Exception e) {
				e.printStackTrace();
				inputs.remove(minFile);
				timestamps.remove(minFile);
			}
		}
		fos.close();
	}
	
	public static void main(String[] args) throws Exception {
		LsfMerge merge = new LsfMerge();
		
		if (args.length < 2) {
			System.out.println("Usage: lsfmerge <destination.lsf> <file1.lsf> [<file2.lsf> ...]");
			System.exit(1);
		}
		
		File[] files = new File[args.length - 1];
		for (int i = 1; i < args.length; i++)
			files[i-1] = new File(args[i]);
		
		merge.merge(files, new File(args[0]));
		System.exit(0);
	}
	
	private String getExtension(File f) {
		return f.getAbsolutePath().replaceAll("^[^.]*\\.(.*)$", "$1");
	}

}
