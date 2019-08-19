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
package pt.lsts.imc.process;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.UIManager;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.PlanControl.TYPE;
import pt.lsts.imc.Sms;
import pt.lsts.imc.TextMessage;
import pt.lsts.imc.lsf.batch.LsfBatch;
import pt.lsts.imc.net.Consume;

/**
 * @author zp
 *
 */
public class BatchLogbook {

	private static final String FILENAME = "logbook.html";
	
	SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm.sss");
	{
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	BufferedWriter htmlOut;

	public BatchLogbook() throws Exception {
		htmlOut = new BufferedWriter(new FileWriter(new File(FILENAME)));
		htmlOut.write("<!DOCTYPE html>\n");
		htmlOut.write("<html>\n");
		htmlOut.write("<head>\n");
		htmlOut.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
		htmlOut.write("<style>\n");
		htmlOut.write("* {\n");
		htmlOut.write("  box-sizing: border-box;\n");
		htmlOut.write("}\n");
		htmlOut.write("\n");
		htmlOut.write("#myTable {\n");
		htmlOut.write("  border-collapse: collapse;\n");
		htmlOut.write("  width: 100%;\n");
		htmlOut.write("  border: 1px solid #ddd;\n");
		htmlOut.write("  font-size: 18px;\n");
		htmlOut.write("}\n");
		htmlOut.write("\n");
		htmlOut.write("#myTable th, #myTable td {\n");
		htmlOut.write("  text-align: left;\n");
		htmlOut.write("  padding: 12px;\n");
		htmlOut.write("}\n");
		htmlOut.write("\n");
		htmlOut.write("#myTable tr {\n");
		htmlOut.write("  border-bottom: 1px solid #ddd;\n");
		htmlOut.write("}\n");
		htmlOut.write("\n");
		htmlOut.write("#myTable tr.header, #myTable tr:hover {\n");
		htmlOut.write("  background-color: #f1f1f1;\n");
		htmlOut.write("}\n");
		htmlOut.write("#myTable tr.err {\n");
		htmlOut.write("  background-color: #ffa1a1;\n");
		htmlOut.write("}\n");
		htmlOut.write("</style>\n");
		htmlOut.write("</head>\n");
		htmlOut.write("<body>\n");
		
		htmlOut.write("  <table id=\"myTable\">\n");
		htmlOut.write("    <tr class=\"header\">\n");
		htmlOut.write("      <th style=\"width:10%;\">Time</th>\n");
		htmlOut.write("      <th style=\"width:10%;\">System</th>\n");
		htmlOut.write("      <th style=\"width:80%;\">Event</th>\n");
		htmlOut.write("    </tr>\n");
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					htmlOut.write("  </table>\n");
					htmlOut.write("</body>\n");
					htmlOut.write("</html>\n");
					htmlOut.close();
					System.out.println("All HTML output written to "+FILENAME+".");
					
					Desktop.getDesktop().open(new File(FILENAME));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			};
		});
	}

	@Consume
	void on(PlanControl msg) {
		if (msg.getType() == TYPE.REQUEST) {
			printOut(IMCDefinition.getInstance().getResolver().resolve(msg.getDst()), msg.getDate(),
					"Request from " + msg.getSourceName() + " to " + msg.getOpStr() + " '" + msg.getPlanId() + "'");
		}
	}

	@Consume
	void on(TextMessage msg) {
		printOut(msg.getSourceName(), msg.getDate(), "TEXT from " + msg.getOrigin() + ": " + msg.getText());
	}

	@Consume
	void on(Sms msg) {
		printOut(msg.getSourceName(), msg.getDate(), "SMS to " + msg.getNumber() + ": " + msg.getContents());
	}

	@Consume
	void on(LogBookEntry msg) {
		if (msg.getType() == LogBookEntry.TYPE.ERROR) {
			printErr(msg.getSourceName(), msg.getDate(), "" + msg.getContext() + ": " + msg.getText());
		}
	}

	private void printOut(String src, Date date, String text) {
		System.out.println(sdf.format(date) + "[" + src + "]: " + text);
		try {
			htmlOut.write("<tr><td>"+sdf.format(date)+"</td><td>"+src+"</td><td>"+text+"</td>\n");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void printErr(String src, Date date, String text) {
		System.err.println(sdf.format(date) + "[" + src + "]: " + text);
		try {
			htmlOut.write("<tr class=err><td>"+sdf.format(date)+"</td><td>"+src+"</td><td>"+text+"</td>\n");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		LsfBatch batch = LsfBatch.selectFolders();
		batch.process(new BatchLogbook());
	}
}
