package cat.app.gmap.nav;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import com.google.android.gms.maps.model.LatLng;

public class RouteParser {

	private static final Context context = null;
	private static final String ROUTES = "routes";
	private static final String SUMMARY = "summary";
	private static final String LEGS = "legs";
	private static final String STEPS = "steps";
	private static final String POINTS = "points";
	private static final String TEXT = "text";
	private static final String VALUE = "value";
	private static final String HTML_INSTRUCTION = "html_instructions";
	private static final String DISTANCE = "distance";
	private static final String DURATION = "duration";
	private static final String LATITUDE = "lat";
	private static final String LONGITUDE = "lng";
	private static final String POLYLINE = "polyline";
	private static final String END_LOCATION = "end_location";
	private static final String START_LOCATION = "start_location";
	private static final String MANEUVER = "maneuver";

	public static List<Route> parse(String routesJSONString) throws JSONException {
	    try {
	        List<Route> routeList = new ArrayList<Route>();
	        final JSONObject jSONObject = new JSONObject(routesJSONString);
	        JSONArray routeJSONArray = jSONObject.getJSONArray(ROUTES);
	        for (int m = 0; m < routeJSONArray.length(); m++) {
	        	Route route = new Route(context);
	        	JSONObject routesJSONObject = routeJSONArray.getJSONObject(m);
	            route.setSummary(routesJSONObject.getString(SUMMARY));
	            JSONArray legsJSONArray = routesJSONObject.getJSONArray(LEGS);
	            for (int b = 0; b < legsJSONArray.length(); b++) {
	            	Leg leg = new Leg();
	            	JSONObject legJSONObject = legsJSONArray.getJSONObject(b);
	                leg.setDistance(new Distance(legJSONObject.optJSONObject(DISTANCE).optString(TEXT), legJSONObject.optJSONObject(DISTANCE).optLong(VALUE)));
	                leg.setDuration(new Duration(legJSONObject.optJSONObject(DURATION).optString(TEXT), legJSONObject.optJSONObject(DURATION).optLong(VALUE)));
	                JSONArray stepsJSONArray = legJSONObject.getJSONArray(STEPS);
	                JSONObject stepJSONObject, stepDurationJSONObject, legPolyLineJSONObject, stepStartLocationJSONObject, stepEndLocationJSONObject;
	                LatLng stepStartLocationLatLng, stepEndLocationLatLng;
	                for (int i = 0; i < stepsJSONArray.length(); i++) {
	                    stepJSONObject = stepsJSONArray.getJSONObject(i);
	                    Step step = new Step();
	                    JSONObject stepDistanceJSONObject = stepJSONObject.getJSONObject(DISTANCE);
	                    step.setDistance(new Distance(stepDistanceJSONObject.getString(TEXT), stepDistanceJSONObject.getLong(VALUE)));
	                    stepDurationJSONObject = stepJSONObject.getJSONObject(DURATION);
	                    step.setDuration(new Duration(stepDurationJSONObject.getString(TEXT), stepDurationJSONObject.getLong(VALUE)));
	                    stepEndLocationJSONObject = stepJSONObject.getJSONObject(END_LOCATION);
	                    stepEndLocationLatLng = new LatLng(stepEndLocationJSONObject.getDouble(LATITUDE), stepEndLocationJSONObject.getDouble(LONGITUDE));
	                    step.setEndLocation(stepEndLocationLatLng);
	                    step.setHtmlInstructions(stepJSONObject.getString(HTML_INSTRUCTION));
	                    try{
	                    	step.setManeuver(stepJSONObject.getString(MANEUVER)); //sometimes null
	                    }catch (JSONException e){
	                    	//doesn't matter if no MANEUVER
	                    }
	                    legPolyLineJSONObject = stepJSONObject.getJSONObject(POLYLINE);
	                    String encodedString = legPolyLineJSONObject.getString(POINTS);
	                    step.setPoints(decodePolyLines(encodedString));
	                    stepStartLocationJSONObject = stepJSONObject.getJSONObject(START_LOCATION);
	                    stepStartLocationLatLng = new LatLng(stepStartLocationJSONObject.getDouble(LATITUDE), stepStartLocationJSONObject.getDouble(LONGITUDE));
	                    step.setStartLocation(stepStartLocationLatLng);
	                    leg.addStep(step);
	                }
	                route.addLeg(leg);
	            }
	            routeList.add(route);
	        }
	        return routeList;
	    } catch (JSONException e) {
	        throw e;
	    }
	}
	public static List<LatLng> getWholeRoutePoints(Route route){
		List<LatLng> list = new ArrayList<LatLng>();
		for (Leg leg:route.getLegs()){
			for(Step step:leg.getSteps()){
				list.addAll(step.getPoints());
			}
		}
		return list;
	}
    private static List<LatLng> decodePolyLines(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;  
                shift += 5;  
            } while (b >= 0x20);  
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);  
        }  
        return poly;  
    }  
}
