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
 * $Id::                                                                       $:
 */
package pt.lsts.imc.types;

import java.util.Arrays;
import java.util.Collection;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.util.WGS84Utilities;

public class EstimatedStateAdapter implements IMessageAdapter {

    protected IMCMessage original;
    protected String originalVersion;
    
    protected double latRads, lonRads, depth = Double.NaN, height = Double.NaN, altitude = Double.NaN, phiRads, thetaRads, psiRads, u, v, w, vx, vy, vz,
            pRads, qRads, rRads;
 
    @Override
    public Collection<String> getCompatibleMessages() {
        return Arrays.asList("EstimatedState");
    }
    
    @Override
    public IMCMessage getData(IMCDefinition definitions) {
        if (definitions.getVersion().equals(originalVersion))
            return original;
        boolean preIMC5 = definitions.getVersion().compareTo("5.0.0") < 0;
        
        IMCMessage estate = definitions.create("EstimatedState");
        
        estate.setValue("phi", getPhiRads());
        estate.setValue("theta", getThetaRads());
        estate.setValue("psi", getPsiRads());
        estate.setValue("p", getpRads());
        estate.setValue("q", getqRads());
        estate.setValue("r", getrRads());
        estate.setValue("u", getU());
        estate.setValue("v", getV());
        estate.setValue("w", getW());
        estate.setValue("vx", getVx());
        estate.setValue("vy", getVy());
        estate.setValue("vz", getVz());
        estate.setValue("lat", getLatRads());
        estate.setValue("lon", getLonRads());
        
        if (preIMC5) {
            estate.setValue("ref", "LLD_ONLY");
            estate.setValue("depth", -getHeight());            
        }
        else {
            estate.setValue("height", height);
            
            if (!Double.isNaN(getDepth()))
                estate.setValue("depth", getDepth());
            else
                estate.setValue("depth", -1);
            
            if (!Double.isNaN(getAltitude()))
                estate.setValue("alt", getAltitude());
            else
                estate.setValue("alt", -1);
        }
        return estate;
    }
    
    @Override
    public void setData(IMCMessage msg) {
        original = msg;
        originalVersion = msg.getMessageType().getImcVersion();
        boolean preIMC5 = originalVersion.compareTo("5.0.0") < 0;
        
        setLatRads(msg.getDouble("lat"));
        setLonRads(msg.getDouble("lon"));        
        
        setpRads(msg.getDouble("p"));
        setqRads(msg.getDouble("q"));
        setrRads(msg.getDouble("r"));
        
        setU(msg.getDouble("u"));
        setV(msg.getDouble("v"));
        setW(msg.getDouble("w"));
        
        setVx(msg.getDouble("vx"));
        setVy(msg.getDouble("vy"));
        setVz(msg.getDouble("vz"));
        
        setPhiRads(msg.getDouble("phi"));
        setThetaRads(msg.getDouble("theta"));
        setPsiRads(msg.getDouble("psi"));
        
        if (preIMC5) {
            String refMode = msg.getString("ref");
            double d = msg.getDouble("depth");
            if (d != 0)
                setHeight(-d);
            else
                setHeight(0);
            
            if (refMode.equals("NED_LLD")) {
                double[] translated = WGS84Utilities.WGS84displace(Math.toDegrees(getLatRads()), Math.toDegrees(getLonRads()), 0, msg.getDouble("x"), msg.getDouble("y"), 0);
                setLatRads(Math.toRadians(translated[0]));
                setLonRads(Math.toRadians(translated[1]));                
                setDepth(d+msg.getDouble("z"));
            }
            else
                setDepth(d);                        
        }
        else {
            double[] translated = WGS84Utilities.WGS84displace(Math.toDegrees(getLatRads()), Math.toDegrees(getLonRads()), 0, msg.getDouble("x"), msg.getDouble("y"), 0);
            setLatRads(Math.toRadians(translated[0]));
            setLonRads(Math.toRadians(translated[1]));            
            setHeight(msg.getDouble("height") - msg.getDouble("z"));
            double altitude = msg.getDouble("alt");
            double depth = msg.getDouble("depth");
            if (altitude != -1)
                setAltitude(altitude);
            if (depth != -1)
                setDepth(depth);
        }
    }    

