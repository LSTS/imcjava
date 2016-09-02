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
 *  IMC Message Follow Point Maneuver (494)<br/>
 *  This maneuver behaves by following a point.<br/>
 */

@SuppressWarnings("unchecked")
public class FollowPoint extends Maneuver {

	public enum SPEED_UNITS {
		METERS_PS(0),
		RPM(1),
		PERCENTAGE(2);

		protected long value;

		public long value() {
			return value;
		}

		SPEED_UNITS(long value) {
			this.value = value;
		}
	}

	public enum Z_UNITS {
		NONE(0),
		DEPTH(1),
		ALTITUDE(2),
		HEIGHT(3);

		protected long value;

		public long value() {
			return value;
		}

		Z_UNITS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 494;

	public FollowPoint() {
		super(ID_STATIC);
	}

	public FollowPoint(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FollowPoint(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static FollowPoint create(Object... values) {
		FollowPoint m = new FollowPoint();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static FollowPoint clone(IMCMessage msg) throws Exception {

		FollowPoint m = new FollowPoint();
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

	public FollowPoint(int target, float max_speed, SPEED_UNITS speed_units, float z, Z_UNITS z_units) {
		super(ID_STATIC);
		setTarget(target);
		setMaxSpeed(max_speed);
		setSpeedUnits(speed_units);
		setZ(z);
		setZUnits(z_units);
	}

	/**
	 *  @return Id To Follow - uint16_t
	 */
	public int getTarget() {
		return getInteger("target");
	}

	/**
	 *  @param target Id To Follow
	 */
	public FollowPoint setTarget(int target) {
		values.put("target", target);
		return this;
	}

	/**
	 *  @return Maximum Speed (m/s) - fp32_t
	 */
	public double getMaxSpeed() {
		return getDouble("max_speed");
	}

	/**
	 *  @param max_speed Maximum Speed (m/s)
	 */
	public FollowPoint setMaxSpeed(double max_speed) {
		values.put("max_speed", max_speed);
		return this;
	}

	/**
	 *  @return Speed Units (enumerated) - uint8_t
	 */
	public SPEED_UNITS getSpeedUnits() {
		try {
			SPEED_UNITS o = SPEED_UNITS.valueOf(getMessageType().getFieldPossibleValues("speed_units").get(getLong("speed_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSpeedUnitsStr() {
		return getString("speed_units");
	}

	public short getSpeedUnitsVal() {
		return (short) getInteger("speed_units");
	}

	/**
	 *  @param speed_units Speed Units (enumerated)
	 */
	public FollowPoint setSpeedUnits(SPEED_UNITS speed_units) {
		values.put("speed_units", speed_units.value());
		return this;
	}

	/**
	 *  @param speed_units Speed Units (as a String)
	 */
	public FollowPoint setSpeedUnitsStr(String speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @param speed_units Speed Units (integer value)
	 */
	public FollowPoint setSpeedUnitsVal(short speed_units) {
		setValue("speed_units", speed_units);
		return this;
	}

	/**
	 *  @return Z (m) - fp32_t
	 */
	public double getZ() {
		return getDouble("z");
	}

	/**
	 *  @param z Z (m)
	 */
	public FollowPoint setZ(double z) {
		values.put("z", z);
		return this;
	}

	/**
	 *  @return Z Units (enumerated) - uint8_t
	 */
	public Z_UNITS getZUnits() {
		try {
			Z_UNITS o = Z_UNITS.valueOf(getMessageType().getFieldPossibleValues("z_units").get(getLong("z_units")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getZUnitsStr() {
		return getString("z_units");
	}

	public short getZUnitsVal() {
		return (short) getInteger("z_units");
	}

	/**
	 *  @param z_units Z Units (enumerated)
	 */
	public FollowPoint setZUnits(Z_UNITS z_units) {
		values.put("z_units", z_units.value());
		return this;
	}

	/**
	 *  @param z_units Z Units (as a String)
	 */
	public FollowPoint setZUnitsStr(String z_units) {
		setValue("z_units", z_units);
		return this;
	}

	/**
	 *  @param z_units Z Units (integer value)
	 */
	public FollowPoint setZUnitsVal(short z_units) {
		setValue("z_units", z_units);
		return this;
	}

}
