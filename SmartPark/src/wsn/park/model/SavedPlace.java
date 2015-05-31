package wsn.park.model;

public class SavedPlace {
	public SavedPlace(int id,String name,String admin,double lat,double lng,String machine,String user){
		this.id=id;
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.machine_code=machine;
		this.user_name=user;
	}
	public SavedPlace(String name,String admin,double lat,double lng){
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
	}
	//id, name, admin, lat,lng, machine_code, user_name
	private int id;
	private String name,admin, machine_code, user_name;
	private double lat,lng;
	private int getId() {
		return id;
	}
	private void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}

	public String getBriefName() {
		String[] names = name.split(",");
		
		return names[0]+", "+names[1];
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
	}
	public String getMachine_code() {
		return machine_code;
	}
	public void setMachine_code(String machine_code) {
		this.machine_code = machine_code;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
}
