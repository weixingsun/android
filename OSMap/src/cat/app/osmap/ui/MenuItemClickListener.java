package cat.app.osmap.ui;

import java.util.ArrayList;
import java.util.List;

import cat.app.maps.MapOptions;
import cat.app.navi.RouteOptions;
import cat.app.osmap.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MenuItemClickListener implements OnItemClickListener {

	int[] subMenus = {
			R.array.nav_type_items,
			R.array.map_type_items,
			R.array.find_road_items,
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
		changeSubMenu(subMenus[position]);
	}

	private void changeSubMenu(int arrayId){
		String[] subSettings = activity.getResources().getStringArray(arrayId);
		ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(activity,R.layout.drawer_list_item, subSettings);
		ListView listChild = (ListView) activity.findViewById(R.id.left_drawer_child);
		listChild.setAdapter(childAdapter);
		listChild.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView tv = (TextView)view;
				String name=tv.getText().toString();
				if(MapOptions.MAP_TILES.containsKey(name)){
					MapOptions.changeTileProvider(MapOptions.MAP_TILES.get(name));
				}else if(RouteOptions.TRAVEL_MODES.containsKey(name)){
					RouteOptions.changeTravelMode(RouteOptions.TRAVEL_MODES.get(name));
				}
			}
		});
	}
}
