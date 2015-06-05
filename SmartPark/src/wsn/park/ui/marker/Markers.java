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
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
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

import wsn.park.LOC;
import wsn.park.MyItemizedOverlay;
import wsn.park.R;
import wsn.park.map.poi.POI;
import wsn.park.maps.OSM;
import wsn.park.model.SavedPlace;
import wsn.park.util.DbHelper;
import wsn.park.util.MapOptions;
import wsn.park.util.MathUtil;
import wsn.park.util.RouteOptions;

public class Markers {
	private static final String tag = Markers.class.getSimpleName();
	private static OSM osm;
	private static Markers mks;
	private DbHelper dbHelper;
	private Markers(){
		dbHelper = DbHelper.getInstance();
	}
	public static Markers getInstance(OSM osm) {
		Markers.osm = osm;
		if(mks==null) mks = new Markers();
		return mks;
	}
	public Marker myLocMarker;
	//private OsmMapsItemizedOverlay pointOverlay;
	public  OsmMapsItemizedOverlay selectedMarker;
	public List<Marker> waypointsMarkerList = new ArrayList<Marker>();
	public List<OsmMapsItemizedOverlay> destinationMarkerList = new ArrayList<OsmMapsItemizedOverlay>();
	public List<OsmMapsItemizedOverlay> savedPlaceMarkers = new CopyOnWriteArrayList<OsmMapsItemizedOverlay>();
	public CopyOnWriteArrayList<POI> pois = new CopyOnWriteArrayList<POI>();
	
	Polyline routePolyline;
	
	public void initMylocMarker() {
		if(LOC.myPos==null){
			if(LOC.myLastPos==null)
				return;
		}else{
			LOC.myLastPos=new GeoPoint(LOC.myPos);
		}
		osm.map.getOverlays().remove(myLocMarker);
		myLocMarker = new Marker(osm.map);
		myLocMarker.setPosition(LOC.myLastPos);
		Drawable img = osm.act.getResources().getDrawable(R.drawable.blue_radio_48);//ic_my_position_auto_follow
		myLocMarker.setIcon(img);
		myLocMarker.setDraggable(true);
		myLocMarker.setOnMarkerDragListener(new OnTestMarkerDragListener());
		myLocMarker.setOnMarkerClickListener(null);
		//myLocMarker.closeInfoWindow();
		osm.map.getOverlays().add(myLocMarker);
	}
	
	public OsmMapsItemizedOverlay updateDestinationOverlay(SavedPlace addr){ //clickable
		if(selectedMarker!=null&& selectedMarker.getSp().getSpecial()<SavedPlace.NORMAL){
			osm.map.getOverlays().remove(selectedMarker);//delete last not saved pin
		}
		selectedMarker=findMyPlace(addr);
		if(selectedMarker==null){
			GeoPoint gp = new GeoPoint(addr.getLat(),addr.getLng());
			Drawable d = osm.act.getResources().getDrawable( R.drawable.marker_azure_48 );
			selectedMarker = constructOverlay(addr);
			OverlayItem newOverlay = new OverlayItem(addr.getBriefName(), addr.getAdmin(), gp);
			newOverlay.setMarker(d);
			selectedMarker.addOverlay(newOverlay);
			selectedMarker.setSp(addr);
			osm.map.getOverlays().add(selectedMarker);
			//osm.map.invalidate();
		}else{
			addr.setSpecial(selectedMarker.getSp().getSpecial());
		}
		osm.move(addr.getPosition());
		osm.dv.openPlacePopup(selectedMarker);
		this.destinationMarkerList.add(selectedMarker);
		return selectedMarker;
	}
	public OsmMapsItemizedOverlay findMyPlace(SavedPlace addr) {
		for(OsmMapsItemizedOverlay a:savedPlaceMarkers){
			if(MathUtil.compare(addr.getPosition(), a.getSp().getPosition())){
				Log.i(tag, "found");
				return a;
			}
		}
		Log.i(tag, "not found");
		return null;
	}
	private OsmMapsItemizedOverlay constructOverlay(final SavedPlace sp) {
		ResourceProxy resourceProxy = (ResourceProxy) new DefaultResourceProxyImpl(osm.act);
		ArrayList<OverlayItem> pList = new ArrayList<OverlayItem>();
		OnItemGestureListener<OverlayItem> listener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item){
            	osm.dv.openPlacePopup(sp);
                return true; // We 'handled' this event.
            }
            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item){
                return true;
            }
        };
		return new OsmMapsItemizedOverlay(pList,listener, resourceProxy);
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
			node.mManeuverType = RouteOptions.getGoogleManeuverTypeFromText(node.mInstructions);
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
		osm.map.invalidate();
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
/*	public void setNaviImage(){
		ImageView navi = (ImageView) osm.act.findViewById(R.id.navi);
		navi.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(selectedMarker!=null){
					osm.ro.setWayPoints(new GeoPoint(osm.loc.myPos),selectedMarker.getPosition());
					osm.startTask("route", selectedMarker.getPosition(),"route");
				}
			}});
	}*/

	/*public void removePOIMarkers(){
		for(POI p:osm.mks.pois){
			osm.map.getOverlays().remove(p.poiMarker);
		}
		osm.mks.pois.clear();
	}*/
	public void showHidePOIs() {}

	public void showAllSavedPlaces(){
		//savedPlaceMarkers
		List<SavedPlace> list = dbHelper.getAllSavedPlaces();
		for(SavedPlace sp:list){
			GeoPoint gp = new GeoPoint(sp.getLat(),sp.getLng());
			Drawable d = osm.act.getResources().getDrawable( R.drawable.heart_24_x );
			OsmMapsItemizedOverlay base = constructOverlay(sp);
			OverlayItem newOverlay = new OverlayItem("", "", gp);
			newOverlay.setMarker(d);
			base.addOverlay(newOverlay);
			base.setSp(sp);
			savedPlaceMarkers.add(base);
			osm.map.getOverlays().add(base);
		}
	}
	public void changeMarkerIcon(int resId) {
		Drawable d = osm.act.getResources().getDrawable( resId );
		osm.mks.selectedMarker.firstOverlay().setMarker(d);
	}
}
