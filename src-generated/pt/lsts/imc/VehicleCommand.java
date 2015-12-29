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
 *  IMC Message Vehicle Command (501)<br/>
 *  Vehicle command.<br/>
 */

public class VehicleCommand extends IMCMessage {

	public enum TYPE {
		REQUEST(0),
		SUCCESS(1),
		IN_PROGRESS(2),
		FAILURE(3);

		protected long value;

		public long value() {
			return value;
		}

		TYPE(long value) {
			this.value = value;
		}
	}

	public enum COMMAND {
		EXEC_MANEUVER(0),
		STOP_MANEUVER(1),
		START_CALIBRATION(2),
		STOP_CALIBRATION(3);

		protected long value;

		public long value() {
			return value;
		}

		COMMAND(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 501;

	public VehicleCommand() {
		super(ID_STATIC);
	}

	public VehicleCommand(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VehicleCommand(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VehicleCommand create(Object... values) {
		VehicleCommand m = new VehicleCommand();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VehicleCommand clone(IMCMessage msg) throws Exception {

		VehicleCommand m = new VehicleCommand();
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

	public VehicleCommand(TYPE type, int request_id, COMMAND command, Maneuver maneuver, int calib_time, String info) {
		super(ID_STATIC);
		setType(type);
		setRequestId(request_id);
		setCommand(command);
		if (maneuver != null)
			setManeuver(maneuver);
		setCalibTime(calib_time);
		if (info != null)
			setInfo(info);
	}

	/**
	 *  @return Type (enumerated) - uint8_t
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
	 *  @param type Type (enumerated)
	 */
	public VehicleCommand setType(TYPE type) {
		values.put("type", type.value());
		return this;
	}

	/**
	 *  @param type Type (as a String)
	 */
	public VehicleCommand setTypeStr(String type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @param type Type (integer value)
	 */
	public VehicleCommand setTypeVal(short type) {
		setValue("type", type);
		return this;
	}

	/**
	 *  @return Request ID - uint16_t
	 */
	public int getRequestId() {
		return getInteger("request_id");
	}

	/**
	 *  @param request_id Request ID
	 */
	public VehicleCommand setRequestId(int request_id) {
		values.put("request_id", request_id);
		return this;
	}

	/**
	 *  @return Command (enumerated) - uint8_t
	 */
	public COMMAND getCommand() {
		try {
			COMMAND o = COMMAND.valueOf(getMessageType().getFieldPossibleValues("command").get(getLong("command")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getCommandStr() {
		return getString("command");
	}

	public short getCommandVal() {
		return (short) getInteger("command");
	}

	/**
	 *  @param command Command (enumerated)
	 */
	public VehicleCommand setCommand(COMMAND command) {
		values.put("command", command.value());
		return this;
	}

	/**
	 *  @param command Command (as a String)
	 */
	public VehicleCommand setCommandStr(String command) {
		setValue("command", command);
		return this;
	}

	/**
	 *  @param command Command (integer value)
	 */
	public VehicleCommand setCommandVal(short command) {
		setValue("command", command);
		return this;
	}

	/**
	 *  @return Maneuver - message
	 */
	public Maneuver getManeuver() {
		try {
			IMCMessage obj = getMessage("maneuver");
			if (obj instanceof Maneuver)
				return (Maneuver) obj;
			else
				return null;
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param maneuver Maneuver
	 */
	public VehicleCommand setManeuver(Maneuver maneuver) {
		values.put("maneuver", maneuver);
		return this;
	}

	/**
	 *  @return Calibration Time (s) - uint16_t
	 */
	public int getCalibTime() {
		return getInteger("calib_time");
	}

	/**
	 *  @param calib_time Calibration Time (s)
	 */
	public VehicleCommand setCalibTime(int calib_time) {
		values.put("calib_time", calib_time);
		return this;
	}

	/**
	 *  @return Info - plaintext
	 */
	public String getInfo() {
		return getString("info");
	}

	/**
	 *  @param info Info
	 */
	public VehicleCommand setInfo(String info) {
		values.put("info", info);
		return this;
	}

}
