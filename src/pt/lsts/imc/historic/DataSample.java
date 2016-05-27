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

import pt.lsts.imc.HistoricData;
import pt.lsts.imc.HistoricSample;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.RemoteCommand;
import pt.lsts.imc.RemoteData;
import pt.lsts.util.WGS84Utilities;

/**
 * @author zp
 *
 */
public class DataSample implements Comparable<DataSample> {

	private double latDegs, lonDegs, zMeters;
	private byte priority;
	private int source;
	private long timestampMillis;
	private IMCMessage sample;

	/**
	 * @return the latDegs
	 */
	public double getLatDegs() {
		return latDegs;
	}

	/**
	 * @param latDegs
	 *            the latDegs to set
	 */
	public void setLatDegs(double latDegs) {
		this.latDegs = latDegs;
	}

	/**
	 * @return the lonDegs
	 */
	public double getLonDegs() {
		return lonDegs;
	}

	/**
	 * @param lonDegs
	 *            the lonDegs to set
	 */
	public void setLonDegs(double lonDegs) {
		this.lonDegs = lonDegs;
	}

	/**
	 * @return the zMeters
	 */
	public double getzMeters() {
		return zMeters;
	}

	/**
	 * @param zMeters
	 *            the zMeters to set
	 */
	public void setzMeters(double zMeters) {
		this.zMeters = zMeters;
	}

	/**
	 * @return the priority
	 */
	public byte getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(byte priority) {
		this.priority = priority;
	}

	/**
	 * @return the source
	 */
	public int getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(int source) {
		this.source = source;
	}

	/**
	 * @return the timestampMillis
	 */
	public long getTimestampMillis() {
		return timestampMillis;
	}

	/**
	 * @param timestampMillis
	 *            the timestampMillis to set
	 */
	public void setTimestampMillis(long timestampMillis) {
		this.timestampMillis = timestampMillis;
	}

	/**
	 * @return the sample
	 */
	public IMCMessage getSample() {
		return sample;
	}

	/**
	 * @param sample
	 *            the sample to set
	 */
	public void setSample(IMCMessage sample) {
		this.sample = sample;
	}
	
	public static ArrayList<RemoteCommand> parseCommands(HistoricData data) {
		return parseCommands(data, 65535);
	}
	
	/**
	 * Extract commands from HistoricData message
	 * @param data an HistoricData message
	 * @param destination The IMC identifier of a destination system. Use 65535 to retrieve commands to any vehicle.
	 * @return All commands destinated to the given destination and which are part of data. If no commands are found, returns an empty list
	 */
	public static ArrayList<RemoteCommand> parseCommands(HistoricData data, int destination) {
		ArrayList<RemoteCommand> ret = new ArrayList<RemoteCommand>();
		for (RemoteData hdata : data.getData())
			if (hdata instanceof RemoteCommand) {
				RemoteCommand cmd = (RemoteCommand) hdata;
				if (destination == 65535 || cmd.getDestination() == destination)
					ret.add(cmd);
			}		
		return ret;
	}
	
	/**
	 * Extract all samples contained in the given HistoricData message
	 */
	public static ArrayList<DataSample> parseSamples(HistoricData data) {
		ArrayList<DataSample> ret = new ArrayList<DataSample>();
		for (RemoteData hdata : data.getData()) {
			if (hdata instanceof HistoricSample) {
				HistoricSample sample = (HistoricSample) hdata;
				DataSample s = new DataSample();
				double[] pos = WGS84Utilities.WGS84displace(data.getBaseLat(), data.getBaseLon(), 0, sample.getX(), sample.getY(), 0);
				s.source = sample.getSysId();
				s.timestampMillis = (long)(data.getBaseTime() * 1000.0 + sample.getT() * 1000.0);
				s.priority = sample.getPriority();
				s.latDegs = pos[0];
				s.lonDegs = pos[1];
				s.zMeters = sample.getZ() / 10.0;
				s.sample = sample.getSample();
				ret.add(s);
			}
		}
		return ret;
	}
	
	/**
	 * Calculate the size of this sample when serialized inside an HistoricData message.
	 */
	public int getSerializationSize() {
		return sample.getPayloadSize() + DataStore.HISTORIC_SAMPLE_BASE_SIZE;
	}
	
	@Override
	public int compareTo(DataSample o) {
		if (o.getPriority() == getPriority()) {
			if (o.getSource() == getSource() && o.getSample().getMgid() == getSample().getMgid()) {
				return new Long(Math.round(getTimestampMillis()/250.0)).compareTo(Math.round(o.getTimestampMillis()/250.0));
			}
			return new Long(getTimestampMillis()).compareTo(o.getTimestampMillis());
		}
		return new Byte(getPriority()).compareTo(o.getPriority());						
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DataSample && compareTo((DataSample) obj) == 0;
	}
}
