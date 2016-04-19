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
 *  IMC Message Historic Data Sample (186)<br/>
 */

@SuppressWarnings("unchecked")
public class HistoricSample extends RemoteData {

	public static final int ID_STATIC = 186;

	public HistoricSample() {
		super(ID_STATIC);
	}

	public HistoricSample(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HistoricSample(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static HistoricSample create(Object... values) {
		HistoricSample m = new HistoricSample();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static HistoricSample clone(IMCMessage msg) throws Exception {

		HistoricSample m = new HistoricSample();
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

	public HistoricSample(int sys_id, byte priority, short x, short y, short z, short t, IMCMessage sample) {
		super(ID_STATIC);
		setSysId(sys_id);
		setPriority(priority);
		setX(x);
		setY(y);
		setZ(z);
		setT(t);
		if (sample != null)
			setSample(sample);
	}

	/**
	 *  @return Original System Id - uint16_t
	 */
	public int getSysId() {
		return getInteger("sys_id");
	}

	/**
	 *  @param sys_id Original System Id
	 */
	public HistoricSample setSysId(int sys_id) {
		values.put("sys_id", sys_id);
		return this;
	}

	/**
	 *  @return Priority - int8_t
	 */
	public byte getPriority() {
		return (byte) getInteger("priority");
	}

	/**
	 *  @param priority Priority
	 */
	public HistoricSample setPriority(byte priority) {
		values.put("priority", priority);
		return this;
	}

	/**
	 *  @return X offset (m) - int16_t
	 */
	public short getX() {
		return (short) getInteger("x");
	}

	/**
	 *  @param x X offset (m)
	 */
	public HistoricSample setX(short x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y offset (m) - int16_t
	 */
	public short getY() {
		return (short) getInteger("y");
	}

	/**
	 *  @param y Y offset (m)
	 */
	public HistoricSample setY(short y) {
		values.put("y", y);
		return this;
	}

	/**
	 *  @return Z offset (dm) - int16_t
	 */
	public short getZ() {
		return (short) getInteger("z");
	}

	/**
	 *  @param z Z offset (dm)
	 */
	public HistoricSample setZ(short z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Time offset (s) - int16_t
	 */
	public short getT() {
		return (short) getInteger("t");
	}

	/**
	 *  @param t Time offset (s)
	 */
	public HistoricSample setT(short t) {
		values.put("t", t);
		return this;
	}

	/**
	 *  @return Data Sample - message
	 */
	public IMCMessage getSample() {
		return getMessage("sample");
	}

	public <T extends IMCMessage> T getSample(Class<T> clazz) throws Exception {
		return getMessage(clazz, "sample");
	}

	/**
	 *  @param sample Data Sample
	 */
	public HistoricSample setSample(IMCMessage sample) {
		values.put("sample", sample);
		return this;
	}

}
