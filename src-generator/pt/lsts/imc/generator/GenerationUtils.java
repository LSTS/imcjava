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
package pt.lsts.imc.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import pt.lsts.imc.IMCAddressResolver;

public class GenerationUtils {

	public static String execCmd(String cmd, File where) throws Exception {
		
		Process p = Runtime.getRuntime().exec(cmd, null, where);
		
        Scanner s = new Scanner(p.getInputStream());
        
        String ret = "";
        s.useDelimiter("\\A");
        if (s.hasNext()) 
        	ret = s.next();
        s.close();
        return ret;
    }
	
	public static String getGitSha(File repo) throws Exception {
		return execCmd("git rev-parse HEAD", repo).trim();
	}
	
	public static String getGitBranch(File repo) throws Exception {
		String branch = execCmd("git log --pretty=format:%d -1", repo).trim();
		String head = execCmd("git log --pretty=format:%h -1 --abbrev-commit", repo).trim();
		String date = execCmd("git log --pretty=format:%ad -1 --date=short", repo).trim();
		return date+" "+head+" "+branch; 
	}
	
	public static String getGitCommitAuthor(File repo) throws Exception {
		return execCmd("git log -1 --format=%cn", repo).trim();
	}

	public static String getGitCommitEmail(File repo) throws Exception {
		return execCmd("git log -1 --format=%ce", repo).trim();
	}
	public static String getGitCommitNote(File repo) throws Exception {
		String log = execCmd("git log -1 --format=%B", repo).trim();
		return org.apache.commons.lang3.StringEscapeUtils
				.escapeJava(log);
	}
	
	public static Date getGitCommitDate(File repo) throws Exception {
		return new Date(Long.parseLong(execCmd("git log -1 --format=%at", repo).trim())*1000);
	}
	
	public static String getGitCommit(File repo) throws Exception {
		String author = getGitCommitAuthor(repo);
		String email = getGitCommitEmail(repo);
		String note = getGitCommitNote(repo);
		Date date = getGitCommitDate(repo);
		return author+" ("+email+"), "+date+", "+note;				
	}
	
	public static void checkRepo(File repo) throws Exception {
		if (!repo.isDirectory() || !repo.canRead())
			throw new Exception("Repository should point to the checked out directory");
		try {
			String sha = getGitSha(repo);
			if (sha.isEmpty())
				throw new Exception("Retrived SHA is empty?");
		}
		catch (Exception e) {
			throw new Exception("Could not get revision SHA for the given repository", e);
		}
			
		if (!new File(repo, "IMC.xml").canRead())
			throw new Exception("Could not find "+new File(repo, "IMC.xml").getCanonicalPath());
		if (!new File(repo, "IMC_Addresses.xml").canRead())
			throw new Exception("Could not find "+new File(repo, "IMC_Addresses.xml").getCanonicalPath());
	}
	
	public static InputStream getImcXml(File repo) throws IOException {
		return new FileInputStream(new File(repo, "IMC.xml"));
	}
	
	public static Map<String, Integer> getImcAddresses(File repo) throws IOException {
		FileInputStream fis = new FileInputStream(new File(repo, "IMC_Addresses.xml"));
		IMCAddressResolver resolver = new IMCAddressResolver(fis);
		fis.close();
		return resolver.getAddresses();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(org.apache.commons.lang3.StringEscapeUtils
			.escapeJava(getGitCommit(new File("../imc"))));
	}
}
