/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2020, Laboratório de Sistemas e Tecnologia Subaquática
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
 *  IMC Message Geographical Feature (3002)<br/>
 *  This message holds a geographical that, according to the number of vertices, may correspond to a point, a line or polygon.<br/>
 */

public class GeoFeature extends IMCMessage {

	public static final int ID_STATIC = 3002;

	public GeoFeature() {
		super(ID_STATIC);
	}

	public GeoFeature(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GeoFeature(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static GeoFeature create(Object... values) {
		GeoFeature m = new GeoFeature();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static GeoFeature clone(IMCMessage msg) throws Exception {

		GeoFeature m = new GeoFeature();
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

	public GeoFeature(int feature_id, java.util.Collection<MapPoint> points) {
		super(ID_STATIC);
		setFeatureId(feature_id);
		if (points != null)
			setPoints(points);
	}

	/**
	 *  @return Identifier - uint16_t
	 */
	public int getFeatureId() {
		return getInteger("feature_id");
	}

	/**
	 *  @param feature_id Identifier
	 */
	public GeoFeature setFeatureId(int feature_id) {
		values.put("feature_id", feature_id);
		return this;
	}

	/**
	 *  @return Points - message-list
	 */
	public java.util.Vector<MapPoint> getPoints() {
		try {
			return getMessageList("points", MapPoint.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param points Points
	 */
	public GeoFeature setPoints(java.util.Collection<MapPoint> points) {
		values.put("points", points);
		return this;
	}

}
