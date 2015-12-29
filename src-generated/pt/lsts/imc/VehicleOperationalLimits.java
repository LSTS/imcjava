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
 *  IMC Message Vehicle Operational Limits (16)<br/>
 *  Vehicle opertional limits.<br/>
 *  For aircraft this should represent the flight envelope and the dynamic contraints.<br/>
 */

public class VehicleOperationalLimits extends IMCMessage {

	public enum OP {
		REQUEST(0),
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

	public static final int ID_STATIC = 16;

	public VehicleOperationalLimits() {
		super(ID_STATIC);
	}

	public VehicleOperationalLimits(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VehicleOperationalLimits(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VehicleOperationalLimits create(Object... values) {
		VehicleOperationalLimits m = new VehicleOperationalLimits();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VehicleOperationalLimits clone(IMCMessage msg) throws Exception {

		VehicleOperationalLimits m = new VehicleOperationalLimits();
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

	public VehicleOperationalLimits(OP op, float speed_min, float speed_max, float long_accel, float alt_max_msl, float dive_fraction_max, float climb_fraction_max, float bank_max, float p_max, float pitch_min, float pitch_max, float q_max, float g_min, float g_max, float g_lat_max, float rpm_min, float rpm_max, float rpm_rate_max) {
		super(ID_STATIC);
		setOp(op);
		setSpeedMin(speed_min);
		setSpeedMax(speed_max);
		setLongAccel(long_accel);
		setAltMaxMsl(alt_max_msl);
		setDiveFractionMax(dive_fraction_max);
		setClimbFractionMax(climb_fraction_max);
		setBankMax(bank_max);
		setPMax(p_max);
		setPitchMin(pitch_min);
		setPitchMax(pitch_max);
		setQMax(q_max);
		setGMin(g_min);
		setGMax(g_max);
		setGLatMax(g_lat_max);
		setRpmMin(rpm_min);
		setRpmMax(rpm_max);
		setRpmRateMax(rpm_rate_max);
	}

	/**
	 *  @return Action on the vehicle operational limits (enumerated) - uint8_t
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
	 *  @param op Action on the vehicle operational limits (enumerated)
	 */
	public VehicleOperationalLimits setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Action on the vehicle operational limits (as a String)
	 */
	public VehicleOperationalLimits setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Action on the vehicle operational limits (integer value)
	 */
	public VehicleOperationalLimits setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Minimum speed (m/s) - fp32_t
	 */
	public double getSpeedMin() {
		return getDouble("speed_min");
	}

	/**
	 *  @param speed_min Minimum speed (m/s)
	 */
	public VehicleOperationalLimits setSpeedMin(double speed_min) {
		values.put("speed_min", speed_min);
		return this;
	}

	/**
	 *  @return Maximum speed (m/s) - fp32_t
	 */
	public double getSpeedMax() {
		return getDouble("speed_max");
	}

	/**
	 *  @param speed_max Maximum speed (m/s)
	 */
	public VehicleOperationalLimits setSpeedMax(double speed_max) {
		values.put("speed_max", speed_max);
		return this;
	}

	/**
	 *  @return Longitudinal maximum acceleration (m/s/s) - fp32_t
	 */
	public double getLongAccel() {
		return getDouble("long_accel");
	}

	/**
	 *  @param long_accel Longitudinal maximum acceleration (m/s/s)
	 */
	public VehicleOperationalLimits setLongAccel(double long_accel) {
		values.put("long_accel", long_accel);
		return this;
	}

	/**
	 *  @return Maximum MSL altitude (m) - fp32_t
	 */
	public double getAltMaxMsl() {
		return getDouble("alt_max_msl");
	}

	/**
	 *  @param alt_max_msl Maximum MSL altitude (m)
	 */
	public VehicleOperationalLimits setAltMaxMsl(double alt_max_msl) {
		values.put("alt_max_msl", alt_max_msl);
		return this;
	}

	/**
	 *  @return Maximum Dive Rate Speed Fraction - fp32_t
	 */
	public double getDiveFractionMax() {
		return getDouble("dive_fraction_max");
	}

	/**
	 *  @param dive_fraction_max Maximum Dive Rate Speed Fraction
	 */
	public VehicleOperationalLimits setDiveFractionMax(double dive_fraction_max) {
		values.put("dive_fraction_max", dive_fraction_max);
		return this;
	}

	/**
	 *  @return Maximum Climb Rate Speed Fraction - fp32_t
	 */
	public double getClimbFractionMax() {
		return getDouble("climb_fraction_max");
	}

	/**
	 *  @param climb_fraction_max Maximum Climb Rate Speed Fraction
	 */
	public VehicleOperationalLimits setClimbFractionMax(double climb_fraction_max) {
		values.put("climb_fraction_max", climb_fraction_max);
		return this;
	}

	/**
	 *  @return Bank limit (rad) - fp32_t
	 */
	public double getBankMax() {
		return getDouble("bank_max");
	}

	/**
	 *  @param bank_max Bank limit (rad)
	 */
	public VehicleOperationalLimits setBankMax(double bank_max) {
		values.put("bank_max", bank_max);
		return this;
	}

	/**
	 *  @return Bank rate limit (rad/s) - fp32_t
	 */
	public double getPMax() {
		return getDouble("p_max");
	}

	/**
	 *  @param p_max Bank rate limit (rad/s)
	 */
	public VehicleOperationalLimits setPMax(double p_max) {
		values.put("p_max", p_max);
		return this;
	}

	/**
	 *  @return Minimum pitch angle (rad) - fp32_t
	 */
	public double getPitchMin() {
		return getDouble("pitch_min");
	}

	/**
	 *  @param pitch_min Minimum pitch angle (rad)
	 */
	public VehicleOperationalLimits setPitchMin(double pitch_min) {
		values.put("pitch_min", pitch_min);
		return this;
	}

	/**
	 *  @return Maximum pitch angle (rad) - fp32_t
	 */
	public double getPitchMax() {
		return getDouble("pitch_max");
	}

	/**
	 *  @param pitch_max Maximum pitch angle (rad)
	 */
	public VehicleOperationalLimits setPitchMax(double pitch_max) {
		values.put("pitch_max", pitch_max);
		return this;
	}

	/**
	 *  @return Maximum pitch rate (rad/s) - fp32_t
	 */
	public double getQMax() {
		return getDouble("q_max");
	}

	/**
	 *  @param q_max Maximum pitch rate (rad/s)
	 */
	public VehicleOperationalLimits setQMax(double q_max) {
		values.put("q_max", q_max);
		return this;
	}

	/**
	 *  @return Minimum load factor (g) - fp32_t
	 */
	public double getGMin() {
		return getDouble("g_min");
	}

	/**
	 *  @param g_min Minimum load factor (g)
	 */
	public VehicleOperationalLimits setGMin(double g_min) {
		values.put("g_min", g_min);
		return this;
	}

	/**
	 *  @return Maximum load factor (g) - fp32_t
	 */
	public double getGMax() {
		return getDouble("g_max");
	}

	/**
	 *  @param g_max Maximum load factor (g)
	 */
	public VehicleOperationalLimits setGMax(double g_max) {
		values.put("g_max", g_max);
		return this;
	}

	/**
	 *  @return Maximum lateral load factor (g) - fp32_t
	 */
	public double getGLatMax() {
		return getDouble("g_lat_max");
	}

	/**
	 *  @param g_lat_max Maximum lateral load factor (g)
	 */
	public VehicleOperationalLimits setGLatMax(double g_lat_max) {
		values.put("g_lat_max", g_lat_max);
		return this;
	}

	/**
	 *  @return Minimum RPMs (rpm) - fp32_t
	 */
	public double getRpmMin() {
		return getDouble("rpm_min");
	}

	/**
	 *  @param rpm_min Minimum RPMs (rpm)
	 */
	public VehicleOperationalLimits setRpmMin(double rpm_min) {
		values.put("rpm_min", rpm_min);
		return this;
	}

	/**
	 *  @return Maximum RPMs (rpm) - fp32_t
	 */
	public double getRpmMax() {
		return getDouble("rpm_max");
	}

	/**
	 *  @param rpm_max Maximum RPMs (rpm)
	 */
	public VehicleOperationalLimits setRpmMax(double rpm_max) {
		values.put("rpm_max", rpm_max);
		return this;
	}

	/**
	 *  @return Maximum RPM rate (rpm/s) - fp32_t
	 */
	public double getRpmRateMax() {
		return getDouble("rpm_rate_max");
	}

	/**
	 *  @param rpm_rate_max Maximum RPM rate (rpm/s)
	 */
	public VehicleOperationalLimits setRpmRateMax(double rpm_rate_max) {
		values.put("rpm_rate_max", rpm_rate_max);
		return this;
	}

}
