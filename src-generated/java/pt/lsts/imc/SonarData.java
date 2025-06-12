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
 *  IMC Message Sonar Data (276)<br/>
 *  This message contains the data acquired by a single sonar<br/>
 *  measurement. The following describes the format used to<br/>
 *  fill the data field used in this message. (Byte order is<br/>
 *  little endian.)<br/>
 *  <code>*Sidescan:*</code><br/>
 *  +------+-------------------+-----------+<br/>
 *  | Data | Name              | Type      |<br/>
 *  +======+===================+===========+<br/>
 *  | A    | Ranges data       |   uintX_t |<br/>
 *  +------+-------------------+-----------+<br/>
 *  <code> The type *uintX_t</code> will depend on the number of bits per unit, and it should be a multiple of 8.<br/>
 *  * Furthermore, for now, 32 bits is the highest value of bits per unit supported.<br/>
 *  <code>*Multibeam:*</code><br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | Index| Section| Name                    | Type    | Comments                                                             |<br/>
 *  +======+========+=========================+=========+======================================================================+<br/>
 *  | 1    | H1     | Number of points        | uint16_t| Number of data points                                                |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 2    | H2     | Start angle             | fp32_t  | In radians                                                           |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 3    | H3     | Flags                   | uint8_t | Refer to next table                                                  |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 4    | H4 ?   | Angle scale factor      | fp32_t  | Used for angle steps in radians                                      |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 5    | H5 ?   | Intensities scale factor| fp32_t  |                                                                      |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 6    | D1 ?   | Angle steps[H1]         | uint16_t| Values in radians                                                    |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 7    | D2     | Ranges[H1]              | uintX_t | Ranges data points (scale factor from common field "Scaling Factor") |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  | 8    | D3 ?   | Intensities[H1]         | uintX_t | Intensities data points                                              |<br/>
 *  +------+--------+-------------------------+---------+----------------------------------------------------------------------+<br/>
 *  +--------+------------------+-----+<br/>
 *  | Section| Flag Label       | Bit |<br/>
 *  +========+==================+=====+<br/>
 *  | H3.1   | Intensities flag | 0   |<br/>
 *  +--------+------------------+-----+<br/>
 *  | H3.2   | Angle step flag  | 1   |<br/>
 *  +--------+------------------+-----+<br/>
 *  <code>Notes:</code><br/>
 *  <code> Each angle at step *i</code> can be calculated is defined by:<br/>
 *  .. code-block:: python<br/>
 *  angle[i] = H2_start_angle + (32-bit sum of D1_angle_step[0] through D1_angle_step[i]) * H4_scaling_factor<br/>
 *  * If bit H3.1 is not set then sections H5 and D3 won't exist.<br/>
 *  * If bit H3.2 is not set then sections H4 and D1 won't exist. In case this bit is set, then the angle steps is read from field "Beam Width" from "Beam Configuration".<br/>
 *  <code> The type *uintX_t</code> will depend on the number of bits per unit, and it should be a multiple of 8.<br/>
 *  * Furthermore, for now, 32 bits is the highest value of bits per unit supported.<br/>
 *  <code>How to write ranges and intensities data:</code><br/>
 *  .. code-block:: python<br/>
 *  :linenos:<br/>
 *  data_unit = (Integer) (data_value / scale_factor);<br/>
 *  bytes_per_unit = bits_per_unit / 8;<br/>
 *  LOOP: i = 0, until i = bytes_per_unit<br/>
 *  byte[i] = (data_unit >> 8 * i) & 0xFF);<br/>
 *  write(byte);<br/>
 *  <code>*Common:*</code><br/>
 */

public class SonarData extends IMCMessage {

	public enum TYPE {
		SIDESCAN(0),
		ECHOSOUNDER(1),
		MULTIBEAM(2),
		PENCILBEAM(3);

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
	 *  @return Frequency (Hz) - uint32_t
	 */
	public long getFrequency() {
		return getLong("frequency");
	}

	/**
	 *  @param frequency Frequency (Hz)
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
