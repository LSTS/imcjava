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
 * $Id:: LogParsingExamples.java 392 2013-02-28 17:26:14Z zepinto@gmail.com    $:
 */
package pt.lsts.imc.examples;

import java.io.File;
import java.io.FileInputStream;
import java.util.NoSuchElementException;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.EstimatedStreamVelocity;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndex;

/**
 * This class shows how to open an LSF log and inspect its contents
 * @author zp
 *
 */
public class LogParsingExamples {

    /**
     * This method calculates the measured average current speed from an LSF log file
     * @param index The LsfIndex to be processed
     * @return The average current, in meters per second
     */
    public static double meanStreamSpeed(LsfIndex index) {
        
        // Retrieve a vector with indexes of all messages of type EstimatedStreamVelocity
        double sum = 0;
        int count = 0;
        for (IMCMessage ss : index.messagesOfType("EstimatedStreamVelocity")) {
            try {
                // Retrieve a message at given index, converting it for expected type
                EstimatedStreamVelocity vel = new EstimatedStreamVelocity();
                vel.copyFrom(ss);
                // calculates speed (vector norm)
                double speed = Math.sqrt(vel.getX() * vel.getX() + vel.getY()*vel.getY()+vel.getZ()*vel.getZ());
                sum += speed;
                count++;
            }
            catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        
        // the average is calculated by diving the sum by the number of EstimatedStreamVelocity messages in the log        
        return sum / count;
    }
    
    // crude way to add NE offsets to lat/lon
    protected static double[] latLonAddOffsets(double lat, double lon, double northing, double easting) {

        double[] ret = new double[2];
        
        //Earth’s radius (spherical model)
        double R=6378137;

        //Coordinate offsets in radians
        double dLat = northing/R;
        double dLon = easting/(R*Math.cos(Math.PI*lat/180));

        //OffsetPosition, decimal degrees
        ret[0] = lat + dLat * 180/Math.PI;
        ret[1] = lon + dLon * 180/Math.PI;
        
        return ret;
    }
    
    /**
     * This method finds the deepest estimated state
     * @param index The LsfIndex to be processed
     * @return An array of doubles where:<br/>
     * <ul>
     * <li> arr[0] corresponds to latitude</li>
     * <li> arr[1] corresponds to longitude</li>
     * <li> arr[2] corresponds to depth</li>
     * </ul>
     */
    public static double[] deepestLocation(LsfIndex index) {
        // retrieve indexes of all EstimatedState messages
        Iterable<IMCMessage> states = index.messagesOfType("EstimatedState");
        
        // deepestDepth starts by being first depth
        IMCMessage deepest;
        try {
            deepest = states.iterator().next();
        }
        catch (NoSuchElementException e) {
            return null;
        }
        
        // goes through all messages...
        for (IMCMessage state : states) {
            // retrieves depth and compares it
            double depth = state.getDouble("depth");
            if (depth > deepest.getDouble("depth")) {
                deepest = state;
            }
        }
        
        try {
            // retrieve the deepest estimated state
            EstimatedState estate = new EstimatedState();
            estate.copyFrom(deepest);
           
            double northing = estate.getX();
            double easting = estate.getY();
            double latlon[] = new double[2];
            
            // translates lat / lon to degrees
            latlon[0] = Math.toDegrees(estate.getLat());
            latlon[1] = Math.toDegrees(estate.getLon());
            
            // if the state also has orthogonal offsets, add it to lat/lon
            latlon = latLonAddOffsets(latlon[0], latlon[1], northing, easting);
            
            // we can return the result
            return new double[] {latlon[0], latlon[1], estate.getDepth()};
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;        
    }
    
    
    public static void main(String[] args) {

        try {
            String lsfPath = args[0];
            File lsfFile = new File(lsfPath);
            File defsFile = new File(lsfFile.getParentFile(),"IMC.xml");
            LsfIndex index = new LsfIndex(lsfFile, new IMCDefinition(new FileInputStream(defsFile)));
            System.out.println("Mean stream speed: "+meanStreamSpeed(index)+" m/s");
            double[] deepest = deepestLocation(index);
            
            System.out.println("Deepest location ("+deepest[2]+" m) was at "+deepest[0]+" / "+deepest[1]);
        }
        catch (Exception e) {
            System.err.println("Error: "+e.getMessage()+" / "+e.getCause()+". Please check argument is a valid LSF log.");
        }
    }
}
