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
 *  IMC Message Dislodge Maneuver (483)<br/>
 *  A "Dislodge" is a maneuver ordering the vehicle to attempt a<br/>
 *  series of thruster operations that will hopefully get it<br/>
 *  unstuck from an entangled condition.<br/>
 *  Parameters are RPMs for the motor when attempting dislodge and<br/>
 *  and a flag specifying whether the thrust burst should be attempted<br/>
 *  forward, backward or auto (letting the vehicle decide).<br/>
 */

@SuppressWarnings("unchecked")
public class Dislodge extends Maneuver {

	public enum DIRECTION {
		AUTO(0),
		FORWARD(1),
		BACKWARD(2);

		protected long value;

		public long value() {
			return value;
		}

		DIRECTION(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 483;

	public Dislodge() {
		super(ID_STATIC);
	}

	public Dislodge(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Dislodge(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Dislodge create(Object... values) {
		Dislodge m = new Dislodge();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Dislodge clone(IMCMessage msg) throws Exception {

		Dislodge m = new Dislodge();
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

	public Dislodge(int timeout, float rpm, DIRECTION direction, String custom) {
		super(ID_STATIC);
		setTimeout(timeout);
		setRpm(rpm);
		setDirection(direction);
		if (custom != null)
			setCustom(custom);
	}

	/**
	 *  @return Timeout (s) - uint16_t
	 */
	public int getTimeout() {
		return getInteger("timeout");
	}

	/**
	 *  @param timeout Timeout (s)
	 */
	public Dislodge setTimeout(int timeout) {
		values.put("timeout", timeout);
		return this;
	}

	/**
	 *  @return RPM - fp32_t
	 */
	public double getRpm() {
		return getDouble("rpm");
	}

	/**
	 *  @param rpm RPM
	 */
	public Dislodge setRpm(double rpm) {
		values.put("rpm", rpm);
		return this;
	}

	/**
	 *  @return Direction (enumerated) - uint8_t
	 */
	public DIRECTION getDirection() {
		try {
			DIRECTION o = DIRECTION.valueOf(getMessageType().getFieldPossibleValues("direction").get(getLong("direction")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getDirectionStr() {
		return getString("direction");
	}

	public short getDirectionVal() {
		return (short) getInteger("direction");
	}

	/**
	 *  @param direction Direction (enumerated)
	 */
	public Dislodge setDirection(DIRECTION direction) {
		values.put("direction", direction.value());
		return this;
	}

	/**
	 *  @param direction Direction (as a String)
	 */
	public Dislodge setDirectionStr(String direction) {
		setValue("direction", direction);
		return this;
	}

	/**
	 *  @param direction Direction (integer value)
	 */
	public Dislodge setDirectionVal(short direction) {
		setValue("direction", direction);
		return this;
	}

	/**
	 *  @return Custom settings for maneuver (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getCustom() {
		return getTupleList("custom");
	}

	/**
	 *  @param custom Custom settings for maneuver (tuplelist)
	 */
	public Dislodge setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public Dislodge setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
