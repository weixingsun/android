package cat.app.map.poi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.model.Tag;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.MapReadResult;
import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.tileprovider.MapTile;

import cat.app.maps.MathUtil;

import android.util.Log;

public class MapsForgePOI {
	private static final String TAG = MapsForgePOI.class.getSimpleName();
	MapDatabase mapDatabase;
	List<String> tags = new ArrayList<String>();//amenity=parking
	
	List<String> highwayTags = new ArrayList<String>();
	//amenity, building, highway(bus_stop,speed_camera),junction(roundabout), historic,leisure,shop,sport,tourism
	
	public MapsForgePOI(File file){
		this.mapDatabase = new MapDatabase();
		this.mapDatabase.openFile(file);
		initTags();
	}
	public MapsForgePOI(MapDatabase mapDatabase){
		this.mapDatabase = mapDatabase;
		initTags();
	}
	public void initTags(){
		if(tags.size()==0){
			//tags.add("amenity");  //amenity=parking
			//tags.add("building");
			//tags.add("highway");
			//tags.add("junction"); //junction=roundabout
			//tags.add("historic");
			//tags.add("leisure");
			//tags.add("shop");
			//tags.add("sport");
			//tags.add("tourism");
			//highwayTags.add("bus_stop");
			highwayTags.add("speed_camera");
		}
	}
	public List<PointOfInterest> getPOI(int x, int y, int zoomLevel){
		Tile tile = new Tile(x, y, (byte)zoomLevel);
		MapReadResult result = mapDatabase.readMapData(tile);
		List<PointOfInterest> POIs = result.pointOfInterests;
		List<PointOfInterest> filteredPOIs = filterPOI(POIs);
		//Log.w(TAG, "poi.size.all="+POIs.size()+",poi.size.filtered="+filteredPOIs.size());
		//printPOIs(filteredPOIs);
		POIs = null;
		return filteredPOIs;
	}
	public List<PointOfInterest> filterPOI(List<PointOfInterest> POI){
		List<PointOfInterest> l = new ArrayList<PointOfInterest>();
		for(PointOfInterest p:POI){
			//if(tags.contains(p.tags.get(0).key)){
				//if(!checkDuplicate(p,l))
				//	l.add(p);
			//}else 
				if(p.tags.get(0).key.equals("highway") && highwayTags.contains(p.tags.get(0).value)){
				//if(!checkDuplicate(p,l))
					l.add(p);
			}
		}
		return l;
	}
	public List<PointOfInterest> getPOI(MapTile mTile){
		return getPOI(mTile.getX(),mTile.getY(),mTile.getZoomLevel());
	}
	/*public boolean compare(PointOfInterest a, PointOfInterest b){
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
	}*/
	/*public int getPOIsize(int x, int y, int zoomLevel){
		int POIsize = getPOI(x,y,zoomLevel).size();
		Log.w(tag,"POIsize="+POIsize);
		return POIsize;
	}*/
}
