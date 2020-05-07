/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2019, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.adapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author zp
 *
 */
public class VehicleDefsGenerator {

	public static void main(String[] args) throws IOException {
		List<String> params = Arrays.asList("id", "name", "type", "x-size", "y-size", "z-size");
		LinkedHashMap<String, String> paramsMap = new LinkedHashMap<>();
		
		JFileChooser chooser = new JFileChooser();
		
        chooser.setDialogTitle("Select base NVCL file");
        chooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "NVCL Files";
			}
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory() || arg0.getName().toLowerCase().endsWith(".nvcl");
			}
		});
        
        int op = chooser.showOpenDialog(null);
        if (op != JFileChooser.APPROVE_OPTION)
        	return;
        
        String original = new String(Files.readAllBytes(chooser.getSelectedFile().toPath()));
        String xml = original;
        for (String param : params) {
        	Pattern p = Pattern.compile("<"+param+">(.*)</"+param+">");
        	Matcher m = p.matcher(original);
        	m.find();
        	String text = m.group();
        	String previous = m.group(1);
        	System.out.println(param+" ["+previous+"]? ");
        	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        	String replacement = reader.readLine();
        	if(replacement.isEmpty())
        		replacement = previous;
        	paramsMap.put(param, replacement);
        	xml = xml.replaceAll(text, "<"+param+">"+replacement+"</"+param+">");	
        }
        
        int imcId = NameIMCIDGenerator.getId(paramsMap.get("id"), paramsMap.get("type"), false);
        String imcStr = Integer.toHexString(imcId);
        imcStr = imcStr.substring(0, 2)+":"+imcStr.substring(2);
        xml = xml.replaceAll("<imc-id>.*</imc-id>", "<imc-id>"+imcStr+"</imc-id>");
        File out = new File(chooser.getSelectedFile().getParent(), Integer.toHexString(imcId)+"-"+paramsMap.get("id")+".nvcl");
        Files.write(out.toPath(), xml.getBytes());
        System.out.println("Vehicle definition written to "+out);
	}
	
}
