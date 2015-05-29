package wsn.park;

import java.util.ArrayList;
import java.util.Locale;

import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;

import wsn.park.maps.OSM;
import wsn.park.util.GeoOptions;
import wsn.park.util.RouteOptions;
import wsn.park.util.SavedOptions;

import android.location.Address;
import android.widget.Toast;

public class MyMapEventsReceiver implements MapEventsReceiver{
	OSM osm;
	public MyMapEventsReceiver(OSM osm){
		this.osm = osm;
	}
	@Override
	public boolean longPressHelper(GeoPoint p) {
		if(SavedOptions.selectedNavi!=null && SavedOptions.selectedNavi.equals(RouteOptions.OFFLINE)){
			//osm.mks.updateRouteMarker(addr);
		}else if (osm.rto.isNetworkAvailable() ) {	//|| loc.myPos == null
			osm.startTask("geo", new GeoPoint(p),"route");
		}else{
			Toast.makeText(osm.act, GeoOptions.NETWORK_UNAVAILABLE, Toast.LENGTH_LONG).show();
		}
		osm.ro.setWayPoints(new GeoPoint(osm.loc.myPos),p);
		osm.startTask("route", new GeoPoint(p),"route");
		return false;
	}
	@Override
	public boolean singleTapConfirmedHelper(GeoPoint arg0) {
		osm.dv.closeAllList();
		return false;
	}
	
}
