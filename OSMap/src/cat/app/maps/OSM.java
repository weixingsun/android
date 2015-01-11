package cat.app.maps;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.mapsforge.GenericMapView;
import org.osmdroid.bonuspack.mapsforge.MapsForgeTileProvider;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerClickListener;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import cat.app.map.markers.InfoWindow;
import cat.app.navi.GeoOptions;
import cat.app.navi.RouteOptions;
import cat.app.navi.google.Step;
import cat.app.navi.task.GeocoderTask;
import cat.app.navi.task.RouteTask;
import cat.app.osmap.Device;
import cat.app.osmap.LOC;
import cat.app.osmap.MyItemizedOverlay;
import cat.app.osmap.R;

public class OSM {
	protected static final String tag = OSM.class.getSimpleName();
	public LOC loc = new LOC();
	public Device dv = new Device();
	public Activity act;
	MapOptions mo;
	RouteOptions ro;
	GeoOptions go;
	org.osmdroid.bonuspack.mapsforge.GenericMapView genericMapView;
	IMapController mapController;
	MyItemizedOverlay myLocOverlay;
	OverlayItem myLocationMarker;
	MyItemizedOverlay routeOverlay;
	OverlayItem routeMarker;
	// Route route;
	Polyline routePolyline;
	public List<Address> suggestPoints;
	public ArrayList<Marker> markers = new ArrayList<Marker>();
	private MapView mapView;
	public MapTileProviderBase mapProvider;
	private boolean switchTileProvider=false;

	public void init(Activity act) {
		this.act = act;
		mo = MapOptions.getInstance(this);
		mo=null;
		ro = RouteOptions.getInstance(this);
		mo.initTileSources(act);
		genericMapView = (GenericMapView) act.findViewById(R.id.osmap);
		MapTileProviderBase mtpb = new MapTileProviderBasic(act.getApplicationContext());
		switchTileProvider=true;
		setMap(mtpb);
		initMylocMarker();
		initRouteMarker();
        loc.init(act,this);
        dv.init(act,this);
	}
	public void setMap(MapTileProviderBase mtpb) {
		genericMapView.setTileProvider(mtpb);
		mapView = genericMapView.getMapView();
		mapController = mapView.getController();
		
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
		mapView.setClickable(true);
		mapView.setLongClickable(true);
		//mapView.setUseDataConnection(false); //disable network
		mapView.setMinZoomLevel(4);
		//mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	if(switchTileProvider)
		    	 move();						//make sure setCenter() is called after mapview is loaded.
		    	switchTileProvider=false;
		    }
		});
		MapEventsReceiver mReceive = new MapEventsReceiver() {
			@Override
			public boolean longPressHelper(GeoPoint p) {
				if (loc.myPos == null) return false;
				OSM.this.startTask("geo", new GeoPoint(p));
				ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
				points.add(new GeoPoint(loc.myPos));
				points.add(p);
				ro.setWayPoints(points);
				OSM.this.startTask("route", new GeoPoint(p));
				return false;
			}
			@Override
			public boolean singleTapConfirmedHelper(GeoPoint arg0) {
				dv.closeAllList();
				return false;
			}
		};
		MapEventsOverlay OverlayEventos = new MapEventsOverlay(act.getBaseContext(), mReceive);
		mapView.getOverlays().add(OverlayEventos);
		mapView.invalidate();
	}
	private void initMylocMarker() {
		Drawable img = act.getResources().getDrawable(R.drawable.blue_point_16);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(
				act.getApplicationContext());
		myLocOverlay = new MyItemizedOverlay(img, resourceProxy);
		mapView.getOverlays().add(myLocOverlay);
	}

	private void initRouteMarker() {
		Drawable img = act.getResources().getDrawable(R.drawable.marker_blue);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(
				act.getApplicationContext());
		routeOverlay = new MyItemizedOverlay(img, resourceProxy);
		mapView.getOverlays().add(routeOverlay);
	}
	public void setDefaultZoomLevel(){
		if (mapView.getZoomLevel() < 13) {
			mapController.setZoom(13);
		}
		mapController.setZoom(13);
	}
	public void setZoomLevel(int level) {
		mapController.setZoom(level);
	}
	public void move(double lat,double lng) {
		GeoPoint gp = new GeoPoint(lat,lng);
		move(gp);
	}
	public void move(GeoPoint gp) {
		//mapController.animateTo(gp);
		mapController.setCenter(gp);
		Log.i(tag, "moved to my location: ");
	}
	public void move() {
		if(loc.myPos==null) return;
		GeoPoint gp = new GeoPoint(loc.myPos);
		move(gp);
	}

	public void updateMyLocationMarker(GeoPoint loc) {
		myLocOverlay.removeItem(myLocationMarker);
		myLocationMarker = new OverlayItem("me", "me", loc);
		myLocOverlay.addItem(myLocationMarker);
	}
	public void updateRouteMarker(Address addr) {
		GeoPoint gp = new GeoPoint(addr.getLatitude(),addr.getLongitude());
		routeOverlay.removeItem(routeMarker);
		String detailAddress = addr.getFeatureName()+", "+addr.getThoroughfare();
		String briefAddress = addr.getLocality()+", "+addr.getCountryName();
		routeMarker = new OverlayItem(detailAddress, briefAddress, gp);
		routeOverlay.addItem(routeMarker);
	}
	public void addPolyline(Polyline pl) {
		mapView.getOverlays().add(pl);
		mapView.invalidate();
		routePolyline = pl;
	}
	public void removePrevPolyline(){
		if (routePolyline != null){
			mapView.getOverlays().remove(routePolyline);
		}
	}
	public void removeAllRouteMarkers(){
		removeAllMarkers();
		removePrevPolyline();
	}

	private void removeAllMarkers() {
		for (Marker mk : markers) {
			mapView.getOverlays().remove(mk);
		}
	}
	
	public void refreshTileSource(String name){
		MapOptions.switchTileProvider(this,name);
		mapView.invalidate();
		switchTileProvider=true;
	}
	public void closeKeyBoard() {
		EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
		InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
	}

	public void startTask(String type,String address){
		if(type.equals("geo")){
			GeocoderTask task = new GeocoderTask(this, address);
			task.execute();
		}
	}
	public void startTask(String type,GeoPoint point){
		if(type.equals("geo")){
			GeocoderTask task = new GeocoderTask(this, point);
			task.execute();
		}else if(type.equals("route")){
			RouteTask task = new RouteTask(act, OSM.this, ro);
			task.execute();
		}
	}

	public void drawSteps(Road road) { // called from tasks
		for (int i = 0; i < road.mNodes.size(); i++) {
			addWayPointMarker(road,i);
		}
	}
