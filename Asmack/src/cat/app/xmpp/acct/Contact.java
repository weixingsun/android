package cat.app.xmpp.acct;

public class Contact {

	private String hostname;
	private String username;
	private String status;

	public Contact(String host, String username, String status) {
		this.setHostname(host);
		this.setUsername(username);
		this.setStatus(status);
	}

	public String getHostname() {
		return hostname;
	}


	public void setHostname(String hostname) {
		this.hostname = hostname;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
