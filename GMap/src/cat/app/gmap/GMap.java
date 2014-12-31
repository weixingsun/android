package cat.app.gmap;

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

public class GMap extends MapFragment 
	implements OnMapLongClickListener, OnMarkerClickListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener  {

	//https://maps.googleapis.com/maps/api/place/search/json?
	//location=-33.8670522,151.1957362&radius=500&types=grocery_or_supermarket
	//&sensor=true&key=AIzaSyApl-_heZUCRD6bJ5TltYPn4gcSCy1LY3A
	BackgroundTimerTask btt = new BackgroundTimerTask();
	
    private HashMap<String,String> settings = new HashMap<String, String>();
	private static final String TAG = "GMap";

	public MainActivity activity;
	private NaviSVC naviSvc ;
	public String myCountryCode;
	public String travelMode;
	public GoogleMap map;
	public LatLng myLatLng;
	public LocationManager lm;
	GoogleApiClient mGoogleApiClient;
	public List<SuggestPoint> suggestPoints = new ArrayList<SuggestPoint>();
	public List<Polyline> routesPolyLines = new ArrayList<Polyline>();
	//public List<Route> routes = new ArrayList<Route>();
	public List<Step> steps = new ArrayList<Step>();
	public SuggestPoint startPoint;
	public Marker selectedMarker;
	public Map<String,Marker> routeMarkers=new TreeMap<String,Marker>();
	public Map<String,Marker> remindMarkers=new TreeMap<String,Marker>();
	public Map<String,MarkerPoint> remindMarkerPoints=new TreeMap<String,MarkerPoint>();//like police/camera/accident
	public TreeMap<String,MarkerPoint> routeMarkerPoints=new TreeMap<String,MarkerPoint>();
	public SparseArray<String> startHintMp3 = new SparseArray<String>();
	public SparseArray<String> endHintMp3 = new SparseArray<String>();
	public SparseArray<String> playedStartMp3 = new SparseArray<String>();
	public SparseArray<String> playedEndMp3 = new SparseArray<String>();
	public int markerMaxSeq = 1;
	public int currentStepIndex = 0;//0 -> instructionToMp3(1), 1 -> instructionToMp3(2)
	public int previousStepIndex = -1;
	public boolean onRoad =false;
	private boolean StepChanged=false;
	LatLngBounds bounds;
	
	private boolean mResolvingError = false;

	
	public void init(final MainActivity activity){
		this.activity= activity;
		initStorage();
		initMap();
		bounds = map.getProjection().getVisibleRegion().latLngBounds;
        settings.put("TrafficEnabled", "false");
        //this.naviSvc = new NaviSVC(activity);
	}
	private void initMap() {
		//LocationManager lm=(LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
		//Location myloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		FragmentManager myFM = activity.getFragmentManager();
		MapFragment fragment = (MapFragment) myFM.findFragmentById(R.id.map);
		map = fragment.getMap();
		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.setMyLocationEnabled(true);
    	map.getUiSettings().setMyLocationButtonEnabled(true);
    	map.setBuildingsEnabled(true);
    	//map.setIndoorEnabled(true);
    	map.setBuildingsEnabled(true);
    	map.setOnMapLongClickListener(this);
    	map.setOnMarkerClickListener(this);
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
			if(type>0){
				this.remindMarkers.remove(marker.getId());
				this.remindMarkerPoints.remove(marker.getId());
				(new UserDataDeleteTask(this.activity, marker.getPosition(),Util.USER_ADMIN)).execute();
			}else{
				routeMarkerPoints.remove(marker.getId());
				routeMarkers.remove(marker.getId());
				updateMarkerSeq();
				//refreshRoute(true);
				markerMaxSeq--;
			}
		}
	}
	private void initStorage() {
		File folder = new File(Util.baseDir);
		Util.createFolder(folder);
	}
	private void updateMarkerSeq() {
		Iterator<Entry<String, MarkerPoint>> iter = routeMarkerPoints.entrySet().iterator();
    	for(int i=1;iter.hasNext();i++){
    		Entry<String,MarkerPoint> entry = iter.next();
    		if(entry.getValue().getSeq()!=i){
    			BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createSeqBitmap(i));
    			routeMarkers.get(entry.getKey()).setIcon(bd);
    		}
    	}
	}
	private Bitmap createSeqBitmap(int seq){
		Bitmap bmRaw = BitmapFactory.decodeResource(activity.getResources(), R.drawable.marker_blue_32);
		return Util.generatorSequencedIcon(bmRaw,seq);
	}
	private Bitmap createBitmap(int resId){
		Bitmap bmRaw = BitmapFactory.decodeResource(activity.getResources(), resId);
		return bmRaw;
	}
	public void addRouteMarker(SuggestPoint point){
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createSeqBitmap(markerMaxSeq));
    	Marker marker = map.addMarker(new MarkerOptions()
	        .title(point.getDetailAddr())
	        .snippet(point.getPoliticalAddr())
	        .position(point.getLatLng())
	        .icon(bd)
        );
		selectedMarker=marker;
    	MarkerPoint mp = new MarkerPoint(marker.getId(),markerMaxSeq,point.getDetailAddr(),point.getPoliticalAddr(),point.getLatLng());
    	routeMarkerPoints.put(marker.getId(), mp);
    	routeMarkers.put(marker.getId(), marker);
    	markerMaxSeq++;
    	//Log.i(TAG, "id="+marker.getId());
    	this.activity.openPopup(marker,0);
    }
	public void updateMarker(Marker marker, SuggestPoint point){
		if(marker.getSnippet().length()==0||marker.getTitle().length()==0){
			String title=Util.getTitlePrefixFromType(point.getType());
			String time = marker.getTitle();
			marker.setTitle(title+" ("+time+")");
			marker.setSnippet(point.getMarkerTitle()+", "+point.getMarkerSnippet() );
		}
	}
	
	public void addRemindMarker(SuggestPoint point,int type){
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createBitmap(Util.getResByType(type)));
    	//Marker marker = this.routeMarkers.get(point.getId());
		Marker marker = map.addMarker(new MarkerOptions()
	        .title(point.getDetailAddr())
	        .snippet(point.getPoliticalAddr())
	        .position(point.getLatLng())
	        .icon(bd)
    	);
    	marker.setIcon(bd);
    	remindMarkerPoints.put(marker.getId(), new MarkerPoint(marker,type));
    	remindMarkers.put(marker.getId(), marker);
    }
	public void addRemindMarker(MarkerPoint mp,int type){
		int resId = Util.getResByType(type);
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createBitmap(resId));
    	Marker marker = this.routeMarkers.get(mp.getId());
    	marker.setIcon(bd);
    	routeMarkerPoints.remove(marker.getId());
    	routeMarkers.remove(mp.getId());
    	mp.setSeq(type);
    	remindMarkerPoints.put(mp.getId(), mp);
    	remindMarkers.put(marker.getId(), marker);
    	markerMaxSeq--;
    }
	public void move(LatLng latlng){
		map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
	}
	public void move(LatLng latlng, int zoomLevel){
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoomLevel));
	}
    public float getZoomLevel(){
    	return map.getCameraPosition().zoom;
    }
    public List<LatLng> getWaypoints(){
    	List<LatLng> ll = new ArrayList<LatLng>();
    	Iterator<Entry<String, MarkerPoint>> iter = routeMarkerPoints.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String,MarkerPoint> entry = iter.next();
    		ll.add(entry.getValue().getLatLng());
    	}
    	//Collections.reverse(ll);
		return ll;
    }
    public void removePreviousRoute() {
    	if(routesPolyLines!=null){
	    	for(Polyline pl:routesPolyLines){
	    		pl.remove();
	    	}
    	}
	}
    private void clearRoute(){
		removePreviousRoute();
		playedStartMp3.clear();
		playedEndMp3.clear();
		steps.clear();
		this.startHintMp3.clear();
		this.endHintMp3.clear();
		currentStepIndex=0;
    }
	public void refreshRoute(boolean restart) {
		if(this.remindMarkerPoints.containsKey(this.selectedMarker.getId())){
			clearRoute();
			GoogleRouteTask task = new GoogleRouteTask(this,myLatLng,selectedMarker.getPosition(),travelMode);
            task.execute();
			return;
		}
		if(restart){//redraw all routes
			clearRoute();
			redrawRoutes(myLatLng);
		}else{ //draw only last route
			Entry<String,MarkerPoint> lastEntry = routeMarkerPoints.pollLastEntry();
			LatLng end = lastEntry.getValue().getLatLng();
			LatLng start=null;
			if(routeMarkerPoints.size()==0) {
				start=myLatLng;
			}else{
				start=routeMarkerPoints.lastEntry().getValue().getLatLng();
			}
			GoogleRouteTask task = new GoogleRouteTask(this,start,end,travelMode);
            task.execute();
			routeMarkerPoints.put(lastEntry.getKey(), lastEntry.getValue());
		}
		
	}
	public void redrawRoutes(LatLng loc){
		//Toast.makeText(activity, "draw lines", Toast.LENGTH_LONG).show();
		if(routeMarkerPoints.size()>0){
			LatLng start = loc;
    		for(LatLng dest:getWaypoints()){
	            GoogleRouteTask task = new GoogleRouteTask(this,start,dest,travelMode);
	            task.execute();
	            start=dest;
    		}
    	}else{
    		//Toast.makeText(activity, "No Target", Toast.LENGTH_LONG).show();
    	}
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

	public void findNewRouteSpeech(int old_size){
        for(int i=old_size;i<steps.size();i++){
        	startHintMp3.append(i, steps.get(i).getStartHint());
		    endHintMp3.append(i, steps.get(i).getEndHint());
        }
        (new TextToSpeechTask(this,startHintMp3,endHintMp3)).execute();
	}

	
	private void hintDetect() {
		Step s = steps.get(currentStepIndex);
		float toCurrentStart = Util.getDistance(myLatLng, s.getStartLocation());
		float toCurrentEnd   = Util.getDistance(myLatLng, s.getEndLocation());
		if(onRoad && !StepChanged && toCurrentStart>Util.hintBeforeTurn && toCurrentEnd>Util.hintBeforeTurn){
			(new FindMyStepTask(activity)).execute(myLatLng);
			if(currentStepIndex>previousStepIndex){
				this.StepChanged=true;
				previousStepIndex=currentStepIndex;
			}
		}
		if(toCurrentStart<Util.hintBeforeTurn){
			playStartHint(currentStepIndex);
			this.onRoad=true;
			this.StepChanged=false;
		}
		if(toCurrentEnd<Util.hintBeforeTurn){
			playEndHint(currentStepIndex);
			this.StepChanged=false;
		}
	}
	private void endDetect() {
		if(currentStepIndex<steps.size()-1) return;
		LatLng last = steps.get(steps.size()-1).getEndLocation();
		float toEnd = Util.getDistance(myLatLng, last);
		if(toEnd<Util.hintBeforeTurn){ 
			clearRoute();
			activity.inputAddress.setText(this.startPoint.getFormatted_address());
		}
	}
	private void playStartHint(int stepId){
		if(playedStartMp3.get(stepId)==null){
			String fileName = Util.getVoiceFileName(Util.startHint, stepId);  //this.startHintMp3.get(stepId);
			File file = new File(fileName);
			if(file.exists() && file.length()>0){
				Player.startPlaying(fileName);
				playedStartMp3.append(stepId, fileName);
				Toast.makeText(activity, steps.get(stepId).getStartHint(), Toast.LENGTH_LONG).show();
			}
		}
	}
	private void playEndHint(int stepId){
		if(playedEndMp3.get(stepId)==null){
			String fileName = Util.getVoiceFileName(Util.endHint, stepId);
			File file = new File(fileName);
			if(file.exists() && file.length()>0 ){
				Player.startPlaying(fileName);
				playedEndMp3.append(stepId, fileName);
				Toast.makeText(activity, steps.get(stepId).getEndHint(), Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
   	 	GoogleSearchByPointTask task = new GoogleSearchByPointTask(this, point);
		task.execute();
	}
	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		int type = 0;
		MarkerPoint mp = routeMarkerPoints.get(arg0.getId());
		if(mp==null){
			mp = remindMarkerPoints.get(arg0.getId());
			type = mp.getSeq();
		}
		Log.i(TAG, "onMarkerClick.type="+type);
		if(arg0.getSnippet().length()==0 || arg0.getTitle().length()==0) {
	   	 	GoogleSearchByPointTask task = new GoogleSearchByPointTask(this, arg0,type);
			task.execute();
		}
		selectedMarker = arg0;
		activity.openPopup(arg0,type);
		return true;
	}
/*	public void drawStepPoint(Step step, int seq){
	String firstAddress = "Step "+(seq+1)+"/"+steps.size();
	String fullAddress = firstAddress+", "+step.getStartHint();
	fullAddress+= "\r\n"+step.getEndHint();
	SuggestPoint sp = new SuggestPoint(step.getStartLocation(), fullAddress);
	this.addRedPointMarker(sp);
}
public void drawAllStepPoints(){
	Log.i(TAG, "draw all step points");
	for (int i=0;i<steps.size();i++){
		Step s = steps.get(i);
		drawStepPoint(s,i);
	}
}*/
/*
public void addRedPointMarker(SuggestPoint point){
BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createBitmap(R.drawable.red_point));
Marker marker = map.addMarker(new MarkerOptions()
    .title(point.getDetailAddr())
    .snippet(point.getPoliticalAddr())
    .position(point.getLatLng())
    .icon(bd)
);
}*/
	////////////////////////////////////////////////////////////////////////////////////////////////
	Runnable backgroundR = new Runnable() {
		@Override
		public void run() {
			/*
			 * Message message = new Message(); message.what = DisplayMessage;
			 * MainView.myHandler.sendMessage(message);
			 */
			if(steps.size()>0){
				moveNavi();
				hintDetect();
				endDetect();
			}
			Log.i(TAG, "bg thread running");
		}
	};
	Handler myHandler = new Handler();
	private class BackgroundTimerTask extends TimerTask {
		@Override
		public void run() {
			myHandler.post(backgroundR);
		}
	}

	public void setupBGThreads() {
		Timer timer = new Timer(true);
		timer.schedule(btt, 1000, Util.THREAD_UPDATE_INTERVAL);
	}
	protected void moveNavi() {
		if(onRoad)
			move(myLatLng);
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////

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
    		myLatLng=new LatLng(loc.getLatitude(),loc.getLongitude());
    		move(myLatLng,13);
        }
        startLocationUpdates();
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		//Toast.makeText(activity, "Connection suspended to Google GMS!", Toast.LENGTH_LONG).show();
		//mGoogleApiClient.connect();
	}
	
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(Util.DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), Util.REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            //((MainActivity)getActivity()).onDialogDismissed();
        }
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
                mGoogleApiClient, createLocationRequest(), this);
    }
    protected synchronized void buildGoogleApiClient() {
    	mGoogleApiClient = new GoogleApiClient.Builder(activity, this, this)
    	.addConnectionCallbacks(this)
    	.addOnConnectionFailedListener(this)
    	.addApi(LocationServices.API)
    	.build();
    	}
	@Override
	public void onLocationChanged(Location loc) {
		myLatLng = new LatLng(loc.getLatitude(),loc.getLongitude());
		startMyCountryCodeTask();
		//Log.i(TAG, "onLocationChanged");
		//Intent i = new Intent(activity, NaviSVC.class);
		//i.putExtra("lat", loc.getLatitude());
		//i.putExtra("lng", loc.getLongitude());
		//activity.startService(i);
	}
	public void startMyCountryCodeTask(){
		//TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    //String countryCode = tm.getSimCountryIso();
		if( myCountryCode==null){
			(new GoogleCountryCodeTask(activity,myLatLng)).execute();
		}
	}
}

