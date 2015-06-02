package wsn.park.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.maps.OSM;

import com.graphhopper.routing.util.EncodingManager;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;



public class RouteOptions {

	private static final String TAG = RouteOptions.class.getSimpleName();
	GeoPoint dest;
	//GraphHopperRouter
	//public static String GH_ROUTE_DATA_PATH = SavedOptions.GH_ROUTE_DATA_PATH;
	//http://www.androidmaps.co.uk/maps/australia-oceania/new-zealand.zip
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
			SavedOptions.selectedNavi=OSM;
		}else{
			SavedOptions.selectedNavi = value;
			if(getRouteFileFullName()==null){
				osm.startDownloadActivity(getRouteDownloadFileShortName());
			}
		}
		//Log.w(TAG, "name="+value+",FullName="+getRouteFileFullName());
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
	/** mapping from GraphHopper directions to MapQuest maneuver IDs: */
	//static final HashMap<Integer, Integer> GH_MANEUVERS;
	static final SparseArray<Integer> GH_MANEUVERS = new SparseArray<Integer>();
	//convert Graphhopper maneuver to MapQuest
	static {
		GH_MANEUVERS.put(-3, 5); //Sharp_Left(-3) = Sharp_Left(5)
		GH_MANEUVERS.put(-2, 4); //Left(-2) = Left(4)
		GH_MANEUVERS.put(-1, 3); //Slight_Left(-1) = Slight_Left(3)
		GH_MANEUVERS.put(0, 1);  //Continue(0) = Straight(1)
		GH_MANEUVERS.put(1, 6);  //Slight_Right(1) = Slight_Right(6)
		GH_MANEUVERS.put(2, 7);  //Right(2) = Right(7)
		GH_MANEUVERS.put(3, 8);  //Sharp_Right(3) = Sharp_Right(8)
		GH_MANEUVERS.put(4, 24); //Arrived(4) = DESTINATION(24)
		//MANEUVERS.put(4, 25); //Arrived(4) = DESTINATION_LEFT(25)
		//MANEUVERS.put(4, 26); //Arrived(4) = DESTINATION_LEFT(26)
		GH_MANEUVERS.put(6, 27); //UseRoundabout(6) = ROUNDABOUT1(27)
		//MANEUVERS.put(6, 28); //UseRoundabout(6) = ROUNDABOUT2(28)
	}
	/*
	// graphhopper maneuver
	public static final int LEAVE_ROUNDABOUT = -6; // for future use
	public static final int TURN_SHARP_LEFT = -3;
	public static final int TURN_LEFT = -2;
	public static final int TURN_SLIGHT_LEFT = -1;
	public static final int CONTINUE_ON_STREET = 0;
	public static final int TURN_SLIGHT_RIGHT = 1;
	public static final int TURN_RIGHT = 2;
	public static final int TURN_SHARP_RIGHT = 3;
	public static final int FINISH = 4;				//
	public static final int REACHED_VIA = 5; 		//?
	public static final int USE_ROUNDABOUT = 6;		//
	* */
	public static int getManeuverCodeByGH(int ghCode){
		Integer code = GH_MANEUVERS.get(ghCode);
		if (code != null)
			return code;
		else 
			return 0;
	}
	public static int getManeuverCode(int origCode) {
		int code = origCode;
		switch(SavedOptions.selectedNavi){
		case SavedOptions.OSM: code=origCode;break;
		case SavedOptions.Google: code=origCode;break;
		case SavedOptions.Graphopper: code=getManeuverCodeByGH(origCode);break;
			default: code=origCode;
		}
		return code;
	}
	//according to mapquest API
	public static String getTurnString(int turn){
		String str=null;
		switch(turn){
		case 1: str="Continue";break;
		case 2: str="Change Road Name";break;
		case 3: str="Slight Left";break;
		case 4: str="Turn Left";break;
		case 5: str="Sharp Left";break;
		case 6: str="Slight Right";break;
		case 7: str="Turn Right";break;
		case 8: str="Sharp Right";break;
		case 9: str="Stay Left";break;
		case 10: str="Stay Right";break;
		case 11: str="Stay Straight";break;
		case 12: str="Make a U-turn";break;
		case 15: str="Exit Left";break;
		case 16: str="Exit Right";break;
		case 20: str="Merge Left";break;
		case 21: str="Merge Right";break;
		case 22: str="Merge";break;
		case 23: str="Enter";break;
		case 24: str="Arrive at your Destination";break;
		case 25: str="Destination is on the Left";break;
		case 26: str="Destination is on the Right";break;
		case 27: str="Enter the roundabout and take the 1st exit";break;
		case 28: str="Enter the roundabout and take the 2st exit";break;
		case 29: str="Enter the roundabout and take the 3st exit";break;
		default: str= "";
		}
		return str;
	}
	public static int getGoogleManeuverTypeFromText(String origtext) {
		//Head <b>east</b> on <b>Elizabeth St</b> toward <b>Picton Ave</b>
		//Turn <b>left</b> onto <b>Picton Ave</b>
		//Turn <b>right</b> onto <b>Riccarton Rd</b>
		//Turn <b>right</b>
		//Turn <b>left</b>
		//At the roundabout, take the <b>2nd</b> exit onto <b>Riccarton Ave</b>
		//<b>Kahu Rd</b> turns slightly <b>right</b> and becomes <b>Kotare St</b><div style="font-size:0.9em">Destination will be on the right</div>
		int type = 0;
		String search_text = origtext.indexOf("Destination")>0?origtext.split("Destination")[0]:origtext;
		if(search_text.startsWith("Head")){
			type = 1;
		}else if(search_text.indexOf("left")>0){
			if(search_text.indexOf("slightly")>0) type = 3;
			else type = 4;
		}else if(search_text.indexOf("right")>0){
			if(search_text.indexOf("slightly")>0) type = 6;
			else type = 7;
		}else if(search_text.indexOf("roundabout")>0 ){
			if(search_text.indexOf("1st")>0) type = 27;
			else if(search_text.indexOf("2nd")>0) type = 28;
			else if(search_text.indexOf("3rd")>0) type = 29;
		}
		return type;
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