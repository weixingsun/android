package cat.app.navi;

import java.util.ArrayList;
import java.util.Arrays;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RouteTask extends AsyncTask<GeoPoint, String, Polyline>{

	private static final String TAG = RouteTask.class.getSimpleName();
	RoadManager roadManager;
	Activity act;
	OSM map;
	Road road;
	RouteOptions ro;
	public RouteTask(Activity act ,OSM map , RouteOptions ro) {
		super();
		this.act = act;
		this.map = map;
		this.ro = ro;
	}

	@Override
	protected Polyline doInBackground(GeoPoint... params) {
		//if(RouteOptions.travelMode!=null){
			roadManager = new MapQuestRoadManager(MapOptions.MAPQUEST_API_KEY);
			roadManager.addRequestOption("routeType="+RouteOptions.travelMode);
			Log.i(TAG, "Special API request:mode="+RouteOptions.travelMode);
		//}else{
			//roadManager = new OSRMRoadManager();
			//Log.i(TAG, "Default API request:mode="+RouteOptions.travelMode);
		//}
		road = roadManager.getRoad(ro.list);
		Polyline pl = RoadManager.buildRoadOverlay(road, act);
		pl.setWidth(10);
		return pl;
	}
	@Override
    protected void onPostExecute(Polyline pl) {
		pl.setColor(RouteOptions.getColor());
		map.removeAllRouteMarkers();
		map.addPolyline(pl);
		map.drawSteps(road);
    }

}
