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
 *  IMC Message System Group (181)<br/>
 *  Group of systems configuration.<br/>
 */

public class SystemGroup extends IMCMessage {

	public enum ACTION {
		DIS(0),
		SET(1),
		REQ(2),
		CHG(3),
		REP(4),
		FRC(5);

		protected long value;

		public long value() {
			return value;
		}

		ACTION(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 181;

	public SystemGroup() {
		super(ID_STATIC);
	}

	public SystemGroup(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SystemGroup(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static SystemGroup create(Object... values) {
		SystemGroup m = new SystemGroup();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static SystemGroup clone(IMCMessage msg) throws Exception {

		SystemGroup m = new SystemGroup();
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

	public SystemGroup(String GroupName, ACTION Action, String GroupList) {
		super(ID_STATIC);
		if (GroupName != null)
			setGroupName(GroupName);
		setAction(Action);
		if (GroupList != null)
			setGroupList(GroupList);
	}

	/**
	 *  @return Group Name - plaintext
	 */
	public String getGroupName() {
		return getString("GroupName");
	}

	/**
	 *  @param GroupName Group Name
	 */
	public SystemGroup setGroupName(String GroupName) {
		values.put("GroupName", GroupName);
		return this;
	}

	/**
	 *  @return Group List Action (enumerated) - uint8_t
	 */
	public ACTION getAction() {
		try {
			ACTION o = ACTION.valueOf(getMessageType().getFieldPossibleValues("Action").get(getLong("Action")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getActionStr() {
		return getString("Action");
	}

	public short getActionVal() {
		return (short) getInteger("Action");
	}

	/**
	 *  @param Action Group List Action (enumerated)
	 */
	public SystemGroup setAction(ACTION Action) {
		values.put("Action", Action.value());
		return this;
	}

	/**
	 *  @param Action Group List Action (as a String)
	 */
	public SystemGroup setActionStr(String Action) {
		setValue("Action", Action);
		return this;
	}

	/**
	 *  @param Action Group List Action (integer value)
	 */
	public SystemGroup setActionVal(short Action) {
		setValue("Action", Action);
		return this;
	}

	/**
	 *  @return Systems Name List - plaintext
	 */
	public String getGroupList() {
		return getString("GroupList");
	}

	/**
	 *  @param GroupList Systems Name List
	 */
	public SystemGroup setGroupList(String GroupList) {
		values.put("GroupList", GroupList);
		return this;
	}

}
