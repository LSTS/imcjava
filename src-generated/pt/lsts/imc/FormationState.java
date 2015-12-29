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
 *  IMC Message Formation Tracking State (512)<br/>
 *  Monitoring variables to assert the formation tracking state, i.e., the mismatch between the real and the simulated aircraft position, the convergence state, etc.<br/>
 */

public class FormationState extends IMCMessage {

	public enum TYPE {
		REQUEST(0),
		REPORT(1);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum OP {
		START(0),
		STOP(1);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public enum POSSIMMON {
		OK(0),
		WRN(1),
		LIM(2);

		protected long value;

		public long value() {
			return value;
		}

		POSSIMMON(long value) {
			this.value = value;
		}
	}

	public enum COMMMON {
		OK(0),
		TIMEOUT(1);

		protected long value;

		public long value() {
			return value;
		}

		COMMMON(long value) {
			this.value = value;
		}
	}

	public enum CONVERGMON {
		OK(0),
		TIMEOUT(1);

		protected long value;

		public long value() {
			return value;
		}

		CONVERGMON(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 512;

	public FormationState() {
		super(ID_STATIC);
	}

	public FormationState(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormationState(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormationState create(Object... values) {
		FormationState m = new FormationState();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormationState clone(IMCMessage msg) throws Exception {

		FormationState m = new FormationState();
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

	public FormationState(TYPE type, OP op, float PosSimErr, float Converg, float Turbulence, POSSIMMON PosSimMon, COMMMON CommMon, CONVERGMON ConvergMon) {
		super(ID_STATIC);
		setType(type);
		setOp(op);
		setPosSimErr(PosSimErr);
		setConverg(Converg);
		setTurbulence(Turbulence);
		setPosSimMon(PosSimMon);
		setCommMon(CommMon);
		setConvergMon(ConvergMon);
	}

	/**
	 *  @return Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Type (enumerated)
	 */
	public FormationState setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public FormationState setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public FormationState setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Operation (enumerated) - uint8_t
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
	 *  @param op Operation (enumerated)
	 */
	public FormationState setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public FormationState setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public FormationState setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Position Mismatch (m) - fp32_t
	 */
	public double getPosSimErr() {
		return getDouble("PosSimErr");
	}

	/**
	 *  @param PosSimErr Position Mismatch (m)
	 */
	public FormationState setPosSimErr(double PosSimErr) {
		values.put("PosSimErr", PosSimErr);
		return this;
	}

	/**
	 *  @return Convergence (m) - fp32_t
	 */
	public double getConverg() {
		return getDouble("Converg");
	}

	/**
	 *  @param Converg Convergence (m)
	 */
	public FormationState setConverg(double Converg) {
		values.put("Converg", Converg);
		return this;
	}

	/**
	 *  @return Stream Turbulence (m/s/s) - fp32_t
	 */
	public double getTurbulence() {
		return getDouble("Turbulence");
	}

	/**
	 *  @param Turbulence Stream Turbulence (m/s/s)
	 */
	public FormationState setTurbulence(double Turbulence) {
		values.put("Turbulence", Turbulence);
		return this;
	}

	/**
	 *  @return Position Mismatch Monitor (enumerated) - uint8_t
	 */
	public POSSIMMON getPosSimMon() {
		try {
			POSSIMMON o = POSSIMMON.valueOf(getMessageType().getFieldPossibleValues("PosSimMon").get(getLong("PosSimMon")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getPosSimMonStr() {
		return getString("PosSimMon");
	}

	public short getPosSimMonVal() {
		return (short) getInteger("PosSimMon");
	}

	/**
	 *  @param PosSimMon Position Mismatch Monitor (enumerated)
	 */
	public FormationState setPosSimMon(POSSIMMON PosSimMon) {
		values.put("PosSimMon", PosSimMon.value());
		return this;
	}

	/**
	 *  @param PosSimMon Position Mismatch Monitor (as a String)
	 */
	public FormationState setPosSimMonStr(String PosSimMon) {
		setValue("PosSimMon", PosSimMon);
		return this;
	}

	/**
	 *  @param PosSimMon Position Mismatch Monitor (integer value)
	 */
	public FormationState setPosSimMonVal(short PosSimMon) {
		setValue("PosSimMon", PosSimMon);
		return this;
	}

	/**
	 *  @return Communications Monitor (enumerated) - uint8_t
	 */
	public COMMMON getCommMon() {
		try {
			COMMMON o = COMMMON.valueOf(getMessageType().getFieldPossibleValues("CommMon").get(getLong("CommMon")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCommMonStr() {
		return getString("CommMon");
	}

	public short getCommMonVal() {
		return (short) getInteger("CommMon");
	}

	/**
	 *  @param CommMon Communications Monitor (enumerated)
	 */
	public FormationState setCommMon(COMMMON CommMon) {
		values.put("CommMon", CommMon.value());
		return this;
	}

	/**
	 *  @param CommMon Communications Monitor (as a String)
	 */
	public FormationState setCommMonStr(String CommMon) {
		setValue("CommMon", CommMon);
		return this;
	}

	/**
	 *  @param CommMon Communications Monitor (integer value)
	 */
	public FormationState setCommMonVal(short CommMon) {
		setValue("CommMon", CommMon);
		return this;
	}

	/**
	 *  @return Convergence (enumerated) - uint8_t
	 */
	public CONVERGMON getConvergMon() {
		try {
			CONVERGMON o = CONVERGMON.valueOf(getMessageType().getFieldPossibleValues("ConvergMon").get(getLong("ConvergMon")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getConvergMonStr() {
		return getString("ConvergMon");
	}

	public short getConvergMonVal() {
		return (short) getInteger("ConvergMon");
	}

	/**
	 *  @param ConvergMon Convergence (enumerated)
	 */
	public FormationState setConvergMon(CONVERGMON ConvergMon) {
		values.put("ConvergMon", ConvergMon.value());
		return this;
	}

	/**
	 *  @param ConvergMon Convergence (as a String)
	 */
	public FormationState setConvergMonStr(String ConvergMon) {
		setValue("ConvergMon", ConvergMon);
		return this;
	}

	/**
	 *  @param ConvergMon Convergence (integer value)
	 */
	public FormationState setConvergMonVal(short ConvergMon) {
		setValue("ConvergMon", ConvergMon);
		return this;
	}

}
