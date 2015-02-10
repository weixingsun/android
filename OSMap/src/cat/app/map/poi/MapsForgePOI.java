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

import android.util.Log;

public class MapsForgePOI extends POI {
	private static final String TAG = MapsForgePOI.class.getSimpleName();
	MapDatabase mapDatabase;
	List<String> tags = new ArrayList<String>();
	List<String> highwayTags = new ArrayList<String>();
	//amenity, building, highway(bus_stop,speed_camera),junction(roundabout), historic,leisure,shop,sport,tourism
	
	public MapsForgePOI(String fileName){
		File file = new File(fileName);
		this.mapDatabase = new MapDatabase();
		this.mapDatabase.openFile(file);
	}
	public MapsForgePOI(MapDatabase mapDatabase){
		this.mapDatabase = mapDatabase;
		if(tags.size()==0){
			tags.add("amenity");
			tags.add("building");
			//tags.add("highway");
			tags.add("junction");
			tags.add("historic");
			tags.add("leisure");
			tags.add("shop");
			tags.add("sport");
			tags.add("tourism");
			highwayTags.add("bus_stop");
			highwayTags.add("speed_camera");
		}
	}
	public List<PointOfInterest> getPOI(int x, int y, int zoomLevel){
		Tile tile = new Tile(x, y, (byte)zoomLevel);
		MapReadResult result = mapDatabase.readMapData(tile);
		List<PointOfInterest> POIs = result.pointOfInterests;
		return filterPOI(POIs);
	}
	public List<PointOfInterest> filterPOI(List<PointOfInterest> POI){
		List<PointOfInterest> l = new ArrayList<PointOfInterest>();
		for(PointOfInterest p:POI){
			if(tags.contains(p.tags.get(0).key)){
				l.add(p);
			}else if(p.tags.get(0).key.equals("highway")){
				if(highwayTags.contains(p.tags.get(0).value)){
					l.add(p);
				}
			}
		}
		return l;
	}
	public List<PointOfInterest> getPOI(MapTile mTile){
		return getPOI(mTile.getX(),mTile.getY(),mTile.getZoomLevel());
	}
	/*public int getPOIsize(int x, int y, int zoomLevel){
		int POIsize = getPOI(x,y,zoomLevel).size();
		Log.w(tag,"POIsize="+POIsize);
		return POIsize;
	}*/
}
