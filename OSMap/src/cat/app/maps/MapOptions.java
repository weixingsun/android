package cat.app.maps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.cachemanager.CacheManager;
import org.osmdroid.bonuspack.mapsforge.GenericMapView;
import org.osmdroid.bonuspack.mapsforge.MapsForgeTileProvider;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;

import cat.app.maps.vendor.OSMMapGoogleRenderer;
import cat.app.maps.vendor.OSMMapMicrosoftRenderer;
import cat.app.osmap.R;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

public class MapOptions {
	static OSM osm;
	static MapOptions opt;
	private MapOptions(){
	}
	public static MapOptions getInstance(OSM osm) {
		MapOptions.osm = osm;
		if(opt==null) opt = new  MapOptions();
		return opt;
	}
	private static final String tag = MapOptions.class.getSimpleName();
	public static final String URL_MAPSFORGE_WEB = "http://ftp-stud.hs-esslingen.de/pub/Mirrors/download.mapsforge.org/maps/";
	public static final String URL_MAPSFORGE_FTP = "ftp-stud.hs-esslingen.de";
	public static final String URL_MAPSFORGE_FTP_FOLDER = "/pub/Mirrors/download.mapsforge.org/maps/";
	
	public static final String MAP_MAPSFORGE = "MapsForge";
	public static final String MAP_GOOGLE_ROADMAP = "Google Roadmap";
	public static final String MAP_GOOGLE_SATELLITE = "Google Satellite";
	public static final String MAP_GOOGLE_TERRAIN = "Google Terrain";
	//public static final String MAP_YAHOO_ROADMAP = "Yahoo Roadmap";
	//public static final String MAP_YAHOO_SATELLITE = "Yahoo Satellite";
	public static final String MAP_MS_ROADMAP = "Microsoft Maps";
	public static final String MAP_MS_EARTH = "Microsoft Earth";
	public static final String MAP_MS_HYBRID = "Microsoft Hybrid";
	
	public static final String MAP_MAPNIK = "Mapnik";
	public static final String MAP_MAPQUESTOSM = "MapquestOSM";
	public static final String MAP_MAPQUEST_AERIAL = "MapquestAerial";
	public static final String MAP_CYCLEMAP = "CycleMap";
	public static final String MAP_PUBLIC_TRANSPORT_OSM = "OSMPublicTransport";
	
	public static HashMap<String, String> MAP_TILES = new HashMap<String,String>();
	static{
		MAP_TILES.put(MAP_MAPSFORGE, MAP_MAPSFORGE);
		MAP_TILES.put(MAP_GOOGLE_ROADMAP, MAP_GOOGLE_ROADMAP);
		MAP_TILES.put(MAP_GOOGLE_SATELLITE, MAP_GOOGLE_SATELLITE);
		MAP_TILES.put(MAP_GOOGLE_TERRAIN, MAP_GOOGLE_TERRAIN);
		MAP_TILES.put(MAP_MS_ROADMAP, MAP_MS_ROADMAP);
		MAP_TILES.put(MAP_MS_EARTH, MAP_MS_EARTH);
		MAP_TILES.put(MAP_MS_HYBRID, MAP_MS_HYBRID);
		MAP_TILES.put(MAP_MAPNIK, MAP_MAPNIK);
		MAP_TILES.put("Open Street Map", MAP_MAPQUESTOSM);
		MAP_TILES.put("OSM Satellite", MAP_MAPQUEST_AERIAL);
		MAP_TILES.put("OSM Bus", MAP_PUBLIC_TRANSPORT_OSM);
		//getMapsForgeMap(Activity act)
	}
	public static final int REQ_CODE_SPEECH_INPUT = 2;
	public static final int REQ_CODE_MOVE_INPUT = 3;
	
