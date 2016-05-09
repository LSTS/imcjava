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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import pt.lsts.imc.Abort;
import pt.lsts.imc.CompressedHistory;
import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.HistoricData;
import pt.lsts.imc.HistoricSample;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;
import pt.lsts.imc.IMCUtil;
import pt.lsts.imc.LogBookEntry;
import pt.lsts.imc.LogBookEntry.TYPE;
import pt.lsts.imc.PlanSpecification;
import pt.lsts.imc.RemoteCommand;
import pt.lsts.imc.RemoteData;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class DataStore {

	public final int HISTORIC_DATA_BASE_SIZE = 16 + IMCDefinition.getInstance().headerLength();
	public static final int HISTORIC_SAMPLE_BASE_SIZE = 15;

	private TreeSet<DataSample> history = new TreeSet<DataSample>(Collections.reverseOrder());

	private LinkedHashMap<Integer, TreeSet<RemoteCommand>> commands = new LinkedHashMap<Integer, TreeSet<RemoteCommand>>();

	public void addData(HistoricData data) {
		for (DataSample sample : DataSample.parseSamples(data))
			addSample(sample);		

		for (RemoteData hdata : data.getData()) {
			if (hdata instanceof RemoteCommand)
				addCommand((RemoteCommand)hdata);
		}
	}
	
	public void clearData() {
		synchronized (history) {
			history.clear();
		}
	}
	
	public int numSamples() {
		return history.size();
	}
	
	public void addData(CompressedHistory data) throws Exception {

		HistoricData msg = new HistoricData();
		msg.setBaseLat(data.getBaseLat());
		msg.setBaseLon(data.getBaseLon());
		msg.setBaseTime(data.getBaseTime());
		IMCInputStream iis = new IMCInputStream(new GZIPInputStream(new ByteArrayInputStream(data.getData())), IMCDefinition.getInstance());
		iis.setBigEndian(false);
		ArrayList<RemoteData> messages = new ArrayList<RemoteData>();

		while (iis.available() > 0) {
			try {
				RemoteData sample = (RemoteData) iis.readInlineMessage();				
				messages.add(sample);
			}
			catch (EOFException e) {
				break;
			}
		}
		iis.close();

		msg.setData(messages);
		addData(msg);		
	}
	
	public boolean contains(DataSample sample) {
		synchronized (history) {
			return history.contains(sample);
		}
	}

	public void addSample(DataSample sample) {
		synchronized (history) {
			history.add(sample);
		}
	}

	public void addCommand(RemoteCommand cmd) {
		synchronized (commands) {
			if (!commands.containsKey(cmd.getDestination())) {
				commands.put(cmd.getDestination(), new TreeSet<RemoteCommand>(new Comparator<RemoteCommand>() {
					@Override
					public int compare(RemoteCommand o1, RemoteCommand o2) {
						return new Double(o1.getTimestamp()).compareTo(new Double(o2.getTimestamp()));
					}
				}));
			}
			commands.get(cmd.getDestination()).add(cmd);
		}
	}

	public HistoricSample translate(DataSample sample, double baseLat, double baseLon, long baseTime) {
		HistoricSample s = new HistoricSample();
		double[] offsets = WGS84Utilities.WGS84displacement(baseLat, baseLon, 0, sample.getLatDegs(),
				sample.getLonDegs(), 0);
		s.setX((short) offsets[0]);
		s.setY((short) offsets[1]);
		s.setZ((short) (sample.getzMeters() * 10));
		s.setT((short) ((sample.getTimestampMillis() - baseTime) / 1000.0));
		s.setSysId(sample.getSource());
		s.setSample(sample.getSample());
		return s;
	}


	public CompressedHistory pollCompressedData(int destination, int size) throws Exception {

		CompressedHistory ret = new CompressedHistory();
		size -= HISTORIC_DATA_BASE_SIZE;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream zipOut = new GZIPOutputStream(baos, true);
		IMCOutputStream ios = new IMCOutputStream(zipOut);
		ArrayList<DataSample> samples = new ArrayList<DataSample>();

		DataSample pivot = history.first();
		if (pivot == null)
			throw new Exception("No data to be transmitted");

		double baseLat = pivot.getLatDegs();
		double baseLon = pivot.getLonDegs();
		long baseTime = pivot.getTimestampMillis();

		ret.setBaseLat(baseLat);
		ret.setBaseLon(baseLon);
		ret.setBaseTime(baseTime/1000.0);

		int last_position = 0;
		
		synchronized (commands) {
			if (commands.containsKey(destination)) {
				while (!commands.get(destination).isEmpty()) {
					RemoteCommand cmd = commands.get(destination).first();
					
					if (cmd.getTimeout() < System.currentTimeMillis() / 1000.0) {
						commands.get(destination).pollFirst();
						System.err.println("Remote command has expired and won't be sent:\n"+cmd);
						continue;
					}
					
					ios.writeInlineMessage(cmd);
					zipOut.flush();
					baos.flush();
					if (baos.size() <= size) {
						last_position = baos.size();
						commands.get(destination).pollFirst();
					}
					else
						break;
				}
			}
		}
		

		synchronized (history) {
			while (history.size() > 0) {
				HistoricSample s = translate(history.first(), baseLat, baseLon, baseTime);
				ios.writeInlineMessage(s);
				zipOut.flush();
				baos.flush();
				if (baos.size() <= size) {
					last_position = baos.size();
					samples.add(history.pollFirst());
				}
				else
					break;
			}
		}
		if (samples.isEmpty())
			throw new Exception("No data to be transmitted");
		ret.setData(Arrays.copyOfRange(baos.toByteArray(), 0, last_position));
		return ret;
	}

	public HistoricData pollData(int destination, int size) throws Exception {

		HistoricData ret = new HistoricData();
		ArrayList<RemoteData> msgList = new ArrayList<RemoteData>();

		size -= HISTORIC_DATA_BASE_SIZE;

		// commands take precedence over historic data
		synchronized (commands) {

			if (commands.containsKey(destination)) {
				ArrayList<RemoteCommand> remove = new ArrayList<RemoteCommand>();

				for (RemoteCommand cmd : commands.get(destination)) {
					if (cmd.getTimeout() <= System.currentTimeMillis()/1000.0) {
						System.err.println("Remote command has expired and won't be sent:\n"+cmd);
						remove.add(cmd); // expired commands are simply dropped
					}
					else if (cmd.getPayloadSize()+2 < size) {
						msgList.add(cmd);
						size -= cmd.getPayloadSize()+2;
						remove.add(cmd);
					}
				}			
				commands.get(destination).removeAll(remove);
			}
		}

		ArrayList<DataSample> samples = new ArrayList<DataSample>();
		ArrayList<DataSample> rejected = new ArrayList<DataSample>();
		synchronized (history) {
			while (size > HISTORIC_SAMPLE_BASE_SIZE) {
				DataSample sample = history.pollFirst();
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


		for (DataSample sample : samples) {
			HistoricSample s = translate(sample, baseLat, baseLon, baseTime);
			msgList.add(s);
		}
		ret.setData(msgList);
		return ret;
	}

	// unit test
	public static void main(String[] args) throws Exception {
		Random r = new Random(System.currentTimeMillis());
		double lat = 41, lon = -8;
		DataStore store1 = new DataStore();
		DataStore store2 = new DataStore();
		DataStore store3 = new DataStore();
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
			store1.addSample(sample);
			store2.addSample(sample);
			Thread.sleep(15);
		}

		store1.addCommand(new RemoteCommand(0, 31, 3600, new Abort()));

		int size = 1000;

		System.out.println("Polling all data compressed and split into "+size+"B messages...");
		int count = 0;
		while (true) {
			try {
				CompressedHistory data = store1.pollCompressedData(0, size);
				store3.addData(data);
				System.out.println(++count+" size: "+(data.getPayloadSize() + IMCDefinition.getInstance().headerLength()+2));				
			}
			catch (Exception  e) {
				e.printStackTrace();
				break;
			}
		}

		System.out.println("Polling all data split into "+size+"B messages...");
		count = 0;
		while (true) {
			try {
				HistoricData data = store3.pollData(0, size);
				System.out.println(++count+" size: "+(data.getPayloadSize() + IMCDefinition.getInstance().headerLength()+2));
			}
			catch (Exception  e) {
				break;
			}
		}
	}
}
