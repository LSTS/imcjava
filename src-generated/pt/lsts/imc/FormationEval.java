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
 *  IMC Message Formation Evaluation Data (821)<br/>
 *  Formation control performance evaluation variables.<br/>
 */

public class FormationEval extends IMCMessage {

	public static final int ID_STATIC = 821;

	public FormationEval() {
		super(ID_STATIC);
	}

	public FormationEval(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormationEval(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormationEval create(Object... values) {
		FormationEval m = new FormationEval();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormationEval clone(IMCMessage msg) throws Exception {

		FormationEval m = new FormationEval();
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

	public FormationEval(float err_mean, float dist_min_abs, float dist_min_mean) {
		super(ID_STATIC);
		setErrMean(err_mean);
		setDistMinAbs(dist_min_abs);
		setDistMinMean(dist_min_mean);
	}

	/**
	 *  @return Mean position error - fp32_t
	 */
	public double getErrMean() {
		return getDouble("err_mean");
	}

	/**
	 *  @param err_mean Mean position error
	 */
	public FormationEval setErrMean(double err_mean) {
		values.put("err_mean", err_mean);
		return this;
	}

	/**
	 *  @return Absolute minimum distance - fp32_t
	 */
	public double getDistMinAbs() {
		return getDouble("dist_min_abs");
	}

	/**
	 *  @param dist_min_abs Absolute minimum distance
	 */
	public FormationEval setDistMinAbs(double dist_min_abs) {
		values.put("dist_min_abs", dist_min_abs);
		return this;
	}

	/**
	 *  @return Mean minimum distance - fp32_t
	 */
	public double getDistMinMean() {
		return getDouble("dist_min_mean");
	}

	/**
	 *  @param dist_min_mean Mean minimum distance
	 */
	public FormationEval setDistMinMean(double dist_min_mean) {
		values.put("dist_min_mean", dist_min_mean);
		return this;
	}

}
