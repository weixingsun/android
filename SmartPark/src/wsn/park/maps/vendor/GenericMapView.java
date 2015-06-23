package wsn.park.maps.vendor;

import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/** 
 * A pseudo MapView allowing to set (and change) its TileProvider. Two key features:<br>
 * - it supports the MapsForgeTileProvider<br>
 * - and it can be defined in a layout. <br>
 * 
 * @author Salsoft, M.Kergall
 */
public class GenericMapView extends FrameLayout{

	private static final String tag = GenericMapView.class.getSimpleName();
	protected FixedMapView mMapView;

	public GenericMapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setTileProvider(MapTileProviderBase aTileProvider){
		if (mMapView != null){
			this.removeView(mMapView);
		}
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(this.getContext());
		int tileSizePixels=aTileProvider.getTileSource().getTileSizePixels();
		FixedMapView newMapView = new FixedMapView(this.getContext(), tileSizePixels, resourceProxy, aTileProvider);
		
		if (mMapView != null){
			//restore as much parameters as possible from previous map:
			IMapController mapController = newMapView.getController();
			mapController.setZoom(mMapView.getZoomLevel());
			mapController.setCenter(mMapView.getMapCenter());
			newMapView.setBuiltInZoomControls(true); //no way to get old setting
			newMapView.setMultiTouchControls(true); //no way to get old setting
			newMapView.setUseDataConnection(mMapView.useDataConnection());
			newMapView.setMapOrientation(mMapView.getMapOrientation());
			newMapView.setScrollableAreaLimit(mMapView.getScrollableAreaLimit());
			List<Overlay> overlays = mMapView.getOverlays();
			for (Overlay o:overlays)
				newMapView.getOverlays().add(o);
		}

		mMapView = newMapView;
		this.addView(mMapView);
	}
	
	public FixedMapView getMapView(){
		return mMapView;
	}

//	@Override
//	public boolean longPressHelper(GeoPoint arg0) {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public boolean singleTapConfirmedHelper(GeoPoint p) {
//		// TODO Auto-generated method stub
//		Log.w(tag, "map.clicked:("+p.getLatitude()+","+p.getLongitude()+")");
//		Log.w(tag, "map.clicked:("+p.getLatitudeE6()+","+p.getLongitudeE6()+")");
//		return true;
//	}
}
