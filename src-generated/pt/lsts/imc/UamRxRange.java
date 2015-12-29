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
 *  IMC Message UamRxRange (817)<br/>
 */

public class UamRxRange extends IMCMessage {

	public static final int ID_STATIC = 817;

	public UamRxRange() {
		super(ID_STATIC);
	}

	public UamRxRange(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UamRxRange(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UamRxRange create(Object... values) {
		UamRxRange m = new UamRxRange();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UamRxRange clone(IMCMessage msg) throws Exception {

		UamRxRange m = new UamRxRange();
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

	public UamRxRange(int seq, String sys, float value) {
		super(ID_STATIC);
		setSeq(seq);
		if (sys != null)
			setSys(sys);
		setValue(value);
	}

	/**
	 *  @return Sequence Id - uint16_t
	 */
	public int getSeq() {
		return getInteger("seq");
	}

	/**
	 *  @param seq Sequence Id
	 */
	public UamRxRange setSeq(int seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return System - plaintext
	 */
	public String getSys() {
		return getString("sys");
	}

	/**
	 *  @param sys System
	 */
	public UamRxRange setSys(String sys) {
		values.put("sys", sys);
		return this;
	}

	/**
	 *  @return Value - fp32_t
	 */
	public double getValue() {
		return getDouble("value");
	}

	/**
	 *  @param value Value
	 */
	public UamRxRange setValue(double value) {
		values.put("value", value);
		return this;
	}

}
