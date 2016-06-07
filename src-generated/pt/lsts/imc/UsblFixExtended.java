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
 *  IMC Message USBL Fix Extended (900)<br/>
 *  This message contains the WGS-84 position of a target computed using<br/>
 *  USBL.<br/>
 */

public class UsblFixExtended extends IMCMessage {

	public enum Z_UNITS {
		NONE(0),
		DEPTH(1),
		ALTITUDE(2),
		HEIGHT(3);

		protected long value;

		public long value() {
			return value;
		}

		Z_UNITS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 900;

	public UsblFixExtended() {
		super(ID_STATIC);
	}

	public UsblFixExtended(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UsblFixExtended(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UsblFixExtended create(Object... values) {
		UsblFixExtended m = new UsblFixExtended();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UsblFixExtended clone(IMCMessage msg) throws Exception {

		UsblFixExtended m = new UsblFixExtended();
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

	public UsblFixExtended(String target, double lat, double lon, Z_UNITS z_units, float z, float accuracy) {
		super(ID_STATIC);
		if (target != null)
			setTarget(target);
		setLat(lat);
		setLon(lon);
		setZUnits(z_units);
		setZ(z);
		setAccuracy(accuracy);
	}

	/**
	 *  @return Target - plaintext
	 */
	public String getTarget() {
		return getString("target");
	}

	/**
	 *  @param target Target
	 */
	public UsblFixExtended setTarget(String target) {
		values.put("target", target);
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
	public UsblFixExtended setLat(double lat) {
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
	public UsblFixExtended setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Z Units (enumerated) - uint8_t
	 */
	public Z_UNITS getZUnits() {
		try {
			Z_UNITS o = Z_UNITS.valueOf(getMessageType().getFieldPossibleValues("z_units").get(getLong("z_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getZUnitsStr() {
		return getString("z_units");
	}

	public short getZUnitsVal() {
		return (short) getInteger("z_units");
	}

	/**
	 *  @param z_units Z Units (enumerated)
	 */
	public UsblFixExtended setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public UsblFixExtended setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public UsblFixExtended setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @return Z Reference (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z Reference (m)
	 */
	public UsblFixExtended setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Accuracy (m) - fp32_t
	 */
	public double getAccuracy() {
		return getDouble("accuracy");
	}

	/**
	 *  @param accuracy Accuracy (m)
	 */
	public UsblFixExtended setAccuracy(double accuracy) {
		values.put("accuracy", accuracy);
		return this;
	}

}
