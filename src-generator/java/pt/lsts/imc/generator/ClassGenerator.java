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
 *  
 * $Id:: ClassGenerator.java 393 2013-03-03 10:40:48Z zepinto@gmail.com        $:
 */
package pt.lsts.imc.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCFieldType;
import pt.lsts.imc.IMCMessageType;

/**
 * This class generates IMCMessage subclasses that ease sending and receiving
 * IMC messages
 * 
 * @author zp
 * 
 */
public class ClassGenerator {

	public static void generateImcFactory(IMCDefinition definitions,
			File outputFolder) throws Exception {
		File outputDir = getOutputDir(outputFolder, "pt.lsts.imc");
		File outputFile = new File(outputDir, "MessageFactory.java");

		// BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "UTF-8"));

		bw.write(getCopyRightHeader());

		bw.write("package pt.lsts.imc;\n\n");

		bw.write("public class MessageFactory {\n\n");

		bw.write("\tprivate static MessageFactory instance = null;\n\n");

		bw.write("\tprivate MessageFactory() {}\n\n");

		bw.write("\tpublic static MessageFactory getInstance() {\n\n");
		bw.write("\t\t if (instance == null)\n");
		bw.write("\t\t\tinstance = new MessageFactory();\n\n");
		bw.write("\t\treturn instance;\n\t}\n\n");

		bw.write("\tpublic IMCMessage createTypedMessage(String msgName, IMCDefinition defs) {\n");
		bw.write("\t\tint msgId = defs.getMessageId(msgName);\n");
		bw.write("\t\treturn createTypedMessage(msgId, defs);\n");
		bw.write("\t}\n");

		bw.write("\tprivate IMCMessage createTypedMessage(int mgid, IMCDefinition defs) {\n\n");

		bw.write("\t\tswitch(mgid) {\n");
		for (String msg : definitions.getConcreteMessages()) {
			bw.write("\t\t\tcase " + msg + ".ID_STATIC:\n");
			bw.write("\t\t\t\treturn new " + msg + "(defs);\n");
		}

		bw.write("\t\t\tdefault:\n");
		bw.write("\t\t\t\treturn new IMCMessage(defs);\n");

		bw.write("\t\t}\n");
		bw.write("\t}\n");
		bw.write("}\n");

		bw.close();
	}

	@Deprecated
	public static void generateImcState(IMCDefinition definitions,
			File outputFolder) throws Exception {
		File outputDir = getOutputDir(outputFolder, "pt.lsts.imc.state");
		File outputFile = new File(outputDir, "ImcSysState.java");

		// BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "UTF-8"));

		bw.write(getCopyRightHeader());

		bw.write("package pt.lsts.imc.state;\n\n");
		bw.write("import pt.lsts.imc.*;\n\n");

		bw.write("public class ImcSysState extends ImcSystemState {\n\n");

		bw.write("\tpublic ImcSysState() {\n");
		bw.write("\t\tsuper(IMCDefinition.getInstance());\n");
		bw.write("\t}\n\n");

		for (String msg : definitions.getMessageNames()) {

			IMCMessageType msgType = definitions.getType(msg);
			if (msgType.isAbstract())
				continue;
			bw.write("\t/**\n");
			bw.write("\t * Retrieve the last {@link "
					+ msg
					+ "} or <b>null</b> if no message of that type was received yet.\n");
			bw.write("\t * @return last {@link "
					+ msg
					+ "} on this state or <b>null</b> if no message of that type was received yet.\n");
			bw.write("\t */\n");
			bw.write("\tpublic pt.lsts.imc." + msg + " last" + msg + "() {\n");
			bw.write("\t\tIMCMessage m = get(\"" + msg + "\");\n");
			bw.write("\t\tif (m != null)\n");
			bw.write("\t\t\ttry {\n");
			bw.write("\t\t\t\t" + msg + " ms = new " + msg + "();\n");
			bw.write("\t\t\t\tms.copyFrom(m);\n");
			bw.write("\t\t\t\treturn ms;\n");
			bw.write("\t\t\t}\n");
			bw.write("\t\t\tcatch (Exception e) { }\n");
			bw.write("\t\treturn null;\n");
			bw.write("\t}\n\n");

			bw.write("\t/**\n");
			bw.write("\t * Retrieve the last {@link "
					+ msg
					+ "} generated by the given entityName or <b>null</b> if no such message was received yet.\n");
			bw.write("\t * @param entityName The name of the entity that generated the message\n");
			bw.write("\t * @return last {@link "
					+ msg
					+ "} on this state or <b>null</b> if no message of that type was received yet.\n");
			bw.write("\t */\n");
			bw.write("\tpublic pt.lsts.imc." + msg + " last" + msg
					+ "(String entityName) {\n");
			bw.write("\t\tIMCMessage m = get(pt.lsts.imc." + msg
					+ ".ID_STATIC, entitiesInverted.get(entityName));\n");
			bw.write("\t\tif (m != null)\n");
			bw.write("\t\t\ttry {\n");
			bw.write("\t\t\t\t" + msg + " ms = new " + msg + "();\n");
			bw.write("\t\t\t\tms.copyFrom(m);\n");
			bw.write("\t\t\t\treturn ms;\n");
			bw.write("\t\t\t}\n");
			bw.write("\t\t\tcatch (Exception e) { }\n");
			bw.write("\t\treturn null;\n");
			bw.write("\t}\n\n");

			bw.write("\t/**\n");
			bw.write("\t * Wait and retrieve the next {@link " + msg + "}.\n");
			bw.write("\t * @param timeoutMillis Maximum ammount of time to block, in milliseconds.\n");
			bw.write("\t * @return The received {@link "
					+ msg
					+ "} or <b>null</b> if no such message was received for <strong>timeoutMillis</strong> milliseconds\n");
			bw.write("\t */\n");
			bw.write("\tpublic pt.lsts.imc." + msg + " poll" + msg
					+ "(long timeoutMillis) {\n");
			bw.write("\t\tIMCMessage m = poll(\"" + msg
					+ "\", timeoutMillis);\n");
			bw.write("\t\tif (m != null)\n");
			bw.write("\t\t\ttry {\n");
			bw.write("\t\t\t\t" + msg + " ms = new " + msg + "();\n");
			bw.write("\t\t\t\tms.copyFrom(m);\n");
			bw.write("\t\t\t\treturn ms;\n");
			bw.write("\t\t\t}\n");
			bw.write("\t\t\tcatch (Exception e) { }\n");
			bw.write("\t\treturn null;\n");
			bw.write("\t}\n\n");

			bw.write("\t/**\n");
			bw.write("\t * Wait and retrieve the next {@link " + msg
					+ "} generated by given entity.\n");
			bw.write("\t * @param entityName The name of the generating entity.\n");
			bw.write("\t * @param timeoutMillis Maximum ammount of time to block, in milliseconds.\n");
			bw.write("\t * @return The received {@link "
					+ msg
					+ "} or <b>null</b> if no such message was received for <strong>timeoutMillis</strong> milliseconds\n");
			bw.write("\t */\n");
			bw.write("\tpublic pt.lsts.imc." + msg + " poll" + msg
					+ "(String entityName, long timeoutMillis) {\n");
			bw.write("\t\tIMCMessage m = poll(\"" + msg
					+ "\", entityName, timeoutMillis);\n");
			bw.write("\t\tif (m != null)\n");
			bw.write("\t\t\ttry {\n");
			bw.write("\t\t\t\t" + msg + " ms = new " + msg + "();\n");
			bw.write("\t\t\t\tms.copyFrom(m);\n");
			bw.write("\t\t\t\treturn ms;\n");
			bw.write("\t\t\t}\n");
			bw.write("\t\t\tcatch (Exception e) { }\n");
			bw.write("\t\treturn null;\n");
			bw.write("\t}\n\n");
		}

		bw.write("}\n");
		bw.close();
	}

