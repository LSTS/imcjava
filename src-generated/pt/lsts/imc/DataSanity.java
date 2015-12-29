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
 *  IMC Message Data Sanity (284)<br/>
 *  Report sanity or lack of it in the data output by a sensor.<br/>
 */

public class DataSanity extends IMCMessage {

	public enum SANE {
		SANE(0),
		NOT_SANE(1);

		protected long value;

		public long value() {
			return value;
		}

		SANE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 284;

	public DataSanity() {
		super(ID_STATIC);
	}

	public DataSanity(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DataSanity(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static DataSanity create(Object... values) {
		DataSanity m = new DataSanity();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static DataSanity clone(IMCMessage msg) throws Exception {

		DataSanity m = new DataSanity();
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

	public DataSanity(SANE sane) {
		super(ID_STATIC);
		setSane(sane);
	}

	/**
	 *  @return Sanity (enumerated) - uint8_t
	 */
	public SANE getSane() {
		try {
			SANE o = SANE.valueOf(getMessageType().getFieldPossibleValues("sane").get(getLong("sane")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSaneStr() {
		return getString("sane");
	}

	public short getSaneVal() {
		return (short) getInteger("sane");
	}

	/**
	 *  @param sane Sanity (enumerated)
	 */
	public DataSanity setSane(SANE sane) {
		values.put("sane", sane.value());
		return this;
	}

	/**
	 *  @param sane Sanity (as a String)
	 */
	public DataSanity setSaneStr(String sane) {
		setValue("sane", sane);
		return this;
	}

	/**
	 *  @param sane Sanity (integer value)
	 */
	public DataSanity setSaneVal(short sane) {
		setValue("sane", sane);
		return this;
	}

}
