package cat.app.map.markers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Address;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;
import cat.app.osmap.MyItemizedOverlay;
import cat.app.osmap.R;

public class Markers {
	static OSM osm;
	static Markers mks;
	private Markers(){
	}
	public static Markers getInstance(OSM osm) {
		Markers.osm = osm;
		if(mks==null) mks = new  Markers();
		return mks;
	}

	MyItemizedOverlay myLocOverlay;
	OverlayItem myLocationMarker;

	MyItemizedOverlay routeOverlay;
	OverlayItem routeMarker;
	public ArrayList<Marker> routeMarkerList = new ArrayList<Marker>();
	
	Polyline routePolyline;
	
	//List<Marker> markers= new ArrayList<Marker>();
/*	public void addMarker(GeoPoint p){
		Marker mk = new Marker(mapView);
		mk.setPosition(p);
		mk.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
		//mk.setIcon(icon);
		mk.setIcon(mapView.getResources().getDrawable(R.drawable.ic_launcher));
		mk.setTitle("Start point");
		mapView.getOverlays().add(mk);
		//markers.add(mk);
	}*/
	public void initMylocMarker() {
		Drawable img = osm.act.getResources().getDrawable(R.drawable.blue_point_16);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(osm.act);
		myLocOverlay = new MyItemizedOverlay(img, resourceProxy);
		osm.mapView.getOverlays().add(myLocOverlay);
	}
	public void updateMyLocationMarker(GeoPoint loc) {
		myLocOverlay.removeItem(myLocationMarker);
		myLocationMarker = new OverlayItem("me", "me", loc);
		myLocOverlay.addItem(myLocationMarker);
	}
	
	public void initRouteMarker() {
		Drawable img = osm.act.getResources().getDrawable(R.drawable.marker_blue);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(osm.act);
		routeOverlay = new MyItemizedOverlay(img, resourceProxy);
		osm.mapView.getOverlays().add(routeOverlay);
	}
	public void updateRouteMarker(Address addr) {
		GeoPoint gp = new GeoPoint(addr.getLatitude(),addr.getLongitude());
		routeOverlay.removeItem(routeMarker);
		String detailAddress = addr.getFeatureName()+", "+addr.getThoroughfare();
		String briefAddress = addr.getLocality()+", "+addr.getCountryName();
		routeMarker = new OverlayItem(detailAddress, briefAddress, gp);
		routeOverlay.addItem(routeMarker);
	}
	public void drawStepsPoint(Road road) { // called from tasks
		for (int i = 0; i < road.mNodes.size(); i++) {
			addWayPointMarker(road,i);
		}
	}
	public void addWayPointMarker(Road road,int seq){
		RoadNode node = road.mNodes.get(seq);
		Marker nodeMarker = new Marker(osm.mapView);
		nodeMarker.setPosition(node.mLocation);
		Drawable nodeIcon = osm.act.getResources().getDrawable(R.drawable.red_point_16);
		nodeMarker.setIcon(nodeIcon);
		nodeMarker.setTitle("Step " + seq);
		nodeMarker.setSnippet(node.mInstructions);
		nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
		int resId = InfoWindow.getIconByManeuver(node.mManeuverType);
		Drawable icon = osm.act.getResources().getDrawable(resId);
		nodeMarker.setImage(icon);
		osm.mapView.getOverlays().add(nodeMarker);
		routeMarkerList.add(nodeMarker);
		/*
		 * nodeMarker.setOnMarkerClickListener(new OnMarkerClickListener(){
		 *  public boolean onMarkerClick(Marker arg0, MapView arg1) {
		 *    Log.i(tag, "marker clicked"); //arg0.closeInfoWindow(); return true; 
		 *  } 
		 * });
		 */
	}
	private void removeAllMarkers() {
		for (Marker mk : routeMarkerList) {
			osm.mapView.getOverlays().remove(mk);
		}
	}	
	public void removeAllRouteMarkers(){
		removeAllMarkers();
		removePrevPolyline();
	}
	public void removePrevPolyline(){
		if (routePolyline != null){
			osm.mapView.getOverlays().remove(routePolyline);
		}
	}

	public void addPolyline(Polyline pl) {
		osm.mapView.getOverlays().add(pl);
		osm.mapView.invalidate();
		routePolyline = pl;
	}
}
