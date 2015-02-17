package cat.app.net.p2p.eb;

public class RemoteSdpEvent {

	public RemoteSdpEvent(String host,String sdp) {
		this.host = host;
		this.sdp = sdp;
	}

	String host;
	String sdp;

	public String getSdp() {
		return sdp;
	}
	public String getHost() {
		return host;
	}
	
}
