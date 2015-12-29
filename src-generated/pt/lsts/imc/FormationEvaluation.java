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
 *  IMC Message Formation Evaluation Data (823)<br/>
 *  Formation control performance evaluation variables.<br/>
 */

public class FormationEvaluation extends IMCMessage {

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
		STOP(1),
		READY(2),
		EXECUTING(3),
		FAILURE(4);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 823;

	public FormationEvaluation() {
		super(ID_STATIC);
	}

	public FormationEvaluation(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormationEvaluation(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormationEvaluation create(Object... values) {
		FormationEvaluation m = new FormationEvaluation();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormationEvaluation clone(IMCMessage msg) throws Exception {

		FormationEvaluation m = new FormationEvaluation();
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

	public FormationEvaluation(TYPE type, OP op, float err_mean, float dist_min_abs, float dist_min_mean, float roll_rate_mean, float time, FormationControlParams ControlParams) {
		super(ID_STATIC);
		setType(type);
		setOp(op);
		setErrMean(err_mean);
		setDistMinAbs(dist_min_abs);
		setDistMinMean(dist_min_mean);
		setRollRateMean(roll_rate_mean);
		setTime(time);
		if (ControlParams != null)
			setControlParams(ControlParams);
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
	public FormationEvaluation setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public FormationEvaluation setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public FormationEvaluation setTypeVal(short type) {
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
	public FormationEvaluation setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public FormationEvaluation setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public FormationEvaluation setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Mean Position Error - fp32_t
	 */
	public double getErrMean() {
		return getDouble("err_mean");
	}

	/**
	 *  @param err_mean Mean Position Error
	 */
	public FormationEvaluation setErrMean(double err_mean) {
		values.put("err_mean", err_mean);
		return this;
	}

	/**
	 *  @return Absolute Minimum Distance - fp32_t
	 */
	public double getDistMinAbs() {
		return getDouble("dist_min_abs");
	}

	/**
	 *  @param dist_min_abs Absolute Minimum Distance
	 */
	public FormationEvaluation setDistMinAbs(double dist_min_abs) {
		values.put("dist_min_abs", dist_min_abs);
		return this;
	}

	/**
	 *  @return Mean Minimum Distance - fp32_t
	 */
	public double getDistMinMean() {
		return getDouble("dist_min_mean");
	}

	/**
	 *  @param dist_min_mean Mean Minimum Distance
	 */
	public FormationEvaluation setDistMinMean(double dist_min_mean) {
		values.put("dist_min_mean", dist_min_mean);
		return this;
	}

	/**
	 *  @return Mean Roll Rate - fp32_t
	 */
	public double getRollRateMean() {
		return getDouble("roll_rate_mean");
	}

	/**
	 *  @param roll_rate_mean Mean Roll Rate
	 */
	public FormationEvaluation setRollRateMean(double roll_rate_mean) {
		values.put("roll_rate_mean", roll_rate_mean);
		return this;
	}

	/**
	 *  @return Evaluation Time - fp32_t
	 */
	public double getTime() {
		return getDouble("time");
	}

	/**
	 *  @param time Evaluation Time
	 */
	public FormationEvaluation setTime(double time) {
		values.put("time", time);
		return this;
	}

	/**
	 *  @return Formation Control Parameters - message
	 */
	public FormationControlParams getControlParams() {
		try {
			IMCMessage obj = getMessage("ControlParams");
			if (obj instanceof FormationControlParams)
				return (FormationControlParams) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param ControlParams Formation Control Parameters
	 */
	public FormationEvaluation setControlParams(FormationControlParams ControlParams) {
		values.put("ControlParams", ControlParams);
		return this;
	}

}
