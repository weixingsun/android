package cat.app.navi;

import java.util.HashMap;

public class GeoOptions {

	public static final String GOOGLE = "Google";
	public static final String MAPQUEST = "MapQuest";
	public static final String GISGRAPHY = "Gisgraphy";	//not used
	public static final String OSM = "OSM";
	
	private static String geocoder;
	public static HashMap<String, String> GEO_CODERS = new HashMap<String,String>();
	static{
		geocoder=OSM;
		GEO_CODERS.put(GOOGLE, GOOGLE);
		GEO_CODERS.put(MAPQUEST, MAPQUEST);
		GEO_CODERS.put(GISGRAPHY, GISGRAPHY);
		GEO_CODERS.put(OSM, OSM);
	}
	public static void changeGeocoder(String geo) {
		setGeocoder(geo);
	}
	public static String getGeocoder() {
		if(geocoder==null) geocoder=GeoOptions.OSM;
		return geocoder;
	}
	public static void setGeocoder(String geocoder) {
		if(!GEO_CODERS.containsKey(geocoder)) GeoOptions.geocoder=OSM;
		GeoOptions.geocoder = geocoder;
	}
}
