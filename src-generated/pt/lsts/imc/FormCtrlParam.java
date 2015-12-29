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
 *  IMC Message Formation Control Parameters (820)<br/>
 *  Formation controller paramenters, as: trajectory gains, control boundary layer thickness, and formation shape gains.<br/>
 */

public class FormCtrlParam extends IMCMessage {

	public enum ACTION {
		REQ(0),
		SET(1),
		REP(2);

		protected long value;

		public long value() {
			return value;
		}

		ACTION(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 820;

	public FormCtrlParam() {
		super(ID_STATIC);
	}

	public FormCtrlParam(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormCtrlParam(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormCtrlParam create(Object... values) {
		FormCtrlParam m = new FormCtrlParam();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormCtrlParam clone(IMCMessage msg) throws Exception {

		FormCtrlParam m = new FormCtrlParam();
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

	public FormCtrlParam(ACTION Action, float LonGain, float LatGain, long BondThick, float LeadGain, float DeconflGain) {
		super(ID_STATIC);
		setAction(Action);
		setLonGain(LonGain);
		setLatGain(LatGain);
		setBondThick(BondThick);
		setLeadGain(LeadGain);
		setDeconflGain(DeconflGain);
	}

	/**
	 *  @return Action (enumerated) - uint8_t
	 */
	public ACTION getAction() {
		try {
			ACTION o = ACTION.valueOf(getMessageType().getFieldPossibleValues("Action").get(getLong("Action")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getActionStr() {
		return getString("Action");
	}

	public short getActionVal() {
		return (short) getInteger("Action");
	}

	/**
	 *  @param Action Action (enumerated)
	 */
	public FormCtrlParam setAction(ACTION Action) {
		values.put("Action", Action.value());
		return this;
	}

	/**
	 *  @param Action Action (as a String)
	 */
	public FormCtrlParam setActionStr(String Action) {
		setValue("Action", Action);
		return this;
	}

	/**
	 *  @param Action Action (integer value)
	 */
	public FormCtrlParam setActionVal(short Action) {
		setValue("Action", Action);
		return this;
	}

	/**
	 *  @return Longitudinal Gain - fp32_t
	 */
	public double getLonGain() {
		return getDouble("LonGain");
	}

	/**
	 *  @param LonGain Longitudinal Gain
	 */
	public FormCtrlParam setLonGain(double LonGain) {
		values.put("LonGain", LonGain);
		return this;
	}

	/**
	 *  @return Lateral Gain - fp32_t
	 */
	public double getLatGain() {
		return getDouble("LatGain");
	}

	/**
	 *  @param LatGain Lateral Gain
	 */
	public FormCtrlParam setLatGain(double LatGain) {
		values.put("LatGain", LatGain);
		return this;
	}

	/**
	 *  @return Boundary Layer Thickness - uint32_t
	 */
	public long getBondThick() {
		return getLong("BondThick");
	}

	/**
	 *  @param BondThick Boundary Layer Thickness
	 */
	public FormCtrlParam setBondThick(long BondThick) {
		values.put("BondThick", BondThick);
		return this;
	}

	/**
	 *  @return Leader Gain - fp32_t
	 */
	public double getLeadGain() {
		return getDouble("LeadGain");
	}

	/**
	 *  @param LeadGain Leader Gain
	 */
	public FormCtrlParam setLeadGain(double LeadGain) {
		values.put("LeadGain", LeadGain);
		return this;
	}

	/**
	 *  @return Deconfliction Gain - fp32_t
	 */
	public double getDeconflGain() {
		return getDouble("DeconflGain");
	}

	/**
	 *  @param DeconflGain Deconfliction Gain
	 */
	public FormCtrlParam setDeconflGain(double DeconflGain) {
		values.put("DeconflGain", DeconflGain);
		return this;
	}

}
