package wsn.park.model;

import org.osmdroid.util.GeoPoint;

public class ParkingPlace implements Place{

	public ParkingPlace(int id, int type, int status, double lat,
			double lng, String operator, String admin, String country, String comment) {
		setId(id);
		setType(type);
		setStatus(status);
		setLat(lat);
		setLng(lng);
		setOperator(operator);
		setAdmin(admin);
		setCountryCode(country);
		setComment(comment);
	}
	public static final int UNAVAILABLE = 0;
	public static final int AVAILABLE = 1;
	public static final int WORKING = 2;
	public static final int ERROR = -1;
	
	//id,status,type,lat,lng,operator,admin,country,comment
	private int id,status;
	private String operator,admin,country,comment;
	private double lat,lng;
	boolean star;
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
	public void setCountryCode(String country_code) {
		this.country = country_code;
	}
	@Override
	public GeoPoint getPosition(){
		return new GeoPoint(this.lat,this.lng);
	}
	public int getType() {
		return Place.PARK;
	}
	public void setType(int type) {
		
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
		String line2=",type="+Place.PARK+",status="+status+",operator="+operator;
		String line3=",admin="+admin+",country="+country+",comment="+comment+"]";
		return line1+line2+line3;
	}
	@Override
	public String getName() {//get distance from the point of search
		return getOperator()+": "+getComment();
	}
	@Override
	public String getCountryCode() {
		return this.country;
	}

	@Override
	public boolean isStar() {
		return star;
	}
	@Override
	public void setStar(boolean star) {
		this.star=star;
	}
	
}