	public static String getCopyRightHeader() {
		StringBuilder sb = new StringBuilder();

		sb.append("/*\n");
		sb.append(" * Below is the copyright agreement for IMCJava.\n");
		sb.append(" * \n");
		sb.append(" * Copyright (c) 2010-2018, Laboratório de Sistemas e Tecnologia Subaquática\n");
		sb.append(" * All rights reserved.\n");
		sb.append(" * \n");
		sb.append(" * Redistribution and use in source and binary forms, with or without\n");
		sb.append(" * modification, are permitted provided that the following conditions are met:\n");
		sb.append(" *     - Redistributions of source code must retain the above copyright\n");
		sb.append(" *       notice, this list of conditions and the following disclaimer.\n");
		sb.append(" *     - Redistributions in binary form must reproduce the above copyright\n");
		sb.append(" *       notice, this list of conditions and the following disclaimer in the\n");
		sb.append(" *       documentation and/or other materials provided with the distribution.\n");
		sb.append(" *     - Neither the names of IMC, LSTS, IMCJava nor the names of its \n");
		sb.append(" *       contributors may be used to endorse or promote products derived from \n");
		sb.append(" *       this software without specific prior written permission.\n");
		sb.append(" * \n");
		sb.append(" * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n");
		sb.append(" * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n");
		sb.append(" * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n");
		sb.append(" * DISCLAIMED. IN NO EVENT SHALL LABORATORIO DE SISTEMAS E TECNOLOGIA SUBAQUATICA\n");
		sb.append(" * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n");
		sb.append(" * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE \n");
		sb.append(" * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) \n");
		sb.append(" * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT \n");
		sb.append(" * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT \n");
		sb.append(" * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n");
		sb.append(" * \n");
		sb.append(" */\n");

		return sb.toString();
	}

