package cat.app.osmap;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.graphics.*;
import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.util.*;
import android.view.View;
import android.widget.Toast;

import cat.app.gmap.MainActivity;
import cat.app.gmap.R;
import cat.app.gmap.Util;
import cat.app.gmap.R.id;
import cat.app.gmap.model.*;
import cat.app.gmap.nav.*;
import cat.app.gmap.svc.NaviSVC;
import cat.app.gmap.task.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;

public class OSMap  {
	
    private HashMap<String,String> settings = new HashMap<String, String>();
	private static final String TAG = OSMap.class.getSimpleName();

	public MainActivity activity;
	//private static OSMap gmap;
	public String myCountryCode;
	public String travelMode;
	public GoogleMap map;
	public MyPosition pos = MyPosition.getInstance();
	public LocationManager lm;
	public List<SuggestPoint> suggestPoints = new ArrayList<SuggestPoint>();
	public List<Polyline> routesPolyLines = new ArrayList<Polyline>();
	
	
	private OSMap( MainActivity activity){
		this.activity= activity;
		//initStorage();
		initMap();
        settings.put("TrafficEnabled", "false");
        //this.naviSvc = new NaviSVC(activity);
	}
	private void initMap() {
		//LocationManager lm=(LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
		//Location myloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		FragmentManager myFM = activity.getFragmentManager();
		MapFragment fragment = (MapFragment) myFM.findFragmentById(R.id.map);
		map = fragment.getMap();
		if(map == null){
			Log.e(TAG, "Failed to initialize the google map, try to install google play service package.");
		}
		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.setMyLocationEnabled(true);
    	map.getUiSettings().setMyLocationButtonEnabled(true);
    	map.setBuildingsEnabled(true);
    	//map.setIndoorEnabled(true);
    	map.setBuildingsEnabled(true);
    	//map.setOnMapLongClickListener(this);
    	//map.setOnMarkerClickListener(this);
        /*map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				removeMarker(marker);
			}
        });*/
        map.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
            	activity.listSuggestion.setVisibility(View.INVISIBLE);
            	activity.listVoice.setVisibility(View.INVISIBLE);
            	//selectedMarker=null;
            	Util.closeKeyBoard(activity);
            }
        });
	}
	public void removeMarker(Marker marker,int type){
		if(marker!=null){
			marker.remove();
		}
	}
	public void updateMarker(Marker marker, SuggestPoint point){
		if(marker.getSnippet().length()==0||marker.getTitle().length()==0){
			String title=Util.getTitlePrefixFromType(point.getType());
			String time = marker.getTitle();
			marker.setTitle(title+" ("+time+")");
			marker.setSnippet(point.getMarkerTitle()+", "+point.getMarkerSnippet() );
		}
	}
	
	public void addRemindMarker(MarkerPoint mp,int type){
    	//markerMaxSeq--;
    }
	public void move(LatLng latlng, int zoomLevel){
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
	}
    public float getZoomLevel(){
    	return map.getCameraPosition().zoom;
    }
    private void clearRoute(){
		//removePreviousRoute();
		activity.player.arraysClear();
		//currentStepIndex=0;
		pos.clear();
    }

	public void switchSettings(String settingName){
		if(settings.get(settingName)==null) return;
		switch(settingName){
			case "TrafficEnabled":
				if(map.isTrafficEnabled()){
					map.setTrafficEnabled(false);
				}else{
					map.setTrafficEnabled(true);
				}
				break;
			default:
				Toast.makeText(activity, "Settings:"+settingName+" not found", Toast.LENGTH_LONG).show();
		}
	}

	protected void moveCamera() {
		//if(onRoad)
			move(pos.getMyLatLng(),10);
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	
}

