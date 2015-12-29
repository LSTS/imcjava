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
 *  IMC Message TREX Command (652)<br/>
 *  This message is used to control TREX execution<br/>
 */

public class TrexCommand extends IMCMessage {

	public enum COMMAND {
		DISABLE(0),
		ENABLE(1),
		POST_GOAL(2),
		RECALL_GOAL(3),
		REQUEST_PLAN(4),
		REPORT_PLAN(5);

		protected long value;

		public long value() {
			return value;
		}

		COMMAND(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 652;

	public TrexCommand() {
		super(ID_STATIC);
	}

	public TrexCommand(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrexCommand(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TrexCommand create(Object... values) {
		TrexCommand m = new TrexCommand();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TrexCommand clone(IMCMessage msg) throws Exception {

		TrexCommand m = new TrexCommand();
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

	public TrexCommand(COMMAND command, String goal_id, String goal_xml) {
		super(ID_STATIC);
		setCommand(command);
		if (goal_id != null)
			setGoalId(goal_id);
		if (goal_xml != null)
			setGoalXml(goal_xml);
	}

	/**
	 *  @return Command (enumerated) - uint8_t
	 */
	public COMMAND getCommand() {
		try {
			COMMAND o = COMMAND.valueOf(getMessageType().getFieldPossibleValues("command").get(getLong("command")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCommandStr() {
		return getString("command");
	}

	public short getCommandVal() {
		return (short) getInteger("command");
	}

	/**
	 *  @param command Command (enumerated)
	 */
	public TrexCommand setCommand(COMMAND command) {
		values.put("command", command.value());
		return this;
	}

	/**
	 *  @param command Command (as a String)
	 */
	public TrexCommand setCommandStr(String command) {
		setValue("command", command);
		return this;
	}

	/**
	 *  @param command Command (integer value)
	 */
	public TrexCommand setCommandVal(short command) {
		setValue("command", command);
		return this;
	}

	/**
	 *  @return Goal Id - plaintext
	 */
	public String getGoalId() {
		return getString("goal_id");
	}

	/**
	 *  @param goal_id Goal Id
	 */
	public TrexCommand setGoalId(String goal_id) {
		values.put("goal_id", goal_id);
		return this;
	}

	/**
	 *  @return Goal XML - plaintext
	 */
	public String getGoalXml() {
		return getString("goal_xml");
	}

	/**
	 *  @param goal_xml Goal XML
	 */
	public TrexCommand setGoalXml(String goal_xml) {
		values.put("goal_xml", goal_xml);
		return this;
	}

}
