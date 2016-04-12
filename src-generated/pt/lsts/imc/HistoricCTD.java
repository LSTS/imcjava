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
 *  IMC Message Historic CTD (107)<br/>
 *  This message is used to store historic (transmitted afterwards) CTD data .<br/>
 */

public class HistoricCTD extends IMCMessage {

	public static final int ID_STATIC = 107;

	public HistoricCTD() {
		super(ID_STATIC);
	}

	public HistoricCTD(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistoricCTD(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static HistoricCTD create(Object... values) {
		HistoricCTD m = new HistoricCTD();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static HistoricCTD clone(IMCMessage msg) throws Exception {

		HistoricCTD m = new HistoricCTD();
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

	public HistoricCTD(float conductivity, float temperature, float depth) {
		super(ID_STATIC);
		setConductivity(conductivity);
		setTemperature(temperature);
		setDepth(depth);
	}

	/**
	 *  @return Conductivity (s/m) - fp32_t
	 */
	public double getConductivity() {
		return getDouble("conductivity");
	}

	/**
	 *  @param conductivity Conductivity (s/m)
	 */
	public HistoricCTD setConductivity(double conductivity) {
		values.put("conductivity", conductivity);
		return this;
	}

	/**
	 *  @return Temperature (°c) - fp32_t
	 */
	public double getTemperature() {
		return getDouble("temperature");
	}

	/**
	 *  @param temperature Temperature (°c)
	 */
	public HistoricCTD setTemperature(double temperature) {
		values.put("temperature", temperature);
		return this;
	}

	/**
	 *  @return Depth (m) - fp32_t
	 */
	public double getDepth() {
		return getDouble("depth");
	}

	/**
	 *  @param depth Depth (m)
	 */
	public HistoricCTD setDepth(double depth) {
		values.put("depth", depth);
		return this;
	}

}
