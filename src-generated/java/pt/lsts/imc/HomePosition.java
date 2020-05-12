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
 */
package pt.lsts.imc;


/**
 *  IMC Message Home Position (909)<br/>
 *  Vehicle Home Position.<br/>
 */

public class HomePosition extends IMCMessage {

	public enum OP {
		SET(1),
		REPORT(2);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 909;

	public HomePosition() {
		super(ID_STATIC);
	}

	public HomePosition(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HomePosition(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static HomePosition create(Object... values) {
		HomePosition m = new HomePosition();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static HomePosition clone(IMCMessage msg) throws Exception {

		HomePosition m = new HomePosition();
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

	public HomePosition(OP op, double lat, double lon, float height, float depth, float alt) {
		super(ID_STATIC);
		setOp(op);
		setLat(lat);
		setLon(lon);
		setHeight(height);
		setDepth(depth);
		setAlt(alt);
	}

	/**
	 *  @return Action on the vehicle home position (enumerated) - uint8_t
	 */
	public OP getOp() {
		try {
			OP o = OP.valueOf(getMessageType().getFieldPossibleValues("op").get(getLong("op")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getOpStr() {
		return getString("op");
	}

	public short getOpVal() {
		return (short) getInteger("op");
	}

	/**
	 *  @param op Action on the vehicle home position (enumerated)
	 */
	public HomePosition setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Action on the vehicle home position (as a String)
	 */
	public HomePosition setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Action on the vehicle home position (integer value)
	 */
	public HomePosition setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Latitude (WGS-84) (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (WGS-84) (rad)
	 */
	public HomePosition setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (WGS-84) (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (WGS-84) (rad)
	 */
	public HomePosition setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Height (WGS-84) (m) - fp32_t
	 */
	public double getHeight() {
		return getDouble("height");
	}

	/**
	 *  @param height Height (WGS-84) (m)
	 */
	public HomePosition setHeight(double height) {
		values.put("height", height);
		return this;
	}

	/**
	 *  @return Depth (m) - fp32_t
	 */
	public double getDepth() {
		return getDouble("depth");
	}

	/**
	 *  @param depth Depth (m)
	 */
	public HomePosition setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

	/**
	 *  @return Altitude (m) - fp32_t
	 */
	public double getAlt() {
		return getDouble("alt");
	}

	/**
	 *  @param alt Altitude (m)
	 */
	public HomePosition setAlt(double alt) {
		values.put("alt", alt);
		return this;
	}

}
