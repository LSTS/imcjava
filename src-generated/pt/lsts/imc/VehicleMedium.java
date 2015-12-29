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
 *  IMC Message Vehicle Medium (508)<br/>
 *  Detect current vehicle medium.<br/>
 */

public class VehicleMedium extends IMCMessage {

	public enum MEDIUM {
		GROUND(0),
		AIR(1),
		WATER(2),
		UNDERWATER(3),
		UNKNOWN(4);

		protected long value;

		public long value() {
			return value;
		}

		MEDIUM(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 508;

	public VehicleMedium() {
		super(ID_STATIC);
	}

	public VehicleMedium(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VehicleMedium(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VehicleMedium create(Object... values) {
		VehicleMedium m = new VehicleMedium();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VehicleMedium clone(IMCMessage msg) throws Exception {

		VehicleMedium m = new VehicleMedium();
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

	public VehicleMedium(MEDIUM medium) {
		super(ID_STATIC);
		setMedium(medium);
	}

	/**
	 *  @return Medium (enumerated) - uint8_t
	 */
	public MEDIUM getMedium() {
		try {
			MEDIUM o = MEDIUM.valueOf(getMessageType().getFieldPossibleValues("medium").get(getLong("medium")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getMediumStr() {
		return getString("medium");
	}

	public short getMediumVal() {
		return (short) getInteger("medium");
	}

	/**
	 *  @param medium Medium (enumerated)
	 */
	public VehicleMedium setMedium(MEDIUM medium) {
		values.put("medium", medium.value());
		return this;
	}

	/**
	 *  @param medium Medium (as a String)
	 */
	public VehicleMedium setMediumStr(String medium) {
		setValue("medium", medium);
		return this;
	}

	/**
	 *  @param medium Medium (integer value)
	 */
	public VehicleMedium setMediumVal(short medium) {
		setValue("medium", medium);
		return this;
	}

}
