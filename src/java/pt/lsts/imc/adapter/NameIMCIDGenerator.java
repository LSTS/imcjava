/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
 */
package pt.lsts.imc.adapter;

/**
 * @author pdias
 *
 */
public class NameIMCIDGenerator {
	public static void main(String[] args) {
		if (args.length == 0) {
			String[] args2 = {"iver2", "auv",
					"auv", "auv",
					"asv", "asv",
					"rov", "rov",
					"uav", "uav",
					"ccu", "ccu"};
			for (int i = 0; i < args2.length; i = i + 2) {
				getId(args2[i], args2[i + 1], false);
			}
		}
		else {
			String type = args.length > 0 ? args[1] : "";
			getId(args[0], type, false);
		}
	}
	
	public static int getId(String name, String type) {
		return getId(name, type, false);
	}
	
	public static int getId(String name, String type, boolean lstsOwned) {
		int src = name.hashCode() % 0xFFFF;
		src &= 0x1FFF; // clean first 3 bytes selector
		if (type.equalsIgnoreCase("UAV") || type.equalsIgnoreCase("UAS")) {
			src &= (0x03FF);
			src |= lstsOwned ? (0x00 << 1 << 12) : (0x01 << 1 << 12); // vehicle
			src |= (0x3 << 2 << 8);
		}
		else if (type.equalsIgnoreCase("ASV") || type.equalsIgnoreCase("USV")) {
			src &= (0x03FF);
			src |= lstsOwned ? (0x00 << 1 << 12) : (0x01 << 1 << 12); // vehicle
			src |= (0x2 << 2 << 8);
		}
		else if (type.equalsIgnoreCase("ROV")) {
			src &= (0x03FF);
			src |= lstsOwned ? (0x00 << 1 << 12) : (0x01 << 1 << 12); // vehicle
			src |= (0x1 << 2 << 8);
		}
		else if (type.equalsIgnoreCase("CCU") || type.equalsIgnoreCase("GCS")) {
			src |= (0x40 << 8);
		}
		else if (type.equalsIgnoreCase("Sensor")) {
			src &= (0x9FFF);
			src |= lstsOwned ? (0x80 << 8) : (0x90 << 8);
		}
		else { // AUV
			src &= (0x03FF);
			src |= lstsOwned ? (0x00 << 1 << 12) : (0x01 << 1 << 12); // vehicle
			src |= (0x0 << 2 << 8);
		}
		
		System.out.println(String.format("IMC Id for %s is %s type %s", name, Integer.toHexString(src), getType(src)));
		return src;
	}

	/**
	 * @param id
	 * @return
	 */
	public static String getType(int id) {
//		System.out.println(src >> (16-3));
		if (((id >> (16-3)) ^ 0b000) == 0) {
			return "lsts vehicle " + getVehicleTypeWorker((id & (0b00011100 << 8)) >> 10);
		}
		else if (((id >> (16-3)) ^ 0b001) == 0) {
			return "other vehicle " + getVehicleTypeWorker((id & (0b00011100 << 8)) >> 10);
		}
		else if (((id >> (16-3)) ^ 0b010) == 0) {
			return "CCU";
		}
		else if (((id >> (16-4)) ^ 0b0110) == 0) {
			return "lsts payload sensor";
		}
		else if (((id >> (16-4)) ^ 0b0111) == 0) {
			return "other payload sensor";
		}
		else if (((id >> (16-4)) ^ 0b1000) == 0) {
			return "lsts sensor";
		}
		else if (((id >> (16-4)) ^ 0b1001) == 0) {
			return "other sensor";
		}

		return null;
	}

	public static String getVehicleType(int id) {
		if (((id >> (16-3)) ^ 0b000) == 0 || ((id >> (16-3)) ^ 0b001) == 0) {
			return getVehicleTypeWorker((id & (0b00011100 << 8)) >> 10);
		}
		
		return null;
	}

	/**
	 * @param i
	 * @return
	 */
	private static String getVehicleTypeWorker(int i) {
//		System.out.println(i);
		switch (i) {
			case 0b000:
				return "AUV";
			case 0b001:
				return "ROV";
			case 0b010:
				return "ASV";
			case 0b011:
				return "UAV";
			default:
				return "unknown";
		}
	}

}
