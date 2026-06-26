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
 *  IMC Message BMS Data (2042)<br/>
 *  Battery management system data returned in response to a<br/>
 *  QueryBmsData request.  One message is sent per battery pack.<br/>
 *  Fields that are not applicable to the requested operation are<br/>
 *  set to zero.<br/>
 */

public class BmsData extends IMCMessage {

	public static final int BST_OCA = 0x8000;
	public static final int BST_TCA = 0x4000;
	public static final int BST_OTA = 0x1000;
	public static final int BST_TDA = 0x0800;
	public static final int BST_RCA = 0x0200;
	public static final int BST_RTA = 0x0100;
	public static final int BST_INIT = 0x0080;
	public static final int BST_DSG = 0x0040;
	public static final int BST_FC = 0x0020;
	public static final int BST_FD = 0x0010;
	public static final int BST_EC = 0x0007;

	public static final int FET_CHG = 0x0001;
	public static final int FET_PCHG = 0x0004;

	public static final long SS_OC = 0x00100000;
	public static final long SS_CTO = 0x00040000;
	public static final long SS_PTO = 0x00010000;
	public static final long SS_OCDL = 0x00004000;
	public static final long SS_OTF = 0x00002000;
	public static final long SS_AFE_OVRD = 0x00001000;
	public static final long SS_UTD = 0x00000800;
	public static final long SS_UTC = 0x00000400;
	public static final long SS_OTD = 0x00000200;
	public static final long SS_OTC = 0x00000100;
	public static final long SS_ASCDL = 0x00000080;
	public static final long SS_ASCD = 0x00000040;
	public static final long SS_AOLDL = 0x00000020;
	public static final long SS_AOLD = 0x00000010;
	public static final long SS_OCD = 0x00000008;
	public static final long SS_OCC = 0x00000004;
	public static final long SS_COV = 0x00000002;
	public static final long SS_CUV = 0x00000001;

	public static final long PF_DFW = 0x00020000;
	public static final long PF_IFC = 0x00010000;
	public static final long PF_SOTF = 0x00008000;
	public static final long PF_TS3 = 0x00004000;
	public static final long PF_TS2 = 0x00002000;
	public static final long PF_TS1 = 0x00001000;
	public static final long PF_AFE_XRDY = 0x00000800;
	public static final long PF_AFEC = 0x00000200;
	public static final long PF_AFER = 0x00000100;
	public static final long PF_DFETF = 0x00000080;
	public static final long PF_CFETF = 0x00000040;
	public static final long PF_VIMR = 0x00000020;
	public static final long PF_SOT = 0x00000010;
	public static final long PF_SOCD = 0x00000008;
	public static final long PF_SOCC = 0x00000004;
	public static final long PF_SOV = 0x00000002;
	public static final long PF_SUV = 0x00000001;

	public static final long OS_KEYIN = 0x80000000;
	public static final long OS_CB = 0x10000000;
	public static final long OS_SLPCC = 0x08000000;
	public static final long OS_SLPAD = 0x04000000;
	public static final long OS_SLEEPM = 0x00800000;
	public static final long OS_XL = 0x00400000;
	public static final long OS_CAL_OFFSET = 0x00200000;
	public static final long OS_CAL = 0x00100000;
	public static final long OS_AUTH = 0x00040000;
	public static final long OS_LED = 0x00020000;
	public static final long OS_SDM = 0x00010000;
	public static final long OS_SLEEP = 0x00008000;
	public static final long OS_XCHG = 0x00004000;
	public static final long OS_XDSG = 0x00002000;
	public static final long OS_PF = 0x00001000;
	public static final long OS_SAFE_MODE = 0x00000800;
	public static final long OS_SDV = 0x00000400;
	public static final long OS_SEC1 = 0x00000200;
	public static final long OS_SEC0 = 0x00000100;
	public static final long OS_SAFE = 0x00000020;
	public static final long OS_HCFET = 0x00000010;
	public static final long OS_PRES = 0x00000001;

