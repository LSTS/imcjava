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
 *  IMC Message Image Snippet (704)<br/>
 *  Small image encoded as several frames (transferable over low bandwidth lossy links).<br/>
 */

public class ImageSnippet extends IMCMessage {

	public enum CODEC {
		JPEG2000(0);

		protected long value;

		public long value() {
			return value;
		}

		CODEC(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 704;

	public ImageSnippet() {
		super(ID_STATIC);
	}

	public ImageSnippet(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ImageSnippet(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ImageSnippet create(Object... values) {
		ImageSnippet m = new ImageSnippet();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ImageSnippet clone(IMCMessage msg) throws Exception {

		ImageSnippet m = new ImageSnippet();
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

	public ImageSnippet(int snippet_id, short total_frames, short frame_id, CODEC codec, byte[] data) {
		super(ID_STATIC);
		setSnippetId(snippet_id);
		setTotalFrames(total_frames);
		setFrameId(frame_id);
		setCodec(codec);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Snippet Id - uint16_t
	 */
	public int getSnippetId() {
		return getInteger("snippet_id");
	}

	/**
	 *  @param snippet_id Snippet Id
	 */
	public ImageSnippet setSnippetId(int snippet_id) {
		values.put("snippet_id", snippet_id);
		return this;
	}

	/**
	 *  @return Total Frames - uint8_t
	 */
	public short getTotalFrames() {
		return (short) getInteger("total_frames");
	}

	/**
	 *  @param total_frames Total Frames
	 */
	public ImageSnippet setTotalFrames(short total_frames) {
		values.put("total_frames", total_frames);
		return this;
	}

	/**
	 *  @return Frame Number - uint8_t
	 */
	public short getFrameId() {
		return (short) getInteger("frame_id");
	}

	/**
	 *  @param frame_id Frame Number
	 */
	public ImageSnippet setFrameId(short frame_id) {
		values.put("frame_id", frame_id);
		return this;
	}

	/**
	 *  @return Codec (enumerated) - uint8_t
	 */
	public CODEC getCodec() {
		try {
			CODEC o = CODEC.valueOf(getMessageType().getFieldPossibleValues("codec").get(getLong("codec")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCodecStr() {
		return getString("codec");
	}

	public short getCodecVal() {
		return (short) getInteger("codec");
	}

	/**
	 *  @param codec Codec (enumerated)
	 */
	public ImageSnippet setCodec(CODEC codec) {
		values.put("codec", codec.value());
		return this;
	}

	/**
	 *  @param codec Codec (as a String)
	 */
	public ImageSnippet setCodecStr(String codec) {
		setValue("codec", codec);
		return this;
	}

	/**
	 *  @param codec Codec (integer value)
	 */
	public ImageSnippet setCodecVal(short codec) {
		setValue("codec", codec);
		return this;
	}

	/**
	 *  @return Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Data
	 */
	public ImageSnippet setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
