package cat.app.navi;

import java.util.ArrayList;

import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;

import android.app.Activity;

public class Route {

	Activity act;
	
	ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
	Polyline roadOverlay;
	public Route(Activity act){
		this.act=act;
	}

}
