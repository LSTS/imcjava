/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message ADCP Beam Measurements (1016)<br/>
 *  Measurement from one specific beam at the given CellPosition.<br/>
 *  Water Velocity is provided in the chosen Coordinate system.<br/>
 *  Amplitude and Correlation are always in the BEAM coordinate system.<br/>
 */

public class ADCPBeam extends IMCMessage {

	public static final int ID_STATIC = 1016;

	public ADCPBeam() {
		super(ID_STATIC);
	}

	public ADCPBeam(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ADCPBeam(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ADCPBeam create(Object... values) {
		ADCPBeam m = new ADCPBeam();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ADCPBeam clone(IMCMessage msg) throws Exception {

		ADCPBeam m = new ADCPBeam();
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

	public ADCPBeam(float vel, float amp, short cor) {
		super(ID_STATIC);
		setVel(vel);
		setAmp(amp);
		setCor(cor);
	}

	/**
	 *  @return Water Velocity (m/s) - fp32_t
	 */
	public double getVel() {
		return getDouble("vel");
	}

	/**
	 *  @param vel Water Velocity (m/s)
	 */
	public ADCPBeam setVel(double vel) {
		values.put("vel", vel);
		return this;
	}

	/**
	 *  @return Amplitude (db) - fp32_t
	 */
	public double getAmp() {
		return getDouble("amp");
	}

	/**
	 *  @param amp Amplitude (db)
	 */
	public ADCPBeam setAmp(double amp) {
		values.put("amp", amp);
		return this;
	}

	/**
	 *  @return Correlation (%) - uint8_t
	 */
	public short getCor() {
		return (short) getInteger("cor");
	}

	/**
	 *  @param cor Correlation (%)
	 */
	public ADCPBeam setCor(short cor) {
		values.put("cor", cor);
		return this;
	}

}
