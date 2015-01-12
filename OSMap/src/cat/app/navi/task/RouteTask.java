package cat.app.navi.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import cat.app.maps.APIOptions;
import cat.app.maps.MapOptions;
import cat.app.maps.OSM;
import cat.app.navi.RouteOptions;

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
	OSM map;
	Road road;
	RouteOptions ro;
	public RouteTask(OSM map , RouteOptions ro) {
		super();
		this.map = map;
		this.ro = ro;
	}

	@Override
	protected Polyline doInBackground(GeoPoint... params) {
		roadManager = Routers.getRoadManager(RouteOptions.getRouteProvider());
		road = roadManager.getRoad(ro.list);
		if(road==null || road.mNodes==null) return null;
		Log.i(TAG, "road="+road.mNodes.size());
		Polyline pl = RoadManager.buildRoadOverlay(road, map.act);
		pl.setWidth(10);
		pl.setColor(RouteOptions.getColor());
		return pl;
	}
	@Override
    protected void onPostExecute(Polyline pl) {
		map.mks.removeAllRouteMarkers();
		map.mks.removePrevPolyline();
		if(road==null) return;
		map.mks.addPolyline(pl);
		map.mks.drawStepsPoint(road);
    }

}
