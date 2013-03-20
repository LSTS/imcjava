package pt.up.fe.dceg.neptus.imc.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateMessageDefinitions {

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
	
	public static void main(String[] args) throws Exception {
		HttpURLConnection con = (HttpURLConnection) new URL(master_imc).openConnection();
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/msgdefs/IMC.xml"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null)
			writer.write(line+"\n");
		
		writer.close();
		reader.close();
		con.disconnect();
	}
}
