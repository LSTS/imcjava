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
 *  IMC Message Get World Coordinates (897)<br/>
 *  Message containing the x, y and z coordinates of object in the real world.<br/>
 */

public class GetWorldCoordinates extends IMCMessage {

	public enum TRACKING {
		FALSE(0),
		TRUE(1);

		protected long value;

		public long value() {
			return value;
		}

		TRACKING(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 897;

	public GetWorldCoordinates() {
		super(ID_STATIC);
	}

	public GetWorldCoordinates(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GetWorldCoordinates(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GetWorldCoordinates create(Object... values) {
		GetWorldCoordinates m = new GetWorldCoordinates();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GetWorldCoordinates clone(IMCMessage msg) throws Exception {

		GetWorldCoordinates m = new GetWorldCoordinates();
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

	public GetWorldCoordinates(TRACKING tracking, double lat, double lon, float x, float y, float z) {
		super(ID_STATIC);
		setTracking(tracking);
		setLat(lat);
		setLon(lon);
		setX(x);
		setY(y);
		setZ(z);
	}

	/**
	 *  @return Tracking (enumerated) - uint8_t
	 */
	public TRACKING getTracking() {
		try {
			TRACKING o = TRACKING.valueOf(getMessageType().getFieldPossibleValues("tracking").get(getLong("tracking")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTrackingStr() {
		return getString("tracking");
	}

	public short getTrackingVal() {
		return (short) getInteger("tracking");
	}

	/**
	 *  @param tracking Tracking (enumerated)
	 */
	public GetWorldCoordinates setTracking(TRACKING tracking) {
		values.put("tracking", tracking.value());
		return this;
	}

	/**
	 *  @param tracking Tracking (as a String)
	 */
	public GetWorldCoordinates setTrackingStr(String tracking) {
		setValue("tracking", tracking);
		return this;
	}

	/**
	 *  @param tracking Tracking (integer value)
	 */
	public GetWorldCoordinates setTrackingVal(short tracking) {
		setValue("tracking", tracking);
		return this;
	}

	/**
	 *  @return Latitude (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (rad)
	 */
	public GetWorldCoordinates setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (rad)
	 */
	public GetWorldCoordinates setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return X (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x X (m)
	 */
	public GetWorldCoordinates setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Y (m)
	 */
	public GetWorldCoordinates setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z (m)
	 */
	public GetWorldCoordinates setZ(double z) {
		values.put("z", z);
		return this;
	}

}
