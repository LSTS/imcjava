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
 *  IMC Message Vehicle Depot (913)<br/>
 */

public class VehicleDepot extends IMCMessage {

	public static final int ID_STATIC = 913;

	public VehicleDepot() {
		super(ID_STATIC);
	}

	public VehicleDepot(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public VehicleDepot(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static VehicleDepot create(Object... values) {
		VehicleDepot m = new VehicleDepot();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static VehicleDepot clone(IMCMessage msg) throws Exception {

		VehicleDepot m = new VehicleDepot();
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

	public VehicleDepot(int vehicle, double lat, double lon, double deadline) {
		super(ID_STATIC);
		setVehicle(vehicle);
		setLat(lat);
		setLon(lon);
		setDeadline(deadline);
	}

	/**
	 *  @return vehicle - uint16_t
	 */
	public int getVehicle() {
		return getInteger("vehicle");
	}

	/**
	 *  @param vehicle vehicle
	 */
	public VehicleDepot setVehicle(int vehicle) {
		values.put("vehicle", vehicle);
		return this;
	}

	/**
	 *  @return Latitude (rad) - fp64_t
	 */
	public double getLat() {
		return getDouble("lat");
	}

	/**
	 *  @param lat Latitude (rad)
	 */
	public VehicleDepot setLat(double lat) {
		values.put("lat", lat);
		return this;
	}

	/**
	 *  @return Longitude (rad) - fp64_t
	 */
	public double getLon() {
		return getDouble("lon");
	}

	/**
	 *  @param lon Longitude (rad)
	 */
	public VehicleDepot setLon(double lon) {
		values.put("lon", lon);
		return this;
	}

	/**
	 *  @return Deadline (s) - fp64_t
	 */
	public double getDeadline() {
		return getDouble("deadline");
	}

	/**
	 *  @param deadline Deadline (s)
	 */
	public VehicleDepot setDeadline(double deadline) {
		values.put("deadline", deadline);
		return this;
	}

}
