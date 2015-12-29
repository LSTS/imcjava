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
 *  IMC Message Entity Monitoring State (503)<br/>
 */

public class EntityMonitoringState extends IMCMessage {

	public static final int ID_STATIC = 503;

	public EntityMonitoringState() {
		super(ID_STATIC);
	}

	public EntityMonitoringState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntityMonitoringState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EntityMonitoringState create(Object... values) {
		EntityMonitoringState m = new EntityMonitoringState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EntityMonitoringState clone(IMCMessage msg) throws Exception {

		EntityMonitoringState m = new EntityMonitoringState();
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

	public EntityMonitoringState(short mcount, String mnames, short ecount, String enames, short ccount, String cnames, String last_error, double last_error_time) {
		super(ID_STATIC);
		setMcount(mcount);
		if (mnames != null)
			setMnames(mnames);
		setEcount(ecount);
		if (enames != null)
			setEnames(enames);
		setCcount(ccount);
		if (cnames != null)
			setCnames(cnames);
		if (last_error != null)
			setLastError(last_error);
		setLastErrorTime(last_error_time);
	}

	/**
	 *  @return Entities monitored - Count - uint8_t
	 */
	public short getMcount() {
		return (short) getInteger("mcount");
	}

	/**
	 *  @param mcount Entities monitored - Count
	 */
	public EntityMonitoringState setMcount(short mcount) {
		values.put("mcount", mcount);
		return this;
	}

	/**
	 *  @return Entities monitored - Names - plaintext
	 */
	public String getMnames() {
		return getString("mnames");
	}

	/**
	 *  @param mnames Entities monitored - Names
	 */
	public EntityMonitoringState setMnames(String mnames) {
		values.put("mnames", mnames);
		return this;
	}

	/**
	 *  @return Entities with errors - Count - uint8_t
	 */
	public short getEcount() {
		return (short) getInteger("ecount");
	}

	/**
	 *  @param ecount Entities with errors - Count
	 */
	public EntityMonitoringState setEcount(short ecount) {
		values.put("ecount", ecount);
		return this;
	}

	/**
	 *  @return Entities with errors - Names - plaintext
	 */
	public String getEnames() {
		return getString("enames");
	}

	/**
	 *  @param enames Entities with errors - Names
	 */
	public EntityMonitoringState setEnames(String enames) {
		values.put("enames", enames);
		return this;
	}

	/**
	 *  @return Entities with critical errors - Count - uint8_t
	 */
	public short getCcount() {
		return (short) getInteger("ccount");
	}

	/**
	 *  @param ccount Entities with critical errors - Count
	 */
	public EntityMonitoringState setCcount(short ccount) {
		values.put("ccount", ccount);
		return this;
	}

	/**
	 *  @return Entities with critical errors - Names - plaintext
	 */
	public String getCnames() {
		return getString("cnames");
	}

	/**
	 *  @param cnames Entities with critical errors - Names
	 */
	public EntityMonitoringState setCnames(String cnames) {
		values.put("cnames", cnames);
		return this;
	}

	/**
	 *  @return Last Error -- Description - plaintext
	 */
	public String getLastError() {
		return getString("last_error");
	}

	/**
	 *  @param last_error Last Error -- Description
	 */
	public EntityMonitoringState setLastError(String last_error) {
		values.put("last_error", last_error);
		return this;
	}

	/**
	 *  @return Last Error -- Time (s) - fp64_t
	 */
	public double getLastErrorTime() {
		return getDouble("last_error_time");
	}

	/**
	 *  @param last_error_time Last Error -- Time (s)
	 */
	public EntityMonitoringState setLastErrorTime(double last_error_time) {
		values.put("last_error_time", last_error_time);
		return this;
	}

}
