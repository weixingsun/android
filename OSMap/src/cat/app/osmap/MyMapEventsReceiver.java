package cat.app.osmap;

import java.util.ArrayList;

import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;

import android.widget.Toast;
import cat.app.maps.OSM;

public class MyMapEventsReceiver implements MapEventsReceiver{
	OSM osm;
	public MyMapEventsReceiver(OSM osm){
		this.osm = osm;
	}
	@Override
	public boolean longPressHelper(GeoPoint p) {
		if (osm.rto.isNetworkAvailable() ) {	//|| loc.myPos == null
			osm.startTask("geo", new GeoPoint(p),"route");
		}else{
			Toast.makeText(osm.act, "Network Not Available, please try Offline map and routing.", Toast.LENGTH_LONG).show();
		}
		ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
		points.add(new GeoPoint(osm.loc.myPos));
		points.add(p);
		osm.ro.setWayPoints(points);
		osm.startTask("route", new GeoPoint(p),"route");
		return false;
	}
	@Override
	public boolean singleTapConfirmedHelper(GeoPoint arg0) {
		osm.dv.closeAllList();
		return false;
	}
}
