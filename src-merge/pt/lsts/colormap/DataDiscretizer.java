/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: DataDiscretizer.java 334 2013-01-02 11:20:47Z zepinto                 $:
 */
package pt.lsts.colormap;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 * @author ZP
 */
public class DataDiscretizer {

	private LinkedHashMap<String, DataPoint> points = new LinkedHashMap<String, DataPoint>();
	private GeneralPath cHullShape = new GeneralPath();
	public double maxX = Double.NaN, maxY = Double.NaN, minX = Double.NaN, minY = Double.NaN;
	public double minVal[] = null, maxVal[] = null;
	private int cellWidth = 5;
    private ArrayList<Point> chull = new ArrayList<Point>();;
	
	public DataDiscretizer(int cellWidth) {
		this.cellWidth = cellWidth;		
	}
	
	public void addPoint(Number x, Number y, Number value) {
		addPoint(x.doubleValue(), y.doubleValue(), value.doubleValue());
	}
	
	public void addPoint(Number x, Number y, Number[] values) {
		double[] vals = new double[values.length];
		for (int i = 0; i < vals.length; i++)
			vals[i] = values[i].doubleValue();
		addPoint(x.doubleValue(), y.doubleValue(), vals);
	}
	
	public void addPoint(double x, double y, double[] values) {
		if (Double.isNaN(maxX)) {
			maxX = minX = x;
			maxY = minY = y;
			minVal = new double[values.length];
			maxVal = new double[values.length];
			for (int i = 0; i < values.length; i++)
				minVal[i] = maxVal[i] = values[i];					
		}
		else {
			if (x > maxX) maxX = x;
			if (x < minX) minX = x;
			if (y > maxY) maxY = y;
			if (y < minY) minY = y;
			for (int i = 0; i < minVal.length; i++) {
				if (values[i] < minVal[i]) minVal[i] = values[i];
				if (values[i] > maxVal[i]) maxVal[i] = values[i];
			}
		}
		
		int x_ =  (int)(Math.floor(x/cellWidth)*cellWidth);
		int y_ =  (int)(Math.floor(y/cellWidth)*cellWidth);
		
		String id = x_+","+y_;
		
		if (points.containsKey(id))
			points.get(id).addValue(values);
		else
			points.put(id, new DataPoint(x_, y_, values));	
		
		ArrayList<Point> array = new ArrayList<Point>();
		for (DataPoint dp : points.values()) {
            array.add(new Point(dp.x, dp.y));
        }
        Collections.sort(array, new Comparator<Point>() {
            public int compare(Point pt1, Point pt2) {
                int r = pt1.x - pt2.x;
                if (r != 0)
                    return r;
                else
                    return pt1.y - pt2.y;
            }
        });
//        if (points.size() > 3) {
//            chull = CHull.cHull(array);
//            cHullShape.reset();
//            for (int i = 0; i < chull.size(); i++) {
//                if (i == 0)
//                    cHullShape.moveTo(chull.get(i).x, chull.get(i).y);
//                else
//                    cHullShape.lineTo(chull.get(i).x, chull.get(i).y);
//            }
//            cHullShape.closePath();
//        }
	}
	
	public void addPoint(double x, double y, double value) {
		addPoint(x, y, new double[] {value});
	}
	
	public DataPoint[] getDataPoints(int minValues) {		
		Vector<DataPoint> dps = new Vector<DataPoint>();
		
		for (DataPoint dp : points.values()) {
			if (dp.numValues >= minValues)
				dps.add(dp);
		}		
		return dps.toArray(new DataPoint[0]);
	}
	
	public DataPoint[] getDataPoints() {
		return points.values().toArray(new DataPoint[0]);
	}
	
	public class DataPoint {
		private int x, y, numValues;
		double sum[];
		public DataPoint(int x, int y) {
			this.x = x; this.y = y;
		}
		
		public DataPoint(int x, int y, double value) {
			this(x,y); addValue(value);
		}
		
		public DataPoint(int x, int y, double[] values) {
			this(x,y); addValue(values);
		}
		
		public void addValue(double value) {
			addValue(new double[]{value});
		}
		
		public void addValue(double values[]) {
			if (numValues == 0)
				sum = new double[values.length];
			numValues++;
			for (int i = 0; i < values.length; i++)
				sum[i] += values[i];
		}
		
		public double getValue() {
			return (numValues > 0)? sum[0]/(double)numValues : Double.NaN; 
		}
		
		public double[] getValues() {
			double[] ret = new double[sum.length];
			for (int i = 0; i < sum.length; i++)
				ret[i] = (numValues > 0)? sum[i]/(double)numValues : Double.NaN;
				
			return ret; 
		}
		
		public Point2D getPoint2D() {
			return new Point2D.Double(x,y);
		}
		
		@Override
		public String toString() {
			return "("+x+","+y+")="+getValue();
		}
	}
	
	/**
     * @return the cHullShape
     */
    public GeneralPath getCHullShape() {
        return cHullShape;
    }

    /**
     * @return the chull
     */
    public ArrayList<Point> getChull() {
        return chull;
    }
    

}

