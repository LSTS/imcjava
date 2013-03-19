/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2013, Laboratório de Sistemas e Tecnologia Subaquática
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
 * $Id:: ClassGenerator.java 393 2013-03-03 10:40:48Z zepinto@gmail.com        $:
 */

// Source generated by IMCJava from IMC version 5.1.0
package pt.up.fe.dceg.neptus.imc;

/**
 *  IMC Message Reference To Follow (479)<br/>
 */

public class Reference extends IMCMessage {

	public static final int ID_STATIC = 479;

	public static final short FLAG_LOCATION = 0x01;
	public static final short FLAG_SPEED = 0x02;
	public static final short FLAG_Z = 0x04;
	public static final short FLAG_MANDONE = 0x80;

	public Reference() {
		super(ID_STATIC);
	}

	public Reference(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Reference create(Object... values) {
		Reference m = new Reference();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Reference clone(IMCMessage msg) throws Exception {

		Reference m = new Reference();
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

	public Reference(short flags, DesiredSpeed speed, DesiredZ z, double lat, double lon) {
		super(ID_STATIC);
		setFlags(flags);
		if (speed != null)
			setSpeed(speed);
		if (z != null)
			setZ(z);
		setLat(lat);
		setLon(lon);
	}

	/**
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @return Speed Reference - message
	 */
	public DesiredSpeed getSpeed() {
		try {
			IMCMessage obj = getMessage("speed");
			if (obj instanceof DesiredSpeed)
				return (DesiredSpeed) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @return Z Reference - message
	 */
	public DesiredZ getZ() {
		try {
			IMCMessage obj = getMessage("z");
			if (obj instanceof DesiredZ)
				return (DesiredZ) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @return Latitude Reference - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @return Longitude Reference - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public void setFlags(short flags) {
		values.put("flags", flags);
	}

	/**
	 *  @param speed Speed Reference
	 */
	public void setSpeed(DesiredSpeed speed) {
		values.put("speed", speed);
	}

	/**
	 *  @param z Z Reference
	 */
	public void setZ(DesiredZ z) {
		values.put("z", z);
	}

	/**
	 *  @param lat Latitude Reference
	 */
	public void setLat(double lat) {
		values.put("lat", lat);
	}

	/**
	 *  @param lon Longitude Reference
	 */
	public void setLon(double lon) {
		values.put("lon", lon);
	}

}