package cat.app.map.markers;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerDragListener;

import android.util.Log;

import cat.app.maps.OSM;
import cat.app.navi.task.FindMyStepTask;

public class OnTestMarkerDragListener implements OnMarkerDragListener {

	private static final String tag = OnTestMarkerDragListener.class.getSimpleName();
	OSM osm;
	public OnTestMarkerDragListener(OSM osm) {
		this.osm = osm;
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		//Object object = marker.getRelatedObject();
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		(new FindMyStepTask(osm, marker.getPosition(),marker)).execute();
		//Log.i(tag, "onMarkerDragEnd");
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
	}

}
