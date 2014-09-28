package pt.lsts.neptus.messages.listener;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicCallbacks {

	private static ScheduledExecutorService executor = Executors
			.newScheduledThreadPool(0);

	public static void stopAll() {
		executor.shutdownNow();
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
						.millisBetweenUpdates();
				executor.scheduleAtFixedRate(callback, period, period,
						TimeUnit.MILLISECONDS);
			}
		}
	}
}
