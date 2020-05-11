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
 * 
 */
package pt.lsts.imc;

public class ImcStringDefs {

	public static final String IMC_SHA = "9c3b29617fd44e858e16024c70a3bab9d825eec1";
	public static final String IMC_BRANCH = "master";
	public static final String IMC_COMMIT = "Jose Pinto (zepinto@gmail.com), 2019-09-27T15:35:01Z, https://github.com/LSTS/imc/commit/9c3b29617fd44e858e16024c70a3bab9d825eec1, Added Squirtle to IMC addresses.";

	public static java.util.Map<String, Integer> IMC_ADDRESSES = new java.util.LinkedHashMap<String, Integer>();

	static {

		IMC_ADDRESSES = java.util.Collections.unmodifiableMap(IMC_ADDRESSES);
	}

	public static String getDefinitions() {

		java.io.InputStream xmlStream = ImcStringDefs.class.getResourceAsStream("/xml/IMC.xml");
		java.io.InputStreamReader isreader = new java.io.InputStreamReader(xmlStream);
		java.io.BufferedReader reader = new java.io.BufferedReader(isreader);
		java.lang.StringBuilder builder = new java.lang.StringBuilder();
		String line = null;

		try {
			while ((line = reader.readLine()) != null)
				builder.append(line+"\n");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
			return null;
		}

		return builder.toString();
	}
}
