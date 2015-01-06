package cat.app.maps;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;

import cat.app.navi.Route;
import cat.app.navi.RoutePolylineTask;
import cat.app.osmap.LOC;
import cat.app.osmap.MyItemizedOverlay;
import cat.app.osmap.R;

public class OSM {
	Activity act;
	MapView mapView;
	IMapController mapController;
	MyItemizedOverlay myItemizedOverlay;
	OverlayItem myLocationMarker;
	Route route;
	public Location myLoc;
	public void onCreate(Activity act){
		this.act=act;
        mapView  = (MapView) act.findViewById(R.id.osmap);
        mapController = mapView.getController();
        setMap();
        initMarker();
	}
	private void setMap() {
    	//mapView.getOverlay().remove(view);
        //mapView.removeView(view);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapView.setClickable(true);
        mapView.setLongClickable(true);
        //mapView.setUseDataConnection(false);
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setMinZoomLevel(4);
        //mapView.setTileSource(TileSourceFactory.MAPNIK);
        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean longPressHelper(GeoPoint arg0) {
                //Log.d("debug", "LongPress:("+arg0.getLatitude()+","+arg0.getLongitude()+")");
            	ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
            	points.add(new GeoPoint(myLoc));
            	points.add(arg0);
                (new RoutePolylineTask(act,OSM.this)).execute(points);
                return false;
            }
			@Override
			public boolean singleTapConfirmedHelper(GeoPoint arg0) {
				Log.d("debug", "SingleTap:("+arg0.getLatitude()+","+arg0.getLongitude()+")");
				return false;
			}
        };
        MapEventsOverlay OverlayEventos = new MapEventsOverlay(act.getBaseContext(), mReceive);
        mapView.getOverlays().add(OverlayEventos);
        mapView.invalidate();
	}
	
	private void initMarker(){
        Drawable img=act.getResources().getDrawable(R.drawable.blue_point_16);
        int markerWidth = img.getIntrinsicWidth();
        int markerHeight = img.getIntrinsicHeight();
        img.setBounds(0, markerHeight, markerWidth, 0);
        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(act.getApplicationContext());
        myItemizedOverlay = new MyItemizedOverlay(img, resourceProxy);
        mapView.getOverlays().add(myItemizedOverlay);
        //mapView.getOverlays().remove(myItemizedOverlay);
	}
	public void addMarker(GeoPoint gp){
        myLocationMarker = new OverlayItem("me", "me", gp);
        myItemizedOverlay.addItem(myLocationMarker);
	}
	public void removeMarker(OverlayItem item){
        myItemizedOverlay.removeItem(item);
	}
	public void setDefaultCenter(GeoPoint gp) {
		mapController.setCenter(gp);
		mapController.setZoom(13);
	}
	public int getMarkerSize(){
        return myItemizedOverlay.size();
	}
	public void updateMyLocationMarker(GeoPoint loc) {
		removeMarker(myLocationMarker);
		addMarker(loc);
	}
	public void addPolyline(Polyline pl) {
		mapView.getOverlays().add(pl);
		mapView.invalidate();
	}
}
