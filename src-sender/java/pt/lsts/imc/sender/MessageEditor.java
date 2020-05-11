/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
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

import com.jhe.hexed.JHexEditor;

import pt.lsts.imc.Abort;
import pt.lsts.imc.Goto;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.PlanControl;
import pt.lsts.imc.net.UDPTransport;

public class MessageEditor extends JPanel {

	private static final long serialVersionUID = -449856037981913932L;

	public enum MODE {JSON, XML, HEX}

	private IMCMessage msg; 
	private MODE mode;
	private JToggleButton xmlToggle = new JToggleButton("XML");
	private JToggleButton jsonToggle = new JToggleButton("JSON");
	private JToggleButton hexToggle = new JToggleButton("HEX");
	private JButton addMsg = new JButton("Insert");

	private JPanel centerPanel = new JPanel(new CardLayout());
	private RSyntaxTextArea xmlTextArea, jsonTextArea;
	private RTextScrollPane xmlScroll, jsonScroll;
	private JHexEditor hexEditor;
	
	private ArrayList<MessageTemplate> templates = new ArrayList<>();

	public MessageEditor() {
		setLayout(new BorderLayout());
		xmlTextArea = new RSyntaxTextArea();
		jsonTextArea = new RSyntaxTextArea();
		jsonTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
		xmlTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
		xmlScroll = new RTextScrollPane(xmlTextArea);
		jsonScroll = new RTextScrollPane(jsonTextArea);
		hexEditor = new JHexEditor(new byte[0]);
		hexEditor.setEnabled(false);
		hexEditor.setBackground(Color.white);
		JPanel hexEditorPanel = new JPanel(new BorderLayout());
		hexEditorPanel.add(hexEditor, BorderLayout.CENTER);
		
		for (String name : IMCDefinition.getInstance().getConcreteMessages())
			templates.add(new MessageTemplate(name, IMCDefinition.getInstance().create(name)));
		
		templates.add(new MessageTemplate("(Blank)", null));
		Collections.sort(templates);

		ButtonGroup bgGroup = new ButtonGroup();
		bgGroup.add(xmlToggle);
		bgGroup.add(jsonToggle);
		bgGroup.add(hexToggle);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(xmlToggle);
		top.add(jsonToggle);
		top.add(hexToggle);
		add(top, BorderLayout.NORTH);

		setMode(MODE.XML);


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
				if (xmlToggle.isSelected())
					setMode(MODE.XML);
				if (hexToggle.isSelected())
					setMode(MODE.HEX);
			}
		};

		jsonToggle.addActionListener(toggleListener);
		xmlToggle.addActionListener(toggleListener);
		hexToggle.addActionListener(toggleListener);
		
		centerPanel.add(xmlScroll, "xml");
		centerPanel.add(jsonScroll, "json");
		centerPanel.add(hexEditorPanel, "hex");

		add(centerPanel, BorderLayout.CENTER);

		addMsg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				MessageTemplate template = (MessageEditor.MessageTemplate) JOptionPane.showInputDialog(MessageEditor.this, "Select a message template", "Create new message",
						JOptionPane.QUESTION_MESSAGE, null, templates.toArray(new MessageEditor.MessageTemplate[0]), templates.iterator().next());
				if (template == null)
					return;
				IMCMessage msg = template.message.cloneMessage();

				switch (mode) {
				case JSON:
					if (jsonTextArea.getText().trim().isEmpty())
						setMessage(msg);	
					else {
						String text = msg.asJSON();
						jsonTextArea.replaceSelection(text);
					}
					break;
				case XML:
					if (xmlTextArea.getText().trim().isEmpty())
						setMessage(msg);
					else {
						String txt = msg.asXml(true);
						xmlTextArea.replaceSelection(txt);
					}
					break;
				case HEX:
					break;
				default:
					break;
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
			IMCMessage.parseJson(jsonTextArea.getText());
			break;
		case XML:
			IMCMessage.parseXml(xmlTextArea.getText());
			break;
		default:
			break;
		}
	}

	public IMCMessage getMessage() {
		try {
			switch (mode) {
			case JSON:
				if (jsonTextArea.getText().trim().isEmpty())
					return null;
				
				this.msg = IMCMessage.parseJson(jsonTextArea.getText());
				break;
			case XML:
				if (xmlTextArea.getText().trim().isEmpty())
					return null;
				this.msg = IMCMessage.parseXml(xmlTextArea.getText());
				break;
			default:
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
			((CardLayout)centerPanel.getLayout()).show(centerPanel, "json");
			break;
		case XML:
			((CardLayout)centerPanel.getLayout()).show(centerPanel, "xml");
			break;
		case HEX:
			((CardLayout)centerPanel.getLayout()).show(centerPanel, "hex");
			break;
		default:
			break;	
		}

		jsonToggle.setSelected(mode == MODE.JSON);
		xmlToggle.setSelected(mode == MODE.XML);
		hexToggle.setSelected(mode == MODE.HEX);
		addMsg.setEnabled(mode != MODE.HEX);

		setMessage(msg);
	}

	public void setMessage(IMCMessage msg) {
		if (msg == null) {
			jsonTextArea.setText("");
			xmlTextArea.setText("");
			hexEditor.setBytes(new byte[0]);			
		}
		else {
			try {
				jsonTextArea.setText(FormatUtils.formatJSON(msg.asJSON()));
			}
			catch(Exception e) {
				e.printStackTrace();
				jsonTextArea.setText("");
			}
		
			try {
				xmlTextArea.setText(FormatUtils.formatXML(msg.asXml(false)));
			}
			catch(Exception e) {
				e.printStackTrace();
				xmlTextArea.setText("");
			}
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IMCOutputStream ios = new IMCOutputStream(baos);
				ios.writeMessage(msg);
				hexEditor.setBytes(baos.toByteArray());				
			}
			catch (Exception e) {
				e.printStackTrace();
				hexEditor.setBytes(new byte[0]);
			}
		}
	}
	
	static class MessageTemplate implements Comparable<MessageTemplate> {
		public final String name;
		public final IMCMessage message;
		
		public MessageTemplate(String name, IMCMessage msg) {
			this.name = name;
			this.message = msg;
		}
		
		@Override
		public int compareTo(MessageTemplate o) {
			return name.compareTo(o.name);
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	public void addTemplate(String name, IMCMessage msg) {
		templates.add(new MessageTemplate(name, msg));
		Collections.sort(templates);
	}
	
	public void newMessage() {
		MessageTemplate template = (MessageEditor.MessageTemplate) JOptionPane.showInputDialog(MessageEditor.this, "Select a message template", "Create new message",
				JOptionPane.QUESTION_MESSAGE, null, templates.toArray(new MessageEditor.MessageTemplate[0]), templates.iterator().next());
		if (template == null)
			return;
		setMessage(template.message);
	}

	public static void main(String[] args) throws Exception  {
		UDPTransport.sendMessage(new Abort(), "127.0.0.1", 6002);
		JFrame frm = new JFrame("Test MessageEditor");
		MessageEditor editor = new MessageEditor();
		PlanControl pc = new PlanControl();
		pc.setInfo("teste");
		pc.setArg(new Goto());
		editor.setMessage(pc);
		frm.getContentPane().add(editor);
		frm.setSize(800, 600);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.setVisible(true);
	}
}
