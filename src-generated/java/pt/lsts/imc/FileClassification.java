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
 *  IMC Message File Classification Event (1104)<br/>
 *  Signals that an object has been detected by any of the vehicle's sensors<br/>
 */

public class FileClassification extends IMCMessage {

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

	public static final int ID_STATIC = 1104;

	public FileClassification() {
		super(ID_STATIC);
	}

	public FileClassification(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileClassification(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FileClassification create(Object... values) {
		FileClassification m = new FileClassification();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FileClassification clone(IMCMessage msg) throws Exception {

		FileClassification m = new FileClassification();
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

	public FileClassification(String original_filepath, String resized_filepath, String class_prediction, short confidence, FSTYPE fstype) {
		super(ID_STATIC);
		if (original_filepath != null)
			setOriginalFilepath(original_filepath);
		if (resized_filepath != null)
			setResizedFilepath(resized_filepath);
		if (class_prediction != null)
			setClassPrediction(class_prediction);
		setConfidence(confidence);
		setFstype(fstype);
	}

	/**
	 *  @return Original Filepath - plaintext
	 */
	public String getOriginalFilepath() {
		return getString("original_filepath");
	}

	/**
	 *  @param original_filepath Original Filepath
	 */
	public FileClassification setOriginalFilepath(String original_filepath) {
		values.put("original_filepath", original_filepath);
		return this;
	}

	/**
	 *  @return Resized Filepath - plaintext
	 */
	public String getResizedFilepath() {
		return getString("resized_filepath");
	}

	/**
	 *  @param resized_filepath Resized Filepath
	 */
	public FileClassification setResizedFilepath(String resized_filepath) {
		values.put("resized_filepath", resized_filepath);
		return this;
	}

	/**
	 *  @return Classification Prediction - plaintext
	 */
	public String getClassPrediction() {
		return getString("class_prediction");
	}

	/**
	 *  @param class_prediction Classification Prediction
	 */
	public FileClassification setClassPrediction(String class_prediction) {
		values.put("class_prediction", class_prediction);
		return this;
	}

	/**
	 *  @return Confidence Value - uint8_t
	 */
	public short getConfidence() {
		return (short) getInteger("confidence");
	}

	/**
	 *  @param confidence Confidence Value
	 */
	public FileClassification setConfidence(short confidence) {
		values.put("confidence", confidence);
		return this;
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
	public FileClassification setFstype(FSTYPE fstype) {
		values.put("fstype", fstype.value());
		return this;
	}

	/**
	 *  @param fstype File Type (as a String)
	 */
	public FileClassification setFstypeStr(String fstype) {
		setValue("fstype", fstype);
		return this;
	}

	/**
	 *  @param fstype File Type (integer value)
	 */
	public FileClassification setFstypeVal(short fstype) {
		setValue("fstype", fstype);
		return this;
	}

}
