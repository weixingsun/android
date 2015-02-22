package cat.app.net.p2p.eb;

public class DatabaseEvent {

	public DatabaseEvent(String name,String value) {
		this.setSettingsName(name);
		this.setSettingsValue(value);
	}
	public DatabaseEvent(int count) {
		this.count = count;
	}
	int count;
	private String settings_name;
	private String settings_value;

	public int getCount() {
		return count;
	}
	public String getSettingsName() {
		return settings_name;
	}
	void setSettingsName(String name) {
		this.settings_name = name;
	}
	public String getSettingsValue() {
		return settings_value;
	}
	void setSettingsValue(String value) {
		this.settings_value = value;
	}
	
}
