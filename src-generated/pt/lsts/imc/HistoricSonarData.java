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
 *  IMC Message Historic Sonar Data (109)<br/>
 *  This message is used to store historic (transmitted afterwards) sonar data.<br/>
 */

public class HistoricSonarData extends IMCMessage {

	public enum ENCODING {
		ONE_BYTE_PER_PIXEL(0),
		PNG(1),
		JPEG(2);

		protected long value;

		public long value() {
			return value;
		}

		ENCODING(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 109;

	public HistoricSonarData() {
		super(ID_STATIC);
	}

	public HistoricSonarData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistoricSonarData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static HistoricSonarData create(Object... values) {
		HistoricSonarData m = new HistoricSonarData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static HistoricSonarData clone(IMCMessage msg) throws Exception {

		HistoricSonarData m = new HistoricSonarData();
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

	public HistoricSonarData(float altitude, float width, float length, float bearing, short pxl, ENCODING encoding, byte[] sonar_data) {
		super(ID_STATIC);
		setAltitude(altitude);
		setWidth(width);
		setLength(length);
		setBearing(bearing);
		setPxl(pxl);
		setEncoding(encoding);
		if (sonar_data != null)
			setSonarData(sonar_data);
	}

	/**
	 *  @return Altitude (m) - fp32_t
	 */
	public double getAltitude() {
		return getDouble("altitude");
	}

	/**
	 *  @param altitude Altitude (m)
	 */
	public HistoricSonarData setAltitude(double altitude) {
		values.put("altitude", altitude);
		return this;
	}

	/**
	 *  @return Width (m) - fp32_t
	 */
	public double getWidth() {
		return getDouble("width");
	}

	/**
	 *  @param width Width (m)
	 */
	public HistoricSonarData setWidth(double width) {
		values.put("width", width);
		return this;
	}

	/**
	 *  @return Length (m) - fp32_t
	 */
	public double getLength() {
		return getDouble("length");
	}

	/**
	 *  @param length Length (m)
	 */
	public HistoricSonarData setLength(double length) {
		values.put("length", length);
		return this;
	}

	/**
	 *  @return Bearing - fp32_t
	 */
	public double getBearing() {
		return getDouble("bearing");
	}

	/**
	 *  @param bearing Bearing
	 */
	public HistoricSonarData setBearing(double bearing) {
		values.put("bearing", bearing);
		return this;
	}

	/**
	 *  @return Pixels Per Line - int16_t
	 */
	public short getPxl() {
		return (short) getInteger("pxl");
	}

	/**
	 *  @param pxl Pixels Per Line
	 */
	public HistoricSonarData setPxl(short pxl) {
		values.put("pxl", pxl);
		return this;
	}

	/**
	 *  @return Encoding (enumerated) - uint8_t
	 */
	public ENCODING getEncoding() {
		try {
			ENCODING o = ENCODING.valueOf(getMessageType().getFieldPossibleValues("encoding").get(getLong("encoding")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getEncodingStr() {
		return getString("encoding");
	}

	public short getEncodingVal() {
		return (short) getInteger("encoding");
	}

	/**
	 *  @param encoding Encoding (enumerated)
	 */
	public HistoricSonarData setEncoding(ENCODING encoding) {
		values.put("encoding", encoding.value());
		return this;
	}

	/**
	 *  @param encoding Encoding (as a String)
	 */
	public HistoricSonarData setEncodingStr(String encoding) {
		setValue("encoding", encoding);
		return this;
	}

	/**
	 *  @param encoding Encoding (integer value)
	 */
	public HistoricSonarData setEncodingVal(short encoding) {
		setValue("encoding", encoding);
		return this;
	}

	/**
	 *  @return SonarData - rawdata
	 */
	public byte[] getSonarData() {
		return getRawData("sonar_data");
	}

	/**
	 *  @param sonar_data SonarData
	 */
	public HistoricSonarData setSonarData(byte[] sonar_data) {
		values.put("sonar_data", sonar_data);
		return this;
	}

}
