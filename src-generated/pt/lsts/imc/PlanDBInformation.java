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
 *  IMC Message Plan DB Information (558)<br/>
 */

public class PlanDBInformation extends IMCMessage {

	public static final int ID_STATIC = 558;

	public PlanDBInformation() {
		super(ID_STATIC);
	}

	public PlanDBInformation(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanDBInformation(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanDBInformation create(Object... values) {
		PlanDBInformation m = new PlanDBInformation();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanDBInformation clone(IMCMessage msg) throws Exception {

		PlanDBInformation m = new PlanDBInformation();
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

	public PlanDBInformation(String plan_id, int plan_size, double change_time, int change_sid, String change_sname, byte[] md5) {
		super(ID_STATIC);
		if (plan_id != null)
			setPlanId(plan_id);
		setPlanSize(plan_size);
		setChangeTime(change_time);
		setChangeSid(change_sid);
		if (change_sname != null)
			setChangeSname(change_sname);
		if (md5 != null)
			setMd5(md5);
	}

	/**
	 *  @return Plan ID - plaintext
	 */
	public String getPlanId() {
		return getString("plan_id");
	}

	/**
	 *  @param plan_id Plan ID
	 */
	public PlanDBInformation setPlanId(String plan_id) {
		values.put("plan_id", plan_id);
		return this;
	}

	/**
	 *  @return Plan Size - uint16_t
	 */
	public int getPlanSize() {
		return getInteger("plan_size");
	}

	/**
	 *  @param plan_size Plan Size
	 */
	public PlanDBInformation setPlanSize(int plan_size) {
		values.put("plan_size", plan_size);
		return this;
	}

	/**
	 *  @return Last Changed -- Time - fp64_t
	 */
	public double getChangeTime() {
		return getDouble("change_time");
	}

	/**
	 *  @param change_time Last Changed -- Time
	 */
	public PlanDBInformation setChangeTime(double change_time) {
		values.put("change_time", change_time);
		return this;
	}

	/**
	 *  @return Last Change -- Source Address - uint16_t
	 */
	public int getChangeSid() {
		return getInteger("change_sid");
	}

	/**
	 *  @param change_sid Last Change -- Source Address
	 */
	public PlanDBInformation setChangeSid(int change_sid) {
		values.put("change_sid", change_sid);
		return this;
	}

	/**
	 *  @return Last Change -- Source Name - plaintext
	 */
	public String getChangeSname() {
		return getString("change_sname");
	}

	/**
	 *  @param change_sname Last Change -- Source Name
	 */
	public PlanDBInformation setChangeSname(String change_sname) {
		values.put("change_sname", change_sname);
		return this;
	}

	/**
	 *  @return MD5 - rawdata
	 */
	public byte[] getMd5() {
		return getRawData("md5");
	}

	/**
	 *  @param md5 MD5
	 */
	public PlanDBInformation setMd5(byte[] md5) {
		values.put("md5", md5);
		return this;
	}

}
