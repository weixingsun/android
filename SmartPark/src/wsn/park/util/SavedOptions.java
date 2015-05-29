package wsn.park.util;

import java.util.LinkedHashMap;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import wsn.park.R;

public class SavedOptions {
	private static final String tag = SavedOptions.class.getSimpleName();
	public static final int GPS_TOLERANCE = 30;
	public static String sdcard = Environment.getExternalStorageDirectory().getPath();
	public static String MAPSFORGE_FILE_URL = "http://servicedata.vhostall.com/map/"; // + nz.map
	public static String GH_ROUTE_DATA_URL = "http://servicedata.vhostall.com/route/"; // + nz.zip
	public static String MAPSFORGE_FILE_PATH = "osmdroid/maps/";	//	/sdcard/osmdroid/maps/nz.map
	public static String GH_ROUTE_DATA_PATH = "osmdroid/routes/" ;	//	/sdcard/osmdroid/routes/nz.zip
	public static String HINT_FILE_PATH = sdcard + "/osmdroid/hints/";
	public static String MAPSFORGE_FILE_EXT = ".map";
	public static String GH_ROUTE_DATA_NAME = "edges";  //geometry/locationIndex/names/nodes/properties
	public static String GH_ROUTE_DATA_ZIP_EXT = ".zip";
	//default=Open Street Map
	public static String selectedBy;
	public static String selectedMap;
	public static String selectedGeo;
	public static String selectedNavi;
	//public static String selectedCountry = "China";
	//public static String geocodingProvider = selectedNavi;		//navigation
	//private static String selectedOnRoad = "Police";

	public static final String BY   = "Maps";
	public static final String MAP  = "Travel";
	public static final String GEO  = "Geocoder";
	public static final String NAVI = "Navigate";
	
	public static final String MYPLACES = "My Places";
	public static final String HISTORY = "History";
	public static final String PARKING = "Parking";
	public static final String SETTINGS = "Settings";
	
	//public static String countryCode = null;
	/*public static LinkedHashMap<String, String> COUNTRIES = new LinkedHashMap<String,String>();

	static{
		COUNTRIES.put("China", "cn");
		COUNTRIES.put("New Zealand", "nz");
	}
	public void init(Activity activity){
		String[] countries = activity.getResources().getStringArray(R.array.country_items);
		String[] codes = activity.getResources().getStringArray(R.array.country_code_items);
		for(int i=0;i<countries.length;i++){
			COUNTRIES.put(countries[i], codes[i]);
		}
		String[] mainMenu = activity.getResources().getStringArray(R.array.menu_items);
		
	}
	public static int getMainMenuIndex(String main,Activity act){
		String[] mainMenu = act.getResources().getStringArray(R.array.menu_items);
		LinkedHashMap<String, String> map=new LinkedHashMap<String, String>();
		for(String s:mainMenu){
			map.put(s, s);
		}
		return getIndexFromLinkedMap(main,map);
	}
	public static int getIndex(String main,String sub){
		LinkedHashMap<String, String> map=null;
		switch(main){
		case "Maps": map = MapOptions.MAP_TILES;
				break;
		case "Travel": map = RouteOptions.MAPQUEST_TRAVEL_MODES;
				break;
		case "Navigate": map = RouteOptions.ROUTERS;   //GeoOptions.GEO_CODERS;
				break;
		case "Country": map = SavedOptions.COUNTRIES;   //GeoOptions.GEO_CODERS;
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
		case "Travel": subsettingsSelectedName = SavedOptions.selectedBy;
				break;
		case "Navigate": subsettingsSelectedName = SavedOptions.selectedNavi;
				break;
		case "On Road": subsettingsSelectedName = SavedOptions.selectedOnRoad ;
				break;
		}
		//Log.i(tag, "getSubsettingsSelectedMenuName="+settingsName);
		return subsettingsSelectedName;
	}*/
}
