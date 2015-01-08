package cat.app.maps;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerClickListener;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
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
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import cat.app.map.markers.InfoWindow;
import cat.app.navi.RouteOptions;
import cat.app.navi.RouteTask;
import cat.app.osmap.Device;
import cat.app.osmap.LOC;
import cat.app.osmap.MyItemizedOverlay;
import cat.app.osmap.R;

public class OSM {
	protected static final String tag = OSM.class.getSimpleName();
	public LOC loc = new LOC();
	public Device dv = new Device();
	MapOptions mo;
	RouteOptions ro;
	Activity act;
	MapView mapView;
	IMapController mapController;
	MyItemizedOverlay myItemizedOverlay;
	OverlayItem myLocationMarker;
	// Route route;
	Polyline routePolyline;
	//public Location myLoc;
	public ArrayList<Marker> markers = new ArrayList<Marker>();

	public void init(Activity act) {
		this.act = act;
		mapView = (MapView) act.findViewById(R.id.osmap);
		mapController = mapView.getController();
		setMap();
		initMarker();
        loc.init(act,this);
        dv.init(act,this);
	}

	private void setMap() {
		// mapView.getOverlay().remove(view);
		// mapView.removeView(view);
		mo = MapOptions.getInstance(this);
		ro = RouteOptions.getInstance(this);
		initTileSources();
		mapView.setBuiltInZoomControls(true);
		mapView.setMultiTouchControls(true);
		mapView.setClickable(true);
		mapView.setLongClickable(true);
		mapView.setUseDataConnection(false);
		mapView.setMinZoomLevel(4);
		switchTileSource(MapOptions.MAP_MAPQUESTOSM);
		// workaround for:OpenGLRenderer(3672): 
		// Path too large to be rendered into a texture
		mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		
		MapEventsReceiver mReceive = new MapEventsReceiver() {
			@Override
			public boolean longPressHelper(GeoPoint arg0) {
				// Log.d("debug",
				// "LongPress:("+arg0.getLatitude()+","+arg0.getLongitude()+")");
				ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
				if (loc.myPos == null)
					return false;
				points.add(new GeoPoint(loc.myPos));
				points.add(arg0);
				//GeoPoint[] arr = points.toArray(new GeoPoint[points.size()]);
				ro.setWayPoints(points);
				(new RouteTask(act, OSM.this, ro)).execute();
				return false;
			}

			@Override
			public boolean singleTapConfirmedHelper(GeoPoint arg0) {
				dv.closeAllList();
				return false;
			}
		};
		MapEventsOverlay OverlayEventos = new MapEventsOverlay(
				act.getBaseContext(), mReceive);
		mapView.getOverlays().add(OverlayEventos);
		mapView.invalidate();
	}
	private void initMarker() {
		Drawable img = act.getResources().getDrawable(R.drawable.blue_point_16);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(
				act.getApplicationContext());
		myItemizedOverlay = new MyItemizedOverlay(img, resourceProxy);
		mapView.getOverlays().add(myItemizedOverlay);
	}

	public void addMarker(GeoPoint gp) {
		myLocationMarker = new OverlayItem("me", "me", gp);
		myItemizedOverlay.addItem(myLocationMarker);
	}

	public void removeMarker(OverlayItem item) {
		myItemizedOverlay.removeItem(item);
	}

	public void setCenter(GeoPoint gp) {
		if (mapView.getZoomLevel() < 13) {
			mapController.setZoom(13);
		}
		mapController.setCenter(gp);
		loc.gps_fired = true;
		Log.i(tag, "gps fired");
	}
	public void setCenter() {
		GeoPoint gp = new GeoPoint(loc.myPos);
		mapController.setCenter(gp);
	}
	public int getMarkerSize() {
		return myItemizedOverlay.size();
	}

