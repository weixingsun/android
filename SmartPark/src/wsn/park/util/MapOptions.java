package wsn.park.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.maps.OSM;
import wsn.park.maps.vendor.OSMMapGoogleRenderer;
import wsn.park.maps.vendor.OSMMapMicrosoftRenderer;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class MapOptions {
	static OSM osm;
	static MapOptions opt;

	private MapOptions() {
	}

	public static MapOptions getInstance(OSM osm) {
		MapOptions.osm = osm;
		if (opt == null)
			opt = new MapOptions();
		return opt;
	}

	private static final String tag = MapOptions.class.getSimpleName();

	static String path = SavedOptions.sdcard + "/"
			+ SavedOptions.MAPSFORGE_FILE_PATH;
	//public static String MF_ROUTE_URL = "http://www.androidmaps.co.uk/maps/australia-oceania/new-zealand.zip";
	public static String MF_ROUTE_URL = "http://servicedata.vhostall.com/map/"; // nz.map
	public static final String URL_MAPSFORGE_WEB = "http://ftp-stud.hs-esslingen.de/pub/Mirrors/download.mapsforge.org/maps/";
	public static final String URL_MAPSFORGE_FTP = "ftp-stud.hs-esslingen.de";
	public static final String URL_MAPSFORGE_FTP_FOLDER = "/pub/Mirrors/download.mapsforge.org/maps/";

	public static final String MAP_OFFLINE = "Offline Map";
	public static final String MAP_MAPSFORGE = "MapsForge";
	public static final String MAP_GOOGLE_ROADMAP = "Google Roadmap";
	public static final String MAP_GOOGLE_SATELLITE = "Google Satellite";
	public static final String MAP_GOOGLE_TERRAIN = "Google Terrain";
	// public static final String MAP_YAHOO_ROADMAP = "Yahoo Roadmap";
	// public static final String MAP_YAHOO_SATELLITE = "Yahoo Satellite";
	public static final String MAP_MS_ROADMAP = "Microsoft Maps";
	public static final String MAP_MS_EARTH = "Microsoft Earth";
	public static final String MAP_MS_HYBRID = "Microsoft Hybrid";

	public static final String MAP_MAPNIK = "Mapnik";
	public static final String MAP_MAPQUESTOSM = "MapquestOSM";
	public static final String MAP_MAPQUEST_AERIAL = "MapquestAerial";
	public static final String MAP_CYCLEMAP = "CycleMap";
	public static final String MAP_PUBLIC_TRANSPORT_OSM = "OSMPublicTransport";

	public static LinkedHashMap<String, String> MAP_TILES = new LinkedHashMap<String, String>();
	static {
		MAP_TILES.put("Open Street Map", MAP_MAPQUESTOSM);
		MAP_TILES.put("OSM Satellite", MAP_MAPQUEST_AERIAL);
		MAP_TILES.put("OSM Bus", MAP_PUBLIC_TRANSPORT_OSM);
		MAP_TILES.put(MAP_OFFLINE, MAP_MAPSFORGE);
		MAP_TILES.put(MAP_MAPNIK, MAP_MAPNIK);
		MAP_TILES.put(MAP_GOOGLE_ROADMAP, MAP_GOOGLE_ROADMAP);
		MAP_TILES.put(MAP_GOOGLE_SATELLITE, MAP_GOOGLE_SATELLITE);
		MAP_TILES.put(MAP_GOOGLE_TERRAIN, MAP_GOOGLE_TERRAIN);
		MAP_TILES.put(MAP_MS_ROADMAP, MAP_MS_ROADMAP);
		MAP_TILES.put(MAP_MS_EARTH, MAP_MS_EARTH);
		MAP_TILES.put(MAP_MS_HYBRID, MAP_MS_HYBRID);
		// getMapsForgeMap(Activity act)
	}
	public static final int REQ_CODE_SPEECH_INPUT = 2;
	public static final int REQ_CODE_MOVE_INPUT = 3;

	/*
	 * Mapnik, CycleMap, OSMPublicTransport, MapquestOSM, MapquestAerial, Google
	 * Maps, Google Maps Satellite, Google Maps Terrain, Yahoo Maps, Yahoo Maps
	 * Satellite, Microsoft Maps, Microsoft Earth, Microsoft Hybrid
	 */
	public static MapTileProviderBase getTileProvider(String name){
		MapTileProviderBase provider = null;

		if (name!=null && name.equals(MapOptions.MAP_MAPSFORGE) ) {
			//Log.w(tag, "getTileProvider.name="+name+",activity="+osm.act);
			// MapsForge offline data need recreate a mapview
			provider = MapOptions.getForgeMapTileProvider(osm);
			if (provider == null) {
				osm.startDownloadActivity(getNeededMapFileShortName());
				return null;// no map file.
			}
		} else { // others refresh with tilesource
			provider = new MapTileProviderBasic(osm.act);
			if(name==null){
				provider.setTileSource(TileSourceFactory.getTileSource(MAP_MAPQUESTOSM));
			}else{
				provider.setTileSource(TileSourceFactory.getTileSource(name));
			}
		}
		return provider;
	}
	public static void switchTileProvider(String name) {
		osm.mapProvider = getTileProvider(name);
		osm.setMap(osm.mapProvider);
	}

	public static void changeTileProvider(String provider) {
		osm.refreshTileSource(provider);
	}

	public static void move() {
		if(osm.loc.myPos==null || osm.mks.myLocMarker ==null) return;
		GeoPoint gp = new GeoPoint(osm.loc.myPos);
		osm.mks.myLocMarker.setPosition(gp);
		move(gp);
	}

	public static void move(GeoPoint loc) {
		if(loc!=null)
			osm.move(loc);
	}

	public void initTileSources(Activity act) {
		CloudmadeUtil.retrieveCloudmadeKey(act.getApplicationContext());
		ArrayList<ITileSource> list = TileSourceFactory.getTileSources();
		final int size = list.size();
		TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(
				MapOptions.MAP_GOOGLE_ROADMAP, ResourceProxy.string.unknown, 0,
				20, 256, ".png", size, "http://mt0.google.com/vt/lyrs=m@127&"));
		TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(
				MapOptions.MAP_GOOGLE_SATELLITE, ResourceProxy.string.unknown,
				0, 20, 256, ".png", size + 1,
				"http://mt0.google.com/vt/lyrs=s@127,h@127&"));
		TileSourceFactory.addTileSource(new OSMMapGoogleRenderer(
				MapOptions.MAP_GOOGLE_TERRAIN, ResourceProxy.string.unknown, 0,
				20, 256, ".jpg", size + 2,
				"http://mt0.google.com/vt/lyrs=t@127,r@127&"));

		// TileSourceFactory.addTileSource(new
		// OSMMapYahooRenderer(MapOptions.MAP_YAHOO_ROADMAP,ResourceProxy.string.unknown,0,17,256,".jpg",size
		// + 3,"http://maps.yimg.com/hw/tile?"));
		// TileSourceFactory.addTileSource(new
		// OSMMapYahooRenderer(MapOptions.MAP_YAHOO_SATELLITE,ResourceProxy.string.unknown,0,17,256,".jpg",size
		// + 4,"http://maps.yimg.com/ae/ximg?"));

		TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(
				MapOptions.MAP_MS_ROADMAP, ResourceProxy.string.unknown, 0, 19,
				256, ".png", size + 5,
				"http://r0.ortho.tiles.virtualearth.net/tiles/r"));
		TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(
				MapOptions.MAP_MS_EARTH, ResourceProxy.string.unknown, 0, 19,
				256, ".jpg", size + 6,
				"http://a0.ortho.tiles.virtualearth.net/tiles/a"));
		TileSourceFactory.addTileSource(new OSMMapMicrosoftRenderer(
				MapOptions.MAP_MS_HYBRID, ResourceProxy.string.unknown, 0, 19,
				256, ".jpg", size + 7,
				"http://h0.ortho.tiles.virtualearth.net/tiles/h"));

		// TileSourceFactory.addTileSource(new
		// OSMMapGoogleRenderer("Google Maps Hybrid",
		// ResourceProxy.string.unknown, 0, 19, 256, ".jpg", size+8,
		// "http://mt0.google.com/vt/lyrs=m@127,s@127,h@127,r@127&"));
		// //mt0.google.com/vt/lyrs=h@159000000&hl=ru
		// TileSourceFactory.addTileSource(getTileSource("MapquestOSM"));
		// AssetsTileProvider atp = new AssetsTileProvider();
		// File str = OpenStreetMapTileProviderConstants.OSMDROID_PATH;
		// CacheManager cm = new CacheManager (null);
	}

	/*
	 * Google Maps: Road, Aerial, Hybrid, Terrain, Korea OpenStreetMap¡± Classic,
	 * Cycle, Transport, Osmarender, OpenPiste OVI-Nokia map:Classic, Satellite,
	 * Terrain (Locus is the only app I¡¯ve seen so far with these useful
	 * mapsets) Yahoo: Classic, Satellite Bing: Road, Hybrid, London A-Z, OS
	 * Maps OSM-regional: UMP-pcPL, Hike&Bike Freemap (Slovakia): Car, Turistic,
	 * Cyclo, Aerial Yandex (East Europe): Classic, Satellite Eniro (North
	 * Europe): Classic, Aerial, Nautical, Hybrid MyTopo (USA): 1:24K
	 * topographic maps Outdoor Active (Germany, Austria, South Tyrol) Statkaart
	 * (Norway): Topo, Raster Maps+ (Switzerland): Topography, Terrain NearMap
	 * (Australia): PhotoMap, StreetMap, Terrain
	 */
	public static MapTileProviderBase getForgeMapTileProvider(OSM osm) {
		String mapFileName = getMapFileName();
		Log.i(tag, "mapFile=" + mapFileName);

		if (mapFileName == null)
			return null;
		wsn.park.maps.vendor.MapsForgeTileProvider mfProvider = new wsn.park.maps.vendor.MapsForgeTileProvider(
				osm,new SimpleRegisterReceiver(osm.act), new File(mapFileName));
		return mfProvider;
		// GenericMapView genericMap = (GenericMapView)
		// act.findViewById(R.id.osmap);
		// genericMap.setTileProvider(mfProvider);
		// return genericMap.getMapView();
	}

	public static String getMapFileName() {
		String mapFileFullName = getNeededMapFileFullName();
		//Log.w(tag, "mapFileFullName==========="+mapFileFullName);
		File mapFile = new File(mapFileFullName);
		if (mapFile.exists()) {
			return mapFileFullName;
		}
		return null;
	}

	public static String getNeededMapFileFullName() {
		return SavedOptions.sdcard + "/" + SavedOptions.MAPSFORGE_FILE_PATH
				+ getNeededMapFileShortName();
	}

	public static String getNeededMapFileShortName() {
		return LOC.countryCode + SavedOptions.MAPSFORGE_FILE_EXT;
	}
}
