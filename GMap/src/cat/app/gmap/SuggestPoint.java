package cat.app.gmap;

import com.google.android.gms.maps.model.LatLng;

public class SuggestPoint {

	public String getMarkerTitle() {
		return formatted_address.split(",")[0];
	}
	public String getMarkerSnippet() {
		return formatted_address.split(",")[0];
	}
	public String getFormatted_address() {
		return formatted_address;
	}
	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}
	public LatLng getLocation() {
		return location;
	}
	public void setLocation(LatLng location) {
		this.location = location;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	private String formatted_address; //"10A Elizabeth Street, Riccarton, Christchurch 8011, New Zealand"
	private LatLng location;	// [ "lat" : -43.5344743, "lng" : 172.6039153 ]
	private String types; //[ "street_address" ]
	public SuggestPoint(LatLng location, String formatted_address, String types){
		this.location = location;
		this.formatted_address=formatted_address;
		this.types=types;
	}
	public SuggestPoint(LatLng ll, String formatted_address) {
		this.location = ll;
		this.formatted_address=formatted_address;
	}
}
