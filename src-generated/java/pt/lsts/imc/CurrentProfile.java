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
 *  IMC Message Current Profile (1014)<br/>
 *  Contains a profile of water velocities measured relative to the vehicle<br/>
 *  velocity, represented in the specified coordinate system.<br/>
 */

public class CurrentProfile extends IMCMessage {

	public static final short UTF_XYZ = 0x01;
	public static final short UTF_NED = 0x02;
	public static final short UTF_BEAMS = 0x04;
	public static final short UTF_ENU = 0x08;

	public static final int ID_STATIC = 1014;

	public CurrentProfile() {
		super(ID_STATIC);
	}

	public CurrentProfile(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CurrentProfile(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static CurrentProfile create(Object... values) {
		CurrentProfile m = new CurrentProfile();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static CurrentProfile clone(IMCMessage msg) throws Exception {

		CurrentProfile m = new CurrentProfile();
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

	public CurrentProfile(short nbeams, short ncells, short coord_sys, java.util.Collection<CurrentProfileCell> profile) {
		super(ID_STATIC);
		setNbeams(nbeams);
		setNcells(ncells);
		setCoordSys(coord_sys);
		if (profile != null)
			setProfile(profile);
	}

	/**
	 *  @return Number of Beams - uint8_t
	 */
	public short getNbeams() {
		return (short) getInteger("nbeams");
	}

	/**
	 *  @param nbeams Number of Beams
	 */
	public CurrentProfile setNbeams(short nbeams) {
		values.put("nbeams", nbeams);
		return this;
	}

	/**
	 *  @return Number of Cells - uint8_t
	 */
	public short getNcells() {
		return (short) getInteger("ncells");
	}

	/**
	 *  @param ncells Number of Cells
	 */
	public CurrentProfile setNcells(short ncells) {
		values.put("ncells", ncells);
		return this;
	}

	/**
	 *  @return Coordinate System (bitfield) - uint8_t
	 */
	public short getCoordSys() {
		return (short) getInteger("coord_sys");
	}

	/**
	 *  @param coord_sys Coordinate System (bitfield)
	 */
	public CurrentProfile setCoordSys(short coord_sys) {
		values.put("coord_sys", coord_sys);
		return this;
	}

	/**
	 *  @return Profile - message-list
	 */
	public java.util.Vector<CurrentProfileCell> getProfile() {
		try {
			return getMessageList("profile", CurrentProfileCell.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param profile Profile
	 */
	public CurrentProfile setProfile(java.util.Collection<CurrentProfileCell> profile) {
		values.put("profile", profile);
		return this;
	}

}
