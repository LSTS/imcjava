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
 *  IMC Message Remote Command (188)<br/>
 *  Command to remote system. If a system receives a RemoteCommand and it isn't the intended recipient, then it should<br/>
 *  resend it.<br/>
 */

@SuppressWarnings("unchecked")
public class RemoteCommand extends RemoteData {

	public static final int ID_STATIC = 188;

	public RemoteCommand() {
		super(ID_STATIC);
	}

	public RemoteCommand(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public RemoteCommand(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static RemoteCommand create(Object... values) {
		RemoteCommand m = new RemoteCommand();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static RemoteCommand clone(IMCMessage msg) throws Exception {

		RemoteCommand m = new RemoteCommand();
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

	public RemoteCommand(int original_source, int destination, double timeout, IMCMessage cmd) {
		super(ID_STATIC);
		setOriginalSource(original_source);
		setDestination(destination);
		setTimeout(timeout);
		if (cmd != null)
			setCmd(cmd);
	}

	/**
	 *  @return Original Source - uint16_t
	 */
	public int getOriginalSource() {
		return getInteger("original_source");
	}

	/**
	 *  @param original_source Original Source
	 */
	public RemoteCommand setOriginalSource(int original_source) {
		values.put("original_source", original_source);
		return this;
	}

	/**
	 *  @return Destination - uint16_t
	 */
	public int getDestination() {
		return getInteger("destination");
	}

	/**
	 *  @param destination Destination
	 */
	public RemoteCommand setDestination(int destination) {
		values.put("destination", destination);
		return this;
	}

	/**
	 *  @return Timeout (s) - fp64_t
	 */
	public double getTimeout() {
		return getDouble("timeout");
	}

	/**
	 *  @param timeout Timeout (s)
	 */
	public RemoteCommand setTimeout(double timeout) {
		values.put("timeout", timeout);
		return this;
	}

	/**
	 *  @return Command - message
	 */
	public IMCMessage getCmd() {
		return getMessage("cmd");
	}

	public <T extends IMCMessage> T getCmd(Class<T> clazz) throws Exception {
		return getMessage(clazz, "cmd");
	}

	/**
	 *  @param cmd Command
	 */
	public RemoteCommand setCmd(IMCMessage cmd) {
		values.put("cmd", cmd);
		return this;
	}

}
