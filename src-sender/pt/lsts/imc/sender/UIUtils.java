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
package pt.lsts.imc.sender;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class UIUtils {
	static public void exceptionDialog(Component parent, Exception ex, String message, String title) {
		String text = message + "\n\n" + "Error details:\n\n" + ex.toString();

		String[] buttons = { "OK", "Save" };

		int ret = JOptionPane.showOptionDialog(parent, text, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.ERROR_MESSAGE, null, buttons, buttons[0]);

		if (ret == JOptionPane.NO_OPTION) {
			try {
				File report = new File("report.log");
				FileWriter out = new FileWriter(report);
				PrintWriter print = new PrintWriter(out);
				print.print(text);
				ex.printStackTrace(print);
				print.close();
			} catch (IOException ex1) {
				System.out.println(":'-( I'm really buggy! I can't do a dump of my exceptions");
				ex1.printStackTrace();
			}
		}
	}
	
	public static FileFilter createFileFilter(final String description, final String[] extensions) {
		return new FileFilter() {
			
			@Override
			public String getDescription() {
				return description;
			}
			
			@Override
			public boolean accept(File f) {
				if (!f.canRead())
					return false;
				if (f.isDirectory())
					return true;
				
				 String name = f.getName().toLowerCase();
				 for (String extension : extensions)
					 if (name.endsWith("."+extension.toLowerCase()))
						 return true;
				 
				 return false;
			}
		};
	}
}
