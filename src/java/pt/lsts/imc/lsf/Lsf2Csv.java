/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

/**
 * @author zp
 *
 */
public class Lsf2Csv {

	public Lsf2Csv(IMCDefinition defs, InputStream LsfInput, String message, BufferedWriter output) throws Exception {
		IMCMessage m = defs.create(message);
		int count = 0, total = 0;
		if (m == null)
			throw new Exception("Message name not valid: " + message);
		String[] fieldNames = m.getFieldNames();

		output.write("timestamp, src, dst, src_ent, dst_ent");
		for (String field : fieldNames) {
			output.write(", " + field);
		}
		output.write("\n");

		int mgid = m.getMgid();

		while (true) {
			try {
				UnserializedMessage msg = UnserializedMessage.readMessage(defs, LsfInput);
				total++;

				if (total % 5000 == 0)
					System.out.print(".");
				if (total % 400000 == 0)
					System.out.println();
				if (msg.getMgId() == mgid) {
					count++;
					m = msg.deserialize();
					output.write(m.getTimestamp() + ", " + m.getSrc() + ", " + m.getDst() + ", " + m.getSrcEnt() + ", "
							+ m.getDstEnt());
					for (String field : fieldNames) {
						output.write(", " + m.getAsString(field));
					}
					output.write("\n");
				}
			} catch (EOFException e) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		output.close();
		System.out.println("\nRead " + total + " messages and converted " + count + " messages.");
	}

	public static Lsf2Csv open(File file, String message) throws Exception {

		File logDir = file.getParentFile();
		File logFile = file;

		IMCDefinition defs;
		InputStream imcStream = null;

		if (!file.canRead())
			throw new Exception("Cannot read from " + file.getAbsolutePath());

		if (file.isDirectory()) {
			logDir = file;
			logFile = null;
			for (File f : file.listFiles()) {
				String filename = f.getName().toLowerCase();
				if (filename.endsWith(".lsf") || filename.endsWith(".lsf.gz")) {
					logFile = f;
					break;
				}
			}
		}

		if (logFile.getName().endsWith(".lsf.gz")) {
			imcStream = new GZIPInputStream(new FileInputStream(logFile));
		} else
			imcStream = new FileInputStream(file);

		if (new File(logDir, "IMC.xml").canRead())
			defs = new IMCDefinition(new FileInputStream(new File(logDir, "IMC.xml")));
		else if (new File(logDir, "IMC.xml.gz").canRead())
			defs = new IMCDefinition(new GZIPInputStream(new FileInputStream(new File(logDir, "IMC.xml.gz"))));
		else
			defs = IMCDefinition.getInstance();

		File out = new File(logDir, message + ".csv");
		System.out.println("Output will be written to " + out + "...");
		return new Lsf2Csv(defs, imcStream, message, new BufferedWriter(new FileWriter(out)));

	}

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Usage: ./lsf2csv <filename> <message>");
			System.exit(1);
		}
		open(new File(args[0]), args[1]);
	}

}
