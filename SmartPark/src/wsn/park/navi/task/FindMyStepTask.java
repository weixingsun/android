package wsn.park.navi.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import wsn.park.audio.MyPlayer;
import wsn.park.maps.OSM;
import wsn.park.ui.marker.InfoWindow;
import wsn.park.util.SavedOptions;


import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FindMyStepTask extends AsyncTask<GeoPoint, Void, Float> {
	OSM osm;
	GeoPoint myGP;
	//boolean onRoad=false;
	//RoadNode node;
	private Marker marker;
	RoadNode currNode;
	RoadNode lastNode;
	boolean endFlag = false;
	private int toCurrent = 0;
	private int toPrev = 0;
	private static final String TAG = FindMyStepTask.class.getSimpleName();
	//(new FindMyStepTask(osm, point)).execute();
	public FindMyStepTask(OSM osm, GeoPoint point) {
		super();
		this.osm = osm;
		this.myGP=point;
	}
	//drag marker
	public FindMyStepTask(OSM osm, GeoPoint point,Marker marker) {
		this(osm,point);
		this.marker = marker;
	}
	public boolean isInStep(Road road, GeoPoint point){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(point, road.mRouteHigh, geodesic,SavedOptions.GPS_TOLERANCE); //tolerance=30 meters
	}
	public boolean isInStep(List<GeoPoint> points, GeoPoint point){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(point, points, geodesic,SavedOptions.GPS_TOLERANCE); //tolerance=30 meters
	}
	public boolean isInStep(Polyline line, GeoPoint point){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(point, line.getPoints(), geodesic,SavedOptions.GPS_TOLERANCE); //tolerance=30 meters
	}
	private void findCurrentStep(GeoPoint p) {
		this.currNode = findCurrentNode();
		this.toCurrent = getDistance(p, this.currNode.mLocation);
		if(osm.loc.passedNodes.size()>0){
			this.lastNode = findLastNode();
			this.toPrev = getDistance(p, this.lastNode.mLocation);
			osm.loc.onRoad=isInStep(osm.polyline, p);
			if(!osm.loc.onRoad) {
				Log.i(TAG, "redraw route...");
				osm.ro.redraw(p);
				RouteTask task = new RouteTask(osm, osm.ro);
				task.execute();
				osm.loc.passedNodes.clear();
			}
		}else{
			this.toPrev=0;
		}
		//Log.i(TAG, "toCurrent="+this.toCurrent+":toPrev="+this.toPrev);
		if(this.toCurrent<SavedOptions.GPS_TOLERANCE){
			osm.loc.passedNodes.add(this.currNode);
			osm.loc.onRoad=true;
		}
		if(osm.loc.passedNodes.size()==osm.loc.road.mNodes.size() && this.toCurrent<SavedOptions.GPS_TOLERANCE){
			Log.i(TAG, "the end of route");
			this.endFlag = true;
		}
		
		return;
	}

@Override
protected Float doInBackground(GeoPoint... params) {
	if(osm.loc.road==null || osm.loc.road.mNodes.size()<1){return null;}
	//boolean onRoad=isInStep(osm.loc.road, myGP);	//如果误差超过30米，会认为不在线路上，重新寻路
	//osm.loc.onRoad = onRoad;
	findHintMarkers();
	return null;
}

private void playHintSounds(int index) {
	if(index<osm.loc.road.mNodes.size()-1){
		//RoadNode nextNode = osm.loc.road.mNodes.get(osm.loc.currIndex+1);
		//if(this.toCurrent>SavedOptions.GPS_TOLERANCE && this.toPrev>SavedOptions.GPS_TOLERANCE){
		if(this.toCurrent<SavedOptions.VOICE_DISTANCE && !MyPlayer.isPlayed(index)){
			//if(this.toCurrent>200 && this.toCurrent < 500) return;
			MyPlayer.setPlayedId(index);
			marker.setTitle("in "+this.toCurrent+" m, "+this.currNode.mInstructions);
			BigDecimal bd = new BigDecimal(this.toCurrent).setScale(-2, BigDecimal.ROUND_HALF_UP);  //整百
			MyPlayer.play(osm.act, this.currNode, bd.intValue());
			Toast.makeText(osm.act, "playing "+index+":"+bd.intValue(), Toast.LENGTH_LONG).show();
			
		}
	}
}
private void findHintMarkers() {
	findCurrentStep(marker.getPosition());
}
private RoadNode findCurrentNode() {
	for (RoadNode n:osm.loc.road.mNodes){
		if(osm.loc.passedNodes.contains(n)) {
			continue;
		}else{
			return n;
		}
	}
	return null;
}
private RoadNode findLastNode() {
	return osm.loc.passedNodes.get(osm.loc.passedNodes.size()-1);
}
	private void cleanupAllonRoad() {
		osm.mks.removeAllRouteMarkers();
		osm.mks.removePrevPolyline();
		osm.loc.cleanupRoad();
		MyPlayer.clearPlayedList();
		this.cleanupRoad();
	}
	private void cleanupRoad() {
		toCurrent = 0;
		toPrev = 0;
	}
	@Override
    protected void onPostExecute(Float useless) {
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		if(toCurrent>0){
			int index = osm.loc.passedNodes.size();
			playHintSounds(index);
			String snippet = "node "+(index)+"/"+osm.loc.road.mNodes.size()+":("+osm.loc.passedNodes.size()+")toCurr="+toCurrent+",toPrev="+this.toPrev;
			marker.setSnippet(snippet);
			int resId = InfoWindow.getIconByManeuver(currNode.mManeuverType);
			Drawable flagImg = osm.act.getResources().getDrawable(resId);
			marker.setImage(flagImg);
			marker.showInfoWindow();
			String dist = "In "+this.toCurrent+"m";
			osm.dv.updateNaviInstruction(dist,resId);
			if(this.endFlag){
				cleanupAllonRoad();
				//osm.move();
			}
		}
		
    }
	public int getDistance(GeoPoint start, GeoPoint end){
		float[] results = new float[1];
		Location.distanceBetween(start.getLatitude(), start.getLongitude(),
		                end.getLatitude(), end.getLongitude(), results);
		return Math.round(results[0]);
	}

}
