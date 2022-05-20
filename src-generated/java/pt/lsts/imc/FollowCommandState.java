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
 *  IMC Message Follow Command State (498)<br/>
 */

public class FollowCommandState extends IMCMessage {

	public enum STATE {
		WAIT(1),
		MOVING(2),
		STOPPED(3),
		BAD_COMMAND(4),
		TIMEOUT(5);

		protected long value;

		public long value() {
			return value;
		}

		STATE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 498;

	public FollowCommandState() {
		super(ID_STATIC);
	}

	public FollowCommandState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FollowCommandState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FollowCommandState create(Object... values) {
		FollowCommandState m = new FollowCommandState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FollowCommandState clone(IMCMessage msg) throws Exception {

		FollowCommandState m = new FollowCommandState();
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

	public FollowCommandState(int control_src, short control_ent, Command command, STATE state) {
		super(ID_STATIC);
		setControlSrc(control_src);
		setControlEnt(control_ent);
		if (command != null)
			setCommand(command);
		setState(state);
	}

	/**
	 *  @return Controlling Source - uint16_t
	 */
	public int getControlSrc() {
		return getInteger("control_src");
	}

	/**
	 *  @param control_src Controlling Source
	 */
	public FollowCommandState setControlSrc(int control_src) {
		values.put("control_src", control_src);
		return this;
	}

	/**
	 *  @return Controlling Entity - uint8_t
	 */
	public short getControlEnt() {
		return (short) getInteger("control_ent");
	}

	/**
	 *  @param control_ent Controlling Entity
	 */
	public FollowCommandState setControlEnt(short control_ent) {
		values.put("control_ent", control_ent);
		return this;
	}

	/**
	 *  @return Command - message
	 */
	public Command getCommand() {
		try {
			IMCMessage obj = getMessage("command");
			if (obj instanceof Command)
				return (Command) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param command Command
	 */
	public FollowCommandState setCommand(Command command) {
		values.put("command", command);
		return this;
	}

	/**
	 *  @return State (enumerated) - uint8_t
	 */
	public STATE getState() {
		try {
			STATE o = STATE.valueOf(getMessageType().getFieldPossibleValues("state").get(getLong("state")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStateStr() {
		return getString("state");
	}

	public short getStateVal() {
		return (short) getInteger("state");
	}

	/**
	 *  @param state State (enumerated)
	 */
	public FollowCommandState setState(STATE state) {
		values.put("state", state.value());
		return this;
	}

	/**
	 *  @param state State (as a String)
	 */
	public FollowCommandState setStateStr(String state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @param state State (integer value)
	 */
	public FollowCommandState setStateVal(short state) {
		setValue("state", state);
		return this;
	}

}