	public static void changeTileProvider(String provider) {
		osm.refreshTileSource(provider);
	}
	public static void move() {
		osm.move();
		Log.i(tag, "moved to my location="+osm.loc.myPos);
	}
	public void initTileSources(Activity act){
		CloudmadeUtil.retrieveCloudmadeKey(act.getApplicationContext());
		ArrayList<ITileSource> list = TileSourceFactory.getTileSources();
        final int size = list.size();
        TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(MapOptions.MAP_GOOGLE_ROADMAP, ResourceProxy.string.unknown, 0, 20, 256, ".png",size, "http://mt0.google.com/vt/lyrs=m@127&"));
        TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(MapOptions.MAP_GOOGLE_SATELLITE, ResourceProxy.string.unknown, 0, 20, 256, ".png",size+1, "http://mt0.google.com/vt/lyrs=s@127,h@127&"));
        TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(MapOptions.MAP_GOOGLE_TERRAIN, ResourceProxy.string.unknown, 0, 20, 256, ".jpg",size+2, "http://mt0.google.com/vt/lyrs=t@127,r@127&"));
        
        //TileSourceFactory.addTileSource(new OSMMapYahooRenderer(MapOptions.MAP_YAHOO_ROADMAP,ResourceProxy.string.unknown,0,17,256,".jpg",size + 3,"http://maps.yimg.com/hw/tile?"));
        //TileSourceFactory.addTileSource(new OSMMapYahooRenderer(MapOptions.MAP_YAHOO_SATELLITE,ResourceProxy.string.unknown,0,17,256,".jpg",size + 4,"http://maps.yimg.com/ae/ximg?"));
        
        TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(MapOptions.MAP_MS_ROADMAP,ResourceProxy.string.unknown,0,19,256,".png",size + 5,"http://r0.ortho.tiles.virtualearth.net/tiles/r"));
        TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(MapOptions.MAP_MS_EARTH,ResourceProxy.string.unknown,0,19,256,".jpg",size + 6,"http://a0.ortho.tiles.virtualearth.net/tiles/a"));
        TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(MapOptions.MAP_MS_HYBRID,ResourceProxy.string.unknown,0,19,256,".jpg",size + 7,"http://h0.ortho.tiles.virtualearth.net/tiles/h"));
        
        //TileSourceFactory.addTileSource(new OSMMapGoogleRenderer("Google Maps Hybrid", ResourceProxy.string.unknown, 0, 19, 256, ".jpg", size+8, "http://mt0.google.com/vt/lyrs=m@127,s@127,h@127,r@127&"));  //mt0.google.com/vt/lyrs=h@159000000&hl=ru
        //TileSourceFactory.addTileSource(getTileSource("MapquestOSM"));
        //AssetsTileProvider atp = new AssetsTileProvider();
        //File str = OpenStreetMapTileProviderConstants.OSMDROID_PATH;
        //CacheManager cm = new CacheManager (null);
	}
	/*
    Google Maps: Road, Aerial, Hybrid, Terrain, Korea
    OpenStreetMap¡± Classic, Cycle, Transport, Osmarender, OpenPiste
    OVI-Nokia map:Classic, Satellite, Terrain (Locus is the only app I¡¯ve seen so far with these useful mapsets)
    Yahoo: Classic, Satellite
    Bing: Road, Hybrid, London A-Z, OS Maps
    OSM-regional: UMP-pcPL, Hike&Bike
    Freemap (Slovakia): Car, Turistic, Cyclo, Aerial
    Yandex (East Europe): Classic, Satellite
    Eniro (North Europe): Classic, Aerial, Nautical, Hybrid
    MyTopo (USA): 1:24K topographic maps
    Outdoor Active (Germany, Austria, South Tyrol)
    Statkaart (Norway): Topo, Raster
    Maps+ (Switzerland): Topography, Terrain
    NearMap (Australia): PhotoMap, StreetMap, Terrain
*/
	public static MapTileProviderBase getForgeMapTileProvider(Activity act){
		String path = Environment.getExternalStorageDirectory().getPath()+"/mapsforge/";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles == null)
			return null;
		File mapFile = null;
		for (File file:listOfFiles){
			if (file.isFile() && file.getName().endsWith(".map")){
				mapFile = file;
			}
		}
		if (mapFile == null)
			return null;
		MapsForgeTileProvider mfProvider = new MapsForgeTileProvider(new SimpleRegisterReceiver(act), mapFile);
		return mfProvider;
		//GenericMapView genericMap = (GenericMapView) act.findViewById(R.id.osmap);
		//genericMap.setTileProvider(mfProvider);
		//return genericMap.getMapView();
	}
}
