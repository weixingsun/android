package wsn.park.navi.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import wsn.park.maps.OSM;
import wsn.park.model.DataBus;
import wsn.park.navi.GraphHopperOfflineRoadManager;
import wsn.park.util.APIOptions;
import wsn.park.util.GeoOptions;
import wsn.park.util.MapOptions;
import wsn.park.util.MathUtil;
import wsn.park.util.RouteOptions;
import wsn.park.util.RuntimeOptions;
import wsn.park.util.SavedOptions;


import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class RouteTask extends AsyncTask<GeoPoint, String, Polyline>{

	private static final String tag = RouteTask.class.getSimpleName();
	RoadManager roadManager;
	OSM osm;
	Road road;
	RouteOptions ro;
	public RouteTask(OSM map , RouteOptions ro) {
		super();
		this.osm = map;
		this.ro = ro;
	}

	@Override
	protected Polyline doInBackground(GeoPoint... params) {
		if( !RuntimeOptions.getInstance(osm.act).isNetworkAvailable()
				&& !SavedOptions.selectedNavi.equals(RouteOptions.OFFLINE) ){
			Log.w(tag, "provider="+SavedOptions.selectedNavi);
			return null;
		}
		roadManager = Routers.getRoadManager(SavedOptions.selectedNavi);
		if(roadManager==null) return null;
		
		try{
			road = roadManager.getRoad(ro.list);
		}catch(IllegalStateException e){
			return null;
		}
		if(road==null || road.mNodes==null) return null;
		osm.loc.road = road;
		osm.polyline = RoadManager.buildRoadOverlay(road, osm.act);
		osm.polyline.setWidth(10);
		osm.polyline.setColor(RouteOptions.getColor());
		/*if(roadManager.getEndAddress()!=null){
			osm.startAddr=roadManager.getStartAddress();
			osm.endAddr=roadManager.getEndAddress();
		}*/
		osm.lines=getSubPolylines(osm.polyline,road);
		return osm.polyline;
	}
	private List<Polyline> getSubPolylines(Polyline wholeLine,Road road){
		int endIndex = 1;
		int pointStartIndex = 0;
		//List<GeoPoint> wholeList = new CopyOnWriteArrayList<GeoPoint>(wholeLine.getPoints());
		//GeoPoint start = road.mNodes.get(roadIndex).mLocation;
		List<GeoPoint> wholeList = wholeLine.getPoints();
		GeoPoint end = road.mNodes.get(endIndex).mLocation;
		List<Polyline> list = new ArrayList<Polyline>();
		for(int i=0;i<wholeList.size();i++) {
			GeoPoint p = wholeList.get(i);
			if(MathUtil.compare(p, end)){//==end
				List<GeoPoint> subList = wholeList.subList(pointStartIndex, i); //start ~ i-1, subList still in wholeList 
				List<GeoPoint> newSubList = new ArrayList<GeoPoint>(subList);
				newSubList.add(wholeList.get(i));
				Polyline subLine = new Polyline(osm.act);
				subLine.setPoints(newSubList);
				list.add(subLine);
				//Log.i(tag, endIndex+"/"+(road.mNodes.size()-1)+" in points: "+i+"/"+(wholeList.size()-1));
				endIndex++;
				if(endIndex>road.mNodes.size()-1) continue;
				end = road.mNodes.get(endIndex).mLocation;
				pointStartIndex=i;
			}
		}
		print(wholeList,list);
		return list;
	}
	private void print(List<GeoPoint> whole, List<Polyline> list) {
		Log.i(tag, "all points="+whole.size()+",devided("+list.size()+")");
//		for(Polyline line:list){
//			List<GeoPoint> l  = line.getPoints();
//			//Log.i(tag, "("+l.size()+")");
//		}
	}

	@Override
    protected void onPostExecute(Polyline pl) {
		if(pl == null && SavedOptions.selectedNavi.equals(RouteOptions.OFFLINE)){
			//Toast.makeText(osm.act, "Please download new version routes files ", Toast.LENGTH_LONG).show();
			osm.startDownloadActivity(RouteOptions.getRouteDownloadFileShortName());
			return;
		}
		//osm.dv.closeNaviPopup();
		osm.mks.removeAllRouteMarkers();
		osm.mks.removePrevPolyline();
		if(road==null) return;
		osm.mks.addPolyline(pl);
		osm.mks.drawStepsPoint(road);
		osm.loc.passedNodes.clear();
		GeoPoint end = road.mNodes.get(road.mNodes.size()-1).mLocation;
		DataBus.getInstance().setEndPoint(end);
    }

}
