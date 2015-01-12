package cat.app.navi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.RoutingAlgorithmFactorySimple;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;

import android.util.Log;

/** 
	offline routing class
 */
public class GraphHopperOfflineRoadManager extends RoadManager {

	//protected static final String ROUTE_FILE_PATH = RouteOptions.GH_ROUTE_DATA_PATH;
	public static final int STATUS_NO_ROUTE = Road.STATUS_TECHNICAL_ISSUE+1;

	public GraphHopper hopper = new GraphHopper().forMobile();
	protected String mServiceUrl;
	
	/** mapping from GraphHopper directions to MapQuest maneuver IDs: */
	static final HashMap<Integer, Integer> MANEUVERS;

	private static final String tag = GraphHopperOfflineRoadManager.class.getSimpleName();
	static {
		MANEUVERS = new HashMap<Integer, Integer>();
		MANEUVERS.put(0, 1); //Continue
		MANEUVERS.put(1, 6); //Slight right
		MANEUVERS.put(2, 7); //Right
		MANEUVERS.put(3, 8); //Sharp right
		MANEUVERS.put(-3, 5); //Sharp left
		MANEUVERS.put(-2, 4); //Left
		MANEUVERS.put(-1, 3); //Slight left
		MANEUVERS.put(4, 24); //Arrived
		MANEUVERS.put(5, 24); //Arrived at waypoint
	}
	
	/**
	 * @param apiKey GraphHopper API key, mandatory to use the public GraphHopper service. 
	 * @see <a href="http://graphhopper.com/#enterprise">GraphHopper</a> to obtain an API key. 
	 */
	public GraphHopperOfflineRoadManager(String path){
		super();
		hopper.setCHEnable(true);
		//hopper.setElevation(true);
		//hopper.setTraversalMode(traversalMode);
		hopper.setEnableInstructions(true);
		//RoutingAlgorithmFactorySimple factory = new RoutingAlgorithmFactorySimple();
		//hopper.setAlgorithmFactory(factory.createAlgo(arg0, arg1));
		hopper.load(path);
	}

	@Override public Road getRoad(ArrayList<GeoPoint> waypoints) {

		GeoPoint start = waypoints.get(0);
		GeoPoint end = waypoints.get(waypoints.size()-1);
		GHRequest req = new GHRequest(start.getLatitude(),start.getLongitude(),end.getLatitude(),end.getLongitude());
		req.setAlgorithm(AlgorithmOptions.DIJKSTRA_BI);
		//req.getHints().put("instructions", "true");
		GHResponse resp = hopper.route(req);
		PointList pointList = resp.getPoints();
		InstructionList hintList = resp.getInstructions();
		///////////////////////////////////////////////////////////////////////////////////
		Road road = new Road();
		if(pointList==null || pointList.size()==0){
			road = new Road(waypoints);
			road.mStatus = STATUS_NO_ROUTE;
			return road;
		}
			road.mRouteHigh = this.getHighPoints(pointList);
			for(Instruction hint:hintList){
				RoadNode node = new RoadNode();
				node.mLocation = new GeoPoint(hint.getPoints().getLat(0),hint.getPoints().getLon(0));
				node.mLength = hint.getDistance()/1000;
				node.mDuration = hint.getTime()/1000;
				node.mManeuverType = getManeuverCode(hint.getSign());
				node.mInstructions = hint.getName();
				road.mNodes.add(node);
			}
		Log.d(BonusPackHelper.LOG_TAG, "GraphHopper.getRoad - finished");
		return road;
	}
	
	protected int getManeuverCode(int direction){
		Integer code = MANEUVERS.get(direction);
		if (code != null)
			return code;
		else 
			return 0;
	}
	public ArrayList<GeoPoint> getHighPoints(PointList pointList){
		ArrayList<GeoPoint> list = new ArrayList<GeoPoint>();
		for(int i=0;i<pointList.size();i++){
			GeoPoint gp = new GeoPoint(pointList.getLat(i),pointList.getLon(i));
			list.add(gp);
		}
		return list;
	}
}

/*
 	protected String getUrl(ArrayList<GeoPoint> waypoints){
		StringBuffer urlString = new StringBuffer(mServiceUrl);
		for (int i=0; i<waypoints.size(); i++){
			GeoPoint p = waypoints.get(i);
			urlString.append("&point="+geoPointAsString(p));
		}
		//urlString.append("&instructions=true"); already set by default
		//urlString.append("&elevation="+(mWithElevation?"true":"false"));
		urlString.append(mOptions);
		return urlString.toString();
	}
*/