	public static void generateStringDefinitions(String packageName,
			Map<String, Integer> addresses, String sha, String branch,
			String commitDetails, File outputFolder)
					throws Exception {
		File outputDir = getOutputDir(outputFolder, packageName);
		File outputFile = new File(outputDir, "ImcStringDefs.java");

		// BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "UTF-8"));

		bw.write(getCopyRightHeader());

		bw.write("package pt.lsts.imc;\n\n");

		bw.write("public class ImcStringDefs {\n\n");

		// GIT COMMIT DETAILS

		bw.write("\tpublic static final String IMC_SHA = \"" + sha + "\";\n");
		bw.write("\tpublic static final String IMC_BRANCH = \"" + branch
				+ "\";\n");
		bw.write("\tpublic static final String IMC_COMMIT = \"" + commitDetails
				+ "\";\n\n");

		// IMC_STATIC_ADDRESSES
		bw.write("\tpublic static java.util.Map<String, Integer> IMC_ADDRESSES = new java.util.LinkedHashMap<String, Integer>();\n\n");

		bw.write("\tstatic {\n");

		for (Entry<String, Integer> entry : addresses.entrySet())
			bw.write("\t\tIMC_ADDRESSES.put(\"" + entry.getKey() + "\", "
					+ entry.getValue() + ");\n");
		bw.write("\n\t\tIMC_ADDRESSES = java.util.Collections.unmodifiableMap(IMC_ADDRESSES);\n");
		bw.write("\t}\n\n");

		bw.write("\tpublic static String getDefinitions() {\n\n");
		bw.write("\t\tjava.io.InputStream xmlStream = ImcStringDefs.class.getResourceAsStream(\"/xml/IMC.xml\");\n");
		bw.write("\t\tjava.io.InputStreamReader isreader = new java.io.InputStreamReader(xmlStream);\n");
		bw.write("\t\tjava.io.BufferedReader reader = new java.io.BufferedReader(isreader);\n");
		bw.write("\t\tjava.lang.StringBuilder builder = new java.lang.StringBuilder();\n");
		bw.write("\t\tString line = null;\n");		
		bw.write("\n");
		bw.write("\t\ttry {\n");
		bw.write("\t\t\twhile ((line = reader.readLine()) != null)\n");
		bw.write("\t\t\t\tbuilder.append(line+\"\\n\");\n");
		bw.write("\t\t} catch (java.lang.Exception e) {\n");
		bw.write("\t\t\te.printStackTrace();\n");
		bw.write("\t\t\treturn null;\n");
		bw.write("\t\t}\n");
		bw.write("\n");
		bw.write("\t\treturn builder.toString();\n");
		bw.write("\t}\n}\n");
		bw.close();
	}

	public static void generateClasses() throws Exception {
		generateClasses("pt.lsts.imc", new File("src-generated"),
				IMCDefinition.getInstance());
	}

	public static void generateClasses(String packageName, File outputFolder,
			IMCDefinition definitions) throws Exception {

		for (File f : outputFolder.listFiles()) {
			f.delete();
		}
		outputFolder.delete();

		generateHeader(packageName, outputFolder, definitions);
		
		generateGlobalDefinitions(packageName, outputFolder, definitions);

		for (String msg : definitions.getMessageNames())
			generateClass(packageName, outputFolder, definitions, msg);

	}

	protected static String capitalize(String fieldName) {
		String capitalizedField = "_" + fieldName;
		String str = "";
		for (int j = 0; j < capitalizedField.length(); j++) {
			if (capitalizedField.charAt(j) == '_') {
				j++;
				str += Character.toUpperCase(capitalizedField.charAt(j));
			} else {
				str += capitalizedField.charAt(j);
			}
		}
		return str;
	}

	protected static String generateFullConstructor(IMCDefinition defs,
			String msgName) throws Exception {

		StringBuilder sb = new StringBuilder();
		IMCMessageType type = defs.getType(msgName);

		Vector<String> fields = new Vector<String>();
		fields.addAll(type.getFieldNames());

		if (fields.isEmpty())
			return "";

		if (fields.size() == 1) {
			IMCFieldType fieldType = type.getFieldType(fields.firstElement());
			if (fieldType == IMCFieldType.TYPE_MESSAGE
					&& type.getFieldSubtype(fields.firstElement()) == null)
				return "";
		}

		sb.append("\tpublic ").append(msgName).append("(");
		sb.append(imcTypeToJava(type, fields.firstElement())).append(" ")
		.append(fields.firstElement());

		for (int i = 1; i < fields.size(); i++) {
			sb.append(", ").append(imcTypeToJava(type, fields.get(i)))
			.append(" ").append(fields.get(i));
		}

		sb.append(") {\n\t\tsuper(ID_STATIC);\n");
		for (int i = 0; i < fields.size(); i++) {
			IMCFieldType fieldType = type.getFieldType(fields.get(i));

			switch (fieldType) {
			case TYPE_MESSAGE:
			case TYPE_PLAINTEXT:
			case TYPE_MESSAGELIST:
			case TYPE_RAWDATA:
				sb.append("\t\tif (" + fields.get(i) + " != null)\n\t");
			default:
				sb.append("\t\tset").append(capitalize(fields.get(i)))
				.append("(").append(fields.get(i)).append(");\n");

			}
		}
		sb.append("\t}\n\n");
		return sb.toString();
	}

	protected static String imcTypeToJava(IMCMessageType type, String field) {
		IMCFieldType imcType = type.getFieldType(field);

		if ("enumerated".equals(type.getFieldUnits(field))) {
			String valueDef = type.getFieldValueDefs(field);		
			String capField = valueDef != null? valueDef : field.toUpperCase();
			return capField;
		}

		switch (imcType) {
		case TYPE_INT8:
			return "byte";
		case TYPE_UINT8:
		case TYPE_INT16:
			return "short";
		case TYPE_UINT16:
		case TYPE_INT32:
			return "int";
		case TYPE_FP32:
			return "float";
		case TYPE_FP64:
			return "double";
		case TYPE_INT64:
		case TYPE_UINT32:
			return "long";
		case TYPE_PLAINTEXT:
			return "String";
		case TYPE_RAWDATA:
			return "byte[]";
		case TYPE_MESSAGE:
			if (type.getFieldSubtype(field) == null) {
				return "IMCMessage";
			} else {
				return type.getFieldSubtype(field);
			}
		case TYPE_MESSAGELIST:
			if (type.getFieldSubtype(field) == null) {
				return "java.util.Collection<IMCMessage>";
			} else {
				return "java.util.Collection<" + type.getFieldSubtype(field)
				+ ">";
			}

		}
		return "Object";
	}

