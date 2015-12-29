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
 *  IMC Message Low Level Control Maneuver (455)<br/>
 *  Low level maneuver that sends a (heading, roll, speed, ...)<br/>
 *  reference to a controller of the vehicle and then optionally<br/>
 *  lingers for some time.<br/>
 */

@SuppressWarnings("unchecked")
public class LowLevelControl extends Maneuver {

	public static final int ID_STATIC = 455;

	public LowLevelControl() {
		super(ID_STATIC);
	}

	public LowLevelControl(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LowLevelControl(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static LowLevelControl create(Object... values) {
		LowLevelControl m = new LowLevelControl();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static LowLevelControl clone(IMCMessage msg) throws Exception {

		LowLevelControl m = new LowLevelControl();
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

	public LowLevelControl(ControlCommand control, int duration, String custom) {
		super(ID_STATIC);
		if (control != null)
			setControl(control);
		setDuration(duration);
		if (custom != null)
			setCustom(custom);
	}

	/**
	 *  @return Control - message
	 */
	public ControlCommand getControl() {
		try {
			IMCMessage obj = getMessage("control");
			if (obj instanceof ControlCommand)
				return (ControlCommand) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param control Control
	 */
	public LowLevelControl setControl(ControlCommand control) {
		values.put("control", control);
		return this;
	}

	/**
	 *  @return Duration (s) - uint16_t
	 */
	public int getDuration() {
		return getInteger("duration");
	}

	/**
	 *  @param duration Duration (s)
	 */
	public LowLevelControl setDuration(int duration) {
		values.put("duration", duration);
		return this;
	}

	/**
	 *  @return Custom settings for maneuver (tuplelist) - plaintext
	 */
	public java.util.LinkedHashMap<String, String> getCustom() {
		return getTupleList("custom");
	}

	/**
	 *  @param custom Custom settings for maneuver (tuplelist)
	 */
	public LowLevelControl setCustom(java.util.LinkedHashMap<String, ?> custom) {
		String val = encodeTupleList(custom);
		values.put("custom", val);
		return this;
	}

	public LowLevelControl setCustom(String custom) {
		values.put("custom", custom);
		return this;
	}

}
