package cat.app.map.poi;

import java.io.File;
import java.util.List;

import org.mapsforge.core.model.Tile;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.MapReadResult;
import org.mapsforge.map.reader.PointOfInterest;
import org.osmdroid.tileprovider.MapTile;

import android.util.Log;

public class MapsForgePOI extends POI {
	private static final String tag = MapsForgePOI.class.getSimpleName();
	MapDatabase mapDatabase;
	
	public MapsForgePOI(String fileName){
		File file = new File(fileName);
		this.mapDatabase = new MapDatabase();
		this.mapDatabase.openFile(file);
	}
	public MapsForgePOI(MapDatabase mapDatabase){
		this.mapDatabase = mapDatabase;
	}
	public List<PointOfInterest> getPOI(int x, int y, int zoomLevel){
		Tile tile = new Tile(x, y, (byte)zoomLevel);
		MapReadResult result = mapDatabase.readMapData(tile);
		List<PointOfInterest> POI = result.pointOfInterests;
		return POI;
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
