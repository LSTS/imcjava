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
package pt.lsts.neptus.messages.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.adapter.VehicleAdapter;
import pt.lsts.imc.net.Consume;

public class ImcConsumer implements MessageListener<MessageInfo, IMCMessage> {

	private Pattern patternMethods = Pattern.compile("(\\w+?)(?=\\(.*?\\))");
	private LinkedHashMap<Class<?>, ArrayList<Method>> consumeMethods = new LinkedHashMap<Class<?>, ArrayList<Method>>();
	private LinkedHashMap<Method, List<String>> sources = new LinkedHashMap<Method, List<String>>();
	private LinkedHashMap<Method, List<String>> entities = new LinkedHashMap<Method, List<String>>();
	
	private Object pojo;

	private ImcConsumer(Object pojo) {
		this.pojo = pojo;
		boolean isSuperClass = false;

		Class<?> clazz = pojo.getClass();
		while (clazz != Object.class) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getAnnotation(Consume.class) != null) {
					if (m.getParameterTypes().length != 1) {
						System.err.println("Warning: Ignoring @Consume annotation on method " + m
								+ " due to wrong number of parameters.");
						continue;
					}
					if (!IMCMessage.class.isAssignableFrom(m.getParameterTypes()[0])) {
						System.err.println("Warning: Ignoring @Consume annotation on method " + m
								+ " due to wrong parameter type.");
						continue;
					}

					Class<?> c = m.getParameterTypes()[0];

					if (!consumeMethods.containsKey(c)) {
						consumeMethods.put(c, new ArrayList<Method>());
					}
					if (!m.isAccessible())
						m.setAccessible(true);

					if (!isSuperClass) {
						consumeMethods.get(c).add(m);
					}
					else {
						String str = m.toString();
						Matcher matcher = patternMethods.matcher(str);
						if (matcher.find()) {
							String mstr = str.substring(matcher.start(), str.length());
							boolean found = false;
							for (Method im : consumeMethods.get(c)) {
								str = im.toString();
								matcher = patternMethods.matcher(str);
								if (matcher.find()) {
									String sstr = str.substring(matcher.start(), str.length());
									if (mstr.equalsIgnoreCase(sstr)) {
										found = true;
										break;
									}
 								}
							}
							
							if (!found)
								consumeMethods.get(c).add(m);
						}
						else {
							consumeMethods.get(c).add(m);
						}
					}

					Consume annotation = m.getAnnotation(Consume.class);

					List<String> srcs = Arrays.asList(annotation.Source());
					if (srcs.size() != 1 || !srcs.get(0).isEmpty())
						sources.put(m, srcs);
					List<String> ents = Arrays.asList(annotation.Entity());
					if (ents.size() != 1 || !ents.get(0).isEmpty())
						entities.put(m, ents);
				}
			}

			clazz = clazz.getSuperclass();
			isSuperClass = true;
		}
	}

	public Collection<String> getTypesToListen() {
		HashSet<String> types = new HashSet<String>();
		for (Class<?> c : consumeMethods.keySet()) {
			if (c.getClass().equals(IMCMessage.class))
				return null;
			String name = c.getSimpleName();
			// If its a supertype, also add its subtypes to the list of messages
			// listened
			if (IMCDefinition.getInstance().subtypesOf(name) != null)
				types.addAll(IMCDefinition.getInstance().subtypesOf(name));
			types.add(name);
		}
		
		if (types.contains("IMCMessage"))
			return null;
		
		return types;
	}

	public void onMessage(MessageInfo i, IMCMessage m) {
		m.setMessageInfo(i);
		Class<?> c = m.getClass();
		ArrayList<Method> consumers = new ArrayList<Method>();

		while (c != Object.class) {
			if (consumeMethods.containsKey(c))
				consumers.addAll(consumeMethods.get(c));
			c = c.getSuperclass();
		}

		for (Method method : consumers) {
			if (sources.containsKey(method) && ! sources.get(method).contains(m.getSourceName()))
				continue;
			if (entities.containsKey(method) && ! entities.get(method).contains(m.getEntityName()))
				continue;
			try {
				method.invoke(pojo, m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static ImcConsumer create(final Object pojo) {
		return new ImcConsumer(pojo);
	}

	/**
	 * @return the pojo
	 */
	public final Object getPojo() {
		return pojo;
	}
	
	public static void main(String[] args) {
		Pattern TOKEN = Pattern.compile("(\\w+?)(?=\\(.*?\\))");
		ArrayList<String> strings = new ArrayList<>();
		strings.add("protected void pt.lsts.imc.adapter.VehicleAdapter2.on(pt.lsts.imc.PlanDB)");
		strings.add("protected void pt.lsts.imc.adapter.VehicleAdapter2.on()");
		
		for (Method m : Object.class.getMethods()) {
			strings.add(m.toGenericString());
		}

		for (Method m : ImcConsumer.class.getMethods()) {
			strings.add(m.toGenericString());
		}

		for (Method m : VehicleAdapter.class.getMethods()) {
			strings.add(m.toGenericString());
		}

//		for (Method m : VehicleAdapter2.class.getMethods()) {
//			strings.add(m.toGenericString());
//		}

		for (String str : strings) {
			Matcher matcher = TOKEN.matcher(str);
			System.out.println(str);
			System.out.println(matcher.find());
			System.out.println(matcher.start() + "    " + matcher.end());
			System.out.println(str.substring(matcher.start(), str.length()));
		}
	}
}
