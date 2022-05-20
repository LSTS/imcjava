/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Task Status (3103)<br/>
 */

public class TaskStatus extends TaskAdminArgs {

	public enum STATUS {
		COMPLETED(1),
		IN_PROGRESS(2),
		ERROR(3),
		ASSIGNED(4);

		protected long value;

		public long value() {
			return value;
		}

		STATUS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 3103;

	public TaskStatus() {
		super(ID_STATIC);
	}

	public TaskStatus(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TaskStatus(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TaskStatus create(Object... values) {
		TaskStatus m = new TaskStatus();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TaskStatus clone(IMCMessage msg) throws Exception {

		TaskStatus m = new TaskStatus();
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

	public TaskStatus(int task_id, STATUS status, short progress, short quality) {
		super(ID_STATIC);
		setTaskId(task_id);
		setStatus(status);
		setProgress(progress);
		setQuality(quality);
	}

	/**
	 *  @return Task Identifier - uint16_t
	 */
	public int getTaskId() {
		return getInteger("task_id");
	}

	/**
	 *  @param task_id Task Identifier
	 */
	public TaskStatus setTaskId(int task_id) {
		values.put("task_id", task_id);
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
	public TaskStatus setStatus(STATUS status) {
		values.put("status", status.value());
		return this;
	}

	/**
	 *  @param status Status (as a String)
	 */
	public TaskStatus setStatusStr(String status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @param status Status (integer value)
	 */
	public TaskStatus setStatusVal(short status) {
		setValue("status", status);
		return this;
	}

	/**
	 *  @return Progress (%) - uint8_t
	 */
	public short getProgress() {
		return (short) getInteger("progress");
	}

	/**
	 *  @param progress Progress (%)
	 */
	public TaskStatus setProgress(short progress) {
		values.put("progress", progress);
		return this;
	}

	/**
	 *  @return Quality (%) - uint8_t
	 */
	public short getQuality() {
		return (short) getInteger("quality");
	}

	/**
	 *  @param quality Quality (%)
	 */
	public TaskStatus setQuality(short quality) {
		values.put("quality", quality);
		return this;
	}

}
