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
 * $Id::                                                                       $:
 */
package pt.lsts.imc.teleop;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import pt.lsts.imc.PlanControl;
import pt.lsts.imc.RemoteActions;
import pt.lsts.imc.Teleoperation;
import pt.lsts.imc.net.StaticIMCConnection;

public class TeleoperationApp extends JPanel implements AxisListener {

    private static final long serialVersionUID = -3348532890197250474L;
    protected StaticIMCConnection conn;
    protected LinkedHashMap<String, Object> remoteActions = new LinkedHashMap<String, Object>();
    protected JLabel status = new JLabel("Teleoperation is off");
    protected JPanel buttonsBar = new JPanel(new GridLayout(0, 1));
    protected JCheckBox useAxisForThrust = new JCheckBox("Send thrust");
    protected ScheduledThreadPoolExecutor executor;
    
    public TeleoperationApp(String host, int port) {
        conn = new StaticIMCConnection(host, port, 7007);
        setLayout(new BorderLayout());
        add(status, BorderLayout.SOUTH);
        status.setFont(new Font("Arial", Font.PLAIN, 10));
        DualAxisPanel axis = new DualAxisPanel();
        add(axis, BorderLayout.CENTER);
        axis.addListener(this);
        addActionButton("Accelerate");
        addActionButton("Decelerate");
        addActionButton("Stop");
        final JToggleButton toggle = new JToggleButton("Teleoperation");
        toggle.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (toggle.isSelected())
                    startTeleop();
                else
                    endTeleop();
            }
        });
        buttonsBar.add(toggle);
        buttonsBar.add(useAxisForThrust);
        add(buttonsBar, BorderLayout.EAST);
    }
    
    protected void sendActions() {
        RemoteActions control = new RemoteActions();
        control.setActions(remoteActions);
        conn.send(control);
        status.setText(remoteActions.toString());
    }
    
    
    protected void addActionButton(final String label) {
        JButton btn = new JButton(label);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                remoteActions.put(label, 1);
            }
            
            public void mouseReleased(MouseEvent e) {
                remoteActions.put(label, 0);
            }
            
            
        });
        remoteActions.put(label, 0);
        buttonsBar.add(btn);
    }
    
    @Override
    public void throttleAxisChanged(double newValue) {
        
    }
    
    @Override
    public void xyAxisChanged(double xValue, double yValue) {
        remoteActions.put("Heading", (int)(xValue*127));
        if (useAxisForThrust.isSelected())
            remoteActions.put("Thrust", (int)(yValue*127));
        else
            remoteActions.put("Thrust", 0);
    }
    
    protected void startTeleop() {
        PlanControl pc = new PlanControl();        
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.START);
        pc.setRequestId(0);
        pc.setPlanId("teleoperation-mode");
        pc.setFlags(0);
        pc.setArg(new Teleoperation());        
        conn.send(pc);

        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                sendActions();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }
    
    protected void endTeleop() {
        PlanControl pc = new PlanControl();
        pc.setType(PlanControl.TYPE.REQUEST);
        pc.setOp(PlanControl.OP.STOP);
        pc.setRequestId(1);
        pc.setPlanId("teleoperation-mode");
        pc.setFlags(0);
        status.setText("Teleoperation is off");
        executor.shutdown();
        conn.send(pc);     
    }
    
    protected static void printUsage() {
        System.err.println("Usage: java -jar teleop.jar <hostname> <port>");
    }
    
    public static void main(String[] args) {
        
        if (args.length < 2) {
           String resp = JOptionPane.showInputDialog("Enter vehicle address", "127.0.0.1:6002");
           args = resp.split(":");         
        }
        try {
            TeleoperationApp app = new TeleoperationApp(args[0], Integer.valueOf(args[1]));
            JFrame frame = new JFrame("Controlling "+args[0]+":"+args[1]);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(380, 300);
            frame.getContentPane().add(app);
            frame.setVisible(true);
        }
        catch (Exception e) {
            printUsage();
            return;
        }        
    }
}
