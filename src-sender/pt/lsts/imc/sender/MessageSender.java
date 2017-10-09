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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.xml.bind.DatatypeConverter;

import org.xml.sax.SAXParseException;

import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.net.TcpTransport;
import pt.lsts.imc.net.UDPTransport;
import pt.lsts.ripples.model.iridium.ImcIridiumMessage;

/**
 * @author zp
 *
 */
public class MessageSender extends JPanel implements MessageDrawer.MessageSelectionListener {

	private static final long serialVersionUID = 1L;
	private MessageEditor editor = new MessageEditor();
	private JTextField txtHostname;
	private JFormattedTextField txtPort;
	private JComboBox<String> comboTransport = new JComboBox<>(new String[] {"UDP", "TCP", "HTTP", "Iridium"});
	private MessageDrawer drawer = new MessageDrawer();


	public MessageSender() {
		setLayout(new BorderLayout());
		add(editor, BorderLayout.CENTER);
		add(bottomPanel(), BorderLayout.SOUTH);
		drawer.loadMessages(new File("msg"));
	}

	public JPanel bottomPanel() {
		JPanel bottom = new JPanel(new FlowLayout());
		bottom.add(comboTransport);
		bottom.add(new JLabel("IP:"));
		try {
			txtHostname = new JTextField("127.0.0.1");
			txtHostname.setColumns(20);
			bottom.add(txtHostname);
		}
		catch (Exception e) {
			e.printStackTrace();
		}


		bottom.add(new JLabel("   Port:"));
		txtPort = new JFormattedTextField("0");
		txtPort.setValue(6002);
		txtPort.setColumns(5);	
		bottom.add(txtPort);

		JButton send = new JButton("Send!");
		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});

		bottom.add(send);
		return bottom;		
	}

	void sendMessage() {
		int port;
		String host;
		IMCMessage msg;		
		try {
			port = Integer.parseInt(txtPort.getText());
			if (txtHostname.getText().isEmpty())
				throw new IllegalArgumentException("Please set hostname");
			host = txtHostname.getText();

			editor.validateMessage();
			msg = editor.getMessage();

			switch (comboTransport.getSelectedItem().toString()) {
			case "TCP":
				sendViaTcp(msg, host, port);
				break;
			case "UDP":
				sendViaUdp(msg, host, port);
				break;
			case "HTTP":
				sendViaHttp(msg, host, port);
				break;
			case "Iridium":
				sendViaIridium(msg, host, port);
				break;
			default:
				break;
			}
		}
		catch (SAXParseException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MessageSender.this, "<html>XML error at line "+e.getLineNumber()+", column "+e.getColumnNumber()+": <ul><li>"+e.getMessage(), "Send message", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MessageSender.this, e.getClass().getSimpleName()+": "+e.getMessage(), "Send message", JOptionPane.ERROR_MESSAGE);
		}
	}

	void sendViaHttp(IMCMessage message, String host, int port) throws Exception {
		URL url = new URL("http://"+host+":"+port+"/dune/messages/imc/");
		System.out.println(url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");

		conn.setDoOutput(true);

		IMCOutputStream ios = new IMCOutputStream(conn.getOutputStream());
		message.serialize(ios);
		conn.getOutputStream().flush();
		conn.getOutputStream().close();

		int responseCode = conn.getResponseCode();
		if (responseCode != 200)
			throw new Exception("Bad response code: "+responseCode);
	}

	void sendViaIridium(IMCMessage message, String host, int port) throws Exception {
	    String serverUrl = "http://ripples.lsts.pt/api/v1/iridium";
	    int timeoutMillis = 10000;
	    
		ImcIridiumMessage msg = new ImcIridiumMessage();
		msg.setDestination(message.getDst());
		msg.setSource(message.getSrc());
		msg.source = message.getSrc();
		msg.setMsg(message);

		byte[] data = msg.serialize();

		data = new String(DatatypeConverter.printHexBinary(data)).getBytes();
		
		URL u = new URL(serverUrl);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/hub" );
		conn.setRequestProperty( "Content-Length", String.valueOf(data.length * 2) );
		conn.setConnectTimeout(timeoutMillis);

		OutputStream os = conn.getOutputStream();
		os.write(data);
		os.close();

		InputStream is = conn.getInputStream();
		ByteArrayOutputStream incoming = new ByteArrayOutputStream();
		
		byte buff[] = new byte[1024];
		int read = 0;
		while ((read = is.read(buff)) > 0)
			incoming.write(buff, 0, read);
		is.close();

		System.out.println("Sent "+msg.getClass().getSimpleName()+" through HTTP: "+conn.getResponseCode()+" "+conn.getResponseMessage());		

		if (conn.getResponseCode() != 200) {
			throw new Exception("Server returned "+conn.getResponseCode()+": "+conn.getResponseMessage());
		}
		else {
			System.out.println(new String(incoming.toByteArray()));
		}
	}

	void sendViaUdp(IMCMessage message, String host, int port) throws Exception {
		UDPTransport.sendMessage(message, host, port);
	}

	void sendViaTcp(IMCMessage message, String host, int port) throws Exception {
		TcpTransport.sendMessage(host, port, message, 10000);
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

		AbstractAction newAct = new AbstractAction("New") {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.newMessage();
			}
		};		

		actions.add(newAct);
		actions.add(open);
		actions.add(save);		

		return actions;
	}

	@Override
	public void messageSelected(String name, IMCMessage msg) {
		editor.setMessage(msg);
	}

	@Override
	public void storeCurrentMessage() {
		try {
			editor.validateMessage();
			IMCMessage msg = editor.getMessage();
			String name = msg.getAbbrev();
			int i = 0;
			while (drawer.getMessages().containsKey(name)) {
				i++;
				name = msg.getAbbrev()+"."+i;
			}
			name = JOptionPane.showInputDialog(this, "Enter message name", name);

			if (name == null)
				return;
			drawer.addMessage(name, msg);
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(MessageSender.this, e.getClass().getSimpleName()+": "+e.getMessage(), "Store message", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("IMC Message Sender");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		MessageSender sender = new MessageSender();
		try {
			frame.setIconImage(ImageIO.read(sender.getClass().getClassLoader().getResourceAsStream("images/bottle32.png")));	
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		frame.getContentPane().add(sender);
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		JMenu file = menubar.add(new JMenu("File"));
		for (AbstractAction action : sender.fileActions()) {
			file.add(action);
		}

		sender.drawer.addSelectionListener(sender);
		menubar.add(sender.drawer.getMessagesMenu());

		if (args.length > 0) {
			File f = new File(args[0]);
			try {
				sender.openFile(f);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		frame.setVisible(true);
	}



}
