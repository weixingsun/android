package wsn.park.map.poi;

import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.bonuspack.overlays.Marker;

public class POI {
	public POI(PointOfInterest poiInfo, Marker poiMarker){
		this.poiInfo = poiInfo;
		this.poiMarker = poiMarker;
	}
	public PointOfInterest poiInfo;
	public Marker poiMarker;
}
