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
 *  IMC Message External Navigation Data (294)<br/>
 *  This message is a representation of the state of the vehicle,<br/>
 *  as seen by an external navigation computer.<br/>
 *  An example usage is when DUNE is used with ardupilot. The<br/>
 *  data gathered from the autopilot is a complete navigation<br/>
 *  solution.<br/>
 *  ExternalNavData contains an inline Estimated State, which<br/>
 *  is a complete description of the system<br/>
 *  in terms of parameters such as position, orientation and<br/>
 *  velocities at a particular moment in time.<br/>
 *  The Type field selects wether the navigation data is a<br/>
 *  full state estimation, or only concerns attitude or<br/>
 *  position/velocity.<br/>
 */

public class ExternalNavData extends IMCMessage {

	public enum TYPE {
		FULL(0),
		AHRS(1),
		POSREF(2);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 294;

	public ExternalNavData() {
		super(ID_STATIC);
	}

	public ExternalNavData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ExternalNavData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ExternalNavData create(Object... values) {
		ExternalNavData m = new ExternalNavData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ExternalNavData clone(IMCMessage msg) throws Exception {

		ExternalNavData m = new ExternalNavData();
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

	public ExternalNavData(EstimatedState state, TYPE type) {
		super(ID_STATIC);
		if (state != null)
			setState(state);
		setType(type);
	}

	/**
	 *  @return Estimated State - message
	 */
	public EstimatedState getState() {
		try {
			IMCMessage obj = getMessage("state");
			if (obj instanceof EstimatedState)
				return (EstimatedState) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param state Estimated State
	 */
	public ExternalNavData setState(EstimatedState state) {
		values.put("state", state);
		return this;
	}

	/**
	 *  @return Nav Data Type (enumerated) - uint8_t
	 */
	public TYPE getType() {
		try {
			TYPE o = TYPE.valueOf(getMessageType().getFieldPossibleValues("type").get(getLong("type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getTypeStr() {
		return getString("type");
	}

	public short getTypeVal() {
		return (short) getInteger("type");
	}

	/**
	 *  @param type Nav Data Type (enumerated)
	 */
	public ExternalNavData setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Nav Data Type (as a String)
	 */
	public ExternalNavData setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Nav Data Type (integer value)
	 */
	public ExternalNavData setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

}
