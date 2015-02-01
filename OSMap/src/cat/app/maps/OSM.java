package cat.app.maps;

import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.mapsforge.GenericMapView;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cat.app.map.markers.Markers;
import cat.app.navi.task.GeocoderTask;
import cat.app.navi.task.RouteTask;
import cat.app.osmap.Device;
import cat.app.osmap.LOC;
import cat.app.osmap.MyMapEventsReceiver;
import cat.app.osmap.R;
import cat.app.osmap.ui.GlobalLayoutListener;
import cat.app.osmap.util.GeoOptions;
import cat.app.osmap.util.MapOptions;
import cat.app.osmap.util.RouteOptions;
import cat.app.osmap.util.RuntimeOptions;

public class OSM {
	protected static final String tag = OSM.class.getSimpleName();
	public LOC loc = new LOC();
	public Device dv = new Device();
	public Activity act;
	MapOptions mo;
	public RouteOptions ro;
	GeoOptions go;
	public RuntimeOptions rto;
	GenericMapView genericMapView;
	IMapController mapController;
	public Polyline polyline;
	public Markers mks;
	public List<Address> suggestPoints;
	public MapView map;
	public MapTileProviderBase mapProvider;
	public boolean switchTileProvider=false;
	public ScaleBarOverlay scaleBarOverlay;
	public Address startAddr;
	public Address endAddr;

	public void init(Activity act) {
		this.act = act;
		mo = MapOptions.getInstance(this);
		ro = RouteOptions.getInstance(this);
		rto = RuntimeOptions.getInstance(act);
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
		map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		map.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener(this));
		MyMapEventsReceiver mReceive = new MyMapEventsReceiver(this);
		MapEventsOverlay OverlayEventos = new MapEventsOverlay(act.getBaseContext(), mReceive);
		map.getOverlays().add(OverlayEventos);
		map.invalidate();
	}

	public void initScaleBar(){	//have to invoke after map initialized, call in GlobalLayoutListener
		map.getOverlays().remove(scaleBarOverlay);
		scaleBarOverlay = new ScaleBarOverlay(act);
		scaleBarOverlay.setMaxLength(2);
		scaleBarOverlay.setScaleBarOffset(map.getWidth()/2 - 130,map.getHeight()-20);
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
		MapOptions.switchTileProvider(name);
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
	//type= map/route
	public void startDownloadActivity(String fileName){
		Toast.makeText(act, "You need download offline files", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(act, cat.app.osmap.ui.DownloadManagerUI.class);
		intent.putExtra("file", fileName);
	    act.startActivity(intent);
	}
}