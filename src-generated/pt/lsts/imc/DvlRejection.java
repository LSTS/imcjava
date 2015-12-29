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
 *  IMC Message DVL Rejection (358)<br/>
 *  When the vehicle uses Doppler Velocity Log sensor, this message<br/>
 *  notifies that a new measurement was locally rejected by the<br/>
 *  navigation filter.<br/>
 */

public class DvlRejection extends IMCMessage {

	public static final short TYPE_GV = 0x01;
	public static final short TYPE_WV = 0x02;

	public enum REASON {
		INNOV_THRESHOLD_X(0),
		INNOV_THRESHOLD_Y(1),
		ABS_THRESHOLD_X(2),
		ABS_THRESHOLD_Y(3);

		protected long value;

		public long value() {
			return value;
		}

		REASON(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 358;

	public DvlRejection() {
		super(ID_STATIC);
	}

	public DvlRejection(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DvlRejection(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DvlRejection create(Object... values) {
		DvlRejection m = new DvlRejection();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DvlRejection clone(IMCMessage msg) throws Exception {

		DvlRejection m = new DvlRejection();
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

	public DvlRejection(short type, REASON reason, float value, float timestep) {
		super(ID_STATIC);
		setType(type);
		setReason(reason);
		setValue(value);
		setTimestep(timestep);
	}

	/**
	 *  @return Type of velocity (bitfield) - uint8_t
	 */
	public short getType() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Type of velocity (bitfield)
	 */
	public DvlRejection setType(short type) {
		values.put("type", type);
		return this;
	}

	/**
	 *  @return Reason (enumerated) - uint8_t
	 */
	public REASON getReason() {
		try {
			REASON o = REASON.valueOf(getMessageType().getFieldPossibleValues("reason").get(getLong("reason")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getReasonStr() {
		return getString("reason");
	}

	public short getReasonVal() {
		return (short) getInteger("reason");
	}

	/**
	 *  @param reason Reason (enumerated)
	 */
	public DvlRejection setReason(REASON reason) {
		values.put("reason", reason.value());
		return this;
	}

	/**
	 *  @param reason Reason (as a String)
	 */
	public DvlRejection setReasonStr(String reason) {
		setValue("reason", reason);
		return this;
	}

	/**
	 *  @param reason Reason (integer value)
	 */
	public DvlRejection setReasonVal(short reason) {
		setValue("reason", reason);
		return this;
	}

	/**
	 *  @return Value (m/s) - fp32_t
	 */
	public double getValue() {
		return getDouble("value");
	}

	/**
	 *  @param value Value (m/s)
	 */
	public DvlRejection setValue(double value) {
		values.put("value", value);
		return this;
	}

	/**
	 *  @return Timestep (s) - fp32_t
	 */
	public double getTimestep() {
		return getDouble("timestep");
	}

	/**
	 *  @param timestep Timestep (s)
	 */
	public DvlRejection setTimestep(double timestep) {
		values.put("timestep", timestep);
		return this;
	}

}
