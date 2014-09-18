package pt.lsts.neptus.messages.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCMessage;

public class ImcConsumer implements MessageListener<MessageInfo, IMCMessage> {

	private LinkedHashMap<Class<?>, ArrayList<Method>> consumeMethods = new LinkedHashMap<Class<?>, ArrayList<Method>>();
	private Object pojo;

	private ImcConsumer(Object pojo) {
		this.pojo = pojo;
		for (Method m : pojo.getClass().getMethods()) {
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
				consumeMethods.get(c).add(m);
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
		return types;
	}

	public void onMessage(MessageInfo i, IMCMessage m) {
		Class<?> c = m.getClass();
		// System.out.println(IMCDefinition.getInstance().getResolver().
		// .getSrcEnt());

		ArrayList<Method> consumers = new ArrayList<Method>();

		while (c != Object.class) {
			if (consumeMethods.containsKey(c))
				consumers.addAll(consumeMethods.get(c));
			c = c.getSuperclass();
		}

		for (Method method : consumers) {
			try {
				method.setAccessible(true);
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
