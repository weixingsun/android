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
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cat.app.gmap.model.MarkerPoint;
import cat.app.gmap.model.SuggestPoint;
import cat.app.gmap.task.GoogleMapRouteTask;
import cat.app.gmap.task.GoogleMapSearchByPositionTask;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMap extends MapFragment implements OnMapLongClickListener,OnMyLocationChangeListener {
	//GooglePlayServicesClient.ConnectionCallbacks,
    //GooglePlayServicesClient.OnConnectionFailedListener

	//https://maps.googleapis.com/maps/api/place/search/json?
	//location=-33.8670522,151.1957362&radius=500&types=grocery_or_supermarket
	//&sensor=true&key=AIzaSyApl-_heZUCRD6bJ5TltYPn4gcSCy1LY3A
    private HashMap<String,String> settings = new HashMap<String, String>();
	private static final String TAG = "GMap";
	public GoogleMap map;
	public MainActivity activity;
	public Location loc;
	public Map<String,Marker> markers=new TreeMap<String,Marker>();
	public TreeMap<String,MarkerPoint> markerpoints=new TreeMap<String,MarkerPoint>();
	//public Map<String,LatLng> markerLatLngs=new HashMap<String,LatLng>();
	public int markerMaxSeq = 1;
	public List<SuggestPoint> suggestPoints = new ArrayList<SuggestPoint>();
	
	@SuppressLint("NewApi") 
	public void init(final MainActivity activity){
		this.activity= activity;
		FragmentManager myFM = activity.getFragmentManager();
		final MapFragment fragment = (MapFragment) myFM.findFragmentById(R.id.map);
		//MapFragment fragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.map);
		map = fragment.getMap();
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
            }
        });
        settings.put("TrafficEnabled", "false");
        //settings.put("TrafficEnabled", "false");
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
	
	protected String getLastMarkerId() {
    	Iterator<Entry<String, MarkerPoint>> iter = markerpoints.entrySet().iterator();
    	String key = null;
    	while(iter.hasNext()){
    		Entry<String,MarkerPoint> entry = iter.next();
    		key = entry.getKey();
    	}
		return key;
	}

	private Bitmap createSeqBitmap(int seq){
		Bitmap bmRaw = BitmapFactory.decodeResource(activity.getResources(), R.drawable.marker_blue_32);
		return generatorSequencedIcon(bmRaw,seq);
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

	@Override
	public void onMapLongClick(LatLng point) {
   	 	GoogleMapSearchByPositionTask task = new GoogleMapSearchByPositionTask(this, point);
		task.execute();
	}
    /*public void showMarkers(){
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
    }*/

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
    	Iterator<Entry<String, MarkerPoint>> iter = markerpoints.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String,MarkerPoint> entry = iter.next();
    		ll.add(entry.getValue().getLatlng());
    	}
    	//Collections.reverse(ll);
		return ll;
    }

	public void refreshRoute(boolean restart) {
		LatLng myLoc = new LatLng(loc.getLatitude(),loc.getLongitude());
		if(restart){//redraw all routes
			GoogleMapRouteTask.removePreviousRoute();
			refreshRoute(myLoc);
		}else{ //draw only last route
			Entry<String,MarkerPoint> lastEntry = markerpoints.pollLastEntry();
			LatLng end = lastEntry.getValue().getLatlng();
			LatLng start=null;
			if(markerpoints.size()==0) {
				start=myLoc;
			}else{
				start=markerpoints.lastEntry().getValue().getLatlng();
			}
			GoogleMapRouteTask task = new GoogleMapRouteTask(this,start,end);
            task.execute();
			markerpoints.put(lastEntry.getKey(), lastEntry.getValue());
		}
	}
	public void refreshRoute(LatLng loc){
		//Toast.makeText(activity, "draw lines", Toast.LENGTH_LONG).show(); 
		if(markerpoints.size()>0){
            //LatLng end = markers.get(lastMarkerId).getPosition();
			LatLng start = loc;
    		for(LatLng dest:getWaypoints()){
	            GoogleMapRouteTask task = new GoogleMapRouteTask(this,start,dest);
	            task.execute();
	            start=dest;
    		}
    	}else{
    		//Toast.makeText(activity, "No Target", Toast.LENGTH_LONG).show();
    	}
	}

	//TrafficEnabled
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

	/**
     * 在给定的图片的右上角加上联系人数量。数量用红色表示
     * @param icon 给定的图片
     * @return 带联系人数量的图片
     */
    private Bitmap generatorSequencedIcon(Bitmap markerIcon,int seq){
    	
    	Bitmap contactIcon=Bitmap.createBitmap(markerIcon.getWidth(), markerIcon.getHeight(), Config.ARGB_8888);
    	Canvas canvas=new Canvas(contactIcon);
    	
    	//拷贝图片
    	Paint iconPaint=new Paint();
    	iconPaint.setDither(true);//防抖动
    	iconPaint.setFilterBitmap(true);//用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
    	Rect src=new Rect(0, 0, markerIcon.getWidth(), markerIcon.getHeight());
    	Rect dst=new Rect(0, 0, markerIcon.getWidth(), markerIcon.getHeight());
    	canvas.drawBitmap(markerIcon, src, dst, iconPaint);
    	
    	//启用抗锯齿和使用设备的文本字距
    	Paint countPaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DEV_KERN_TEXT_FLAG);
    	countPaint.setColor(Color.RED);
    	countPaint.setTextSize(20f);
    	countPaint.setTypeface(Typeface.DEFAULT_BOLD);
    	canvas.drawText(String.valueOf(seq), markerIcon.getWidth()-37, 35, countPaint);
    	return contactIcon;
    }
}
