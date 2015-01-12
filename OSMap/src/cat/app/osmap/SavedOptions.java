package cat.app.osmap;

import java.util.LinkedHashMap;

import android.os.Environment;
import android.util.Log;

import cat.app.maps.MapOptions;
import cat.app.navi.GeoOptions;
import cat.app.navi.RouteOptions;

public class SavedOptions {
	private static final String tag = SavedOptions.class.getSimpleName();
	private static String sdcard = Environment.getExternalStorageDirectory().getPath();
	public static String MAPSFORGE_FILE_PATH = sdcard +"/osmdroid/maps/";	//	/sdcard/osmdroid/maps/nz.map
	public static String GH_ROUTE_DATA_PATH = sdcard + "/osmdroid/routes/" + "nz/";	//	/sdcard/osmdroid/routes/nz.zip
	
	//default=Open Street Map
	public static String selectedMap = MapOptions.MAP_MAPQUESTOSM;
	public static String selectedTravelMode = "Fast";
	public static String routingProvider = RouteOptions.OSM;		//navigation
	public static String geocodingProvider = routingProvider;		//navigation
	private static String selectedOnRoad = "Police";
	
	public static int getIndex(String main,String sub){
		LinkedHashMap<String, String> map=null;
		switch(main){
		case "Maps": map = MapOptions.MAP_TILES;
				break;
		case "Travel": map = RouteOptions.MAPQUEST_TRAVEL_MODES;
				break;
		case "Navigate": map = RouteOptions.ROUTERS;   //GeoOptions.GEO_CODERS;
				break;
		}
		if(map==null) return -1;
		return getIndexFromLinkedMap(sub,map);
	}
	
	private static int getIndexFromLinkedMap(String menuText,
			LinkedHashMap<String, String> map) {
		int i=0;
		for(String s:map.keySet()){
			if(s.equals(menuText)) return i;
			else{i++;}
		}
		return 0;
	}
	public static String getSubsettingsSelectedMenuName(String settingsName){
		String subsettingsSelectedName = null;
		switch(settingsName){
		case "Maps": subsettingsSelectedName = SavedOptions.selectedMap;
				break;
		case "Travel": subsettingsSelectedName = SavedOptions.selectedTravelMode;
				break;
		case "Navigate": subsettingsSelectedName = SavedOptions.routingProvider;
		break;
		case "On Road": subsettingsSelectedName = SavedOptions.selectedOnRoad ;
		break;
		}
		//Log.i(tag, "getSubsettingsSelectedMenuName="+settingsName);
		return subsettingsSelectedName;
	}
}
