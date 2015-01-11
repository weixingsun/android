package cat.app.navi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.osmdroid.util.GeoPoint;

import android.graphics.Color;
import android.util.Log;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;


public class RouteOptions {

	private static final String TAG = RouteOptions.class.getSimpleName();

	public static final String GOOGLE = "Google";
	public static final String MAPQUEST = "MapQuest";
	public static final String GISGRAPHY = "Gisgraphy";		//not used
	public static final String GRAPHHOPPER = "GraphHopper"; //no API key,geocoding still not a product
	public static final String OSM = "OSM";
	private static String provider;
	public static LinkedHashMap<String, String> ROUTERS = new LinkedHashMap<String,String>();
	static{
		provider=OSM;
		ROUTERS.put(OSM, OSM);
		ROUTERS.put(GOOGLE, GOOGLE);
		ROUTERS.put(MAPQUEST, MAPQUEST);
		ROUTERS.put(GISGRAPHY, GISGRAPHY);
		ROUTERS.put(GRAPHHOPPER,GRAPHHOPPER);
	}
	public static void changeRouteProvider(String r) {
		setProvider(r);
	}
	public static String getRouteProvider() {
		if(provider==null) provider=RouteOptions.OSM;
		return provider;
	}
	public static void setProvider(String router) {
		if(!ROUTERS.containsKey(router)) RouteOptions.provider=OSM;
		else RouteOptions.provider = router;
	}
	static OSM osm;
	static RouteOptions opt;
	private RouteOptions(){
	}
	public static RouteOptions getInstance(OSM osm) {
		RouteOptions.osm = osm;
		if(opt==null) opt = new RouteOptions();
		return opt;
	}
	public static LinkedHashMap<String, String> MAPQUEST_TRAVEL_MODES = new LinkedHashMap<String,String>();
	public static LinkedHashMap<String, String> GOOGLE_TRAVEL_MODES = new LinkedHashMap<String,String>();
	static{
		MAPQUEST_TRAVEL_MODES.put("Fast",  "fastest");
		MAPQUEST_TRAVEL_MODES.put("Short", "shortest");
		MAPQUEST_TRAVEL_MODES.put("Walk",  "pedestrian");
		MAPQUEST_TRAVEL_MODES.put("Bike",  "bicycle");
		MAPQUEST_TRAVEL_MODES.put("Bus",   "multimodal");
	}
	static{
		GOOGLE_TRAVEL_MODES.put("Fast",  "driving");
		GOOGLE_TRAVEL_MODES.put("Short", "driving");
		GOOGLE_TRAVEL_MODES.put("Walk",  "walking");
		GOOGLE_TRAVEL_MODES.put("Bike",  "bicycling");
		GOOGLE_TRAVEL_MODES.put("Bus",   "transit");
	}

	public ArrayList<GeoPoint> list ;
	String MAPQUEST_API_KEY = "Fmjtd%7Cluu8296znl%2Crg%3Do5-9w1xdz";

	private static String travelMode;//Fast
	public static String getTravelMode(String provider){
		if(travelMode==null) travelMode="Fast";
		switch (provider) {
	    case GOOGLE:   return GOOGLE_TRAVEL_MODES.get(travelMode);
	    case MAPQUEST: return MAPQUEST_TRAVEL_MODES.get(travelMode);
	    case GISGRAPHY:
	    case GRAPHHOPPER:  return null;
	    case OSM:   return null;
	    default:      return "Fast";
		}
	}
	public void setWayPoints(ArrayList<GeoPoint> points) {
		this.list=points;
	}
	public static void changeTravelMode(String mode) {
		travelMode=mode;
	}
	public static int getColor() {
		if(travelMode==null) return  Color.BLUE;
	    switch (travelMode) {
	    case "Fast":
	    case "Short": return Color.BLUE;
	    case "Bike":
	    case "Walk":  return Color.YELLOW;
	    case "Bus":   return Color.RED;
	    default:      return -1;
	    }
	}
}
/*
Guidance Route Data										Narrative
ManeuverType: STRAIGHT		Link: Jonestown Rd			Go southwest on Jonestown Rd
ManeuverType: RIGHT			Link: Lincoln School Rd		Turn right on Lincoln School Rd
ManeuverType: RIGHT			Link: US-22					Turn right on US-22/Allentown Blvd
ManeuverType: EXIT_RIGHT	Link: NO ROAD NAME			Exit right
ManeuverType: RIGHT			Link: PA-72 N				Turn right on PA-72 N
ManeuverType: LEFT			Link: PA-443				Turn left on PA-443/Moonshine Rd
ManeuverType: DESTINATION	Link: PA-443				Arrive at GREEN POINT, PA
*/

/*
Limited Access - Highways
Toll Road
Ferry
Unpaved
Seasonal Closure - Approximate. Season roads might not be relected with 100% accuracy.
Country Crossing
*/
/*
 On a 350km trip (in France, Rennes-Paris), the duration of an end-to-end route retrieval (including full parsing of server response) is, on an average of 5 requests:

    With MapQuest Open API: 6 seconds (5s min, 7s max)
    With Google Directions API: 1.6s (1.5s min, 1.9s max)
    With OSRM demo service: 0.8s (0.4s min, 1.2s max)
    With GraphHopper service: not measured yet, but seems similar to OSRM 

Yes, OSRM and GraphHopper are really fast! 
 * */