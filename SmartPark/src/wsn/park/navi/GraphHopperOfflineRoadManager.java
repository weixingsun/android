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
	//convert Graphhopper maneuver to MapQuest
	static {
		MANEUVERS = new HashMap<Integer, Integer>();
		MANEUVERS.put(-3, 5); //Sharp_Left(-3) = Sharp_Left(5)
		MANEUVERS.put(-2, 4); //Left(-2) = Left(4)
		MANEUVERS.put(-1, 3); //Slight_Left(-1) = Slight_Left(3)
		MANEUVERS.put(0, 1);  //Continue(0) = Straight(1)
		MANEUVERS.put(1, 6);  //Slight_Right(1) = Slight_Right(6)
		MANEUVERS.put(2, 7);  //Right(2) = Right(7)
		MANEUVERS.put(3, 8);  //Sharp_Right(3) = Sharp_Right(8)
		MANEUVERS.put(4, 24); //Arrived(4) = DESTINATION(24)
		//MANEUVERS.put(4, 25); //Arrived(4) = DESTINATION_LEFT(25)
		//MANEUVERS.put(4, 26); //Arrived(4) = DESTINATION_LEFT(26)
		MANEUVERS.put(6, 27); //UseRoundabout(6) = ROUNDABOUT1(27)
		//MANEUVERS.put(6, 28); //UseRoundabout(6) = ROUNDABOUT2(28)
	}
	/*
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
		if(RouteOptions.GH_TRAVEL_MODES.containsKey(SavedOptions.selectedBy)){ //Bus not supported yet
			String value = RouteOptions.GH_TRAVEL_MODES.get(SavedOptions.selectedBy);
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
			//Log.e(tag, "Instruction.sign="+hint.getSign());
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
