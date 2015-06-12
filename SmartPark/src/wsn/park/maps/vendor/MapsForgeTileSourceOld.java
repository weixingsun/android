package wsn.park.maps.vendor;

import java.io.File;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.MapReadResult;
import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.mapgenerator.JobParameters;
import org.mapsforge.android.maps.mapgenerator.MapGeneratorJob;
import org.mapsforge.android.maps.mapgenerator.databaserenderer.DatabaseRenderer;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.osmdroid.ResourceProxy;
import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;
import org.mapsforge.map.rendertheme.ExternalRenderTheme;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.rendertheme.XmlRenderTheme;
import org.mapsforge.core.model.Tile;

import wsn.park.map.poi.LoadPOITask;
import wsn.park.map.poi.MapsForgePOI;
import wsn.park.maps.OSM;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * http://www.salidasoftware.com/how-to-render-mapsforge-tiles-in-osmdroid/
 * @author Salida Software
 * Adapted from code found here : http://www.sieswerda.net/2012/08/15/upping-the-developer-friendliness/
 */
public class MapsForgeTileSourceOld extends BitmapTileSourceBase {

	private static final String tag = MapsForgeTileSourceOld.class.getSimpleName();
	private Bitmap bitmapOrigin = Bitmap.createBitmap(TILE_SIZE_PIXELS, TILE_SIZE_PIXELS, Bitmap.Config.RGB_565);
	protected File mapFile;
	//static Activity act;
	private static OSM osm;
	// Reasonable defaults ..
	public static final int MIN_ZOOM = 5;
	public static final int MAX_ZOOM = 20;
	public static final int TILE_SIZE_PIXELS = 256;

	private DatabaseRenderer renderer;
	public MapDatabase mapDatabase = new MapDatabase();
	private XmlRenderTheme jobTheme;
	private JobParameters jobParameters;
	private DebugSettings debugSettings;
	//public MapsForgePOI poi;

	// Required for the superclass
	public static final string resourceId = ResourceProxy.string.offline_mode;

	/**
	 * The reason this constructor is protected is because all parameters,
	 * except file should be determined from the archive file. Therefore a
	 * factory method is necessary.
	 * 
	 * @param minZoom
	 * @param maxZoom
	 * @param tileSizePixels
	 * @param file
	 */
	protected MapsForgeTileSourceOld(int minZoom, int maxZoom, int tileSizePixels, File file) {
		super("MapsForgeTiles", resourceId, minZoom, maxZoom, tileSizePixels, ".png");
		//Make sure the database can open the file
		FileOpenResult fileOpenResult = this.mapDatabase.openFile(file);
		if (fileOpenResult.isSuccess()) {
			mapFile = file;
		}
		else{
			mapFile = null;
		}
		osm.offlineMapFile = mapFile;
		Log.w(tag, "osm.offlineMapFile="+mapFile);
		//poi = new MapsForgePOI(this.mapDatabase);
		renderer = new DatabaseRenderer(mapDatabase);
		minZoom = renderer.getStartZoomLevel();
		maxZoom = renderer.getZoomLevelMax();
		tileSizePixels = mapDatabase.getMapFileInfo().tilePixelSize;
		Log.d("MAPSFORGE", "min="+minZoom+" max="+maxZoom+" tilesize="+tileSizePixels);

		//  For this to work I had to edit org.mapsforge.map.rendertheme.InternalRenderTheme.getRenderThemeAsStream()  to:
		//  return this.getClass().getResourceAsStream(this.absolutePath + this.file);
		jobTheme = InternalRenderTheme.OSMARENDER;    		
		jobParameters = new JobParameters(jobTheme, 1);
		debugSettings = new DebugSettings(false, false, false);
	}

	/**
	 * Creates a new MapsForgeTileSource from file.
	 * 
	 * Parameters minZoom and maxZoom are obtained from the
	 * database. If they cannot be obtained from the DB, the default values as
	 * defined by this class are used.
	 * 
	 * @param file
	 * @return the tile source
	 */
	public static MapsForgeTileSourceOld createFromFile(OSM osm,File file) {
		//TODO - set these based on .map file info
		int minZoomLevel = MIN_ZOOM;
		int maxZoomLevel = MAX_ZOOM;
		int tileSizePixels = TILE_SIZE_PIXELS;
		MapsForgeTileSourceOld.osm = osm;
		return new MapsForgeTileSourceOld(minZoomLevel, maxZoomLevel, tileSizePixels, file);
	}

	//The synchronized here is VERY important.  If missing, the mapDatabase read gets corrupted by multiple threads reading the file at once.
	public synchronized Drawable renderTile(MapTile pTile) {

		Tile tile = new Tile((long)pTile.getX(), (long)pTile.getY(), (byte)pTile.getZoomLevel());

		//osm.mks.pois.addAll(poi.getPOI(pTile));
		//LoadPOITask task = new LoadPOITask(osm, poi, pTile);
		//task.execute();
		///////////////////////////////////////////////////////////////////////////
		//Create a bitmap to draw on
		Bitmap bitmap = this.bitmapOrigin.copy(Bitmap.Config.RGB_565, true);
		try{
			MapGeneratorJob mapGeneratorJob = new MapGeneratorJob(tile, mapFile, jobParameters, debugSettings);
			renderer.executeJob(mapGeneratorJob, bitmap);
		}catch(Exception ex){
			//Make the bad tile easy to spot
			bitmap.eraseColor(Color.YELLOW);
		}
		Drawable d = new BitmapDrawable(osm.act.getResources(),bitmap);
		return d;
	}

}