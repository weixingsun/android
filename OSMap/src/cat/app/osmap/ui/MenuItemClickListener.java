package cat.app.osmap.ui;

import java.util.ArrayList;
import java.util.List;

import cat.app.osmap.LOC;
import cat.app.osmap.R;
import cat.app.osmap.util.DbHelper;
import cat.app.osmap.util.GeoOptions;
import cat.app.osmap.util.MapOptions;
import cat.app.osmap.util.RouteOptions;
import cat.app.osmap.util.SavedOptions;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MenuItemClickListener implements OnItemClickListener {
	DbHelper dbHelper;
	String selectedMenu;
	int[] subMenus = {
			R.array.map_type_items,
			R.array.nav_type_items,
			R.array.geocoder_type_items,
			R.array.country_items,
			R.array.parking_items,
			R.array.whats_hot_items
			};
	private static final String TAG = MenuItemClickListener.class.getSimpleName();
	Activity activity;
	List<String> data=new ArrayList<String>();
	ListView lv;
	public MenuItemClickListener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TextView tv = (TextView) view;
		String name = tv.getText().toString();

		dbHelper = DbHelper.getInstance();
		selectedMenu = dbHelper.getSettings(name);
		changeSubMenu(subMenus[position],name);
		//Log.i(TAG, "menu="+name);
	}

	private void changeSubMenu(int arrayId, String settingsName){  //Maps
		String[] subSettingsStr = activity.getResources().getStringArray(arrayId);
		ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(activity,R.layout.drawer_list_item, subSettingsStr);
		ListView subSettings = (ListView) activity.findViewById(R.id.left_drawer_child);
		subSettings.setAdapter(childAdapter);
		//String selectedSubsettingsName=null;
		if(selectedMenu==null){
			selectedMenu = SavedOptions.getSubsettingsSelectedMenuName(settingsName);
		}
		if(selectedMenu==null) return;
		int order = SavedOptions.getIndex(settingsName,selectedMenu);
		if(order<0) return;
		subSettings.setItemChecked(order, true);
		subSettings.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView)view;
				String name=tv.getText().toString();
				if(MapOptions.MAP_TILES.containsKey(name)){
					MapOptions.changeTileProvider(MapOptions.MAP_TILES.get(name));
					SavedOptions.selectedMap = name;
					if(name.equals(MapOptions.MAP_OFFLINE)){
						if(MapOptions.getMapFileName()!=null){
							dbHelper.changeSettings("Maps", name);
						}
					}else{
						dbHelper.changeSettings("Maps", name);
					}
					//Log.i(TAG, "MapTileProvider="+name);
				}else if(RouteOptions.MAPQUEST_TRAVEL_MODES.containsKey(name)){
					RouteOptions.changeTravelMode(name);
					SavedOptions.selectedTravelMode = name;
					dbHelper.changeSettings("Travel", name);
					//Log.i(TAG, "TravelMode="+name);
					//////////////////MyPlayer.play(activity, 0, 2);
				}else if(GeoOptions.GEO_CODERS.containsKey(name)){
					GeoOptions.changeGeocoder(GeoOptions.GEO_CODERS.get(name));
					RouteOptions.changeRouteProvider(RouteOptions.ROUTERS.get(name));
					String geoProvider = GeoOptions.GEO_CODERS.get(name);
					String routeProvider = RouteOptions.ROUTERS.get(name);
					SavedOptions.geocodingProvider = geoProvider;
					SavedOptions.routingProvider = routeProvider;

					if(name.equals(RouteOptions.OFFLINE)){
						if(RouteOptions.getRouteFileFullName()!=null){
							dbHelper.changeSettings("Navigate", name);
						}
					}else{
						dbHelper.changeSettings("Navigate", name);
					}
					//Log.i(TAG, "Route="+name);
				}else if(SavedOptions.COUNTRIES.containsKey(name)){
					SavedOptions.selectedCountry = name;
					String countryCode = SavedOptions.COUNTRIES.get(name);
					//Log.i(TAG, "countryCode="+countryCode);
					dbHelper.changeSettings("Country", name);
					dbHelper.updateCountryCode(countryCode);
					//judge if the country's map/route file exist?
					LOC.countryCode = countryCode;
				}
				//Log.i(TAG, "MenuClicked="+name);
			}
		});
	}
}
