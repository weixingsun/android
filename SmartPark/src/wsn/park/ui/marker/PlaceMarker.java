package wsn.park.ui.marker;

import org.osmdroid.bonuspack.overlays.Marker;

import wsn.park.maps.OSM;
import wsn.park.model.Place;

public class PlaceMarker extends Marker{
	private Place p;
	public PlaceMarker(Place p) {
		super(OSM.getInstance().map);
		this.setPlace(p);
	}
	public Place getPlace() {
		return p;
	}
	public void setPlace(Place p) {
		this.p = p;
	}

}
