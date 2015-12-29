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
 *  IMC Message Dynamics Simulation Parameters (53)<br/>
 *  Vehicle dynamics parameters for 3DOF, 4DOF or 5DOF simulations.<br/>
 */

public class DynamicsSimParam extends IMCMessage {

	public enum OP {
		REQUEST(0),
		SET(1),
		REPORT(2);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 53;

	public DynamicsSimParam() {
		super(ID_STATIC);
	}

	public DynamicsSimParam(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DynamicsSimParam(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DynamicsSimParam create(Object... values) {
		DynamicsSimParam m = new DynamicsSimParam();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DynamicsSimParam clone(IMCMessage msg) throws Exception {

		DynamicsSimParam m = new DynamicsSimParam();
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

	public DynamicsSimParam(OP op, float tas2acc_pgain, float bank2p_pgain) {
		super(ID_STATIC);
		setOp(op);
		setTas2accPgain(tas2acc_pgain);
		setBank2pPgain(bank2p_pgain);
	}

	/**
	 *  @return Action on the Vehicle Simulation Parameters (enumerated) - uint8_t
	 */
	public OP getOp() {
		try {
			OP o = OP.valueOf(getMessageType().getFieldPossibleValues("op").get(getLong("op")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getOpStr() {
		return getString("op");
	}

	public short getOpVal() {
		return (short) getInteger("op");
	}

	/**
	 *  @param op Action on the Vehicle Simulation Parameters (enumerated)
	 */
	public DynamicsSimParam setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Action on the Vehicle Simulation Parameters (as a String)
	 */
	public DynamicsSimParam setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Action on the Vehicle Simulation Parameters (integer value)
	 */
	public DynamicsSimParam setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return TAS to Longitudinal Acceleration Gain - fp32_t
	 */
	public double getTas2accPgain() {
		return getDouble("tas2acc_pgain");
	}

	/**
	 *  @param tas2acc_pgain TAS to Longitudinal Acceleration Gain
	 */
	public DynamicsSimParam setTas2accPgain(double tas2acc_pgain) {
		values.put("tas2acc_pgain", tas2acc_pgain);
		return this;
	}

	/**
	 *  @return Bank to Bank Rate Gain - fp32_t
	 */
	public double getBank2pPgain() {
		return getDouble("bank2p_pgain");
	}

	/**
	 *  @param bank2p_pgain Bank to Bank Rate Gain
	 */
	public DynamicsSimParam setBank2pPgain(double bank2p_pgain) {
		values.put("bank2p_pgain", bank2p_pgain);
		return this;
	}

}
