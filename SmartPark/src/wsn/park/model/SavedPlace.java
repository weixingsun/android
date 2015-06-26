package wsn.park.model;

import org.osmdroid.util.GeoPoint;

public class SavedPlace implements Place {
	public SavedPlace(int id,String name,String admin,double lat,double lng,String countryCode,String machine,String user,int type,boolean star){
		this.id=id;
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
		this.machine_code=machine;
		this.user_name=user;
		this.type = type;
		this.star = star;
	}
	public SavedPlace(String name,String admin,double lat,double lng,String countryCode){
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
	}
	public SavedPlace(String name,String admin,double lat,double lng,String countryCode,double dist){
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
		this.dist=dist;
	}
	public SavedPlace(String name,String admin,double lat,double lng,String countryCode,int type){
		this.name=name;
		this.admin=admin;
		this.lat=lat;
		this.lng=lng;
		this.countryCode=countryCode;
		this.type = type;
	}
	public SavedPlace(String name,String admin,GeoPoint p,String countryCode,int type){
		this.name=name;
		this.admin=admin;
		this.lat=p.getLatitude();
		this.lng=p.getLongitude();
		this.countryCode=countryCode;
		this.type = type;
	}
	
	//id, name, admin, lat,lng, machine_code, user_name
	private int id,type;
	private String name,admin, machine_code, user_name,countryCode;
	private double lat,lng,dist;
	private boolean star;
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
		//String[] names = name.split(",");
		//return names[0]+", "+names[1];
		return name;
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
	public GeoPoint getPosition(){
		return new GeoPoint(this.lat,this.lng);
	}
	public double getDist() {
		return dist;
	}
	public void setDist(double dist) {
		this.dist = dist;
	}
	public boolean isStar() {
		return star;
	}
	public void setStar(boolean star) {
		this.star = star;
	}
	@Override
	public void setType(int type) {
		this.type=type;
	}
	@Override
	public int getType() {
		return type;
	}
}
