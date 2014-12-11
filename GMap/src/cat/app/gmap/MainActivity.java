package cat.app.gmap;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.annotation.SuppressLint;
import android.os.Bundle;

public class MainActivity extends android.app.Activity {

	GMap gMap;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        String comments1 = "A beautiful garden city";
        LatLng position = Position.findLatLng("CHC");
        gMap.addMarker(position,comments1);
        gMap.move(position);
    }
    
    
}
