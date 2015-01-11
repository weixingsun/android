package cat.app.osmap;

import org.osmdroid.util.GeoPoint;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;
import cat.app.navi.GeoOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LOC implements LocationListener {
	private static final String tag = LOC.class.getSimpleName();
	private LocationManager lm;
	public Location myPos;
	private int speed;
	Activity act;
	OSM osm;
	// public static boolean gps_fired = false;
	public String countryCode = null;
	String provider;
	private boolean navigating = false;

	public void init(Activity act, OSM osm) {
		this.act = act;
		this.osm = osm;
		if (openGPSEnabled()) {
			provider = this.getProvider();
			myPos = lm.getLastKnownLocation(provider);
			startGPSLocation();
			osm.startTask("geo", new GeoPoint(myPos));
			osm.setDefaultZoomLevel();
		}
	}

	private boolean openGPSEnabled() {
		lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
		if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			// ||lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
			// Log.i(tag, "GPS enabled");
			return true;
		}
		Toast.makeText(act, "Please enable GPS.", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		act.startActivityForResult(intent, 0);
		return false;
	}

	private void startGPSLocation() {
		lm.requestLocationUpdates(provider, 2000, 10, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		myPos = location;
		if (this.countryCode == null) {
			osm.startTask("geo", new GeoPoint(location));
		}
		if(navigating )
			MapOptions.move();
		speed = (int) (location.getSpeed() * 3.6);
		GeoPoint gp = new GeoPoint(location.getLatitude(),
				location.getLongitude());
		osm.updateMyLocationMarker(gp);
		Log.i(tag, "speed=" + speed);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(act, "Location Disabled", Toast.LENGTH_LONG).show();
	}

	private String getProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // good quality
		criteria.setAltitudeRequired(false); // no altitude
		criteria.setBearingRequired(false); //
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);//
		return lm.getBestProvider(criteria, true);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
