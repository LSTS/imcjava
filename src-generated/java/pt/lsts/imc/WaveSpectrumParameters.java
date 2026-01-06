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
 *  IMC Message Wave Spectrum Parameters (1018)<br/>
 *  This message reports various parameters related to ocean wave characteristics, including height, direction, and period.<br/>
 */

public class WaveSpectrumParameters extends IMCMessage {

	public static final int ID_STATIC = 1018;

	public WaveSpectrumParameters() {
		super(ID_STATIC);
	}

	public WaveSpectrumParameters(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WaveSpectrumParameters(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static WaveSpectrumParameters create(Object... values) {
		WaveSpectrumParameters m = new WaveSpectrumParameters();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static WaveSpectrumParameters clone(IMCMessage msg) throws Exception {

		WaveSpectrumParameters m = new WaveSpectrumParameters();
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

	public WaveSpectrumParameters(float sig_wave_height_hm0, float wave_peak_direction, float wave_peak_period, float wave_height_wind_hm0, float wave_height_swell_hm0, float wave_peak_period_wind, float wave_peak_period_swell, float wave_peak_direction_wind, float wave_peak_direction_swell, float wave_mean_direction, float wave_mean_period_tm02, float wave_height_hmax, float wave_height_crest, float wave_height_trough, float wave_period_tmax, float wave_period_tz, float significant_wave_height_h1_3, float mean_spreading_angle, float first_order_spread, float long_crestedness_parameters, float heading, float pitch, float roll, float external_heading, float stdev_heading, float stdev_pitch, float stdev_roll) {
		super(ID_STATIC);
		setSigWaveHeightHm0(sig_wave_height_hm0);
		setWavePeakDirection(wave_peak_direction);
		setWavePeakPeriod(wave_peak_period);
		setWaveHeightWindHm0(wave_height_wind_hm0);
		setWaveHeightSwellHm0(wave_height_swell_hm0);
		setWavePeakPeriodWind(wave_peak_period_wind);
		setWavePeakPeriodSwell(wave_peak_period_swell);
		setWavePeakDirectionWind(wave_peak_direction_wind);
		setWavePeakDirectionSwell(wave_peak_direction_swell);
		setWaveMeanDirection(wave_mean_direction);
		setWaveMeanPeriodTm02(wave_mean_period_tm02);
		setWaveHeightHmax(wave_height_hmax);
		setWaveHeightCrest(wave_height_crest);
		setWaveHeightTrough(wave_height_trough);
		setWavePeriodTmax(wave_period_tmax);
		setWavePeriodTz(wave_period_tz);
		setSignificantWaveHeightH13(significant_wave_height_h1_3);
		setMeanSpreadingAngle(mean_spreading_angle);
		setFirstOrderSpread(first_order_spread);
		setLongCrestednessParameters(long_crestedness_parameters);
		setHeading(heading);
		setPitch(pitch);
		setRoll(roll);
		setExternalHeading(external_heading);
		setStdevHeading(stdev_heading);
		setStdevPitch(stdev_pitch);
		setStdevRoll(stdev_roll);
	}

	/**
	 *  @return Significant Wave Height Hm0 (m) - fp32_t
	 */
	public double getSigWaveHeightHm0() {
		return getDouble("sig_wave_height_hm0");
	}

	/**
	 *  @param sig_wave_height_hm0 Significant Wave Height Hm0 (m)
	 */
	public WaveSpectrumParameters setSigWaveHeightHm0(double sig_wave_height_hm0) {
		values.put("sig_wave_height_hm0", sig_wave_height_hm0);
		return this;
	}

	/**
	 *  @return Wave Peak Direction (°) - fp32_t
	 */
	public double getWavePeakDirection() {
		return getDouble("wave_peak_direction");
	}

	/**
	 *  @param wave_peak_direction Wave Peak Direction (°)
	 */
	public WaveSpectrumParameters setWavePeakDirection(double wave_peak_direction) {
		values.put("wave_peak_direction", wave_peak_direction);
		return this;
	}

	/**
	 *  @return Wave Peak Period (s) - fp32_t
	 */
	public double getWavePeakPeriod() {
		return getDouble("wave_peak_period");
	}

	/**
	 *  @param wave_peak_period Wave Peak Period (s)
	 */
	public WaveSpectrumParameters setWavePeakPeriod(double wave_peak_period) {
		values.put("wave_peak_period", wave_peak_period);
		return this;
	}

	/**
	 *  @return Wave Height Wind Hm0 (m) - fp32_t
	 */
	public double getWaveHeightWindHm0() {
		return getDouble("wave_height_wind_hm0");
	}

	/**
	 *  @param wave_height_wind_hm0 Wave Height Wind Hm0 (m)
	 */
	public WaveSpectrumParameters setWaveHeightWindHm0(double wave_height_wind_hm0) {
		values.put("wave_height_wind_hm0", wave_height_wind_hm0);
		return this;
	}

	/**
	 *  @return Wave Height Swell Hm0 (m) - fp32_t
	 */
	public double getWaveHeightSwellHm0() {
		return getDouble("wave_height_swell_hm0");
	}

	/**
	 *  @param wave_height_swell_hm0 Wave Height Swell Hm0 (m)
	 */
	public WaveSpectrumParameters setWaveHeightSwellHm0(double wave_height_swell_hm0) {
		values.put("wave_height_swell_hm0", wave_height_swell_hm0);
		return this;
	}

	/**
	 *  @return Wave Peak Period Wind (s) - fp32_t
	 */
	public double getWavePeakPeriodWind() {
		return getDouble("wave_peak_period_wind");
	}

	/**
	 *  @param wave_peak_period_wind Wave Peak Period Wind (s)
	 */
	public WaveSpectrumParameters setWavePeakPeriodWind(double wave_peak_period_wind) {
		values.put("wave_peak_period_wind", wave_peak_period_wind);
		return this;
	}

	/**
	 *  @return Wave Peak Period Swell (s) - fp32_t
	 */
	public double getWavePeakPeriodSwell() {
		return getDouble("wave_peak_period_swell");
	}

	/**
	 *  @param wave_peak_period_swell Wave Peak Period Swell (s)
	 */
	public WaveSpectrumParameters setWavePeakPeriodSwell(double wave_peak_period_swell) {
		values.put("wave_peak_period_swell", wave_peak_period_swell);
		return this;
	}

	/**
	 *  @return Wave Peak Direction Wind (°) - fp32_t
	 */
	public double getWavePeakDirectionWind() {
		return getDouble("wave_peak_direction_wind");
	}

	/**
	 *  @param wave_peak_direction_wind Wave Peak Direction Wind (°)
	 */
	public WaveSpectrumParameters setWavePeakDirectionWind(double wave_peak_direction_wind) {
		values.put("wave_peak_direction_wind", wave_peak_direction_wind);
		return this;
	}

	/**
	 *  @return Wave Peak Direction Swell (°) - fp32_t
	 */
	public double getWavePeakDirectionSwell() {
		return getDouble("wave_peak_direction_swell");
	}

	/**
	 *  @param wave_peak_direction_swell Wave Peak Direction Swell (°)
	 */
	public WaveSpectrumParameters setWavePeakDirectionSwell(double wave_peak_direction_swell) {
		values.put("wave_peak_direction_swell", wave_peak_direction_swell);
		return this;
	}

	/**
	 *  @return Wave Mean Direction (°) - fp32_t
	 */
	public double getWaveMeanDirection() {
		return getDouble("wave_mean_direction");
	}

	/**
	 *  @param wave_mean_direction Wave Mean Direction (°)
	 */
	public WaveSpectrumParameters setWaveMeanDirection(double wave_mean_direction) {
		values.put("wave_mean_direction", wave_mean_direction);
		return this;
	}

	/**
	 *  @return Wave Mean Period Tm02 (s) - fp32_t
	 */
	public double getWaveMeanPeriodTm02() {
		return getDouble("wave_mean_period_tm02");
	}

	/**
	 *  @param wave_mean_period_tm02 Wave Mean Period Tm02 (s)
	 */
	public WaveSpectrumParameters setWaveMeanPeriodTm02(double wave_mean_period_tm02) {
		values.put("wave_mean_period_tm02", wave_mean_period_tm02);
		return this;
	}

	/**
	 *  @return Wave Height Hmax (m) - fp32_t
	 */
	public double getWaveHeightHmax() {
		return getDouble("wave_height_hmax");
	}

	/**
	 *  @param wave_height_hmax Wave Height Hmax (m)
	 */
	public WaveSpectrumParameters setWaveHeightHmax(double wave_height_hmax) {
		values.put("wave_height_hmax", wave_height_hmax);
		return this;
	}

	/**
	 *  @return Wave Height Crest (m) - fp32_t
	 */
	public double getWaveHeightCrest() {
		return getDouble("wave_height_crest");
	}

	/**
	 *  @param wave_height_crest Wave Height Crest (m)
	 */
	public WaveSpectrumParameters setWaveHeightCrest(double wave_height_crest) {
		values.put("wave_height_crest", wave_height_crest);
		return this;
	}

	/**
	 *  @return Wave Height Trough (m) - fp32_t
	 */
	public double getWaveHeightTrough() {
		return getDouble("wave_height_trough");
	}

	/**
	 *  @param wave_height_trough Wave Height Trough (m)
	 */
	public WaveSpectrumParameters setWaveHeightTrough(double wave_height_trough) {
		values.put("wave_height_trough", wave_height_trough);
		return this;
	}

	/**
	 *  @return Wave Period Tmax (s) - fp32_t
	 */
	public double getWavePeriodTmax() {
		return getDouble("wave_period_tmax");
	}

	/**
	 *  @param wave_period_tmax Wave Period Tmax (s)
	 */
	public WaveSpectrumParameters setWavePeriodTmax(double wave_period_tmax) {
		values.put("wave_period_tmax", wave_period_tmax);
		return this;
	}

	/**
	 *  @return Wave Period Tz (s) - fp32_t
	 */
	public double getWavePeriodTz() {
		return getDouble("wave_period_tz");
	}

	/**
	 *  @param wave_period_tz Wave Period Tz (s)
	 */
	public WaveSpectrumParameters setWavePeriodTz(double wave_period_tz) {
		values.put("wave_period_tz", wave_period_tz);
		return this;
	}

	/**
	 *  @return Significant Wave Height H1/3 (m) - fp32_t
	 */
	public double getSignificantWaveHeightH13() {
		return getDouble("significant_wave_height_h1_3");
	}

	/**
	 *  @param significant_wave_height_h1_3 Significant Wave Height H1/3 (m)
	 */
	public WaveSpectrumParameters setSignificantWaveHeightH13(double significant_wave_height_h1_3) {
		values.put("significant_wave_height_h1_3", significant_wave_height_h1_3);
		return this;
	}

	/**
	 *  @return Mean Spreading Angle (°) - fp32_t
	 */
	public double getMeanSpreadingAngle() {
		return getDouble("mean_spreading_angle");
	}

	/**
	 *  @param mean_spreading_angle Mean Spreading Angle (°)
	 */
	public WaveSpectrumParameters setMeanSpreadingAngle(double mean_spreading_angle) {
		values.put("mean_spreading_angle", mean_spreading_angle);
		return this;
	}

	/**
	 *  @return First Order Spread (°) - fp32_t
	 */
	public double getFirstOrderSpread() {
		return getDouble("first_order_spread");
	}

	/**
	 *  @param first_order_spread First Order Spread (°)
	 */
	public WaveSpectrumParameters setFirstOrderSpread(double first_order_spread) {
		values.put("first_order_spread", first_order_spread);
		return this;
	}

	/**
	 *  @return Long Crestedness Parameters - fp32_t
	 */
	public double getLongCrestednessParameters() {
		return getDouble("long_crestedness_parameters");
	}

	/**
	 *  @param long_crestedness_parameters Long Crestedness Parameters
	 */
	public WaveSpectrumParameters setLongCrestednessParameters(double long_crestedness_parameters) {
		values.put("long_crestedness_parameters", long_crestedness_parameters);
		return this;
	}

	/**
	 *  @return Heading (°) - fp32_t
	 */
	public double getHeading() {
		return getDouble("heading");
	}

	/**
	 *  @param heading Heading (°)
	 */
	public WaveSpectrumParameters setHeading(double heading) {
		values.put("heading", heading);
		return this;
	}

	/**
	 *  @return Pitch (°) - fp32_t
	 */
	public double getPitch() {
		return getDouble("pitch");
	}

	/**
	 *  @param pitch Pitch (°)
	 */
	public WaveSpectrumParameters setPitch(double pitch) {
		values.put("pitch", pitch);
		return this;
	}

	/**
	 *  @return Roll (°) - fp32_t
	 */
	public double getRoll() {
		return getDouble("roll");
	}

	/**
	 *  @param roll Roll (°)
	 */
	public WaveSpectrumParameters setRoll(double roll) {
		values.put("roll", roll);
		return this;
	}

	/**
	 *  @return External Heading (°) - fp32_t
	 */
	public double getExternalHeading() {
		return getDouble("external_heading");
	}

	/**
	 *  @param external_heading External Heading (°)
	 */
	public WaveSpectrumParameters setExternalHeading(double external_heading) {
		values.put("external_heading", external_heading);
		return this;
	}

	/**
	 *  @return StDev Heading (°) - fp32_t
	 */
	public double getStdevHeading() {
		return getDouble("stdev_heading");
	}

	/**
	 *  @param stdev_heading StDev Heading (°)
	 */
	public WaveSpectrumParameters setStdevHeading(double stdev_heading) {
		values.put("stdev_heading", stdev_heading);
		return this;
	}

	/**
	 *  @return StDev Pitch (°) - fp32_t
	 */
	public double getStdevPitch() {
		return getDouble("stdev_pitch");
	}

	/**
	 *  @param stdev_pitch StDev Pitch (°)
	 */
	public WaveSpectrumParameters setStdevPitch(double stdev_pitch) {
		values.put("stdev_pitch", stdev_pitch);
		return this;
	}

	/**
	 *  @return StDev Roll (°) - fp32_t
	 */
	public double getStdevRoll() {
		return getDouble("stdev_roll");
	}

	/**
	 *  @param stdev_roll StDev Roll (°)
	 */
	public WaveSpectrumParameters setStdevRoll(double stdev_roll) {
		values.put("stdev_roll", stdev_roll);
		return this;
	}

}
