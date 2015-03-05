package cat.app.xmpp.evt;

public class LoginEvent {
	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	private String status;
	public LoginEvent(String status){
		this.status = status;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
