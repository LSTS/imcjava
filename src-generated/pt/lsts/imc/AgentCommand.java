/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2014, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Agent Command (851)<br/>
 */

public class AgentCommand extends IMCMessage {

	public enum CMD {
		INSTANTIATION_REQUEST(1),
		INSTANTIATION_SUCCESS(2),
		INSTANTIATION_FAILURE(3),
		STATE_REQUEST(4),
		STATE_REPLY(5);

		protected long value;

		public long value() {
			return value;
		}

		CMD(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 851;

	public AgentCommand() {
		super(ID_STATIC);
	}

	public AgentCommand(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AgentCommand(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AgentCommand create(Object... values) {
		AgentCommand m = new AgentCommand();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AgentCommand clone(IMCMessage msg) throws Exception {

		AgentCommand m = new AgentCommand();
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

	public AgentCommand(CMD cmd, int request_id, String info, java.util.Collection<Agent> args) {
		super(ID_STATIC);
		setCmd(cmd);
		setRequestId(request_id);
		if (info != null)
			setInfo(info);
		if (args != null)
			setArgs(args);
	}

	/**
	 *  @return Command (enumerated) - uint8_t
	 */
	public CMD getCmd() {
		try {
			CMD o = CMD.valueOf(getMessageType().getFieldPossibleValues("cmd").get(getLong("cmd")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 *  @param cmd Command (enumerated)
	 */
	public AgentCommand setCmd(CMD cmd) {
		values.put("cmd", cmd.value());
		return this;
	}

	/**
	 *  @param cmd Command (as a String)
	 */
	public AgentCommand setCmd(String cmd) {
		setValue("cmd", cmd);
		return this;
	}

	/**
	 *  @param cmd Command (integer value)
	 */
	public AgentCommand setCmd(short cmd) {
		setValue("cmd", cmd);
		return this;
	}

	/**
	 *  @return Request Identifier - uint16_t
	 */
	public int getRequestId() {
		return getInteger("request_id");
	}

	/**
	 *  @param request_id Request Identifier
	 */
	public AgentCommand setRequestId(int request_id) {
		values.put("request_id", request_id);
		return this;
	}

	/**
	 *  @return Extra Information - plaintext
	 */
	public String getInfo() {
		return getString("info");
	}

	/**
	 *  @param info Extra Information
	 */
	public AgentCommand setInfo(String info) {
		values.put("info", info);
		return this;
	}

	/**
	 *  @return Arguments - message-list
	 */
	public java.util.Vector<Agent> getArgs() {
		try {
			return getMessageList("args", Agent.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param args Arguments
	 */
	public AgentCommand setArgs(java.util.Collection<Agent> args) {
		values.put("args", args);
		return this;
	}

}
