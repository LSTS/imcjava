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
 *  IMC Message Relative State (482)<br/>
 *  Inter-vehicle formation state.<br/>
 */

public class RelativeState extends IMCMessage {

	public static final int ID_STATIC = 482;

	public RelativeState() {
		super(ID_STATIC);
	}

	public RelativeState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RelativeState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static RelativeState create(Object... values) {
		RelativeState m = new RelativeState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static RelativeState clone(IMCMessage msg) throws Exception {

		RelativeState m = new RelativeState();
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

	public RelativeState(String s_id, float dist, float err, float ctrl_imp, float rel_dir_x, float rel_dir_y, float rel_dir_z, float err_x, float err_y, float err_z, float rf_err_x, float rf_err_y, float rf_err_z, float rf_err_vx, float rf_err_vy, float rf_err_vz, float ss_x, float ss_y, float ss_z, float virt_err_x, float virt_err_y, float virt_err_z) {
		super(ID_STATIC);
		if (s_id != null)
			setSId(s_id);
		setDist(dist);
		setErr(err);
		setCtrlImp(ctrl_imp);
		setRelDirX(rel_dir_x);
		setRelDirY(rel_dir_y);
		setRelDirZ(rel_dir_z);
		setErrX(err_x);
		setErrY(err_y);
		setErrZ(err_z);
		setRfErrX(rf_err_x);
		setRfErrY(rf_err_y);
		setRfErrZ(rf_err_z);
		setRfErrVx(rf_err_vx);
		setRfErrVy(rf_err_vy);
		setRfErrVz(rf_err_vz);
		setSsX(ss_x);
		setSsY(ss_y);
		setSsZ(ss_z);
		setVirtErrX(virt_err_x);
		setVirtErrY(virt_err_y);
		setVirtErrZ(virt_err_z);
	}

	/**
	 *  @return System Identifier - plaintext
	 */
	public String getSId() {
		return getString("s_id");
	}

	/**
	 *  @param s_id System Identifier
	 */
	public RelativeState setSId(String s_id) {
		values.put("s_id", s_id);
		return this;
	}

	/**
	 *  @return Distance - fp32_t
	 */
	public double getDist() {
		return getDouble("dist");
	}

	/**
	 *  @param dist Distance
	 */
	public RelativeState setDist(double dist) {
		values.put("dist", dist);
		return this;
	}

	/**
	 *  @return Position Error - fp32_t
	 */
	public double getErr() {
		return getDouble("err");
	}

	/**
	 *  @param err Position Error
	 */
	public RelativeState setErr(double err) {
		values.put("err", err);
		return this;
	}

	/**
	 *  @return Control Importance - fp32_t
	 */
	public double getCtrlImp() {
		return getDouble("ctrl_imp");
	}

	/**
	 *  @param ctrl_imp Control Importance
	 */
	public RelativeState setCtrlImp(double ctrl_imp) {
		values.put("ctrl_imp", ctrl_imp);
		return this;
	}

	/**
	 *  @return Relative Direction X (North) - fp32_t
	 */
	public double getRelDirX() {
		return getDouble("rel_dir_x");
	}

	/**
	 *  @param rel_dir_x Relative Direction X (North)
	 */
	public RelativeState setRelDirX(double rel_dir_x) {
		values.put("rel_dir_x", rel_dir_x);
		return this;
	}

	/**
	 *  @return Relative Direction Y (East) - fp32_t
	 */
	public double getRelDirY() {
		return getDouble("rel_dir_y");
	}

	/**
	 *  @param rel_dir_y Relative Direction Y (East)
	 */
	public RelativeState setRelDirY(double rel_dir_y) {
		values.put("rel_dir_y", rel_dir_y);
		return this;
	}

	/**
	 *  @return Relative Direction Z (Down) - fp32_t
	 */
	public double getRelDirZ() {
		return getDouble("rel_dir_z");
	}

	/**
	 *  @param rel_dir_z Relative Direction Z (Down)
	 */
	public RelativeState setRelDirZ(double rel_dir_z) {
		values.put("rel_dir_z", rel_dir_z);
		return this;
	}

	/**
	 *  @return X Position Error (North) - fp32_t
	 */
	public double getErrX() {
		return getDouble("err_x");
	}

	/**
	 *  @param err_x X Position Error (North)
	 */
	public RelativeState setErrX(double err_x) {
		values.put("err_x", err_x);
		return this;
	}

	/**
	 *  @return Y Position Error (East) - fp32_t
	 */
	public double getErrY() {
		return getDouble("err_y");
	}

	/**
	 *  @param err_y Y Position Error (East)
	 */
	public RelativeState setErrY(double err_y) {
		values.put("err_y", err_y);
		return this;
	}

	/**
	 *  @return Z Position Error (Down) - fp32_t
	 */
	public double getErrZ() {
		return getDouble("err_z");
	}

	/**
	 *  @param err_z Z Position Error (Down)
	 */
	public RelativeState setErrZ(double err_z) {
		values.put("err_z", err_z);
		return this;
	}

	/**
	 *  @return X Position Error In Relative Frame (North) - fp32_t
	 */
	public double getRfErrX() {
		return getDouble("rf_err_x");
	}

	/**
	 *  @param rf_err_x X Position Error In Relative Frame (North)
	 */
	public RelativeState setRfErrX(double rf_err_x) {
		values.put("rf_err_x", rf_err_x);
		return this;
	}

	/**
	 *  @return Y Position Error In Relative Frame (East) - fp32_t
	 */
	public double getRfErrY() {
		return getDouble("rf_err_y");
	}

	/**
	 *  @param rf_err_y Y Position Error In Relative Frame (East)
	 */
	public RelativeState setRfErrY(double rf_err_y) {
		values.put("rf_err_y", rf_err_y);
		return this;
	}

	/**
	 *  @return Z Position Error In Relative Frame (Down) - fp32_t
	 */
	public double getRfErrZ() {
		return getDouble("rf_err_z");
	}

	/**
	 *  @param rf_err_z Z Position Error In Relative Frame (Down)
	 */
	public RelativeState setRfErrZ(double rf_err_z) {
		values.put("rf_err_z", rf_err_z);
		return this;
	}

	/**
	 *  @return X Velocity Error In Relative Frame (North) - fp32_t
	 */
	public double getRfErrVx() {
		return getDouble("rf_err_vx");
	}

	/**
	 *  @param rf_err_vx X Velocity Error In Relative Frame (North)
	 */
	public RelativeState setRfErrVx(double rf_err_vx) {
		values.put("rf_err_vx", rf_err_vx);
		return this;
	}

	/**
	 *  @return Y Velocity Error In Relative Frame (East) - fp32_t
	 */
	public double getRfErrVy() {
		return getDouble("rf_err_vy");
	}

	/**
	 *  @param rf_err_vy Y Velocity Error In Relative Frame (East)
	 */
	public RelativeState setRfErrVy(double rf_err_vy) {
		values.put("rf_err_vy", rf_err_vy);
		return this;
	}

	/**
	 *  @return Z Velocity Error In Relative Frame (Down) - fp32_t
	 */
	public double getRfErrVz() {
		return getDouble("rf_err_vz");
	}

	/**
	 *  @param rf_err_vz Z Velocity Error In Relative Frame (Down)
	 */
	public RelativeState setRfErrVz(double rf_err_vz) {
		values.put("rf_err_vz", rf_err_vz);
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
	public RelativeState setSsX(double ss_x) {
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
	public RelativeState setSsY(double ss_y) {
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
	public RelativeState setSsZ(double ss_z) {
		values.put("ss_z", ss_z);
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
	public RelativeState setVirtErrX(double virt_err_x) {
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
	public RelativeState setVirtErrY(double virt_err_y) {
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
	public RelativeState setVirtErrZ(double virt_err_z) {
		values.put("virt_err_z", virt_err_z);
		return this;
	}

}
