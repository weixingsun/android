package wsn.park.navi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

import wsn.park.util.RouteOptions;
import wsn.park.util.SavedOptions;


import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.RoutingAlgorithmFactorySimple;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.TraversalMode;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.util.Log;

/** 
	offline routing class
 */
public class GraphHopperOfflineRoadManager extends RoadManager {
	
	//protected static final String ROUTE_FILE_PATH = RouteOptions.GH_ROUTE_DATA_PATH;
	public static final int STATUS_NO_ROUTE = Road.STATUS_TECHNICAL_ISSUE+1;

	public GraphHopper hopper; // = new GraphHopper().forMobile();
	//protected String mServiceUrl;
	GeoPoint start;
	GeoPoint end;
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
		//hopper.setCHEnable(true);
		//hopper.setCHEnable(false);	//cannot load routing file ???
		//hopper.setCHWeighting("shortest");
		//hopper.setElevation(true);
		//hopper.setTraversalMode(TraversalMode.EDGE_BASED_2DIR);	//no nodes in road.
		//hopper.setEnableInstructions(true);
		//RoutingAlgorithmFactorySimple factory = new RoutingAlgorithmFactorySimple();
		//hopper.setAlgorithmFactory(factory.createAlgo(arg0, arg1));
		//Log.e(tag, "GH.path="+path);
		//hopper.load(path);
	}

	public GraphHopper getGraphHopper(String mode) throws IllegalStateException{
		hopper = new GraphHopper().forMobile();
		hopper.setCHEnable(false);
		//hopper.setCHWeighting("");
		hopper.setWayPointMaxDistance(5);
		hopper.setEnableInstructions(true);
		//hopper.setEncodingManager(new EncodingManager(mode).);
		hopper.load(RouteOptions.getRouteFilePath());
		return hopper;
	}
	@Override 
	public Road getRoad(ArrayList<GeoPoint> waypoints) throws IllegalStateException{
		start = waypoints.get(0);
		end = waypoints.get(waypoints.size()-1);
		GHRequest req = new GHRequest(start.getLatitude(),start.getLongitude(),end.getLatitude(),end.getLongitude());
		req.setAlgorithm(AlgorithmOptions.DIJKSTRA_BI);
		String vehicle = null;
		String weighting = null;
		if(RouteOptions.GH_TRAVEL_MODES.containsKey(SavedOptions.selectedTravelMode)){ //Bus not supported yet
			String value = RouteOptions.GH_TRAVEL_MODES.get(SavedOptions.selectedTravelMode);
			vehicle = value.split(",")[0];
			req.setVehicle(vehicle);
			weighting = value.split(",")[1];
			req.setWeighting(weighting);
		}
		if(vehicle==null) return null;
		//req.getHints().put("instructions", "true");
		hopper = getGraphHopper(vehicle);
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
			String ann = hint.getAnnotation().getImportance()+": "+hint.getAnnotation().getMessage();
			node.mInstructions = hint.getName()+" , "+ann;
			road.mNodes.add(node);
		}
		Log.d(BonusPackHelper.LOG_TAG, "GraphHopper.getRoad - finished");
		//findAddressNames(waypoints);
		return road;
	}
	
	public String getAddressName(GeoPoint point){
		QueryResult rua = hopper.getLocationIndex().findClosest(point.getLatitude(), point.getLongitude(), EdgeFilter.ALL_EDGES);
		return rua.getClosestEdge().getName();
	}
	public double getMaxSpeed(){
		return hopper.getGraph().getEncodingManager().getEncoder("CAR").getMaxSpeed();
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

	@Override
	public Address getEndAddress() {
		Address addr = new Address(Locale.getDefault());
		addr.setLatitude(end.getLatitude());
		addr.setLongitude(end.getLongitude());
		addr.setFeatureName(getAddressName(end));
		return addr;
	}

	@Override
	public Address getStartAddress() {
		Address addr = new Address(Locale.getDefault());
		addr.setLatitude(start.getLatitude());
		addr.setLongitude(start.getLongitude());
		addr.setFeatureName(getAddressName(start));
		return addr;
	}
}
