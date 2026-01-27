/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  
 * $Id:: IMCMessageSniffer.java 333 2013-01-02 11:11:44Z zepinto               $:
 */
package pt.lsts.imc.sniffer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.IMCMessagePanel;
import pt.lsts.imc.net.UDPTransport;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

public class IMCMessageSniffer extends JPanel implements MessageListener<MessageInfo, IMCMessage> {

	private static final long serialVersionUID = 1L;
	protected int portToListen;
	protected DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(new Vector<String>());
	protected LinkedHashMap<String, IMCMessage> lastMessages = new LinkedHashMap<String, IMCMessage>();	
	protected JComboBox<String> combo = new JComboBox<String>(model);
	protected JLabel stats = new JLabel(" ");
	protected Vector<String> known = new Vector<String>();
	protected IMCMessagePanel msgPanel = new IMCMessagePanel();
	protected int selectedType = -1;	
	protected int selectedEntity = -1;	
	
	protected UDPTransport transport = new UDPTransport(6001, 1);
	protected String lastSentDestination = "localhost:6002"; 
	protected long lastSecond = System.currentTimeMillis() / 1000;
	protected long count = 0;
	protected LinkedHashMap<Integer, String> aliases = new LinkedHashMap<Integer, String>();
	
	@Override
	public void onMessage(MessageInfo info, IMCMessage message) {
		
		if (message.getAbbrev().equalsIgnoreCase("EntityList")) {
			LinkedHashMap<String, String> alist = message.getTupleList("list");
			for (String key : alist.keySet())
				aliases.put(Integer.parseInt(alist.get(key)), key);
			lastMessages.clear();
			model.removeAllElements();
			known.clear();
		}
		
		String name = message.getAbbrev();
		if (aliases.containsKey(message.getHeader().getInteger("src_ent"))) {
			name = aliases.get(message.getHeader().getInteger("src_ent"))+"."+name;
		}
		
		Object prev = lastMessages.get(name);
		if (prev == null) {			
			known.add(name);
			Collections.sort(known);			
			model.insertElementAt(name, known.indexOf(name));			
		}
		lastMessages.put(name, message);
		
		if (message.getMgid() == selectedType) {
			if (selectedEntity == -1 || message.getHeader().getInteger("src_ent") == selectedEntity) {
				msgPanel.setMessage(message);
				long thisSecond = System.currentTimeMillis() / 1000;
				if (thisSecond != lastSecond) {
					stats.setText(count+" Hz");
					count = 1;
					lastSecond = thisSecond;
				}
				else {
					count++;
				}	
			}			
		}
	}
	
	public JMenuBar buildMenu() {
		JMenuBar menu = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenu imc = new JMenu("IMC");
		
		imc.add(new AbstractAction("Request entities", new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/reload.png"))) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String res = JOptionPane.showInputDialog(IMCMessageSniffer.this, "Enter destination", lastSentDestination);
				if (res == null)
					return;
				lastSentDestination = res;
				try {
					String host = res.split(":")[0];
					int port = Integer.parseInt(res.split(":")[1]);					
					transport.sendMessage(host, port, IMCDefinition.getInstance().create("Heartbeat"));
					transport.sendMessage(host, port, IMCDefinition.getInstance().create("EntityList", "op", 1));
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		imc.add(new AbstractAction("Listening port...", new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/settings.png"))) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String port = JOptionPane.showInputDialog(IMCMessageSniffer.this, "Select port to listen", transport.getBindPort());
				try {
					int lastPort = transport.getBindPort();
					if (port == null)
						return;					

					int newPort = Integer.parseInt(port);
					if (newPort != lastPort) {
						transport.purge();
						transport = new UDPTransport(newPort, 1);
						transport.addMessageListener(IMCMessageSniffer.this);
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		imc.add(new AbstractAction("Plot variable", new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/plot.png"))) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String var = JOptionPane.showInputDialog(IMCMessageSniffer.this, "Enter variable name", "Voltage.value");
				if (var == null)
					return;
				try {
					String parts[] = var.split("\\.");
					String message = parts[0];
					String field = parts[1];
					int type = IMCDefinition.getInstance().getMessageId(message);
					IMCDefinition.getInstance().getType(type).getFieldType(field);
					final ChartPanel panel = new ChartPanel(message, field, aliases);
					final JFrame tmp = new JFrame(var+" plot");
					transport.addMessageListener(panel, Arrays.asList(type));
					tmp.getContentPane().add(panel);
					tmp.setSize(350, 350);
					tmp.setIconImage(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/imc.png")).getImage());
					//tmp.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					tmp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					tmp.addWindowListener(new WindowAdapter() {
						public void windowClosing(java.awt.event.WindowEvent e) {
							transport.removeMessageListener(panel);
							tmp.setVisible(false);
							tmp.dispose();
						};
					});
					tmp.setVisible(true);
				}
				catch (Exception ex) {
					JOptionPane.showMessageDialog(IMCMessageSniffer.this, "The entered variable does not exist.", "Plot variable", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
					return;
				}
			}
		});
		
		file.add(new AbstractAction("Exit", new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/exit.png"))) {			

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);				
			}
		});
		menu.add(file);
		menu.add(imc);
		return menu;
	}
	
	public IMCMessageSniffer() {

		transport.addMessageListener(this);
		combo.setEditable(false);
		combo.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (combo.getSelectedItem() == null)
					return;
				String msgName = combo.getSelectedItem().toString();
				int entity = -1;
				if (msgName.contains(".")) {
					String parts[] = msgName.split("\\.");
					msgName = parts[1];
					for (Integer i : aliases.keySet())
						if (aliases.get(i).equals(parts[0])) {
							entity = i;
							break;
						}							
				}
					
				try {					
					selectedType = IMCDefinition.getInstance().getMessageId(msgName);
					selectedEntity = entity;
				}
				catch (Exception ex) {
					selectedType = -1;
					ex.printStackTrace();
				}
				stats.setText(" ");
				count = 0;
				msgPanel.setMessage(lastMessages.get(combo.getSelectedItem().toString()));
			}
		});
		setLayout(new BorderLayout());
		add(combo, BorderLayout.NORTH);
		add(msgPanel, BorderLayout.CENTER);
		add(stats, BorderLayout.SOUTH);
	}
	
	public static void main(String[] args) throws Exception {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (Exception e) {
		}
		
		JFrame frame = new JFrame("IMC Message Sniffer");
		frame.setIconImage(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/imc.png")).getImage());
		IMCMessageSniffer sniffer = new IMCMessageSniffer();
		frame.setJMenuBar(sniffer.buildMenu());
		frame.getContentPane().add(sniffer);
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}	
}
