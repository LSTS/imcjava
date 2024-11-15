/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message ENC Awareness (913)<br/>
 *  Contains information as extracted from a digital S-57 chart.<br/>
 *  This can be: location of static objects (buoys, beacons, etc), location and depth of depth contours,<br/>
 *  location and depth of any other location contained in the chart.<br/>
 *  For reference see Supervisors/Grounding.<br/>
 */

public class ENCAwareness extends IMCMessage {

	public static final int ID_STATIC = 913;

	public ENCAwareness() {
		super(ID_STATIC);
	}

	public ENCAwareness(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ENCAwareness(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static ENCAwareness create(Object... values) {
		ENCAwareness m = new ENCAwareness();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static ENCAwareness clone(IMCMessage msg) throws Exception {

		ENCAwareness m = new ENCAwareness();
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

	public ENCAwareness(String depth_at_loc, String danger) {
		super(ID_STATIC);
		if (depth_at_loc != null)
			setDepthAtLoc(depth_at_loc);
		if (danger != null)
			setDanger(danger);
	}

	/**
	 *  @return Depth at location - plaintext
	 */
	public String getDepthAtLoc() {
		return getString("depth_at_loc");
	}

	/**
	 *  @param depth_at_loc Depth at location
	 */
	public ENCAwareness setDepthAtLoc(String depth_at_loc) {
		values.put("depth_at_loc", depth_at_loc);
		return this;
	}

	/**
	 *  @return Danger - plaintext
	 */
	public String getDanger() {
		return getString("danger");
	}

	/**
	 *  @param danger Danger
	 */
	public ENCAwareness setDanger(String danger) {
		values.put("danger", danger);
		return this;
	}

}
