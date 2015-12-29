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
 *  IMC Message LBL Range Acceptance (357)<br/>
 *  When the vehicle uses Long Base Line navigation, this message<br/>
 *  notifies that a new range was received from one of the acoustics<br/>
 *  transponders. The message fields are used to identify the range<br/>
 *  value and the transponder name. Also, this message has an<br/>
 *  acceptance field that indicates whether a LBL range was accepted<br/>
 *  or rejected, and if rejected, the reason why.<br/>
 */

public class LblRangeAcceptance extends IMCMessage {

	public enum ACCEPTANCE {
		ACCEPTED(0),
		ABOVE_THRESHOLD(1),
		SINGULAR(2),
		NO_INFO(3),
		AT_SURFACE(4);

		protected long value;

		public long value() {
			return value;
		}

		ACCEPTANCE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 357;

	public LblRangeAcceptance() {
		super(ID_STATIC);
	}

	public LblRangeAcceptance(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LblRangeAcceptance(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LblRangeAcceptance create(Object... values) {
		LblRangeAcceptance m = new LblRangeAcceptance();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LblRangeAcceptance clone(IMCMessage msg) throws Exception {

		LblRangeAcceptance m = new LblRangeAcceptance();
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

	public LblRangeAcceptance(short id, float range, ACCEPTANCE acceptance) {
		super(ID_STATIC);
		setId(id);
		setRange(range);
		setAcceptance(acceptance);
	}

	/**
	 *  @return Beacon Identification Number - uint8_t
	 */
	public short getId() {
		return (short) getInteger("id");
	}

	/**
	 *  @param id Beacon Identification Number
	 */
	public LblRangeAcceptance setId(short id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Range (m) - fp32_t
	 */
	public double getRange() {
		return getDouble("range");
	}

	/**
	 *  @param range Range (m)
	 */
	public LblRangeAcceptance setRange(double range) {
		values.put("range", range);
		return this;
	}

	/**
	 *  @return Acceptance (enumerated) - uint8_t
	 */
	public ACCEPTANCE getAcceptance() {
		try {
			ACCEPTANCE o = ACCEPTANCE.valueOf(getMessageType().getFieldPossibleValues("acceptance").get(getLong("acceptance")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getAcceptanceStr() {
		return getString("acceptance");
	}

	public short getAcceptanceVal() {
		return (short) getInteger("acceptance");
	}

	/**
	 *  @param acceptance Acceptance (enumerated)
	 */
	public LblRangeAcceptance setAcceptance(ACCEPTANCE acceptance) {
		values.put("acceptance", acceptance.value());
		return this;
	}

	/**
	 *  @param acceptance Acceptance (as a String)
	 */
	public LblRangeAcceptance setAcceptanceStr(String acceptance) {
		setValue("acceptance", acceptance);
		return this;
	}

	/**
	 *  @param acceptance Acceptance (integer value)
	 */
	public LblRangeAcceptance setAcceptanceVal(short acceptance) {
		setValue("acceptance", acceptance);
		return this;
	}

}
