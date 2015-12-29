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
 *  IMC Message Entity Information (3)<br/>
 *  This message describes an entity.<br/>
 */

public class EntityInfo extends IMCMessage {

	public static final int ID_STATIC = 3;

	public EntityInfo() {
		super(ID_STATIC);
	}

	public EntityInfo(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EntityInfo(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static EntityInfo create(Object... values) {
		EntityInfo m = new EntityInfo();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static EntityInfo clone(IMCMessage msg) throws Exception {

		EntityInfo m = new EntityInfo();
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

	public EntityInfo(short id, String label, String component, int act_time, int deact_time) {
		super(ID_STATIC);
		setId(id);
		if (label != null)
			setLabel(label);
		if (component != null)
			setComponent(component);
		setActTime(act_time);
		setDeactTime(deact_time);
	}

	/**
	 *  @return Entity Identifier - uint8_t
	 */
	public short getId() {
		return (short) getInteger("id");
	}

	/**
	 *  @param id Entity Identifier
	 */
	public EntityInfo setId(short id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return Label - plaintext
	 */
	public String getLabel() {
		return getString("label");
	}

	/**
	 *  @param label Label
	 */
	public EntityInfo setLabel(String label) {
		values.put("label", label);
		return this;
	}

	/**
	 *  @return Component name - plaintext
	 */
	public String getComponent() {
		return getString("component");
	}

	/**
	 *  @param component Component name
	 */
	public EntityInfo setComponent(String component) {
		values.put("component", component);
		return this;
	}

	/**
	 *  @return Activation Time (s) - uint16_t
	 */
	public int getActTime() {
		return getInteger("act_time");
	}

	/**
	 *  @param act_time Activation Time (s)
	 */
	public EntityInfo setActTime(int act_time) {
		values.put("act_time", act_time);
		return this;
	}

	/**
	 *  @return Deactivation Time (s) - uint16_t
	 */
	public int getDeactTime() {
		return getInteger("deact_time");
	}

	/**
	 *  @param deact_time Deactivation Time (s)
	 */
	public EntityInfo setDeactTime(int deact_time) {
		values.put("deact_time", deact_time);
		return this;
	}

}
