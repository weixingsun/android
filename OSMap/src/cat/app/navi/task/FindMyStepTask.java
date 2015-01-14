package cat.app.navi.task;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;

import cat.app.audio.MyPlayer;
import cat.app.maps.OSM;
import cat.app.osmap.SavedOptions;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FindMyStepTask extends AsyncTask<GeoPoint, Void, Float> {
	OSM osm;
	GeoPoint myGP;
	//Road road;
	RoadNode node;
	private Marker marker;
	private int toCurrent = 0;
	private int toPrev = 0;
	private static final String TAG = FindMyStepTask.class.getSimpleName();
	//(new FindMyStepTask(osm, point)).execute();
	public FindMyStepTask(OSM osm, GeoPoint point) {
		super();
		this.osm = osm;
		this.myGP=point;
	}
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
	@Override
	protected Float doInBackground(GeoPoint... params) {
		if(osm.loc.road==null || osm.loc.road.mNodes.size()<1){return null;}
		boolean onRoad=isInStep(osm.loc.road, myGP);	//如果误差超过30米，会认为不在线路上，重新寻路
		if(onRoad){
			osm.loc.onRoad = onRoad;
		}
		findHintMarkers();
		return null;
	}

	private void playHintSounds(int index) {
		if(index<osm.loc.road.mNodes.size()-1){
			RoadNode nextNode = osm.loc.road.mNodes.get(osm.loc.currIndex+1);
			if(this.toCurrent>SavedOptions.GPS_TOLERANCE && this.toPrev>SavedOptions.GPS_TOLERANCE){
				if(this.toCurrent<200){
					Log.i(TAG, "in 200 "+nextNode.mInstructions);
				}else{
					Log.i(TAG, "out of intersection: in "+nextNode.mLength+" "+nextNode.mInstructions);
				}
			}
		}
		//MyPlayer.play(osm.act,this.node,this.toCurrent); //node.mManeuverType + roadName
	}
	private void findHintMarkers() {
		if(marker!=null){
			for(int i=0;i<osm.loc.road.mNodes.size();i++){
				RoadNode node = osm.loc.road.mNodes.get(i);
				if(osm.loc.passedNodes.contains(node)) continue;
				if(i>0){
					RoadNode prevNode = osm.loc.road.mNodes.get(i-1);
					List<GeoPoint> list = new ArrayList<GeoPoint>();
					list.add(node.mLocation);
					list.add(prevNode.mLocation);
					if(!isInStep(list, marker.getPosition())){
						continue;
					}
				}
				this.toCurrent = getDistance(marker.getPosition(),node.mLocation);
				if(i==0) this.toPrev = 0 ;
				else this.toPrev = getDistance(marker.getPosition(),osm.loc.road.mNodes.get(i-1).mLocation);
				Log.i(TAG, "toCurrent="+this.toCurrent+":toPrev="+this.toPrev);
				if(this.toCurrent<SavedOptions.GPS_TOLERANCE){
					osm.loc.passedNodes.add(node);
					this.node=node;
					osm.loc.currIndex=i;
					Log.i(TAG, "index="+i);
				}
				return;
			}
		}
	}
	@Override
    protected void onPostExecute(Float useless) {
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		if(toCurrent>0){
			//Toast.makeText(osm.act, "dist="+dist, Toast.LENGTH_SHORT).show();
			if(marker!=null){
				playHintSounds(osm.loc.currIndex);
				String title = "node "+(osm.loc.currIndex+1)+"/"+osm.loc.road.mNodes.size()+":("+osm.loc.passedNodes.size()+")toCurr="+toCurrent+",toPrev="+this.toPrev;
				marker.setTitle(title);
				marker.showInfoWindow();
			}
		}
    }
	public int getDistance(GeoPoint start, GeoPoint end){
		float[] results = new float[1];
		Location.distanceBetween(start.getLatitude(), start.getLongitude(),
		                end.getLatitude(), end.getLongitude(), results);
		return Math.round(results[0]);
	}
	private int getRoadIndex(RoadNode node){
		int i= 0;
		if(node!=null){
			i = osm.loc.road.mNodes.indexOf(node);
		}else{
			i = osm.loc.passedNodes.size()-1;
		}
		return i;
	}
}
