package cat.app.navi;

import java.util.Arrays;
import java.util.List;

import org.osmdroid.util.GeoPoint;

public class SuggestPoint {

	private int seq;	//waypoint sequence
	private int type; // 1 police, 2 camera, 3 medical
	private String formatted_address; //"10A Elizabeth Street, Riccarton, Christchurch 8011, New Zealand"
	private GeoPoint latlng;	// [ "lat" : -43.5344743, "lng" : 172.6039153 ]
	//private String detail; // 10A Elizabeth Street
	private String political; //Riccarton, Christchurch 8011, New Zealand
	private String countryCode;
	private List<String> addr ;
	
	public String getMarkerTitle() {
		return formatted_address.split(",")[0];
	}
	public String getMarkerSnippet() {
		return formatted_address.split(",")[1];
	}
	public String getFormatted_address() {
		return formatted_address;
	}
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	public GeoPoint getLatLng() {
		return latlng;
	}
	public void setLatLng(GeoPoint latlng) {
		this.latlng = latlng;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDetailAddr(){
		return addr.get(0);
	}
	public String getPoliticalAddr(){
		return political;
	}
	public String createPoliticalAddr(){
		StringBuffer sb = new StringBuffer();
		for(int i=1;i<addr.size();i++){
			sb.append(addr.get(i));
			if(i+1<addr.size()){
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	public SuggestPoint(GeoPoint location, String formatted_address, int types){
		this.latlng = location;
		this.formatted_address=formatted_address;
		this.type=types;
		addr = Arrays.asList(formatted_address.split(","));
		political=createPoliticalAddr();
	}
	public SuggestPoint(GeoPoint location, String formatted_address) {
		this(location,formatted_address,0);
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}
