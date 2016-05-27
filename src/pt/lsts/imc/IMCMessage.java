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
 * $Id:: IMCMessage.java 393 2013-03-03 10:40:48Z zepinto@gmail.com            $:
 */
package pt.lsts.imc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;

import pt.lsts.neptus.messages.IMessage;
import pt.lsts.neptus.messages.IMessageProtocol;
import pt.lsts.neptus.messages.InvalidMessageException;
import pt.lsts.neptus.messages.listener.MessageInfo;

/**
 * This class holds a message structure, including header and payload.<br/>
 * The message structure can be accessed with {@link #getMessageType()} and
 * header with {@link #getHeader()}
 * 
 * @author zp
 * 
 */
public class IMCMessage implements IMessage, Comparable<IMCMessage> {

	protected Map<String, Object> values = new LinkedHashMap<String, Object>();
	protected IMCMessageType type;
	private Header header = null;
	public static final int DEFAULT_ENTITY_ID = 255;
	public static final int DEFAULT_SYSTEM_ID = 65535;
	protected IMCDefinition definitions = null;

	private MessageInfo messageInfo = null;

	/**
	 * Creates a new (dummy) message
	 */
	public IMCMessage() {
		this(IMCDefinition.getInstance());
	}

	/**
	 * Creates a new IMCMessage given its type and using default IMC definitions
	 * 
	 * @param type
	 *            The {@link IMCMessageType} of the message to be created
	 */
	public IMCMessage(IMCMessageType type) {
		this(IMCDefinition.getInstance(), type);
	}

	/**
	 * Creates a new message based on an existing header and using default IMC
	 * definitions
	 * 
	 * @param header
	 *            The header of the message, which will define its type
	 */
	public IMCMessage(Header header) {
		this(IMCDefinition.getInstance(), header);
	}

	/**
	 * Uses the default IMC definitions and calls
	 * {@link #IMCMessage(IMCDefinition, Integer)}
	 * 
	 * @param type
	 *            The type (mgid) of the message to be created
	 */
	public IMCMessage(Integer type) {
		this(IMCDefinition.getInstance(), type);
	}

	/**
	 * Resolves the type, given an abbreviated name and creates a corresponding
	 * message using default IMC definitions
	 * 
	 * @param abbreviatedName
	 *            The abbreviated name of the message to be created
	 */
	public IMCMessage(String abbreviatedName) {
		this(IMCDefinition.getInstance(), abbreviatedName);
	}

	/**
	 * Creates a new message given its abbreviated name and fills it with given
	 * values
	 * 
	 * @param abbreviatedName
	 *            The message's abbreviated name
	 * @param values
	 *            A list of objects which will be parse in pairs. <br/>
	 *            Example:
	 * 
	 *            <pre>
	 * IMCMessage state = new IMCMessage(&quot;EstimatedState&quot;, &quot;ref&quot;, &quot;NED_ONLY&quot;, &quot;x&quot;,
	 * 		10.343, y, -100);
	 * </pre>
	 */
	public IMCMessage(String abbreviatedName, Object... values) {
		this(abbreviatedName);
		for (int i = 0; i < values.length - 1; i += 2)
			setValue(values[i].toString(), values[i + 1]);
	}

	/**
	 * Create a new message tied to given IMC definitions
	 * 
	 * @param defs
	 *            The definitions used to generate header of this message
	 */
	protected IMCMessage(IMCDefinition defs) {
		this.definitions = defs;
		header = defs.createHeader();
		type = defs.createDummyType();
	}

	/**
	 * Creates a new message based on an existing header
	 * 
	 * @param defs
	 *            IMC definitions to be used
	 * @param header
	 *            The header of the message, which will define its type
	 */
	protected IMCMessage(IMCDefinition defs, Header header) {
		this.definitions = defs;
		this.header = header;
		this.type = defs.getType(header.getInteger("mgid"));
	}

	/**
	 * Class constructor that creates a message with given numeric type and
	 * initializes its fields with the values
	 * 
	 * @param type
	 *            The numeric id of this message (mgid)
	 * @param values
	 *            Optional initialization values to be set on the message. The
	 *            values are a sequence of <name (String), value (Object)>.
	 *            Example: <br/>
	 *            <code>IMCMessage msg = new IMCMessage(EstimatedState.ID_STATIC, "x", 10.0, "y", -10, "ref", "NED_LLD");</code>
	 */
	protected IMCMessage(Integer type, Object... values) {
		this(type);

		for (int i = 0; i < values.length - 1; i += 2)
			setValue(values[i].toString(), values[i + 1]);
	}

	/**
	 * Resolves the message type given its abbreviated name and then calls
	 * {@link #IMCMessage(IMCMessageType)}
	 * 
	 * @param abbreviatedName
	 *            The message's abbreviated name
	 */
	protected IMCMessage(IMCDefinition defs, String abbreviatedName) {
		this(defs, defs.getMessageId(abbreviatedName));
	}

	/**
	 * Creates a new message given its type. <br/>
	 * If it's not an header, an empty header will be added.
	 * 
	 * @param type
	 *            The type for this message.
	 */
	public IMCMessage(IMCDefinition defs, IMCMessageType type) {
		this.definitions = defs;
		if (type != null) {
			this.type = type;
			if (type != defs.headerType) {
				header = defs.createHeader();
				header.set_sync((int) defs.syncWord);
				header.set_mgid(type.getId());
				header.set_timestamp(System.currentTimeMillis() / 1000.0);
				header.set_dst(0xFFFF);
				header.set_dst_ent((short) 0xFF);
			}
		}
	}

	/**
	 * Resolves the message type, given its message identification number and
	 * then calls {@link #IMCMessage(IMCMessageType)}
	 * 
	 * @param defs
	 *            IMC definitions to be used
	 * @param type
	 *            The id of the message type (field mgid in IMC)
	 * 
	 */
	protected IMCMessage(IMCDefinition defs, Integer type) {
		this(defs, defs.getType(type));
	}

	/**
	 * @return The message type for this message
	 */
	public final IMCMessageType getMessageType() {
		if (type == null && this instanceof Header)
			type = definitions.getHeaderType();
		return type;
	}

	void setMessageType(IMCMessageType type) {
		this.type = type;
	}

	/**
	 * Retrieves the header of this message, stored as an inner IMC Message
	 * (payload fields match header fields and header is NULL)
	 * 
	 * @return The header of this message
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * Change the header of this message
	 * 
	 * @param header
	 *            The new header for this message
	 */
	public void setHeader(Header header) {
		this.header = header;
	}

