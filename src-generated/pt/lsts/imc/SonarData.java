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
 *  IMC Message Sonar Data (276)<br/>
 *  This message contains the data acquired by a single sonar<br/>
 *  measurement.<br/>
 */

public class SonarData extends IMCMessage {

	public enum TYPE {
		SIDESCAN(0),
		ECHOSOUNDER(1),
		MULTIBEAM(2);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 276;

	public SonarData() {
		super(ID_STATIC);
	}

	public SonarData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SonarData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SonarData create(Object... values) {
		SonarData m = new SonarData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SonarData clone(IMCMessage msg) throws Exception {

		SonarData m = new SonarData();
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

	public SonarData(TYPE type, long frequency, int min_range, int max_range, short bits_per_point, float scale_factor, java.util.Collection<BeamConfig> beam_config, byte[] data) {
		super(ID_STATIC);
		setType(type);
		setFrequency(frequency);
		setMinRange(min_range);
		setMaxRange(max_range);
		setBitsPerPoint(bits_per_point);
		setScaleFactor(scale_factor);
		if (beam_config != null)
			setBeamConfig(beam_config);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Type (enumerated)
	 */
	public SonarData setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public SonarData setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public SonarData setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Frequency (hz) - uint32_t
	 */
	public long getFrequency() {
		return getLong("frequency");
	}

	/**
	 *  @param frequency Frequency (hz)
	 */
	public SonarData setFrequency(long frequency) {
		values.put("frequency", frequency);
		return this;
	}

	/**
	 *  @return Minimum Range (m) - uint16_t
	 */
	public int getMinRange() {
		return getInteger("min_range");
	}

	/**
	 *  @param min_range Minimum Range (m)
	 */
	public SonarData setMinRange(int min_range) {
		values.put("min_range", min_range);
		return this;
	}

	/**
	 *  @return Maximum Range (m) - uint16_t
	 */
	public int getMaxRange() {
		return getInteger("max_range");
	}

	/**
	 *  @param max_range Maximum Range (m)
	 */
	public SonarData setMaxRange(int max_range) {
		values.put("max_range", max_range);
		return this;
	}

	/**
	 *  @return Bits Per Data Point (bit) - uint8_t
	 */
	public short getBitsPerPoint() {
		return (short) getInteger("bits_per_point");
	}

	/**
	 *  @param bits_per_point Bits Per Data Point (bit)
	 */
	public SonarData setBitsPerPoint(short bits_per_point) {
		values.put("bits_per_point", bits_per_point);
		return this;
	}

	/**
	 *  @return Scaling Factor - fp32_t
	 */
	public double getScaleFactor() {
		return getDouble("scale_factor");
	}

	/**
	 *  @param scale_factor Scaling Factor
	 */
	public SonarData setScaleFactor(double scale_factor) {
		values.put("scale_factor", scale_factor);
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
	public SonarData setBeamConfig(java.util.Collection<BeamConfig> beam_config) {
		values.put("beam_config", beam_config);
		return this;
	}

	/**
	 *  @return Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Data
	 */
	public SonarData setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
