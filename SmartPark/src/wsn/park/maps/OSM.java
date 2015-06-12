package wsn.park.maps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//import com.google.android.gms.maps.*;
//import com.google.android.gms.maps.model.*;
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

import wsn.park.Device;
import wsn.park.LOC;
import wsn.park.MyMapEventsReceiver;
import wsn.park.NET;
import wsn.park.R;
import wsn.park.map.poi.POI;
import wsn.park.maps.vendor.GenericMapView;
import wsn.park.model.DataBus;
import wsn.park.model.SavedPlace;
import wsn.park.navi.task.GeocoderTask;
import wsn.park.navi.task.RouteTask;
import wsn.park.ui.GlobalLayoutListener;
import wsn.park.ui.marker.Markers;
import wsn.park.util.DbHelper;
import wsn.park.util.GeoOptions;
import wsn.park.util.MapOptions;
import wsn.park.util.RouteOptions;
import wsn.park.util.RuntimeOptions;
import wsn.park.util.SavedOptions;

public class OSM {
	private static OSM singleton;
	private OSM(){ }
	public static synchronized OSM getInstance( ) {
		if (singleton == null)
			singleton=new OSM();
		return singleton;
	}
	protected static final String tag = OSM.class.getSimpleName();
	public DbHelper dbHelper;
	public NET net;
	public LOC loc = new LOC();
	public Device dv = new Device(); //Device.getInstance();
	public Activity act;
	MapOptions mo;
	public RouteOptions ro;
	GeoOptions go;
	public RuntimeOptions rto;
	GenericMapView genericMapView; 
	IMapController mapController;
	public List<Polyline> lines;
	public Polyline polyline;
	public Markers mks;
	public List<SavedPlace> suggestPoints;
	public MapView map;
	public MapTileProviderBase mapProvider;
	public boolean switchTileProvider=false;
	public ScaleBarOverlay scaleBarOverlay;
	public Address startAddr;
	//public Address endAddr;
	public File offlineMapFile;
	private DataBus bus = DataBus.getInstance();
	
	public void init(Activity act) {
		this.act = act;
        loc.init(this);
		net = NET.instance();
        net.init(act);
		this.dbHelper = DbHelper.getInstance(act);
		SavedOptions.selectedBy = dbHelper.getSettings(SavedOptions.BY);
		SavedOptions.selectedMap = dbHelper.getSettings(SavedOptions.MAP);
		SavedOptions.selectedGeo = dbHelper.getSettings(SavedOptions.GEO);
		SavedOptions.selectedNavi = dbHelper.getSettings(SavedOptions.NAVI);

		rto = RuntimeOptions.getInstance(act);
		mo = MapOptions.getInstance(this);
		ro = RouteOptions.getInstance(this);
		mo.initTileSources(act);
		genericMapView = (GenericMapView) act.findViewById(R.id.osmap);
		String selectedMap = MapOptions.MAP_TILES.get(SavedOptions.selectedMap);
		MapTileProviderBase mtpb = MapOptions.getTileProvider(selectedMap);//new MapTileProviderBasic(act.getApplicationContext());
		if(mtpb==null){
			mtpb = MapOptions.getTileProvider(MapOptions.MAP_MAPQUESTOSM);
			Log.e(tag, "no map found "+SavedOptions.selectedMap+", use OSM");
			SavedOptions.selectedMap = MapOptions.MAP_MAPQUESTOSM;
		}
		switchTileProvider=true;
		setMap(mtpb);
		mks=Markers.getInstance(this);
		mks.initMylocMarker();
		mks.showAllSavedPlaces();
		//mks.setNaviImage();
		//mks.initRouteMarker();
        dv.init(act,this);
	}
	public void setMap(MapTileProviderBase mtpb) {
		if(mtpb==null) return;
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
				//BoundingBoxE6 box = arg0.getSource().getBoundingBox();
				//Log.i(tag, "onScroll="+box);
				//mks.showHidePOIs();
				return false;
			}
			@Override
			public boolean onZoom(ZoomEvent arg0) {
				return false;
			}}, 300));
		this.move();
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
		//Log.i(tag, "moved to my location: ");
	}
	public void move() {
		if(LOC.myPos==null ){
			if(bus.getMyPoint()!=null)
				move(bus.getMyPoint());
			else
				return;
		}else{
			move(new GeoPoint(LOC.myPos));
		}
	}
	
	public void refreshTileSource(String name){
		MapOptions.switchTileProvider(name);
		map.invalidate();
		switchTileProvider=true;
	}

	public void startTask(String type,String address){
		if(address.length()<1) return;
		List<SavedPlace> list=null;
		if(dbHelper.existPOI()){
    		Log.w(tag, "find locally first");
			list = dbHelper.getPOIs(address);
			if(list!=null && list.size()>0){
	    		dv.fillList(list);
	    		suggestPoints = list;
	    	}else if(net.isNetworkConnected()){
	    		Log.w(tag, "cannot find locally, then find online");
	    		GeocoderTask task = new GeocoderTask(this, address);
				task.execute();
	    	}
		}else if(net.isNetworkConnected()){	//no local database, then find online
			Log.w(tag, "no local database found");
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
		Intent intent = new Intent(act, wsn.park.ui.DownloadManagerUI.class);
		Log.e(tag,"fileName="+fileName);
		intent.putExtra("file", fileName);
	    act.startActivity(intent);
	}
	public void startSettingsActivity(){
		Intent intent = new Intent(act, wsn.park.ui.SettingsActivity.class);
	    act.startActivity(intent);
	}
	public void startHistoryActivity() {
		Intent intent = new Intent(act, wsn.park.ui.HistoryActivity.class);
	    act.startActivity(intent);
	}
	//activity.osm.startHistoryActivity();
	public void startMyPlacesActivity() {
		Intent intent = new Intent(act, wsn.park.ui.MyPlacesActivity.class);
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