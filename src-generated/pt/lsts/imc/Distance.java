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
 *  IMC Message Distance (262)<br/>
 *  Distance measurement detected by the device.<br/>
 */

public class Distance extends IMCMessage {

	public enum VALIDITY {
		INVALID(0),
		VALID(1);

		protected long value;

		public long value() {
			return value;
		}

		VALIDITY(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 262;

	public Distance() {
		super(ID_STATIC);
	}

	public Distance(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Distance(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Distance create(Object... values) {
		Distance m = new Distance();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Distance clone(IMCMessage msg) throws Exception {

		Distance m = new Distance();
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

	public Distance(VALIDITY validity, java.util.Collection<DeviceState> location, java.util.Collection<BeamConfig> beam_config, float value) {
		super(ID_STATIC);
		setValidity(validity);
		if (location != null)
			setLocation(location);
		if (beam_config != null)
			setBeamConfig(beam_config);
		setValue(value);
	}

	/**
	 *  @return Validity (enumerated) - uint8_t
	 */
	public VALIDITY getValidity() {
		try {
			VALIDITY o = VALIDITY.valueOf(getMessageType().getFieldPossibleValues("validity").get(getLong("validity")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getValidityStr() {
		return getString("validity");
	}

	public short getValidityVal() {
		return (short) getInteger("validity");
	}

	/**
	 *  @param validity Validity (enumerated)
	 */
	public Distance setValidity(VALIDITY validity) {
		values.put("validity", validity.value());
		return this;
	}

	/**
	 *  @param validity Validity (as a String)
	 */
	public Distance setValidityStr(String validity) {
		setValue("validity", validity);
		return this;
	}

	/**
	 *  @param validity Validity (integer value)
	 */
	public Distance setValidityVal(short validity) {
		setValue("validity", validity);
		return this;
	}

	/**
	 *  @return Location - message-list
	 */
	public java.util.Vector<DeviceState> getLocation() {
		try {
			return getMessageList("location", DeviceState.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param location Location
	 */
	public Distance setLocation(java.util.Collection<DeviceState> location) {
		values.put("location", location);
		return this;
	}

	/**
	 *  @return Beam Configuration - message-list
	 */
	public java.util.Vector<BeamConfig> getBeamConfig() {
		try {
			return getMessageList("beam_config", BeamConfig.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param beam_config Beam Configuration
	 */
	public Distance setBeamConfig(java.util.Collection<BeamConfig> beam_config) {
		values.put("beam_config", beam_config);
		return this;
	}

	/**
	 *  @return Measured Distance (m) - fp32_t
	 */
	public double getValue() {
		return getDouble("value");
	}

	/**
	 *  @param value Measured Distance (m)
	 */
	public Distance setValue(double value) {
		values.put("value", value);
		return this;
	}

}
