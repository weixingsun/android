package cat.app.gmap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMap implements OnMapLongClickListener {
	private static final String TAG = "GMap";
	GoogleMap map;
	Activity activity;
	public SparseArray<Marker> markers=new SparseArray<Marker>();
	public int markerSeq = 0;
	public int zoomLevel = 0;
	@SuppressLint("NewApi") 
	public void init(Activity activity){
		this.activity= activity;
		map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.getUiSettings().setCompassEnabled(true);
    	map.setMyLocationEnabled(true);
    	map.setBuildingsEnabled(true);
    	map.setOnMapLongClickListener(this);
    	Location loc = map.getMyLocation();
        this.move(loc);
        //MarkerPoint point = PointUtil.findCity("CHC");
        //gMap.addMarker(point);
        
	}

    public void addMarker(MarkerPoint point){
    	
    	Marker marker = map.addMarker(new MarkerOptions()
        .title(point.getTitle())
        .snippet(point.getComment())
        .position(point.getLatlng()));
    	markers.append(markerSeq, marker);
    	markerSeq++;
        //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    }
    public void move(MarkerPoint point){
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(point.getLatlng(), 13));
    }
    public void move(LatLng latlng){
    	if(zoomLevel<13)
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
    	zoomLevel=13;
    }
    public void move(Location loc){
    	if(loc!=null)
    		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(),loc.getLongitude()), 13));
    }
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
    /** 
     * 组合成googlemap direction所需要的url
     *  
     * @param origin 
     * @param dest 
     * @return url 
     */  
    public String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;  
        // Sensor enabled
        String sensor = "sensor=false";
        // Travelling Mode  
        String mode = "mode=driving";
        // String waypointLatLng = "waypoints="+"40.036675"+","+"116.32885";
        // 如果使用途径点，需要添加此字段
        // String waypoints = "waypoints=";
        String parameters = null;  
        // Building the parameters to the web service
        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;  
        // parameters = str_origin + "&" + str_dest + "&" + sensor + "&"  
        // + mode+"&"+waypoints;
        // String output = "json";
        String output = "xml";
        // Building the url to the web service  
        String url = "https://maps.googleapis.com/maps/api/directions/"  
                + output + "?" + parameters;
        Log.i(TAG,"getDerectionsURL--->: " + url);  
        return url;  
    }

	@Override
	public void onMapLongClick(LatLng arg0) {
   	 	MarkerPoint mp = new MarkerPoint("1","Tap Point", "Lat:"+arg0.latitude+",Lng:"+arg0.longitude, arg0);
   	 	addMarker(mp);
	}
    
}
