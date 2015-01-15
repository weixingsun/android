package cat.app.maps;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.mapsforge.GenericMapView;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import cat.app.map.markers.Markers;
import cat.app.navi.GeoOptions;
import cat.app.navi.RouteOptions;
import cat.app.navi.task.GeocoderTask;
import cat.app.navi.task.RouteTask;
import cat.app.osmap.Device;
import cat.app.osmap.LOC;
import cat.app.osmap.R;
import cat.app.osmap.ui.GlobalLayoutListener;

public class OSM {
	protected static final String tag = OSM.class.getSimpleName();
	public LOC loc = new LOC();
	public Device dv = new Device();
	public Activity act;
	MapOptions mo;
	RouteOptions ro;
	GeoOptions go;
	GenericMapView genericMapView;
	IMapController mapController;
	public Markers mks;
	public List<Address> suggestPoints;
	public MapView map;
	public MapTileProviderBase mapProvider;
	public boolean switchTileProvider=false;
	public ScaleBarOverlay scaleBarOverlay;

	public void init(Activity act) {
		this.act = act;
		mo = MapOptions.getInstance(this);
		ro = RouteOptions.getInstance(this);
		mo.initTileSources(act);
		genericMapView = (GenericMapView) act.findViewById(R.id.osmap);
		MapTileProviderBase mtpb = new MapTileProviderBasic(act.getApplicationContext());
		switchTileProvider=true;
		setMap(mtpb);
		mks=Markers.getInstance(this);
		mks.initMylocMarker();
		//mks.initRouteMarker();
        loc.init(act,this);
        dv.init(act,this);

	}
	public void setMap(MapTileProviderBase mtpb) {
		genericMapView.setTileProvider(mtpb);
		map = genericMapView.getMapView();
		mapController = map.getController();
		mapController.setZoom(16);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);
		map.setClickable(true);
		map.setLongClickable(true);
		//mapView.setUseDataConnection(false); //disable network
		map.setMinZoomLevel(4);
		//mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		map.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener(this));
		MapEventsReceiver mReceive = new MapEventsReceiver() {
			@Override
			public boolean longPressHelper(GeoPoint p) {
				if (loc.myPos == null) return false;
				OSM.this.startTask("geo", new GeoPoint(p),"route");
				ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
				points.add(new GeoPoint(loc.myPos));
				points.add(p);
				ro.setWayPoints(points);
				OSM.this.startTask("route", new GeoPoint(p),"route");
				return false;
			}
			@Override
			public boolean singleTapConfirmedHelper(GeoPoint arg0) {
				dv.closeAllList();
				return false;
			}
		};
		MapEventsOverlay OverlayEventos = new MapEventsOverlay(act.getBaseContext(), mReceive);
		map.getOverlays().add(OverlayEventos);
		map.invalidate();
	}

	public void initScaleBar(){	//have to invoke after map initialized, call in GlobalLayoutListener
		map.getOverlays().remove(scaleBarOverlay);
		scaleBarOverlay = new ScaleBarOverlay(act);
		scaleBarOverlay.setMaxLength(2);
		scaleBarOverlay.setScaleBarOffset(map.getWidth()/2 - 100,map.getHeight()-30); //-scaleBarOverlay.screenWidth
		map.getOverlays().add(scaleBarOverlay);
		//Log.e(tag, "map.height="+mapView.getHeight());
	}
	public void setZoomLevel(int level) {
		mapController.setZoom(level);
	}
	public void move(double lat,double lng) {
		GeoPoint gp = new GeoPoint(lat,lng);
		move(gp);
	}
	public void move(GeoPoint gp) {
		mapController.animateTo(gp);
		//mapController.setCenter(gp);
		Log.i(tag, "moved to my location: ");
	}
	public void move() {
		if(loc.myPos==null) return;
		GeoPoint gp = new GeoPoint(loc.myPos);
		move(gp);
	}
	
	public void refreshTileSource(String name){
		MapOptions.switchTileProvider(this,name);
		map.invalidate();
		switchTileProvider=true;
	}

	public void startTask(String type,String address){
		if(type.equals("geo")){
			GeocoderTask task = new GeocoderTask(this, address);
			task.execute();
		}
	}
	public void startTask(String type,GeoPoint point,String purpose){
		if(type.equals("geo")){
			GeocoderTask task = new GeocoderTask(this, point, purpose);
			task.execute();
		}else if(type.equals("route")){
			RouteTask task = new RouteTask(OSM.this, ro);
			task.execute();
		}
	}

}