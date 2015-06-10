package wsn.park.model;

import org.osmdroid.util.GeoPoint;

public class SavedPlace implements Place {
	public SavedPlace(int id,String name,String admin,double lat,double lng,String countryCode,String machine,String user,int special){
		this.id=id;
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
		this.machine_code=machine;
		this.user_name=user;
		this.special = special;
	}
	public SavedPlace(String name,String admin,double lat,double lng,String countryCode){
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
	}
	public SavedPlace(String name,String admin,double lat,double lng,String countryCode,int special){
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
		this.special = special;
	}
	public SavedPlace(String name,String admin,GeoPoint p,String countryCode,int special){
		this.name=name;
		this.admin=admin;
		this.lat=p.getLatitude();
		this.lng=p.getLongitude();
		this.countryCode=countryCode;
		this.special = special;
	}
	
	//id, name, admin, lat,lng, machine_code, user_name
	private int id,special;
	private String name,admin, machine_code, user_name,countryCode;
	private double lat,lng;
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String country_code) {
		this.countryCode = country_code;
	}
	public int getSpecial() {
		return special;
	}
	public void setSpecial(int special) {
		this.special = special;
	}
	public GeoPoint getPosition(){
		return new GeoPoint(this.lat,this.lng);
	}
}
