package wsn.park.ui.marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerClickListener;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import wsn.park.MyItemizedOverlay;
import wsn.park.R;
import wsn.park.map.poi.POI;
import wsn.park.maps.OSM;
import wsn.park.model.SavedPlace;
import wsn.park.util.MapOptions;
import wsn.park.util.RouteOptions;

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
	//OverlayItem myLocationMarker;

	public Marker myLocMarker;
	Marker routeMarker;

	OsmMapsItemizedOverlay pointOverlay;
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
		Drawable img = osm.act.getResources().getDrawable(R.drawable.blue_point_32);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(osm.act);
		myLocOverlay = new MyItemizedOverlay(img, resourceProxy);
		osm.map.getOverlays().add(myLocOverlay);
	}

	public void removePOIMarkers(){
		for(POI p:osm.mks.pois){
			osm.map.getOverlays().remove(p.poiMarker);
		}
		osm.mks.pois.clear();
	}
	/*public void updateMyLocationMarker(GeoPoint loc) {
		myLocOverlay.removeItem(myLocationMarker);
		myLocationMarker = new OverlayItem("me", "me", loc);
		myLocOverlay.addItem(myLocationMarker);
	}*/
	public void initMyLocMarker(Location loc) {
		if(loc==null) return;
		osm.map.getOverlays().remove(myLocMarker);
		myLocMarker = new Marker(osm.map);
		myLocMarker.setPosition(new GeoPoint(loc));
		Drawable img = osm.act.getResources().getDrawable(R.drawable.blue_radio_48);//ic_my_position_auto_follow
		myLocMarker.setIcon(img);
		myLocMarker.setDraggable(true);
		myLocMarker.setOnMarkerDragListener(new OnTestMarkerDragListener(osm));
		Drawable icon = osm.act.getResources().getDrawable(R.drawable.multiple_45);
		myLocMarker.setImage(icon);
		osm.map.getOverlays().add(myLocMarker);
	}
	/*
	public void initRouteMarker() {
		Drawable img = osm.act.getResources().getDrawable(R.drawable.marker_blue);
		int markerWidth = img.getIntrinsicWidth();
		int markerHeight = img.getIntrinsicHeight();
		img.setBounds(0, markerHeight, markerWidth, 0);
		ResourceProxy resourceProxy = new DefaultResourceProxyImpl(osm.act);
		routeOverlay = new MyItemizedOverlay(img, resourceProxy);
		osm.mapView.getOverlays().add(routeOverlay);
	}*/
/*	public void updateRouteMarker(Address addr) {
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
		////////////////////////////////not working
		routeMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
	        @Override
	        public boolean onMarkerClick(Marker marker, MapView mapView) {
	            //Toast.makeText(osm.act, "CLICK", Toast.LENGTH_SHORT).show();
	        	Log.w(tag, "clicked");
	            return true;
	        }
	    });
		Log.w(tag, "adding routeMarker="+routeMarker);
		osm.map.getOverlays().add(routeMarker);
		osm.move(gp);								//this will cause marker shown in screen ?????????????
		this.destinationMarkerList.add(routeMarker);  //this will cause marker not shown in screen ?????????????
		this.selectedMarker = routeMarker;
	}*/
	public void updatePointOverlay(SavedPlace addr){ //clickable
		osm.map.getOverlays().remove(pointOverlay);
		GeoPoint gp = new GeoPoint(addr.getLat(),addr.getLng());
		Drawable d = osm.act.getResources().getDrawable( R.drawable.marker_blue );
		ArrayList<OverlayItem> pList = new ArrayList<OverlayItem>();
		ResourceProxy resourceProxy = (ResourceProxy) new DefaultResourceProxyImpl(osm.act);
		prepareOverlay(pList,resourceProxy,addr);
		OverlayItem newOverlay = new OverlayItem(addr.getBriefName(), addr.getAdmin(), gp);
		newOverlay.setMarker(d);
		pointOverlay.addOverlay(newOverlay);
		osm.map.getOverlays().add(pointOverlay);
		//osm.map.invalidate();
		osm.move(gp);
		osm.dv.openPopup(addr);
		this.destinationMarkerList.add(routeMarker);
		this.selectedMarker = routeMarker;
	}
	private void prepareOverlay(ArrayList<OverlayItem> pList, ResourceProxy resourceProxy,final SavedPlace sp) {
		pointOverlay = new OsmMapsItemizedOverlay(pList,
        new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item){
            	osm.dv.openPopup(sp);
                return true; // We 'handled' this event.
            }
            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item){
                return true;
            }
        }, resourceProxy);
	}
	public void drawStepsPoint(Road road) { // called from tasks
		for (int i = 0; i < road.mNodes.size(); i++) {
			addWayPointMarker(road,i);
			//
		}
	}
	public void addWayPointMarker(Road road,int seq){
		RoadNode node = road.mNodes.get(seq); 
		Marker nodeMarker = new Marker(osm.map);
		nodeMarker.setPosition(node.mLocation);
		Drawable nodeIcon = osm.act.getResources().getDrawable(R.drawable.red_point_16);
		nodeMarker.setIcon(nodeIcon);
		if(node.mManeuverType==0){//Google API
			node.mManeuverType = getGoogleManeuverTypeFromText(node.mInstructions);
		}
		nodeMarker.setTitle(RouteOptions.getTurnString(node.mManeuverType));
		nodeMarker.setSnippet(node.mInstructions);
		nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
		//int code = RouteOptions.getManeuverCode(node.mManeuverType);
		//Log.i(tag, "turn_code=("+node.mManeuverType+")+text="+node.mInstructions);
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
	private int getGoogleManeuverTypeFromText(String origtext) {
		//Head <b>east</b> on <b>Elizabeth St</b> toward <b>Picton Ave</b>
		//Turn <b>left</b> onto <b>Picton Ave</b>
		//Turn <b>right</b> onto <b>Riccarton Rd</b>
		//Turn <b>right</b>
		//Turn <b>left</b>
		//At the roundabout, take the <b>2nd</b> exit onto <b>Riccarton Ave</b>
		//<b>Kahu Rd</b> turns slightly <b>right</b> and becomes <b>Kotare St</b><div style="font-size:0.9em">Destination will be on the right</div>
		int type = 0;
		String search_text = origtext.indexOf("Destination")>0?origtext.split("Destination")[0]:origtext;
		if(search_text.startsWith("Head")){
			type = 1;
		}else if(search_text.indexOf("left")>0){
			if(search_text.indexOf("slightly")>0) type = 3;
			else type = 4;
		}else if(search_text.indexOf("right")>0){
			if(search_text.indexOf("slightly")>0) type = 6;
			else type = 7;
		}else if(search_text.indexOf("roundabout")>0 ){
			if(search_text.indexOf("1st")>0) type = 27;
			else if(search_text.indexOf("2nd")>0) type = 28;
			else if(search_text.indexOf("3rd")>0) type = 29;
		}
		return type;
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
	public void showHidePOIs() {}
	/*public void updateRouteMarker(SavedPlace addr) {
		GeoPoint gp = new GeoPoint(addr.getLat(),addr.getLng());
		String detailAddress = addr.getBriefName();
		String briefAddress = addr.getAdmin();
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
	}*/
}
