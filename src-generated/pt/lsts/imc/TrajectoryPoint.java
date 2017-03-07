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
 *  IMC Message Trajectory Point (464)<br/>
 *  Waypoint coordinate of a Follow Trajectory maneuver.<br/>
 */

public class TrajectoryPoint extends IMCMessage {

	public static final int ID_STATIC = 464;

	public TrajectoryPoint() {
		super(ID_STATIC);
	}

	public TrajectoryPoint(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TrajectoryPoint(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static TrajectoryPoint create(Object... values) {
		TrajectoryPoint m = new TrajectoryPoint();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static TrajectoryPoint clone(IMCMessage msg) throws Exception {

		TrajectoryPoint m = new TrajectoryPoint();
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

	public TrajectoryPoint(float x, float y, float z, float t) {
		super(ID_STATIC);
		setX(x);
		setY(y);
		setZ(z);
		setT(t);
	}

	/**
	 *  @return North Offset (m) (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x North Offset (m) (m)
	 */
	public TrajectoryPoint setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return East Offset (m) (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y East Offset (m) (m)
	 */
	public TrajectoryPoint setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Down Offset (m) (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Down Offset (m) (m)
	 */
	public TrajectoryPoint setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Time Offset (s) (s) - fp32_t
	 */
	public double getT() {
		return getDouble("t");
	}

	/**
	 *  @param t Time Offset (s) (s)
	 */
	public TrajectoryPoint setT(double t) {
		values.put("t", t);
		return this;
	}

}
