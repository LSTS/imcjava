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
 *  IMC Message Announce (151)<br/>
 *  A system description that is to be broadcasted to other systems.<br/>
 */

public class Announce extends IMCMessage {

	public enum SYS_TYPE {
		CCU(0),
		HUMANSENSOR(1),
		UUV(2),
		USV(3),
		UAV(4),
		UGV(5),
		STATICSENSOR(6),
		MOBILESENSOR(7),
		WSN(8);

		protected long value;

		public long value() {
			return value;
		}

		SYS_TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 151;

	public Announce() {
		super(ID_STATIC);
	}

	public Announce(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Announce(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static Announce create(Object... values) {
		Announce m = new Announce();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static Announce clone(IMCMessage msg) throws Exception {

		Announce m = new Announce();
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

	public Announce(String sys_name, SYS_TYPE sys_type, int owner, double lat, double lon, float height, String services) {
		super(ID_STATIC);
		if (sys_name != null)
			setSysName(sys_name);
		setSysType(sys_type);
		setOwner(owner);
		setLat(lat);
		setLon(lon);
		setHeight(height);
		if (services != null)
			setServices(services);
	}

	/**
	 *  @return System Name - plaintext
	 */
	public String getSysName() {
		return getString("sys_name");
	}

	/**
	 *  @param sys_name System Name
	 */
	public Announce setSysName(String sys_name) {
		values.put("sys_name", sys_name);
		return this;
	}

	/**
	 *  @return System Type (enumerated) - uint8_t
	 */
	public SYS_TYPE getSysType() {
		try {
			SYS_TYPE o = SYS_TYPE.valueOf(getMessageType().getFieldPossibleValues("sys_type").get(getLong("sys_type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getSysTypeStr() {
		return getString("sys_type");
	}

	public short getSysTypeVal() {
		return (short) getInteger("sys_type");
	}

	/**
	 *  @param sys_type System Type (enumerated)
	 */
	public Announce setSysType(SYS_TYPE sys_type) {
		values.put("sys_type", sys_type.value());
		return this;
	}

	/**
	 *  @param sys_type System Type (as a String)
	 */
	public Announce setSysTypeStr(String sys_type) {
		setValue("sys_type", sys_type);
		return this;
	}

	/**
	 *  @param sys_type System Type (integer value)
	 */
	public Announce setSysTypeVal(short sys_type) {
		setValue("sys_type", sys_type);
		return this;
	}

	/**
	 *  @return Control Owner - uint16_t
	 */
	public int getOwner() {
		return getInteger("owner");
	}

	/**
	 *  @param owner Control Owner
	 */
	public Announce setOwner(int owner) {
		values.put("owner", owner);
		return this;
	}

	/**
	 *  @return Latitude WGS-84 (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude WGS-84 (rad)
	 */
	public Announce setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude WGS-84 (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude WGS-84 (rad)
	 */
	public Announce setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Height WGS-84 (m) - fp32_t
	 */
	public double getHeight() {
		return getDouble("height");
	}

	/**
	 *  @param height Height WGS-84 (m)
	 */
	public Announce setHeight(double height) {
		values.put("height", height);
		return this;
	}

	/**
	 *  @return Services - plaintext
	 */
	public String getServices() {
		return getString("services");
	}

	/**
	 *  @param services Services
	 */
	public Announce setServices(String services) {
		values.put("services", services);
		return this;
	}

}
