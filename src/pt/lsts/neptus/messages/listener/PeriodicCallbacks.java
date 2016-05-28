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
import java.util.LinkedHashMap;
import java.util.Vector;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PeriodicCallbacks {
	private static ScheduledThreadPoolExecutor _exec = null;
	
	private static ScheduledThreadPoolExecutor executor() {
		if (_exec == null)
			_exec = new ScheduledThreadPoolExecutor(2);
		return _exec;
	}
	
	private static LinkedHashMap<Integer, Vector<ScheduledFuture<?>>> callbacks = new LinkedHashMap<Integer, Vector<ScheduledFuture<?>>>();
	
	public static void stopAll() {
		executor().shutdown();
		_exec = null;
	}
	
	public static void unregister(Object pojo) {
		Vector<ScheduledFuture<?>> calls = callbacks.remove(pojo.hashCode());
		if (calls != null)
			for (ScheduledFuture<?> t : calls)
				t.cancel(true);
	}
	
	public static void register(Object pojo) {
		for (Method m : pojo.getClass().getDeclaredMethods()) {
			if (m.getAnnotation(Periodic.class) != null) {

				if (m.getParameterTypes().length != 0) {
					System.err
							.println("Warning: Ignoring @Periodic annotation on method "
									+ m + " due to wrong number of parameters.");
					continue;
				}
				m.setAccessible(true);
				final Method method = m;
				final Object client = pojo;
				
				Runnable callback = new Runnable() {

					@Override
					public void run() {
						try {
							method.invoke(client);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};

				long period = method.getAnnotation(Periodic.class)
						.value();
				
				ScheduledFuture<?> c = executor().scheduleAtFixedRate(callback, period, period, TimeUnit.MILLISECONDS);
				
				if (!callbacks.containsKey(pojo.hashCode()))
					callbacks.put(pojo.hashCode(), new Vector<ScheduledFuture<?>>());
				callbacks.get(pojo.hashCode()).add(c);
			}
		}
	}
}