	public void updateMyLocationMarker(GeoPoint loc) {
		removeMarker(myLocationMarker);
		addMarker(loc);
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
	public void drawSteps(Road road) { // called from tasks
		for (int i = 0; i < road.mNodes.size(); i++) {
			addMarker(road,i);
		}
	}
public void addMarker(Road road,int seq){
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
	 * 
	 * @Override public boolean onMarkerClick(Marker arg0, MapView arg1)
	 * { Log.i(tag, "marker clicked"); //arg0.closeInfoWindow(); return
	 * true; } });
	 */
}
	public void switchTravelMode() {

	}

	private void removeAllMarkers() {
		for (Marker mk : markers) {
			mapView.getOverlays().remove(mk);
		}
	}
	/*
	 * Mapnik, CycleMap, OSMPublicTransport, MapquestOSM, MapquestAerial, 
	 * Google Maps, Google Maps Satellite, Google Maps Terrain, 
	 * Yahoo Maps, Yahoo Maps Satellite, 
	 * Microsoft Maps, Microsoft Earth, Microsoft Hybrid
	 */
	private void initTileSources(){
		CloudmadeUtil.retrieveCloudmadeKey(act.getApplicationContext());
		ArrayList<ITileSource> list = TileSourceFactory.getTileSources();
        final int size = list.size();
        TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(MapOptions.MAP_GOOGLE_ROADMAP, ResourceProxy.string.unknown, 0, 20, 256, ".png",size, "http://mt0.google.com/vt/lyrs=m@127&"));
        TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(MapOptions.MAP_GOOGLE_SATELLITE, ResourceProxy.string.unknown, 0, 20, 256, ".png",size+1, "http://mt0.google.com/vt/lyrs=s@127,h@127&"));
        TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(MapOptions.MAP_GOOGLE_TERRAIN, ResourceProxy.string.unknown, 0, 20, 256, ".jpg",size+2, "http://mt0.google.com/vt/lyrs=t@127,r@127&"));
        
        //TileSourceFactory.addTileSource(new OSMMapYahooRenderer(MapOptions.MAP_YAHOO_ROADMAP,ResourceProxy.string.unknown,0,17,256,".jpg",size + 3,"http://maps.yimg.com/hw/tile?"));
        //TileSourceFactory.addTileSource(new OSMMapYahooRenderer(MapOptions.MAP_YAHOO_SATELLITE,ResourceProxy.string.unknown,0,17,256,".jpg",size + 4,"http://maps.yimg.com/ae/ximg?"));
        
        TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(MapOptions.MAP_MS_ROADMAP,ResourceProxy.string.unknown,0,19,256,".png",size + 5,"http://r0.ortho.tiles.virtualearth.net/tiles/r"));
        TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(MapOptions.MAP_MS_EARTH,ResourceProxy.string.unknown,0,19,256,".jpg",size + 6,"http://a0.ortho.tiles.virtualearth.net/tiles/a"));
        TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(MapOptions.MAP_MS_HYBRID,ResourceProxy.string.unknown,0,19,256,".jpg",size + 7,"http://h0.ortho.tiles.virtualearth.net/tiles/h"));
        
        //TileSourceFactory.addTileSource(new OSMMapGoogleRenderer("Google Maps Hybrid", ResourceProxy.string.unknown, 0, 19, 256, ".jpg", size+8, "http://mt0.google.com/vt/lyrs=m@127,s@127,h@127,r@127&"));  //mt0.google.com/vt/lyrs=h@159000000&hl=ru
        //TileSourceFactory.addTileSource(getTileSource("MapquestOSM"));
	}
	private ITileSource getTileSource(String name){
		
		return TileSourceFactory.getTileSource(name);
	}
	public void switchTileSource(String name) {
		ITileSource its = getTileSource(name);
		mapView.setTileSource(its);
	}
	public void refreshTileSource(String name){
		switchTileSource(name);
		mapView.invalidate();
	}
	public void closeKeyBoard() {
		EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
		InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
	}
	public void setTravelMode(String mode){
		
	}
}