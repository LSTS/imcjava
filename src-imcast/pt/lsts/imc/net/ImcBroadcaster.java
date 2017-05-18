/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2017, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.net;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import pt.lsts.imc.Announce;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.def.SystemType;

/**
 * This class listens for incoming JSON packets containing system positions and
 * rebroadcasts them as IMC
 * 
 * @author zp
 */
public class ImcBroadcaster extends Thread {

	private static final String multicastAddress = "224.0.75.69";
	private DatagramSocket receiveSock;
	private Gson gson = new Gson();
	
	public ImcBroadcaster(int port) throws Exception {
		receiveSock = new DatagramSocket(port);
	}

	@Override
	public void run() {
		super.run();
		while (true) {
			DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
			try {
				receiveSock.receive(packet);
				incoming(packet.getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void incoming(byte[] data) {
		JsonReader reader = new JsonReader(new StringReader(new String(data)));
		reader.setLenient(true);
		AnnounceJson ann = gson.fromJson(reader, AnnounceJson.class);	
		
		Announce announce = new Announce();
		announce.setSysName(ann.sys_name);
		announce.setLat(Math.toRadians(ann.latitude));
		announce.setLon(Math.toRadians(ann.longitude));
		announce.setHeight(ann.height);
		String type = ann.sys_type.toUpperCase();
		announce.setSysType(SystemType.UUV);
		int src = ann.sys_name.hashCode()%255;
		
		if (type.equals("UAV") || type.equals("UAS")) {
			announce.setSysType(SystemType.UAV);
			src |= (44 << 8);
		}
		else if (type.equals("ASV") || type.equals("USV")) {
			announce.setSysType(SystemType.USV);
			src |= (40 << 8);
		}
		else if (type.equals("ROV")) {
			src |= (36 << 8);
		}
		else if (type.equals("CCU") || type.equals("GCS")) {
			announce.setSysType(SystemType.CCU);
			src |= (64 << 8);
		}
		else {
			src |= (16 << 8);
		}
		announce.setSrc(src);
		try {
			broadcast(announce);	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void broadcast(Announce announce) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IMCOutputStream ios = new IMCOutputStream(baos);
		ios.writeMessage(announce);
		ios.close();
		byte[] data = baos.toByteArray();
		DatagramSocket datagramSocket = new DatagramSocket();
		for (int port = 30100; port < 30105; port++) {
			InetSocketAddress broadcast = new InetSocketAddress("255.255.255.255", port);
			InetSocketAddress multicast = new InetSocketAddress(multicastAddress, port);
			datagramSocket.send(new DatagramPacket(data, data.length, broadcast));
			datagramSocket.send(new DatagramPacket(data, data.length, multicast));			
		}
		System.out.println(announce.asJSON());
		datagramSocket.close();
	}

	public static class AnnounceJson {
		String sys_name = "";
		double latitude = 0;
		double longitude = 0;
		double height = 0;
		String sys_type = "";
	}

	public static void main(String[] args) throws Exception {
		int port = 3066;
		if (args.length == 1) {
			port = Integer.valueOf(args[0]);
			System.out.println("Listening for positions on port "+port);
			new ImcBroadcaster(port).start();
		}
		else {
			System.err.println("Usage: ./imcast <BIND_PORT>");
			System.exit(1);
		}
	}
}
