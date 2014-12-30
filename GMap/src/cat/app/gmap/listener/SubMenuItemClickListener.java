package cat.app.gmap.listener;

import com.google.android.gms.maps.GoogleMap;

import cat.app.gmap.MainActivity;
import cat.app.gmap.R;
import cat.app.gmap.Util;
import cat.app.gmap.model.MarkerPoint;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SubMenuItemClickListener implements OnItemClickListener {

	protected static final String TAG = "GMap.SubMenuItemClickListener";
	MainActivity activity;
	public SubMenuItemClickListener(MainActivity mainActivity) {
		this.activity = mainActivity;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TextView tv = (TextView)view;
		//Log.i(TAG, "SubMenuItem.Clicked:"+tv.getText().toString());
		proceedClickedItem(tv.getText().toString());
	}
	private void proceedClickedItem(String text) {
		switch(text) {
			case Util.MAP_NORMAL:
				activity.gMap.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
			case Util.MAP_SATELLITE:
				activity.gMap.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break;
			case Util.MAP_TERRAIN:
				activity.gMap.map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
			case Util.MAP_TRAFFIC:
				activity.gMap.switchSettings("TrafficEnabled");
				break;
			case Util.NAV_DRIVING:
				activity.gMap.travelMode=Util.NAV_DRIVING.toLowerCase();
				activity.iconTravelMode.setImageResource(R.drawable.ic_taxi_48);
				activity.openPopup(null,0);
				break;
			case Util.NAV_WALKING:
				activity.gMap.travelMode=Util.NAV_WALKING.toLowerCase();
				activity.iconTravelMode.setImageResource(R.drawable.ic_walk_48);
				activity.openPopup(null,0);
				break;
			case Util.NAV_BICYCLING:
				activity.gMap.travelMode=Util.NAV_BICYCLING.toLowerCase();
				activity.iconTravelMode.setImageResource(R.drawable.ic_bicycle_48);
				activity.openPopup(null,0);
				break;
			case Util.NAV_TRANSIT:
				activity.gMap.travelMode=Util.NAV_TRANSIT.toLowerCase();
				activity.iconTravelMode.setImageResource(R.drawable.ic_bus_48);
				activity.openPopup(null,0);
				break;
			default:
				Toast.makeText(activity, "Function:"+text+" not available yet", Toast.LENGTH_LONG).show();
		}
	}
}
