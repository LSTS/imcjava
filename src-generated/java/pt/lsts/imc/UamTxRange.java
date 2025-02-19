/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message UamTxRange (818)<br/>
 *  Request an acoustic modem driver to measure the distance to another system.<br/>
 */

public class UamTxRange extends IMCMessage {

	public static final int ID_STATIC = 818;

	public UamTxRange() {
		super(ID_STATIC);
	}

	public UamTxRange(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UamTxRange(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UamTxRange create(Object... values) {
		UamTxRange m = new UamTxRange();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UamTxRange clone(IMCMessage msg) throws Exception {

		UamTxRange m = new UamTxRange();
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

	public UamTxRange(int seq, String sys_dst, float timeout) {
		super(ID_STATIC);
		setSeq(seq);
		if (sys_dst != null)
			setSysDst(sys_dst);
		setTimeout(timeout);
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
	public UamTxRange setSeq(int seq) {
		values.put("seq", seq);
		return this;
	}

	/**
	 *  @return Destination System - plaintext
	 */
	public String getSysDst() {
		return getString("sys_dst");
	}

	/**
	 *  @param sys_dst Destination System
	 */
	public UamTxRange setSysDst(String sys_dst) {
		values.put("sys_dst", sys_dst);
		return this;
	}

	/**
	 *  @return Timeout (s) - fp32_t
	 */
	public double getTimeout() {
		return getDouble("timeout");
	}

	/**
	 *  @param timeout Timeout (s)
	 */
	public UamTxRange setTimeout(double timeout) {
		values.put("timeout", timeout);
		return this;
	}

}
