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
 *  IMC Message Plan DB State (557)<br/>
 *  Characterizes the state of the entire plan database.<br/>
 */

public class PlanDBState extends IMCMessage {

	public static final int ID_STATIC = 557;

	public PlanDBState() {
		super(ID_STATIC);
	}

	public PlanDBState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PlanDBState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static PlanDBState create(Object... values) {
		PlanDBState m = new PlanDBState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static PlanDBState clone(IMCMessage msg) throws Exception {

		PlanDBState m = new PlanDBState();
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

	public PlanDBState(int plan_count, long plan_size, double change_time, int change_sid, String change_sname, byte[] md5, java.util.Collection<PlanDBInformation> plans_info) {
		super(ID_STATIC);
		setPlanCount(plan_count);
		setPlanSize(plan_size);
		setChangeTime(change_time);
		setChangeSid(change_sid);
		if (change_sname != null)
			setChangeSname(change_sname);
		if (md5 != null)
			setMd5(md5);
		if (plans_info != null)
			setPlansInfo(plans_info);
	}

	/**
	 *  @return Plan -- Count - uint16_t
	 */
	public int getPlanCount() {
		return getInteger("plan_count");
	}

	/**
	 *  @param plan_count Plan -- Count
	 */
	public PlanDBState setPlanCount(int plan_count) {
		values.put("plan_count", plan_count);
		return this;
	}

	/**
	 *  @return Plan -- Size of all plans - uint32_t
	 */
	public long getPlanSize() {
		return getLong("plan_size");
	}

	/**
	 *  @param plan_size Plan -- Size of all plans
	 */
	public PlanDBState setPlanSize(long plan_size) {
		values.put("plan_size", plan_size);
		return this;
	}

	/**
	 *  @return Last Change -- Time (s) - fp64_t
	 */
	public double getChangeTime() {
		return getDouble("change_time");
	}

	/**
	 *  @param change_time Last Change -- Time (s)
	 */
	public PlanDBState setChangeTime(double change_time) {
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
	public PlanDBState setChangeSid(int change_sid) {
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
	public PlanDBState setChangeSname(String change_sname) {
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
	public PlanDBState setMd5(byte[] md5) {
		values.put("md5", md5);
		return this;
	}

	/**
	 *  @return Plan info - message-list
	 */
	public java.util.Vector<PlanDBInformation> getPlansInfo() {
		try {
			return getMessageList("plans_info", PlanDBInformation.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param plans_info Plan info
	 */
	public PlanDBState setPlansInfo(java.util.Collection<PlanDBInformation> plans_info) {
		values.put("plans_info", plans_info);
		return this;
	}

}
