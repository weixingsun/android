package wsn.park.navi.task;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.audio.MyPlayer;
import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.model.DataBus;
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
	RoadNode currNode;
	boolean endFlag = false;
	boolean reDraw = false;
	private int toCurrent = 0;
	DataBus bus = DataBus.getInstance();
	private static final String tag = FindMyStepTask.class.getSimpleName();

	public FindMyStepTask() {
		this.osm = OSM.getInstance();
	}

	@Override
	protected String doInBackground(GeoPoint... params) {
		
		if(osm.loc.road==null || osm.loc.road.mNodes.size()<1){return null;}
		if(isWorking() || isRedrawing()){
			Log.i(tag, "working/redrawing, skip this call");
			return null;
		}
		return findCurrentStep(osm.mks.myLocMarker.getPosition()); //如果误差超过30米，会认为不在线路上，重新寻路
	}
	private String findCurrentStep(GeoPoint p) {
		osm.loc.onRoadIndex=isInStep(osm.lines, p);//0 means (#1,#2)
		//String comments = "phase="+osm.loc.onRoadIndex;
		if(osm.loc.onRoadIndex<0){
			//comments="redraw route";
			redrawRoutes(p);
			return null;
		} else {//on route
			this.currNode = osm.loc.road.mNodes.get(osm.loc.onRoadIndex+1);
		}
		if(currNode==null) return null;
		this.toCurrent = getDistance(p, this.currNode.mLocation);
		boolean isEnd = MathUtil.compare(DataBus.getInstance().getEndPoint(), this.currNode.mLocation);
		if(isEnd && this.toCurrent<SavedOptions.GPS_TOLERANCE){
			//comments += "the end of route";
			this.endFlag = true;
		}
		return null;
	}
	public void redrawRoutes(GeoPoint start){
		Log.i(tag, "redraw route...");
		this.reDraw=true;
		osm.ro.redraw(start);
		cleanupAllonRoad();
		RouteTask task = new RouteTask(osm, osm.ro);
		task.execute();
		//osm.loc.passedNodes.clear();
	}
	public int isInStep(List<Polyline> lines, GeoPoint point){
		int ret=-1;
		for(int i=0;i<lines.size();i++){
			boolean on = PolyUtil.isLocationOnPath(point, lines.get(i).getPoints(), false,SavedOptions.GPS_TOLERANCE);
			if(on) return i;
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
	
	private void playHintSounds(int index, int dist) {
		boolean played = DataBus.isPlayed(index,dist);
		if(this.currNode!=null  && !played){ //&& this.toCurrent<SavedOptions.VOICE_DISTANCE
			DataBus.setPlayedId(index,dist);
			//BigDecimal bd = new BigDecimal(this.toCurrent).setScale(-2, BigDecimal.ROUND_HALF_UP);  //整百
			String display = MyPlayer.play(osm.act, this.currNode, dist);
			Toast.makeText(osm.act, "playing "+index+":"+dist+" :"+display, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(osm.act, "not playing "+index+":"+dist+" played="+played+", currNode="+currNode, Toast.LENGTH_LONG).show();
		}
	}

	private void cleanupAllonRoad() {
		osm.mks.removeAllRouteMarkers();
		osm.mks.removePrevPolyline();
		osm.loc.cleanupRoad();
		//osm.dv.closePopupNavi(); //backend thread cannot call UI thread
		this.cleanupRoad();
	}
	private void cleanupRoad() {
		toCurrent = 0;
		//toPrev = 0;
	}
	@Override
    protected void onPostExecute(String comments) {
		//if(this.isWorking()) return;
		if(currNode==null) return;
		if(this.reDraw){
			osm.dv.closePopupNavi();
			this.reDraw=false;
		}
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		BigDecimal bd = new BigDecimal(this.toCurrent).setScale(-2, BigDecimal.ROUND_HALF_UP);  //整百
		playHintSounds(osm.loc.onRoadIndex+1,bd.intValue());
		int resId = InfoWindow.getIconByManeuver(currNode.mManeuverType);
		String dist = "In "+this.toCurrent+"m "+Mode.getModeName();
		osm.dv.updateNaviInstruction(dist,resId);
		if(this.endFlag){
			cleanupAllonRoad();
			osm.dv.closePopupNavi();
			//confirm parking button
		}
		DataBus.getInstance().setHintPoint(this.currNode.mLocation);
		osm.mks.updateHintMarker();
		//bus.setFindingMyStep(false);
    }

	public int getDistance(GeoPoint start, GeoPoint end){
		float[] results = new float[1];
		Location.distanceBetween(start.getLatitude(), start.getLongitude(),
		                end.getLatitude(), end.getLongitude(), results);
		return Math.round(results[0]);
	}
	private boolean isWorking() {
		long pre = bus.getFindingMS();
		//Log.w(tag, "lap="+(System.currentTimeMillis()-pre));
		if((System.currentTimeMillis()-pre)<SavedOptions.FIND_DELAY_TIME){
			currNode=null;
			return true;
		}else{
			bus.setFindMyStepTime(System.currentTimeMillis());
			return false;
		}
	}
	private boolean isRedrawing() {
		long pre = bus.getRedrawTime();
		//Log.w(tag, "lap="+(System.currentTimeMillis()-pre));
		if((System.currentTimeMillis()-pre)<SavedOptions.REDRAW_DELAY_TIME){
			currNode=null;
			return true;
		}else{
			bus.setRedrawTime(System.currentTimeMillis());
			return false;
		}
	}
}
