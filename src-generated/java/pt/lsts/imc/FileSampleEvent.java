/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2026, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message File Sample Event (1102)<br/>
 *  Report a sample stored to disk.<br/>
 */

public class FileSampleEvent extends IMCMessage {

	public enum FSTYPE {
		IMAGE(0),
		AUDIO(1),
		SONAR(2),
		OTHER(255);

		protected long value;

		public long value() {
			return value;
		}

		FSTYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 1102;

	public FileSampleEvent() {
		super(ID_STATIC);
	}

	public FileSampleEvent(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileSampleEvent(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FileSampleEvent create(Object... values) {
		FileSampleEvent m = new FileSampleEvent();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FileSampleEvent clone(IMCMessage msg) throws Exception {

		FileSampleEvent m = new FileSampleEvent();
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

	public FileSampleEvent(FSTYPE fstype, String filename) {
		super(ID_STATIC);
		setFstype(fstype);
		if (filename != null)
			setFilename(filename);
	}

	/**
	 *  @return File Type (enumerated) - uint8_t
	 */
	public FSTYPE getFstype() {
		try {
			FSTYPE o = FSTYPE.valueOf(getMessageType().getFieldPossibleValues("fstype").get(getLong("fstype")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getFstypeStr() {
		return getString("fstype");
	}

	public short getFstypeVal() {
		return (short) getInteger("fstype");
	}

	/**
	 *  @param fstype File Type (enumerated)
	 */
	public FileSampleEvent setFstype(FSTYPE fstype) {
		values.put("fstype", fstype.value());
		return this;
	}

	/**
	 *  @param fstype File Type (as a String)
	 */
	public FileSampleEvent setFstypeStr(String fstype) {
		setValue("fstype", fstype);
		return this;
	}

	/**
	 *  @param fstype File Type (integer value)
	 */
	public FileSampleEvent setFstypeVal(short fstype) {
		setValue("fstype", fstype);
		return this;
	}

	/**
	 *  @return File name - plaintext
	 */
	public String getFilename() {
		return getString("filename");
	}

	/**
	 *  @param filename File name
	 */
	public FileSampleEvent setFilename(String filename) {
		values.put("filename", filename);
		return this;
	}

}
