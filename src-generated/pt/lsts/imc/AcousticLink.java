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
 *  IMC Message Acoustic Link Quality (214)<br/>
 *  This message is used to report the perceived link quality to other<br/>
 *  acoustic peers.<br/>
 */

public class AcousticLink extends IMCMessage {

	public static final int ID_STATIC = 214;

	public AcousticLink() {
		super(ID_STATIC);
	}

	public AcousticLink(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AcousticLink(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AcousticLink create(Object... values) {
		AcousticLink m = new AcousticLink();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AcousticLink clone(IMCMessage msg) throws Exception {

		AcousticLink m = new AcousticLink();
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

	public AcousticLink(String peer, float rssi, int integrity) {
		super(ID_STATIC);
		if (peer != null)
			setPeer(peer);
		setRssi(rssi);
		setIntegrity(integrity);
	}

	/**
	 *  @return Peer Name - plaintext
	 */
	public String getPeer() {
		return getString("peer");
	}

	/**
	 *  @param peer Peer Name
	 */
	public AcousticLink setPeer(String peer) {
		values.put("peer", peer);
		return this;
	}

	/**
	 *  @return Received Signal Strength Indicator (db) - fp32_t
	 */
	public double getRssi() {
		return getDouble("rssi");
	}

	/**
	 *  @param rssi Received Signal Strength Indicator (db)
	 */
	public AcousticLink setRssi(double rssi) {
		values.put("rssi", rssi);
		return this;
	}

	/**
	 *  @return Signal Integrity Level - uint16_t
	 */
	public int getIntegrity() {
		return getInteger("integrity");
	}

	/**
	 *  @param integrity Signal Integrity Level
	 */
	public AcousticLink setIntegrity(int integrity) {
		values.put("integrity", integrity);
		return this;
	}

}
