package cat.app.gmap;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.maps.GeoPoint;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends android.app.Activity {

	protected static final String TAG = "GMap.MainActivity";
	GMap gMap = new GMap();
	Button routeBtn;
	//Button addBtn;
	Location loc;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);  
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        gMap.init(this);
        showUI();
    }
    private void showUI(){
    	setButtons();
    }
	private void setButtons() {
		//addBtn = (Button) findViewById(R.id.btnAdd);
		routeBtn = (Button) findViewById(R.id.btnExec);
	    routeBtn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	if(loc==null){ loc = gMap.map.getMyLocation(); return;}
	        	else{
	        		LatLng start = new LatLng(loc.getLatitude(),loc.getLongitude());
	        		gMap.move(start);
		        	if(gMap.markers.size()>0){
		        		GoogleMapRouteTask.removePreviousRoute();
			            LatLng end = gMap.markers.get(gMap.markerSeq-1).getPosition();
			            String url = gMap.getDirectionsUrl(start, end);  
			            GoogleMapRouteTask task = new GoogleMapRouteTask(gMap,url);  
			            task.execute();  
			            gMap.move(start);
		        	}else{
		        		Toast.makeText(gMap.activity, "Please select a target", Toast.LENGTH_LONG).show(); 
		        	}
	        	}
	        }
	    });
	}
	
}
