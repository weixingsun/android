package cat.app.navi;


public class RouteOptions {

	String MAPQUEST_API_KEY = "Fmjtd%7Cluu8296znl%2Crg%3Do5-9w1xdz";
	String bicycle = "routeType=bicycle";		//bike
	String multimodal = "routeType=multimodal"; //bus+walk
	String pedestrian = "routeType=pedestrian"; //walk
	String fastest = "routeType=fastest";		//car
	String shortest = "routeType=shortest";		//car
	
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