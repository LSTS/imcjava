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
 *  IMC Message Desired Velocity (409)<br/>
 *  Desired value for each linear and angular speeds.<br/>
 */

public class DesiredVelocity extends IMCMessage {

	public static final short FL_SURGE = 0x01;
	public static final short FL_SWAY = 0x02;
	public static final short FL_HEAVE = 0x04;
	public static final short FL_ROLL = 0x08;
	public static final short FL_PITCH = 0x10;
	public static final short FL_YAW = 0x20;

	public static final int ID_STATIC = 409;

	public DesiredVelocity() {
		super(ID_STATIC);
	}

	public DesiredVelocity(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DesiredVelocity(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DesiredVelocity create(Object... values) {
		DesiredVelocity m = new DesiredVelocity();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DesiredVelocity clone(IMCMessage msg) throws Exception {

		DesiredVelocity m = new DesiredVelocity();
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

	public DesiredVelocity(double u, double v, double w, double p, double q, double r, short flags) {
		super(ID_STATIC);
		setU(u);
		setV(v);
		setW(w);
		setP(p);
		setQ(q);
		setR(r);
		setFlags(flags);
	}

	/**
	 *  @return Desired Linear Speed in xx (m/s) - fp64_t
	 */
	public double getU() {
		return getDouble("u");
	}

	/**
	 *  @param u Desired Linear Speed in xx (m/s)
	 */
	public DesiredVelocity setU(double u) {
		values.put("u", u);
		return this;
	}

	/**
	 *  @return Desired Linear Speed in yy (m/s) - fp64_t
	 */
	public double getV() {
		return getDouble("v");
	}

	/**
	 *  @param v Desired Linear Speed in yy (m/s)
	 */
	public DesiredVelocity setV(double v) {
		values.put("v", v);
		return this;
	}

	/**
	 *  @return Desired Linear Speed in zz (m/s) - fp64_t
	 */
	public double getW() {
		return getDouble("w");
	}

	/**
	 *  @param w Desired Linear Speed in zz (m/s)
	 */
	public DesiredVelocity setW(double w) {
		values.put("w", w);
		return this;
	}

	/**
	 *  @return Desired Angular Speed in xx (m/s) - fp64_t
	 */
	public double getP() {
		return getDouble("p");
	}

	/**
	 *  @param p Desired Angular Speed in xx (m/s)
	 */
	public DesiredVelocity setP(double p) {
		values.put("p", p);
		return this;
	}

	/**
	 *  @return Desired Angular Speed in yy (m/s) - fp64_t
	 */
	public double getQ() {
		return getDouble("q");
	}

	/**
	 *  @param q Desired Angular Speed in yy (m/s)
	 */
	public DesiredVelocity setQ(double q) {
		values.put("q", q);
		return this;
	}

	/**
	 *  @return Desired Angular Speed in zz (m/s) - fp64_t
	 */
	public double getR() {
		return getDouble("r");
	}

	/**
	 *  @param r Desired Angular Speed in zz (m/s)
	 */
	public DesiredVelocity setR(double r) {
		values.put("r", r);
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
	public DesiredVelocity setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

}
