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
 *  IMC Message TREX Token (657)<br/>
 */

public class TrexToken extends IMCMessage {

	public static final int ID_STATIC = 657;

	public TrexToken() {
		super(ID_STATIC);
	}

	public TrexToken(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrexToken(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TrexToken create(Object... values) {
		TrexToken m = new TrexToken();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TrexToken clone(IMCMessage msg) throws Exception {

		TrexToken m = new TrexToken();
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

	public TrexToken(String timeline, String predicate, java.util.Collection<TrexAttribute> attributes) {
		super(ID_STATIC);
		if (timeline != null)
			setTimeline(timeline);
		if (predicate != null)
			setPredicate(predicate);
		if (attributes != null)
			setAttributes(attributes);
	}

	/**
	 *  @return Timeline - plaintext
	 */
	public String getTimeline() {
		return getString("timeline");
	}

	/**
	 *  @param timeline Timeline
	 */
	public TrexToken setTimeline(String timeline) {
		values.put("timeline", timeline);
		return this;
	}

	/**
	 *  @return Predicate - plaintext
	 */
	public String getPredicate() {
		return getString("predicate");
	}

	/**
	 *  @param predicate Predicate
	 */
	public TrexToken setPredicate(String predicate) {
		values.put("predicate", predicate);
		return this;
	}

	/**
	 *  @return Attributes - message-list
	 */
	public java.util.Vector<TrexAttribute> getAttributes() {
		try {
			return getMessageList("attributes", TrexAttribute.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param attributes Attributes
	 */
	public TrexToken setAttributes(java.util.Collection<TrexAttribute> attributes) {
		values.put("attributes", attributes);
		return this;
	}

}