	/**
	 * Set all values from another message of same type
	 * 
	 * @param otherMessage
	 *            Message where to read values from
	 * @throws Exception
	 *             In case the types of the messages do not match
	 */
	public IMCMessage copyFrom(IMCMessage otherMessage) throws Exception {
		if (otherMessage.getMessageType().getId() != getMessageType().getId()) {
			throw new Exception("Types of messages do not match: "
					+ getMessageType().getShortName() + " vs "
					+ otherMessage.getMessageType().getShortName());
		}

		getHeader().setValues(otherMessage.getHeader().values);
		setValues(otherMessage.values);
		return this;
	}

	/**
	 * All the values in the given hashtable will be copied to this message
	 * without any conversions
	 * 
	 * @param values
	 */
	public void setValues(Map<String, Object> values) {
		this.values.putAll(values);
	}

	/**
	 * Returns all the values in this message. The returned map may not be
	 * modified, otherwise a {@link UnsupportedOperationException} will be
	 * thrown
	 * 
	 * @return All the values in this message
	 */
	public Map<String, Object> getValues() {
		return Collections.unmodifiableMap(this.values);
	}

	/**
	 * Change the type of the message. Bear in mind that values are preserved
	 * but may not match the new type's fields
	 * 
	 * @param type
	 *            The new type for this message
	 */
	public void setType(IMCMessageType type) {
		this.type = type;
	}

