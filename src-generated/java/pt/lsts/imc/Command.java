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
 *  IMC Message Command To Follow (497)<br/>
 *  This message must be sent by an external entity to provide command references to a system<br/>
 *  running a "Follow Command Maneuver". If no Command messages are transmitted, the system<br/>
 *  will terminate maneuver.<br/>
 */

public class Command extends IMCMessage {

	public static final short FLAG_SPEED_METERS_PS = 0x01;
	public static final short FLAG_SPEED_RPM = 0x02;
	public static final short FLAG_DEPTH = 0x04;
	public static final short FLAG_ALTITUDE = 0x08;
	public static final short FLAG_HEADING = 0x10;
	public static final short FLAG_HEADING_RATE = 0x20;
	public static final short FLAG_MANDONE = 0x80;

	public static final int ID_STATIC = 497;

	public Command() {
		super(ID_STATIC);
	}

	public Command(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Command(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Command create(Object... values) {
		Command m = new Command();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Command clone(IMCMessage msg) throws Exception {

		Command m = new Command();
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

	public Command(short flags, float speed, float z, float heading) {
		super(ID_STATIC);
		setFlags(flags);
		setSpeed(speed);
		setZ(z);
		setHeading(heading);
	}

	/**
	 *  @return Flags (bitfield) - uint8_t
	 */
	public short getFlags() {
		return (short) getInteger("flags");
	}

	/**
	 *  @param flags Flags (bitfield)
	 */
	public Command setFlags(short flags) {
		values.put("flags", flags);
		return this;
	}

	/**
	 *  @return Speed Reference - fp32_t
	 */
	public double getSpeed() {
		return getDouble("speed");
	}

	/**
	 *  @param speed Speed Reference
	 */
	public Command setSpeed(double speed) {
		values.put("speed", speed);
		return this;
	}

	/**
	 *  @return Z Reference (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z Reference (m)
	 */
	public Command setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Heading Reference (rad) - fp32_t
	 */
	public double getHeading() {
		return getDouble("heading");
	}

	/**
	 *  @param heading Heading Reference (rad)
	 */
	public Command setHeading(double heading) {
		values.put("heading", heading);
		return this;
	}

}
