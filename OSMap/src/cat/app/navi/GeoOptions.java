package cat.app.navi;

import java.util.HashMap;

public class GeoOptions {

	private static String geocoder;
	public static HashMap<String, String> GEO_CODERS = new HashMap<String,String>();
	static{
		geocoder="osm";
		GEO_CODERS.put("Google", "google");
		GEO_CODERS.put("OSM", "osm");
	}
	public static void changeGeocoder(String geo) {
		setGeocoder(geo);
	}
	public static String getGeocoder() {
		if(geocoder==null) geocoder="osm";
		return geocoder;
	}
	public static void setGeocoder(String geocoder) {
		GeoOptions.geocoder = geocoder;
	}
}
