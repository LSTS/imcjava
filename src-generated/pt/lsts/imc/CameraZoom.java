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
 *  IMC Message Camera Zoom (300)<br/>
 *  Camera Zoom.<br/>
 */

public class CameraZoom extends IMCMessage {

	public enum ACTION {
		ZOOM_RESET(0),
		ZOOM_IN(1),
		ZOOM_OUT(2),
		ZOOM_STOP(3);

		protected long value;

		public long value() {
			return value;
		}

		ACTION(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 300;

	public CameraZoom() {
		super(ID_STATIC);
	}

	public CameraZoom(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CameraZoom(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CameraZoom create(Object... values) {
		CameraZoom m = new CameraZoom();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CameraZoom clone(IMCMessage msg) throws Exception {

		CameraZoom m = new CameraZoom();
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

	public CameraZoom(short id, short zoom, ACTION action) {
		super(ID_STATIC);
		setId(id);
		setZoom(zoom);
		setAction(action);
	}

	/**
	 *  @return Camera Number - uint8_t
	 */
	public short getId() {
		return (short) getInteger("id");
	}

	/**
	 *  @param id Camera Number
	 */
	public CameraZoom setId(short id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Absolute Zoom Level - uint8_t
	 */
	public short getZoom() {
		return (short) getInteger("zoom");
	}

	/**
	 *  @param zoom Absolute Zoom Level
	 */
	public CameraZoom setZoom(short zoom) {
		values.put("zoom", zoom);
		return this;
	}

	/**
	 *  @return Action (enumerated) - uint8_t
	 */
	public ACTION getAction() {
		try {
			ACTION o = ACTION.valueOf(getMessageType().getFieldPossibleValues("action").get(getLong("action")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getActionStr() {
		return getString("action");
	}

	public short getActionVal() {
		return (short) getInteger("action");
	}

	/**
	 *  @param action Action (enumerated)
	 */
	public CameraZoom setAction(ACTION action) {
		values.put("action", action.value());
		return this;
	}

	/**
	 *  @param action Action (as a String)
	 */
	public CameraZoom setActionStr(String action) {
		setValue("action", action);
		return this;
	}

	/**
	 *  @param action Action (integer value)
	 */
	public CameraZoom setActionVal(short action) {
		setValue("action", action);
		return this;
	}

}