/*	public void drawSteps(List<Step> steps) { // called from tasks
		for (Step step: steps) {
			addWayPointMarker(step);
		}
	}
	private void addWayPointMarker(Step step) {
		Marker nodeMarker = new Marker(mapView);
		nodeMarker.setPosition(step.getStartLocation());
		Drawable nodeIcon = act.getResources().getDrawable(R.drawable.red_point_16);
		nodeMarker.setIcon(nodeIcon);
		nodeMarker.setTitle("Step ");
		nodeMarker.setSnippet(step.getHtmlInstructions());
		nodeMarker.setSubDescription(Road.getLengthDurationText(step.getDistance().getValue(), step.getDuration().getValue()));
		//int resId = InfoWindow.getIconByManeuver(step.getManeuver());
		Drawable icon = act.getResources().getDrawable(R.drawable.ic_empty);
		nodeMarker.setImage(icon);
		mapView.getOverlays().add(nodeMarker);
		markers.add(nodeMarker);
	}*/
public void addWayPointMarker(Road road,int seq){
	RoadNode node = road.mNodes.get(seq);
	Marker nodeMarker = new Marker(mapView);
	nodeMarker.setPosition(node.mLocation);
	Drawable nodeIcon = act.getResources().getDrawable(R.drawable.red_point_16);
	nodeMarker.setIcon(nodeIcon);
	nodeMarker.setTitle("Step " + seq);
	nodeMarker.setSnippet(node.mInstructions);
	nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
	int resId = InfoWindow.getIconByManeuver(node.mManeuverType);
	Drawable icon = act.getResources().getDrawable(resId);
	nodeMarker.setImage(icon);
	mapView.getOverlays().add(nodeMarker);
	markers.add(nodeMarker);
	/*
	 * nodeMarker.setOnMarkerClickListener(new OnMarkerClickListener(){
	 *  public boolean onMarkerClick(Marker arg0, MapView arg1) {
	 *    Log.i(tag, "marker clicked"); //arg0.closeInfoWindow(); return true; 
	 *  } 
	 * });
	 */
}

}