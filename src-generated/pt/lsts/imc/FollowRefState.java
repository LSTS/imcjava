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
 *  IMC Message Follow Reference State (480)<br/>
 */

public class FollowRefState extends IMCMessage {

	public static final short PROX_FAR = 0x01;
	public static final short PROX_XY_NEAR = 0x02;
	public static final short PROX_Z_NEAR = 0x04;

	public enum STATE {
		WAIT(1),
		GOTO(2),
		LOITER(3),
		HOVER(4),
		ELEVATOR(5),
		TIMEOUT(6);

		protected long value;

		public long value() {
			return value;
		}

		STATE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 480;

	public FollowRefState() {
		super(ID_STATIC);
	}

	public FollowRefState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FollowRefState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FollowRefState create(Object... values) {
		FollowRefState m = new FollowRefState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FollowRefState clone(IMCMessage msg) throws Exception {

		FollowRefState m = new FollowRefState();
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

	public FollowRefState(int control_src, short control_ent, Reference reference, STATE state, short proximity) {
		super(ID_STATIC);
		setControlSrc(control_src);
		setControlEnt(control_ent);
		if (reference != null)
			setReference(reference);
		setState(state);
		setProximity(proximity);
	}

	/**
	 *  @return Controlling Source - uint16_t
	 */
	public int getControlSrc() {
		return getInteger("control_src");
	}

	/**
	 *  @param control_src Controlling Source
	 */
	public FollowRefState setControlSrc(int control_src) {
		values.put("control_src", control_src);
		return this;
	}

	/**
	 *  @return Controlling Entity - uint8_t
	 */
	public short getControlEnt() {
		return (short) getInteger("control_ent");
	}

	/**
	 *  @param control_ent Controlling Entity
	 */
	public FollowRefState setControlEnt(short control_ent) {
		values.put("control_ent", control_ent);
		return this;
	}

	/**
	 *  @return Reference - message
	 */
	public Reference getReference() {
		try {
			IMCMessage obj = getMessage("reference");
			if (obj instanceof Reference)
				return (Reference) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param reference Reference
	 */
	public FollowRefState setReference(Reference reference) {
		values.put("reference", reference);
		return this;
	}

	/**
	 *  @return State (enumerated) - uint8_t
	 */
	public STATE getState() {
		try {
			STATE o = STATE.valueOf(getMessageType().getFieldPossibleValues("state").get(getLong("state")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getStateStr() {
		return getString("state");
	}

	public short getStateVal() {
		return (short) getInteger("state");
	}

	/**
	 *  @param state State (enumerated)
	 */
	public FollowRefState setState(STATE state) {
		values.put("state", state.value());
		return this;
	}

	/**
	 *  @param state State (as a String)
	 */
	public FollowRefState setStateStr(String state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @param state State (integer value)
	 */
	public FollowRefState setStateVal(short state) {
		setValue("state", state);
		return this;
	}

	/**
	 *  @return Proximity (bitfield) - uint8_t
	 */
	public short getProximity() {
		return (short) getInteger("proximity");
	}

	/**
	 *  @param proximity Proximity (bitfield)
	 */
	public FollowRefState setProximity(short proximity) {
		values.put("proximity", proximity);
		return this;
	}

}
