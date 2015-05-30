package wsn.park.navi.task;

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

import wsn.park.maps.OSM;
import wsn.park.navi.GraphHopperOfflineRoadManager;
import wsn.park.util.APIOptions;
import wsn.park.util.MapOptions;
import wsn.park.util.RouteOptions;
import wsn.park.util.RuntimeOptions;
import wsn.park.util.SavedOptions;


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
				&& !SavedOptions.selectedNavi.equals(RouteOptions.OFFLINE) ){
			Log.w(TAG, "provider="+SavedOptions.selectedNavi);
			return null;
		}
		roadManager = Routers.getRoadManager(SavedOptions.selectedNavi);
		if(roadManager==null) return null;
		
		try{
			road = roadManager.getRoad(ro.list);
		}catch(IllegalStateException e){
			return null;
		}
		if(road==null || road.mNodes==null) return null;
		osm.loc.road = road;
		osm.polyline = RoadManager.buildRoadOverlay(road, osm.act);
		osm.polyline.setWidth(10);
		osm.polyline.setColor(RouteOptions.getColor());
		/*if(roadManager.getEndAddress()!=null){
			osm.startAddr=roadManager.getStartAddress();
			osm.endAddr=roadManager.getEndAddress();
		}*/
		return osm.polyline;
	}
	@Override
    protected void onPostExecute(Polyline pl) {
		if(pl == null && SavedOptions.selectedNavi.equals(RouteOptions.OFFLINE)){
			//Toast.makeText(osm.act, "Please download new version routes files ", Toast.LENGTH_LONG).show();
			osm.startDownloadActivity(RouteOptions.getRouteDownloadFileShortName());
			return;
		}
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
