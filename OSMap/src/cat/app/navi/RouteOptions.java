package cat.app.navi;

import java.util.ArrayList;
import java.util.HashMap;

import org.osmdroid.util.GeoPoint;

import android.graphics.Color;
import android.util.Log;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;


public class RouteOptions {

	private static final String TAG = RouteOptions.class.getSimpleName();
	static OSM osm;
	static RouteOptions opt;
	private RouteOptions(){
	}
	public static RouteOptions getInstance(OSM osm) {
		RouteOptions.osm = osm;
		if(opt==null) opt = new RouteOptions();
		return opt;
	}
	public static HashMap<String, String> TRAVEL_MODES = new HashMap<String,String>();
	static{
	TRAVEL_MODES.put("Fast", "fastest");
	TRAVEL_MODES.put("Short", "shortest");
	TRAVEL_MODES.put("Walk", "pedestrian");
	TRAVEL_MODES.put("Bike", "bicycle");
	TRAVEL_MODES.put("Bus", "multimodal");
	}
	public ArrayList<GeoPoint> list ;
	String MAPQUEST_API_KEY = "Fmjtd%7Cluu8296znl%2Crg%3Do5-9w1xdz";

	public static String travelMode;
	public void setWayPoints(ArrayList<GeoPoint> points) {
		this.list=points;
	}
	/*String bicycle = "routeType=bicycle";		//bike
	String multimodal = "routeType=multimodal"; //bus+walk
	String pedestrian = "routeType=pedestrian"; //walk
	String fastest = "routeType=fastest";		//car
	String shortest = "routeType=shortest";		//car
*/
	public static void changeTravelMode(String mode) {
		travelMode=mode;
	}
	public static int getColor() {
		if(travelMode==null || travelMode.equals("fastest")|| travelMode.equals("shortest")){return Color.BLUE;}
		if(travelMode.equals("pedestrian")|| travelMode.equals("shortest")){return Color.YELLOW;}
		if(travelMode.equals("multimodal")){return Color.RED;}
		return -1;
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