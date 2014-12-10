package babyfon.connectivity.wifi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class AddressHandler {

	/**
	 * Ermittelt die lokale IPv4 Adresse des mobilen Gerätes
	 * 
	 * @return String: Lokale IPv4 Adresse des Mobilgerätes, wenn erkannt, sonst
	 *         null
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public String getLocalIPv4Address() throws SocketException, UnknownHostException {
		InetAddress hostIP;
		hostIP = InetAddress.getLocalHost();
		if (hostIP.getHostAddress().equals("127.0.0.1")) {
			Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
			while (ifaces.hasMoreElements()) {
				NetworkInterface iface = ifaces.nextElement();
				Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					hostIP = inetAddresses.nextElement();
					if (hostIP.isSiteLocalAddress()) {
						return hostIP.getHostAddress();
					}
				}
			}
		} else {
			return hostIP.getHostAddress();
		}
		return null;
	}

	/**
	 * Ermittelt mit Hilfe der lokalen IPv4 des mobilen Gerätes die
	 * Netzwerkadresse
	 * 
	 * @return Lokale IPv4 Netzwerkadresse
	 */
	public String getNetworkAddress() {
		String[] localAddress = null;
		try {
			localAddress = getLocalIPv4Address().split("\\.");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String networkAddress = "";
		for (int i = 0; i < localAddress.length - 1; i++) {
			networkAddress = networkAddress + localAddress[i] + ".";
		}
		return networkAddress;
	}

	/**
	 * Überprüft die Korrektheit von IPv4-Adressen
	 * 
	 * @param hostIP
	 * @return True, wenn die zu testende IPv4-Adresse korrekt ist, sonst false
	 */
	public boolean isIPAdress(String ipv4) {
		String[] ipv4Tokens = ipv4.split("\\.");
		if (ipv4Tokens.length != 4) {
			return false;
		} else if (Integer.parseInt(ipv4Tokens[0]) < 1 || Integer.parseInt(ipv4Tokens[0]) > 254) {
			return false;
		} else if (Integer.parseInt(ipv4Tokens[1]) < 0 || Integer.parseInt(ipv4Tokens[1]) > 255) {
			return false;
		} else if (Integer.parseInt(ipv4Tokens[2]) < 0 || Integer.parseInt(ipv4Tokens[2]) > 255) {
			return false;
		} else if (Integer.parseInt(ipv4Tokens[3]) < 1 || Integer.parseInt(ipv4Tokens[3]) > 254) {
			return false;
		} else {
			return true;
		}
	}
}
