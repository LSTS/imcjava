/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.lsf.batch;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import pt.lsts.imc.EntityList;
import pt.lsts.imc.EntityList.OP;
import pt.lsts.imc.GpsFix;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.Voltage;
import pt.lsts.imc.net.Consume;

/**
 * @author zp
 *
 */
public class DmsSnorter {
	
	int vtol_id = 3114;
	
	LinkedHashMap<Integer, Integer> channels = new LinkedHashMap<>();
	LinkedHashMap<Integer, Double> values = new LinkedHashMap<>();
		
	@Consume
	public void on(EntityList entities) {
		if (entities.getSrc() != vtol_id)
			return;
		
		if (entities.getOp() == OP.REPORT) {
			for (Entry<String,String> ent : entities.getList().entrySet()) {
				if (ent.getKey().startsWith("DMS CH")) {
					int ch = Integer.parseInt(ent.getKey().substring(6));
					channels.put(Integer.parseInt(ent.getValue()), ch);
				}
			}			
		}
	}
	
	@Consume
	public void on(Voltage voltage) {
		if (voltage.getSrc() != vtol_id)
			return;
		
		if (!channels.containsKey((int)voltage.getSrcEnt()))
			return;
		values.put(channels.get((int)voltage.getSrcEnt()), voltage.getValue());
	}
	
	@Consume
	public void on(GpsFix fix) {
		if (fix.getSrc() != vtol_id)
			return;
		if (channels.isEmpty())
			return;
		/*
		if (fix.getDate().before(start))
			return;
		
		if (fix.getDate().after(end))
			return;
		*/
		System.out.print(String.format(fix.getDate()+", %.3f, %.7f, %.7f", fix.getTimestamp(), Math.toDegrees(fix.getLat()),  Math.toDegrees(fix.getLon())));
	
		for (int i = 1; i <= 16; i++)
			System.out.print(", "+values.get(i));
		
		System.out.println();
		values.clear();
	}	
	
	@Consume
	public void on(IMCMessage msg) {
		//System.out.println(msg);
	}
	
	public static void main(String[] args) {
		
		LsfBatch batch = LsfBatch.selectFolders();
		System.out.print("Date, Timestamp, Latitude, Longitude");
		
		for (int i = 1; i <= 16; i++) {
			System.out.print(", "+i);
		}
		
		System.out.println();
		batch.process(new DmsSnorter());
	}
}
