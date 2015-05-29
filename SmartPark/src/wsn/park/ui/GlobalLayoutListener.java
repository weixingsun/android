package wsn.park.ui;

import wsn.park.map.poi.LoadPOITask;
import wsn.park.maps.OSM;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class GlobalLayoutListener implements OnGlobalLayoutListener{
	private static final String tag = GlobalLayoutListener.class.getSimpleName();
	//MinimapOverlay mini;
	OSM osm;
	public GlobalLayoutListener(OSM osm){
		this.osm = osm;
	}
	@Override
    public void onGlobalLayout() {
		//Log.i(tag, "onGlobalLayout");
    	if(osm.switchTileProvider){
    		osm.move();//make sure setCenter() is called after mapview is loaded.
    		//osm.mapView.getOverlays().remove(mini);
    		//mini = new MinimapOverlay(osm.act,osm.mapView.getTileRequestCompleteHandler());
    		//osm.mapView.getOverlays().add(mini);
    		//osm.mks.initTestMarker(osm.loc.myPos);
    		osm.initScaleBar();
    	}
    	osm.switchTileProvider=false;
    	//LoadPOITask task = new LoadPOITask(osm);
    	//task.execute();
    }

}