    /**
     * @return the latRads
     */
    public double getLatRads() {
        return latRads;
    }

    /**
     * @param latRads the latRads to set
     */
    public void setLatRads(double latRads) {
        this.latRads = latRads;
    }

    /**
     * @return the lonRads
     */
    public double getLonRads() {
        return lonRads;
    }

    /**
     * @param lonRads the lonRads to set
     */
    public void setLonRads(double lonRads) {
        this.lonRads = lonRads;
    }

    /**
     * @return the depth
     */
    public double getDepth() {
        return depth;
    }

    /**
     * @param depth the depth to set
     */
    public void setDepth(double depth) {
        this.depth = depth;
    }

    /**
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * @return the altitude
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @param altitude the altitude to set
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * @return the phiRads
     */
    public double getPhiRads() {
        return phiRads;
    }

    /**
     * @param phiRads the phiRads to set
     */
    public void setPhiRads(double phiRads) {
        this.phiRads = phiRads;
    }

    /**
     * @return the thetaRads
     */
    public double getThetaRads() {
        return thetaRads;
    }

    /**
     * @param thetaRads the thetaRads to set
     */
    public void setThetaRads(double thetaRads) {
        this.thetaRads = thetaRads;
    }

    /**
     * @return the psiRads
     */
    public double getPsiRads() {
        return psiRads;
    }

    /**
     * @param psiRads the psiRads to set
     */
    public void setPsiRads(double psiRads) {
        this.psiRads = psiRads;
    }

    /**
     * @return the u
     */
    public double getU() {
        return u;
    }

    /**
     * @param u the u to set
     */
    public void setU(double u) {
        this.u = u;
    }

    /**
     * @return the v
     */
    public double getV() {
        return v;
    }

    /**
     * @param v the v to set
     */
    public void setV(double v) {
        this.v = v;
    }

    /**
     * @return the w
     */
    public double getW() {
        return w;
    }

    /**
     * @param w the w to set
     */
    public void setW(double w) {
        this.w = w;
    }

    /**
     * @return the vx
     */
    public double getVx() {
        return vx;
    }

    /**
     * @param vx the vx to set
     */
    public void setVx(double vx) {
        this.vx = vx;
    }

    /**
     * @return the vy
     */
    public double getVy() {
        return vy;
    }

    /**
     * @param vy the vy to set
     */
    public void setVy(double vy) {
        this.vy = vy;
    }

    /**
     * @return the vz
     */
    public double getVz() {
        return vz;
    }

    /**
     * @param vz the vz to set
     */
    public void setVz(double vz) {
        this.vz = vz;
    }

    /**
     * @return the pRads
     */
    public double getpRads() {
        return pRads;
    }

    /**
     * @param pRads the pRads to set
     */
    public void setpRads(double pRads) {
        this.pRads = pRads;
    }

    /**
     * @return the qRads
     */
    public double getqRads() {
        return qRads;
    }

    /**
     * @param qRads the qRads to set
     */
    public void setqRads(double qRads) {
        this.qRads = qRads;
    }

    /**
     * @return the rRads
     */
    public double getrRads() {
        return rRads;
    }

    /**
     * @param rRads the rRads to set
     */
    public void setrRads(double rRads) {
        this.rRads = rRads;
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
    
    public static void main(String[] args) {
        IMCDefinition b = IMCDefinition.getInstance();
        
        EstimatedStateAdapter pose = new EstimatedStateAdapter();
        pose.setLatRads(Math.toRadians(41));
        pose.setLonRads(Math.toRadians(-8));
        pose.setAltitude(40);
        pose.setDepth(32);
        pose.setHeight(57);
        pose.setThetaRads(Math.toRadians(30));
        System.out.println(pose.getData(b));        
    }
}
