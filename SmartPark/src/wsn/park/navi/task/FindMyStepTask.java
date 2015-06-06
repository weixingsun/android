package wsn.park.navi.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.audio.MyPlayer;
import wsn.park.maps.OSM;
import wsn.park.ui.marker.InfoWindow;
import wsn.park.util.MathUtil;
import wsn.park.util.SavedOptions;


import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FindMyStepTask extends AsyncTask<GeoPoint, Void, String> {
	OSM osm;
	private Marker marker;
	RoadNode currNode;
	//RoadNode prevNode;
	boolean endFlag = false;
	private int toCurrent = 0;
	//private int toPrev = 0;
	private static final String tag = FindMyStepTask.class.getSimpleName();

	public FindMyStepTask() {
		super();
		this.osm = OSM.getInstance();
		this.marker = osm.mks.myLocMarker;
	}

	@Override
	protected String doInBackground(GeoPoint... params) {
		if(osm.loc.road==null || osm.loc.road.mNodes.size()<1){return null;}
		String comments = findCurrentStep(LOC.getMyPoint()); ////如果误差超过30米，会认为不在线路上，重新寻路
		return comments;
	}
	private String findCurrentStep(GeoPoint p) {
		String comments = null;
		osm.loc.onRoadIndex=isInStep(osm.lines, p);//0 means (#1,#2)
		Log.i(tag, "FindMyStepTask.findCurrentStep():phase="+osm.loc.onRoadIndex+1);
		if(osm.loc.onRoadIndex<0){
			if(osm.loc.passedNodes.size()<1){ //2.at very beginning
				comments="at very beginning";
				this.currNode = osm.loc.road.mNodes.get(0);
			}else{ //1.left route, redraw
				comments="redraw route";
				redrawRoutes(p);
				return null;
			}
		}else{//on route
			comments = "on route";
			this.currNode = osm.loc.road.mNodes.get(osm.loc.onRoadIndex);
			//osm.loc.road.mNodes.size() = osm.lines.size()+1
			//if(onRoadIndex==0) currNode=road.mNodes(0)
			//if(onRoadIndex==1) currNode=road.mNodes(1)
			//if(onRoadIndex==2) currNode=road.mNodes(2)
		}
		if(this.toCurrent<SavedOptions.VOICE_DISTANCE){	//near intersection,keep old
			comments +=" still in old intersection";
			if(!osm.loc.passedNodes.contains(this.currNode)){
				osm.loc.passedNodes.add(this.currNode);
				int index = osm.loc.passedNodes.size();
				playHintSounds(index);
			}
		}else{	//away intersection, point to next intersection
			comments +=" point to next intersection";
			this.currNode = osm.loc.road.mNodes.get(osm.loc.onRoadIndex+1);//osm.loc.road.mNodes.size() = osm.lines.size()+1
		}
		this.toCurrent = getDistance(p, this.currNode.mLocation);
		//this.prevNode = findPrevNode();
		//this.toPrev = getDistance(p, this.prevNode.mLocation);
		//int index = osm.loc.passedNodes.size();
		if(osm.loc.passedNodes.size()==osm.loc.road.mNodes.size() && this.toCurrent<SavedOptions.GPS_TOLERANCE){
			Log.i(tag, "the end of route");
			this.endFlag = true;
		}
		return comments;
	}
	public void redrawRoutes(GeoPoint start){
		Log.i(tag, "redraw route...");
		osm.ro.redraw(start);
		RouteTask task = new RouteTask(osm, osm.ro);
		task.execute();
		osm.loc.passedNodes.clear();
	}
	public int isInStep(List<Polyline> lines, GeoPoint point){
		int ret=-1;
		for(int i=0;i<lines.size();i++){
			boolean on = PolyUtil.isLocationOnPath(point, lines.get(i).getPoints(), false,SavedOptions.GPS_TOLERANCE);
			if(on) ret=i;
		}
		return ret;
	}
	public boolean isInStep(Road road, GeoPoint point){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(point, road.mRouteHigh, geodesic,SavedOptions.GPS_TOLERANCE); //tolerance=30 meters
	}
	/*public boolean isInStep(List<GeoPoint> points, GeoPoint point){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(point, points, geodesic,SavedOptions.GPS_TOLERANCE); //tolerance=30 meters
	}*/
	public boolean isInStep(Polyline line, GeoPoint point){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(point, line.getPoints(), geodesic,SavedOptions.GPS_TOLERANCE); //tolerance=30 meters
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

//	private RoadNode findPrevNode() {
//		return osm.loc.passedNodes.get(osm.loc.passedNodes.size()-1);
//	}
	private void cleanupAllonRoad() {
		osm.mks.removeAllRouteMarkers();
		osm.mks.removePrevPolyline();
		osm.loc.cleanupRoad();
		MyPlayer.clearPlayedList();
		this.cleanupRoad();
	}
	private void cleanupRoad() {
		toCurrent = 0;
		//toPrev = 0;
	}
	@Override
    protected void onPostExecute(String comments) {
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		//if(toCurrent>0){
		Toast.makeText(osm.act, comments, Toast.LENGTH_LONG).show();
		
		//String snippet = "node "+(index)+"/"+osm.loc.road.mNodes.size()+":("+osm.loc.passedNodes.size()+")toCurr="+toCurrent;
		//marker.setSnippet(snippet);
		int resId = InfoWindow.getIconByManeuver(currNode.mManeuverType);
		//Drawable flagImg = osm.act.getResources().getDrawable(resId);
		//marker.setImage(flagImg);
		//marker.showInfoWindow();
		String dist = "In "+this.toCurrent+"m";
		osm.dv.updateNaviInstruction(dist,resId);
		if(this.endFlag){
			cleanupAllonRoad();
			//confirm parking button
		}
		//}
    }
	public int getDistance(GeoPoint start, GeoPoint end){
		float[] results = new float[1];
		Location.distanceBetween(start.getLatitude(), start.getLongitude(),
		                end.getLatitude(), end.getLongitude(), results);
		return Math.round(results[0]);
	}

}
