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
 */
package pt.lsts.imc;

/**
 *  IMC Message Operational Limits (504)<br/>
 *  Definition of operational limits.<br/>
 */

public class OperationalLimits extends IMCMessage {

	public static final short OPL_MAX_DEPTH = 0x01;
	public static final short OPL_MIN_ALT = 0x02;
	public static final short OPL_MAX_ALT = 0x04;
	public static final short OPL_MIN_SPEED = 0x08;
	public static final short OPL_MAX_SPEED = 0x10;
	public static final short OPL_MAX_VRATE = 0x20;
	public static final short OPL_AREA = 0x40;

	public static final int ID_STATIC = 504;

	public OperationalLimits() {
		super(ID_STATIC);
	}

	public OperationalLimits(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OperationalLimits(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static OperationalLimits create(Object... values) {
		OperationalLimits m = new OperationalLimits();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static OperationalLimits clone(IMCMessage msg) throws Exception {

		OperationalLimits m = new OperationalLimits();
		if (msg == null)
			return m;
		if(msg.definitions != m.definitions){
			msg = msg.cloneMessage();
			IMCUtil.updateMessage(msg, m.definitions);
		}
		else if (msg.getMgid()!=m.getMgid())
			throw new Exception("Argument "+msg.getAbbrev()+" is incompatible with message "+m.getAbbrev());

		m.getHeader().values.putAll(msg.getHeader().values);
		m.values.putAll(msg.values);
		return m;
	}

	public OperationalLimits(short mask, float max_depth, float min_altitude, float max_altitude, float min_speed, float max_speed, float max_vrate, double lat, double lon, float orientation, float width, float length) {
		super(ID_STATIC);
		setMask(mask);
		setMaxDepth(max_depth);
		setMinAltitude(min_altitude);
		setMaxAltitude(max_altitude);
		setMinSpeed(min_speed);
		setMaxSpeed(max_speed);
		setMaxVrate(max_vrate);
		setLat(lat);
		setLon(lon);
		setOrientation(orientation);
		setWidth(width);
		setLength(length);
	}

	/**
	 *  @return Field Indicator Mask (bitfield) - uint8_t
	 */
	public short getMask() {
		return (short) getInteger("mask");
	}

	/**
	 *  @param mask Field Indicator Mask (bitfield)
	 */
	public OperationalLimits setMask(short mask) {
		values.put("mask", mask);
		return this;
	}

	/**
	 *  @return Maximum Depth (m) - fp32_t
	 */
	public double getMaxDepth() {
		return getDouble("max_depth");
	}

	/**
	 *  @param max_depth Maximum Depth (m)
	 */
	public OperationalLimits setMaxDepth(double max_depth) {
		values.put("max_depth", max_depth);
		return this;
	}

	/**
	 *  @return Minimum Altitude (m) - fp32_t
	 */
	public double getMinAltitude() {
		return getDouble("min_altitude");
	}

	/**
	 *  @param min_altitude Minimum Altitude (m)
	 */
	public OperationalLimits setMinAltitude(double min_altitude) {
		values.put("min_altitude", min_altitude);
		return this;
	}

	/**
	 *  @return Maximum Altitude (m) - fp32_t
	 */
	public double getMaxAltitude() {
		return getDouble("max_altitude");
	}

	/**
	 *  @param max_altitude Maximum Altitude (m)
	 */
	public OperationalLimits setMaxAltitude(double max_altitude) {
		values.put("max_altitude", max_altitude);
		return this;
	}

	/**
	 *  @return Minimum Speed (m/s) - fp32_t
	 */
	public double getMinSpeed() {
		return getDouble("min_speed");
	}

	/**
	 *  @param min_speed Minimum Speed (m/s)
	 */
	public OperationalLimits setMinSpeed(double min_speed) {
		values.put("min_speed", min_speed);
		return this;
	}

	/**
	 *  @return Maximum Speed (m/s) - fp32_t
	 */
	public double getMaxSpeed() {
		return getDouble("max_speed");
	}

	/**
	 *  @param max_speed Maximum Speed (m/s)
	 */
	public OperationalLimits setMaxSpeed(double max_speed) {
		values.put("max_speed", max_speed);
		return this;
	}

	/**
	 *  @return Maximum Vertical Rate (m/s) - fp32_t
	 */
	public double getMaxVrate() {
		return getDouble("max_vrate");
	}

	/**
	 *  @param max_vrate Maximum Vertical Rate (m/s)
	 */
	public OperationalLimits setMaxVrate(double max_vrate) {
		values.put("max_vrate", max_vrate);
		return this;
	}

	/**
	 *  @return Area -- WGS-84 Latitude (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Area -- WGS-84 Latitude (rad)
	 */
	public OperationalLimits setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Area -- WGS-84 Longitude (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Area -- WGS-84 Longitude (rad)
	 */
	public OperationalLimits setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Area -- Orientation (rad) - fp32_t
	 */
	public double getOrientation() {
		return getDouble("orientation");
	}

	/**
	 *  @param orientation Area -- Orientation (rad)
	 */
	public OperationalLimits setOrientation(double orientation) {
		values.put("orientation", orientation);
		return this;
	}

	/**
	 *  @return Area -- Width (m) - fp32_t
	 */
	public double getWidth() {
		return getDouble("width");
	}

	/**
	 *  @param width Area -- Width (m)
	 */
	public OperationalLimits setWidth(double width) {
		values.put("width", width);
		return this;
	}

	/**
	 *  @return Area -- Length (m) - fp32_t
	 */
	public double getLength() {
		return getDouble("length");
	}

	/**
	 *  @param length Area -- Length (m)
	 */
	public OperationalLimits setLength(double length) {
		values.put("length", length);
		return this;
	}

}
