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
 *  IMC Message Synchronization Task (3104)<br/>
 *  This message is used to describe an area synchronization task.<br/>
 */

public class SynchTask extends IMCMessage {

	public static final int ID_STATIC = 3104;

	public SynchTask() {
		super(ID_STATIC);
	}

	public SynchTask(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SynchTask(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SynchTask create(Object... values) {
		SynchTask m = new SynchTask();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SynchTask clone(IMCMessage msg) throws Exception {

		SynchTask m = new SynchTask();
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

	public SynchTask(int task_id, int feature_id, int time_window, double deadline) {
		super(ID_STATIC);
		setTaskId(task_id);
		setFeatureId(feature_id);
		setTimeWindow(time_window);
		setDeadline(deadline);
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
	public SynchTask setTaskId(int task_id) {
		values.put("task_id", task_id);
		return this;
	}

	/**
	 *  @return Geo Feature Identifier - uint16_t
	 */
	public int getFeatureId() {
		return getInteger("feature_id");
	}

	/**
	 *  @param feature_id Geo Feature Identifier
	 */
	public SynchTask setFeatureId(int feature_id) {
		values.put("feature_id", feature_id);
		return this;
	}

	/**
	 *  @return Synchronization Time Window (s) - uint16_t
	 */
	public int getTimeWindow() {
		return getInteger("time_window");
	}

	/**
	 *  @param time_window Synchronization Time Window (s)
	 */
	public SynchTask setTimeWindow(int time_window) {
		values.put("time_window", time_window);
		return this;
	}

	/**
	 *  @return Deadline (s) - fp64_t
	 */
	public double getDeadline() {
		return getDouble("deadline");
	}

	/**
	 *  @param deadline Deadline (s)
	 */
	public SynchTask setDeadline(double deadline) {
		values.put("deadline", deadline);
		return this;
	}

}
