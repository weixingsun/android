package cat.app.osmap.ui;

import cat.app.maps.OSM;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class GlobalLayoutListener implements OnGlobalLayoutListener{

	OSM osm;
	public GlobalLayoutListener(OSM osm){
		this.osm = osm;
	}
	@Override
    public void onGlobalLayout() {
    	if(osm.switchTileProvider)
    		osm.move();						//make sure setCenter() is called after mapview is loaded.
    	osm.switchTileProvider=false;
    }

}
