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
 *  IMC Message Temporal Action (911)<br/>
 *  This message will hold an action that needs to be executed at specified time interval.<br/>
 */

public class TemporalAction extends IMCMessage {

	public enum STATUS {
		UKNOWN(0),
		IGNORED(1),
		SCHEDULED(2),
		FAILED(3),
		CANCELLED(4),
		FINISHED(5);

		protected long value;

		public long value() {
			return value;
		}

		STATUS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 911;

	public TemporalAction() {
		super(ID_STATIC);
	}

	public TemporalAction(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TemporalAction(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TemporalAction create(Object... values) {
		TemporalAction m = new TemporalAction();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TemporalAction clone(IMCMessage msg) throws Exception {

		TemporalAction m = new TemporalAction();
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

	public TemporalAction(String action_id, int system_id, STATUS status, double start_time, double duration, PlanSpecification action) {
		super(ID_STATIC);
		if (action_id != null)
			setActionId(action_id);
		setSystemId(system_id);
		setStatus(status);
		setStartTime(start_time);
		setDuration(duration);
		if (action != null)
			setAction(action);
	}

	/**
	 *  @return Action Identifier - plaintext
	 */
	public String getActionId() {
		return getString("action_id");
	}

	/**
	 *  @param action_id Action Identifier
	 */
	public TemporalAction setActionId(String action_id) {
		values.put("action_id", action_id);
		return this;
	}

	/**
	 *  @return System Identifier - uint16_t
	 */
	public int getSystemId() {
		return getInteger("system_id");
	}

	/**
	 *  @param system_id System Identifier
	 */
	public TemporalAction setSystemId(int system_id) {
		values.put("system_id", system_id);
		return this;
	}

	/**
	 *  @return Status (enumerated) - uint8_t
	 */
	public STATUS getStatus() {
		try {
			STATUS o = STATUS.valueOf(getMessageType().getFieldPossibleValues("status").get(getLong("status")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStatusStr() {
		return getString("status");
	}

	public short getStatusVal() {
		return (short) getInteger("status");
	}

	/**
	 *  @param status Status (enumerated)
	 */
	public TemporalAction setStatus(STATUS status) {
		values.put("status", status.value());
		return this;
	}

	/**
	 *  @param status Status (as a String)
	 */
	public TemporalAction setStatusStr(String status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @param status Status (integer value)
	 */
	public TemporalAction setStatusVal(short status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @return Start Time - fp64_t
	 */
	public double getStartTime() {
		return getDouble("start_time");
	}

	/**
	 *  @param start_time Start Time
	 */
	public TemporalAction setStartTime(double start_time) {
		values.put("start_time", start_time);
		return this;
	}

	/**
	 *  @return Duration - fp64_t
	 */
	public double getDuration() {
		return getDouble("duration");
	}

	/**
	 *  @param duration Duration
	 */
	public TemporalAction setDuration(double duration) {
		values.put("duration", duration);
		return this;
	}

	/**
	 *  @return Action - message
	 */
	public PlanSpecification getAction() {
		try {
			IMCMessage obj = getMessage("action");
			if (obj instanceof PlanSpecification)
				return (PlanSpecification) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param action Action
	 */
	public TemporalAction setAction(PlanSpecification action) {
		values.put("action", action);
		return this;
	}

}
