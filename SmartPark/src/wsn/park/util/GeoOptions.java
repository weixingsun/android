package wsn.park.util;

import java.util.HashMap;
import java.util.LinkedHashMap;

import wsn.park.model.SavedPlace;

import android.location.Address;

public class GeoOptions {

	public static final String OSM = "Nominatim";
	public static final String GOOGLE = "Google";
	public static final String MAPQUEST = "MapQuest";
	public static final String GISGRAPHY = "Gisgraphy";		//not used
	public static final String GRAPHHOPPER = "GraphHopper"; //not used
	public static final String OFFLINE = "Offline";			//not used
	
	public static final String NETWORK_UNAVAILABLE = "Network Not Available, please try later or use offline map and routing.";
	public static final CharSequence OFFLINE_GEOCODING_UNAVAILABLE = "Offline Geocoding is not available yet, we may implement it in next release.";
	//private static String geocoder;
	public static LinkedHashMap<String, String> GEO_CODERS = new LinkedHashMap<String,String>();
	static{
		//geocoder=OSM;
		GEO_CODERS.put(OSM, OSM);			//
		GEO_CODERS.put(GOOGLE, GOOGLE);
		GEO_CODERS.put(MAPQUEST, MAPQUEST);
		//GEO_CODERS.put(GISGRAPHY, GISGRAPHY);
		//GEO_CODERS.put(OFFLINE, OFFLINE);
		//GEO_CODERS.put(GRAPHHOPPER, GRAPHHOPPER);
		
	}
	public static String getAddressName(Address a) {
		String display = null;
    	if(a.getExtras() != null){//Nominatim
    		String display1=a.getExtras().get("display_name").toString();
    		if(display1!=null ) display = display1;
    	}else{
    		/*if(a.getFeatureName() != null){//Google
	        	String feature = a.getFeatureName();
	        	String admin = a.getSubAdminArea()==null?a.getAdminArea():a.getSubAdminArea();
	        	String road = a.getThoroughfare()==null?a.getAddressLine(1):a.getThoroughfare();
	        	
    		}*/
    		display=a.getAddressLine(0)+", "+a.getAddressLine(1)+", "+a.getAddressLine(2)+", "+a.getCountryName();
    	}
    	return display;
	}
	public static SavedPlace getMyPlace(Address addr){
		SavedPlace sp = new SavedPlace(getAddressName(addr), addr.getAdminArea(), addr.getLatitude(), addr.getLongitude(),addr.getCountryCode());
		return sp;
	}
	/*public static String getGeocoder() {
		if(geocoder==null) geocoder=GeoOptions.GOOGLE;
		return geocoder;
	}
	public static void setGeocoder(String geocoder) {
		if(!GEO_CODERS.containsKey(geocoder)) GeoOptions.geocoder=OSM;
		GeoOptions.geocoder = geocoder;
	}*/
}
