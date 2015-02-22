package cat.app.net.p2p.eb;

public class SdpEvent {

	public SdpEvent(String host,String sdp, String group) {
		this.host = host;
		this.sdp = sdp;
		this.group = group;
	}

	String host;
	String sdp;
	String group;

	public String getSdp() {
		return sdp;
	}
	public String getHost() {
		return host;
	}
	public String getGroup() {
		return group;
	}
}
