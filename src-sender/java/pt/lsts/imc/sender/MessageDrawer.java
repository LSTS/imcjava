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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import pt.lsts.imc.IMCMessage;

/**
 * @author zp
 *
 */
public class MessageDrawer implements ActionListener {

	private LinkedHashMap<String, IMCMessage> messages = new LinkedHashMap<String, IMCMessage>();
	private File folder = new File("msg");
	private JMenu menu;
	private HashSet<MessageSelectionListener> listeners = new HashSet<MessageSelectionListener>();

	public void loadMessages(File folder) {
		this.folder = folder;
		if (folder.exists() && folder.isDirectory()) {
			for (File f : folder.listFiles()) {
				try {
					IMCMessage m = loadMessage(f);
					messages.put(f.getName().substring(0, f.getName().lastIndexOf('.')), m);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		menu = new JMenu("Messages");
		
		JMenuItem store = new JMenuItem("Store");
		store.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				for (MessageSelectionListener listener : listeners)
					listener.storeCurrentMessage();
			}
		});

		menu.add(store);
		menu.addSeparator();
		
		ArrayList<String> msgNames = new ArrayList<String>();
		msgNames.addAll(messages.keySet());
		Collections.sort(msgNames);
		
		for (String msgName : msgNames) {
			JMenuItem item = new JMenuItem(msgName);
			item.addActionListener(this);
			menu.add(item);
		}		
	}	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			String msg = ((JMenuItem)e.getSource()).getText();
			for (MessageSelectionListener listener : listeners)
				listener.messageSelected(msg, messages.get(msg));
		}
	}	

	private static IMCMessage loadMessage(File f) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(f));
		StringBuilder text = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			text.append(line + "\n");

		reader.close();
		IMCMessage msg;

		if (f.getName().toLowerCase().endsWith(".xml"))
			msg = IMCMessage.parseXml(text.toString());
		else if (f.getName().toLowerCase().endsWith(".json"))
			msg = IMCMessage.parseJson(text.toString());
		else 
			throw new Exception("Unrecognized file extension: "+f.getName());

		return msg;
	}

	public void addMessage(String name, IMCMessage msg) {
		try {
			folder.mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(folder, name+".json")));
			writer.write(FormatUtils.formatJSON(msg.asJSON(true)));
			writer.close();
			if (messages.put(name, msg) == null) {
				JMenuItem item = new JMenuItem(name);
				item.addActionListener(this);
				menu.add(item);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}		 
	}

	JMenu getMessagesMenu() {
		return menu;
	}

	public Map<String, IMCMessage> getMessages() {
		return Collections.unmodifiableMap(messages);
	}

	public void addSelectionListener(MessageSelectionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeSelectionListener(MessageSelectionListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	public static interface MessageSelectionListener {
		void messageSelected(String name, IMCMessage msg);
		void storeCurrentMessage();
	}
}
