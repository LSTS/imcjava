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
 *  IMC Message Beam Configuration (283)<br/>
 *  Beam configuration of the device.<br/>
 */

public class BeamConfig extends IMCMessage {

	public static final int ID_STATIC = 283;

	public BeamConfig() {
		super(ID_STATIC);
	}

	public BeamConfig(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BeamConfig(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static BeamConfig create(Object... values) {
		BeamConfig m = new BeamConfig();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static BeamConfig clone(IMCMessage msg) throws Exception {

		BeamConfig m = new BeamConfig();
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

	public BeamConfig(float beam_width, float beam_height) {
		super(ID_STATIC);
		setBeamWidth(beam_width);
		setBeamHeight(beam_height);
	}

	/**
	 *  @return Beam Width (rad) - fp32_t
	 */
	public double getBeamWidth() {
		return getDouble("beam_width");
	}

	/**
	 *  @param beam_width Beam Width (rad)
	 */
	public BeamConfig setBeamWidth(double beam_width) {
		values.put("beam_width", beam_width);
		return this;
	}

	/**
	 *  @return Beam Height (rad) - fp32_t
	 */
	public double getBeamHeight() {
		return getDouble("beam_height");
	}

	/**
	 *  @param beam_height Beam Height (rad)
	 */
	public BeamConfig setBeamHeight(double beam_height) {
		values.put("beam_height", beam_height);
		return this;
	}

}
