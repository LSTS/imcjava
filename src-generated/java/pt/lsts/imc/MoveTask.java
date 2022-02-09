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
 *  IMC Message Move Task (3102)<br/>
 *  This message is used to describe an area surveying task.<br/>
 */

public class MoveTask extends TaskAdminArgs {

	public static final int ID_STATIC = 3102;

	public MoveTask() {
		super(ID_STATIC);
	}

	public MoveTask(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MoveTask(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static MoveTask create(Object... values) {
		MoveTask m = new MoveTask();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static MoveTask clone(IMCMessage msg) throws Exception {

		MoveTask m = new MoveTask();
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

	public MoveTask(int task_id, MapPoint destination, double deadline) {
		super(ID_STATIC);
		setTaskId(task_id);
		if (destination != null)
			setDestination(destination);
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
	public MoveTask setTaskId(int task_id) {
		values.put("task_id", task_id);
		return this;
	}

	/**
	 *  @return Destination - message
	 */
	public MapPoint getDestination() {
		try {
			IMCMessage obj = getMessage("destination");
			if (obj instanceof MapPoint)
				return (MapPoint) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param destination Destination
	 */
	public MoveTask setDestination(MapPoint destination) {
		values.put("destination", destination);
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
	public MoveTask setDeadline(double deadline) {
		values.put("deadline", deadline);
		return this;
	}

}
