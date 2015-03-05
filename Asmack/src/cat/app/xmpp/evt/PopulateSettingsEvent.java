package cat.app.xmpp.evt;

public class PopulateSettingsEvent {
	public static final String LAST_LOGIN = "last_login";
	public static final String LAST_PASSWORD = "last_password";
	private String settingsName;
	private String settingsValue;
	public PopulateSettingsEvent(String settingsName, String settingsValue) {
		this.settingsName = settingsName;
		this.settingsValue = settingsValue;
	}
	public String getSettingsName() {
		return settingsName;
	}
	public void setSettingsName(String settingsName) {
		this.settingsName = settingsName;
	}
	public String getSettingsValue() {
		return settingsValue;
	}
	public void setSettingsValue(String settingsValue) {
		this.settingsValue = settingsValue;
	}
	
}
