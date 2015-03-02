package cat.app.net.p2p.core;

public class P2PServerInfo {

	public P2PServerInfo(String turnServerIP,int turnServerPort,String turnServerUsername,String turnServerPassword){
		this.turnServerIP = turnServerIP;
		this.turnServerPort = turnServerPort;
		this.turnServerUsername = turnServerUsername;
		this.turnServerPassword = turnServerPassword;
		this.stunServerIP = turnServerIP;
		this.stunServerPort = turnServerPort;
	}
	public P2PServerInfo(String stunServerIP,int stunServerPort,String turnServerIP,int turnServerPort,String turnServerUsername,String turnServerPassword){
		this(turnServerIP,turnServerPort,turnServerUsername,turnServerPassword);
		this.stunServerIP=stunServerIP;
		this.stunServerPort=stunServerPort;
	}
	private String turnServerIP;
	private int turnServerPort;
	private String turnServerUsername;
	private String turnServerPassword;
	private String stunServerIP;
	private int stunServerPort;
	public String getTurnServerIP() {
		return turnServerIP;
	}
	public void setTurnServerIP(String turnServerIP) {
		this.turnServerIP = turnServerIP;
	}
	public int getTurnServerPort() {
		return turnServerPort;
	}
	public void setTurnServerPort(int turnServerPort) {
		this.turnServerPort = turnServerPort;
	}
	public String getTurnServerUsername() {
		return turnServerUsername;
	}
	public void setTurnServerUsername(String turnServerUsername) {
		this.turnServerUsername = turnServerUsername;
	}
	public String getTurnServerPassword() {
		return turnServerPassword;
	}
	public void setTurnServerPassword(String turnServerPassword) {
		this.turnServerPassword = turnServerPassword;
	}
	public String getStunServerIP() {
		return stunServerIP;
	}
	public void setStunServerIP(String stunServerIP) {
		this.stunServerIP = stunServerIP;
	}
	public int getStunServerPort() {
		return stunServerPort;
	}
	public void setStunServerPort(int stunServerPort) {
		this.stunServerPort = stunServerPort;
	}
}
