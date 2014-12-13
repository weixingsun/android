package cat.app.gmap;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.maps.GeoPoint;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends android.app.Activity {

	protected static final String TAG = "GMap.MainActivity";
	GMap gMap = new GMap();
	Button NaviBtn;
	EditText inputAddress;
	//Location loc;
	
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
    	setText();
    }
	private void setText() {
		this.inputAddress = (EditText) findViewById(R.id.inputAddress);
		inputAddress.setTextColor(Color.BLACK);
	}
	private void setButtons() {
		//addBtn = (Button) findViewById(R.id.btnAdd);
		NaviBtn = (Button) findViewById(R.id.navigateBtn);
	    NaviBtn.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	/*if(gMap.loc!=null || gMap.markers.size()>0){
		        	gMap.refreshRoute();
	        	}*/
	        	
	        }
	    });
	}
	
	
}
