package cat.app.xmpp.evt;

public class MessageReceiveEvent {
	private String from; 
	private String body;
	public MessageReceiveEvent(String from, String body) {
		this.setFrom(from);
		this.setBody(body);
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
