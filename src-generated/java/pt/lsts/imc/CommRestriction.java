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
 *  IMC Message Communication Restriction (2010)<br/>
 *  This message is used to restrict the vehicle from using some communication means.<br/>
 */

public class CommRestriction extends IMCMessage {

	public static final short MEAN_SATELLITE = 0x01;
	public static final short MEAN_ACOUSTIC = 0x02;
	public static final short MEAN_WIFI = 0x04;
	public static final short MEAN_GSM = 0x08;

	public static final int ID_STATIC = 2010;

	public CommRestriction() {
		super(ID_STATIC);
	}

	public CommRestriction(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CommRestriction(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CommRestriction create(Object... values) {
		CommRestriction m = new CommRestriction();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CommRestriction clone(IMCMessage msg) throws Exception {

		CommRestriction m = new CommRestriction();
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

	public CommRestriction(short restriction, String reason) {
		super(ID_STATIC);
		setRestriction(restriction);
		if (reason != null)
			setReason(reason);
	}

	/**
	 *  @return Restricted Communication Means (bitfield) - uint8_t
	 */
	public short getRestriction() {
		return (short) getInteger("restriction");
	}

	/**
	 *  @param restriction Restricted Communication Means (bitfield)
	 */
	public CommRestriction setRestriction(short restriction) {
		values.put("restriction", restriction);
		return this;
	}

	/**
	 *  @return Reason - plaintext
	 */
	public String getReason() {
		return getString("reason");
	}

	/**
	 *  @param reason Reason
	 */
	public CommRestriction setReason(String reason) {
		values.put("reason", reason);
		return this;
	}

}
