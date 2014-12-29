package cat.app.gmap;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.*;
import android.location.*;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.*;
import android.view.View;
import android.widget.Toast;

import cat.app.gmap.model.*;
import cat.app.gmap.nav.*;
import cat.app.gmap.task.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;

public class GMap extends MapFragment 
	implements OnMapLongClickListener, OnMyLocationChangeListener, OnMarkerClickListener {
	//GooglePlayServicesClient.ConnectionCallbacks,
    //GooglePlayServicesClient.OnConnectionFailedListener

	//https://maps.googleapis.com/maps/api/place/search/json?
	//location=-33.8670522,151.1957362&radius=500&types=grocery_or_supermarket
	//&sensor=true&key=AIzaSyApl-_heZUCRD6bJ5TltYPn4gcSCy1LY3A
    private HashMap<String,String> settings = new HashMap<String, String>();
	private static final String TAG = "GMap";
	public String myCountryCode;
	public String travelMode;
	public MainActivity activity;
	public GoogleMap map;
	public LatLng myLatLng;
	public LocationManager lm;
	public List<SuggestPoint> suggestPoints = new ArrayList<SuggestPoint>();
	public List<Polyline> routesPolyLines = new ArrayList<Polyline>();
	//public List<Route> routes = new ArrayList<Route>();
	public List<Step> steps = new ArrayList<Step>();
	//public List<LatLng> startPointOfSteps = new ArrayList<LatLng>();
	public Map<String,Marker> routeMarkers=new TreeMap<String,Marker>();
	public Map<String,Marker> remindMarkers=new TreeMap<String,Marker>();//like police/camera/accident
	public TreeMap<String,MarkerPoint> routeMarkerpoints=new TreeMap<String,MarkerPoint>();
	public Map<String,String> instructionToMp3 = new HashMap<String,String>();
	public List<String> playedMp3 = new ArrayList<String>();
	public int markerMaxSeq = 1;
	public int currentStepIndex = 0;//0 -> instructionToMp3(1), 1 -> instructionToMp3(2)
	public boolean onRoad =false;
	
	public void init(final MainActivity activity){
		this.activity= activity;
		initStorage();
		initMap();
        settings.put("TrafficEnabled", "false");
	}
	private void initMap() {
		LocationManager lm=(LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
		Location myloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		FragmentManager myFM = activity.getFragmentManager();
		MapFragment fragment = (MapFragment) myFM.findFragmentById(R.id.map);
		map = fragment.getMap();
		if (myloc != null) {
			myLatLng = new LatLng(myloc.getLatitude(),myloc.getLongitude());
	        move(myLatLng,13);
	    }
		map.getUiSettings().setCompassEnabled(false);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.setMyLocationEnabled(true);
    	map.getUiSettings().setMyLocationButtonEnabled(true);
    	map.setBuildingsEnabled(true);
    	//map.setIndoorEnabled(true);
    	map.setBuildingsEnabled(true);
    	map.setOnMapLongClickListener(this);
    	map.setOnMyLocationChangeListener(this);
    	map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				removeMarker(marker);
			}
        });
        map.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
            	activity.listSuggestion.setVisibility(View.INVISIBLE);
            	activity.listVoice.setVisibility(View.INVISIBLE);
            	Util.closeKeyBoard(activity);
            }
        });
	}
	public void removeMarker(Marker marker){
		if(marker!=null){
			marker.remove();
			routeMarkerpoints.remove(marker.getId());
			routeMarkers.remove(marker.getId());
			updateMarkerSeq();
			//refreshRoute(true);
			markerMaxSeq--;
		}
	}
	private void initStorage() {
		File folder = new File(Util.baseDir);
		Util.createFolder(folder);
	}
	private void updateMarkerSeq() {
		Iterator<Entry<String, MarkerPoint>> iter = routeMarkerpoints.entrySet().iterator();
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
    	MarkerPoint mp = new MarkerPoint(marker.getId(),markerMaxSeq,point.getDetailAddr(),point.getPoliticalAddr(),point.getLatLng());
    	routeMarkerpoints.put(marker.getId(), mp);
    	routeMarkers.put(marker.getId(), marker);
    	markerMaxSeq++;
    	//Log.i(TAG, "id="+marker.getId());
    	this.activity.openPopup(mp);
    }
	public void addRedPointMarker(SuggestPoint point){
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createBitmap(R.drawable.red_point));
    	Marker marker = map.addMarker(new MarkerOptions()
	        .title(point.getDetailAddr())
	        .snippet(point.getPoliticalAddr())
	        .position(point.getLatLng())
	        .icon(bd)
        );
    }
	public void addRemindMarker(MarkerPoint point,int resId){
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createBitmap(resId));
    	Marker marker = this.routeMarkers.get(point.getId());
    	marker.setIcon(bd);
    	routeMarkerpoints.remove(marker.getId());
    	routeMarkers.remove(point.getId());
    	remindMarkers.put(point.getId(), marker);
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
	@Override
	public void onMapLongClick(LatLng point) {
		//right drawer popup
   	 	GoogleSearchByPointTask task = new GoogleSearchByPointTask(this, point);
		task.execute();
	}
    public List<LatLng> getWaypoints(){
    	List<LatLng> ll = new ArrayList<LatLng>();
    	Iterator<Entry<String, MarkerPoint>> iter = routeMarkerpoints.entrySet().iterator();
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
	public void refreshRoute(boolean restart) {
		if(restart){//redraw all routes
			removePreviousRoute();
			playedMp3.clear();
			steps.clear();
			instructionToMp3.clear();
			currentStepIndex=0;
			redrawRoutes(myLatLng);
		}else{ //draw only last route
			Entry<String,MarkerPoint> lastEntry = routeMarkerpoints.pollLastEntry();
			LatLng end = lastEntry.getValue().getLatLng();
			LatLng start=null;
			if(routeMarkerpoints.size()==0) {
				start=myLatLng;
			}else{
				start=routeMarkerpoints.lastEntry().getValue().getLatLng();
			}
			GoogleRouteTask task = new GoogleRouteTask(this,start,end,travelMode);
            task.execute();
			routeMarkerpoints.put(lastEntry.getKey(), lastEntry.getValue());
		}
	}
	public void redrawRoutes(LatLng loc){
		//Toast.makeText(activity, "draw lines", Toast.LENGTH_LONG).show();
		if(routeMarkerpoints.size()>0){
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
	public void startMyCountryCodeTask(){
		//TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    //String countryCode = tm.getSimCountryIso();
		if(myCountryCode==null){
			(new GoogleCountryCodeTask(activity,myLatLng)).execute();
		}
	}
	public void findNewRouteSpeech(int old_size){
        for(int i=old_size;i<steps.size();i++){
        	String hintFile = Util.createVoiceFileName("hint",i);//baseDir+/GMap/routes/hint/
        	String hint = steps.get(i).getStartHint();
        	instructionToMp3.put(hint, hintFile);
        	String maneuver = steps.get(i).getManeuver();
        	if(maneuver!=null){
		    	String maneuverFile = Util.createVoiceFileName("maneuver",i);
		    	instructionToMp3.put(maneuver, maneuverFile);
        	}
        }
        (new TextToSpeechTask(this,instructionToMp3)).execute();
	}
	
	@Override
	public void onMyLocationChange(Location arg0) {
		myLatLng = new LatLng(arg0.getLatitude(),arg0.getLongitude());
		startMyCountryCodeTask();
		if(steps.size()>0){
			(new FindMyStepTask(activity)).execute(myLatLng);
			playStartHint(currentStepIndex);
			float toCurrentStart = Util.getDistance(myLatLng, steps.get(currentStepIndex).getStartLocation());
			float toCurrentEnd   = Util.getDistance(myLatLng, steps.get(currentStepIndex).getEndLocation());
			if(toCurrentStart<Util.hintBeforeTurn){
				onRoad=true;
			}
			if(toCurrentEnd<Util.hintBeforeTurn)
				playEndHint(currentStepIndex);
			
			//String text = "onRoad="+onRoad+"("+this.playedMp3.size()+"),myStep="+currentStepIndex+",toStart="+(int)(toCurrentStart)+",toEnd="+(int)toCurrentEnd;
			//Toast.makeText(activity, null, Toast.LENGTH_SHORT).show();
			//activity.openPopupDebug(text);
			//Log.i(TAG, "myStep="+currentStepIndex+",toCurrentStart="+toCurrentStart+",toCurrentEnd="+toCurrentEnd);
			//Log.i(TAG, "played hints="+playedMp3.size());
		}
	}
	private void play(String hint){
		String fileName = this.instructionToMp3.get(hint);
		if(fileName == null) return ;
		File file = new File(fileName);
		if(file.exists() && file.length()>0 && !playedMp3.contains(fileName)){
			Player.startPlaying(fileName);
			playedMp3.add(fileName);
			Toast.makeText(activity, hint, Toast.LENGTH_LONG).show();
		}
	}
	
	private void playStartHint(int stepId){
		play(steps.get(stepId).getStartHint());
	}
	private void playEndHint(int stepId){
		play(steps.get(stepId).getEndHint());
	}
	@Override
	public boolean onMarkerClick(Marker arg0) {
		MarkerPoint mp = new MarkerPoint(arg0.getId(),0,arg0.getTitle(),arg0.getSnippet(),arg0.getPosition());
		activity.openPopup(mp);
		return true;
	}
	public void drawStepPoint(Step step, int seq){
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
	}
}

