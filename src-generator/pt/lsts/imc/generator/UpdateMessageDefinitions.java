package pt.lsts.imc.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class UpdateMessageDefinitions {

	
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
	
	protected static void download(String url, File destination) throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		BufferedWriter writer = new BufferedWriter(new FileWriter(destination));
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null)
			writer.write(line+"\n");
		
		writer.close();
		reader.close();
		con.disconnect();
	}
	
	public static void main(String[] args) throws Exception {
		Properties p = new Properties();
		p.load(new FileInputStream("imc.properties"));
		String tag = p.getProperty("tag");
		String master_imc = "https://raw.github.com/LSTS/imc/"+tag+"/IMC.xml";
		String master_addrs = "https://raw.github.com/LSTS/imc/"+tag+"/IMC_Addresses.xml";

		System.out.println("Updating definitions from "+master_imc);
		download(master_imc, new File("src/msgdefs/IMC.xml"));
		download(master_addrs, new File("src/msgdefs/IMC_Addresses.xml"));
	}
}
