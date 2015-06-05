package wsn.park;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.navi.task.FindMyStepTask;
import wsn.park.ui.Drawer;
import wsn.park.util.CountryCode;
import wsn.park.util.DbHelper;
import wsn.park.util.MapOptions;
import wsn.park.wifi.Wifi;

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
//import com.google.android.gms.location.LocationRequest;

public class LOC implements LocationListener {
	private static final String tag = LOC.class.getSimpleName();
	DbHelper dbHelper;
	private LocationManager lm;
	public static Location myPos;
	public static GeoPoint myLastPos;
	private int speed;
	OSM osm;
	//Wifi wifi;
	public Drawer dr = Drawer.INSTANCE();
	public static String countryCode = null;
	String provider;
	public int onRoadIndex=0;
	public Road road;
	//public Integer currIndex = -1;
	public List<RoadNode> passedNodes = new ArrayList<RoadNode>();
	public void init(OSM osm) {
		this.osm = osm;
		//wifi = new Wifi(act);
		this.dbHelper = DbHelper.getInstance();
		if (openGPSEnabled()) {
			provider = getGoodProvider() ; //LocationManager.GPS_PROVIDER; //this.getProvider();
			//createLocationRequest(1000,2000);
			if(provider.equals(LocationManager.NETWORK_PROVIDER)){
				provider = LocationManager.GPS_PROVIDER;
			}
			//Log.i(tag, "gps provider="+provider);
			myPos = lm.getLastKnownLocation(provider);
			startGPSLocation();
			if(myPos!=null){ ///////////////////////////////////////////////////////////////////////
				countryCode = CountryCode.getByLatLng(myPos.getLatitude(), myPos.getLongitude());
				//Log.w(tag, "countryCode="+countryCode);
				if(osm.rto.isNetworkAvailable()){
					if(countryCode==null ){
						osm.startTask("geo", new GeoPoint(myPos),"countryCode");
					}
				}
				dbHelper.updateGPS(0, myPos);
			}else{
				String strLastPos = dbHelper.getLastPosition();
				if(strLastPos != null){
					String[] lastPosDB = strLastPos.split(",");
					LOC.myLastPos = new GeoPoint(Double.valueOf(lastPosDB[1]),Double.valueOf(lastPosDB[2]));
					countryCode = lastPosDB[0];
					Log.i(tag, "str="+strLastPos+",lastPos="+myLastPos+",code="+countryCode);
					//osm.move(myLastPos);
				}
			}
		}
	}

	private boolean openGPSEnabled() {
		lm = (LocationManager) osm.act.getSystemService(Context.LOCATION_SERVICE);
		if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			// ||lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
			// Log.i(tag, "GPS enabled");
			return true;
		}
		Toast.makeText(osm.act, "Please enable GPS.", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		osm.act.startActivityForResult(intent, 0);
		return false;
	}

	private void startGPSLocation() {
		lm.requestLocationUpdates(provider, 2000, 10, this);
	}

	@Override
	public void onLocationChanged(Location loc) {
		myPos = loc;
		myLastPos = new GeoPoint(loc.getLatitude(),loc.getLongitude());
		if (countryCode == null)
			countryCode = CountryCode.getByGeoPoint(myLastPos);
		osm.mks.myLocMarker.setPosition(myLastPos);
		if(Mode.getID()==Mode.NAVI){
			osm.move(myLastPos);
			(new FindMyStepTask()).execute();
		}
		dbHelper.updateGPS(0, myPos);
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(osm.act, "Location Disabled", Toast.LENGTH_LONG).show();
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
	/*protected void createLocationRequest(int min, int max) {
	    LocationRequest mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(max);
	    mLocationRequest.setFastestInterval(min);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}*/
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void cleanupRoad() {
		this.onRoadIndex=0;
		this.passedNodes.clear();
		//this.currIndex = -1;
		this.road = null;
	}
	public static GeoPoint getMyPoint() {
		GeoPoint gp = new GeoPoint(LOC.myPos.getLatitude(),LOC.myPos.getLongitude());
		return gp;
	}
}

/*
	//Log.w(tag, "countryCode="+countryCode);
	//if(countryCode==null && osm.rto.isNetworkAvailable())
		//osm.startTask("geo", gp,"countryCode");
	//choose country from list
	//if(MathUtil.compare(osm.mks.testMarker.getPosition(), gp) ){
		//osm.mks.testMarker.setTitle(title)
	//}
	//speed = (int) (loc.getSpeed() * 3.6);
	//Log.i(tag, "point="+gp+",speed=" + speed);
 * */
