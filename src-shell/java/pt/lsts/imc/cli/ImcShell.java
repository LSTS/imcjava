/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2025, Laboratório de Sistemas e Tecnologia Subaquática
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
package pt.lsts.imc.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.DatatypeConverter;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.ImcStringDefs;
import pt.lsts.imc.lsf.LsfMessageLogger;
import pt.lsts.imc.net.Consume;
import pt.lsts.imc.net.IMCProtocol;
import pt.lsts.imc.net.UDPTransport;
import pt.lsts.neptus.messages.listener.ImcConsumer;
import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;

public class ImcShell {

	private LinkedHashMap<String, IMCMessage> vars = new LinkedHashMap<String, IMCMessage>();
	private UDPTransport transport = new UDPTransport();
	private IMCProtocol proto = null;
	private Shell reference = null;
	ScriptEngineManager mgr = new ScriptEngineManager();

	public ImcShell() {
	}

	@Consume
	public void onMessage(IMCMessage m) {
		vars.put(m.getSourceName() + "." + m.getAbbrev(), m);
		LsfMessageLogger.log(m);
	}

	@Command(abbrev = "env", description = "Print the names of messages in the environment")
	public void env() {
		System.out.println(vars.keySet());
	}

	@Command(abbrev = "print", description = "Print the value of a message in the environment")
	public void print(
			@Param(name = "expr", description = "The name of the message to be printed or a value in the form <message>[/<field>]+") String msg) {

		if (msg.matches("`(.*)`")) {
			System.out.println(valueOf(msg.substring(1, msg.length() - 1)));
			return;
		}

		if (msg.contains("/")) {
			String[] parts = msg.split("\\/");
			IMCMessage m = vars.get(parts[0]);
			for (int i = 1; i < parts.length - 1; i++) {
				if (m == null)
					break;
				if (m.getTypeOf(parts[i]).equals("message"))
					m = m.getMessage(parts[i]);
			}
			if (m != null)
				System.out.println(m.getValue(parts[parts.length - 1]));
			else {
				System.out.println("null");
			}
		} else
			System.out.println(vars.get(msg));
	}

	@Command(abbrev = "create", description = "Create and add a new message to the environment")
	public void create(
			@Param(name = "msg", description = "The name of the message (variable) to be created") String name,
			@Param(name = "type", description = "The type of the message to be created. Examples: Announce, EstimatedState, etc") String msgType)
			throws Exception {

		IMCMessage m = IMCDefinition.getInstance().create(msgType);
		if (m == null)
			throw new Exception("Message type not valid: '" + msgType + "'");

		if (!name.matches("[a-zA-Z_]\\w*")) {
			throw new Exception("Message identifier is not valid: '" + name
					+ "'.");
		}

		vars.put(name, m);
	}

	private Pattern scriptedVars = Pattern.compile(".*\\$\\{(.*)\\}.*");

