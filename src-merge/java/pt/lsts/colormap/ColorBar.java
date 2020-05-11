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
 * $Id:: ColorBar.java 334 2013-01-02 11:20:47Z zepinto                        $:
 */
package pt.lsts.colormap;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class ColorBar extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int HORIZONTAL_ORIENTATION = 0, VERTICAL_ORIENTATION = 1;
	int orientation = HORIZONTAL_ORIENTATION;
	ColorMap cmap = ColorMapFactory.createGrayScaleColorMap();
	BufferedImage cachedImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	//boolean cached = false;
	
	public ColorBar() {
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	
	public ColorBar(int orientation) {
		this();
		this.orientation = orientation;
	}
	
	public ColorBar(int orientation, ColorMap cmap) {
		this();
		this.orientation = orientation;
		setCmap(cmap);
	}
	
	public void paint(Graphics g) {
		
		if (getWidth() != cachedImage.getWidth() || getHeight() != cachedImage.getHeight()) {
			cachedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g2d = (Graphics2D) cachedImage.getGraphics(); 
			
			if (orientation == HORIZONTAL_ORIENTATION) {
				for (int i = 0; i < cachedImage.getWidth(); i++) {
					double pos = (double)i/(double)cachedImage.getWidth();
					g2d.setColor(cmap.getColor(pos));
					g2d.drawLine(i, 0, i, cachedImage.getHeight());
				}
			}
			
			if (orientation == VERTICAL_ORIENTATION) {
				for (int i = 0; i < cachedImage.getHeight(); i++) {
					//System.out.println(colors.getGraphics().getColor());
					g2d.setColor(cmap.getColor((double)i/(double)cachedImage.getHeight()));
					g2d.drawLine(0, cachedImage.getHeight()-i, cachedImage.getWidth(), cachedImage.getHeight()-i);
				}
			}
		}
		g.drawImage(cachedImage, 0, 0, null);
	}

	public ColorMap getCmap() {
		return cmap;
	}

	public void setCmap(ColorMap cmap) {
		this.cmap = cmap;
	}
	
	}
