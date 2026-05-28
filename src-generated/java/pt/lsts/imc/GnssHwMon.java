/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message GNSS Hardware Monitor (2024)<br/>
 *  GNSS debug information<br/>
 */

public class GnssHwMon extends IMCMessage {

	public enum JAM_STAT {
		UNKNOWN(0),
		OK(1),
		WAR(2),
		CRIT(3);

		protected long value;

		public long value() {
			return value;
		}

		JAM_STAT(long value) {
			this.value = value;
		}
	}

	public enum ANT_STAT {
		INIT(0),
		UNKNOWN(1),
		OK(2),
		SHORT(3),
		OPEN(4);

		protected long value;

		public long value() {
			return value;
		}

		ANT_STAT(long value) {
			this.value = value;
		}
	}

	public enum ANT_PWR {
		OFF(0),
		ON(1),
		UNKNOWN(2);

		protected long value;

		public long value() {
			return value;
		}

		ANT_PWR(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 2024;

	public GnssHwMon() {
		super(ID_STATIC);
	}

	public GnssHwMon(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GnssHwMon(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GnssHwMon create(Object... values) {
		GnssHwMon m = new GnssHwMon();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GnssHwMon clone(IMCMessage msg) throws Exception {

		GnssHwMon m = new GnssHwMon();
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

	public GnssHwMon(short jamming_prob, JAM_STAT jam_stat, int rf_noise, ANT_STAT ant_stat, ANT_PWR ant_pwr) {
		super(ID_STATIC);
		setJammingProb(jamming_prob);
		setJamStat(jam_stat);
		setRfNoise(rf_noise);
		setAntStat(ant_stat);
		setAntPwr(ant_pwr);
	}

	/**
	 *  @return Jamming Probability (%) - uint8_t
	 */
	public short getJammingProb() {
		return (short) getInteger("jamming_prob");
	}

	/**
	 *  @param jamming_prob Jamming Probability (%)
	 */
	public GnssHwMon setJammingProb(short jamming_prob) {
		values.put("jamming_prob", jamming_prob);
		return this;
	}

	/**
	 *  @return Jamming Status (enumerated) - uint8_t
	 */
	public JAM_STAT getJamStat() {
		try {
			JAM_STAT o = JAM_STAT.valueOf(getMessageType().getFieldPossibleValues("jam_stat").get(getLong("jam_stat")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getJamStatStr() {
		return getString("jam_stat");
	}

	public short getJamStatVal() {
		return (short) getInteger("jam_stat");
	}

	/**
	 *  @param jam_stat Jamming Status (enumerated)
	 */
	public GnssHwMon setJamStat(JAM_STAT jam_stat) {
		values.put("jam_stat", jam_stat.value());
		return this;
	}

	/**
	 *  @param jam_stat Jamming Status (as a String)
	 */
	public GnssHwMon setJamStatStr(String jam_stat) {
		setValue("jam_stat", jam_stat);
		return this;
	}

	/**
	 *  @param jam_stat Jamming Status (integer value)
	 */
	public GnssHwMon setJamStatVal(short jam_stat) {
		setValue("jam_stat", jam_stat);
		return this;
	}

	/**
	 *  @return RF noise - uint16_t
	 */
	public int getRfNoise() {
		return getInteger("rf_noise");
	}

	/**
	 *  @param rf_noise RF noise
	 */
	public GnssHwMon setRfNoise(int rf_noise) {
		values.put("rf_noise", rf_noise);
		return this;
	}

	/**
	 *  @return Antenna Status (enumerated) - uint8_t
	 */
	public ANT_STAT getAntStat() {
		try {
			ANT_STAT o = ANT_STAT.valueOf(getMessageType().getFieldPossibleValues("ant_stat").get(getLong("ant_stat")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAntStatStr() {
		return getString("ant_stat");
	}

	public short getAntStatVal() {
		return (short) getInteger("ant_stat");
	}

	/**
	 *  @param ant_stat Antenna Status (enumerated)
	 */
	public GnssHwMon setAntStat(ANT_STAT ant_stat) {
		values.put("ant_stat", ant_stat.value());
		return this;
	}

	/**
	 *  @param ant_stat Antenna Status (as a String)
	 */
	public GnssHwMon setAntStatStr(String ant_stat) {
		setValue("ant_stat", ant_stat);
		return this;
	}

	/**
	 *  @param ant_stat Antenna Status (integer value)
	 */
	public GnssHwMon setAntStatVal(short ant_stat) {
		setValue("ant_stat", ant_stat);
		return this;
	}

	/**
	 *  @return Antenna Power (enumerated) - uint8_t
	 */
	public ANT_PWR getAntPwr() {
		try {
			ANT_PWR o = ANT_PWR.valueOf(getMessageType().getFieldPossibleValues("ant_pwr").get(getLong("ant_pwr")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAntPwrStr() {
		return getString("ant_pwr");
	}

	public short getAntPwrVal() {
		return (short) getInteger("ant_pwr");
	}

	/**
	 *  @param ant_pwr Antenna Power (enumerated)
	 */
	public GnssHwMon setAntPwr(ANT_PWR ant_pwr) {
		values.put("ant_pwr", ant_pwr.value());
		return this;
	}

	/**
	 *  @param ant_pwr Antenna Power (as a String)
	 */
	public GnssHwMon setAntPwrStr(String ant_pwr) {
		setValue("ant_pwr", ant_pwr);
		return this;
	}

	/**
	 *  @param ant_pwr Antenna Power (integer value)
	 */
	public GnssHwMon setAntPwrVal(short ant_pwr) {
		setValue("ant_pwr", ant_pwr);
		return this;
	}

}
