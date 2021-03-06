package wsn.park.ui.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import android.graphics.drawable.Drawable;
import android.util.Log;

import wsn.park.LOC;
import wsn.park.R;
import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.model.DataBus;
import wsn.park.model.Place;
import wsn.park.model.SavedPlace;
import wsn.park.util.DbHelper;
import wsn.park.util.MathUtil;
import wsn.park.util.RouteOptions;

public class Markers {
	private static final String tag = Markers.class.getSimpleName();
	private static OSM osm;
	private static Markers mks;
	private DbHelper dbHelper;
	public PlaceMarker tempMarker;
	private DataBus bus = DataBus.getInstance();
	private Markers(){
		dbHelper = DbHelper.getInstance();
	}
	public static Markers getInstance(OSM osm) {
		Markers.osm = osm;
		if(mks==null) mks = new Markers();
		return mks;
	}
	public Marker myLocMarker;
	public Marker hintMarker;
	public PlaceMarker selectedMarker;
	public List<Marker> waypointsMarkerList = new ArrayList<Marker>();
	public List<Marker> destinationMarkerList = new ArrayList<Marker>();
	public List<PlaceMarker> savedPlaceMarkers = new CopyOnWriteArrayList<PlaceMarker>();
	//public CopyOnWriteArrayList<POI> pois = new CopyOnWriteArrayList<POI>();
	
	Polyline routePolyline;
	
	public void initMylocMarker() {
		if(LOC.myPos==null){
			if(bus.getMyPoint()==null)
				return;
		}else{
			bus.setMyPoint(new GeoPoint(LOC.myPos));
		}
		osm.map.getOverlays().remove(myLocMarker);
		myLocMarker = new Marker(osm.map);
		myLocMarker.setPosition(bus.getMyPoint());
		Drawable img = osm.act.getResources().getDrawable(R.drawable.blue_radio_48);//ic_my_position_auto_follow
		myLocMarker.setIcon(img);
		myLocMarker.setDraggable(true);
		myLocMarker.setOnMarkerDragListener(new OnTestMarkerDragListener());
		myLocMarker.setOnMarkerClickListener(null);
		myLocMarker.setInfoWindow(null);
		osm.map.getOverlays().add(myLocMarker);
	}
	
	public Marker updateTargetMarker(Place p) {
		this.changeLastMarkerIcon(R.drawable.star_red_24);
		GeoPoint gp = new GeoPoint(p.getLat(),p.getLng());
		Drawable d = osm.act.getResources().getDrawable( R.drawable.marker_sky_80 );
		selectedMarker = new PlaceMarker(p);
		selectedMarker.setPosition(p.getPosition());
		selectedMarker.setIcon(d);
		selectedMarker.setInfoWindow(null);
//		selectedMarker.setAnchor(Marker.ANCHOR_CENTER, 1.0f);
		osm.map.getOverlays().add(selectedMarker);
		osm.move(gp);
		this.destinationMarkerList.add(selectedMarker);
		if(!this.savedPlaceMarkers.contains(this.selectedMarker)){
			this.tempMarker=selectedMarker;
			//Log.i(tag, "this is a temp marker");
		}else{
			this.tempMarker=null;
		}
		return selectedMarker;
	}
	public Marker updateTargetMarker(PlaceMarker pm) {
		this.changeLastMarkerIcon(R.drawable.star_red_24);
		Drawable d = osm.act.getResources().getDrawable( R.drawable.marker_sky_80 );
		selectedMarker = pm;
		selectedMarker.setIcon(d);
		osm.map.getOverlays().add(selectedMarker);
		osm.move(pm.getPosition());
		this.destinationMarkerList.add(selectedMarker);
		if(!this.savedPlaceMarkers.contains(this.selectedMarker)){
			this.tempMarker=selectedMarker;
			//Log.i(tag, "this is a temp marker");
		}else{
			this.tempMarker=null;
		}
		return selectedMarker;
	}
	public PlaceMarker findMyPlace(GeoPoint gp) {
		for(PlaceMarker a:savedPlaceMarkers){
			if(MathUtil.compare(a.getPosition(), gp)){
				Log.i(tag, "found");
				return a;
			}
		}
		Log.i(tag, "not found");
		return null;
	}

	public void drawStepsPoint(Road road) { // called from tasks
		for (int i = 0; i < road.mNodes.size(); i++) {
			addWayPointMarker(road,i);
			//
		}
	}
	public void updateHintMarker(){
		if(hintMarker==null){
			hintMarker = new Marker(osm.map);
			osm.map.getOverlays().add(hintMarker);
			Drawable nodeIcon = osm.act.getResources().getDrawable(R.drawable.star_yellow_16);
			hintMarker.setIcon(nodeIcon);
			//Log.w(tag, "addHintMarker()");
		}
		//Log.w(tag, "updateHintMarker()="+DataBus.getInstance().getHintPoint());
		if(DataBus.getInstance().getHintPoint()!=null)
			hintMarker.setPosition(DataBus.getInstance().getHintPoint());
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
		osm.map.getOverlays().remove(hintMarker);
	}	
	public void removeAllRouteMarkers(){
		removeAllMarkers();
		removePrevPolyline();
		//osm.map.invalidate();
		DataBus.clearPlayedList();
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

	public void showHidePOIs() {}

	public void showAllSavedPlaces(){
		//savedPlaceMarkers
		List<SavedPlace> list = dbHelper.getAllSavedPlaces();
		for(SavedPlace sp:list){
			GeoPoint gp = new GeoPoint(sp.getLat(),sp.getLng());
			Drawable star = osm.act.getResources().getDrawable( R.drawable.star_red_24 );
			PlaceMarker base = new PlaceMarker(sp); //gp
			base.setPosition(gp);
			base.setIcon(star);
			base.setOnMarkerClickListener(null);
			base.setInfoWindow(null);
			savedPlaceMarkers.add(base);
			osm.map.getOverlays().add(base);
		}
	}
	public void changeLastMarkerIcon(int resId) {
		//Log.d(tag, "selectedMarker="+selectedMarker);
		if(this.selectedMarker==null) return;
		//if it was a temp marker, then delete it.
		if(selectedMarker.equals(this.tempMarker)){
			osm.map.getOverlays().remove(tempMarker);
		}
		Drawable d = osm.act.getResources().getDrawable( resId );
		selectedMarker.setIcon(d);
	}
	public void removeTempMarker(PlaceMarker tapMarker) {
		if(tapMarker!=null && tapMarker.equals(this.tempMarker)) return;
		if(Mode.getID()==Mode.NAVI) return;
		osm.map.getOverlays().remove(tempMarker);
		osm.map.invalidate();
	}

}
