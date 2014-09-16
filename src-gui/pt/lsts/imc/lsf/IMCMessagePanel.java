/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: IMCMessagePanel.java 333 2013-01-02 11:11:44Z zepinto                 $:
 */
package pt.lsts.imc.lsf;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCMessageType;

public class IMCMessagePanel extends JPanel implements TableModel {

	private static final long serialVersionUID = 1L;

	protected Vector<TableModelListener> listeners = new Vector<TableModelListener>();
	
	protected IMCMessageType type = null;
	protected String[] fieldNames = new String[0];
	protected String[] headerNames = new String[0];

	protected JTable table;
	//protected JLabel msgLabel = new JLabel();
	protected IMCMessage message;
	protected JPanel controls = new JPanel();
	
	public IMCMessagePanel() {
		this.type = null;//IMCDefinition.getInstance().createHeader().getType();
		controls.setLayout(new BoxLayout(controls, BoxLayout.LINE_AXIS));
		headerNames = IMCDefinition.getInstance().createHeader().getFieldNames();
		table = new JTable(this);
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		controls.add(new JButton("a"));
		controls.add(new JButton("test"));
		controls.add(new JButton("teste3"));
		
		add(controls, BorderLayout.SOUTH);
	}
	
	public void setMessage(IMCMessage message) {
		if (message == null)
			return;
		this.message = message;
		this.type = message.getMessageType();
		fieldNames = type.getFieldNames().toArray(new String[0]);
		for (TableModelListener l : listeners)
			l.tableChanged(new TableModelEvent(this));			
	}
	
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return "Field";
		return "Value";
	}
	
	@Override
	public int getRowCount() {
		if (message == null)
			return 0;
		return headerNames.length + fieldNames.length;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			if (rowIndex < headerNames.length)
				return headerNames[rowIndex];
			return fieldNames[rowIndex-headerNames.length];
		}
			
		if (message != null) {
			if (rowIndex < headerNames.length) {
				return message.getHeader().getString(headerNames[rowIndex]);
			}
			return message.getString(fieldNames[rowIndex-headerNames.length]);
		}
			
		return "N/A";
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {		
		return false;
	}
	
	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);	
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			return;
		
	}	
	
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame("testing message panel");
		IMCMessagePanel panel = new IMCMessagePanel();
		panel.setMessage(IMCDefinition.getInstance().create("EstimatedState", "x", 1003.34, "y", 245.2324, "ref", 0));
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(panel);
		frame.setSize(400, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
