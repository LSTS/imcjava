/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática
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

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;

/**
 * @author zp
 *
 */
public class LsfSplit {

	public LsfSplit(String filename) throws Exception {
		
		File f = new File(filename);
		if (!f.canRead()) {
			System.err.println("Could not open file "+f);
			return;
		}
		IMCDefinition defs;
		
		File pivot = f.getParentFile();
		if (new File(pivot, "IMC.xml").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml"));
		else if (new File(pivot, "IMC.xml.gz").canRead())
			defs = new IMCDefinition(new File(pivot, "IMC.xml.gz"));
		else
			defs = IMCDefinition.getInstance();
		
		long midSize = f.length() / 2;
		
		IMCInputStream iis = new IMCInputStream(new FileInputStream(f), defs);
		UnserializedMessage msg = UnserializedMessage.readMessage(defs, iis);
		byte sync1 = msg.getData()[0];
		byte sync2 = msg.getData()[1];
		
		iis.skip(midSize);
		byte prev = 0;
		byte cur = 0;
		
		while (iis.available() > 0) {
			cur = iis.readByte();
			midSize++;
			if (prev == sync1 && cur == sync2) {
				break;
			}
			prev = cur;
		}
		
		midSize -= 2;
		
		System.out.println("File Size: "+f.length()+", Split here: "+midSize);
		
		writeFilePart(f, new File(f.getAbsolutePath()+".1"), 0, midSize-1);
		writeFilePart(f, new File(f.getAbsolutePath()+".2"), midSize, f.length());
		
	}
	
	private void writeFilePart(File source, File destination, long startPos, long endPos) throws Exception {
		FileInputStream in = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(destination);
		
		byte[] array = new byte[65535];
		
		for (long pos = startPos; pos < endPos; ) {
			int len = (int)Math.min(array.length, endPos-pos);
			in.read(array, 0, len);
			out.write(array, 0, len);
			
			pos += len;
			
			if (pos >= endPos)
				break;
		}
		
		in.close();
		out.close();
	}
	
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: lsfsplit <file.lsf>");			
			System.exit(1);
		}
		
		new LsfSplit(args[0]);		
	}
}
