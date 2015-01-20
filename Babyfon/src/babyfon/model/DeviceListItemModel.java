package babyfon.model;

public class DeviceListItemModel {

	private String deviceName;
	private String deviceIP;

	public DeviceListItemModel(String deviceName, String deviceIP) {
		this.deviceName = deviceName;
		this.deviceIP = deviceIP;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public String getIP() {
		return this.deviceIP;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
}
