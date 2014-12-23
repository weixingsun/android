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
	implements OnMapLongClickListener, OnMyLocationChangeListener {
	//GooglePlayServicesClient.ConnectionCallbacks,
    //GooglePlayServicesClient.OnConnectionFailedListener

	//https://maps.googleapis.com/maps/api/place/search/json?
	//location=-33.8670522,151.1957362&radius=500&types=grocery_or_supermarket
	//&sensor=true&key=AIzaSyApl-_heZUCRD6bJ5TltYPn4gcSCy1LY3A
    private HashMap<String,String> settings = new HashMap<String, String>();
	private static final String TAG = "GMap";
	public String myCountryCode;
	public Map<String,String> instructionToMp3 = new HashMap<String,String>();
	public GoogleMap map;
	public MainActivity activity;
	public LatLng myLatLng;
	public LatLng myLastLatLng;
	public LocationManager lm;
	public String travelMode;
	public Map<String,Marker> markers=new TreeMap<String,Marker>();
	public TreeMap<String,MarkerPoint> markerpoints=new TreeMap<String,MarkerPoint>();
	public int markerMaxSeq = 1;
	public List<SuggestPoint> suggestPoints = new ArrayList<SuggestPoint>();
	public List<Route> routes = new ArrayList<Route>();
	public List<Step> steps = new ArrayList<Step>();
	public int currentStepIndex = 0;
	public int previousStepIndex = -1;
	public List<Polyline> routesPolyLines = new ArrayList<Polyline>();
	
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
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker marker) {
				marker.remove();
				markerpoints.remove(marker.getId());
				markers.remove(marker.getId());
				updateMarkerSeq();
				refreshRoute(true);
				markerMaxSeq--;
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

	private void initStorage() {
		File folder = new File(Util.baseDir);
		Util.createFolder(folder);
	}

	private void updateMarkerSeq() {
		Iterator<Entry<String, MarkerPoint>> iter = markerpoints.entrySet().iterator();
    	for(int i=1;iter.hasNext();i++){
    		Entry<String,MarkerPoint> entry = iter.next();
    		if(entry.getValue().getSeq()!=i){
    			BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createSeqBitmap(i));
    			markers.get(entry.getKey()).setIcon(bd);
    		}
    	}
	}

	private Bitmap createSeqBitmap(int seq){
		Bitmap bmRaw = BitmapFactory.decodeResource(activity.getResources(), R.drawable.marker_blue_32);
		return Util.generatorSequencedIcon(bmRaw,seq);
	}
	public void addMarker(SuggestPoint point){
		
		BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(createSeqBitmap(markerMaxSeq));
    	Marker marker = map.addMarker(new MarkerOptions()
	        .title(point.getMarkerTitle())
	        .snippet(point.getMarkerSnippet())
	        .position(point.getLocation())
	        .icon(bd)
        );
    	MarkerPoint mp = new MarkerPoint(markerMaxSeq,point.getMarkerTitle(),point.getMarkerSnippet(),point.getLocation());
    	markerpoints.put(marker.getId(), mp);
    	markers.put(marker.getId(), marker);
    	refreshRoute(false);
    	markerMaxSeq++;
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
   	 	GoogleSearchByPointTask task = new GoogleSearchByPointTask(this, point);
		task.execute();
	}

    public List<LatLng> getWaypoints(){
    	List<LatLng> ll = new ArrayList<LatLng>();
    	Iterator<Entry<String, MarkerPoint>> iter = markerpoints.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String,MarkerPoint> entry = iter.next();
    		ll.add(entry.getValue().getLatlng());
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
			routes.clear();
			steps.clear();
			instructionToMp3.clear();
			currentStepIndex=0;
			previousStepIndex=-1;
			redrawRoutes(myLatLng);
		}else{ //draw only last route
			Entry<String,MarkerPoint> lastEntry = markerpoints.pollLastEntry();
			LatLng end = lastEntry.getValue().getLatlng();
			LatLng start=null;
			if(markerpoints.size()==0) {
				start=myLatLng;
			}else{
				start=markerpoints.lastEntry().getValue().getLatlng();
			}
			GoogleRouteTask task = new GoogleRouteTask(this,start,end,travelMode);
            task.execute();
			markerpoints.put(lastEntry.getKey(), lastEntry.getValue());
		}
	}
	public void redrawRoutes(LatLng loc){
		//Toast.makeText(activity, "draw lines", Toast.LENGTH_LONG).show();
		if(markerpoints.size()>0){
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
		(new GoogleCountryCodeTask(activity,myLatLng)).execute();
	}
	@Override
	public void onMyLocationChange(Location arg0) {
		myLatLng = new LatLng(arg0.getLatitude(),arg0.getLongitude());
		if(myLastLatLng==null) myLastLatLng=myLatLng;
		if(myCountryCode==null){
			startMyCountryCodeTask();
		}
		if(Util.getDistance(myLatLng, myLastLatLng)>10){	//more than 10 meters
			myLastLatLng=myLatLng;
		}
		if(routes.size()>0){
			(new FindMyStepTask(activity)).execute(myLatLng); //currentStepIndex found
			if(previousStepIndex!=currentStepIndex){
				String hintHTML = this.steps.get(currentStepIndex).getHtmlInstructions();
				String hintFile = this.instructionToMp3.get(hintHTML);
				if(hintFile!=null){
					Player.play(hintFile);
				}
				previousStepIndex=currentStepIndex;
			}
		}
	}
	public void findNewRouteSpeech(int old_size){
		Handler handler = new Handler();
        for(int i=old_size;i<steps.size();i++){
        	String hintFile = Util.createHintFileName(i);//baseDir+/GMap/routes/hint/
        	String hintHTML = steps.get(i).getHtmlInstructions();
        	handler.postDelayed(null,10);
        	(new TextToSpeechTask(this,hintHTML,hintFile)).execute();	//gMap.instructionToMp3(hint,hintFile)
        }
	}
	
}
