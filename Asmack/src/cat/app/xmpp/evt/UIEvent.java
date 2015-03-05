package cat.app.xmpp.evt;

public class UIEvent {

	public static String DRAWER = "drawer";
	public static String KEYBOARD = "keyboard";
	public static String CLOSE = "close";
	public static String OPEN = "open";
	
	private String type;
	private String action;
	public UIEvent(String type, String action){
		this.setType(type);
		this.setAction(action);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
