package cat.app.xmpp.acct;

public class Account {

	private String hostname;
	private String username;
	private String password;

	public Account(String host, String username, String password) {
		this.setHostname(host);
		this.setUsername(username);
		this.setPassword(password);
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

}
