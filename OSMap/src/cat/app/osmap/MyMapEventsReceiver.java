package cat.app.osmap;

import java.util.ArrayList;
import java.util.Locale;

import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;

import android.location.Address;
import android.widget.Toast;
import cat.app.maps.OSM;
import cat.app.navi.GeoOptions;

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
			Address addr = new Address(Locale.getDefault());
			addr.setLatitude(p.getLatitude());
			addr.setLongitude(p.getLongitude());
			addr.setFeatureName("Destination");
			osm.mks.updateRouteMarker(addr);
			Toast.makeText(osm.act, GeoOptions.NETWORK_UNAVAILABLE, Toast.LENGTH_LONG).show();
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
