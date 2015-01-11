package cat.app.navi.task;

import java.util.List;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.GoogleRoadManager;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

import cat.app.maps.APIOptions;
import cat.app.navi.GeoOptions;
import cat.app.navi.RouteOptions;

import android.app.Activity;
import android.location.Address;
import android.util.Log;

public class Routers {
	private static final String tag = Routers.class.getSimpleName();
	String provider;
	List<Address> list;
	Activity act;
	public Routers(Activity act) {
		this.act = act;
	}
	public static RoadManager getRoadManager(String provider) {
		RoadManager  roadManager = null;
		try{
		switch(provider){
			case RouteOptions.GOOGLE: {
				roadManager = new GoogleRoadManager();
				roadManager.addRequestOption("mode="+RouteOptions.getTravelMode(RouteOptions.getRouteProvider()));
				Log.i(tag, "GOOGLE route");
				break;
			}
			case RouteOptions.OSM: {
				Log.i(tag, "OSM route");
				roadManager =new OSRMRoadManager();
				return roadManager;
			}
			case RouteOptions.GISGRAPHY: {
				//com.gisgraphy.gisgraphoid
				Log.i(tag, "GISGRAPHY route");
				return null;
			}
			case RouteOptions.MAPQUEST: {
				roadManager = new MapQuestRoadManager(APIOptions.MAPQUEST_API_KEY);
				String opt = "routeType="+RouteOptions.getTravelMode(provider);
				roadManager.addRequestOption(opt);
				Log.i(tag, "MAPQUEST route opt="+opt);
				break;
			}
			case RouteOptions.GRAPHHOPPER: {
				roadManager = new GraphHopperRoadManager("");
				roadManager.addRequestOption("routeType="+RouteOptions.getTravelMode(""));
				Log.i(tag, "GRAPHHOPPER route");
				return null;
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
