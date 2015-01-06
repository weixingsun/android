package cat.app.navi;

import java.util.ArrayList;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import cat.app.maps.OSM;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

public class RoutePolylineTask extends AsyncTask<ArrayList<GeoPoint>, String, Polyline>{

	private static final String TAG = RoutePolylineTask.class.getSimpleName();
	RoadManager roadManager = new OSRMRoadManager();
	Activity act;
	OSM map;
	Road road;
	public RoutePolylineTask(Activity act ,OSM map , RouteOptions ro) {
		super();
		this.act = act;
		this.map = map;
	}

	@Override
	protected Polyline doInBackground(ArrayList<GeoPoint>... params) {
		road = roadManager.getRoad(params[0]);
		//roadManager.addRequestOption("routeType=bicycle");
		//
		Polyline pl = RoadManager.buildRoadOverlay(road, act);
		pl.setColor(Color.BLUE);
		pl.setWidth(10);
		return pl;
	}
	@Override
    protected void onPostExecute(Polyline pl) {
		map.drawSteps(road);
		map.addPolyline(pl);
    }

}
