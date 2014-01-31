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
 * $Id:: ImcStatePanel.java 334 2013-01-02 11:20:47Z zepinto                   $:
 */
package pt.lsts.imc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pt.lsts.imc.EntityControl;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.Heartbeat;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.state.ImcSysState;
import pt.lsts.imc.state.ImcSystemState;

public class ImcStatePanel extends JPanel {

    private static final long serialVersionUID = 1L;
    protected LinkedHashMap<String, JToggleButton> toggles = new LinkedHashMap<String, JToggleButton>();
    protected JList<String> messagesList;
    protected JPanel mainPanel = new JPanel();
    protected ImcSystemState state;
    protected JTabbedPane tabs = new JTabbedPane();
    protected IMCMessage lastMsg = null;
    protected Timer timer = null;
    protected StateListModel stateListModel = null;
    
    public ImcStatePanel(ImcSystemState state) {
        this.state = state;
        setLayout(new BorderLayout());
        stateListModel = new StateListModel(state);
        messagesList = new JList<String>(stateListModel);
        add(new JScrollPane(messagesList), BorderLayout.WEST);
        messagesList.addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                String msgName = messagesList.getSelectedValue().toString();
                tabs.removeAll();
                IMCMessage[] msgs = ImcStatePanel.this.state.get(msgName, IMCMessage[].class);
                for (IMCMessage m: msgs) {
                    JLabel html = new JLabel(IMCUtil.getAsHtml(m));
                    html.setHorizontalAlignment(JLabel.CENTER);
                    html.setBackground(Color.white);
                    html.setOpaque(true);
                    tabs.add(ImcStatePanel.this.state.getEntityName(m.getSrcEnt()), new JScrollPane(html));            
                }
            }
        });
        
        add(tabs, BorderLayout.CENTER);
        
        scheduleRefreshTabTimer();
        
        this.addPropertyChangeListener("ancestor", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
//                System.out.println(evt.getNewValue());
                if (evt.getNewValue() == null) {
                    cleanup();
                }
                else {
                    scheduleRefreshTabTimer();
                }
            }
        });
    }

    /**
     * 
     */
    private void scheduleRefreshTabTimer() {
        if (timer != null)
            return;
        
        timer = new Timer(ImcStatePanel.class.getSimpleName() + " tab updater", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshTab();
            }
        }, 0, 250);
        
        stateListModel.scheduleRefreshListTimer();
    }
    
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (stateListModel != null) {
            stateListModel.cleanup();
            stateListModel = null;
        }
    }
    
    protected void refreshTab() {
        try {
            if (tabs.getSelectedIndex() != -1) {
                String msg = messagesList.getSelectedValue().toString();
                String entity = tabs.getTitleAt(tabs.getSelectedIndex());
                IMCMessage m;
                if (entity.equals("*") || entity.equals("0")) {
                    m = state.get(msg);
                }
                else {
                    m = state.get(msg+"."+entity, IMCMessage.class);
                }
                if (m != lastMsg && m != null) {
                	
                	if (tabs.getSelectedIndex() != -1) {
                		try {
                			JScrollPane pane = (JScrollPane) tabs.getComponentAt(tabs.getSelectedIndex());
                			JViewport viewPort = (JViewport) pane.getComponent(0);
                			JLabel lbl = (JLabel) viewPort.getComponent(0);
                			lbl.setText(IMCUtil.getAsHtml(m));
                		}
                		catch (Exception e) {
                			e.printStackTrace();
                		}
                	}
                    lastMsg = m;   
                }
            }            
        }
        catch (Exception e) {
            System.err.println("RefreshTab: "+e.getMessage());
        }
        //this.invalidate();
        //this.revalidate();
        this.repaint(50);
    }
    
    class StateListModel extends AbstractListModel<String> {
        
        private static final long serialVersionUID = 1L;
        protected Vector<String> messages = new Vector<String>();
        protected ImcSystemState state;
        protected Timer timer = null;
        
        protected boolean refreshList() {
            boolean changed = false;
            
            Vector<String> receivedMessages = new Vector<String>();
            receivedMessages.addAll(state.availableMessages());
            
            for (String s : receivedMessages) {
                if (!messages.contains(s)) {
                    messages.add(s);
                    changed = true;
                }
            }
            
            if (changed)
                Collections.sort(messages);
            
            return changed;
        }
        
        public StateListModel(ImcSystemState imcState) {
            this.state = imcState;
            refreshList();
            
            scheduleRefreshListTimer();            
        }

        /**
         * 
         */
        public void scheduleRefreshListTimer() {
            if (timer != null)
                return;
            
            timer = new Timer(ImcStatePanel.class.getSimpleName() + " update received messages", true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (refreshList()) {
                        fireContentsChanged(this, 0, messages.size()-1);
                    }
                }
            }, 500, 500);
        }
        
        @Override
        public String getElementAt(int index) {
            return messages.get(index);
        }
        
        @Override
        public int getSize() {
            return messages.size();
        }
        
        public void cleanup() {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }
    
    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        System.out.println("Visible " + aFlag);
    }
    
    public static void main(String[] args) throws Exception {
        
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } 
//        catch (Exception e) {
//            
//        }
//        
        ImcSysState state = new ImcSysState();
//        LsfIndex index = new LsfIndex(new File("D:\\LSTS-Logs\\2012-11-13-apdl-imc5\\lauv-noptilus-1\\20121115-154158_rows_-2m\\Data.lsf"),
//                IMCDefinition.getInstance());
//        
        ImcStatePanel statePanel = new ImcStatePanel(state);
        
        state.setMessage(new EstimatedState());
        state.setMessage(new Heartbeat());
        state.setMessage(new EntityControl());
        final JFrame frame = new JFrame();

        frame.getContentPane().add(statePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
//        int src = index.sourceOf(0);
//        
//        double start = index.timeOf(0);
//        long startMillis = System.currentTimeMillis();
//        
        EstimatedState hb = new EstimatedState();
//        for (int i = 0; i < index.getNumberOfMessages(); i++) {
//            double curTime = (System.currentTimeMillis() - startMillis)/1000.0 + start;
//            IMCMessage m = index.getMessage(i);
//            if (m.getSrc() == src) {
//                while (m.getTimestamp()> curTime) {
//                    Thread.sleep(10);
//                    curTime = (System.currentTimeMillis() - startMillis)/1000.0 + start;
//                }
//            }
//            hb.setTimestamp(System.currentTimeMillis() / 1E3);
//            m = hb.cloneMessage();
//            state.setMessage(m);
//            System.out.println(m);
//        }
        
        short ent = 0;
        for (int i = 0; i < 1000000000; i++) {
            Thread.sleep(100);
            hb.setTimestamp(System.currentTimeMillis() / 1E3);
            hb.setX(i);
            
            hb.setSrcEnt(ent++);
            ent = ent > 5 ? 0 : ent;
            IMCMessage m = hb.cloneMessage();
            state.setMessage(m);
        }
        System.out.println("finished");
        
    }
}
