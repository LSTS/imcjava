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
 *  
 * $Id::                                                                       $:
 */
package pt.lsts.imc.teleop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 * @author zp
 *
 */
public class DualAxisPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {

    private static final long serialVersionUID = 1L;
    protected Point2D mousePos = null;
    protected double curThrottle = 0, curX = 0, curY = 0;
    protected HashSet<AxisListener> listeners = new HashSet<AxisListener>();
    
    public DualAxisPanel() {
        setBackground(Color.WHITE);
        setBorder(new LineBorder(Color.black));
        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }
    
    public void addListener(AxisListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(AxisListener listener) {
        listeners.remove(listener);
    }
   
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double prev = curThrottle;
        curThrottle -= e.getWheelRotation() / 50.0;
        curThrottle = Math.min(1, curThrottle);
        curThrottle = Math.max(-1, curThrottle);
        
        if (prev != curThrottle)
            for (AxisListener listener : listeners)
                listener.throttleAxisChanged(curThrottle);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        updateMousePos(new Point2D.Double(getWidth()/2, getHeight()/2));
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
                
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3)
            updateMousePos(e.getPoint());
        else
            updateMousePos(e.getPoint());
        repaint();
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        updateMousePos(new Point2D.Double(getWidth()/2, getHeight()/2));
        repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3)
            updateMousePos(e.getPoint());
        else
            updateMousePos(e.getPoint());
        repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
    
    protected void updateMousePos(Point2D newMousePosition) {
        if (mousePos == null)
            mousePos = new Point2D.Double(getWidth()/2, getHeight()/2);     
        mousePos = newMousePosition;
        mousePos.setLocation(
                Math.min(mousePos.getX(), getWidth()),
                Math.min(mousePos.getY(), getHeight()));
        
        mousePos.setLocation(
                Math.max(mousePos.getX(), 0),
                Math.max(mousePos.getY(), 0));
        
        curX = 2* ((mousePos.getX() - getWidth()/2)/getWidth());
        curY = -2* ((mousePos.getY() - getHeight()/2)/getHeight());
        
        for (AxisListener listener : listeners)
            listener.xyAxisChanged(curX, curY);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.setColor(new Color(255,255,255,64));
        //g.fillRect(0, 0, getWidth(), getHeight());
        if (mousePos == null)
            updateMousePos(new Point2D.Double(getWidth()/2, getHeight()/2));
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
        g.setColor(Color.BLACK);
        
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);                
        ((Graphics2D)g).fill(new Ellipse2D.Double(mousePos.getX()-8, mousePos.getY()-8, 16, 16));
        
    }
    
    public static void main(String[] args) {
        DualAxisPanel panel = new DualAxisPanel();
        panel.setPreferredSize(new Dimension(150, 150));
        JPanel p = new JPanel();
        p.add(panel);
        JFrame frame = new JFrame("Teleoperation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.getContentPane().add(p);
        frame.setVisible(true);
    }
    
}
