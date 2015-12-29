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
 *  IMC Message Formation Monitoring Data (481)<br/>
 *  Monitoring variables for the formation state and performance.<br/>
 */

public class FormationMonitor extends IMCMessage {

	public static final int ID_STATIC = 481;

	public FormationMonitor() {
		super(ID_STATIC);
	}

	public FormationMonitor(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormationMonitor(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormationMonitor create(Object... values) {
		FormationMonitor m = new FormationMonitor();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormationMonitor clone(IMCMessage msg) throws Exception {

		FormationMonitor m = new FormationMonitor();
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

	public FormationMonitor(float ax_cmd, float ay_cmd, float az_cmd, float ax_des, float ay_des, float az_des, float virt_err_x, float virt_err_y, float virt_err_z, float surf_fdbk_x, float surf_fdbk_y, float surf_fdbk_z, float surf_unkn_x, float surf_unkn_y, float surf_unkn_z, float ss_x, float ss_y, float ss_z, java.util.Collection<RelativeState> rel_state) {
		super(ID_STATIC);
		setAxCmd(ax_cmd);
		setAyCmd(ay_cmd);
		setAzCmd(az_cmd);
		setAxDes(ax_des);
		setAyDes(ay_des);
		setAzDes(az_des);
		setVirtErrX(virt_err_x);
		setVirtErrY(virt_err_y);
		setVirtErrZ(virt_err_z);
		setSurfFdbkX(surf_fdbk_x);
		setSurfFdbkY(surf_fdbk_y);
		setSurfFdbkZ(surf_fdbk_z);
		setSurfUnknX(surf_unkn_x);
		setSurfUnknY(surf_unkn_y);
		setSurfUnknZ(surf_unkn_z);
		setSsX(ss_x);
		setSsY(ss_y);
		setSsZ(ss_z);
		if (rel_state != null)
			setRelState(rel_state);
	}

	/**
	 *  @return Commanded X Acceleration (North) - fp32_t
	 */
	public double getAxCmd() {
		return getDouble("ax_cmd");
	}

	/**
	 *  @param ax_cmd Commanded X Acceleration (North)
	 */
	public FormationMonitor setAxCmd(double ax_cmd) {
		values.put("ax_cmd", ax_cmd);
		return this;
	}

	/**
	 *  @return Commanded Y Acceleration (East) - fp32_t
	 */
	public double getAyCmd() {
		return getDouble("ay_cmd");
	}

	/**
	 *  @param ay_cmd Commanded Y Acceleration (East)
	 */
	public FormationMonitor setAyCmd(double ay_cmd) {
		values.put("ay_cmd", ay_cmd);
		return this;
	}

	/**
	 *  @return Commanded Z Acceleration (Down) - fp32_t
	 */
	public double getAzCmd() {
		return getDouble("az_cmd");
	}

	/**
	 *  @param az_cmd Commanded Z Acceleration (Down)
	 */
	public FormationMonitor setAzCmd(double az_cmd) {
		values.put("az_cmd", az_cmd);
		return this;
	}

	/**
	 *  @return Desired X Acceleration (North) - fp32_t
	 */
	public double getAxDes() {
		return getDouble("ax_des");
	}

	/**
	 *  @param ax_des Desired X Acceleration (North)
	 */
	public FormationMonitor setAxDes(double ax_des) {
		values.put("ax_des", ax_des);
		return this;
	}

	/**
	 *  @return Desired Y Acceleration (East) - fp32_t
	 */
	public double getAyDes() {
		return getDouble("ay_des");
	}

	/**
	 *  @param ay_des Desired Y Acceleration (East)
	 */
	public FormationMonitor setAyDes(double ay_des) {
		values.put("ay_des", ay_des);
		return this;
	}

	/**
	 *  @return Desired Z Acceleration (Down) - fp32_t
	 */
	public double getAzDes() {
		return getDouble("az_des");
	}

	/**
	 *  @param az_des Desired Z Acceleration (Down)
	 */
	public FormationMonitor setAzDes(double az_des) {
		values.put("az_des", az_des);
		return this;
	}

	/**
	 *  @return X Virtual Error (North) - fp32_t
	 */
	public double getVirtErrX() {
		return getDouble("virt_err_x");
	}

	/**
	 *  @param virt_err_x X Virtual Error (North)
	 */
	public FormationMonitor setVirtErrX(double virt_err_x) {
		values.put("virt_err_x", virt_err_x);
		return this;
	}

	/**
	 *  @return Y Virtual Error (East) - fp32_t
	 */
	public double getVirtErrY() {
		return getDouble("virt_err_y");
	}

	/**
	 *  @param virt_err_y Y Virtual Error (East)
	 */
	public FormationMonitor setVirtErrY(double virt_err_y) {
		values.put("virt_err_y", virt_err_y);
		return this;
	}

	/**
	 *  @return Z Virtual Error (Down) - fp32_t
	 */
	public double getVirtErrZ() {
		return getDouble("virt_err_z");
	}

	/**
	 *  @param virt_err_z Z Virtual Error (Down)
	 */
	public FormationMonitor setVirtErrZ(double virt_err_z) {
		values.put("virt_err_z", virt_err_z);
		return this;
	}

	/**
	 *  @return X Sliding Surface Feedback (North) - fp32_t
	 */
	public double getSurfFdbkX() {
		return getDouble("surf_fdbk_x");
	}

	/**
	 *  @param surf_fdbk_x X Sliding Surface Feedback (North)
	 */
	public FormationMonitor setSurfFdbkX(double surf_fdbk_x) {
		values.put("surf_fdbk_x", surf_fdbk_x);
		return this;
	}

	/**
	 *  @return Y Sliding Surface Feedback (East) - fp32_t
	 */
	public double getSurfFdbkY() {
		return getDouble("surf_fdbk_y");
	}

	/**
	 *  @param surf_fdbk_y Y Sliding Surface Feedback (East)
	 */
	public FormationMonitor setSurfFdbkY(double surf_fdbk_y) {
		values.put("surf_fdbk_y", surf_fdbk_y);
		return this;
	}

	/**
	 *  @return Z Sliding Surface Feedback (Down) - fp32_t
	 */
	public double getSurfFdbkZ() {
		return getDouble("surf_fdbk_z");
	}

	/**
	 *  @param surf_fdbk_z Z Sliding Surface Feedback (Down)
	 */
	public FormationMonitor setSurfFdbkZ(double surf_fdbk_z) {
		values.put("surf_fdbk_z", surf_fdbk_z);
		return this;
	}

	/**
	 *  @return X Uncertainty Compensation (North) - fp32_t
	 */
	public double getSurfUnknX() {
		return getDouble("surf_unkn_x");
	}

	/**
	 *  @param surf_unkn_x X Uncertainty Compensation (North)
	 */
	public FormationMonitor setSurfUnknX(double surf_unkn_x) {
		values.put("surf_unkn_x", surf_unkn_x);
		return this;
	}

	/**
	 *  @return Y Uncertainty Compensation (East) - fp32_t
	 */
	public double getSurfUnknY() {
		return getDouble("surf_unkn_y");
	}

	/**
	 *  @param surf_unkn_y Y Uncertainty Compensation (East)
	 */
	public FormationMonitor setSurfUnknY(double surf_unkn_y) {
		values.put("surf_unkn_y", surf_unkn_y);
		return this;
	}

	/**
	 *  @return Z Uncertainty Compensation (Down) - fp32_t
	 */
	public double getSurfUnknZ() {
		return getDouble("surf_unkn_z");
	}

	/**
	 *  @param surf_unkn_z Z Uncertainty Compensation (Down)
	 */
	public FormationMonitor setSurfUnknZ(double surf_unkn_z) {
		values.put("surf_unkn_z", surf_unkn_z);
		return this;
	}

	/**
	 *  @return X Convergence Deviation (North) - fp32_t
	 */
	public double getSsX() {
		return getDouble("ss_x");
	}

	/**
	 *  @param ss_x X Convergence Deviation (North)
	 */
	public FormationMonitor setSsX(double ss_x) {
		values.put("ss_x", ss_x);
		return this;
	}

	/**
	 *  @return Y Convergence Deviation (East) - fp32_t
	 */
	public double getSsY() {
		return getDouble("ss_y");
	}

	/**
	 *  @param ss_y Y Convergence Deviation (East)
	 */
	public FormationMonitor setSsY(double ss_y) {
		values.put("ss_y", ss_y);
		return this;
	}

	/**
	 *  @return Z Convergence Deviation (Down) - fp32_t
	 */
	public double getSsZ() {
		return getDouble("ss_z");
	}

	/**
	 *  @param ss_z Z Convergence Deviation (Down)
	 */
	public FormationMonitor setSsZ(double ss_z) {
		values.put("ss_z", ss_z);
		return this;
	}

	/**
	 *  @return Relative State - message-list
	 */
	public java.util.Vector<RelativeState> getRelState() {
		try {
			return getMessageList("rel_state", RelativeState.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param rel_state Relative State
	 */
	public FormationMonitor setRelState(java.util.Collection<RelativeState> rel_state) {
		values.put("rel_state", rel_state);
		return this;
	}

}
