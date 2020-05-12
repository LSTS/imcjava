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
 *  IMC Message APM Status (906)<br/>
 *  StatusText message from ardupilot.<br/>
 */

public class ApmStatus extends IMCMessage {

	public enum SEVERITY {
		EMERGENCY(0),
		ALERT(1),
		CRITICAL(2),
		ERROR(3),
		WARNING(4),
		NOTICE(5),
		INFO(6),
		DEBUG(7);

		protected long value;

		public long value() {
			return value;
		}

		SEVERITY(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 906;

	public ApmStatus() {
		super(ID_STATIC);
	}

	public ApmStatus(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ApmStatus(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ApmStatus create(Object... values) {
		ApmStatus m = new ApmStatus();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ApmStatus clone(IMCMessage msg) throws Exception {

		ApmStatus m = new ApmStatus();
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

	public ApmStatus(SEVERITY severity, String text) {
		super(ID_STATIC);
		setSeverity(severity);
		if (text != null)
			setText(text);
	}

	/**
	 *  @return Severity (enumerated) - uint8_t
	 */
	public SEVERITY getSeverity() {
		try {
			SEVERITY o = SEVERITY.valueOf(getMessageType().getFieldPossibleValues("severity").get(getLong("severity")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSeverityStr() {
		return getString("severity");
	}

	public short getSeverityVal() {
		return (short) getInteger("severity");
	}

	/**
	 *  @param severity Severity (enumerated)
	 */
	public ApmStatus setSeverity(SEVERITY severity) {
		values.put("severity", severity.value());
		return this;
	}

	/**
	 *  @param severity Severity (as a String)
	 */
	public ApmStatus setSeverityStr(String severity) {
		setValue("severity", severity);
		return this;
	}

	/**
	 *  @param severity Severity (integer value)
	 */
	public ApmStatus setSeverityVal(short severity) {
		setValue("severity", severity);
		return this;
	}

	/**
	 *  @return Text - plaintext
	 */
	public String getText() {
		return getString("text");
	}

	/**
	 *  @param text Text
	 */
	public ApmStatus setText(String text) {
		values.put("text", text);
		return this;
	}

}
