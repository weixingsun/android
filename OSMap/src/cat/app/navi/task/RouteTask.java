package cat.app.navi.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import cat.app.maps.OSM;
import cat.app.navi.GraphHopperOfflineRoadManager;
import cat.app.osmap.util.APIOptions;
import cat.app.osmap.util.MapOptions;
import cat.app.osmap.util.RouteOptions;
import cat.app.osmap.util.RuntimeOptions;
import cat.app.osmap.util.SavedOptions;

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
	OSM osm;
	Road road;
	RouteOptions ro;
	public RouteTask(OSM map , RouteOptions ro) {
		super();
		this.osm = map;
		this.ro = ro;
	}

	@Override
	protected Polyline doInBackground(GeoPoint... params) {
		if( !RuntimeOptions.getInstance(osm.act).isNetworkAvailable()
				&& !SavedOptions.routingProvider.equals(RouteOptions.OFFLINE) ){
			Log.w(TAG, "provider="+SavedOptions.routingProvider);
			return null;
		}
		roadManager = Routers.getRoadManager(SavedOptions.routingProvider);
		if(roadManager==null) return null;
		
		road = roadManager.getRoad(ro.list);
		if(road==null || road.mNodes==null) return null;
		osm.loc.road = road;
		osm.polyline = RoadManager.buildRoadOverlay(road, osm.act);
		osm.polyline.setWidth(10);
		osm.polyline.setColor(RouteOptions.getColor());
		if(roadManager.getEndAddress()!=null){
			osm.startAddr=roadManager.getStartAddress();
			osm.endAddr=roadManager.getEndAddress();
		}
		return osm.polyline;
	}
	@Override
    protected void onPostExecute(Polyline pl) {
		osm.mks.removeAllRouteMarkers();
		osm.mks.removePrevPolyline();
		if(road==null) return;
		osm.mks.addPolyline(pl);
		osm.mks.drawStepsPoint(road);
		osm.loc.passedNodes.clear();
		if(osm.endAddr!=null){
			osm.mks.updateRouteMarker(osm.endAddr);
		}
    }

}
