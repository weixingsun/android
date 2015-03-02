package cat.app.net.p2p.eb;

public class StatusEvent {

	public static String TERMINATED="terminated";
	public static String FAILED="failed";
	public static String RUNNING="running";
	public StatusEvent(String status) {
		this.status = status;
	}

	String status;

	public String getStatus() {
		return status;
	}
	
}
