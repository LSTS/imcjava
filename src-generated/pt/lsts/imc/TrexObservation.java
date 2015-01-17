/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message TREX Observation (651)<br/>
 *  This message is sent to TREX to post timeline observations<br/>
 */

public class TrexObservation extends IMCMessage {

	public static final int ID_STATIC = 651;

	public TrexObservation() {
		super(ID_STATIC);
	}

	public TrexObservation(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrexObservation(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TrexObservation create(Object... values) {
		TrexObservation m = new TrexObservation();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TrexObservation clone(IMCMessage msg) throws Exception {

		TrexObservation m = new TrexObservation();
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

	public TrexObservation(String timeline, String predicate, String attributes) {
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
	public TrexObservation setTimeline(String timeline) {
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
	public TrexObservation setPredicate(String predicate) {
		values.put("predicate", predicate);
		return this;
	}

	/**
	 *  @return Attributes (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getAttributes() {
		return getTupleList("attributes");
	}

	/**
	 *  @param attributes Attributes (tuplelist)
	 */
	public TrexObservation setAttributes(java.util.LinkedHashMap<String, ?> attributes) {
		String val = encodeTupleList(attributes);
		values.put("attributes", val);
		return this;
	}

	public TrexObservation setAttributes(String attributes) {
		values.put("attributes", attributes);
		return this;
	}

}
