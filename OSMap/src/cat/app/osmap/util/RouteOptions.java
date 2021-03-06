package cat.app.osmap.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.osmdroid.util.GeoPoint;

import com.graphhopper.routing.util.EncodingManager;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import cat.app.maps.OSM;
import cat.app.osmap.LOC;


public class RouteOptions {

	private static final String TAG = RouteOptions.class.getSimpleName();
	GeoPoint dest;
	//GraphHopperRouter
	//public static String GH_ROUTE_DATA_PATH = SavedOptions.GH_ROUTE_DATA_PATH;
	public static String GH_ROUTE_URL = "http://servicedata.vhostall.com/route/";	//nz.zip
	public static final String OSM = "OSM";
	public static final String GOOGLE = "Google";
	public static final String MAPQUEST = "MapQuest";
	public static final String GRAPHHOPPER = "GraphHopper"; //offline routing, geocoding still not a product
	public static final String OFFLINE = "Offline Route"; //GraphHopper shown name
	public static final String GISGRAPHY = "Gisgraphy";		//not used
	//private static String provider;
	public static LinkedHashMap<String, String> ROUTERS = new LinkedHashMap<String,String>();
	
	static{
		//provider=OSM;
		ROUTERS.put(OSM, OSM);
		ROUTERS.put(GOOGLE, GOOGLE);
		ROUTERS.put(MAPQUEST, MAPQUEST);
		ROUTERS.put(OFFLINE, OFFLINE);
		//ROUTERS.put(GISGRAPHY, GISGRAPHY);
	}
	public static void changeRouteProvider(String value) {
		if(!ROUTERS.containsValue(value)) {
			SavedOptions.routingProvider=OSM;
		}else{
			SavedOptions.routingProvider = value;
			if(getRouteFileFullName()==null){ //
				osm.startDownloadActivity(getRouteDownloadFileShortName());
			}
		}
		Log.w(TAG, "name="+value+",FullName="+getRouteFileFullName());
	}
	public static String getRouteFileFullName(){
		String routeFileName = null;
		String routeFilePath = getRouteFilePath();
		if(getRouteFilePath()!=null){
			routeFileName = routeFilePath+SavedOptions.GH_ROUTE_DATA_NAME;	//edges
		}
		File routeFile = new File(routeFileName);
		if(routeFile.exists() ){	//&& routeFile.exists()
			return routeFileName;
		}
		return null;
	}
	public static String getRouteFilePath(){
		String routeFileFullName = SavedOptions.sdcard+"/"+SavedOptions.GH_ROUTE_DATA_PATH+LOC.countryCode+"/";
		//String routeFileFullName =  SavedOptions.sdcard+"/"+SavedOptions.GH_ROUTE_DATA_PATH+SavedOptions.GH_ROUTE_DATA_NAME;
		File routeFilePath = new File(routeFileFullName);
		//File routeFile = new File(routeFileFullName);
		routeFilePath.mkdirs();
		if(routeFilePath.exists())
			return routeFileFullName;
		else
			return null;
	}
	public static String getRouteDownloadFileShortName() {
		return LOC.countryCode+SavedOptions.GH_ROUTE_DATA_ZIP_EXT;
	}
	/*public static void setProvider(String router) {

		Log.i(TAG, "setProvider="+router);
		if(!ROUTERS.containsKey(router)) {
			RouteOptions.provider=OSM;
		} else {
			RouteOptions.provider = router;
		}
		//Log.i(TAG, "RouteOptions.provider="+provider);
	}*/
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
	public static LinkedHashMap<String, String> GH_TRAVEL_MODES = new LinkedHashMap<String,String>();
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

	static{
		GH_TRAVEL_MODES.put("Fast",  EncodingManager.CAR+",fastest");
		GH_TRAVEL_MODES.put("Short", EncodingManager.CAR+",shortest");
		GH_TRAVEL_MODES.put("Walk",  EncodingManager.FOOT+",shortest");
		GH_TRAVEL_MODES.put("Bike",  EncodingManager.BIKE+",shortest");// not supported yet
		//GH_TRAVEL_MODES.put("Bus",   null);				// not supported yet
	}

	
	String MAPQUEST_API_KEY = "Fmjtd%7Cluu8296znl%2Crg%3Do5-9w1xdz";

	private static String travelMode;//Fast
	public static String getTravelMode(String provider){
		if(travelMode==null) travelMode="Fast";
		switch (provider) {
	    case GOOGLE:   return GOOGLE_TRAVEL_MODES.get(travelMode);
	    case MAPQUEST: return MAPQUEST_TRAVEL_MODES.get(travelMode);
	    //case GISGRAPHY: 
	    case OFFLINE:  return null;
	    case OSM:   return null;
	    default:      return "Fast";
		}
	}
	
	public ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
	public void setWayPoints(ArrayList<GeoPoint> points) {
		this.list=points;
	}
	public void setWayPoints(GeoPoint start, GeoPoint end){
		list.clear();
		list.add(start);
		list.add(end);
		this.dest=end;
	}

	public void redraw(GeoPoint start){
		list.clear();
		list.add(start);
		list.add(this.dest);
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
	    case "Walk":  return Color.RED;
	    case "Bus":   return Color.GREEN;
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