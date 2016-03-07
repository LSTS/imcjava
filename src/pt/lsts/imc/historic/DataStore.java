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
 */
package pt.lsts.imc.historic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Random;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.HistoricData;
import pt.lsts.imc.HistoricSample;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.LogBookEntry.TYPE;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class DataStore {

	public final int HISTORIC_DATA_BASE_SIZE = 14 + IMCDefinition.getInstance().headerLength();
	public static final int HISTORIC_SAMPLE_BASE_SIZE = 15;

	private PriorityQueue<DataSample> history = new PriorityQueue<DataSample>(Collections.reverseOrder());

	public void addData(HistoricData data) {
		for (DataSample sample : DataSample.parse(data))
			addSample(sample);		
	}

	public void addSample(DataSample sample) {
		synchronized (history) {
			history.add(sample);	
		}
	}

	public HistoricData pollData(int destination, int size) throws Exception {

		HistoricData ret = new HistoricData();
		size -= HISTORIC_DATA_BASE_SIZE ;

		ArrayList<DataSample> samples = new ArrayList<DataSample>();
		ArrayList<DataSample> rejected = new ArrayList<DataSample>();
		synchronized (history) {
			while (size > HISTORIC_SAMPLE_BASE_SIZE) {
				DataSample sample = history.poll();
				if (sample == null)
					break;
				if (sample.getSerializationSize() > size)
					rejected.add(sample);
				else {
					samples.add(sample);
					size -= sample.getSerializationSize();
				}
			}
			history.addAll(rejected);
		}

		if (samples.isEmpty())
			throw new Exception("No data to be transmitted");
		
		double baseLat = samples.get(0).getLatDegs();
		double baseLon = samples.get(0).getLonDegs();
		long baseTime = samples.get(0).getTimestampMillis();
		
		for (DataSample sample : samples) {
			if (sample.getTimestampMillis() < baseTime)
				baseTime = sample.getTimestampMillis();
		}
		
		ret.setBaseLat(baseLat);
		ret.setBaseLon(baseLon);
		ret.setBaseTime(baseTime/1000.0);

		ArrayList<HistoricSample> msgList = new ArrayList<HistoricSample>();
		for (DataSample sample : samples) {
			HistoricSample s = new HistoricSample();
			double[] offsets = WGS84Utilities.WGS84displacement(baseLat, baseLon, 0, sample.getLatDegs(), sample.getLonDegs(), 0);
			s.setSysId(sample.getSource());
			s.setPriority(sample.getPriority());
			s.setT((int)((sample.getTimestampMillis()-baseTime)/1000));
			s.setX((short)offsets[0]);
			s.setY((short)offsets[1]);
			s.setZ((short)(sample.getzMeters() * 10));
			s.setSample(sample.getSample());
			msgList.add(s);
		}
		ret.setData(msgList);
		return ret;
	}

	// unitary test
	public static void main(String[] args) throws Exception {
		Random r = new Random(System.currentTimeMillis());
		double lat = 41, lon = -8;
		DataStore store = new DataStore();

		System.out.println("Adding 500 random samples...");
		for (int i = 0; i < 500; i++) {
			int v = r.nextInt(5);
			double newPos[] = WGS84Utilities.WGS84displace(lat, lon, 0, 1.34, 0.27, 0);
			lat = newPos[0];
			lon = newPos[1];
			IMCMessage msg;
			byte priority = 0;
			switch (v) {
			case 0:			
				PlanSpecification spec = new PlanSpecification();
				IMCUtil.fillWithRandomData(spec);
				msg = spec;
				priority = 100;
				break;
			case 1:
				LogBookEntry logBook = new LogBookEntry();
				logBook.setHtime(System.currentTimeMillis()/1000.0);
				logBook.setContext("UnitaryTest");
				logBook.setType(TYPE.INFO);
				logBook.setText(new Date().toString());
				msg = logBook;
				priority = 50;
				break;
			default:
				EstimatedState state = new EstimatedState();
				IMCUtil.fillWithRandomData(state);
				msg = state;
				priority = -50;
				break;
			}
			DataSample sample = new DataSample();
			sample.setLatDegs(lat);
			sample.setLonDegs(lon);
			sample.setPriority(priority);
			sample.setSource(28);
			sample.setzMeters(0);
			sample.setSample(msg);
			sample.setTimestampMillis(System.currentTimeMillis());
			store.addSample(sample);
			Thread.sleep(15);
		}

		System.out.println("Polling all data splitted in 1000B messages...");
		while (true) {
			try {
				HistoricData data = store.pollData(0, 1000);
				System.out.println(data.getPayloadSize());
			}
			catch (Exception  e) {
				break;
			}
		}
	}
}
