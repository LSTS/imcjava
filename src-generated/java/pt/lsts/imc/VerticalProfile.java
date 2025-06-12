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
 */
package pt.lsts.imc;


/**
 *  IMC Message Vertical Profile (111)<br/>
 *  This message is used to store historic profiles for water parameters: Temperature, Salinity, Chlorophyll...<br/>
 */

public class VerticalProfile extends IMCMessage {

	public enum PARAMETER {
		TEMPERATURE(0),
		SALINITY(1),
		CONDUCTIVITY(2),
		PH(3),
		REDOX(4),
		CHLOROPHYLL(5),
		TURBIDITY(6),
		CURRENT_VELOCITY_U(7),
		CURRENT_VELOCITY_V(8),
		ABSOLUTE_WIND_AVG(9),
		ABSOLUTE_WIND_MAX(10),
		DISS_ORGANIC_MATTER(11),
		DISS_OXYGEN(12);

		protected long value;

		public long value() {
			return value;
		}

		PARAMETER(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 111;

	public VerticalProfile() {
		super(ID_STATIC);
	}

	public VerticalProfile(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VerticalProfile(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VerticalProfile create(Object... values) {
		VerticalProfile m = new VerticalProfile();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VerticalProfile clone(IMCMessage msg) throws Exception {

		VerticalProfile m = new VerticalProfile();
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

	public VerticalProfile(PARAMETER parameter, short numSamples, java.util.Collection<ProfileSample> samples, double lat, double lon) {
		super(ID_STATIC);
		setParameter(parameter);
		setNumSamples(numSamples);
		if (samples != null)
			setSamples(samples);
		setLat(lat);
		setLon(lon);
	}

	/**
	 *  @return Parameter (enumerated) - uint8_t
	 */
	public PARAMETER getParameter() {
		try {
			PARAMETER o = PARAMETER.valueOf(getMessageType().getFieldPossibleValues("parameter").get(getLong("parameter")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getParameterStr() {
		return getString("parameter");
	}

	public short getParameterVal() {
		return (short) getInteger("parameter");
	}

	/**
	 *  @param parameter Parameter (enumerated)
	 */
	public VerticalProfile setParameter(PARAMETER parameter) {
		values.put("parameter", parameter.value());
		return this;
	}

	/**
	 *  @param parameter Parameter (as a String)
	 */
	public VerticalProfile setParameterStr(String parameter) {
		setValue("parameter", parameter);
		return this;
	}

	/**
	 *  @param parameter Parameter (integer value)
	 */
	public VerticalProfile setParameterVal(short parameter) {
		setValue("parameter", parameter);
		return this;
	}

	/**
	 *  @return Number of Samples - uint8_t
	 */
	public short getNumSamples() {
		return (short) getInteger("numSamples");
	}

	/**
	 *  @param numSamples Number of Samples
	 */
	public VerticalProfile setNumSamples(short numSamples) {
		values.put("numSamples", numSamples);
		return this;
	}

	/**
	 *  @return Samples - message-list
	 */
	public java.util.Vector<ProfileSample> getSamples() {
		try {
			return getMessageList("samples", ProfileSample.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param samples Samples
	 */
	public VerticalProfile setSamples(java.util.Collection<ProfileSample> samples) {
		values.put("samples", samples);
		return this;
	}

	/**
	 *  @return Latitude (°) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (°)
	 */
	public VerticalProfile setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (°) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (°)
	 */
	public VerticalProfile setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

}
