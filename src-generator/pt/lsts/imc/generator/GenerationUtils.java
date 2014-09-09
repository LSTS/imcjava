package pt.lsts.imc.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import pt.lsts.imc.IMCAddressResolver;
import pt.lsts.imc.ImcStringDefs;

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
		return execCmd("git rev-parse --abbrev-ref HEAD", repo).trim();
	}
	
	public static String getGitCommitAuthor(File repo) throws Exception {
		return execCmd("git log -1 --format=%cn", repo).trim();
	}

	public static String getGitCommitEmail(File repo) throws Exception {
		return execCmd("git log -1 --format=%ce", repo).trim();
	}
	public static String getGitCommitNote(File repo) throws Exception {
		return execCmd("git log -1 --format=%B", repo).trim();
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
		System.out.println(ImcStringDefs.getDefinitions());
	}
}