	public static final int CS_VCT = 0x8000;
	public static final int CS_SU = 0x2000;
	public static final int CS_IN = 0x1000;
	public static final int CS_FCHG = 0x0200;
	public static final int CS_OT = 0x0010;
	public static final int CS_HT = 0x0008;
	public static final int CS_ST = 0x0004;
	public static final int CS_LT = 0x0002;
	public static final int CS_UT = 0x0001;

	public static final int GS_VDQ = 0x8000;
	public static final int GS_EDV2 = 0x4000;
	public static final int GS_EDV1 = 0x2000;
	public static final int GS_FCCX = 0x0400;
	public static final int GS_REST = 0x0100;
	public static final int GS_CF = 0x0080;
	public static final int GS_EDV0 = 0x0020;
	public static final int GS_BAL_OK = 0x0010;
	public static final int GS_TC = 0x0008;
	public static final int GS_TD = 0x0004;

	public enum REQ_STATUS {
		SUCCESS(0),
		FAILURE(1),
		NOT_SUPPORTED(2),
		INVALID_PACK(3);

		protected long value;

		public long value() {
			return value;
		}

		REQ_STATUS(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 2042;

	public BmsData() {
		super(ID_STATIC);
	}

	public BmsData(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BmsData(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static BmsData create(Object... values) {
		BmsData m = new BmsData();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static BmsData clone(IMCMessage msg) throws Exception {

		BmsData m = new BmsData();
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

	public BmsData(IMCMessage original, REQ_STATUS req_status, short pack_idx, float temperature, float voltage, float current, short rsoc, short asoc, short soh, int remaining_capacity, int full_charge_capacity, int cycle_count, int time_to_empty, int time_to_full, int battery_status, int serial_number, int fet_status, long safety_status, long pf_status, long operation_status, int charging_status, int gauging_status, java.util.Collection<BmsCellVoltage> cell_voltages, java.util.Collection<BmsRegister> registers, byte[] data) {
		super(ID_STATIC);
		if (original != null)
			setOriginal(original);
		setReqStatus(req_status);
		setPackIdx(pack_idx);
		setTemperature(temperature);
		setVoltage(voltage);
		setCurrent(current);
		setRsoc(rsoc);
		setAsoc(asoc);
		setSoh(soh);
		setRemainingCapacity(remaining_capacity);
		setFullChargeCapacity(full_charge_capacity);
		setCycleCount(cycle_count);
		setTimeToEmpty(time_to_empty);
		setTimeToFull(time_to_full);
		setBatteryStatus(battery_status);
		setSerialNumber(serial_number);
		setFetStatus(fet_status);
		setSafetyStatus(safety_status);
		setPfStatus(pf_status);
		setOperationStatus(operation_status);
		setChargingStatus(charging_status);
		setGaugingStatus(gauging_status);
		if (cell_voltages != null)
			setCellVoltages(cell_voltages);
		if (registers != null)
			setRegisters(registers);
		if (data != null)
			setData(data);
	}

	/**
	 *  @return Original Message - message
	 */
	public IMCMessage getOriginal() {
		return getMessage("original");
	}

	public <T extends IMCMessage> T getOriginal(Class<T> clazz) throws Exception {
		return getMessage(clazz, "original");
	}

	/**
	 *  @param original Original Message
	 */
	public BmsData setOriginal(IMCMessage original) {
		values.put("original", original);
		return this;
	}

	/**
	 *  @return Request Status (enumerated) - uint8_t
	 */
	public REQ_STATUS getReqStatus() {
		try {
			REQ_STATUS o = REQ_STATUS.valueOf(getMessageType().getFieldPossibleValues("req_status").get(getLong("req_status")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getReqStatusStr() {
		return getString("req_status");
	}

	public short getReqStatusVal() {
		return (short) getInteger("req_status");
	}

	/**
	 *  @param req_status Request Status (enumerated)
	 */
	public BmsData setReqStatus(REQ_STATUS req_status) {
		values.put("req_status", req_status.value());
		return this;
	}

	/**
	 *  @param req_status Request Status (as a String)
	 */
	public BmsData setReqStatusStr(String req_status) {
		setValue("req_status", req_status);
		return this;
	}

	/**
	 *  @param req_status Request Status (integer value)
	 */
	public BmsData setReqStatusVal(short req_status) {
		setValue("req_status", req_status);
		return this;
	}

	/**
	 *  @return Pack Index - uint8_t
	 */
	public short getPackIdx() {
		return (short) getInteger("pack_idx");
	}

	/**
	 *  @param pack_idx Pack Index
	 */
	public BmsData setPackIdx(short pack_idx) {
		values.put("pack_idx", pack_idx);
		return this;
	}

	/**
	 *  @return Temperature (°C) - fp32_t
	 */
	public double getTemperature() {
		return getDouble("temperature");
	}

	/**
	 *  @param temperature Temperature (°C)
	 */
	public BmsData setTemperature(double temperature) {
		values.put("temperature", temperature);
		return this;
	}

	/**
	 *  @return Voltage (V) - fp32_t
	 */
	public double getVoltage() {
		return getDouble("voltage");
	}

	/**
	 *  @param voltage Voltage (V)
	 */
	public BmsData setVoltage(double voltage) {
		values.put("voltage", voltage);
		return this;
	}

	/**
	 *  @return Current (A) - fp32_t
	 */
	public double getCurrent() {
		return getDouble("current");
	}

	/**
	 *  @param current Current (A)
	 */
	public BmsData setCurrent(double current) {
		values.put("current", current);
		return this;
	}

	/**
	 *  @return Relative State of Charge (%) - uint8_t
	 */
	public short getRsoc() {
		return (short) getInteger("rsoc");
	}

	/**
	 *  @param rsoc Relative State of Charge (%)
	 */
	public BmsData setRsoc(short rsoc) {
		values.put("rsoc", rsoc);
		return this;
	}

	/**
	 *  @return Absolute State of Charge (%) - uint8_t
	 */
	public short getAsoc() {
		return (short) getInteger("asoc");
	}

	/**
	 *  @param asoc Absolute State of Charge (%)
	 */
	public BmsData setAsoc(short asoc) {
		values.put("asoc", asoc);
		return this;
	}

	/**
	 *  @return State of Health (%) - uint8_t
	 */
	public short getSoh() {
		return (short) getInteger("soh");
	}

	/**
	 *  @param soh State of Health (%)
	 */
	public BmsData setSoh(short soh) {
		values.put("soh", soh);
		return this;
	}

	/**
	 *  @return Remaining Capacity (mAh) - uint16_t
	 */
	public int getRemainingCapacity() {
		return getInteger("remaining_capacity");
	}

	/**
	 *  @param remaining_capacity Remaining Capacity (mAh)
	 */
	public BmsData setRemainingCapacity(int remaining_capacity) {
		values.put("remaining_capacity", remaining_capacity);
		return this;
	}

	/**
	 *  @return Full Charge Capacity (mAh) - uint16_t
	 */
	public int getFullChargeCapacity() {
		return getInteger("full_charge_capacity");
	}

	/**
	 *  @param full_charge_capacity Full Charge Capacity (mAh)
	 */
	public BmsData setFullChargeCapacity(int full_charge_capacity) {
		values.put("full_charge_capacity", full_charge_capacity);
		return this;
	}

	/**
	 *  @return Cycle Count - uint16_t
	 */
	public int getCycleCount() {
		return getInteger("cycle_count");
	}

	/**
	 *  @param cycle_count Cycle Count
	 */
	public BmsData setCycleCount(int cycle_count) {
		values.put("cycle_count", cycle_count);
		return this;
	}

	/**
	 *  @return Time to Empty (min) - uint16_t
	 */
	public int getTimeToEmpty() {
		return getInteger("time_to_empty");
	}

	/**
	 *  @param time_to_empty Time to Empty (min)
	 */
	public BmsData setTimeToEmpty(int time_to_empty) {
		values.put("time_to_empty", time_to_empty);
		return this;
	}

	/**
	 *  @return Time to Full (min) - uint16_t
	 */
	public int getTimeToFull() {
		return getInteger("time_to_full");
	}

	/**
	 *  @param time_to_full Time to Full (min)
	 */
	public BmsData setTimeToFull(int time_to_full) {
		values.put("time_to_full", time_to_full);
		return this;
	}

	/**
	 *  @return Battery Status (bitfield) - uint16_t
	 */
	public int getBatteryStatus() {
		return getInteger("battery_status");
	}

	/**
	 *  @param battery_status Battery Status (bitfield)
	 */
	public BmsData setBatteryStatus(int battery_status) {
		values.put("battery_status", battery_status);
		return this;
	}

	/**
	 *  @return Serial Number - uint16_t
	 */
	public int getSerialNumber() {
		return getInteger("serial_number");
	}

	/**
	 *  @param serial_number Serial Number
	 */
	public BmsData setSerialNumber(int serial_number) {
		values.put("serial_number", serial_number);
		return this;
	}

	/**
	 *  @return FET Status (bitfield) - uint16_t
	 */
	public int getFetStatus() {
		return getInteger("fet_status");
	}

	/**
	 *  @param fet_status FET Status (bitfield)
	 */
	public BmsData setFetStatus(int fet_status) {
		values.put("fet_status", fet_status);
		return this;
	}

	/**
	 *  @return Safety Status (bitfield) - uint32_t
	 */
	public long getSafetyStatus() {
		return getLong("safety_status");
	}

	/**
	 *  @param safety_status Safety Status (bitfield)
	 */
	public BmsData setSafetyStatus(long safety_status) {
		values.put("safety_status", safety_status);
		return this;
	}

	/**
	 *  @return PF Status (bitfield) - uint32_t
	 */
	public long getPfStatus() {
		return getLong("pf_status");
	}

	/**
	 *  @param pf_status PF Status (bitfield)
	 */
	public BmsData setPfStatus(long pf_status) {
		values.put("pf_status", pf_status);
		return this;
	}

	/**
	 *  @return Operation Status (bitfield) - uint32_t
	 */
	public long getOperationStatus() {
		return getLong("operation_status");
	}

	/**
	 *  @param operation_status Operation Status (bitfield)
	 */
	public BmsData setOperationStatus(long operation_status) {
		values.put("operation_status", operation_status);
		return this;
	}

	/**
	 *  @return Charging Status (bitfield) - uint16_t
	 */
	public int getChargingStatus() {
		return getInteger("charging_status");
	}

	/**
	 *  @param charging_status Charging Status (bitfield)
	 */
	public BmsData setChargingStatus(int charging_status) {
		values.put("charging_status", charging_status);
		return this;
	}

	/**
	 *  @return Gauging Status (bitfield) - uint16_t
	 */
	public int getGaugingStatus() {
		return getInteger("gauging_status");
	}

	/**
	 *  @param gauging_status Gauging Status (bitfield)
	 */
	public BmsData setGaugingStatus(int gauging_status) {
		values.put("gauging_status", gauging_status);
		return this;
	}

	/**
	 *  @return Cell Voltages - message-list
	 */
	public java.util.Vector<BmsCellVoltage> getCellVoltages() {
		try {
			return getMessageList("cell_voltages", BmsCellVoltage.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param cell_voltages Cell Voltages
	 */
	public BmsData setCellVoltages(java.util.Collection<BmsCellVoltage> cell_voltages) {
		values.put("cell_voltages", cell_voltages);
		return this;
	}

	/**
	 *  @return Registers - message-list
	 */
	public java.util.Vector<BmsRegister> getRegisters() {
		try {
			return getMessageList("registers", BmsRegister.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param registers Registers
	 */
	public BmsData setRegisters(java.util.Collection<BmsRegister> registers) {
		values.put("registers", registers);
		return this;
	}

	/**
	 *  @return Data - rawdata
	 */
	public byte[] getData() {
		return getRawData("data");
	}

	/**
	 *  @param data Data
	 */
	public BmsData setData(byte[] data) {
		values.put("data", data);
		return this;
	}

}
