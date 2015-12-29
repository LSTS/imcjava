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
 *  IMC Message Autopilot Mode (511)<br/>
 *  Reports autopilot mode.<br/>
 */

public class AutopilotMode extends IMCMessage {

	public enum AUTONOMY {
		MANUAL(0),
		ASSISTED(1),
		AUTO(2);

		protected long value;

		public long value() {
			return value;
		}

		AUTONOMY(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 511;

	public AutopilotMode() {
		super(ID_STATIC);
	}

	public AutopilotMode(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AutopilotMode(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AutopilotMode create(Object... values) {
		AutopilotMode m = new AutopilotMode();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AutopilotMode clone(IMCMessage msg) throws Exception {

		AutopilotMode m = new AutopilotMode();
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

	public AutopilotMode(AUTONOMY autonomy, String mode) {
		super(ID_STATIC);
		setAutonomy(autonomy);
		if (mode != null)
			setMode(mode);
	}

	/**
	 *  @return Autonomy Level (enumerated) - uint8_t
	 */
	public AUTONOMY getAutonomy() {
		try {
			AUTONOMY o = AUTONOMY.valueOf(getMessageType().getFieldPossibleValues("autonomy").get(getLong("autonomy")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAutonomyStr() {
		return getString("autonomy");
	}

	public short getAutonomyVal() {
		return (short) getInteger("autonomy");
	}

	/**
	 *  @param autonomy Autonomy Level (enumerated)
	 */
	public AutopilotMode setAutonomy(AUTONOMY autonomy) {
		values.put("autonomy", autonomy.value());
		return this;
	}

	/**
	 *  @param autonomy Autonomy Level (as a String)
	 */
	public AutopilotMode setAutonomyStr(String autonomy) {
		setValue("autonomy", autonomy);
		return this;
	}

	/**
	 *  @param autonomy Autonomy Level (integer value)
	 */
	public AutopilotMode setAutonomyVal(short autonomy) {
		setValue("autonomy", autonomy);
		return this;
	}

	/**
	 *  @return Mode - plaintext
	 */
	public String getMode() {
		return getString("mode");
	}

	/**
	 *  @param mode Mode
	 */
	public AutopilotMode setMode(String mode) {
		values.put("mode", mode);
		return this;
	}

}
