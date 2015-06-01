package wsn.park.map.poi;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerClickListener;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import wsn.park.R;
import wsn.park.maps.MathUtil;
import wsn.park.maps.OSM;
import wsn.park.maps.ProviderUtil;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class LoadPOITask extends AsyncTask<String, Void, String> {
private static final String TAG = LoadPOITask.class.getSimpleName();
private OSM osm;
private MapsForgePOI mfpoi;
private List<MapTile> tilesNeeded = new ArrayList<MapTile>();
private List<PointOfInterest> newPOIs = new ArrayList<PointOfInterest>();
//private List<Marker> newMakers;
private BoundingBoxE6 bbE6Visible;
private int zoom;
public LoadPOITask(OSM osm) {
	//Log.w(TAG, "LoadPOITask:osm="+osm);
	this.osm = osm;
}
@Override
protected String doInBackground(String... params) {
	if(osm.offlineMapFile==null) return null;
	mfpoi = new MapsForgePOI(osm.offlineMapFile);
	zoom = osm.map.getZoomLevel();
	bbE6Visible = osm.getBoundary();
	//Log.w(TAG, "doInBackground");
	clearMarkers();
	ProviderUtil.calculateNeededTilesForZoomLevelInBoundingBox(tilesNeeded, zoom, bbE6Visible);
	for(MapTile pTile:tilesNeeded){
		List<PointOfInterest> POIs = mfpoi.getPOI(pTile);
		for(PointOfInterest a:POIs){
			if(!this.checkDuplicate(a, newPOIs)){
				newPOIs.add(a);
			}
		}
	}
	//Log.w(TAG, "newPOIs="+newPOIs.size()+",tilesNeeded.size="+tilesNeeded.size());
	//printPOIs(newPOIs);
	for(PointOfInterest p:newPOIs){
		createPOIMarker(R.drawable.square_outter_blue,p);
	}
	return null;
}
@Override  
protected void onPostExecute(String ret) {
    super.onPostExecute(ret);
    //Log.w(TAG, "POI.size="+osm.mks.pois.size());
    for(POI p:osm.mks.pois){
    	osm.map.getOverlays().add(p.poiMarker);
    }
	osm.map.invalidate();
    
}
public void createPOIMarker(int resId,PointOfInterest poi) {
	Marker newMarker = new Marker(osm.map);
	newMarker.setPosition(new GeoPoint(poi.position.latitude,poi.position.longitude));
	String name = poi.tags.size()>1?poi.tags.get(1).value:poi.tags.get(0).value;
	newMarker.setTitle(name);	//+":"+poi.layer
	//newMarker.setSnippet(poi.tags)
	//newMarker.setSubDescription(poi.tags)
	Drawable img = osm.act.getResources().getDrawable(resId);
	newMarker.setIcon(img);
	//Drawable icon = osm.act.getResources().getDrawable(resId);
	newMarker.setImage(img);
	newMarker.setOnMarkerClickListener(new OnMarkerClickListener(){
		@Override
		public boolean onMarkerClick(Marker arg0, MapView arg1) {
			osm.mks.selectedMarker = arg0;
			arg0.showInfoWindow();
			return false;
		}});
	POI newPOI = new POI(poi,newMarker);
	osm.mks.pois.add(newPOI);
}
public void clearMarkers(){
	osm.mks.pois.clear();
	osm.mks.removePOIMarkers();
	//Log.w(TAG, "POI.size="+osm.mks.pois.size()+",LatN="+bbE6Visible.getLatNorthE6()+",LatS="+bbE6Visible.getLatSouthE6()+",LngE="+bbE6Visible.getLonEastE6()+",LngW="+bbE6Visible.getLonWestE6());
	//osm.map.invalidate();
}
public boolean compare(PointOfInterest a, PointOfInterest b){
	if(MathUtil.compare(a.position, b.position)){
		return true;
	}
	return false;
}
public boolean checkDuplicate(PointOfInterest a, List<PointOfInterest> list){
	boolean ret = false;
	for(PointOfInterest p:list){
		if(compare(a,p)){
			ret=true;
		}
	}
	return ret;
}

private void printPOIs(List<PointOfInterest> list) {
	for(PointOfInterest p : list){
		String extra = p.tags.size()>1?p.tags.get(1).value:"";
		Log.i(TAG, "key="+p.tags.get(0).key+",value="+p.tags.get(0).value+",name="+extra);
	}
}
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
