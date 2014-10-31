package pt.lsts.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

public class NetworkUtilities {

	public static Collection<String> getNetworkInterfaces() {
		Collection<String> itfs = getNetworkInterfaces(false);
		if (itfs.isEmpty())
			return getNetworkInterfaces(true);
		else
			return itfs;
	}

	public static String getBroadcastAddress() {
		Collection<NetworkInterface> itfs = getInterfaces(false);
		if (itfs.isEmpty())
			itfs = getInterfaces(true);
		
		for (NetworkInterface ni : itfs) {
			for (InterfaceAddress addr : ni.getInterfaceAddresses()) {
				if (addr.getBroadcast() != null)
					return addr.getBroadcast().getHostAddress();
			}
		}
		return null;
	}

	public static Collection<NetworkInterface> getInterfaces(boolean includeLoopback) {
		Vector<NetworkInterface> itfs = new Vector<NetworkInterface>();
		try {
			Enumeration<NetworkInterface> nis = NetworkInterface
					.getNetworkInterfaces();
			while (nis.hasMoreElements()) {
				NetworkInterface ni = nis.nextElement();
				try {
					if (ni.isLoopback() && !includeLoopback)
						continue;				
					itfs.add(ni);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return itfs;
	}

	public static Collection<String> getNetworkInterfaces(
			boolean includeLoopback) {
		Vector<String> itfs = new Vector<String>();
		try {
			for (NetworkInterface ni : getInterfaces(includeLoopback)) {
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

	public static void main(String[] args) throws Exception {
		System.out.println(getBroadcastAddress());
	}
}
