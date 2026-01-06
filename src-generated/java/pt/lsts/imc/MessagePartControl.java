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
 *  IMC Message Message Fragment Control (878)<br/>
 *  This message is used by the receiver of MessageParts messages<br/>
 *  to inform the sender of the status of the reception of a message<br/>
 *  in fragments.<br/>
 *  The sender can then use this information to determine which<br/>
 *  fragments were received and which ones were not.<br/>
 *  This message is sent in response to a MessagePart message.<br/>
 */

public class MessagePartControl extends IMCMessage {

	public enum OP {
		STATUS_RECEIVED(0),
		REQUEST_RETRANSMIT(1);

		protected long value;

		public long value() {
			return value;
		}

		OP(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 878;

	public MessagePartControl() {
		super(ID_STATIC);
	}

	public MessagePartControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MessagePartControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static MessagePartControl create(Object... values) {
		MessagePartControl m = new MessagePartControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static MessagePartControl clone(IMCMessage msg) throws Exception {

		MessagePartControl m = new MessagePartControl();
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

	public MessagePartControl(short uid, OP op, String frag_ids) {
		super(ID_STATIC);
		setUid(uid);
		setOp(op);
		if (frag_ids != null)
			setFragIds(frag_ids);
	}

	/**
	 *  @return Transmission Unique Id - uint8_t
	 */
	public short getUid() {
		return (short) getInteger("uid");
	}

	/**
	 *  @param uid Transmission Unique Id
	 */
	public MessagePartControl setUid(short uid) {
		values.put("uid", uid);
		return this;
	}

	/**
	 *  @return Operation (enumerated) - uint8_t
	 */
	public OP getOp() {
		try {
			OP o = OP.valueOf(getMessageType().getFieldPossibleValues("op").get(getLong("op")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getOpStr() {
		return getString("op");
	}

	public short getOpVal() {
		return (short) getInteger("op");
	}

	/**
	 *  @param op Operation (enumerated)
	 */
	public MessagePartControl setOp(OP op) {
		values.put("op", op.value());
		return this;
	}

	/**
	 *  @param op Operation (as a String)
	 */
	public MessagePartControl setOpStr(String op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @param op Operation (integer value)
	 */
	public MessagePartControl setOpVal(short op) {
		setValue("op", op);
		return this;
	}

	/**
	 *  @return Fragments IDs - plaintext
	 */
	public String getFragIds() {
		return getString("frag_ids");
	}

	/**
	 *  @param frag_ids Fragments IDs
	 */
	public MessagePartControl setFragIds(String frag_ids) {
		values.put("frag_ids", frag_ids);
		return this;
	}

}
