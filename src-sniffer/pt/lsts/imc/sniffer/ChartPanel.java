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
 *  
 * $Id:: ChartPanel.java 333 2013-01-02 11:11:44Z zepinto                      $:
 */
package pt.lsts.imc.sniffer;

import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.traces.Trace2DLtd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import pt.lsts.imc.IMCMessage;
import pt.lsts.neptus.messages.listener.MessageInfo;
import pt.lsts.neptus.messages.listener.MessageListener;

public class ChartPanel extends JPanel implements MessageListener<MessageInfo, IMCMessage> {

	private static final long serialVersionUID = 6181832884003345193L;
	private static double startTime = System.currentTimeMillis() / 1000.0;
	protected static final Color[] colors = {Color.red, Color.blue, Color.green, Color.yellow, Color.pink, Color.cyan, Color.orange, Color.gray, Color.magenta};
	protected int colorIndex = 0;
	protected String messageToDisplay;
	protected String variableToDisplay;
	protected LinkedHashMap<Integer, String> aliases = new LinkedHashMap<Integer, String>();
	protected ZoomableChart chart = new ZoomableChart();
	protected LinkedHashMap<String, Trace2DLtd> traces = new LinkedHashMap<String, Trace2DLtd>();
	protected JLabel title = new JLabel();
	
	
	public ChartPanel(String messageToDisplay, String variableToDisplay, LinkedHashMap<Integer, String> aliases) {
		this.messageToDisplay = messageToDisplay;
		this.variableToDisplay = variableToDisplay;
		this.aliases = aliases;
		setLayout(new BorderLayout());
		chart.setBackground(Color.black);
		chart.setForeground(Color.gray.brighter());
		chart.setUseAntialiasing(true);
		add(chart, BorderLayout.CENTER);
		chart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					chart.zoomAll();
				}
			}
		});
	}
	
	@Override
	public void onMessage(MessageInfo info, IMCMessage message) {
		if (message.getAbbrev().equals(messageToDisplay)) {
			int entity = message.getHeader().getInteger("src_ent");
			String name = variableToDisplay;
			if (aliases.containsKey(entity))		
				name = aliases.get(entity);
			else if (entity != 255)
				name = variableToDisplay+"."+entity;
			if (traces.get(name) == null) {
				traces.put(name, new Trace2DLtd(name));
				traces.get(name).setColor(colors[(colorIndex++)%colors.length]);
				chart.addTrace(traces.get(name));				
			}
			traces.get(name).addPoint(System.currentTimeMillis()/1000.0-startTime, message.getDouble(variableToDisplay));			
		}
	}	
}
