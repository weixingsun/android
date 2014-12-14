package cat.app.gmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMap implements OnMapLongClickListener,OnMyLocationChangeListener {
	//GooglePlayServicesClient.ConnectionCallbacks,
    //GooglePlayServicesClient.OnConnectionFailedListener
	private static final String TAG = "GMap";
	GoogleMap map;
	MainActivity activity;
	Location loc;
	public Map<String,Marker> markers=new TreeMap<String,Marker>();
	//public Map<String,LatLng> markerLatLngs=new HashMap<String,LatLng>();
	public int markerSeq = 0;
	public String lastMarkerId;
	List<SuggestPoint> points = new ArrayList<SuggestPoint>();
	
	@SuppressLint("NewApi") 
	public void init(MainActivity activity){
		this.activity= activity;
		map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.getUiSettings().setCompassEnabled(true);
    	map.setMyLocationEnabled(true);
    	map.setBuildingsEnabled(true);
    	map.setOnMapLongClickListener(this);
    	map.setOnMyLocationChangeListener(this);
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			@Override
			public void onInfoWindowClick(Marker arg0) {
				arg0.remove();
				markers.remove(arg0.getId());
				if(!markers.containsKey(lastMarkerId)){
					lastMarkerId = getLastMarkerId(markers);
				}
				refreshRoute();
			}
        });
	}

	public void refreshRoute(LatLng currentLoc){
		//Toast.makeText(activity, "draw lines", Toast.LENGTH_LONG).show(); 
		GoogleMapRouteTask.removePreviousRoute();
		if(markers.size()>0){
            //LatLng end = markers.get(lastMarkerId).getPosition();
			LatLng start = currentLoc;
    		for(LatLng dest:getWaypoints()){
	            GoogleMapRouteTask task = new GoogleMapRouteTask(this,start,dest);
	            task.execute();
	            start=dest;
    		}
    	}else{
    		Toast.makeText(activity, "Please select a target", Toast.LENGTH_LONG).show();
    	}
	}

	protected String getLastMarkerId(Map<String, Marker> markers2) {
    	Iterator<Entry<String, Marker>> iter = markers.entrySet().iterator();
    	String key = null;
    	while(iter.hasNext()){
    		Entry<String,Marker> entry = iter.next();
    		key = entry.getKey();
    	}
		return key;
	}

	public void addMarker(MarkerPoint point){
    	
    	Marker marker = map.addMarker(new MarkerOptions()
        .title(point.getTitle())
        .snippet(point.getComment())
        .position(point.getLatlng()));
    	
    	markers.put(marker.getId(), marker);
    	lastMarkerId = marker.getId();
        //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    	refreshRoute();
    }
	public void addMarker(SuggestPoint point){
    	
    	Marker marker = map.addMarker(new MarkerOptions()
        .title(point.getMarkerTitle())
        .snippet(point.getMarkerSnippet())
        .position(point.getLocation()));
    	markers.put(marker.getId(), marker);
    	lastMarkerId = marker.getId();
        //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    	refreshRoute();
    }
	public void move(LatLng latlng){
		map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
}
    /*public void move(MarkerPoint point){
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(point.getLatlng(), 13));
    }
    public void move(LatLng latlng){
    		map.moveCamera(CameraUpdateFactory.newLatLng(latlng));
    }
    public void move(Location loc){
    	if(loc!=null)
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(),loc.getLongitude()), 13));
    }*/
    public float getZoomLevel(){
    	return map.getCameraPosition().zoom;
    }
    /**
		There are 5 types of map
		Normal：典型的地圖。
		Hybrid：混合衛星照片及道路地圖。
		Satellite：衛星照片。
		Terrain：地形圖。
     */
    private void changeMapType(){
    	if(map.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
    		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    	}else if(map.getMapType()==GoogleMap.MAP_TYPE_SATELLITE){
    		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	}
    }


	@Override
	public void onMapLongClick(LatLng arg0) {
   	 	MarkerPoint mp = new MarkerPoint(""+markerSeq,"Point "+markerSeq, 
   	 			"Click to remove", arg0);
   	 	addMarker(mp);
	}
    public void showMarkers(){
    	LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

    	Iterator<Entry<String, Marker>> iter = markers.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String,Marker> entry = iter.next();
    		//String key = entry.getKey();
    		Marker mk = entry.getValue();
    		if(bounds.contains(mk.getPosition()))
        	{
        	    mk.setVisible(true);
        	}else{
        		//mk.remove();
        		mk.setVisible(false);
        	}
    	}
    }

	@Override
	public void onMyLocationChange(Location arg0) {
		loc = arg0;
		if(getZoomLevel()<5){
			//Toast.makeText(activity, "getZoomLevel="+getZoomLevel(), Toast.LENGTH_LONG).show();
			//map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(),arg0.getLongitude())));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(),arg0.getLongitude()), 13));
		}
		//Log.i(TAG, "loc="+loc.getLatitude()+","+loc.getLongitude());
	}
    public List<LatLng> getWaypoints(){
    	List<LatLng> ll = new ArrayList<LatLng>();
    	Iterator<Entry<String, Marker>> iter = markers.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String,Marker> entry = iter.next();
    		Marker mk = entry.getValue();
    		//if(!entry.getKey().equals(this.lastMarkerId))
    		ll.add(mk.getPosition());
    	}
    	//Log.i(TAG, "markers.size()="+markers.size()+", waypoints.size()="+ll.size());
    	//Collections.reverse(ll);
		return ll;
    }

	public void refreshRoute() {
		LatLng start = new LatLng(loc.getLatitude(),loc.getLongitude());
		//move(start);
		this.refreshRoute(start);
	}
}
