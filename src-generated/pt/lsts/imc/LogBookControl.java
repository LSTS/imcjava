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
 *  IMC Message Log Book Control (104)<br/>
 *  Control history log.<br/>
 */

public class LogBookControl extends IMCMessage {

	public enum COMMAND {
		GET(0),
		CLEAR(1),
		GET_ERR(2),
		REPLY(3);

		protected long value;

		public long value() {
			return value;
		}

		COMMAND(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 104;

	public LogBookControl() {
		super(ID_STATIC);
	}

	public LogBookControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LogBookControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LogBookControl create(Object... values) {
		LogBookControl m = new LogBookControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LogBookControl clone(IMCMessage msg) throws Exception {

		LogBookControl m = new LogBookControl();
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

	public LogBookControl(COMMAND command, double htime, java.util.Collection<LogBookEntry> msg) {
		super(ID_STATIC);
		setCommand(command);
		setHtime(htime);
		if (msg != null)
			setMsg(msg);
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
	public LogBookControl setCommand(COMMAND command) {
		values.put("command", command.value());
		return this;
	}

	/**
	 *  @param command Command (as a String)
	 */
	public LogBookControl setCommandStr(String command) {
		setValue("command", command);
		return this;
	}

	/**
	 *  @param command Command (integer value)
	 */
	public LogBookControl setCommandVal(short command) {
		setValue("command", command);
		return this;
	}

	/**
	 *  @return Timestamp (s) - fp64_t
	 */
	public double getHtime() {
		return getDouble("htime");
	}

	/**
	 *  @param htime Timestamp (s)
	 */
	public LogBookControl setHtime(double htime) {
		values.put("htime", htime);
		return this;
	}

	/**
	 *  @return Messages - message-list
	 */
	public java.util.Vector<LogBookEntry> getMsg() {
		try {
			return getMessageList("msg", LogBookEntry.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param msg Messages
	 */
	public LogBookControl setMsg(java.util.Collection<LogBookEntry> msg) {
		values.put("msg", msg);
		return this;
	}

}
