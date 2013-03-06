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
 * $Id:: LsfInspectorPanel.java 333 2013-01-02 11:11:44Z zepinto               $:
 */
package pt.up.fe.dceg.neptus.imc.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

import pt.up.fe.dceg.neptus.imc.EstimatedState;

public class PathPreviewPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    GeneralPath path = new GeneralPath();
    Point2D.Double[] points;
    double pathWidth, pathHeight;

    public static BufferedImage generateImage(Vector<EstimatedState> states, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);        
        new PathPreviewPanel(states).paint((Graphics2D)image.getGraphics(), width, height);        
        return image;
    }
    
    public PathPreviewPanel(Vector<EstimatedState> states) {
        
        double minNorth, minEast, maxNorth, maxEast;
        points = new Point2D.Double[states.size()];
        
        EstimatedState first = states.firstElement();
        minNorth = maxNorth = first.getX();
        minEast = maxEast = first.getY();
        
        for (int i = 0; i < states.size(); i++) {
            EstimatedState state = states.get(i);
            points[i] = new Point2D.Double(state.getY(), state.getX());
            if (points[i].x < minEast)
                minEast = points[i].x;
            if (points[i].x > maxEast)
                maxEast = points[i].x;
            
            if (points[i].y < minNorth)
                minNorth = points[i].y;
            if (points[i].y > maxNorth)
                maxNorth = points[i].y;
        }
        
        double meanEast = (maxEast - minEast) / 2 + minEast;
        double meanNorth = (maxNorth - minNorth) / 2 + minNorth;
         
        System.out.println(minEast+", "+maxEast+", "+meanEast+", "+minNorth+", "+maxNorth+", "+meanNorth);
        
        path.moveTo(points[0].x, points[0].y);
        for (int i = 0; i < points.length; i++) {
            path.moveTo(points[i].x - meanEast, points[i].y - meanNorth);
            path.lineTo(points[i].x - meanEast, points[i].y - meanNorth);
        }
        
        pathWidth = (maxEast - minEast) * 1.05;
        pathHeight = (maxNorth - minNorth) * 1.05;
    }
    
    
    public void paint(Graphics2D g, int width, int height) {
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.blue.darker());
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.translate(width/2, height/2);        
        double scaleX = width / pathWidth;
        double scaleY = height / pathHeight;
        double scale = scaleY;
        if (scaleX < scaleY)
            scale = scaleX;
        
        if (scale > 2)
            g.setStroke(new BasicStroke((float)(2/scale)));
        g.scale(scale, -scale);
        
        g.draw(path);        
    }    
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paint((Graphics2D)g, getWidth(), getHeight());
    }
    
}
