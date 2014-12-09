package cat.app.gmap;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.annotation.SuppressLint;
import android.os.Bundle;

public class MainActivity extends android.app.Activity {

	GoogleMap map;
    //Sydney (-33.867, 151.206)
    //Beijing (39.915291, 116.396860)
    //Shanghai(31.192609, 121.431577)
	//Christchurch(-43.5320544,172.6362254)
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        String comments1 = "A beautiful garden city";
        LatLng position = new LatLng(39.915291, 116.396860);
        position = new LatLng(-43.5320544,172.6362254);
        marker(position,comments1);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
    }
    public void marker(LatLng point,String comments){
    	map.setMyLocationEnabled(true);
        map.addMarker(new MarkerOptions()
        .title("Christchurch")
        .snippet(comments)
        .position(point));
    }
}
