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
 *  IMC Message Euler Angles Delta (255)<br/>
 *  Component of incremetal orientation vector over a period of time.<br/>
 */

public class EulerAnglesDelta extends IMCMessage {

	public static final int ID_STATIC = 255;

	public EulerAnglesDelta() {
		super(ID_STATIC);
	}

	public EulerAnglesDelta(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EulerAnglesDelta(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EulerAnglesDelta create(Object... values) {
		EulerAnglesDelta m = new EulerAnglesDelta();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EulerAnglesDelta clone(IMCMessage msg) throws Exception {

		EulerAnglesDelta m = new EulerAnglesDelta();
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

	public EulerAnglesDelta(double time, double x, double y, double z, float timestep) {
		super(ID_STATIC);
		setTime(time);
		setX(x);
		setY(y);
		setZ(z);
		setTimestep(timestep);
	}

	/**
	 *  @return Device Time (s) - fp64_t
	 */
	public double getTime() {
		return getDouble("time");
	}

	/**
	 *  @param time Device Time (s)
	 */
	public EulerAnglesDelta setTime(double time) {
		values.put("time", time);
		return this;
	}

	/**
	 *  @return X (rad) - fp64_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x X (rad)
	 */
	public EulerAnglesDelta setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y (rad) - fp64_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Y (rad)
	 */
	public EulerAnglesDelta setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z (rad) - fp64_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z (rad)
	 */
	public EulerAnglesDelta setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Timestep (s) - fp32_t
	 */
	public double getTimestep() {
		return getDouble("timestep");
	}

	/**
	 *  @param timestep Timestep (s)
	 */
	public EulerAnglesDelta setTimestep(double timestep) {
		values.put("timestep", timestep);
		return this;
	}

}
