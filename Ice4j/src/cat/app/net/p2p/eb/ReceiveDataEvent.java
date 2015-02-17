package cat.app.net.p2p.eb;

public class ReceiveDataEvent {

	public ReceiveDataEvent(String host,String msg) {
		this.host = host;
		this.msg = msg;
	}
	private String host;
	private String msg;

	public String getMessage() {
		return msg;
	}
	
	public String getHost() {
		return host;
	}
}
