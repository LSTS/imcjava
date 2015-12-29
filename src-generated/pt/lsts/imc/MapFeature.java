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
 *  IMC Message Map Feature (603)<br/>
 *  A feature to appear on the map<br/>
 */

public class MapFeature extends IMCMessage {

	public enum FEATURE_TYPE {
		POI(0),
		FILLEDPOLY(1),
		CONTOUREDPOLY(2),
		LINE(3),
		TRANSPONDER(4),
		STARTLOC(5),
		HOMEREF(6);

		protected long value;

		public long value() {
			return value;
		}

		FEATURE_TYPE(long value) {
			this.value = value;
		}
	}

	public static final int ID_STATIC = 603;

	public MapFeature() {
		super(ID_STATIC);
	}

	public MapFeature(IMCMessage msg) {
		super(ID_STATIC);
		try{
			copyFrom(msg);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MapFeature(IMCDefinition defs) {
		super(defs, ID_STATIC);
	}

	public static MapFeature create(Object... values) {
		MapFeature m = new MapFeature();
		for (int i = 0; i < values.length-1; i+= 2)
			m.setValue(values[i].toString(), values[i+1]);
		return m;
	}

	public static MapFeature clone(IMCMessage msg) throws Exception {

		MapFeature m = new MapFeature();
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

	public MapFeature(String id, FEATURE_TYPE feature_type, short rgb_red, short rgb_green, short rgb_blue, java.util.Collection<MapPoint> feature) {
		super(ID_STATIC);
		if (id != null)
			setId(id);
		setFeatureType(feature_type);
		setRgbRed(rgb_red);
		setRgbGreen(rgb_green);
		setRgbBlue(rgb_blue);
		if (feature != null)
			setFeature(feature);
	}

	/**
	 *  @return Identifier - plaintext
	 */
	public String getId() {
		return getString("id");
	}

	/**
	 *  @param id Identifier
	 */
	public MapFeature setId(String id) {
		values.put("id", id);
		return this;
	}

	/**
	 *  @return FeatureType (enumerated) - uint8_t
	 */
	public FEATURE_TYPE getFeatureType() {
		try {
			FEATURE_TYPE o = FEATURE_TYPE.valueOf(getMessageType().getFieldPossibleValues("feature_type").get(getLong("feature_type")));
			return o;
		}
		catch (Exception e) {
			return null;
		}
	}

	public String getFeatureTypeStr() {
		return getString("feature_type");
	}

	public short getFeatureTypeVal() {
		return (short) getInteger("feature_type");
	}

	/**
	 *  @param feature_type FeatureType (enumerated)
	 */
	public MapFeature setFeatureType(FEATURE_TYPE feature_type) {
		values.put("feature_type", feature_type.value());
		return this;
	}

	/**
	 *  @param feature_type FeatureType (as a String)
	 */
	public MapFeature setFeatureTypeStr(String feature_type) {
		setValue("feature_type", feature_type);
		return this;
	}

	/**
	 *  @param feature_type FeatureType (integer value)
	 */
	public MapFeature setFeatureTypeVal(short feature_type) {
		setValue("feature_type", feature_type);
		return this;
	}

	/**
	 *  @return RedComponent - uint8_t
	 */
	public short getRgbRed() {
		return (short) getInteger("rgb_red");
	}

	/**
	 *  @param rgb_red RedComponent
	 */
	public MapFeature setRgbRed(short rgb_red) {
		values.put("rgb_red", rgb_red);
		return this;
	}

	/**
	 *  @return GreenComponent - uint8_t
	 */
	public short getRgbGreen() {
		return (short) getInteger("rgb_green");
	}

	/**
	 *  @param rgb_green GreenComponent
	 */
	public MapFeature setRgbGreen(short rgb_green) {
		values.put("rgb_green", rgb_green);
		return this;
	}

	/**
	 *  @return BlueComponent - uint8_t
	 */
	public short getRgbBlue() {
		return (short) getInteger("rgb_blue");
	}

	/**
	 *  @param rgb_blue BlueComponent
	 */
	public MapFeature setRgbBlue(short rgb_blue) {
		values.put("rgb_blue", rgb_blue);
		return this;
	}

	/**
	 *  @return Feature - message-list
	 */
	public java.util.Vector<MapPoint> getFeature() {
		try {
			return getMessageList("feature", MapPoint.class);
		}
		catch (Exception e) {
			return null;
		}

	}

	/**
	 *  @param feature Feature
	 */
	public MapFeature setFeature(java.util.Collection<MapPoint> feature) {
		values.put("feature", feature);
		return this;
	}

}
