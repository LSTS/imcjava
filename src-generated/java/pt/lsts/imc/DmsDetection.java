/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message DMS Detection (908)<br/>
 *  Presence of DMS (Dimethyl Sulphide).<br/>
 *  If the value of the channel is greater than zero, it means DMS was detected.<br/>
 */

public class DmsDetection extends IMCMessage {

	public static final int ID_STATIC = 908;

	public DmsDetection() {
		super(ID_STATIC);
	}

	public DmsDetection(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DmsDetection(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DmsDetection create(Object... values) {
		DmsDetection m = new DmsDetection();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DmsDetection clone(IMCMessage msg) throws Exception {

		DmsDetection m = new DmsDetection();
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

	public DmsDetection(float ch01, float ch02, float ch03, float ch04, float ch05, float ch06, float ch07, float ch08, float ch09, float ch10, float ch11, float ch12, float ch13, float ch14, float ch15, float ch16) {
		super(ID_STATIC);
		setCh01(ch01);
		setCh02(ch02);
		setCh03(ch03);
		setCh04(ch04);
		setCh05(ch05);
		setCh06(ch06);
		setCh07(ch07);
		setCh08(ch08);
		setCh09(ch09);
		setCh10(ch10);
		setCh11(ch11);
		setCh12(ch12);
		setCh13(ch13);
		setCh14(ch14);
		setCh15(ch15);
		setCh16(ch16);
	}

	/**
	 *  @return Channel 1 - fp32_t
	 */
	public double getCh01() {
		return getDouble("ch01");
	}

	/**
	 *  @param ch01 Channel 1
	 */
	public DmsDetection setCh01(double ch01) {
		values.put("ch01", ch01);
		return this;
	}

	/**
	 *  @return Channel 2 - fp32_t
	 */
	public double getCh02() {
		return getDouble("ch02");
	}

	/**
	 *  @param ch02 Channel 2
	 */
	public DmsDetection setCh02(double ch02) {
		values.put("ch02", ch02);
		return this;
	}

	/**
	 *  @return Channel 3 - fp32_t
	 */
	public double getCh03() {
		return getDouble("ch03");
	}

	/**
	 *  @param ch03 Channel 3
	 */
	public DmsDetection setCh03(double ch03) {
		values.put("ch03", ch03);
		return this;
	}

	/**
	 *  @return Channel 4 - fp32_t
	 */
	public double getCh04() {
		return getDouble("ch04");
	}

	/**
	 *  @param ch04 Channel 4
	 */
	public DmsDetection setCh04(double ch04) {
		values.put("ch04", ch04);
		return this;
	}

	/**
	 *  @return Channel 5 - fp32_t
	 */
	public double getCh05() {
		return getDouble("ch05");
	}

	/**
	 *  @param ch05 Channel 5
	 */
	public DmsDetection setCh05(double ch05) {
		values.put("ch05", ch05);
		return this;
	}

	/**
	 *  @return Channel 6 - fp32_t
	 */
	public double getCh06() {
		return getDouble("ch06");
	}

	/**
	 *  @param ch06 Channel 6
	 */
	public DmsDetection setCh06(double ch06) {
		values.put("ch06", ch06);
		return this;
	}

	/**
	 *  @return Channel 7 - fp32_t
	 */
	public double getCh07() {
		return getDouble("ch07");
	}

	/**
	 *  @param ch07 Channel 7
	 */
	public DmsDetection setCh07(double ch07) {
		values.put("ch07", ch07);
		return this;
	}

	/**
	 *  @return Channel 8 - fp32_t
	 */
	public double getCh08() {
		return getDouble("ch08");
	}

	/**
	 *  @param ch08 Channel 8
	 */
	public DmsDetection setCh08(double ch08) {
		values.put("ch08", ch08);
		return this;
	}

	/**
	 *  @return Channel 9 - fp32_t
	 */
	public double getCh09() {
		return getDouble("ch09");
	}

	/**
	 *  @param ch09 Channel 9
	 */
	public DmsDetection setCh09(double ch09) {
		values.put("ch09", ch09);
		return this;
	}

	/**
	 *  @return Channel 10 - fp32_t
	 */
	public double getCh10() {
		return getDouble("ch10");
	}

	/**
	 *  @param ch10 Channel 10
	 */
	public DmsDetection setCh10(double ch10) {
		values.put("ch10", ch10);
		return this;
	}

	/**
	 *  @return Channel 11 - fp32_t
	 */
	public double getCh11() {
		return getDouble("ch11");
	}

	/**
	 *  @param ch11 Channel 11
	 */
	public DmsDetection setCh11(double ch11) {
		values.put("ch11", ch11);
		return this;
	}

	/**
	 *  @return Channel 12 - fp32_t
	 */
	public double getCh12() {
		return getDouble("ch12");
	}

	/**
	 *  @param ch12 Channel 12
	 */
	public DmsDetection setCh12(double ch12) {
		values.put("ch12", ch12);
		return this;
	}

	/**
	 *  @return Channel 13 - fp32_t
	 */
	public double getCh13() {
		return getDouble("ch13");
	}

	/**
	 *  @param ch13 Channel 13
	 */
	public DmsDetection setCh13(double ch13) {
		values.put("ch13", ch13);
		return this;
	}

	/**
	 *  @return Channel 14 - fp32_t
	 */
	public double getCh14() {
		return getDouble("ch14");
	}

	/**
	 *  @param ch14 Channel 14
	 */
	public DmsDetection setCh14(double ch14) {
		values.put("ch14", ch14);
		return this;
	}

	/**
	 *  @return Channel 15 - fp32_t
	 */
	public double getCh15() {
		return getDouble("ch15");
	}

	/**
	 *  @param ch15 Channel 15
	 */
	public DmsDetection setCh15(double ch15) {
		values.put("ch15", ch15);
		return this;
	}

	/**
	 *  @return Channel 16 - fp32_t
	 */
	public double getCh16() {
		return getDouble("ch16");
	}

	/**
	 *  @param ch16 Channel 16
	 */
	public DmsDetection setCh16(double ch16) {
		values.put("ch16", ch16);
		return this;
	}

}
