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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import pt.lsts.imc.Abort;
import pt.lsts.imc.Goto;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.net.UDPTransport;

public class MessageEditor extends JPanel {

	private static final long serialVersionUID = -449856037981913932L;

	public enum MODE {JSON, XML}

	private IMCMessage msg; 
	private MODE mode;
	private JToggleButton xmlToggle = new JToggleButton("XML");
	private JToggleButton jsonToggle = new JToggleButton("JSON");
	RSyntaxTextArea textArea;

	public MessageEditor() {
		setLayout(new BorderLayout());
		textArea = new RSyntaxTextArea();
		RTextScrollPane scroll = new RTextScrollPane(textArea);
		ButtonGroup bgGroup = new ButtonGroup();
		bgGroup.add(xmlToggle);
		bgGroup.add(jsonToggle);
		setMode(MODE.JSON);
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(xmlToggle);
		top.add(jsonToggle);
		add(top, BorderLayout.NORTH);

		ActionListener toggleListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					msg = getMessage();
				}
				catch (Exception ex) {
				}

				if (jsonToggle.isSelected())
					setMode(MODE.JSON);
				else
					setMode(MODE.XML);
			}
		};

		jsonToggle.addActionListener(toggleListener);
		xmlToggle.addActionListener(toggleListener);

		add(scroll, BorderLayout.CENTER);

		JButton addMsg = new JButton("Insert");

		addMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			
				ArrayList<String> msgs = new ArrayList<String>();
				msgs.addAll(IMCDefinition.getInstance().getConcreteMessages());
				Collections.sort(msgs);
				Object res = JOptionPane.showInputDialog(MessageEditor.this, "Select message to insert", "Insert message",
						JOptionPane.QUESTION_MESSAGE, null, msgs.toArray(new String[0]), msgs.iterator().next());
				
				if (res == null)
					return;
				
				IMCMessage msg = IMCDefinition.getInstance().create(res.toString());
				
				if (textArea.getText().trim().isEmpty()) {
					setMessage(msg);	
				}
				else {
					switch (mode) {
					case JSON:
						String text = msg.asJSON();
						textArea.replaceSelection(text);
						break;
					case XML:
						String txt = msg.asXml(true);
						textArea.replaceSelection(txt);
						break;
					default:
						break;
					}
				}
			}
		});
		addMsg.setToolTipText("Insert message with default values at current position");
		top.add(addMsg);
		
		JButton validate = new JButton("Validate");

		validate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					validateMessage();
					msg = getMessage();					
				}
				catch (Exception ex) {
					ex.printStackTrace();
					UIUtils.exceptionDialog(MessageEditor.this, ex, "Error parsing message", "Validate message");					
					return;
				}
				JOptionPane.showMessageDialog(MessageEditor.this, "Message parsed successfully.", "Validate message", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		top.add(validate);
	}
	
	public void validateMessage() throws Exception {
		switch (mode) {
		case JSON:
			IMCMessage.parseJson(textArea.getText());
			break;
		case XML:
			IMCMessage.parseXml(textArea.getText());
			break;
		}
	}

	public IMCMessage getMessage() {
		try {
			switch (mode) {
			case JSON:
				this.msg = IMCMessage.parseJson(textArea.getText());
				break;
			case XML:
				this.msg = IMCMessage.parseXml(textArea.getText());
				break;
			}
		}
		catch (Exception e) {
			
		}
		return this.msg;		
	}

	public void setMode(MODE mode) {
		this.mode = mode;
		switch (mode) {
		case JSON:
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
			break;
		case XML:
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
			break;
		}

		jsonToggle.setSelected(mode == MODE.JSON);
		xmlToggle.setSelected(mode == MODE.XML);
		setMessage(msg);
	}

	public void setMessage(IMCMessage msg) {
		if (msg == null) {
			textArea.setText("");
			return;
		}
		switch (mode) {
		case JSON:
			try {
				textArea.setText(FormatUtils.formatJSON(msg.asJSON()));
			}
			catch(Exception e) {
				e.printStackTrace();
				textArea.setText("");
			}
			break;
		case XML:
			try {
				textArea.setText(FormatUtils.formatXML(msg.asXml(false)));
			}
			catch(Exception e) {
				textArea.setText("");
			}
			break;
		}
	}
	
	public static void main(String[] args) throws Exception  {
		UDPTransport.sendMessage(new Abort(), "127.0.0.1", 6002);
		JFrame frm = new JFrame("Test MessageEditor");
		MessageEditor editor = new MessageEditor();
		PlanControl pc = new PlanControl();
		pc.setArg(new Goto());
		editor.setMessage(pc);
		frm.getContentPane().add(editor);
		frm.setSize(800, 600);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);
	}
}
