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
package pt.lsts.imc.def;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.lsts.imc.EstimatedState;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessageType;

public class DefaultProtocolParser extends AbstractProtocolParser {

	String specification = null;
	
	@Override
	public ProtocolDefinition parseDefinitions(InputStream is) throws Exception {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FilterInputStream fis = new FilterInputStream(is) {
			@Override
			public int read() throws IOException {
				int tmp = super.read();
				baos.write(tmp);
				return tmp;
			}

			@Override
			public int read(byte[] b) throws IOException {
				return read(b, 0, b.length);
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				int tmp = super.read(b, off, len);
				if (tmp != -1)
					baos.write(b, off, tmp);
				return tmp;
			}
		};
		is = fis;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(true);

		XPath xPath = XPathFactory.newInstance().newXPath();

		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(is);
		Element root = doc.getDocumentElement();

		// get version
		version = root.getAttributes().getNamedItem("version").getTextContent();

		// get name
		name = root.getAttributes().getNamedItem("name").getTextContent();

		// get sync word
		String swText = (String) xPath.evaluate(
				"header/field[@abbrev='sync']/@value", root,
				XPathConstants.STRING);
		swText = swText.replaceAll("0x", "");
		sync = Integer.parseInt(swText, 16);

		// md5
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		md5 = computeMD5String(bais);

		// get Header definition
		header = parseFields((NodeList) xPath.evaluate("header/field", root,
				XPathConstants.NODESET));
		header.setShortName("Header");

		// get Footer definition
		footer = parseFields((NodeList) xPath.evaluate("footer/field", root,
				XPathConstants.NODESET));

		// Global Enumerations
		NodeList nlist = (NodeList) xPath.evaluate("enumerations", root,
				XPathConstants.NODESET);

		for (int i = 0; i < nlist.getLength(); i++)
			enums.putAll(parseDescriptorSection(nlist.item(i)));

		// Global Bit Fields
		nlist = (NodeList) xPath.evaluate("bitfields|bitmasks", root,
				XPathConstants.NODESET);
		for (int i = 0; i < nlist.getLength(); i++)
			bitfields.putAll(parseDescriptorSection(nlist.item(i)));

		// Messages (first pass)
		nlist = (NodeList) xPath.evaluate("message", root,
				XPathConstants.NODESET);

		for (int i = 0; i < nlist.getLength(); i++) {
			Node msgNode = nlist.item(i);
			IMCMessageType msgType = parseFields(msgNode.getChildNodes());
			msgType.setShortName(msgNode.getAttributes().getNamedItem("abbrev")
					.getTextContent());
			msgType.setFullName(msgNode.getAttributes().getNamedItem("name")
					.getTextContent());
			Node idNode = msgNode.getAttributes().getNamedItem("id");
			if (idNode != null)
				msgType.setId(Integer.parseInt(idNode.getTextContent()));

			Node flagsNode = msgNode.getAttributes().getNamedItem("flags");
			if (flagsNode != null) {
				String[] flgs = flagsNode.getTextContent().split(",");
				for (int j = 0; j < flgs.length; j++)
					msgType.getFlags().add(flgs[j].trim());
			}

			messages.put(msgType.getShortName(), msgType);
		}

		// Message groups definitions
		nlist = (NodeList) xPath.evaluate("message-groups/message-group", root,
				XPathConstants.NODESET);
		for (int i = 0; i < nlist.getLength(); i++) {
			MessageGroupType mg = parseMessageGroup(nlist.item(i));
			messages.put(mg.getAbbrev(), mg.getMsgType());
			for (String s : mg.getSubTypes()) {
				messages.get(s).setSuperType(mg.getMsgType());
			}
		}

		// Messages that extend other messages
		nlist = (NodeList) xPath.evaluate("//message[@extends]", root,
				XPathConstants.NODESET);
		for (int i = 0; i < nlist.getLength(); i++) {
			Node attr = nlist.item(i);
			String superType = attr.getAttributes().getNamedItem("extends").getTextContent();
			String subType = attr.getAttributes().getNamedItem("abbrev").getTextContent();
			messages.get(subType).setSuperType(messages.get(superType));
		}
		
		specification = baos.toString("UTF-8");
		
		return this;
	}

	private MessageGroupType parseMessageGroup(Node nd) {

		MessageGroupType group = new MessageGroupType();
		group.setName(nd.getAttributes().getNamedItem("name").getTextContent());
		group.setAbbrev(nd.getAttributes().getNamedItem("abbrev")
				.getTextContent());

		NodeList subtypeList = nd.getChildNodes();
		IMCMessageType msgType = parseFields(subtypeList);
		msgType.setFullName(group.getName());
		msgType.setShortName(group.getAbbrev());
		group.setMsgType(msgType);

		for (int i = 0; i < subtypeList.getLength(); i++) {
			Node tmp = subtypeList.item(i);
			if (tmp.getNodeName().equals("message-type")) {
				group.getSubTypes().add(
						tmp.getAttributes().getNamedItem("abbrev")
								.getTextContent());
			}
		}

		return group;
	}

	private LinkedHashMap<String, ValueDescriptor> parseDescriptorSection(
			Node descNode) {
		LinkedHashMap<String, ValueDescriptor> ret = new LinkedHashMap<String, ValueDescriptor>();
		
		
		if (descNode.getAttributes() == null)
			return ret;

		NodeList bitDefs = descNode.getChildNodes();
		for (int j = 0; j < bitDefs.getLength(); j++) {
			Node def = bitDefs.item(j);
			if (!def.getNodeName().equals("def"))
				continue;
			ValueDescriptor descriptor = readValueDescriptor(def);
			ret.put(descriptor.getAbbrev(), descriptor);
		}
		
		return ret;
	}

