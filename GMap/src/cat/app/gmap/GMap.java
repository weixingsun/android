package cat.app.gmap;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GMap {
	GoogleMap map;
	ArrayList<LatLng> markerPoints=new ArrayList<LatLng>();
	@SuppressLint("NewApi") 
	public void init(Activity activity){
		map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();
	}

    public void addMarker(LatLng point,String comments){
    	map.setMyLocationEnabled(true);
    	map.getUiSettings().setCompassEnabled(true);
    	map.setBuildingsEnabled(true);
        map.addMarker(new MarkerOptions()
        .title("Christchurch")
        .snippet(comments)
        .position(point));
    }
    public void move(LatLng point){
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
    }
    private void changeMapType(){
    	if(map.getMapType()==GoogleMap.MAP_TYPE_NORMAL){
    		map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    	}else if(map.getMapType()==GoogleMap.MAP_TYPE_SATELLITE){
    		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	}
    }
}
