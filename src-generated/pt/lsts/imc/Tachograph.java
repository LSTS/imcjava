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
 *  IMC Message Tachograph (905)<br/>
 *  This messages is used to record system activity parameters. These<br/>
 *  parameters are mainly used for used for maintenance purposes.<br/>
 */

public class Tachograph extends IMCMessage {

	public static final int ID_STATIC = 905;

	public Tachograph() {
		super(ID_STATIC);
	}

	public Tachograph(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tachograph(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Tachograph create(Object... values) {
		Tachograph m = new Tachograph();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Tachograph clone(IMCMessage msg) throws Exception {

		Tachograph m = new Tachograph();
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

	public Tachograph(double timestamp_last_service, float time_next_service, float time_motor_next_service, float time_idle_ground, float time_idle_air, float time_idle_water, float time_idle_underwater, float time_idle_unknown, float time_motor_ground, float time_motor_air, float time_motor_water, float time_motor_underwater, float time_motor_unknown, short rpm_min, short rpm_max, float depth_max) {
		super(ID_STATIC);
		setTimestampLastService(timestamp_last_service);
		setTimeNextService(time_next_service);
		setTimeMotorNextService(time_motor_next_service);
		setTimeIdleGround(time_idle_ground);
		setTimeIdleAir(time_idle_air);
		setTimeIdleWater(time_idle_water);
		setTimeIdleUnderwater(time_idle_underwater);
		setTimeIdleUnknown(time_idle_unknown);
		setTimeMotorGround(time_motor_ground);
		setTimeMotorAir(time_motor_air);
		setTimeMotorWater(time_motor_water);
		setTimeMotorUnderwater(time_motor_underwater);
		setTimeMotorUnknown(time_motor_unknown);
		setRpmMin(rpm_min);
		setRpmMax(rpm_max);
		setDepthMax(depth_max);
	}

	/**
	 *  @return Last Service Timestamp (s) - fp64_t
	 */
	public double getTimestampLastService() {
		return getDouble("timestamp_last_service");
	}

	/**
	 *  @param timestamp_last_service Last Service Timestamp (s)
	 */
	public Tachograph setTimestampLastService(double timestamp_last_service) {
		values.put("timestamp_last_service", timestamp_last_service);
		return this;
	}

	/**
	 *  @return Time - Next Service (s) - fp32_t
	 */
	public double getTimeNextService() {
		return getDouble("time_next_service");
	}

	/**
	 *  @param time_next_service Time - Next Service (s)
	 */
	public Tachograph setTimeNextService(double time_next_service) {
		values.put("time_next_service", time_next_service);
		return this;
	}

	/**
	 *  @return Time Motor - Next Service (s) - fp32_t
	 */
	public double getTimeMotorNextService() {
		return getDouble("time_motor_next_service");
	}

	/**
	 *  @param time_motor_next_service Time Motor - Next Service (s)
	 */
	public Tachograph setTimeMotorNextService(double time_motor_next_service) {
		values.put("time_motor_next_service", time_motor_next_service);
		return this;
	}

	/**
	 *  @return Time Idle - Ground (s) - fp32_t
	 */
	public double getTimeIdleGround() {
		return getDouble("time_idle_ground");
	}

	/**
	 *  @param time_idle_ground Time Idle - Ground (s)
	 */
	public Tachograph setTimeIdleGround(double time_idle_ground) {
		values.put("time_idle_ground", time_idle_ground);
		return this;
	}

	/**
	 *  @return Time Idle - Air (s) - fp32_t
	 */
	public double getTimeIdleAir() {
		return getDouble("time_idle_air");
	}

	/**
	 *  @param time_idle_air Time Idle - Air (s)
	 */
	public Tachograph setTimeIdleAir(double time_idle_air) {
		values.put("time_idle_air", time_idle_air);
		return this;
	}

	/**
	 *  @return Time Idle - Water (s) - fp32_t
	 */
	public double getTimeIdleWater() {
		return getDouble("time_idle_water");
	}

	/**
	 *  @param time_idle_water Time Idle - Water (s)
	 */
	public Tachograph setTimeIdleWater(double time_idle_water) {
		values.put("time_idle_water", time_idle_water);
		return this;
	}

	/**
	 *  @return Time Idle - Underwater (s) - fp32_t
	 */
	public double getTimeIdleUnderwater() {
		return getDouble("time_idle_underwater");
	}

	/**
	 *  @param time_idle_underwater Time Idle - Underwater (s)
	 */
	public Tachograph setTimeIdleUnderwater(double time_idle_underwater) {
		values.put("time_idle_underwater", time_idle_underwater);
		return this;
	}

	/**
	 *  @return Time Idle - Unknown (s) - fp32_t
	 */
	public double getTimeIdleUnknown() {
		return getDouble("time_idle_unknown");
	}

	/**
	 *  @param time_idle_unknown Time Idle - Unknown (s)
	 */
	public Tachograph setTimeIdleUnknown(double time_idle_unknown) {
		values.put("time_idle_unknown", time_idle_unknown);
		return this;
	}

	/**
	 *  @return Time Motor - Ground (s) - fp32_t
	 */
	public double getTimeMotorGround() {
		return getDouble("time_motor_ground");
	}

	/**
	 *  @param time_motor_ground Time Motor - Ground (s)
	 */
	public Tachograph setTimeMotorGround(double time_motor_ground) {
		values.put("time_motor_ground", time_motor_ground);
		return this;
	}

	/**
	 *  @return Time Motor - Air (s) - fp32_t
	 */
	public double getTimeMotorAir() {
		return getDouble("time_motor_air");
	}

	/**
	 *  @param time_motor_air Time Motor - Air (s)
	 */
	public Tachograph setTimeMotorAir(double time_motor_air) {
		values.put("time_motor_air", time_motor_air);
		return this;
	}

	/**
	 *  @return Time Motor - Water (s) - fp32_t
	 */
	public double getTimeMotorWater() {
		return getDouble("time_motor_water");
	}

	/**
	 *  @param time_motor_water Time Motor - Water (s)
	 */
	public Tachograph setTimeMotorWater(double time_motor_water) {
		values.put("time_motor_water", time_motor_water);
		return this;
	}

	/**
	 *  @return Time Motor - Underwater (s) - fp32_t
	 */
	public double getTimeMotorUnderwater() {
		return getDouble("time_motor_underwater");
	}

	/**
	 *  @param time_motor_underwater Time Motor - Underwater (s)
	 */
	public Tachograph setTimeMotorUnderwater(double time_motor_underwater) {
		values.put("time_motor_underwater", time_motor_underwater);
		return this;
	}

	/**
	 *  @return Time Motor - Unknown (s) - fp32_t
	 */
	public double getTimeMotorUnknown() {
		return getDouble("time_motor_unknown");
	}

	/**
	 *  @param time_motor_unknown Time Motor - Unknown (s)
	 */
	public Tachograph setTimeMotorUnknown(double time_motor_unknown) {
		values.put("time_motor_unknown", time_motor_unknown);
		return this;
	}

	/**
	 *  @return Recorded RPMs - Minimum (rpm) - int16_t
	 */
	public short getRpmMin() {
		return (short) getInteger("rpm_min");
	}

	/**
	 *  @param rpm_min Recorded RPMs - Minimum (rpm)
	 */
	public Tachograph setRpmMin(short rpm_min) {
		values.put("rpm_min", rpm_min);
		return this;
	}

	/**
	 *  @return Recorded RPMs - Maximum (rpm) - int16_t
	 */
	public short getRpmMax() {
		return (short) getInteger("rpm_max");
	}

	/**
	 *  @param rpm_max Recorded RPMs - Maximum (rpm)
	 */
	public Tachograph setRpmMax(short rpm_max) {
		values.put("rpm_max", rpm_max);
		return this;
	}

	/**
	 *  @return Recorded Depth - Maximum (m) - fp32_t
	 */
	public double getDepthMax() {
		return getDouble("depth_max");
	}

	/**
	 *  @param depth_max Recorded Depth - Maximum (m)
	 */
	public Tachograph setDepthMax(double depth_max) {
		values.put("depth_max", depth_max);
		return this;
	}

}
