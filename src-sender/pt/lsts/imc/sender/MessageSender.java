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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import pt.lsts.imc.IMCMessage;

/**
 * @author zp
 *
 */
public class MessageSender extends JPanel {

	private static final long serialVersionUID = 1L;
	private MessageEditor editor = new MessageEditor();

	public MessageSender() {
		setLayout(new BorderLayout());
		add(editor, BorderLayout.CENTER);
	}

	private void openFile(File f) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader(f));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			text.append(line + "\n");

		reader.close();

		IMCMessage msg = editor.getMessage();

		if (f.getName().endsWith(".xml"))
			msg = IMCMessage.parseXml(text.toString());
		else
			msg = IMCMessage.parseJson(text.toString());

		editor.setMessage(msg);
	}

	private void saveFile(File f) throws Exception {
		String toSave = "";

		if (f.getName().endsWith(".xml")) {
			toSave = FormatUtils.formatXML(editor.getMessage().asXml(false));
		} else {
			toSave = FormatUtils.formatJSON(editor.getMessage().asJSON());
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(toSave);
		writer.close();
	}

	@SuppressWarnings("serial")
	public Collection<AbstractAction> fileActions() {
		ArrayList<AbstractAction> actions = new ArrayList<AbstractAction>();
		AbstractAction open = new AbstractAction("Open") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(UIUtils.createFileFilter("Single IMC message files (.XML, .JSON)",
						new String[] { "xml", "json" }));
				int op = chooser.showOpenDialog(MessageSender.this);

				if (op == JFileChooser.APPROVE_OPTION)
					try {
						openFile(chooser.getSelectedFile());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
			}
		};
		actions.add(open);

		AbstractAction save = new AbstractAction("Save") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(
						UIUtils.createFileFilter("Message files (.XML, .JSON)", new String[] { "xml", "json" }));
				int op = chooser.showSaveDialog(MessageSender.this);
				if (op == JFileChooser.APPROVE_OPTION)
					try {
						saveFile(chooser.getSelectedFile());
					} catch (Exception e2) {
						e2.printStackTrace();
					}

			}
		};
		actions.add(save);

		return actions;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Message Sender");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		MessageSender sender = new MessageSender();
		frame.getContentPane().add(sender);
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		JMenu file = menubar.add(new JMenu("File"));
		for (AbstractAction action : sender.fileActions()) {
			file.add(action);
		}
		frame.setVisible(true);
	}
}
