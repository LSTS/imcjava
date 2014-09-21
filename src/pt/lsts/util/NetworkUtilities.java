package pt.lsts.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

public class NetworkUtilities {

	public static Collection<String> getNetworkInterfaces(
			boolean includeLoopback) {
		Vector<String> itfs = new Vector<String>();
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface
					.getNetworkInterfaces();
			while (nis.hasMoreElements()) {
				NetworkInterface ni = nis.nextElement();
				try {
					if (ni.isLoopback() && !includeLoopback)
						continue;
				} catch (Exception e) {
					continue;
				}

				Enumeration<InetAddress> adrs = ni.getInetAddresses();
				while (adrs.hasMoreElements()) {
					InetAddress addr = adrs.nextElement();
					if (addr instanceof Inet4Address)
						itfs.add(addr.getHostAddress());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itfs;
	}
}
