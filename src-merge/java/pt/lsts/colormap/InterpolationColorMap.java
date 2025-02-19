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
 * $Id:: InterpolationColorMap.java 334 2013-01-02 11:20:47Z zepinto           $:
 */
package pt.lsts.colormap;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;

public class InterpolationColorMap implements ColorMap {

    protected double[] values = new double[] {0f, 1f};
    protected Color[] colors = new Color[] {Color.BLACK, Color.WHITE};
    protected boolean debug = false;
    protected String name;

    public InterpolationColorMap(double[] values, Color[] colors) {
        this("Unknown", values, colors);
    }

    public InterpolationColorMap(String name, double[] values, Color[] colors) {
        this.name = name;
        if (values.length != colors.length) {
            System.err.println("The values[] and colors[] sizes don't match!");
            return;
        }
        this.values = values;
        this.colors = colors;
        
    }
    @Override
    public String toString() {
        return name;
    }

    public InterpolationColorMap(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String line;
        Vector<Color> colorsV = new Vector<Color>();

        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == '#')
                continue;

            String[] parts = line.split("[ \t,]+");

            if (parts.length < 3)
                continue;
            int r = (int)(Double.parseDouble(parts[0])*255);
            int g = (int)(Double.parseDouble(parts[1])*255);
            int b = (int)(Double.parseDouble(parts[2])*255);

            colorsV.add(new Color(r,g,b));
        }
        
        this.colors = colorsV.toArray(new Color[0]);        
        this.values = new double[colorsV.size()];
        for (int i = 0; i < values.length; i++)     
            values[i] = (double)i/(double)(values.length-1);        
    }	    

    public InterpolationColorMap(File file) throws FileNotFoundException, IOException {
        this (new FileReader(file));

    }

    public InterpolationColorMap(String filename) throws FileNotFoundException, IOException {
        this(new File(filename));

    }

    public static void main(String args[]) throws Exception {
        ColorMap cmap = new InterpolationColorMap("c:/cmap.txt");
        System.out.println(cmap.getColor(0.1f));
    }

    public Color getColor(double value) {	
        if (debug)
            System.out.println("getColor()");
        
        if (value >= values[values.length-1])
            return colors[values.length-1];
        
        if (value <= values[0])
            return colors[0];
        
        value = Math.min(value, values[values.length-1]);
        value = Math.max(value, values[0]);

        int pos = 0;
        while (pos < values.length && value > values[pos])
            pos++;

        
        if (pos == 0) 
            return colors[0];
        else if (pos == values.length)
            return colors[colors.length-1];
        else {
            return interpolate(values[pos-1], colors[pos-1], value, values[pos], colors[pos]);
        }
    }

    private Color interpolate(double belowValue, Color belowColor, double value, double aboveValue, Color aboveColor) {

        if (debug)
            System.out.println("interpolate()");

        double totalDist = aboveValue - belowValue;

        double aboveDist = (value - belowValue) / totalDist;
        double belowDist = (aboveValue - value) / totalDist;

        if (debug)
            System.out.println("aboveDist="+aboveDist+", belowDist="+belowDist);

        return new Color(
                (int) (belowColor.getRed() * belowDist + aboveColor.getRed() * aboveDist),
                (int) (belowColor.getGreen() * belowDist + aboveColor.getGreen() * aboveDist),
                (int) (belowColor.getBlue() * belowDist + aboveColor.getBlue() * aboveDist)
                );
    } 

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the values
     */
    public double[] getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(double[] values) {
        this.values = values;
    }

    /**
     * @return the colors
     */
    public Color[] getColors() {
        return colors;
    }

    /**
     * @param colors the colors to set
     */
    public void setColors(Color[] colors) {
        this.colors = colors;
    }
}