	private IMCMessageType parseFields(NodeList fields) {
		return parseFields(null, fields);
	}

	private IMCMessageType parseFields(IMCMessageType supertype, NodeList fields) {

		IMCMessageType msgType = new IMCMessageType();

		if (supertype != null) {
			msgType = new IMCMessageType(supertype);
		}

		for (int i = 0; i < fields.getLength(); i++) {
			Node field = fields.item(i);
			if (field.getNodeName().equals("description"))
				msgType.setMessageDescription(field.getTextContent());
			if (!field.getNodeName().equals("field"))
				continue;
			NamedNodeMap attrs = field.getAttributes();
			String fieldAbbrv = attrs.getNamedItem("abbrev").getTextContent();
			String fieldName = attrs.getNamedItem("name").getTextContent();
			String type = attrs.getNamedItem("type").getTextContent();

			String unit = null;// attrs.getNamedItem("unit").getTextContent();
			Node unitNd = attrs.getNamedItem("unit");
			if (unitNd != null)
				unit = unitNd.getTextContent();

			String subtype = null;
			Node subtNd = attrs.getNamedItem("subtype");
			if (subtNd != null)
				subtype = subtNd.getTextContent();

			subtNd = attrs.getNamedItem("message-type");
			if (subtNd != null)
				subtype = subtNd.getTextContent();

			String minVal = null;
			Node minNd = attrs.getNamedItem("min");
			if (minNd != null)
				minVal = minNd.getTextContent();

			String maxVal = null;
			Node maxNd = attrs.getNamedItem("max");
			if (maxNd != null)
				maxVal = maxNd.getTextContent();

			msgType.addField(fieldAbbrv, type, unit, minVal, maxVal);
			msgType.setFieldName(fieldAbbrv, fieldName);

			if (subtype != null) {
				msgType.setFieldSubtype(fieldAbbrv, subtype);
			}
			if (unit == null)
				continue;

			if (unit.equalsIgnoreCase("enumerated")
					|| unit.equalsIgnoreCase("bitmask")
					|| unit.equalsIgnoreCase("bitfield")) {

				LinkedHashMap<Long, String> possibleValues = new LinkedHashMap<Long, String>();
				String prefix = null;

				if (field.getAttributes().getNamedItem("enum-def") != null) {
					String enumName = field.getAttributes()
							.getNamedItem("enum-def").getTextContent();

					prefix = enums.get(enumName).getPrefix();
					possibleValues.putAll(enums.get(enumName).getValues());
				} else if (field.getAttributes().getNamedItem("bitmask-def") != null) {
					String bfName = field.getAttributes()
							.getNamedItem("bitmask-def").getTextContent();

					prefix = bitfields.get(bfName).getPrefix();
					possibleValues.putAll(bitfields.get(bfName).getValues());
				} else if (field.getAttributes().getNamedItem("bitfield-def") != null) {
					String bfName = field.getAttributes()
							.getNamedItem("bitfield-def").getTextContent();

					prefix = bitfields.get(bfName).getPrefix();
					possibleValues.putAll(bitfields.get(bfName).getValues());
				} else {
					ValueDescriptor descriptor = readValueDescriptor(field);
					prefix = descriptor.getPrefix();
					possibleValues.putAll(descriptor.getValues());
				}
				if (prefix != null)
					msgType.setFieldPrefix(fieldAbbrv, prefix);
				msgType.setFieldPossibleValues(fieldAbbrv, possibleValues);							
			}			
		}
		return msgType;
	}

	private ValueDescriptor readValueDescriptor(Node node) {

		ValueDescriptor desc = new ValueDescriptor();
		NodeList inner = node.getChildNodes();

		if (node.getAttributes().getNamedItem("prefix") != null)
			desc.setPrefix(node.getAttributes().getNamedItem("prefix")
					.getTextContent());

		if (node.getAttributes().getNamedItem("name") != null)
			desc.setName(node.getAttributes().getNamedItem("name")
					.getTextContent());

		if (node.getAttributes().getNamedItem("abbrev") != null)
			desc.setAbbrev(node.getAttributes().getNamedItem("abbrev")
					.getTextContent());

		for (int j = 0; j < inner.getLength(); j++) {

			Node n = inner.item(j);

			if (n.getNodeName().equals("enum")
					|| n.getNodeName().equals("bitmask")
					|| n.getNodeName().equals("value")) {
				String idEl = n.getAttributes().getNamedItem("id")
						.getTextContent();
				String abbrev = n.getAttributes().getNamedItem("abbrev")
						.getTextContent();

				long val = 0;
				if (!idEl.contains("x")) {
					val = Long.parseLong(idEl);
				} else {
					idEl = idEl.substring(idEl.indexOf('x') + 1);
					val = Long.parseLong(idEl, 16);
				}
				desc.getValues().put(val, abbrev);
			}
		}

		return desc;
	}

	/**
	 * @return the specification
	 */
	public String getSpecification() {
		return specification;
	}

	public static void main(String[] args) throws Exception {
		IMCDefinition.getInstance().create(EstimatedState.class);
		for (String name : IMCDefinition.getInstance().getMessageNames()) {
			if (IMCDefinition.getInstance().getType(name).isAbstract())
			System.out.println(name);
		}
		
	}
}
