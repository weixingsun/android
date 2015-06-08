package wsn.park.ui.marker;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerDragListener;

import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.navi.task.FindMyStepTask;

import android.util.Log;


public class OnTestMarkerDragListener implements OnMarkerDragListener {

	private static final String tag = OnTestMarkerDragListener.class.getSimpleName();

	@Override
	public void onMarkerDrag(Marker marker) {
		//Object object = marker.getRelatedObject();
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		(new FindMyStepTask()).execute();
		Mode.setID(Mode.PRACTICE);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
	}

}
