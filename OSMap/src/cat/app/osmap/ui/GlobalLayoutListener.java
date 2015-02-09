package cat.app.osmap.ui;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import cat.app.maps.OSM;
import android.R;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class GlobalLayoutListener implements OnGlobalLayoutListener{
	private static final String tag = GlobalLayoutListener.class.getSimpleName();
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
    		osm.initScaleBar();
    	}
    	osm.switchTileProvider=false;
		BoundingBoxE6 box = osm.getBoundary();
		int i = osm.mks.cleanPOIs();
		osm.mks.addPOIMarkers();
		Log.w(tag, "POI.size="+osm.mks.pois.size()+",cleaned="+i+",LatN="+box.getLatNorthE6()+",LatS="+box.getLatSouthE6()+",LngE="+box.getLonEastE6()+",LngW="+box.getLonWestE6());
		osm.map.invalidate();
    }

}