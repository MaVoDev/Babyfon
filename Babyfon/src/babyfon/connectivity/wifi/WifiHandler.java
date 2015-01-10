package babyfon.connectivity.wifi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import babyfon.init.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiHandler {

	private WifiManager mWifiManager;
	
	private Context mContext;

	public WifiHandler(Context mContext) {
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		
		this.mContext = mContext;
	}

	/**
	 * Überprüft, ob Wi-Fi unterstützt wird und ob das Wi-Fi ein- oder
	 * ausgeschaltet ist.
	 * 
	 * @return int: Wi-Fi Status
	 */
	public int getWifiState() {
		if (mWifiManager == null) {
			// Wi-Fi wird nicht unterstützt.
			return -1;
		} else {
			// Wi-Fi wird unterstützt.
			if (!mWifiManager.isWifiEnabled()) {
				// Wi-Fi ist inaktiv.
				return 0;
			}
			// Wi-Fi ist aktiv.
			return 1;
		}
	}
	
	public void startWiFi() {
		mWifiManager.setWifiEnabled(true);
	}

	/**
	 * Gibt die SSID des verbundenen Netzwerks zurück.
	 * 
	 * @return String: SSID
	 */
	public String getSSID() {
		WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		return wifiInfo.getSSID();
	}

	/**
	 * Überprüft, ob das mobile Gerät mit einem Netzwerk verbunden ist.
	 * 
	 * @return True: Das Gerät ist per Wi-Fi mit einem Netzwerk verbunden.
	 *         False: Das Gerät ist nicht per Wi-Fi mit einem Netzwerk
	 *         verbunden.
	 */
	public boolean isWifiConnected() {
		ConnectivityManager conManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo.isConnected()) {
			// Wi-Fi ist mir einem Netzwerk verbunden
			return true;
		}
		// Wi-Fi ist mir keinem Netzwerk verbunden
		return false;
	}

	/**
	 * Ermittelt die lokale IPv4 Adresse des mobilen Gerätes.
	 * 
	 * @return String: Lokale IPv4 Adresse des Mobilgerätes, wenn erkannt, sonst
	 *         null.
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public String getLocalIPv4Address() throws SocketException, UnknownHostException {

		if (getWifiState() == 0) {
			// Wi-Fi ist inaktiv.
			return mContext.getString(R.string.WIFI_STATE_ERROR);
		} else if (!isWifiConnected()) {
			// Wi-Fi ist mir keinem Netzwerk verbunden
			return mContext.getString(R.string.WIFI_CONNECTION_ERROR);
		} else {

			InetAddress localIPv4Address;
			localIPv4Address = InetAddress.getLocalHost();
			if (localIPv4Address.getHostAddress().equals(mContext.getString(R.string.ip_localhost))) {
				Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
				while (ifaces.hasMoreElements()) {
					NetworkInterface iface = ifaces.nextElement();
					Enumeration<InetAddress> inetAddresses = iface.getInetAddresses();
					while (inetAddresses.hasMoreElements()) {
						localIPv4Address = inetAddresses.nextElement();
						if (localIPv4Address.isSiteLocalAddress()) {
							return localIPv4Address.getHostAddress();
						}
					}
				}
			} else {
				return localIPv4Address.getHostAddress();
			}
		}
		return null;
	}

	/**
	 * Ermittelt mit Hilfe der lokalen IPv4 des mobilen Gerätes die Class C
	 * Netzwerkadresse.
	 * 
	 * @return String: Lokale IPv4 Class C Netzwerkadresse
	 */
	public String getNetworkAddressClassC() {
		String[] localIPv4Address = null;
		try {
			localIPv4Address = getLocalIPv4Address().split("\\.");
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String networkAddressClassC = "";
		for (int i = 0; i < localIPv4Address.length - 1; i++) {
			networkAddressClassC = networkAddressClassC + localIPv4Address[i] + ".";
		}
		return networkAddressClassC;
	}

	/**
	 * Überprüft die Korrektheit von IPv4-Adressen.
	 * 
	 * @param hostIP
	 * @return True: IPv4-Adresse ist korrekt. False: IPv4-Adresse ist
	 *         inkorrekt.
	 */
	public boolean isIPv4Adress(String addressIPv4) {
		String[] addressIPv4Tokens = addressIPv4.split("\\.");
		if (addressIPv4Tokens.length != 4) {
			return false;
		} else if (Integer.parseInt(addressIPv4Tokens[0]) < 1 || Integer.parseInt(addressIPv4Tokens[0]) > 254) {
			return false;
		} else if (Integer.parseInt(addressIPv4Tokens[1]) < 0 || Integer.parseInt(addressIPv4Tokens[1]) > 255) {
			return false;
		} else if (Integer.parseInt(addressIPv4Tokens[2]) < 0 || Integer.parseInt(addressIPv4Tokens[2]) > 255) {
			return false;
		} else if (Integer.parseInt(addressIPv4Tokens[3]) < 1 || Integer.parseInt(addressIPv4Tokens[3]) > 254) {
			return false;
		} else {
			return true;
		}
	}
}
