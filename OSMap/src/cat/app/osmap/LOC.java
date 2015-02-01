package cat.app.osmap;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import cat.app.maps.MathUtil;
import cat.app.maps.OSM;
import cat.app.navi.task.FindMyStepTask;
import cat.app.osmap.ui.Drawer;
import cat.app.osmap.util.CountryCode;
import cat.app.osmap.util.DbHelper;
import cat.app.osmap.util.MapOptions;

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
	DbHelper dbHelper;
	private LocationManager lm;
	public Location myPos;
	private int speed;
	Activity act;
	OSM osm;
	public Drawer dr = Drawer.INSTANCE();
	public static String countryCode = null;
	String provider;
	public boolean onRoad=false;
	public boolean navigating = false;
	public Road road;
	//public Integer currIndex = -1;
	public List<RoadNode> passedNodes = new ArrayList<RoadNode>();
	public void init(Activity act, OSM osm) {
		this.act = act;
		this.osm = osm;
		this.dbHelper = DbHelper.getInstance();
		if (openGPSEnabled()) {
			provider = getGoodProvider() ; //LocationManager.GPS_PROVIDER; //this.getProvider();
			if(provider.equals(LocationManager.NETWORK_PROVIDER)){
				provider = LocationManager.GPS_PROVIDER;
			}
			//Log.i(tag, "gps provider="+provider);
			myPos = lm.getLastKnownLocation(provider);
			startGPSLocation();
			if(myPos!=null){ ///////////////////////////////////////////////////////////////////////
				countryCode = CountryCode.getByLatLng(myPos.getLatitude(), myPos.getLongitude());
				//Log.w(tag, "countryCode="+countryCode);
				if(countryCode==null && osm.rto.isNetworkAvailable()){
					osm.startTask("geo", new GeoPoint(myPos),"countryCode");
				}
				dbHelper.updateGPS(0, myPos);
			}else{
				countryCode = dbHelper.getCountryCode();
				if(countryCode==null){
					//show supported country list in left drawer
					dr.show("Country");
				}
			}
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
	public void onLocationChanged(Location loc) {
		myPos = loc;
		GeoPoint gp = new GeoPoint(loc.getLatitude(),loc.getLongitude());
		if (countryCode == null) {
			countryCode = CountryCode.getByGeoPoint(gp);
			//Log.w(tag, "countryCode="+countryCode);
			//if(countryCode==null && osm.rto.isNetworkAvailable())
				//osm.startTask("geo", gp,"countryCode");
			//choose country from list
		}
		//if(MathUtil.compare(osm.mks.testMarker.getPosition(), gp) ){
			//osm.mks.testMarker.setTitle(title)
		//}
		//speed = (int) (loc.getSpeed() * 3.6);
		//Log.i(tag, "point="+gp+",speed=" + speed);
		if(osm.mks.testMarker==null){
			osm.mks.initTestMarker(loc);
		}
		osm.mks.testMarker.setPosition(gp);
		osm.move(gp);
		osm.mks.updateMyLocationMarker(gp);
		(new FindMyStepTask(osm, osm.mks.testMarker.getPosition(),osm.mks.testMarker)).execute();

		dbHelper.updateGPS(0, myPos);//
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

	private String getNetworkProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // good quality
		criteria.setAltitudeRequired(false); // no altitude
		criteria.setBearingRequired(false); //
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return lm.getBestProvider(criteria, true);
	}
	private String getGoodProvider(){
        //All your normal criteria setup
        Criteria criteria = new Criteria();
        //Use FINE or COARSE (or NO_REQUIREMENT) here
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(true);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        return lm.getBestProvider(criteria, true);
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void cleanupRoad() {
		this.onRoad=false;
		this.passedNodes.clear();
		//this.currIndex = -1;
		this.navigating = false;
		this.road = null;
	}

}