	/**
	 * Create a copy of this message using the given definitions. If some fields
	 * changed between IMC definitions the clone may loose some fields
	 * 
	 * @param defs
	 *            The definitions to be used when cloning the message
	 * @return A message with same values and type
	 */
	public IMCMessage cloneMessage(IMCDefinition defs) {
		try {
			IMCMessage message = getHeader() != null ? defs.newMessage(type
					.getShortName()) : defs.createHeader();
			if (getHeader() != null)
				message.getHeader().setValues(getHeader().values);
			message.setValues(values);
			return message;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Creates a cloned message with copied values
	 * 
	 * @return A clone of this instance
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IMCMessage cloneMessage() {
		IMCMessage message = new IMCMessage(this.definitions, getMessageType());
		if (getHeader() != null)
			message.getHeader().setValues(getHeader().values);
		message.setValues(values);
		return message;
	}

	/**
	 * Retrieve IMC id of this message source (header field "src")
	 * 
	 * @return The IMC identifier of this message source (system)
	 */
	public int getSrc() {
		return getHeader().getInteger("src");
	}

	public String getSourceName() {
		return definitions.getResolver().resolve(getSrc());
	}

	public String getEntityName() {
		return definitions.getResolver().resolveEntity(getSrc(), getSrcEnt());
	}

	/**
	 * The numeric id of this message destination (header field "dst")
	 * 
	 * @return The IMC identifier of this message destination (system)
	 */
	public int getDst() {
		return getHeader().get_dst();
	}

	/**
	 * Retrieve IMC id of this message source entity (header field "src_ent")
	 * 
	 * @return The IMC identifier of this message source entity
	 */
	public short getSrcEnt() {
		return getHeader().get_src_ent();
	}

	/**
	 * Retrieve IMC id of this message destination entity (header field
	 * "dst_ent")
	 * 
	 * @return The IMC identifier of this message destination entity
	 */
	public short getDstEnt() {
		return getHeader().get_dst_ent();
	}

	/**
	 * Retrieve the size indicated on the header of this message (header field
	 * "size")
	 * 
	 * @return The indicated size
	 */
	public int getSize() {
		return getHeader().get_size();
	}

	/**
	 * Set the source of this message (header field "src")
	 * 
	 * @param src
	 *            The new source of this message
	 */
	public void setSrc(int src) {
		getHeader().set_src(src);
	}

	/**
	 * Set the destination of this message (header field "dst")
	 * 
	 * @param src
	 *            The destination of this message
	 */
	public void setDst(int dst) {
		getHeader().set_dst(dst);
	}

	/**
	 * Set the source entity of this message (header field "src_ent")
	 * 
	 * @param src
	 *            The source entity of this message
	 */
	public void setSrcEnt(int src_ent) {
		getHeader().set_src_ent((short) src_ent);
	}

	/**
	 * Set the destination entity of this message (header field "dst_ent")
	 * 
	 * @param src
	 *            The destination entity of this message
	 */
	public void setDstEnt(int dst_ent) {
		getHeader().set_dst_ent((short) dst_ent);
	}

	protected void setSize(int size) {
		getHeader().set_size(size);
	}

	protected void fillHeader() {
		setTimestamp(System.currentTimeMillis() / 1000.0);
		setSize(getPayloadSize());
		setDst(65535);
		setDstEnt((short) 255);
		setSrc(65535);
		setSrcEnt((short) 255);
	}

	/**
	 * @return The number of bytes that the payload occupies when serialized
	 */
	public int getPayloadSize() {
		int size = 0;

		// in case it is an header, there is no payload
		if (type == null)
			return 0;
		// ---------------
		for (String f : type.getFieldNames()) {
			IMCFieldType t = type.getFieldType(f);
			if (t.isSizeKnown())
				size += t.getSizeInBytes();
			else {
				switch (t) {
				case TYPE_RAWDATA:
				case TYPE_PLAINTEXT:
					if (getRawData(f) != null)
						size += 2 + getRawData(f).length;
					else
						size += 2;
					break;
				case TYPE_MESSAGE:
					IMCMessage m = getMessage(f);
					if (m == null)
						size += 2;
					else
						size += 2 + m.getPayloadSize();
					break;
				case TYPE_MESSAGELIST:
					Vector<IMCMessage> msgs = getMessageList(f);
					for (int i = 0; i < msgs.size(); i++) {
						size += 2 + msgs.get(i).getPayloadSize();
					}
					size += 2;
					break;
				default:
					break;
				}
			}
		}
		return size;
	}

	/**
	 * Sets a field value
	 * 
	 * @param field
	 *            The field to be set
	 * @param value
	 *            The new value for the field. <br/>
	 *            If the field is Enumerated, then the name of the value can be
	 *            passed as a String If the field is Bitmask, then the name of
	 *            the value can be passed as a LinkedHashMap<String, Boolean>
	 */
	@SuppressWarnings("unchecked")
	public IMCMessage setValue(String field, Object value) {

		if (header == null) {
			values.put(field, value);
			return this;
		}

		header.setValue(field, value);
		if (getMessageType().getFieldType(field) == null
				&& header.type.getFieldType(field) != null) {
			return this;
		}

		if (getMessageType().getFieldMeanings(field) == null) {
			values.put(field, value);
			return this;
		}

		if (value instanceof String) {
			parseStringValue(field, (String) value);
			return this;
		}

		if (value instanceof LinkedHashMap<?, ?>) {
			if (getMessageType().getFieldUnits(field).equalsIgnoreCase(
					"tuplelist")) {

				values.put(field,
						encodeTupleList((LinkedHashMap<String, ?>) value));
			} else if (getMessageType().getFieldUnits(field).equalsIgnoreCase(
					"bitmask")) {
				try {
					LinkedHashMap<String, Boolean> valTmp = (LinkedHashMap<String, Boolean>) value;
					setBitMask(field, valTmp);
				} catch (Exception e) {
					e.printStackTrace();
					System.err
					.println("Trying to set a bitmask with an LinkedHashMap other"
							+ " than LinkedHashMap<String, Boolean>!!!");
				}
			}
			return this;
		}

		values.put(field, value);
		return this;
	}

	protected void parseStringValue(String field, String sValue) {
		IMCFieldType type = getMessageType().getFieldType(field);

		if (getMessageType().getFieldUnits(field)
				.equalsIgnoreCase("enumerated")) {
			Long val = getMessageType().getFieldMeanings(field).get(sValue);
			if (val != null) {
				setValue(field, val);
				return;
			}
		}

		if (getMessageType().getFieldUnits(field).equalsIgnoreCase("bitmask")
				|| getMessageType().getFieldUnits(field).equalsIgnoreCase(
						"bitfield")) {
			String[] parts = sValue.split("\\|");
			long value = 0;

			for (String s : parts) {
				Long val = getMessageType().getFieldMeanings(field).get(
						s.trim());
				if (val != null)
					value += val;
			}
			setValue(field, value);
			return;
		}

		switch (type) {
		case TYPE_PLAINTEXT:
			setValue(field, sValue);
			break;
		case TYPE_RAWDATA:
			byte[] result = new byte[sValue.length() / 2];
			for (int i = 0; i < sValue.length(); i += 2)
				result[i] = Byte.parseByte("0x" + sValue.charAt(i)
						+ sValue.charAt(i + 1));
			break;
		case TYPE_FP32:
			setValue(field, Float.parseFloat(sValue));
			break;
		case TYPE_FP64:
			setValue(field, Double.parseDouble(sValue));
			break;
		case TYPE_INT8:
			setValue(field, Byte.parseByte(sValue));
			break;
		case TYPE_UINT8:
		case TYPE_INT16:
			setValue(field, Short.parseShort(sValue));
			break;
		case TYPE_UINT16:
		case TYPE_INT32:
			setValue(field, Integer.parseInt(sValue));
			break;
		case TYPE_UINT32:
		case TYPE_INT64:
			setValue(field, Long.parseLong(sValue));
			break;
		default:
			break;
		}
	}

	/**
	 * Retrieve a value from the header
	 * 
	 * @param field
	 *            The name of the field to retrieve
	 * @return The value of the field in the header or null if the field does
	 *         not exist in the header
	 */
	public Object getHeaderValue(String field) {
		return header.getValue(field);
	}

	/**
	 * Retrieve a value in the message
	 * 
	 * @param field
	 *            The name of the field to be retrieved
	 * @return The value of the field in the payload or in the header if it
	 *         doesn't exist in the payload or null if the field does not exist
	 *         on both
	 */
	public Object getValue(String field) {
		Object o = values.get(field);
		if (o == null && header != null)
			o = header.getValue(field);
		if (o == null)
			return getMessageType().getDefaultValue(field);

		return o;
	}

	/**
	 * Given a map of Strings to Objects, encodes a tuplelist in the type:
	 * name1=value1;name2=value2 ...
	 * 
	 * @param map
	 *            A map from Strings (var names) to Objects (values)
	 * @return The encoded tuplelist
	 */
	public static String encodeTupleList(LinkedHashMap<String, ?> map) {
		StringBuilder res = new StringBuilder();
		for (String key : map.keySet())
			res.append(key).append('=').append(map.get(key)).append(';');
		return res.toString();
	}

	/**
	 * Reverse from {@link #encodeTupleList(LinkedHashMap)}
	 * 
	 * @param tupleList
	 *            An encoded tuplelist
	 * @return A map from Strings to Strings
	 */
	public static LinkedHashMap<String, String> decodeTupleList(String tupleList) {
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		String[] parts = tupleList.split(";");
		for (String p : parts) {
			String[] ps = p.split("=");
			if (ps.length == 2)
				values.put(ps[0], ps[1]);
		}
		return values;
	}

	/**
	 * Returns the value of a field as an hash map
	 * 
	 * @param field
	 *            The name of the field (should be a tuplelist)
	 * @return An hash map with all the values of the tuplelist already parsed
	 */
	public LinkedHashMap<String, String> getTupleList(String field) {
		if (getMessageType().getFieldUnits(field).equals("tuplelist"))
			return IMCMessage.decodeTupleList(getAsString(field));

		return new LinkedHashMap<String, String>();
	}

	/**
	 * Retrieves the value of a Bitmask as map of booleans
	 * 
	 * @param field
	 *            The name of the field (should be a Bitmask)
	 * @return The value of the Bitmask field as map of booleans
	 */
	public LinkedHashMap<String, Boolean> getBitmask(String field) {
		LinkedHashMap<String, Boolean> bitmask = new LinkedHashMap<String, Boolean>();
		long value = getLong(field);
		for (String key : getMessageType().getFieldMeanings(field).keySet()) {
			bitmask.put(key, (value & getMessageType().getFieldMeanings(field)
					.get(key)) != 0);
		}
		return bitmask;
	}

	/**
	 * Set the value of a bitmask field using a map from Strings to Booleans
	 */
	public void setBitMask(String field, LinkedHashMap<String, Boolean> bitmask) {
		long value = 0;

		for (String k : bitmask.keySet()) {
			if (bitmask.get(k))
				value += getMessageType().getFieldMeanings(field).get(k.trim());
		}
		setValue(field, value);
	}

	/**
	 * Retrives the value of a field as a double value. <br/>
	 * If the field is not numeric, returns <i>Double.NaN</i>
	 * 
	 * @param field
	 *            The name of the field (should be numeric)
	 * @return The value of the field as double or <i>Double.NaN</i> if the
	 *         field is not numeric
	 */
	public double getDouble(String field) {
		Object o = getValue(field);
		if (o instanceof Double)
			return (Double) o;
		if (o instanceof Number)
			return ((Number) o).doubleValue();
		return Double.NaN;
	}

	/**
	 * Retrives the value of a field as a float value. <br/>
	 * If the field is not numeric, returns <i>Float.NaN</i>
	 * 
	 * @param field
	 *            The name of the field (should be numeric)
	 * @return The value of the field as float or <i>Float.NaN</i> if the field
	 *         is not numeric
	 */
	public float getFloat(String field) {
		Object o = getValue(field);
		if (o instanceof Float)
			return (Float) o;
		if (o instanceof Number)
			return ((Number) o).floatValue();
		return Float.NaN;
	}

	/**
	 * Retrives the value of a field as an integer value. <br/>
	 * If the field is not numeric, returns <i>0</i>
	 * 
	 * @param field
	 *            The name of the field (should be numeric)
	 * @return The value of the field as integer or <i>0</i> if the field is not
	 *         numeric
	 */
	public int getInteger(String field) {
		Object o = getValue(field);
		if (o instanceof Integer)
			return (Integer) o;
		if (o instanceof Number)
			return ((Number) o).intValue();

		return 0;
	}

	/**
	 * Retrives the value of a field as a long value. <br/>
	 * If the field is not numeric, returns <i>0</i>
	 * 
	 * @param field
	 *            The name of the field (should be numeric)
	 * @return The value of the field as long or <i>0</i> if the field is not
	 *         numeric
	 */
	public long getLong(String field) {
		Object o = getValue(field);
		if (o instanceof Long)
			return (Long) o;
		if (o instanceof Number)
			return ((Number) o).longValue();
		return 0;
	}

	protected DecimalFormat format = new DecimalFormat("#.000");
	protected DecimalFormat doubleFormat = new DecimalFormat("0.00000000");

	public String getString(String field, boolean addUnits) {
		// if (field.equals("timestamp"))
		// return format.format(getDouble("timestamp"));

		Object o = getValue(field);
		if (o == null)
			return null;
		else if (o instanceof Number
				&& getMessageType().getFieldPossibleValues(field) != null) {
			if (getUnitsOf(field).equals("tuplelist")
					|| getUnitsOf(field).equals("enumerated"))
				return getMessageType().getFieldPossibleValues(field).get(
						((Number) o).longValue());
			else {

				long val = getLong(field);
				String ret = "";
				for (int i = 0; i < 16; i++) {
					long bitVal = (long) Math.pow(2, i);
					if ((val & bitVal) > 0)
						ret += getMessageType().getFieldPossibleValues(field)
						.get(bitVal) + "|";
				}
				ret = ret.replaceAll("null\\|", "");
				ret = ret.replaceAll("\\|null", "");
				if (ret.length() > 0) // remove last "|"
					ret = ret.substring(0, ret.length() - 1);
				return ret;
			}
		} else if (o instanceof byte[]) {
			StringBuilder sb = new StringBuilder();
			byte[] buf = (byte[]) o;
			for (int i = 0; i < buf.length && i < 10; i++) {
				sb.append(String.format("%02X", buf[i]));
			}
			if (buf.length > 10)
				sb.append("...");
			return sb.toString();
		} else if (o instanceof Double) {
			return doubleFormat.format((Double) o);
		} else if (o instanceof IMCMessage) {
			return "%INLINE{"
					+ (((IMCMessage) o).getMessageType() != null ? ((IMCMessage) o)
							.getMessageType().getShortName() : "NULL") + "}";
		} else if (o instanceof Vector<?>) {
			String ret = "%MESSAGE-LIST[";
			Vector<?> vec = (Vector<?>) o;
			for (Object ob : vec) {
				ret += ((IMCMessage) ob).getMessageType() != null ? ((IMCMessage) ob)
						.getMessageType().getShortName() + ", "
						: "NULL, ";
			}
			if (!vec.isEmpty())
				ret = ret.substring(0, ret.length() - 2);
			return ret + "]";
		} else if ("rad"
				.equalsIgnoreCase(getMessageType().getFieldUnits(field))) {
			return addUnits ? Math.toDegrees(((Number) o).doubleValue())
					+ " deg" : o.toString();
		} else if ("rad/s".equalsIgnoreCase(getMessageType().getFieldUnits(
				field))) {
			return addUnits ? Math.toDegrees(((Number) o).doubleValue())
					+ " deg/s" : o.toString();
		}
		if (getMessageType().getFieldUnits(field) == null)
			return o.toString();
		return o.toString()
				+ (addUnits ? " " + getMessageType().getFieldUnits(field) : "");
	}

	/**
	 * Returns a String representation for the value in the given field
	 * 
	 * @param field
	 *            The field to be returned as String
	 * @return A String representation of the value in the given field
	 */
	public String getString(String field) {
		return getString(field, true);
	}

	/**
	 * Returns a byte array from a rawdata field
	 * 
	 * @param field
	 *            The field whose value is to be returned
	 * @return The value of the rawdata as a byte array or <i>null</i> if an
	 *         error occurs
	 */
	public byte[] getRawData(String field) {
		Object o = getValue(field);
		if (o instanceof byte[])
			return (byte[]) o;
		if (o instanceof String) {
			try {
				return o.toString().getBytes("UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Retrieves an IMCMesssage from a field (used for inline messages)
	 * 
	 * @param field
	 *            The field whose value is to be returned
	 * @return The IMCMessage in the field or <i>null</i> if the value is not a
	 *         message
	 */
	public IMCMessage getMessage(String field) {
		Object o = getValue(field);
		if (o instanceof IMCMessage)
			return (IMCMessage) o;
		return null;
	}

	/**
	 * Retrieves an IMCMessage from a field and converts it to a chosen type.<br/>
	 * Example:
	 * 
	 * <pre>
	 * PlanSpecification plan = planControl.getMessage(PlanSpecification.class, &quot;arg&quot;);
	 * </pre>
	 * 
	 * @param clazz
	 *            The expected class of the inline message
	 * @param field
	 *            The field corresponding to the inline message
	 * @return The inline message, converted to chosen type
	 */
	public <T extends IMCMessage> T getMessage(Class<T> clazz, String field)
			throws Exception {
		IMCMessage m = getMessage(field);
		if (m == null)
			return null;
		if (!m.getAbbrev().equals(clazz.getSimpleName())) {
			throw new Exception("The inline message type (" + m.getAbbrev()
					+ " doesn't match given type (" + clazz.getSimpleName()
					+ ")");
		}

		return clazz.getConstructor(IMCMessage.class).newInstance(m);
	}

	/**
	 * Retrieve all the messages in a MessageList as a Vector
	 * 
	 * @param field
	 *            The name of the field in this message which is of type
	 *            MessageList
	 * @return all the messages in a MessageList as a Vector
	 */
	public Vector<IMCMessage> getMessageList(String field) {
		Vector<IMCMessage> ret = new Vector<IMCMessage>();

		if (type.getFieldType(field) == IMCFieldType.TYPE_MESSAGE) {
			IMCMessage list = getMessage(field);
			if (list == null)
				return ret;
			while (true) {
				IMCMessage inner = list.getMessage("msg");
				if (inner != null)
					ret.add(inner);
				else
					ret.add(list); // For supporting old ManeuverSpecification,
				// PathPoint, and other messages that had
				// 'next' fields
				list = list.getMessage("next");
				if (list == null)
					break;
			}
		} else if (type.getFieldType(field) == IMCFieldType.TYPE_MESSAGELIST) {
			Object o = getValue(field);

			if (o != null && o instanceof Collection<?>) {
				Collection<?> vec = (Collection<?>) o;
				for (Object ob : vec)
					if (ob == null || ob instanceof IMCMessage)
						ret.add((IMCMessage) ob);
			} else if (o != null && o.getClass().isArray()) {
				int length = Array.getLength(o);
				for (int i = 0; i < length; i++) {
					Object ob = Array.get(o, i);
					if (ob == null || ob instanceof IMCMessage)
						ret.add((IMCMessage) ob);
				}
			}
		}
		return ret;
	}

	/**
	 * This method receives a vector of messages and sets a field with a
	 * MessageList of those messages
	 * 
	 * @param messages
	 *            The messages in the MessageList
	 * @param field
	 *            The field to be set with a list of messages
	 */
	public void setMessageList(Vector<? extends IMCMessage> messages,
			String field) {

		if (type.getFieldType(field) == IMCFieldType.TYPE_MESSAGE) {
			// encode list of messages as a single MessageList message
			IMCMessage first = new IMCMessage("MessageList"), prev = null;
			for (IMCMessage m : messages) {
				if (prev == null) {
					first.setValue("msg", m);
					prev = first;
				} else {
					IMCMessage cur = new IMCMessage("MessageList");
					cur.setValue("msg", m);
					prev.setValue("next", cur);
					prev = cur;
				}
			}
			setValue(field, first);
		} else {
			// hoping it is a message-list field...
			setValue(field, messages);
		}
	}

	/**
	 * Retrieve a message list from this message using a given type
	 * 
	 * @param field
	 *            The field from where to retrieve the message list
	 * @param clazz
	 *            The type of the messages in this list
	 * @return A Vector of typed messages
	 * @throws Exception
	 *             If the messages found do not correspond to expected type
	 */
	@SuppressWarnings("unchecked")
	public <T extends IMCMessage> Vector<T> getMessageList(String field,
			Class<T> clazz) throws Exception {
		Vector<IMCMessage> msgs = getMessageList(field);
		Vector<T> ret = new Vector<T>();

		for (IMCMessage m : msgs) {
			if (m.getClass().isInstance(clazz))
				ret.add((T) m);
			else
				ret.add((T) clazz.getMethod("clone", IMCMessage.class).invoke(
						null, m));
		}
		return ret;
	}

	/**
	 * Returns a String representation of this message (used for debugging)
	 */
	@Override
	public String toString() {

		if (getHeader() != null) {
			StringBuilder sb = new StringBuilder("message (").append(
					getMessageType().getShortName())
					.append(") {\n\theader {\n");

			for (String f : getHeader().getMessageType().getFieldNames()) {
				sb.append("\t\t").append(f).append(": \t").append(getString(f))
				.append('\n');
			}
			sb.append("\t}\n\tpayload {\n");
			for (String f : getMessageType().getFieldNames()) {
				sb.append("\t\t").append(f).append(": \t").append(getString(f))
				.append('\n');
			}
			sb.append("\t}\n}\n");
			return sb.toString();
		} else {
			StringBuilder sb = new StringBuilder("header {\n");

			for (String f : getMessageType().getFieldNames()) {
				sb.append("\t").append(f).append(": \t").append(getString(f))
				.append('\n');
			}
			sb.append("}\n");
			return sb.toString();
		}
	}

	/**
	 * Writes this message to an OutputStream
	 * 
	 * @param out
	 *            The OutputStream to write to
	 * @return The number of bytes written
	 */
	public int serialize(IMCDefinition def, IMCOutputStream out)
			throws IOException {
		int count = 0;
		out.resetCRC();
		if (!getAbbrev().equals("Header")) {
			if (header == null) {
				header = def.createHeader();
			}
			header.set_sync((int) def.syncWord);
			header.set_mgid(type.getId());
			if (getTimestamp() == 0)
				header.set_timestamp(System.currentTimeMillis() / 1000.0);
			header.set_size(getPayloadSize());
			count += header.serialize(def, out); // header
			count += def.serializeFields(this, out); // fields
			def.serialize(out.getCRC(), IMCFieldType.TYPE_UINT16, out); // footer
			return count + 2;
		} else {
			// this message is an header
			return def.serializeFields(this, out);
		}
	}

	/**
	 * Serialize this message to an IMCOutputStream
	 * 
	 * @return The number of bytes writen
	 */
	public int serialize(IMCOutputStream out) throws IOException {
		return serialize(IMCDefinition.getInstance(), out);
	}

	/**
	 * Change the timestamp of this message (stored in the header field "time")
	 * 
	 * @param time
	 *            The new unix time (seconds since 1970)
	 */
	public void setTimestamp(double time) {

		getHeader().set_timestamp(time);

		for (Object o : values.values()) {
			if (o instanceof IMCMessage)
				((IMCMessage) o).setTimestamp(time);
		}
	}

	public void setTimestampMillis(long timestampMillis) {
		setTimestamp(timestampMillis / 1000.0);
	}

	/**
	 * @return The message timestamp in milliseconds
	 */
	public long getTimestampMillis() {
		return (long) (getHeader().get_timestamp() * 1000);
	}

	/**
	 * @return The message timestamp in seconds
	 */
	public double getTimestamp() {
		if (header != null)
			return header.get_timestamp();
		return 0;
	}

	/**
	 * Retrieve the timestamp of this message as a Date
	 */
	public Date getDate() {
		double time = getTimestamp();
		return new Date((long) (time * 1000));
	}

	public Number getAsNumber(String fieldName) {
		Object o = getValue(fieldName);
		if (!(o instanceof Number)) {
			return getDouble(fieldName);
		}
		return (Number) o;
	}

	public String getAsString(String fieldName) {
		Object o = getValue(fieldName);
		if (o == null)
			return null;
		switch (getMessageType().getFieldType(fieldName)) {
		case TYPE_RAWDATA:
			StringBuilder sb = new StringBuilder();
			byte[] buf = (byte[]) o;
			for (int i = 0; i < buf.length; i++) {
				sb.append(String.format("%02X", buf[i]));
			}
			return sb.toString();
		case TYPE_MESSAGE:
			return "%INLINE{"
			+ ((IMCMessage) o).getMessageType().getShortName() + "}";
		case TYPE_MESSAGELIST:
			String ret = "%MESSAGE-LIST[";
			Collection<?> vec = (Collection<?>) o;
			for (Object ob : vec) {
				ret += ((IMCMessage) ob).getMessageType() != null ? ((IMCMessage) ob)
						.getMessageType().getShortName() + ", "
						: "NULL, ";
			}
			if (!vec.isEmpty())
				ret = ret.substring(0, ret.length() - 2);
			return ret + "]";
		default:
			return o.toString();
		}
	}

	public int getMgid() {
		return getMessageType().getId();
	}

	@Override
	public String getAbbrev() {
		return getMessageType().getShortName();
	}

	@Override
	public String getLongName() {
		return getMessageType().getFullName();
	}

	public String[] getFieldNames() {
		return getMessageType().getFieldNames().toArray(new String[0]);
	}

	public String getLongFieldName(String fieldName) {
		return fieldName;
	}

	public String getTypeOf(String fieldName) {
		IMCFieldType type = getMessageType().getFieldType(fieldName);
		return type != null ? type.toString() : null;
	}

	public String getUnitsOf(String fieldName) {
		return getMessageType().getFieldUnits(fieldName);
	}

	public boolean hasFlag(String flagName) {
		return getMessageType().getFlags().contains(flagName);
	}

	public boolean isPeriodic() {
		return hasFlag("periodic");
	}

	public void validate() throws InvalidMessageException {
		// TODO
	}

	public IMessageProtocol<? extends IMessage> getProtocolFactory() {
		if (definitions != null)
			return definitions;
		return IMCDefinition.getInstance();
	}

	/**
	 * Check in this is a null inline message
	 * 
	 * @return <strong>true</strong> if the type of this message is -1
	 */
	public boolean isNull() {
		return getMgid() == -1;
	}

	/**
	 * Write this message as text into the given OutputStream. Example:
	 * 
	 * <pre>
	 * IMCMessage estate = new IMCMessage(&quot;EstimatedState&quot;);
	 * estate.dump(System.out);
	 * </pre>
	 * 
	 * @param err
	 *            Where to write this message to.
	 */
	public void dump(OutputStream err) {
		try {
			err.write(toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected byte[] getBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			serialize(new IMCOutputStream(baos));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	public void hexdump(OutputStream err) {
		byte[] result = getBytes();

		for (int i = 0; i < result.length; i++) {
			System.out.printf("%02X ", result[i]);
		}

	}

	private static IMCMessage parseJsonObject(JsonObject obj) {
		IMCMessage msg = IMCDefinition.getInstance().create(obj.getString("abbrev", null));
		for (Member m : obj) {
			if (m.getName().equals("abbrev"))
				continue;
			
			IMCFieldType type = msg.getMessageType().getFieldType(m.getName());
			if (type == null)
				type = msg.getHeader().getMessageType().getFieldType(m.getName());
			
			switch (type) {
			case TYPE_PLAINTEXT:
				msg.setValue(m.getName(), m.getValue().asString());
				break;
			case TYPE_RAWDATA:
				msg.setValue(m.getName(), Base64.decode(m.getValue().asString()));
				break;
			case TYPE_MESSAGE:
				msg.setValue(m.getName(), parseJsonObject(m.getValue().asObject()));
				break;
			case TYPE_MESSAGELIST:
				JsonArray arr = m.getValue().asArray();
				ArrayList<IMCMessage> msgs = new ArrayList<IMCMessage>();
				for (int i = 0; i < arr.size(); i++) {
					msgs.add(parseJsonObject(arr.get(i).asObject()));
				}
				msg.setValue(m.getName(), msgs);
				break;
			case TYPE_FP32:
				msg.setValue(m.getName(), m.getValue().asFloat());
				break;
			case TYPE_FP64:
				msg.setValue(m.getName(), m.getValue().asDouble());
				break;
			default:
				msg.setValue(m.getName(), m.getValue().asLong());				
				break;
			}
		}
		return msg;
	}

	public static IMCMessage parseJson(String json) {
		JsonObject obj = JsonObject.readFrom(json);
		return parseJsonObject(obj);
	}

	private JsonObject asJsonObject(boolean includeHeader) {
		JsonObject obj = new JsonObject();
		obj.add("abbrev", getMessageType().getShortName());

		if (includeHeader) {
			for (String fieldName : header.getFieldNames()) {
				if (fieldName.equals("mgid") || fieldName.equals("size")
						|| fieldName.equals("sync"))
					continue;
				else if (fieldName.equals("timestamp"))
					obj.add(fieldName, getDouble(fieldName));
				else
					obj.add(fieldName, getLong(fieldName));				
			}
		}

		for (String fieldName : getMessageType().getFieldNames()) {

			IMCFieldType fieldType = getMessageType().getFieldType(fieldName);

			switch (fieldType) {
			case TYPE_PLAINTEXT:
				obj.add(fieldName, getValue(fieldName).toString());
				break;
			case TYPE_RAWDATA:
				byte[] bytes = getRawData(fieldName);
				if (bytes == null) {
					obj.add(fieldName, "");					
				} 
				else {
					obj.add(fieldName, Base64.encode(bytes));
				}
				break;
			case TYPE_MESSAGE:
				IMCMessage msg = null;
				msg = getMessage(fieldName);
				if (msg != null)
					obj.add(fieldName, msg.asJsonObject(false));
				else
					obj.add(fieldName, new JsonObject());
				break;
			case TYPE_MESSAGELIST:
				Vector<IMCMessage> msgs = null;
				msgs = getMessageList(fieldName);
				if (msgs == null)
					msgs = new Vector<IMCMessage>();
				JsonArray arr = new JsonArray();
				for (IMCMessage m : msgs) {
					arr.add(m.asJsonObject(false));
				}
				obj.add(fieldName, arr);
				break;
			case TYPE_FP32:
				obj.add(fieldName, getFloat(fieldName));
				break;
			case TYPE_FP64:
				obj.add(fieldName, getDouble(fieldName));
				break;				
			default:
				obj.add(fieldName, getLong(fieldName));
				break;
			}
		}
		return obj;
	}




	/**
	 * Retrieve this message as a JSON string
	 * 
	 * @return this message as a JSON string
	 */
	public String asJSON() {
		return asJSON(true);
	}
	
	public String asJSON(boolean includeHeader) {		
		return asJsonObject(includeHeader).toString();
	}
	
	public String asXmlStripped(int tabAmount, boolean isInline) {
		StringBuilder sb = new StringBuilder();
		String tabs = "";
		for (int i = 0; i < tabAmount; i++)
			tabs += "  ";
		sb.append(tabs + "<" + getAbbrev());

		if (!isInline) {
			sb.append(" imcv=\"" + definitions.getVersion() + "\"");
			sb.append(" time=\"" + getTimestamp() + "\"");
			sb.append(" src=\"" + getSrc() + "\"");
			sb.append(" dst=\"" + getDst() + "\"");
			sb.append(" src_ent=\"" + getSrcEnt() + "\"");
			sb.append(" dst_ent=\"" + getDstEnt() + "\"");
		}
		sb.append(">\n");
		// tabs += "  ";

		for (String fieldName : getMessageType().getFieldNames()) {
			// sb.append("<" + fieldName + ">");
			switch (getMessageType().getFieldType(fieldName)) {
			case TYPE_FP32:
			case TYPE_FP64:
				double val = getDouble(fieldName);
				if (val != 0)
					sb.append(tabs
							+ "  <"
							+ fieldName
							+ ">"
							+ getString(fieldName, false).replaceAll("\\.0+$",
									".0") + "</" + fieldName + ">\n");
				break;
			case TYPE_INT16:
			case TYPE_INT32:
			case TYPE_INT64:
			case TYPE_INT8:
			case TYPE_UINT16:
			case TYPE_UINT32:
			case TYPE_UINT8:
				long longVal = getLong(fieldName);
				if (longVal != 0)
					sb.append(tabs + "  <" + fieldName + ">"
							+ getString(fieldName, false) + "</" + fieldName
							+ ">\n");
				break;
			case TYPE_PLAINTEXT:
				if (!getString(fieldName).isEmpty())
					sb.append(tabs + "  <" + fieldName + ">"
							+ getString(fieldName, false) + "</" + fieldName
							+ ">\n");
				break;
			case TYPE_MESSAGE:
				IMCMessage msg = getMessage(fieldName);
				if (msg != null)
					sb.append(tabs + "  <" + fieldName + ">\n"
							+ msg.asXmlStripped(tabAmount + 2, true) + tabs
							+ "  </" + fieldName + ">\n");
				break;
			case TYPE_RAWDATA:
				if (getRawData(fieldName) != null)
					sb.append(tabs + "  <" + fieldName + ">\n"
							+ Base64.encode(getRawData(fieldName)) + "  </"
							+ fieldName + ">\n");
				break;
			case TYPE_MESSAGELIST:
				if (!getMessageList(fieldName).isEmpty()) {
					sb.append(tabs + "  <" + fieldName + ">\n");
					for (IMCMessage m : getMessageList(fieldName))
						sb.append(m.asXmlStripped(tabAmount + 2, true));
					sb.append(tabs + "  </" + fieldName + ">\n");
				}
				break;
			}
		}

		sb.append(tabs + "</" + getAbbrev() + ">\n");

		return sb.toString();
	}

	public Map<String, Object> asMap(boolean inner) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

		if (!inner) {
			map.put("timestamp", getTimestamp());
			map.put("src", getSrc());
			map.put("src_ent", getSrcEnt());
			map.put("dst", getDst());
			map.put("dst_ent", getDstEnt());
		}

		for (String fieldName : getMessageType().getFieldNames()) {
			switch (getMessageType().getFieldType(fieldName)) {
			case TYPE_FP32:
			case TYPE_FP64:
			case TYPE_INT16:
			case TYPE_INT32:
			case TYPE_INT64:
			case TYPE_INT8:
			case TYPE_UINT16:
			case TYPE_UINT32:
			case TYPE_UINT8:
			case TYPE_PLAINTEXT:
			case TYPE_RAWDATA:
				map.put(fieldName, getValue(fieldName));
				break;
			case TYPE_MESSAGE:
				IMCMessage innerMsg = getMessage(fieldName);
				if (innerMsg != null)
					map.put(fieldName, innerMsg.asMap(true));
				else
					map.put(fieldName, null);
			case TYPE_MESSAGELIST:
				Vector<Map<String, Object>> msgs = new Vector<Map<String, Object>>();
				for (IMCMessage m : getMessageList(fieldName)) {
					msgs.add(m.asMap(true));
				}
				map.put(fieldName, msgs);
			}
		}

		return map;

	}

	public String asXml(boolean isInline) {
		StringBuilder sb = new StringBuilder();
		if (!isInline)
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

		sb.append("<" + getAbbrev());

		if (!isInline) {
			sb.append(" imcv=\"" + definitions.getVersion() + "\"");
			sb.append(" time=\"" + getTimestamp() + "\"");
			sb.append(" src=\"" + getSrc() + "\"");
			sb.append(" dst=\"" + getDst() + "\"");
			sb.append(" src_ent=\"" + getSrcEnt() + "\"");
			sb.append(" dst_ent=\"" + getDstEnt() + "\"");
		}
		sb.append(">\n");

		for (String fieldName : getMessageType().getFieldNames()) {
			sb.append("<" + fieldName + ">");
			switch (getMessageType().getFieldType(fieldName)) {
			case TYPE_FP32:
			case TYPE_FP64:
				double val = getDouble(fieldName);
				if (val == 0)
					sb.append("0.0");
				else
					sb.append(getString(fieldName));
				break;
			case TYPE_INT16:
			case TYPE_INT32:
			case TYPE_INT64:
			case TYPE_INT8:
			case TYPE_UINT16:
			case TYPE_UINT32:
			case TYPE_UINT8:
			case TYPE_PLAINTEXT:
				sb.append(getString(fieldName, false));
				break;
			case TYPE_MESSAGE:
				IMCMessage msg = getMessage(fieldName);
				if (msg != null)
					sb.append("\n" + msg.asXml(true));
				break;
			case TYPE_RAWDATA:
				if (getRawData(fieldName) != null)
					sb.append(Base64.encode(getRawData(fieldName)));
				break;
			case TYPE_MESSAGELIST:
				sb.append("\n");
				for (IMCMessage m : getMessageList(fieldName))
					sb.append(m.asXml(true));
				break;
			}
			sb.append("</" + fieldName + ">\n");
		}

		sb.append("</" + getAbbrev() + ">\n");

		return sb.toString();
	}

	/**
	 * Compute this message's payload MD5
	 * 
	 * @return The computed MD5 of this message's payload
	 */
	public byte[] payloadMD5() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MessageDigest md = MessageDigest.getInstance("MD5");
			IMCDefinition.getInstance().serializeFields(this,
					new IMCOutputStream(baos));
			md.update(baos.toByteArray());
			return md.digest();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String field, Class<T> type) throws ClassCastException {
		if (type == Object.class)
			return (T) getValue(field);
		else if (type == String.class)
			return (T) getString(field);
		else if (type == Integer.class)
			return (T) (Integer) getInteger(field);
		else if (type == Float.class)
			return (T) (Float) getFloat(field);
		else if (type == byte[].class)
			return (T) (byte[]) getRawData(field);
		else if (type == Double.class)
			return (T) (Double) getDouble(field);
		else if (type == Long.class)
			return (T) (Long) getLong(field);
		else
			return (T) getValue(field);
	}

	protected static IMCMessage parseElement(IMCDefinition defs, Element element) {
		String name = element.getNodeName();
		IMCMessage msg = defs.create(name);

		if (element.getAttributes().getNamedItem("time") != null)
			msg.setTimestamp(Double.parseDouble(element.getAttributes()
					.getNamedItem("time").getTextContent()));

		if (element.getAttributes().getNamedItem("src") != null)
			msg.setSrc(Integer.parseInt(element.getAttributes()
					.getNamedItem("src").getTextContent()));

		if (element.getAttributes().getNamedItem("dst") != null)
			msg.setDst(Integer.parseInt(element.getAttributes()
					.getNamedItem("dst").getTextContent()));

		if (element.getAttributes().getNamedItem("src_ent") != null)
			msg.setSrcEnt(Short.parseShort(element.getAttributes()
					.getNamedItem("src_ent").getTextContent()));

		if (element.getAttributes().getNamedItem("dst_ent") != null)
			msg.setDstEnt(Short.parseShort(element.getAttributes()
					.getNamedItem("dst_ent").getTextContent()));

		NodeList children = element.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element) {
				Element el = (Element) node;
				String field = el.getNodeName();
				if (msg.getTypeOf(field) == null)
					continue;

				switch (msg.getMessageType().getFieldType(field)) {

				case TYPE_FP32:
				case TYPE_FP64:
					msg.setValue(field, Double.parseDouble(el.getTextContent()));
					break;
				case TYPE_INT16:
				case TYPE_INT64:
				case TYPE_INT32:
				case TYPE_INT8:
				case TYPE_UINT16:
				case TYPE_UINT32:
				case TYPE_UINT8:
					if (msg.getMessageType().getFieldMeanings(field) != null)
						msg.setValue(field, el.getTextContent());
					else
						msg.setValue(field, Long.parseLong(el.getTextContent()));
					break;
				case TYPE_PLAINTEXT:
					msg.setValue(field, el.getTextContent());
					break;
				case TYPE_RAWDATA:
					msg.setValue(
							field,
							Base64.decode(el.getTextContent().replaceAll("\n",
									"")));
					break;
				case TYPE_MESSAGE:
					NodeList inner = el.getChildNodes();
					for (int j = 0; j < inner.getLength(); j++) {
						Node nd = inner.item(j);
						if (nd instanceof Element)
							msg.setValue(field,
									parseElement(defs, (Element) nd));
					}
					break;
				case TYPE_MESSAGELIST:
					Vector<IMCMessage> msgs = new Vector<IMCMessage>();
					NodeList innerMsgs = el.getChildNodes();
					for (int k = 0; k < innerMsgs.getLength(); k++) {
						Node nd = innerMsgs.item(k);
						if (nd instanceof Element)
							msgs.add(parseElement(defs, (Element) nd));
					}
					msg.setValue(field, msgs);
					break;
				}
			}
		}
		return msg;

	}

	/**
	 * Parse an IMC-XML file and retrieve the messages found on the file
	 * 
	 * @param xml
	 *            The xml to be parsed
	 * @return The messages that exist in the file
	 * @throws Exception
	 *             Malformed XML, incompatible types, etc
	 */
	public static IMCMessage parseXml(String xml) throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = fac.newDocumentBuilder();
		Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
		return parseElement(IMCDefinition.getInstance(),
				doc.getDocumentElement());
	}

	/**
	 * Natural order uses the timestamp of the message for comparison
	 */
	@Override
	public int compareTo(IMCMessage arg0) {
		double diff = getTimestamp() - arg0.getTimestamp();
		if (diff > 0)
			return 1;
		else if (diff < 0)
			return -1;
		else
			return 0;
	}

	/**
	 * Calculates the difference between current time and the timestamp in this
	 * message
	 * 
	 * @return The difference between current time and the timestamp in this
	 *         message, in seconds
	 */
	public double getAgeInSeconds() {
		return (System.currentTimeMillis() - getTimestampMillis()) / 1000.0;
	}

	/**
	 * This method enforces all values in this message as immutable. Will give
	 * run time exceptions if someone tries to change the values.
	 */
	public void makeImmutable() {
		if (header != null)
			header.makeImmutable();
		values = Collections.unmodifiableMap(values);
	}

	/**
	 * @return the messageInfo
	 */
	public final MessageInfo getMessageInfo() {
		return messageInfo;
	}

	public int serialize(ByteBuffer destination, int offset) {
		destination.position(offset);
		int size = header.serializePayload(destination, offset);
		size += serializePayload(destination, offset + size);
		int crc = IMCUtil.computeCrc16(destination.array(), offset, size, 0);
		destination.putShort((short) crc);
		return size + 2;
	}

	public int serializePayload(ByteBuffer destination, int offset) {
		destination.position(offset);
		return 0;
	}
		
	/**
	 * This method will copy this message to system clipboard (as XML)
	 */
	public void copyToClipoard() {
		StringSelection stringSelection = new StringSelection(asXml(false));
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
	
	/**
	 * This method will try to get a message from the system clipboard
	 * @return The message in the system clipboard. Both IMC-XML and JSON formats are accepted.
	 * @throws Exception In case there is no valid message in the clipboard.
	 */
	public static IMCMessage pasteFromClipoard() throws Exception {
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		String txt = clpbrd.getData(DataFlavor.stringFlavor).toString();
		
		try {
			if (txt.startsWith("<?xml"))
				return IMCMessage.parseXml(txt);
			
			if (txt.startsWith("{"))
				return IMCMessage.parseJson(txt);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new Exception("Invalid clipboard contents");
	}
	
	/**
	 * @param messageInfo
	 *            the messageInfo to set
	 */
	public final void setMessageInfo(MessageInfo messageInfo) {
		this.messageInfo = messageInfo;
	}
	
	/**
	 * Serialize this message to byte array using current IMC definitions
	 * @return Byte array with message serialized
	 */
	public byte[] toByteArray() {
		return toByteArray(IMCDefinition.getInstance());		
	}
	
	/**
	 * Serialize this message to byte array using provided IMC definitions
	 * @param def The IMC definitions to use to serialize the message
	 * @return Byte array with message serialized
	 */
	public byte[] toByteArray(IMCDefinition def) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IMCOutputStream out = new IMCOutputStream(def, baos);
		try {
			out.writeMessage(this);
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}		
		return baos.toByteArray();
	}
}