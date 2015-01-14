package cat.app.osmap.ui;

import org.osmdroid.views.overlay.MinimapOverlay;

import cat.app.maps.OSM;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class GlobalLayoutListener implements OnGlobalLayoutListener{
	MinimapOverlay mini;
	OSM osm;
	public GlobalLayoutListener(OSM osm){
		this.osm = osm;
	}
	@Override
    public void onGlobalLayout() {
    	if(osm.switchTileProvider){
    		osm.move();						//make sure setCenter() is called after mapview is loaded.
    		//osm.mapView.getOverlays().remove(mini);
    		//mini = new MinimapOverlay(osm.act,osm.mapView.getTileRequestCompleteHandler());
    		//osm.mapView.getOverlays().add(mini);
    		osm.mks.initTestMarker(osm.loc.myPos);
    	}
    	osm.switchTileProvider=false;
    }

}
