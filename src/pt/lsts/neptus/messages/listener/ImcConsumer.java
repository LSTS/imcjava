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

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.net.Consume;

public class ImcConsumer implements MessageListener<MessageInfo, IMCMessage> {

	private LinkedHashMap<Class<?>, ArrayList<Method>> consumeMethods = new LinkedHashMap<Class<?>, ArrayList<Method>>();
	private LinkedHashMap<Method, List<String>> sources = new LinkedHashMap<Method, List<String>>();
	private LinkedHashMap<Method, List<String>> entities = new LinkedHashMap<Method, List<String>>();
	
	private Object pojo;

	private ImcConsumer(Object pojo) {
		this.pojo = pojo;
		for (Method m : pojo.getClass().getDeclaredMethods()) {
			if (m.getAnnotation(Consume.class) != null) {
				
				if (m.getParameterTypes().length != 1) {
					System.err
							.println("Warning: Ignoring @Consume annotation on method "
									+ m + " due to wrong number of parameters.");
					continue;
				}
				if (!IMCMessage.class
						.isAssignableFrom(m.getParameterTypes()[0])) {
					System.err
							.println("Warning: Ignoring @Consume annotation on method "
									+ m + " due to wrong parameter type.");
					continue;
				}

				Class<?> c = m.getParameterTypes()[0];

				if (!consumeMethods.containsKey(c)) {
					consumeMethods.put(c, new ArrayList<Method>());
				}
				if (!m.isAccessible())
					m.setAccessible(true);
				
				
				consumeMethods.get(c).add(m);
				
				Consume annotation = m.getAnnotation(Consume.class);
				
				List<String> srcs = Arrays.asList(annotation.Source());
				if (srcs.size() != 1 || !srcs.get(0).isEmpty())
					sources.put(m, srcs);
				List<String> ents = Arrays.asList(annotation.Entity());
				if (ents.size() != 1 || !ents.get(0).isEmpty())
					entities.put(m, ents);
				
			}
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
}
