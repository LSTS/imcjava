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
 *  IMC Message LBL Beacon Position Estimate (360)<br/>
 *  LBL Beacon position estimate.<br/>
 */

public class LblEstimate extends IMCMessage {

	public static final int ID_STATIC = 360;

	public LblEstimate() {
		super(ID_STATIC);
	}

	public LblEstimate(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LblEstimate(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LblEstimate create(Object... values) {
		LblEstimate m = new LblEstimate();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LblEstimate clone(IMCMessage msg) throws Exception {

		LblEstimate m = new LblEstimate();
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

	public LblEstimate(LblBeacon beacon, float x, float y, float var_x, float var_y, float distance) {
		super(ID_STATIC);
		if (beacon != null)
			setBeacon(beacon);
		setX(x);
		setY(y);
		setVarX(var_x);
		setVarY(var_y);
		setDistance(distance);
	}

	/**
	 *  @return LBL Beacon Configuration - message
	 */
	public LblBeacon getBeacon() {
		try {
			IMCMessage obj = getMessage("beacon");
			if (obj instanceof LblBeacon)
				return (LblBeacon) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param beacon LBL Beacon Configuration
	 */
	public LblEstimate setBeacon(LblBeacon beacon) {
		values.put("beacon", beacon);
		return this;
	}

	/**
	 *  @return North position (m) - fp32_t
	 */
	public double getX() {
		return getDouble("x");
	}

	/**
	 *  @param x North position (m)
	 */
	public LblEstimate setX(double x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return East position (m) - fp32_t
	 */
	public double getY() {
		return getDouble("y");
	}

	/**
	 *  @param y East position (m)
	 */
	public LblEstimate setY(double y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return North position variance (m) - fp32_t
	 */
	public double getVarX() {
		return getDouble("var_x");
	}

	/**
	 *  @param var_x North position variance (m)
	 */
	public LblEstimate setVarX(double var_x) {
		values.put("var_x", var_x);
		return this;
	}

	/**
	 *  @return East position variance (m) - fp32_t
	 */
	public double getVarY() {
		return getDouble("var_y");
	}

	/**
	 *  @param var_y East position variance (m)
	 */
	public LblEstimate setVarY(double var_y) {
		values.put("var_y", var_y);
		return this;
	}

	/**
	 *  @return Distance (m) - fp32_t
	 */
	public double getDistance() {
		return getDouble("distance");
	}

	/**
	 *  @param distance Distance (m)
	 */
	public LblEstimate setDistance(double distance) {
		values.put("distance", distance);
		return this;
	}

}
