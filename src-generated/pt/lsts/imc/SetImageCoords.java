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
 *  IMC Message Set Image Coordinates (895)<br/>
 *  Message containing the x and y coordinates of object to track in remote peer.<br/>
 */

public class SetImageCoords extends IMCMessage {

	public static final int ID_STATIC = 895;

	public SetImageCoords() {
		super(ID_STATIC);
	}

	public SetImageCoords(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SetImageCoords(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SetImageCoords create(Object... values) {
		SetImageCoords m = new SetImageCoords();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SetImageCoords clone(IMCMessage msg) throws Exception {

		SetImageCoords m = new SetImageCoords();
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

	public SetImageCoords(short camId, int x, int y) {
		super(ID_STATIC);
		setCamId(camId);
		setX(x);
		setY(y);
	}

	/**
	 *  @return Camera Identifier - uint8_t
	 */
	public short getCamId() {
		return (short) getInteger("camId");
	}

	/**
	 *  @param camId Camera Identifier
	 */
	public SetImageCoords setCamId(short camId) {
		values.put("camId", camId);
		return this;
	}

	/**
	 *  @return X (px) - uint16_t
	 */
	public int getX() {
		return getInteger("x");
	}

	/**
	 *  @param x X (px)
	 */
	public SetImageCoords setX(int x) {
		values.put("x", x);
		return this;
	}

	/**
	 *  @return Y (px) - uint16_t
	 */
	public int getY() {
		return getInteger("y");
	}

	/**
	 *  @param y Y (px)
	 */
	public SetImageCoords setY(int y) {
		values.put("y", y);
		return this;
	}

}
