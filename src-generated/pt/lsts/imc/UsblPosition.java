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
 *  IMC Message USBL Position (891)<br/>
 *  This message contains information, collected using USBL, about a<br/>
 *  target's position.<br/>
 */

public class UsblPosition extends IMCMessage {

	public static final int ID_STATIC = 891;

	public UsblPosition() {
		super(ID_STATIC);
	}

	public UsblPosition(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UsblPosition(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static UsblPosition create(Object... values) {
		UsblPosition m = new UsblPosition();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static UsblPosition clone(IMCMessage msg) throws Exception {

		UsblPosition m = new UsblPosition();
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

	public UsblPosition(int target, float x, float y, float z) {
		super(ID_STATIC);
		setTarget(target);
		setX(x);
		setY(y);
		setZ(z);
	}

	/**
	 *  @return Target - uint16_t
	 */
	public int getTarget() {
		return getInteger("target");
	}

	/**
	 *  @param target Target
	 */
	public UsblPosition setTarget(int target) {
		values.put("target", target);
		return this;
	}

	/**
	 *  @return X (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x X (m)
	 */
	public UsblPosition setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y Y (m)
	 */
	public UsblPosition setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z (m)
	 */
	public UsblPosition setZ(double z) {
		values.put("z", z);
		return this;
	}

}
