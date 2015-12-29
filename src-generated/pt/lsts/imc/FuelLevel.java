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
 *  IMC Message Fuel Level (279)<br/>
 *  Report of fuel level.<br/>
 */

public class FuelLevel extends IMCMessage {

	public static final int ID_STATIC = 279;

	public FuelLevel() {
		super(ID_STATIC);
	}

	public FuelLevel(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FuelLevel(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FuelLevel create(Object... values) {
		FuelLevel m = new FuelLevel();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FuelLevel clone(IMCMessage msg) throws Exception {

		FuelLevel m = new FuelLevel();
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

	public FuelLevel(float value, float confidence, String opmodes) {
		super(ID_STATIC);
		setValue(value);
		setConfidence(confidence);
		if (opmodes != null)
			setOpmodes(opmodes);
	}

	/**
	 *  @return Value (%) - fp32_t
	 */
	public double getValue() {
		return getDouble("value");
	}

	/**
	 *  @param value Value (%)
	 */
	public FuelLevel setValue(double value) {
		values.put("value", value);
		return this;
	}

	/**
	 *  @return Confidence Level (%) - fp32_t
	 */
	public double getConfidence() {
		return getDouble("confidence");
	}

	/**
	 *  @param confidence Confidence Level (%)
	 */
	public FuelLevel setConfidence(double confidence) {
		values.put("confidence", confidence);
		return this;
	}

	/**
	 *  @return Operation Modes (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getOpmodes() {
		return getTupleList("opmodes");
	}

	/**
	 *  @param opmodes Operation Modes (tuplelist)
	 */
	public FuelLevel setOpmodes(java.util.LinkedHashMap<String, ?> opmodes) {
		String val = encodeTupleList(opmodes);
		values.put("opmodes", val);
		return this;
	}

	public FuelLevel setOpmodes(String opmodes) {
		values.put("opmodes", opmodes);
		return this;
	}

}
