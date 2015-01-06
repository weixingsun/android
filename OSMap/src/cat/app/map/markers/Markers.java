package cat.app.map.markers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import cat.app.osmap.R;

public class Markers {
	MapView map;
	//List<Marker> markers= new ArrayList<Marker>();
	public void addMarker(GeoPoint p){
		Marker mk = new Marker(map);
		mk.setPosition(p);
		mk.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
		//mk.setIcon(icon);
		mk.setIcon(map.getResources().getDrawable(R.drawable.ic_launcher));
		mk.setTitle("Start point");
		map.getOverlays().add(mk);
		//markers.add(mk);
	}
}
