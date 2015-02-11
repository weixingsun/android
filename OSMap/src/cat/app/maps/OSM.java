package cat.app.maps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.Toast;

import cat.app.map.markers.Markers;
import cat.app.map.poi.POI;
import cat.app.maps.vendor.GenericMapView;
import cat.app.navi.task.GeocoderTask;
import cat.app.navi.task.RouteTask;
import cat.app.osmap.Device;
import cat.app.osmap.LOC;
import cat.app.osmap.MyMapEventsReceiver;
import cat.app.osmap.R;
import cat.app.osmap.ui.GlobalLayoutListener;
import cat.app.osmap.util.DbHelper;
import cat.app.osmap.util.GeoOptions;
import cat.app.osmap.util.MapOptions;
import cat.app.osmap.util.RouteOptions;
import cat.app.osmap.util.RuntimeOptions;
import cat.app.osmap.util.SavedOptions;

public class OSM {
	protected static final String tag = OSM.class.getSimpleName();
	public DbHelper dbHelper;  
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
	public File offlineMapFile;
	
	public void init(Activity act) {
		this.act = act;
		this.dbHelper = DbHelper.getInstance(act);
		SavedOptions.routingProvider = dbHelper.getSettings("Navigate");
		SavedOptions.selectedTravelMode = dbHelper.getSettings("Travel");
		String providerMenu = dbHelper.getSettings("Maps");

		rto = RuntimeOptions.getInstance(act);
        loc.init(act,this);
		mo = MapOptions.getInstance(this);
		ro = RouteOptions.getInstance(this);
		mo.initTileSources(act);
		genericMapView = (GenericMapView) act.findViewById(R.id.osmap);
		String selectedMap = MapOptions.MAP_TILES.get(providerMenu);
		MapTileProviderBase mtpb = MapOptions.getTileProvider(selectedMap);//new MapTileProviderBasic(act.getApplicationContext());
		switchTileProvider=true;
		setMap(mtpb);
		mks=Markers.getInstance(this);
		mks.initMylocMarker();
		mks.setNaviImage();
		//mks.initRouteMarker();
        dv.init(act,this);

	}
	public void setMap(MapTileProviderBase mtpb) {
		genericMapView.setTileProvider(mtpb);
		map = genericMapView.getMapView();
		//Log.w(tag, "provider="+mtpb.getTileSource().name());
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
		map.setMapListener(new DelayedMapListener(new MapListener(){
			@Override
			public boolean onScroll(ScrollEvent arg0) {
				BoundingBoxE6 box = arg0.getSource().getBoundingBox();
				Log.i(tag, "onScroll="+box);
				return false;
			}
			@Override
			public boolean onZoom(ZoomEvent arg0) {
				return false;
			}}, 300));
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
	public BoundingBoxE6 getBoundary(){
		 return map.getBoundingBox();
	}

	public boolean InBoundary(GeoPoint pos,BoundingBoxE6 box) {
		int minLat = Math.min(box.getLatNorthE6(), box.getLatSouthE6());
		int maxLat = Math.max(box.getLatNorthE6(), box.getLatSouthE6());
		int minLng = Math.min(box.getLonEastE6(), box.getLonWestE6());
		int maxLng = Math.max(box.getLonEastE6(), box.getLonWestE6());
		if(pos.getLatitudeE6()>minLat && pos.getLatitudeE6()<maxLat 
				&& pos.getLongitudeE6()>minLng && pos.getLongitudeE6()<maxLng ){
			return true;
		}
		return false;
	}
	
}