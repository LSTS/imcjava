/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: LsfInspector.java 333 2013-01-02 11:11:44Z zepinto                    $:
 */
package pt.lsts.imc.lsf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCUtil;

public class LsfInspector extends JPanel {

	private static final long serialVersionUID = 1L;

	protected JList<IMCMessage> allMessages;
	protected IMCMessagePanel messagePanel;
	protected JEditorPane editor = new JEditorPane("text/html", "");

    public LsfInspector(File folderToLoad) {
		setLayout(new BorderLayout(2,2));
		ImcData data = new ImcData();
		
		try {
			data.load(folderToLoad);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		MessagesListModel listModel = new MessagesListModel(data);
		messagePanel = new IMCMessagePanel();
		allMessages = new JList<IMCMessage>(listModel);
		allMessages.setCellRenderer(new MessageListCellRenderer());
		allMessages.addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				IMCMessage m = (IMCMessage)allMessages.getSelectedValue();
				messagePanel.setMessage(m);
				editor.setText(IMCUtil.getAsHtml(m));
			}
		});

		JTabbedPane tabs = new JTabbedPane();
		tabs.add(new JScrollPane(editor), "html");
		tabs.add(new JScrollPane(messagePanel), "table");		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(allMessages), tabs);
		split.setDividerLocation(400);
		add(split, BorderLayout.CENTER);
		allMessages.setSelectedIndex(0);
	}


	class MessagesListModel extends AbstractListModel<IMCMessage> {

		private static final long serialVersionUID = 5049538982696862090L;
		protected ImcData data;

		public MessagesListModel(ImcData data) {
			this.data = data;
		}

		@Override
		public IMCMessage getElementAt(int index) {
			return data.allMessages.get(index);
		}

		@Override
		public int getSize() {
			return data.allMessages.size();
		}		
	}

	class MessageListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 4305755571806348964L;

		protected SimpleDateFormat format = new SimpleDateFormat("yyyy/MMM/dd - HH:mm:ss.SSS");
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			IMCMessage m = (IMCMessage)value;

			JLabel label = new JLabel("["+
					format.format(new Date((long)(m.getHeader().getDouble("time")*1000)))+
					"] "+m.getAbbrev());
			if (isSelected) {
				label.setBorder(new LineBorder(Color.red.darker(), 2));
				label.setOpaque(true);
				label.setBackground(Color.yellow.brighter());
			}
			return label;
		}
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} 
		catch (Exception e) {
		    e.printStackTrace(); 
		}
		JFrame frame = new JFrame("LsfInspector");
		File f = null;
		if (args.length > 0) 
			f = new File(args[0]);
		else {
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(true);
			int result = chooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION)
				f = chooser.getSelectedFile();
			else 
				System.exit(0);
		}
		frame.getContentPane().add(new LsfInspector(f));
		frame.setVisible(true);
		frame.setSize(800,500);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}


