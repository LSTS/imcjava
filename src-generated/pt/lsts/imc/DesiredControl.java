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
 *  IMC Message Desired Control (407)<br/>
 *  Set the desired virtual forces and torques to be applied to the<br/>
 *  vehicle.<br/>
 */

public class DesiredControl extends IMCMessage {

	public static final short FL_X = 0x01;
	public static final short FL_Y = 0x02;
	public static final short FL_Z = 0x04;
	public static final short FL_K = 0x08;
	public static final short FL_M = 0x10;
	public static final short FL_N = 0x20;

	public static final int ID_STATIC = 407;

	public DesiredControl() {
		super(ID_STATIC);
	}

	public DesiredControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DesiredControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DesiredControl create(Object... values) {
		DesiredControl m = new DesiredControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DesiredControl clone(IMCMessage msg) throws Exception {

		DesiredControl m = new DesiredControl();
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

	public DesiredControl(double x, double y, double z, double k, double m, double n, short flags) {
		super(ID_STATIC);
		setX(x);
		setY(y);
		setZ(z);
		setK(k);
		setM(m);
		setN(n);
		setFlags(flags);
	}

	/**
	 *  @return Force along the x axis (n) - fp64_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x Force along the x axis (n)
	 */
	public DesiredControl setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Force along the y axis (n) - fp64_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Force along the y axis (n)
	 */
	public DesiredControl setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Force along the z axis (n) - fp64_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Force along the z axis (n)
	 */
	public DesiredControl setZ(double z) {
		values.put("z", z);
		return this;
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
	public DesiredControl setK(double k) {
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
	public DesiredControl setM(double m) {
		values.put("m", m);
		return this;
	}

	/**
	 *  @return Torque about the z axis (nm) - fp64_t
	 */
	public double getN() {
		return getDouble("n");
	}

	/**
	 *  @param n Torque about the z axis (nm)
	 */
	public DesiredControl setN(double n) {
		values.put("n", n);
		return this;
	}

	/**
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public DesiredControl setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

}
