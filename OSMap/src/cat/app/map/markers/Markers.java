package cat.app.map.markers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import cat.app.map.poi.POI;
import cat.app.maps.OSM;
import cat.app.osmap.MyItemizedOverlay;
import cat.app.osmap.R;
import cat.app.osmap.util.MapOptions;

public class Markers {
	private static final String tag = Markers.class.getSimpleName();
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

	public Marker testMarker;
	Marker routeMarker;
	public List<Marker> waypointsMarkerList = new ArrayList<Marker>();
	public List<Marker> destinationMarkerList = new ArrayList<Marker>();
	//public List<Marker> poiMarkerList = new CopyOnWriteArrayList<Marker>();
	public CopyOnWriteArrayList<POI> pois = new CopyOnWriteArrayList<POI>();
	
	Polyline routePolyline;
	public Marker selectedMarker;
	
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
		osm.map.getOverlays().add(myLocOverlay);
	}
	/*
	public int cleanPOIs(boolean all){
		if(all){
			this.pois.clear();
			return 0;
		}else{
			int i = 0;
			BoundingBoxE6 box = osm.getBoundary();
			for(POI poi:this.pois){
				if(!osm.InBoundary(new GeoPoint(poi.poiInfo.position.latitude,poi.poiInfo.position.longitude),box)){
					this.pois.remove(poi);
					osm.map.getOverlays().remove(poi.poiMarker);
					i++;
				}
			}
			return i;
		}
	}
	public void addPOIMarker(int resId,PointOfInterest poi) {
		Marker newMarker = new Marker(osm.map);
		newMarker.setPosition(new GeoPoint(poi.position.latitude,poi.position.longitude));
		
		String name = poi.tags.size()>1?poi.tags.get(1).value:poi.tags.get(0).value;
		newMarker.setTitle(name+":"+poi.layer);
		//newMarker.setSnippet(poi.tags)
		//newMarker.setSubDescription(poi.tags)
		Drawable img = osm.act.getResources().getDrawable(resId);
		newMarker.setIcon(img);
		//Drawable icon = osm.act.getResources().getDrawable(resId);
		newMarker.setImage(img);
		osm.map.getOverlays().add(newMarker);
		//poiMarkerList.add(newMarker);
	}
	public void addPOIMarkers() {
		for(POI poi:this.pois){
			addPOIMarker(R.drawable.square_outter_blue,poi.poiInfo);
		}
	}*/
	public void removePOIMarkers(){
		for(POI p:osm.mks.pois){
			osm.map.getOverlays().remove(p.poiMarker);
		}
		osm.mks.pois.clear();
	}
	public void updateMyLocationMarker(GeoPoint loc) {
		myLocOverlay.removeItem(myLocationMarker);
		myLocationMarker = new OverlayItem("me", "me", loc);
		myLocOverlay.addItem(myLocationMarker);
	}
	public void initTestMarker(Location loc) {
		if(loc==null) return;
		osm.map.getOverlays().remove(testMarker);
		testMarker = new Marker(osm.map);
		testMarker.setPosition(new GeoPoint(loc));
		Drawable img = osm.act.getResources().getDrawable(R.drawable.beetle_64);
		testMarker.setIcon(img);
		testMarker.setDraggable(true);
		testMarker.setOnMarkerDragListener(new OnTestMarkerDragListener(osm));
		Drawable icon = osm.act.getResources().getDrawable(R.drawable.home_icon);
		testMarker.setImage(icon);
		osm.map.getOverlays().add(testMarker);
	}
	/*public void initRouteMarker() {
		Drawable img = osm.act.getResources().getDrawable(R.drawable.marker_blue);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(osm.act);
		routeOverlay = new MyItemizedOverlay(img, resourceProxy);
		osm.mapView.getOverlays().add(routeOverlay);
	}*/
	public void updateRouteMarker(Address addr) {
		GeoPoint gp = new GeoPoint(addr.getLatitude(),addr.getLongitude());
		String detailAddress = addr.getFeatureName()+", "+addr.getThoroughfare();
		String briefAddress = addr.getLocality()+", "+addr.getCountryName();
		osm.map.getOverlays().remove(routeMarker);
		routeMarker = new Marker(osm.map);
		routeMarker.setPosition(gp);
		routeMarker.setEnabled(true);
		routeMarker.setTitle(detailAddress);
		routeMarker.setSnippet(briefAddress);
		Drawable icon = osm.act.getResources().getDrawable(R.drawable.marker_blue);
		routeMarker.setIcon(icon);
		Drawable img = osm.act.getResources().getDrawable(R.drawable.ic_arrived);
		routeMarker.setImage(img);
		//Log.w(tag, "adding routeMarker="+routeMarker);
		osm.map.getOverlays().add(routeMarker);
		osm.move(gp);								//this will cause marker shown in screen ?????????????
		this.destinationMarkerList.add(routeMarker);  //this will cause marker not shown in screen ?????????????
		this.selectedMarker = routeMarker;
	}
	public void drawStepsPoint(Road road) { // called from tasks
		for (int i = 0; i < road.mNodes.size(); i++) {
			addWayPointMarker(road,i);
		}
	}
	public void addWayPointMarker(Road road,int seq){
		RoadNode node = road.mNodes.get(seq); 
		Marker nodeMarker = new Marker(osm.map);
		nodeMarker.setPosition(node.mLocation);
		Drawable nodeIcon = osm.act.getResources().getDrawable(R.drawable.red_point_16);
		nodeMarker.setIcon(nodeIcon);
		nodeMarker.setTitle("Step " + seq);
		nodeMarker.setSnippet(node.mInstructions);
		nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
		int resId = InfoWindow.getIconByManeuver(node.mManeuverType);
		Drawable icon = osm.act.getResources().getDrawable(resId);
		nodeMarker.setImage(icon);
		osm.map.getOverlays().add(nodeMarker);
		waypointsMarkerList.add(nodeMarker);
		/*
		 * nodeMarker.setOnMarkerClickListener(new OnMarkerClickListener(){
		 *  public boolean onMarkerClick(Marker arg0, MapView arg1) {
		 *    Log.i(tag, "marker clicked"); //arg0.closeInfoWindow(); return true; 
		 *  } 
		 * });
		 */
	}
	private void removeAllMarkers() {
		for (Marker mk : waypointsMarkerList) {
			osm.map.getOverlays().remove(mk);
		}
		/*for (Marker mk : this.destinationMarkerList.keySet()){
			osm.mapView.getOverlays().remove(mk);
		}*/
	}	
	public void removeAllRouteMarkers(){
		removeAllMarkers();
		removePrevPolyline();
	}
	public void removePrevPolyline(){
		if (routePolyline != null){
			osm.map.getOverlays().remove(routePolyline);
		}
	}

	public void addPolyline(Polyline pl) {
		osm.map.getOverlays().add(pl);
		osm.map.invalidate();
		routePolyline = pl;
	}
	public void setNaviImage(){
		ImageView navi = (ImageView) osm.act.findViewById(R.id.navi);
		navi.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(selectedMarker!=null){
					osm.ro.setWayPoints(new GeoPoint(osm.loc.myPos),selectedMarker.getPosition());
					osm.startTask("route", selectedMarker.getPosition(),"route");
				}
			}});
	}
	public void showHidePOIs() {
		
		
	}
}
