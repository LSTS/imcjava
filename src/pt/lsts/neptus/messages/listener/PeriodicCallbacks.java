/*
 * Below is the copyright agreement for IMCJava.
 * 
 * Copyright (c) 2010-2015, Laboratório de Sistemas e Tecnologia Subaquática
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class PeriodicCallbacks {

	private static Timer executor = new Timer();
	
	private static LinkedHashMap<Integer, Vector<TimerTask>> callbacks = new LinkedHashMap<Integer, Vector<TimerTask>>();
	
	public static void stopAll() {
		executor.cancel();
	}
	
	public static void unregister(Object pojo) {
		Vector<TimerTask> calls = callbacks.remove(pojo.hashCode());
		if (calls != null)
			for (TimerTask t : calls)
				t.cancel();		
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
				
				TimerTask callback = new TimerTask() {

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
				
				executor.scheduleAtFixedRate(callback, period, period);				
			}
		}
	}
}
