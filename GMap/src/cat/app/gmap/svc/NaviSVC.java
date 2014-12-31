package cat.app.gmap.svc;

import com.google.android.gms.maps.model.LatLng;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class NaviSVC extends IntentService {

	private static String TAG = "NaviSVC";
	//MainActivity act ;
	LatLng myLatLng;
	public NaviSVC() {
		super(TAG);
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		//double lat = intent.getDoubleExtra("lat", 0);
		//double lng = intent.getDoubleExtra("lng", 0);
		Log.i(TAG, "onHandleIntent()");
	}


}

/*
Intent i = new Intent(Main.context, NaviSVC.class);
i.putExtra("port", SERVER_PORT);
UDPClient.setInterval(UPDATE_INTERVAL);
MainView.context.startService(i);
*/