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
 *  IMC Message Navigation Data (355)<br/>
 *  Report of navigation data.<br/>
 *  This is constituted by data which is not<br/>
 *  part of the vehicle estimated state but<br/>
 *  that the user may refer for more information.<br/>
 */

public class NavigationData extends IMCMessage {

	public static final int ID_STATIC = 355;

	public NavigationData() {
		super(ID_STATIC);
	}

	public NavigationData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NavigationData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static NavigationData create(Object... values) {
		NavigationData m = new NavigationData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static NavigationData clone(IMCMessage msg) throws Exception {

		NavigationData m = new NavigationData();
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

	public NavigationData(float bias_psi, float bias_r, float cog, float cyaw, float lbl_rej_level, float gps_rej_level, float custom_x, float custom_y, float custom_z) {
		super(ID_STATIC);
		setBiasPsi(bias_psi);
		setBiasR(bias_r);
		setCog(cog);
		setCyaw(cyaw);
		setLblRejLevel(lbl_rej_level);
		setGpsRejLevel(gps_rej_level);
		setCustomX(custom_x);
		setCustomY(custom_y);
		setCustomZ(custom_z);
	}

	/**
	 *  @return Yaw Bias (rad) - fp32_t
	 */
	public double getBiasPsi() {
		return getDouble("bias_psi");
	}

	/**
	 *  @param bias_psi Yaw Bias (rad)
	 */
	public NavigationData setBiasPsi(double bias_psi) {
		values.put("bias_psi", bias_psi);
		return this;
	}

	/**
	 *  @return Gyro. Yaw Rate Bias (rad/s) - fp32_t
	 */
	public double getBiasR() {
		return getDouble("bias_r");
	}

	/**
	 *  @param bias_r Gyro. Yaw Rate Bias (rad/s)
	 */
	public NavigationData setBiasR(double bias_r) {
		values.put("bias_r", bias_r);
		return this;
	}

	/**
	 *  @return Course Over Ground (rad) - fp32_t
	 */
	public double getCog() {
		return getDouble("cog");
	}

	/**
	 *  @param cog Course Over Ground (rad)
	 */
	public NavigationData setCog(double cog) {
		values.put("cog", cog);
		return this;
	}

	/**
	 *  @return Continuous Yaw (rad) - fp32_t
	 */
	public double getCyaw() {
		return getDouble("cyaw");
	}

	/**
	 *  @param cyaw Continuous Yaw (rad)
	 */
	public NavigationData setCyaw(double cyaw) {
		values.put("cyaw", cyaw);
		return this;
	}

	/**
	 *  @return GPS Rejection Filter Level - fp32_t
	 */
	public double getLblRejLevel() {
		return getDouble("lbl_rej_level");
	}

	/**
	 *  @param lbl_rej_level GPS Rejection Filter Level
	 */
	public NavigationData setLblRejLevel(double lbl_rej_level) {
		values.put("lbl_rej_level", lbl_rej_level);
		return this;
	}

	/**
	 *  @return LBL Rejection Filter Level - fp32_t
	 */
	public double getGpsRejLevel() {
		return getDouble("gps_rej_level");
	}

	/**
	 *  @param gps_rej_level LBL Rejection Filter Level
	 */
	public NavigationData setGpsRejLevel(double gps_rej_level) {
		values.put("gps_rej_level", gps_rej_level);
		return this;
	}

	/**
	 *  @return Variance - Custom Variable X - fp32_t
	 */
	public double getCustomX() {
		return getDouble("custom_x");
	}

	/**
	 *  @param custom_x Variance - Custom Variable X
	 */
	public NavigationData setCustomX(double custom_x) {
		values.put("custom_x", custom_x);
		return this;
	}

	/**
	 *  @return Variance - Custom Variable Y - fp32_t
	 */
	public double getCustomY() {
		return getDouble("custom_y");
	}

	/**
	 *  @param custom_y Variance - Custom Variable Y
	 */
	public NavigationData setCustomY(double custom_y) {
		values.put("custom_y", custom_y);
		return this;
	}

	/**
	 *  @return Variance - Custom Variable Z - fp32_t
	 */
	public double getCustomZ() {
		return getDouble("custom_z");
	}

	/**
	 *  @param custom_z Variance - Custom Variable Z
	 */
	public NavigationData setCustomZ(double custom_z) {
		values.put("custom_z", custom_z);
		return this;
	}

}
