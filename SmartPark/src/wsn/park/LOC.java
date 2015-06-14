package wsn.park;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.model.DataBus;
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
import android.location.LocationProvider;
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
	//public static GeoPoint myLastPos;
	private int speed;
	OSM osm;
	//Wifi wifi;
	public Drawer dr = Drawer.INSTANCE();
	public static String countryCode = null;
	String provider;
	public int onRoadIndex=0;
	public Road road;
	//public Integer currIndex = -1;
	//public List<RoadNode> passedNodes = new ArrayList<RoadNode>();
	private static DataBus bus = DataBus.getInstance();
	public void init(OSM osm) {
		this.osm = osm;
		//wifi = new Wifi(act);
		this.dbHelper = DbHelper.getInstance(osm.act);
		getLocFromDB();
		//countryCode = CountryCode.getBySim(osm.act);
		Log.w(tag, "===========LOC.countryCode="+countryCode);
		if (openGPSEnabled()) {
			//createLocationRequest(1000,2000);
			provider = getGoodProvider(); //LocationManager.GPS_PROVIDER; //this.getProvider();
			startGPSLocation();
		}
	}

	private void getLocFromDB() {
		String strLastPos = dbHelper.getLastPosition();
		if(strLastPos != null){
			String[] lastPosDB = strLastPos.split(",");
			bus.setMyPoint(new GeoPoint(Double.valueOf(lastPosDB[1]),Double.valueOf(lastPosDB[2])));
			countryCode = lastPosDB[0];
			Log.i(tag, "getPosition from DB="+strLastPos+",lastPos="+bus.getMyPoint()+",code="+countryCode);
			//osm.move(myLastPos);
		}
		if(countryCode==null && NET.instance().isNetworkConnected()){
			//myPos = getNetLocation();
			//Log.w(tag, "===========LOC.myPos="+myPos);
			//osm.startTask("geo", new GeoPoint(myPos),"countryCode");
			//countryCode = CountryCode.getByLatLng(myPos.getLatitude(), myPos.getLongitude());
			//Log.w(tag, "countryCode="+countryCode);
			//dbHelper.updateGPS(0, myPos);
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
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
	}

	@Override
	public void onLocationChanged(Location loc) {
		myPos = loc;
		bus.setMyPoint(new GeoPoint(loc.getLatitude(),loc.getLongitude()));
		if (countryCode == null)
			countryCode = CountryCode.getByGeoPoint(bus.getMyPoint());
		(new FindMyStepTask()).execute();
		osm.mks.myLocMarker.setPosition(bus.getMyPoint());
		osm.map.invalidate();
		//playHintSounds(osm.loc.passedNodes.size());
		dbHelper.updateGPS(0, myPos);
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(this.provider.equals(lm.NETWORK_PROVIDER)){
			this.provider=lm.GPS_PROVIDER;
			startGPSLocation();
		}
		if(status==LocationProvider.TEMPORARILY_UNAVAILABLE || status==LocationProvider.OUT_OF_SERVICE){
			//if(provider.equals(LocationManager.NETWORK_PROVIDER)){
				//this.provider = LocationManager.GPS_PROVIDER;
//				if(getGoodProvider().equals(LocationManager.NETWORK_PROVIDER))
//					this.provider = LocationManager.GPS_PROVIDER;
//				Log.i(tag, "provider="+provider);
//				Toast.makeText(osm.act, "Location Provider changed to: "+provider, Toast.LENGTH_SHORT).show();
			//}
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(osm.act, "Location Disabled", Toast.LENGTH_LONG).show();
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
		//this.passedNodes.clear();
		//this.currIndex = -1;
		this.road = null;
	}
	public static GeoPoint getMyPoint() {
		if(LOC.myPos==null) return bus.getMyPoint();
		GeoPoint gp = new GeoPoint(LOC.myPos.getLatitude(),LOC.myPos.getLongitude());
		return gp;
	}
    private boolean isNetworkAvailable(){
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private Location getNetLocation(){   
        if(NET.instance().isNetworkConnected()){
        	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10, this);
        	Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			//Log.w(tag, "getPosition from NET,lastLat="+l.getLatitude());
            return l;
        }
        return null;
        //if(isGPSValiable())
        //    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