	protected static String generateSetters(IMCMessageType type, String field,
			boolean camelCase) {
		String capitalizedField = "_" + field;
		if (camelCase) {
			String str = "";
			for (int i = 0; i < capitalizedField.length(); i++) {
				if (capitalizedField.charAt(i) == '_') {
					i++;
					str += Character.toUpperCase(capitalizedField.charAt(i));
				} else {
					str += capitalizedField.charAt(i);
				}
			}
			capitalizedField = str;
		}

		StringBuilder sb = new StringBuilder();

		if (type.getFieldUnits(field) != null)
			sb.append("\t/**\n\t *  @param " + field + " "
					+ type.getFullFieldName(field) + " ("
					+ type.getFieldUnits(field) + ")");
		else
			sb.append("\t/**\n\t *  @param " + field + " "
					+ type.getFullFieldName(field));
		sb.append("\n\t */\n");
		switch (type.getFieldType(field)) {
		case TYPE_INT8:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(" + capField + " " + field
						+ ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field
						+ ".value());\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (as a String)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "Str(String " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (integer value)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "Val(byte " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(byte " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_UINT8:
		case TYPE_INT16:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(" + capField + " " + field
						+ ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field
						+ ".value());\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (as a String)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "Str(String " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (integer value)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "Val(short " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(short " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_UINT16:
		case TYPE_INT32:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(" + capField + " " + field
						+ ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field
						+ ".value());\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (as a String)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "Str(String " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (integer value)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "Val(int " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(int " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_UINT32:
		case TYPE_INT64:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(" + capField + " " + field
						+ ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field
						+ ".value());\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (as a String)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(String " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");

				sb.append("\t/**\n\t *  @param " + field + " "
						+ type.getFullFieldName(field)
						+ " (integer value)\n\t */\n");
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(long " + field + ") {\n");
				sb.append("\t\tsetValue(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(long " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_FP32:
		case TYPE_FP64:
			sb.append("\tpublic " + type.getShortName() + " set"
					+ capitalizedField + "(double " + field + ") {\n");
			sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
			sb.append("\t\treturn this;\n");
			sb.append("\t}\n\n");
			break;
		case TYPE_PLAINTEXT:
			if (("" + type.getFieldUnits(field)).equalsIgnoreCase("tuplelist")) {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField
						+ "(java.util.LinkedHashMap<String, ?> " + field
						+ ") {\n");
				sb.append("\t\tString val = encodeTupleList(" + field + ");\n");
				sb.append("\t\tvalues.put(\"" + field + "\", val);\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			sb.append("\tpublic " + type.getShortName() + " set"
					+ capitalizedField + "(String " + field + ") {\n");
			sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
			sb.append("\t\treturn this;\n");
			sb.append("\t}\n\n");
			break;
		case TYPE_RAWDATA:
			sb.append("\tpublic " + type.getShortName() + " set"
					+ capitalizedField + "(byte[] " + field + ") {\n");
			sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
			sb.append("\t\treturn this;\n");
			sb.append("\t}\n\n");
			break;
		case TYPE_MESSAGE:
			if (type.getFieldSubtype(field) != null) {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(" + type.getFieldSubtype(field)
						+ " " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(IMCMessage " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_MESSAGELIST:
			if (type.getFieldSubtype(field) != null) {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField + "(java.util.Collection<"
						+ type.getFieldSubtype(field) + "> " + field + ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic " + type.getShortName() + " set"
						+ capitalizedField
						+ "(java.util.Collection<IMCMessage> " + field
						+ ") {\n");
				sb.append("\t\tvalues.put(\"" + field + "\", " + field + ");\n");
				sb.append("\t\treturn this;\n");
				sb.append("\t}\n\n");
			}
			break;

		default:
			System.err.println("Setter for unknown field was not generated: "
					+ type.getFieldType(field));
			break;
		}

		return sb.toString();
	}

	protected static String generateGetters(IMCMessageType type, String field,
			boolean camelCase) {

		String capitalizedField = "_" + field;
		if (camelCase) {
			String str = "";
			for (int i = 0; i < capitalizedField.length(); i++) {
				if (capitalizedField.charAt(i) == '_') {
					i++;
					str += Character.toUpperCase(capitalizedField.charAt(i));
				} else {
					str += capitalizedField.charAt(i);
				}
			}
			capitalizedField = str;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\t/**\n");

		if (type.getFieldDescription(field) != null) {

			String desc = DescriptionToHtml.descToHtml(type
					.getFieldDescription(field));
			String lines[] = desc.split("\n");
			for (String line : lines) {
				if (!line.trim().isEmpty())
					sb.append("\t *  " + line.trim() + "<br/>\n");
			}
		}
		if (type.getFieldUnits(field) != null)
			sb.append("\t *  @return " + type.getFullFieldName(field) + " ("
					+ type.getFieldUnits(field) + ")");
		else
			sb.append("\t *  @return " + type.getFullFieldName(field));
		sb.append(" - " + type.getFieldType(field) + "\n\t */\n");

		
		
		switch (type.getFieldType(field)) {
		case TYPE_INT8:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String defName = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + defName + " get" + capitalizedField
						+ "() {\n");
				sb.append("\t\ttry {\n");
				sb.append("\t\t\t" + defName + " o = " + defName
						+ ".valueOf(getMessageType().getFieldPossibleValues(\""
						+ field + "\").get(getLong(\"" + field + "\")));\n");
				sb.append("\t\t\treturn o;\n");
				sb.append("\t\t}\n");
				sb.append("\t\tcatch (Exception e) {\n");
				sb.append("\t\t\treturn null;\n");
				sb.append("\t\t}\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic String get" + capitalizedField
						+ "Str() {\n");
				sb.append("\t\treturn getString(\"" + field + "\");\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic byte get" + capitalizedField
						+ "Val() {\n");
				sb.append("\t\treturn (byte) getInteger(\"" + field + "\");\n");
				sb.append("\t}\n\n");

			} else {
				sb.append("\tpublic byte get" + capitalizedField + "() {\n");
				sb.append("\t\treturn (byte) getInteger(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_UINT8:
		case TYPE_INT16:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + capField + " get" + capitalizedField
						+ "() {\n");
				sb.append("\t\ttry {\n");
				sb.append("\t\t\t" + capField + " o = " + capField
						+ ".valueOf(getMessageType().getFieldPossibleValues(\""
						+ field + "\").get(getLong(\"" + field + "\")));\n");
				sb.append("\t\t\treturn o;\n");
				sb.append("\t\t}\n");
				sb.append("\t\tcatch (Exception e) {\n");
				sb.append("\t\t\treturn null;\n");
				sb.append("\t\t}\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic String get" + capitalizedField
						+ "Str() {\n");
				sb.append("\t\treturn getString(\"" + field + "\");\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic short get" + capitalizedField
						+ "Val() {\n");
				sb.append("\t\treturn (short) getInteger(\"" + field + "\");\n");
				sb.append("\t}\n\n");

			} else {
				sb.append("\tpublic short get" + capitalizedField + "() {\n");
				sb.append("\t\treturn (short) getInteger(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_UINT16:
		case TYPE_INT32:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + capField + " get" + capitalizedField
						+ "() {\n");
				sb.append("\t\ttry {\n");
				sb.append("\t\t\t" + capField + " o = " + capField
						+ ".valueOf(getMessageType().getFieldPossibleValues(\""
						+ field + "\").get(getLong(\"" + field + "\")));\n");
				sb.append("\t\t\treturn o;\n");
				sb.append("\t\t}\n");
				sb.append("\t\tcatch (Exception e) {\n");
				sb.append("\t\t\treturn null;\n");
				sb.append("\t\t}\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic String get" + capitalizedField
						+ "Str() {\n");
				sb.append("\t\treturn getString(\"" + field + "\");\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic int get" + capitalizedField
						+ "Val() {\n");
				sb.append("\t\treturn getInteger(\"" + field + "\");\n");
				sb.append("\t}\n\n");

			} else {
				sb.append("\tpublic int get" + capitalizedField + "() {\n");
				sb.append("\t\treturn getInteger(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_UINT32:
		case TYPE_INT64:
			if (("" + type.getFieldUnits(field)).equals("enumerated")) {
				String valueDef = type.getFieldValueDefs(field);		
				String capField = valueDef != null? valueDef : field.toUpperCase();
				sb.append("\tpublic " + capField + " get" + capitalizedField
						+ "() {\n");
				sb.append("\t\ttry {\n");
				sb.append("\t\t\t" + capField + " o = " + capField
						+ ".valueOf(getMessageType().getFieldPossibleValues(\""
						+ field + "\").get(getLong(\"" + field + "\")));\n");
				sb.append("\t\t\treturn o;\n");
				sb.append("\t\t}\n");
				sb.append("\t\tcatch (Exception e) {\n");
				sb.append("\t\t\treturn null;\n");
				sb.append("\t\t}\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic long get" + capitalizedField + "() {\n");
				sb.append("\t\treturn getLong(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_FP32:
		case TYPE_FP64:
			sb.append("\tpublic double get" + capitalizedField + "() {\n");
			sb.append("\t\treturn getDouble(\"" + field + "\");\n");
			sb.append("\t}\n\n");
			break;
		case TYPE_PLAINTEXT:
			if (("" + type.getFieldUnits(field)).equalsIgnoreCase("TupleList")) {
				sb.append("\tpublic java.util.LinkedHashMap<String, String> get"
						+ capitalizedField + "() {\n");
				sb.append("\t\treturn getTupleList(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			} else {
				sb.append("\tpublic String get" + capitalizedField + "() {\n");
				sb.append("\t\treturn getString(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_RAWDATA:
			sb.append("\tpublic byte[] get" + capitalizedField + "() {\n");
			sb.append("\t\treturn getRawData(\"" + field + "\");\n");
			sb.append("\t}\n\n");
			break;
		case TYPE_MESSAGE:
			if (type.getFieldSubtype(field) == null) {
				sb.append("\tpublic IMCMessage get" + capitalizedField
						+ "() {\n");
				sb.append("\t\treturn getMessage(\"" + field + "\");\n");
				sb.append("\t}\n\n");

				sb.append("\tpublic <T extends IMCMessage> T get"
						+ capitalizedField
						+ "(Class<T> clazz) throws Exception {\n");
				sb.append("\t\treturn getMessage(clazz, \"" + field + "\");\n");
				sb.append("\t}\n\n");
			} else {
				String subtype = type.getFieldSubtype(field);
				sb.append("\tpublic " + subtype + " get" + capitalizedField
						+ "() {\n");
				sb.append("\t\ttry {\n");
				sb.append("\t\t\tIMCMessage obj = getMessage(\"" + field
						+ "\");\n");
				sb.append("\t\t\tif (obj instanceof " + subtype + ")\n");
				sb.append("\t\t\t\treturn (" + subtype + ") obj;\n");
				sb.append("\t\t\telse\n");
				sb.append("\t\t\t\treturn null;\n");
				sb.append("\t\t}\n");
				sb.append("\t\tcatch (Exception e) {\n");
				sb.append("\t\t\treturn null;\n");
				sb.append("\t\t}\n\n");
				sb.append("\t}\n\n");
			}
			break;
		case TYPE_MESSAGELIST:
			if (type.getFieldSubtype(field) == null) {
				sb.append("\tpublic java.util.Vector<IMCMessage> get"
						+ capitalizedField + "() {\n");
				sb.append("\t\treturn getMessageList(\"" + field + "\");\n");
				sb.append("\t}\n\n");
			} else {
				String subtype = type.getFieldSubtype(field);
				sb.append("\tpublic java.util.Vector<" + subtype + "> get"
						+ capitalizedField + "() {\n");
				sb.append("\t\ttry {\n");
				sb.append("\t\t\treturn getMessageList(\"" + field + "\", "
						+ subtype + ".class);\n");
				sb.append("\t\t}\n");
				sb.append("\t\tcatch (Exception e) {\n");
				sb.append("\t\t\treturn null;\n");
				sb.append("\t\t}\n\n");
				sb.append("\t}\n\n");
			}
		default:
			break;
		}

		return sb.toString();
	}
	
	protected static void generateGlobalDefinitions(String packageName, File outputFolder,
			IMCDefinition defs) throws Exception {

//		for (String name : defs.getGlobalBitfieldPrefixes().keySet()) {
//			LinkedHashMap<Long, String> values = defs.getGlobalBitfields().get(name);
//			generateEnumDef(packageName+".def", outputFolder, name, values);
//		}
		
		for (String name : defs.getGlobalEnumerations().keySet()) {
			LinkedHashMap<Long, String> values = defs.getGlobalEnumerations().get(name);
			generateEnumDef(packageName+".def", outputFolder, name, values);
		}
		
	}
	
	protected static void generateEnumDef(String packageName, File outputFolder, String name,
			LinkedHashMap<Long, String> values) throws IOException {
		File outputDir = getOutputDir(outputFolder, packageName);
		File outputFile = new File(outputDir, name + ".java");

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

		bw.write(getCopyRightHeader());
		bw.write("package "+packageName+";\n\n");
		
		bw.write("public enum "+name+" {\n\n");
		
		boolean first = true;
		
		for (Entry<Long, String> val : values.entrySet()) {
			if (!first)
				bw.write(",\n");
			first = false;
			bw.write(String.format("\t%s(0x%08Xl)", val.getValue(), val.getKey()));
		}
		
		bw.write(";\n\n");
		
		bw.write("\tprotected long value;\n\n");
		
		bw.write("\t"+name+"(long value) {\n");
		bw.write("\t\tthis.value = value;\n");
		bw.write("\t}\n\n");
		
		bw.write("\tpublic long value() {\n");
		bw.write("\t\treturn value;\n");
		bw.write("\t}\n\n");
		
		bw.write("\tpublic static "+name+" valueOf(long value) throws IllegalArgumentException {\n");
		bw.write("\t\tfor ("+name+" v : "+name+".values()) {\n");
		bw.write("\t\t\tif (v.value == value) {\n");
		bw.write("\t\t\t\treturn v;\n");
		bw.write("\t\t\t}\n");
		bw.write("\t\t}\n");
		bw.write("\t\tthrow new IllegalArgumentException(\"Invalid value for "+name+": \"+value);\n");
		bw.write("\t}\n\n");
		bw.write("}\n");
		
		bw.close();
		
	}

	protected static void generateHeader(String packageName, File outputFolder,
			IMCDefinition defs) throws Exception {
		IMCMessageType type = defs.getHeaderType();
		String msgName = "Header";
		File outputDir = getOutputDir(outputFolder, packageName);
		File outputFile = new File(outputDir, msgName + ".java");
		System.out.println("Generating " + outputFile.getPath());

		// BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "UTF-8"));

		bw.write(getCopyRightHeader());

		bw.write("package pt.lsts.imc;\n\n");

		bw.write("/**\n");
		bw.write(" *  IMC Message Header \n");
		bw.write(" */\n\n");

		bw.write("public class " + msgName + " extends IMCMessage {\n\n");

		bw.write("\tpublic "
				+ msgName
				+ "() {\n\t\tsuper(IMCDefinition.getInstance().getHeaderType());\n\t}\n\n");
		bw.write("\tpublic "
				+ msgName
				+ "(IMCDefinition defs) {\n\t\tsuper(defs.getHeaderType());\n\t}\n\n");

		for (String field : type.getFieldNames())
			bw.write(generateGetters(type, field, false));

		for (String field : type.getFieldNames())
			bw.write(generateSetters(type, field, false));

		bw.write("}\n");

		bw.close();
	}

	private static String importDefinitions(String packageName, IMCMessageType type) throws IOException {
		StringBuilder result = new StringBuilder();
		
		ArrayList<String> alreadyImported = new ArrayList<String>();
		
		for (String field : type.getFieldNames()) {
			if (!("enumerated".equals(type.getFieldUnits(field))))
				continue;
			String fieldValueDef = type.getFieldValueDefs(field);
			
			if (fieldValueDef != null && !alreadyImported.contains(fieldValueDef)) {
				result.append("import "+packageName+".def."+fieldValueDef+";\n");
				alreadyImported.add(fieldValueDef);
			}
		}
		
		return result.toString();
	}
	
	
	private static String generateDefinitions(IMCMessageType type,
			IMCMessageType superType) throws IOException {

		Vector<String> generatedBitmaskDefs = new Vector<String>();
		StringBuilder result = new StringBuilder();

		for (String field : type.getFieldNames()) {

			// this field is inherited, skip
			if (superType.getFieldType(field) != null) {
				continue;
			}
			
			if ("bitfield".equals(type.getFieldUnits(field))) {

				if (type.getFieldPossibleValues(field) == null)
					continue;

				String bmaskType = "long";

				if (type.getFieldType(field) == IMCFieldType.TYPE_INT8)
					bmaskType = "byte";
				else if (type.getFieldType(field) == IMCFieldType.TYPE_UINT8
						|| type.getFieldType(field) == IMCFieldType.TYPE_INT16)
					bmaskType = "short";
				else if (type.getFieldType(field) == IMCFieldType.TYPE_UINT16
						|| type.getFieldType(field) == IMCFieldType.TYPE_INT32)
					bmaskType = "int";

				LinkedHashMap<Long, String> enum_vals = type
						.getFieldPossibleValues(field);

				for (long val : enum_vals.keySet()) {
					String name = enum_vals.get(val);
					if (generatedBitmaskDefs.contains(name))
						continue;
					generatedBitmaskDefs.add(name);

					Formatter fmt = new Formatter();
					int bits = type.getFieldType(field).getSizeInBytes() * 2;
					fmt.format("0x%0" + bits + "X", val);
					String hex = fmt.out().toString();
					result.append("\tpublic static final " + bmaskType + " "
							+ type.getFieldPrefix(field) + "_"
							+ enum_vals.get(val).toUpperCase() + " = " + hex
							+ ";\n");
					fmt.close();
				}
				result.append("\n");
			}
		}

		for (String field : type.getFieldNames()) {
			
			if (type.getFieldValueDefs(field) != null) {
				continue;
			}
			
			if ("enumerated".equals(type.getFieldUnits(field))) {

				LinkedHashMap<String, Long> enum_vals = type
						.getFieldMeanings(field);

				result.append("\tpublic enum " + field.toUpperCase() + " {\n");
				boolean first = true;
				for (String name : enum_vals.keySet()) {
					if (!first)
						result.append(",\n");
					result.append("\t\t" + name.toUpperCase() + "("
							+ enum_vals.get(name) + ")");
					first = false;
				}
				result.append(";\n\n");

				result.append("\t\tprotected long value;\n\n");
				result.append("\t\tpublic long value() {\n");
				result.append("\t\t\treturn value;\n");
				result.append("\t\t}\n\n");
				result.append("\t\t" + field.toUpperCase() + "(long value) {\n");
				result.append("\t\t\tthis.value = value;\n");
				result.append("\t\t}\n");
				result.append("\t}\n\n");
			}
		}

		return result.toString();
	}

	protected static void generateClass(String packageName, File outputFolder,
			IMCDefinition defs, String msgName) throws Exception {
		IMCMessageType type = defs.getType(msgName);

		if (type == null) {
			throw new Exception("Message with name " + msgName
					+ " does not exist.");
		}

		File outputDir = getOutputDir(outputFolder, packageName);
		File outputFile = new File(outputDir, msgName + ".java");
		System.out.println("Generating " + outputFile.getPath());

		// BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "UTF-8"));

		bw.write(getCopyRightHeader());

		bw.write("package "+packageName+";\n\n");

		bw.write(importDefinitions(packageName, type));
		bw.write("\n");
		
		bw.write("/**\n");
		bw.write(" *  IMC Message " + type.getFullName() + " (" + type.getId()
		+ ")<br/>\n");

		if (type.getMessageDescription() != null) {
			String desc = DescriptionToHtml.descToHtml(type
					.getMessageDescription());

			String lines[] = desc.split("\n");
			for (String line : lines) {
				if (!line.trim().isEmpty())
					bw.write(" *  " + line.trim() + "<br/>\n");
			}
		}

		bw.write(" */\n\n");

		IMCMessageType superType = new IMCMessageType();

		String abstractClass = type.isAbstract() ? "abstract " : "";
		String superClass = " extends IMCMessage";

		if (type.getSupertype() != null) {
			superType = type.getSupertype();
			superClass = " extends " + superType.getShortName();
		}

		bw.write("public " + abstractClass + "class " + msgName + superClass
				+ " {\n\n");

		bw.write(generateDefinitions(type, superType));

		if (type.isAbstract()) {
			bw.write("\tpublic " + msgName
					+ "(int type) {\n\t\tsuper(type);\n\t}\n\n");
			bw.write("\tpublic "
					+ msgName
					+ "(IMCDefinition defs, int type) {\n\t\tsuper(defs, type);\n\t}\n\n");


			bw.write("\tpublic static "+msgName+" clone(IMCMessage msg) throws Exception {\n" +
					"\t\tIMCMessage m = IMCDefinition.getInstance().create(msg.getAbbrev());\n" +
					"\t\tif (!"+msgName+".class.isAssignableFrom(m.getClass()))\n"+
					"\t\t\tthrow new Exception(m.getClass().getSimpleName()+\" is not a subclass\");\n\n" +
					"\t\tif(msg.definitions != m.definitions){\n" +
					"\t\t\tmsg = msg.cloneMessage();\n" +
					"\t\t\tIMCUtil.updateMessage(msg, m.definitions);\n"+
					"\t\t}\n\n"+
					"\t\tm.getHeader().values.putAll(msg.getHeader().values);\n"+
					"\t\tm.values.putAll(msg.values);\n\n"+
					"\t\treturn ("+msgName+")m;\n"+
					"\t}\n\n");
		} else {
			bw.write("\tpublic static final int ID_STATIC = " + type.getId()
			+ ";\n\n");
			bw.write("\tpublic " + msgName
					+ "() {\n\t\tsuper(ID_STATIC);\n\t}\n\n");
			bw.write("\tpublic " + msgName + "(IMCMessage msg) {\n");
			bw.write("\t\tsuper(ID_STATIC);\n");
			bw.write("\t\ttry{\n");
			bw.write("\t\t\tcopyFrom(msg);\n");
			bw.write("\t\t}\n");
			bw.write("\t\tcatch (Exception e) {\n");
			bw.write("\t\t\te.printStackTrace();\n");
			bw.write("\t\t}\n");
			bw.write("\t}\n\n");
			bw.write("\tpublic "
					+ msgName
					+ "(IMCDefinition defs) {\n\t\tsuper(defs, ID_STATIC);\n\t}\n\n");

			bw.write("\tpublic static " + msgName
					+ " create(Object... values) {\n");
			bw.write("\t\t" + msgName + " m = new " + msgName + "();\n");
			bw.write("\t\tfor (int i = 0; i < values.length-1; i+= 2)\n");
			bw.write("\t\t\tm.setValue(values[i].toString(), values[i+1]);\n");
			bw.write("\t\treturn m;\n");
			bw.write("\t}\n\n");

			bw.write("\tpublic static " + msgName
					+ " clone(IMCMessage msg) throws Exception {\n\n");
			bw.write("\t\t" + msgName + " m = new " + msgName + "();\n");
			bw.write("\t\tif (msg == null)\n");
			bw.write("\t\t\treturn m;\n");
			bw.write("\t\tif(msg.definitions != m.definitions){\n");
			bw.write("\t\t\tmsg = msg.cloneMessage();\n");
			bw.write("\t\t\tIMCUtil.updateMessage(msg, m.definitions);\n\t\t}\n");
			bw.write("\t\telse if (msg.getMgid()!=m.getMgid())\n");
			bw.write("\t\t\tthrow new Exception(\"Argument \"+msg.getAbbrev()+\" is incompatible with message \"+m.getAbbrev());\n\n");
			bw.write("\t\tm.getHeader().values.putAll(msg.getHeader().values);\n");
			bw.write("\t\tm.values.putAll(msg.values);\n");
			bw.write("\t\treturn m;\n\t}\n\n");

			bw.write(generateFullConstructor(defs, msgName));
		}
		for (String field : type.getFieldNames()) {
			if (superType.getFieldType(field) == null) {
				bw.write(generateGetters(type, field, true));
				bw.write(generateSetters(type, field, true));
			}
		}
		bw.write("}\n");
		bw.close();
	}

	private static File getOutputDir(File outputFolder, String packageName) {
		String[] subfolders = packageName.split("\\.");
		File curFolder = outputFolder;
		for (String folder : subfolders) {
			File tmp = new File(curFolder, folder);
			tmp.mkdirs();
			curFolder = tmp;
		}
		return curFolder;
	}

	private static void clearDir(File outputFolder) {
		for (File f : outputFolder.listFiles()) {
			if (f.getName().endsWith(".java")) {
				System.out.println("Deleting " + f.getPath());
				f.delete();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1)
			throw new Exception(
					"Expected one argument with path to IMC repository");

		String basePath = "src-generated";
		String baseResourcesPath = "/resources";
		String baseJavaPath = "/java";
	
		File repo = new File(args[0]);

		String sha = "N/A";
		String branch = "N/A";
		String commitDetails = "Not a GIT repository";
		
		if (args.length > 1)
			sha = args[1];
		if (args.length > 2)
			branch = args[2];
		if (args.length > 3)
			commitDetails = args[3];
		
		if (args.length <= 1) {
			try {
				GenerationUtils.checkRepo(repo);
				sha = GenerationUtils.getGitSha(repo);
				branch = GenerationUtils.getGitBranch(repo);
				commitDetails = GenerationUtils.getGitCommit(repo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			IMCDefinition defs = new IMCDefinition(
					GenerationUtils.getImcXml(repo));
			// copy IMC.xml to generated source folder
			Path source = FileSystems.getDefault().getPath(repo.getAbsolutePath(), "IMC.xml");
			Path target = FileSystems.getDefault().getPath(basePath + baseResourcesPath, "xml", "IMC.xml");
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

			Map<String, Integer> addrs;
			try {
				addrs = GenerationUtils.getImcAddresses(repo);
			} catch (Exception e) {
				addrs = new HashMap<>();
				e.printStackTrace();
			}

			File output = getOutputDir(new File(basePath + baseJavaPath), "pt.lsts.imc");
			clearDir(output);

			generateClasses("pt.lsts.imc", new File(basePath + baseJavaPath), defs);
			generateStringDefinitions("pt.lsts.imc", addrs, sha, branch,
					commitDetails, new File(basePath + baseJavaPath));
			generateImcFactory(defs, new File(basePath + baseJavaPath));
			//generateImcState(defs, new File(basePath + baseJavaPath));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}