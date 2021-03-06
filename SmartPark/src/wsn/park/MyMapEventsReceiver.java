package wsn.park;

import java.util.ArrayList;
import java.util.Locale;

import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;

import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.model.Place;
import wsn.park.model.SavedPlace;
import wsn.park.ui.marker.OsmMapsItemizedOverlay;
import wsn.park.util.GeoOptions;
import wsn.park.util.RouteOptions;
import wsn.park.util.SavedOptions;

import android.location.Address;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MyMapEventsReceiver implements MapEventsReceiver{
	OSM osm;
	private String tag = MyMapEventsReceiver.class.getSimpleName();
	public MyMapEventsReceiver(OSM osm){
		this.osm = osm;
	}
	@Override
	public boolean longPressHelper(GeoPoint p) {
		if (osm.net.isNetworkConnected() ) {	//|| loc.myPos == null
			if(Mode.getID()!=Mode.NAVI)
				osm.startTask("geo", p,"route");
			//Log.i(tag , "long press network available");
		}else{
			/*if(SavedOptions.selectedNavi!=null && SavedOptions.selectedNavi.equals(RouteOptions.OFFLINE)){
				//TODO offline geo
				Toast.makeText(osm.act, GeoOptions.OFFLINE_GEOCODING_UNAVAILABLE, Toast.LENGTH_LONG).show();
			}*/
			Toast.makeText(osm.act, GeoOptions.NETWORK_UNAVAILABLE, Toast.LENGTH_LONG).show();
			Place place = GeoOptions.getLLPlace(p);
			osm.mks.updateTargetMarker(place);
			osm.dv.openPlacePopup(place);
		}
		return false;
	}
	@Override
	public boolean singleTapConfirmedHelper(GeoPoint arg0) {
		osm.dv.closeAllList();
		return false;
	}
	
}
