package cat.app.maps;

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
	public static final String MAPQUEST_API_KEY = "Fmjtd%7Cluu8296znl%2Crg%3Do5-9w1xdz";
	
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
	
	public static final int REQ_CODE_SPEECH_INPUT = 2;
	
	public static void changeTileProvider(String provider) {
		osm.refreshTileSource(provider);
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
}
