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
 *  IMC Message Formation Parameters (476)<br/>
 *  A "Formation" is defined by the relative positions of the vehicles<br/>
 *  inside the formation, and the reference frame where this positions are defined.<br/>
 *  The formation reference frame may be:<br/>
 *  - Earth Fixed: Where the vehicles relative position do not depend on the followed path.<br/>
 *  This results in all UAVs following the same path with an offset relative to each other;<br/>
 *  - Path Fixed:  Where the vehicles relative position depends on the followed path,<br/>
 *  changing the inter-vehicle offset direction with the path direction.<br/>
 *  - Path Curved:  Where the vehicles relative position depends on the followed path,<br/>
 *  changing the inter-vehicle offset direction with the path direction and direction<br/>
 *  change rate.<br/>
 *  An offset in the xx axis results in a distance over the curved path line.<br/>
 *  An offset in the yy axis results in an offset of the vehicle path line relative to the<br/>
 *  formation center path line.<br/>
 */

public class FormationParameters extends IMCMessage {

	public enum REFERENCE_FRAME {
		EARTH_FIXED(0),
		PATH_FIXED(1),
		PATH_CURVED(2);

		protected long value;

		public long value() {
			return value;
		}

		REFERENCE_FRAME(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 476;

	public FormationParameters() {
		super(ID_STATIC);
	}

	public FormationParameters(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormationParameters(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FormationParameters create(Object... values) {
		FormationParameters m = new FormationParameters();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FormationParameters clone(IMCMessage msg) throws Exception {

		FormationParameters m = new FormationParameters();
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

	public FormationParameters(String formation_name, REFERENCE_FRAME reference_frame, java.util.Collection<VehicleFormationParticipant> participants, String custom) {
		super(ID_STATIC);
		if (formation_name != null)
			setFormationName(formation_name);
		setReferenceFrame(reference_frame);
		if (participants != null)
			setParticipants(participants);
		if (custom != null)
			setCustom(custom);
	}

	/**
	 *  @return Formation Name - plaintext
	 */
	public String getFormationName() {
		return getString("formation_name");
	}

	/**
	 *  @param formation_name Formation Name
	 */
	public FormationParameters setFormationName(String formation_name) {
		values.put("formation_name", formation_name);
		return this;
	}

	/**
	 *  @return Formation Reference Frame (enumerated) - uint8_t
	 */
	public REFERENCE_FRAME getReferenceFrame() {
		try {
			REFERENCE_FRAME o = REFERENCE_FRAME.valueOf(getMessageType().getFieldPossibleValues("reference_frame").get(getLong("reference_frame")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getReferenceFrameStr() {
		return getString("reference_frame");
	}

	public short getReferenceFrameVal() {
		return (short) getInteger("reference_frame");
	}

	/**
	 *  @param reference_frame Formation Reference Frame (enumerated)
	 */
	public FormationParameters setReferenceFrame(REFERENCE_FRAME reference_frame) {
		values.put("reference_frame", reference_frame.value());
		return this;
	}

	/**
	 *  @param reference_frame Formation Reference Frame (as a String)
	 */
	public FormationParameters setReferenceFrameStr(String reference_frame) {
		setValue("reference_frame", reference_frame);
		return this;
	}

	/**
	 *  @param reference_frame Formation Reference Frame (integer value)
	 */
	public FormationParameters setReferenceFrameVal(short reference_frame) {
		setValue("reference_frame", reference_frame);
		return this;
	}

	/**
	 *  @return Formation Participants - message-list
	 */
	public java.util.Vector<VehicleFormationParticipant> getParticipants() {
		try {
			return getMessageList("participants", VehicleFormationParticipant.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param participants Formation Participants
	 */
	public FormationParameters setParticipants(java.util.Collection<VehicleFormationParticipant> participants) {
		values.put("participants", participants);
		return this;
	}

	/**
	 *  @return Custom settings for formation (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getCustom() {
		return getTupleList("custom");
	}

	/**
	 *  @param custom Custom settings for formation (tuplelist)
	 */
	public FormationParameters setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public FormationParameters setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