	private Object valueOf(String expr) {
		Matcher matcher = scriptedVars.matcher(expr);
		while (matcher.matches()) {
			String inner = matcher.group(1);
			expr = expr.replaceFirst("\\$\\{" + inner + "\\}", ""
					+ valueOf(inner));
			matcher = scriptedVars.matcher(expr);
		}
		if (vars.containsKey(expr))
			return vars.get(expr);
		else if (expr.contains("/")
				&& vars.containsKey(expr.substring(0, expr.indexOf("/")))) {
			String[] parts = expr.split("\\/");
			IMCMessage m = vars.get(parts[0]);
			for (int i = 1; i < parts.length - 1; i++) {
				if (m == null)
					break;
				if (m.getTypeOf(parts[i]).equals("message"))
					m = m.getMessage(parts[i]);
			}
			if (m != null)
				return m.getValue(parts[parts.length - 1]);
			else {
				return null;
			}
		} else {
			try {
				ScriptEngine engine = mgr.getEngineByName("JavaScript");
				// Only required for Java 8
				try {
					engine.eval("load(\"nashorn:mozilla_compat.js\");");
				} catch (Exception e) {
				}

				return engine.eval(expr);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private void setValue(IMCMessage m, String field, String value)
			throws Exception {

		String fieldType = m.getTypeOf(field);
		if (fieldType == null) {
			throw new Exception("The field '" + field + "' is not valid.");
		}

		value = value.trim();
		if (value.matches("`(.*)`")) {
			m.setValue(field, valueOf(value.substring(1, value.length() - 1)));
			return;
		}

		switch (fieldType) {
		case "uint8_t":
		case "uint16_t":
		case "uint32_t":
			try {
				int uradix = 10;

				if (value.startsWith("0x") || value.startsWith("0X")) {
					value = value.substring(2);
					uradix = 16;
				}

				long uval = Long.parseLong(value, uradix);
				if (uval < 0)
					throw new Exception("The value of '" + field + "' ("
							+ fieldType + ") is not valid.");
				m.setValue(field, uval);
			} catch (NumberFormatException e) {
				if (m.getMessageType().getFieldPossibleValues(field) != null
						&& m.getMessageType().getFieldPossibleValues(field)
								.containsValue(value))
					m.setValue(field, value);
				else {
					throw new Exception("The value of '" + field + "' ("
							+ fieldType + ") is not valid.");
				}
			}
			break;
		case "int8_t":
		case "int16_t":
		case "int32_t":
		case "int64_t":
			try {
				int lradix = 10;

				if (value.startsWith("0x") || value.startsWith("0X")) {
					value = value.substring(2);
					lradix = 16;
				}

				long lval = Long.parseLong(value, lradix);
				m.setValue(field, lval);
			} catch (NumberFormatException e) {
				if (m.getMessageType().getFieldPossibleValues(field) != null
						&& m.getMessageType().getFieldPossibleValues(field)
								.containsValue(value))
					m.setValue(field, value);
				else {
					throw new Exception("The value of '" + field + "' ("
							+ fieldType + ") is not valid.");
				}
			}
			break;
		case "fp64_t":
		case "fp32_t":
			double dval = Double.parseDouble(value);
			m.setValue(field, dval);
			break;
		case "rawdata":
			m.setValue(field, DatatypeConverter.parseHexBinary(value));
			break;
		case "message":
			if (!value.toLowerCase().equals("null") && !vars.containsKey(value))
				throw new Exception(
						"There is no message in the environment named '"
								+ value + "'.");
			m.setValue(field, vars.get(value));
			break;
		case "message-list":
			if (!value.toLowerCase().equals("null")) {
				String[] msgs = value.replace("[", "").replace("]", "")
						.replace(" ", "").split(",");
				ArrayList<IMCMessage> val = new ArrayList<IMCMessage>();
				for (String s : msgs) {
					if (vars.containsKey(s))
						val.add(vars.get(s).cloneMessage());
					else
						throw new Exception(
								"There is no message in the environment named '"
										+ s + "'.");
				}
				m.setValue(field, val);
			}
			break;
		default:
			m.setValue(field, value.trim());
			break;
		}
	}

	@Command(abbrev = "create", description = "Create and add a new message to the environment")
	public void create(
			@Param(name = "msg", description = "The name of the message (variable) to be created") String name,
			@Param(name = "type", description = "The type of the message to be created. Examples: Announce, EstimatedState, etc") String msgType,
			@Param(name = "values", description = "Initial message values in the form <field>=<value>") String... args)
			throws Exception {
		IMCMessage m = IMCDefinition.getInstance().create(msgType);

		if (m == null) {
			throw new Exception("Message type not valid: '" + msgType + "'");
		}
		for (int i = 0; i < args.length; i++) {
			String[] parts = args[i].split("=");
			if (parts.length != 2) {
				throw new Exception(
						"Message field values should be passed as <field name>=<value>.");
			}
			String field = parts[0].trim();
			String value = parts[1].trim();
			setValue(m, field, value);
		}
		vars.put(name, m);
	}

	@Command(abbrev = "change", description = "Change a message already stored in the environment")
	public void change(
			@Param(name = "msg", description = "The name of the message to be changed") String name,
			@Param(name = "changes", description = "The changes to be performed in the form <field>=<value>") String... args)
			throws Exception {
		IMCMessage m = vars.get(name);

		if (m == null) {
			throw new Exception("Message not found in the current environment");
		}
		for (int i = 0; i < args.length; i++) {
			String[] parts = args[i].split("=");
			if (parts.length != 2) {
				throw new Exception("Malformed field attribution expression: '"
						+ args[i] + "'.");
			}
			String field = parts[0].trim();
			String value = parts[1].trim();
			setValue(m, field, value);
		}
		vars.put(name, m);
	}

	@Command(abbrev = "sleep", description = "Sleep for a number of seconds")
	public void sleep(
			@Param(name = "secs", description = "Time, in seconds, to sleep for") int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Command(abbrev = "send", description = "Send a message to a known IMC destination")
	public void send(
			@Param(name = "destination", description = "IMC name of the destination") String host,
			@Param(name = "message", description = "Message to be sent (name of a message saved in this environment)") String message)
			throws Exception {

		if (proto == null)
			throw new Exception(
					"Discovery is not active. Use the command 'bind'.");

		if (!Arrays.asList(proto.systems()).contains(host)) {
			throw new Exception("The IMC destination '" + host
					+ "' was not yet discovered.");
		}
		if (!vars.containsKey(message))
			throw new Exception(
					"There is no message in the environment named '" + message
							+ "'.");

		IMCMessage msg = vars.get(message);
		proto.sendMessage(host, msg);
		LsfMessageLogger.log(msg);
	}

	@Command(name = "send", description = "Send a message to a remote host", abbrev = "send")
	public void send(
			@Param(name = "hostname", description = "Host where to send the message") String host,
			@Param(name = "port", description = "Port where to send the message") int port,
			@Param(name = "message", description = "Message to be sent (name of a message saved in this environment)") String message)
			throws Exception {
		if (!vars.containsKey(message))
			throw new Exception(
					"There is no message in the environment named '" + message
							+ "'.");

		IMCMessage msg = vars.get(message);
		transport.sendMessage(host, port, vars.get(message));
		LsfMessageLogger.log(msg);
	}

	@Command(abbrev = "copy", description = "Create a copy of a message in the environment")
	public void copy(
			@Param(name = "src", description = "Name of the message to be copied") String src,
			@Param(name = "dst", description = "Name of the message (copy) to be created") String dst) {
		IMCMessage m = vars.get(src);
		if (m != null)
			m = m.cloneMessage();
		vars.put(dst, m);
	}

	@Command(abbrev = "bind", description = "Discover another IMC peers and start receiving messages")
	public void bind(
			@Param(name = "port", description = "Local port where to listen") int port) {
		if (proto != null)
			proto.stop();
		proto = new IMCProtocol(port);
		proto.addMessageListener(ImcConsumer.create(this));
	}

	@Command(abbrev = "loop", description = "Execute a list of commands in a loop")
	public void loop(
			@Param(name = "count", description = "Number of times to execute (<= 0 for infinite loop)") int count,
			@Param(name = "instructions", description = "List of instructions, separated by ;") String instr) {

		String[] instrs = instr.split(";");
		for (int i = 0; count <= 0 || i < count; i++) {
			for (int j = 0; j < instrs.length; j++) {
				try {
					System.out.println(instrs[j]);
					reference.processLine(instrs[j]);
				} catch (Exception e) {
					System.err.println(e.getClass().getSimpleName() + ": "
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * @param reference
	 *            the reference to set
	 */
	public void setReference(Shell reference) {
		this.reference = reference;
	}

	public static void main(String[] args) throws Exception {
		ImcShell imcShell = new ImcShell();
		Shell shell = ShellFactory.createConsoleShell("?", "IMC Shell",
				imcShell);
		imcShell.setReference(shell);

		if (args.length == 1 && new File(args[0]).canRead()) {
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					args[0])));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#") || line.trim().isEmpty())
					continue;
				shell.processLine(line);
			}
			reader.close();
			return;
		}
		shell.setDisplayTime(true);
		System.out.println("Using IMC v"
				+ IMCDefinition.getInstance().getVersion() + " ("
				+ ImcStringDefs.IMC_SHA
				+ ").\nEnter ?help for usage information.");
		shell.commandLoop();
	}
}