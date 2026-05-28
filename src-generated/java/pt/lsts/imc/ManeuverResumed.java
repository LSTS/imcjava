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
 *  IMC Message Maneuver Resumed (2020)<br/>
 *  This message is sent when a maneuver is stoped, describing how it could be resumed to completion later.<br/>
 */

public class ManeuverResumed extends IMCMessage {

	public static final int ID_STATIC = 2020;

	public ManeuverResumed() {
		super(ID_STATIC);
	}

	public ManeuverResumed(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ManeuverResumed(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ManeuverResumed create(Object... values) {
		ManeuverResumed m = new ManeuverResumed();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ManeuverResumed clone(IMCMessage msg) throws Exception {

		ManeuverResumed m = new ManeuverResumed();
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

	public ManeuverResumed(String man_id, java.util.Collection<Maneuver> man_list) {
		super(ID_STATIC);
		if (man_id != null)
			setManId(man_id);
		if (man_list != null)
			setManList(man_list);
	}

	/**
	 *  @return Maneuver Identifier - plaintext
	 */
	public String getManId() {
		return getString("man_id");
	}

	/**
	 *  @param man_id Maneuver Identifier
	 */
	public ManeuverResumed setManId(String man_id) {
		values.put("man_id", man_id);
		return this;
	}

	/**
	 *  @return Maneuver List - message-list
	 */
	public java.util.Vector<Maneuver> getManList() {
		try {
			return getMessageList("man_list", Maneuver.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param man_list Maneuver List
	 */
	public ManeuverResumed setManList(java.util.Collection<Maneuver> man_list) {
		values.put("man_list", man_list);
		return this;
	}

}
