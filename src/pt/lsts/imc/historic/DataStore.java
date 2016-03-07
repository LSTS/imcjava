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
import java.util.PriorityQueue;

import pt.lsts.imc.HistoricData;
import pt.lsts.imc.HistoricSample;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class DataStore {

	public final int HISTORIC_DATA_BASE_SIZE = 14 + IMCDefinition.getInstance().headerLength();
	public static final int HISTORIC_SAMPLE_BASE_SIZE = 15;
	
	private PriorityQueue<DataSample> history = new PriorityQueue<DataSample>(Collections.reverseOrder());
	
	public void setData(HistoricData data) {
		synchronized (history) {
			history.addAll(DataSample.parse(data));	
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
	public static void main(String[] args) {
		//TODO
	}
}
