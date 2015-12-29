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
 *  IMC Message Desired Linear State (414)<br/>
 *  Position, velocity and acceleration setpoints in NED<br/>
 */

public class DesiredLinearState extends IMCMessage {

	public static final int FL_X = 0x0001;
	public static final int FL_Y = 0x0002;
	public static final int FL_Z = 0x0004;
	public static final int FL_VX = 0x0008;
	public static final int FL_VY = 0x0010;
	public static final int FL_VZ = 0x0020;
	public static final int FL_AX = 0x0040;
	public static final int FL_AY = 0x0080;
	public static final int FL_AZ = 0x0100;

	public static final int ID_STATIC = 414;

	public DesiredLinearState() {
		super(ID_STATIC);
	}

	public DesiredLinearState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DesiredLinearState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DesiredLinearState create(Object... values) {
		DesiredLinearState m = new DesiredLinearState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DesiredLinearState clone(IMCMessage msg) throws Exception {

		DesiredLinearState m = new DesiredLinearState();
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

	public DesiredLinearState(double x, double y, double z, double vx, double vy, double vz, double ax, double ay, double az, int flags) {
		super(ID_STATIC);
		setX(x);
		setY(y);
		setZ(z);
		setVx(vx);
		setVy(vy);
		setVz(vz);
		setAx(ax);
		setAy(ay);
		setAz(az);
		setFlags(flags);
	}

	/**
	 *  @return Desired pos in xx (m) - fp64_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x Desired pos in xx (m)
	 */
	public DesiredLinearState setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Desired pos in yy (m) - fp64_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Desired pos in yy (m)
	 */
	public DesiredLinearState setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Desired pos in zz (m) - fp64_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Desired pos in zz (m)
	 */
	public DesiredLinearState setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Desired Linear Speed in xx (m/s) - fp64_t
	 */
	public double getVx() {
		return getDouble("vx");
	}

	/**
	 *  @param vx Desired Linear Speed in xx (m/s)
	 */
	public DesiredLinearState setVx(double vx) {
		values.put("vx", vx);
		return this;
	}

	/**
	 *  @return Desired Linear Speed in yy (m/s) - fp64_t
	 */
	public double getVy() {
		return getDouble("vy");
	}

	/**
	 *  @param vy Desired Linear Speed in yy (m/s)
	 */
	public DesiredLinearState setVy(double vy) {
		values.put("vy", vy);
		return this;
	}

	/**
	 *  @return Desired Linear Speed in zz (m/s) - fp64_t
	 */
	public double getVz() {
		return getDouble("vz");
	}

	/**
	 *  @param vz Desired Linear Speed in zz (m/s)
	 */
	public DesiredLinearState setVz(double vz) {
		values.put("vz", vz);
		return this;
	}

	/**
	 *  @return Desired Linear Acceleration in xx (m/s/s) - fp64_t
	 */
	public double getAx() {
		return getDouble("ax");
	}

	/**
	 *  @param ax Desired Linear Acceleration in xx (m/s/s)
	 */
	public DesiredLinearState setAx(double ax) {
		values.put("ax", ax);
		return this;
	}

	/**
	 *  @return Desired Linear Acceleration in yy (m/s/s) - fp64_t
	 */
	public double getAy() {
		return getDouble("ay");
	}

	/**
	 *  @param ay Desired Linear Acceleration in yy (m/s/s)
	 */
	public DesiredLinearState setAy(double ay) {
		values.put("ay", ay);
		return this;
	}

	/**
	 *  @return Desired Linear Acceleration in zz (m/s/s) - fp64_t
	 */
	public double getAz() {
		return getDouble("az");
	}

	/**
	 *  @param az Desired Linear Acceleration in zz (m/s/s)
	 */
	public DesiredLinearState setAz(double az) {
		values.put("az", az);
		return this;
	}

	/**
	 *  @return Flags (bitfield) - uint16_t
	 */
	public int getFlags() {
		return getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public DesiredLinearState setFlags(int flags) {
		values.put("flags", flags);
		return this;
	}

}
