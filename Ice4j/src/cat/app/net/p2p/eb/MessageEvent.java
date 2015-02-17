package cat.app.net.p2p.eb;

public class MessageEvent {

	public MessageEvent(String content) {
		this.msg = content;
	}

	String msg;

	public String getMessage() {
		return msg;
	}
	
}
