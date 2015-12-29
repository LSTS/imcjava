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
 * $Id:: LLFMessageLogger.java 333 2013-01-02 11:11:44Z zepinto                $:
 */
package pt.lsts.imc.llf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCFieldType;
import pt.lsts.imc.IMCMessage;

public class LLFMessageLogger {

	protected LinkedHashMap<Integer, BufferedWriter> writers = new LinkedHashMap<Integer, BufferedWriter>();
	protected File dir;
	protected double startTime = -1;

	protected String[] headerNames = new String[] {"timestamp", "src", "src_ent", "dst", "dst_ent"};
	protected String[] headerFields = null;//new String[] {"time", "src", "src_ent", "dst", "dst_ent"};
	protected IMCDefinition defs = IMCDefinition.getInstance();
	
	public LLFMessageLogger(String directory) {
		dir = new File(directory);
	}
	
	public void flushLogs() {
	    for (BufferedWriter bw : writers.values()) {
	        try {
	            bw.flush();
	        }
	        catch (Exception e) {
	            e.printStackTrace();
            }
	    }
	}
	
	protected String[] getHeaderFields(IMCMessage m) {
	    
	    if (headerFields == null) {
	        headerFields = new String[] {"timestamp", "src", "src_ent", "dst", "dst_ent"};
	        if (m.getHeader().getTypeOf("timestamp") == null)
	            headerFields[0] = "time";
	    }
	    return headerFields;
	}
	
	public int logMessage(IMCMessage message) throws IOException {
	    message = message.cloneMessage();
		int id = message.getMessageType().getId(), count = 0;
		if (!writers.containsKey(id))
			writeHeader(message);
		BufferedWriter bw = writers.get(id);
		boolean first = true;
		message.setTimestamp(message.getTimestamp()-startTime);
		
		for (String s : getHeaderFields(message)) {
			if (!first)
				bw.write("\t");
			first = false;
			try {
			    bw.write(message.getHeader().getAsString(s));
			}
			catch (Exception e) {
			   e.printStackTrace();
            }
		}
		
		Vector<IMCMessage> innerMessages = new Vector<IMCMessage>();
		
		for (String s : message.getMessageType().getFieldNames()) {
			if (message.getMessageType().getFieldType(s) == IMCFieldType.TYPE_MESSAGE) {
				IMCMessage m = message.getMessage(s);
				if (m != null)
					innerMessages.add(m);
			}
			if (message.getMessageType().getFieldType(s) == IMCFieldType.TYPE_MESSAGELIST) {
                Vector<IMCMessage> ms = message.getMessageList(s);
                if (ms != null)
                    innerMessages.addAll(ms);
            }
			bw.write("\t"+message.getAsString(s));	
		}
		bw.write("\r\n");
		count ++;
		
		for (IMCMessage m : innerMessages) {
		    
		    for (String f : new String[] {"src", "dst", "src_ent", "dst_ent"})
			m.getHeader().setValue(f, message.getHeader().getValue(f));
			m.getHeader().set_timestamp(message.getTimestamp());
		    count += logMessage(m);
		}
		
		return count;
	}
	
	public void writeHeader(IMCMessage message) throws IOException {

	    if (startTime <= 0)
	        startTime = message.getTimestamp();
	    int id = message.getMessageType().getId();
		writers.put(id, new BufferedWriter(new FileWriter(new File(dir, message.getMessageType().getShortName()+".llf"))));
		BufferedWriter writer = writers.get(id);
		writer.write("#LLF1\r\n#generator: IMCJava\r\n#standard: IMC\r\n#version: "
				+ defs.getVersion() + "\r\n#name: "
				+ message.getMessageType().getShortName() + "\r\n#date: " + (new Date())
				+ "\r\n#startTime: " + startTime + "\r\n#types: ");
		
		String types = "", names = "";
		
		for (String s : getHeaderFields(message)) {
			types += message.getHeader().getTypeOf(s)+"\t";
			if ( message.getHeader().getTypeOf(s) == null) {
			    System.err.println("could not get type of "+s+" for message "+message.getAbbrev());
			}
		}
		        
		for (IMCFieldType t : message.getMessageType().getFieldIMCTypes())
			types += t+"\t";
		
		for (String s : headerNames)
			names += s+"\t";
		for (String s : message.getMessageType().getFieldNames())
			names += s+"\t";

		writer.write(types.trim()+"\r\n");
		writer.write(names.trim()+"\r\n");
	}
	
	public void close() {		
		for (BufferedWriter bw : writers.values()) {
			try {
				bw.close();				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		writers.clear();
	}
}
