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
 *  IMC Message GPS Fix Rejection (356)<br/>
 */

public class GpsFixRejection extends IMCMessage {

	public enum REASON {
		ABOVE_THRESHOLD(0),
		INVALID(1),
		ABOVE_MAX_HDOP(2),
		ABOVE_MAX_HACC(3),
		LOST_VAL_BIT(4);

		protected long value;

		public long value() {
			return value;
		}

		REASON(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 356;

	public GpsFixRejection() {
		super(ID_STATIC);
	}

	public GpsFixRejection(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GpsFixRejection(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GpsFixRejection create(Object... values) {
		GpsFixRejection m = new GpsFixRejection();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GpsFixRejection clone(IMCMessage msg) throws Exception {

		GpsFixRejection m = new GpsFixRejection();
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

	public GpsFixRejection(float utc_time, REASON reason) {
		super(ID_STATIC);
		setUtcTime(utc_time);
		setReason(reason);
	}

	/**
	 *  @return UTC Time of Fix (s) - fp32_t
	 */
	public double getUtcTime() {
		return getDouble("utc_time");
	}

	/**
	 *  @param utc_time UTC Time of Fix (s)
	 */
	public GpsFixRejection setUtcTime(double utc_time) {
		values.put("utc_time", utc_time);
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
	public GpsFixRejection setReason(REASON reason) {
		values.put("reason", reason.value());
		return this;
	}

	/**
	 *  @param reason Reason (as a String)
	 */
	public GpsFixRejection setReasonStr(String reason) {
		setValue("reason", reason);
		return this;
	}

	/**
	 *  @param reason Reason (integer value)
	 */
	public GpsFixRejection setReasonVal(short reason) {
		setValue("reason", reason);
		return this;
	}

}
