package cat.app.osmap;

import org.osmdroid.util.GeoPoint;

import cat.app.maps.OSM;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LOC implements LocationListener {
	private static final String tag = LOC.class.getSimpleName();
	private LocationManager lm;
	private Location myPos;
	Activity act;
	OSM osm;
    public void init(Activity act,OSM osm) {
    	this.act = act;
    	this.osm = osm;
    	lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
        myPos = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(myPos!=null) {
        	osm.setDefaultCenter(new GeoPoint(myPos));
        }
	}
	@Override
	public void onLocationChanged(Location location) {
		myPos=location;
		GeoPoint gp = new GeoPoint(location.getLatitude(),location.getLongitude());
		osm.updateMyLocationMarker(gp);
		osm.setDefaultCenter(gp);
		Log.i(tag, "markers.size="+osm.getMarkerSize());
		osm.myLoc=location;
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//Toast.makeText(this, provider+" changed to "+status, Toast.LENGTH_LONG).show();
	}
	@Override
	public void onProviderEnabled(String provider) {
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(act, "Location Disabled", Toast.LENGTH_LONG).show();
	}
}
