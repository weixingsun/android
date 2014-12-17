package cat.app.gmap.listener;

import java.util.ArrayList;
import java.util.List;

import cat.app.gmap.MainActivity;
import cat.app.gmap.R;
import cat.app.gmap.adapter.SubNavDrawerListAdapter;
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
			R.array.find_police_items,
			R.array.whats_hot_items};
	private static final String TAG = "GMap.DrawerItemClickListener";
	MainActivity activity;
	List<String> data=new ArrayList<String>();
	ListView lv;
	public MenuItemClickListener(MainActivity mainActivity) {
		this.activity = mainActivity;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		selectItem(view,position);
	}

	private void selectItem(View view, int position) {
		//Log.i(TAG, "view.Class="+view.getClass()+",Parent.Class="+view.getParent().getClass());
		//Log.i(TAG, "DrawerItem.Click="+position+", text"+tv.getText().toString());
		//TextView tv = (TextView)view;
		changeSubMenu(subMenus[position]);
		//childAdapter.notifyDataSetChanged();
		//ListView lv = (ListView)view.getParent();
	}

	private void changeSubMenu(int arrayId){
		String[] subSettings = activity.getResources().getStringArray(arrayId);
		ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(activity,R.layout.drawer_list_item, subSettings);
		ListView listChild = (ListView) activity.findViewById(R.id.left_drawer_child);
		listChild.setAdapter(childAdapter);
	}
}
