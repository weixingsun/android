package cat.app.xmpp.acct;

public class Contact {

	public static String AVAILABLE = "available";
	public static String ONLINE = "online";
	public static String OFFLINE = "offline";
	private String name;
	private String user;
	private String status;

	public Contact(String user, String name, String status) {
		this.setName(name);
		this.setUser(user);
		this.setStatus(status);
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
