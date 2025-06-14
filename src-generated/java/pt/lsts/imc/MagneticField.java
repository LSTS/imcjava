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
 *  IMC Message Magnetic Field (258)<br/>
 *  Vector quantifying the direction and magnitude of the measured<br/>
 *  magnetic field that a device is exposed to.<br/>
 */

public class MagneticField extends IMCMessage {

	public static final int ID_STATIC = 258;

	public MagneticField() {
		super(ID_STATIC);
	}

	public MagneticField(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MagneticField(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static MagneticField create(Object... values) {
		MagneticField m = new MagneticField();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static MagneticField clone(IMCMessage msg) throws Exception {

		MagneticField m = new MagneticField();
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

	public MagneticField(double time, double x, double y, double z) {
		super(ID_STATIC);
		setTime(time);
		setX(x);
		setY(y);
		setZ(z);
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
	public MagneticField setTime(double time) {
		values.put("time", time);
		return this;
	}

	/**
	 *  @return X (G) - fp64_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x X (G)
	 */
	public MagneticField setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y (G) - fp64_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Y (G)
	 */
	public MagneticField setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z (G) - fp64_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z (G)
	 */
	public MagneticField setZ(double z) {
		values.put("z", z);
		return this;
	}

}
