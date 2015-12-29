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
 *  IMC Message Image Transmission Settings (703)<br/>
 */

public class ImageTxSettings extends IMCMessage {

	public static final int ID_STATIC = 703;

	public ImageTxSettings() {
		super(ID_STATIC);
	}

	public ImageTxSettings(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ImageTxSettings(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ImageTxSettings create(Object... values) {
		ImageTxSettings m = new ImageTxSettings();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ImageTxSettings clone(IMCMessage msg) throws Exception {

		ImageTxSettings m = new ImageTxSettings();
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

	public ImageTxSettings(short fps, short quality, short reps, short tsize) {
		super(ID_STATIC);
		setFps(fps);
		setQuality(quality);
		setReps(reps);
		setTsize(tsize);
	}

	/**
	 *  @return Frames Per Second - uint8_t
	 */
	public short getFps() {
		return (short) getInteger("fps");
	}

	/**
	 *  @param fps Frames Per Second
	 */
	public ImageTxSettings setFps(short fps) {
		values.put("fps", fps);
		return this;
	}

	/**
	 *  @return Quality - uint8_t
	 */
	public short getQuality() {
		return (short) getInteger("quality");
	}

	/**
	 *  @param quality Quality
	 */
	public ImageTxSettings setQuality(short quality) {
		values.put("quality", quality);
		return this;
	}

	/**
	 *  @return Repetitions - uint8_t
	 */
	public short getReps() {
		return (short) getInteger("reps");
	}

	/**
	 *  @param reps Repetitions
	 */
	public ImageTxSettings setReps(short reps) {
		values.put("reps", reps);
		return this;
	}

	/**
	 *  @return Target Size - uint8_t
	 */
	public short getTsize() {
		return (short) getInteger("tsize");
	}

	/**
	 *  @param tsize Target Size
	 */
	public ImageTxSettings setTsize(short tsize) {
		values.put("tsize", tsize);
		return this;
	}

}
