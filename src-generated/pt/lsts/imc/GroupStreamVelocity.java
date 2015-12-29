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
 *  IMC Message GroupStreamVelocity (362)<br/>
 *  The stream velocity estimated by a group of systems.<br/>
 *  Typically for water or air streams.<br/>
 */

public class GroupStreamVelocity extends IMCMessage {

	public static final int ID_STATIC = 362;

	public GroupStreamVelocity() {
		super(ID_STATIC);
	}

	public GroupStreamVelocity(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GroupStreamVelocity(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GroupStreamVelocity create(Object... values) {
		GroupStreamVelocity m = new GroupStreamVelocity();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GroupStreamVelocity clone(IMCMessage msg) throws Exception {

		GroupStreamVelocity m = new GroupStreamVelocity();
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

	public GroupStreamVelocity(double x, double y, double z) {
		super(ID_STATIC);
		setX(x);
		setY(y);
		setZ(z);
	}

	/**
	 *  @return X component (North) (m/s) - fp64_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x X component (North) (m/s)
	 */
	public GroupStreamVelocity setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y component (East) (m/s) - fp64_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Y component (East) (m/s)
	 */
	public GroupStreamVelocity setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z component (Down) (m/s) - fp64_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z component (Down) (m/s)
	 */
	public GroupStreamVelocity setZ(double z) {
		values.put("z", z);
		return this;
	}

}
