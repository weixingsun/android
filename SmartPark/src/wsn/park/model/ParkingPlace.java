package wsn.park.model;

import org.osmdroid.util.GeoPoint;

public class ParkingPlace {

	public ParkingPlace(int id, int type, int status, double lat,
			double lng, String operator, String admin, String country, String comment) {
		this.id=id;
		this.setType(type);
		this.setStatus(status);
		this.lat=lat;
		this.lng=lng;
		this.setOperator(operator);
		this.admin=admin;
		this.country=country;
		this.setComment(comment);
	}
	public static final int UNAVAILABLE = 0;
	public static final int AVAILABLE = 1;
	public static final int WORKING = 2;
	public static final int ERROR = -1;
	
	//id,status,type,lat,lng,operator,admin,country,comment
	private int id,type,status;
	private String operator,admin,country,comment;
	private double lat,lng;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAdmin() {
		return admin;
	}
	public void setAdmin(String admin) {
		this.admin = admin;
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country_code) {
		this.country = country_code;
	}
	public GeoPoint getPosition(){
		return new GeoPoint(this.lat,this.lng);
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String toString(){
		String line1=ParkingPlace.class.getSimpleName()+"("+lat+","+lng+")["+"id="+id;
		String line2=",type="+type+",status="+status;
		String line3=",operator="+operator+",admin="+admin+",country="+country+"]";
		return line1+line2+line3;
		
	}
}
