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
package pt.lsts.util;

import pt.lsts.imc.EstimatedState;


public class WGS84Utilities {

    public static final double c_wgs84_a = 6378137.0;
    public static final double c_wgs84_e2 = 0.00669437999013;
    public static final double c_wgs84_f = 0.0033528106647475;

    
    // ---------------------------------------------------------------------------------------------
    /**
     * Copied from Dune
     * 
     * @param loc
     * @return
     */
    private static double[] toECEF(double latDegrees, double lonDegrees, double depth) {

        double lld[] = {latDegrees, lonDegrees, depth};

        lld[0] = Math.toRadians(lld[0]);
        lld[1] = Math.toRadians(lld[1]);

        double cos_lat = Math.cos(lld[0]);
        double sin_lat = Math.sin(lld[0]);
        double cos_lon = Math.cos(lld[1]);
        double sin_lon = Math.sin(lld[1]);
        double rn = c_wgs84_a / Math.sqrt(1.0 - c_wgs84_e2 * sin_lat * sin_lat);
        double[] ned = new double[3];
        ned[0] = (rn - lld[2]) * cos_lat * cos_lon;
        ned[1] = (rn - lld[2]) * cos_lat * sin_lon;
        ned[2] = (((1.0 - c_wgs84_e2) * rn) - lld[2]) * sin_lat;

        return ned;
    }

    /**
     * Copied from Dune
     * 
     * @param lat
     * @return
     */
    private static double n_rad(double lat) {
        double lat_sin = Math.sin(lat);
        return c_wgs84_a / Math.sqrt(1 - c_wgs84_e2 * (lat_sin * lat_sin));
    }

    /**
     * Copied from Dune
     * 
     * @param x
     * @param y
     * @param z
     * @return
     */
    private static double[] toGeodetic(double x, double y, double z) {
        double[] lld = new double[3];

        double p = Math.sqrt(x * x + y * y);
        lld[1] = Math.atan2(y, x);
        lld[0] = Math.atan2(z / p, 0.01);
        double n = n_rad(lld[0]);
        lld[2] = p / Math.cos(lld[0]) - n;
        double old_hae = -1e-9;
        double num = z / p;

        while (Math.abs(lld[2] - old_hae) > 1e-4) {
            old_hae = lld[2];
            double den = 1 - c_wgs84_e2 * n / (n + lld[2]);
            lld[0] = Math.atan2(num, den);
            n = n_rad(lld[0]);
            lld[2] = p / Math.cos(lld[0]) - n;
        }

        lld[0] = Math.toDegrees(lld[0]);
        lld[1] = Math.toDegrees(lld[1]);

        return lld;
    }
    
    /**
     * Copied from Dune
     * 
     * @param loc1
     * @param loc2
     * @return
     */
    public static double[] WGS84displacement(double latDegrees1, double lonDegrees1, double depth1,
            double latDegrees2, double lonDegrees2, double depth2) {

        double cs1[];
        double cs2[];

        cs1 = toECEF(latDegrees1, lonDegrees1, depth1);
        cs2 = toECEF(latDegrees2, lonDegrees2, depth2);

        double ox = cs2[0] - cs1[0];
        double oy = cs2[1] - cs1[1];
        double oz = cs2[2] - cs1[2];
        double[] lld1 = { latDegrees1, lonDegrees1, depth1 };

        double slat = Math.sin(Math.toRadians(lld1[0]));
        double clat = Math.cos(Math.toRadians(lld1[0]));
        double slon = Math.sin(Math.toRadians(lld1[1]));
        double clon = Math.cos(Math.toRadians(lld1[1]));

        double[] ret = new double[3];

        ret[0] = -slat * clon * ox - slat * slon * oy + clat * oz; // North
        ret[1] = -slon * ox + clon * oy; // East
        ret[2] = depth1 - depth2;

        return ret;
    }
    
    /**
     * Copied from Dune
     * 
     * @param loc
     * @param n
     * @param e
     * @param d
     */
    public static double[] WGS84displace(double latDegrees, double lonDegrees, double depth, double n, double e, double d) {
        // Convert reference to ECEF coordinates
        double xyz[] = toECEF(latDegrees, lonDegrees, depth);
        double lld[] = {latDegrees, lonDegrees, depth };
        // Compute Geocentric latitude
        double phi = Math.atan2(xyz[2], Math.sqrt(xyz[0] * xyz[0] + xyz[1] * xyz[1]));

        // Compute all needed sine and cosine terms for conversion.
        double slon = Math.sin(Math.toRadians(lld[1]));
        double clon = Math.cos(Math.toRadians(lld[1]));
        double sphi = Math.sin(phi);
        double cphi = Math.cos(phi);

        // Obtain ECEF coordinates of displaced point
        // Note: some signs from standard ENU formula
        // are inverted - we are working with NED (= END) coordinates
        xyz[0] += -slon * e - clon * sphi * n - clon * cphi * d;
        xyz[1] += clon * e - slon * sphi * n - slon * cphi * d;
        xyz[2] += cphi * n - sphi * d;

        // Convert back to WGS-84 coordinates
        lld = toGeodetic(xyz[0], xyz[1], xyz[2]);
//        LocationType loc2 = new LocationType();
//        loc2.setLatitude(lld[0]);
//        loc2.setLongitude(lld[1]);
//        loc2.setDepth(lld[2]);
//        loc.setLocation(loc2);
        
        if (d != 0d)
            lld[2] = depth + d;
        else
            lld[2] = depth;
        return lld;
    }
    
    public static double distance(double latDegrees1, double lonDegrees1, double latDegrees2, double lonDegrees2) {
    	double[] offsets = WGS84displacement(latDegrees1, lonDegrees1, 0, latDegrees2, lonDegrees2, 0);
    	return Math.hypot(offsets[0],offsets[1]);
    }
    
    public static double[] toLatLonDepth(EstimatedState msg) {
    	if (msg == null)
    		return null;
    	return WGS84displace(Math.toDegrees(msg.getLat()), Math.toDegrees(msg.getLon()), msg.getDepth(), msg.getX(), msg.getY(), 0);
    }
}
