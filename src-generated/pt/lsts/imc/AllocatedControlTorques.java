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
 *  IMC Message Allocated Control Torques (411)<br/>
 *  Control torques allocated to the actuators.<br/>
 */

public class AllocatedControlTorques extends IMCMessage {

	public static final int ID_STATIC = 411;

	public AllocatedControlTorques() {
		super(ID_STATIC);
	}

	public AllocatedControlTorques(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AllocatedControlTorques(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static AllocatedControlTorques create(Object... values) {
		AllocatedControlTorques m = new AllocatedControlTorques();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static AllocatedControlTorques clone(IMCMessage msg) throws Exception {

		AllocatedControlTorques m = new AllocatedControlTorques();
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

	public AllocatedControlTorques(double k, double m, double n) {
		super(ID_STATIC);
		setK(k);
		setM(m);
		setN(n);
	}

	/**
	 *  @return Torque about the x axis (nm) - fp64_t
	 */
	public double getK() {
		return getDouble("k");
	}

	/**
	 *  @param k Torque about the x axis (nm)
	 */
	public AllocatedControlTorques setK(double k) {
		values.put("k", k);
		return this;
	}

	/**
	 *  @return Torque about the y axis (nm) - fp64_t
	 */
	public double getM() {
		return getDouble("m");
	}

	/**
	 *  @param m Torque about the y axis (nm)
	 */
	public AllocatedControlTorques setM(double m) {
		values.put("m", m);
		return this;
	}

	/**
	 *  @return Torque about the x axis (nm) - fp64_t
	 */
	public double getN() {
		return getDouble("n");
	}

	/**
	 *  @param n Torque about the x axis (nm)
	 */
	public AllocatedControlTorques setN(double n) {
		values.put("n", n);
		return this;
	}

}
