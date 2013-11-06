package pt.lsts.imc.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

import pt.lsts.imc.ImcStringDefs;

public class ImcSynchTest {

	protected static final String java_imc = "https://raw.github.com/LSTS/imcjava/master/src/msgdefs/IMC.xml";
	protected static final String local_imc = "file:///"+new File("src/msgdefs/IMC.xml").getAbsolutePath();
	protected static final String master_imc = "https://raw.github.com/LSTS/imc/master/IMC.xml";
	
	protected static String getUrl(String url) throws Exception {
		
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		//Assert.assertNotNull(con);
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder sb = new StringBuilder();
		while (true) {
			String line = reader.readLine();
			if (line == null)
				return sb.toString();
			else
				sb.append(line+"\n");
		}
	}
	
	@Test
	public void localDefinitionsMatchImcMaster() throws Exception {
		
		String master = getUrl(master_imc);
		String local = ImcStringDefs.getDefinitions();
		Assert.assertEquals(master, local);		
		
	}
	@Test
	public void RemoteDefinitionsMatchImcMaster() throws Exception {
		
		String master = getUrl(master_imc);
		String java = getUrl(java_imc);
		Assert.assertEquals(master, java);		
		
	}
}
