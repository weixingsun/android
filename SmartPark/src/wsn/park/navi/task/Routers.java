package wsn.park.navi.task;

import java.util.List;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.GoogleRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.navi.GraphHopperOfflineRoadManager;
import wsn.park.util.APIOptions;
import wsn.park.util.GeoOptions;
import wsn.park.util.RouteOptions;
import wsn.park.util.RuntimeOptions;
import wsn.park.util.SavedOptions;


import android.app.Activity;
import android.location.Address;
import android.util.Log;

public class Routers {
	private static final String tag = Routers.class.getSimpleName();
	String provider;
	List<Address> list;
	static Activity act;
	public Routers(Activity act) {
		Routers.act = act;
	}
	public static RoadManager getRoadManager(String provider) {
		RoadManager roadManager = null;
		//Log.i(tag, "getRoadManager().provider="+provider);
		try{
		switch(provider){
			case RouteOptions.GOOGLE: {
				roadManager = new GoogleRoadManager();
				roadManager.addRequestOption("mode="+RouteOptions.getTravelMode(provider));
				Log.i(tag, "GOOGLE route");
				break;
			}
			case RouteOptions.OSM: {
				Log.i(tag, "OSM route");
				roadManager =new OSRMRoadManager();
				return roadManager;
			}
			/*case RouteOptions.GISGRAPHY: {
				//com.gisgraphy.gisgraphoid
				Log.i(tag, "GISGRAPHY route");
				return null;
			}*/
			case RouteOptions.MAPQUEST: {
				roadManager = new MapQuestRoadManager(APIOptions.MAPQUEST_API_KEY);
				String opt = "routeType="+RouteOptions.getTravelMode(provider);
				roadManager.addRequestOption(opt);
				Log.i(tag, "MAPQUEST route opt="+opt);
				break;
			}
			case RouteOptions.OFFLINE: {
				roadManager = new GraphHopperOfflineRoadManager(RouteOptions.getRouteFilePath());
				Log.i(tag, "GRAPHHOPPER Offline route");
				break;
			}
			default: 
				Log.i(tag, "default router ? "+provider);
				return null;
		}
		}catch(Exception e){
			Log.i(tag, "Exception"+e.getMessage());
		}
		return roadManager;
	}
}
