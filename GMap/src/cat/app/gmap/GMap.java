package cat.app.gmap;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMap;
import org.osmdroid.api.IPosition;
import org.osmdroid.api.IProjection;
import org.osmdroid.api.Marker;
import org.osmdroid.api.OnCameraChangeListener;

import android.app.FragmentManager;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cat.app.gmap.model.MarkerPoint;
import cat.app.gmap.task.GoogleSearchByPointTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;


public class GMap implements IMap, OnMapLongClickListener, OnMarkerClickListener, ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = GMap.class.getSimpleName();
	MainActivity activity;
	static GoogleMap gmap;

	GoogleApiClient mGoogleApiClient;
	private boolean mResolvingError = false;
	@Override
	public void addMarker(Marker arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void addPointsToPolyline(int arg0, IGeoPoint... arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int addPolyline(org.osmdroid.api.Polyline arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void clearPolyline(int arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public float getBearing() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public IGeoPoint getCenter() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IProjection getProjection() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float getZoomLevel() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public boolean isMyLocationEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setBearing(float arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setCenter(double arg0, double arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setMyLocationEnabled(boolean arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setOnCameraChangeListener(OnCameraChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setPosition(IPosition arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setZoom(float arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean zoomIn() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean zoomOut() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isTrafficEnabled() {
		return gmap.isTrafficEnabled();
	}
	public void setTrafficEnabled(boolean b) {
		gmap.setTrafficEnabled(b);
	}
	public void setMapType(int type) {
		gmap.setMapType(type);
	}

	public static boolean initGoogleMap(MainActivity activity) {
		//LocationManager lm=(LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
		//Location myloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		FragmentManager myFM = activity.getFragmentManager();
		MapFragment fragment = (MapFragment) myFM.findFragmentById(R.id.map_google);
		gmap = fragment.getMap();
		if(gmap == null){
			Log.e(TAG, "Failed to initialize the google map, try to install google play service package.");
			return false;
		}
		gmap.getUiSettings().setCompassEnabled(false);
		gmap.getUiSettings().setRotateGesturesEnabled(false);
		gmap.setMyLocationEnabled(true);
		gmap.getUiSettings().setMyLocationButtonEnabled(true);
		gmap.setBuildingsEnabled(true);
		gmap.setBuildingsEnabled(true);
		//googlemap.setOnMapLongClickListener(this);
		//googlemap.setOnMarkerClickListener(this);
    	//googlemap.setIndoorEnabled(true);
        /*map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				removeMarker(marker);
			}
        });*/
		/*
		googlemap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
            	activity.listSuggestion.setVisibility(View.INVISIBLE);
            	activity.listVoice.setVisibility(View.INVISIBLE);
            	//selectedMarker=null;
            	Util.closeKeyBoard(activity);
            }
        });*/
		return true;
	}

	@Override
	public boolean onMarkerClick(com.google.android.gms.maps.model.Marker arg0) {
		int type = 0;
		MarkerPoint mp = activity.gMap.routeMarkerPoints.get(arg0.hashCode());
		if(mp==null){
			mp = activity.gMap.remindMarkerPoints.get(arg0.hashCode());
			type = mp.getSeq();
		}
		//Log.i(TAG, "onMarkerClick.type="+type);
		//Log.i(TAG, "title="+arg0.getTitle()+",snippet="+arg0.getSnippet());
		//snippet is "" if user defined marker.
		if(arg0.getSnippet().length()==0 || arg0.getTitle().length()==0) {
	   	 	GoogleSearchByPointTask task = new GoogleSearchByPointTask(activity, arg0,type);
			task.execute();
		}else if(arg0.getTitle().indexOf("(")<0){
			String title=Util.getTitlePrefixFromType(type);
			String time = arg0.getTitle();
			arg0.setTitle(title+" ("+time+")");
		}
		activity.gMap.selectedMarker = arg0;
		activity.openPopup(arg0,type);
		return true;
	}
	@Override
	public void onMapLongClick(LatLng point) {
   	 	GoogleSearchByPointTask task = new GoogleSearchByPointTask(activity, point);
		task.execute();
	}
    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, createLocationRequest(), activity.gMap);
    }
    protected synchronized void buildGoogleApiClient() {
    	mGoogleApiClient = new GoogleApiClient.Builder(activity, this, this)
    	.addConnectionCallbacks(this)
    	.addOnConnectionFailedListener(this)
    	.addApi(LocationServices.API)
    	.build();
    }
    protected synchronized void cleanGoogleApiClient() {
    	mGoogleApiClient.disconnect();
    	mGoogleApiClient=null;
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		String errMsg = "Connect Failed to Google GMS:";
		Toast.makeText(activity, errMsg, Toast.LENGTH_LONG).show();
		if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
	        // The Android Wear app is not installed
			Toast.makeText(activity, errMsg+" API_UNAVAILABLE", Toast.LENGTH_LONG).show();
	    }
		if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(activity, Util.REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            activity.showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
	}
	@Override
	public void onConnected(Bundle arg0) {
		//Toast.makeText(activity, "Connected to Google GMS!", Toast.LENGTH_SHORT).show();
		Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (loc != null) {
    		activity.gMap.pos.setMyLatLng(new LatLng(loc.getLatitude(),loc.getLongitude()));
    		activity.gMap.move(activity.gMap.pos.getMyLatLng(),13);
        }
        startLocationUpdates();
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		//Toast.makeText(activity, "Connection suspended to Google GMS!", Toast.LENGTH_LONG).show();
		//mGoogleApiClient.connect();
	}
}
