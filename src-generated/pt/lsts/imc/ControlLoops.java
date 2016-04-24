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
 *  IMC Message Control Loops (507)<br/>
 *  Enable or disable control loops.<br/>
 */

public class ControlLoops extends IMCMessage {

	public static final long CL_NONE = 0x00000000;
	public static final long CL_PATH = 0x00000001;
	public static final long CL_TELEOPERATION = 0x00000002;
	public static final long CL_ALTITUDE = 0x00000004;
	public static final long CL_DEPTH = 0x00000008;
	public static final long CL_ROLL = 0x00000010;
	public static final long CL_PITCH = 0x00000020;
	public static final long CL_YAW = 0x00000040;
	public static final long CL_SPEED = 0x00000080;
	public static final long CL_YAW_RATE = 0x00000100;
	public static final long CL_VERTICAL_RATE = 0x00000200;
	public static final long CL_TORQUE = 0x00000400;
	public static final long CL_FORCE = 0x00000800;
	public static final long CL_VELOCITY = 0x00001000;
	public static final long CL_THROTTLE = 0x00002000;
	public static final long CL_EXTERNAL = 0x40000000;
	public static final long CL_NO_OVERRIDE = 0x80000000;
	public static final long CL_ALL = 0xFFFFFFFF;

	public enum ENABLE {
		DISABLE(0),
		ENABLE(1);

		protected long value;

		public long value() {
			return value;
		}

		ENABLE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 507;

	public ControlLoops() {
		super(ID_STATIC);
	}

	public ControlLoops(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ControlLoops(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ControlLoops create(Object... values) {
		ControlLoops m = new ControlLoops();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ControlLoops clone(IMCMessage msg) throws Exception {

		ControlLoops m = new ControlLoops();
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

	public ControlLoops(ENABLE enable, long mask, long scope_ref) {
		super(ID_STATIC);
		setEnable(enable);
		setMask(mask);
		setScopeRef(scope_ref);
	}

	/**
	 *  @return Enable (enumerated) - uint8_t
	 */
	public ENABLE getEnable() {
		try {
			ENABLE o = ENABLE.valueOf(getMessageType().getFieldPossibleValues("enable").get(getLong("enable")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getEnableStr() {
		return getString("enable");
	}

	public short getEnableVal() {
		return (short) getInteger("enable");
	}

	/**
	 *  @param enable Enable (enumerated)
	 */
	public ControlLoops setEnable(ENABLE enable) {
		values.put("enable", enable.value());
		return this;
	}

	/**
	 *  @param enable Enable (as a String)
	 */
	public ControlLoops setEnableStr(String enable) {
		setValue("enable", enable);
		return this;
	}

	/**
	 *  @param enable Enable (integer value)
	 */
	public ControlLoops setEnableVal(short enable) {
		setValue("enable", enable);
		return this;
	}

	/**
	 *  @return Control Loop Mask (bitfield) - uint32_t
	 */
	public long getMask() {
		return getLong("mask");
	}

	/**
	 *  @param mask Control Loop Mask (bitfield)
	 */
	public ControlLoops setMask(long mask) {
		values.put("mask", mask);
		return this;
	}

	/**
	 *  @return Scope Time Reference - uint32_t
	 */
	public long getScopeRef() {
		return getLong("scope_ref");
	}

	/**
	 *  @param scope_ref Scope Time Reference
	 */
	public ControlLoops setScopeRef(long scope_ref) {
		values.put("scope_ref", scope_ref);
		return this;
	}

}
