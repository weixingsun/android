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

public class DrawerItemClickListener implements OnItemClickListener {

	private ArrayAdapter childAdapter;
	private static final String TAG = "GMap.DrawerItemClickListener";
	MainActivity activity;
	//List<String> data=new ArrayList<String>();
	ListView lv;
	public DrawerItemClickListener(MainActivity mainActivity, ArrayAdapter childAdapter) {
		this.activity = mainActivity;
		this.childAdapter = childAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		selectItem(view,position);
	}

	private void selectItem(View view, int position) {
		//Log.i(TAG, "view.Class="+view.getClass()+",Parent.Class="+view.getParent().getClass());
		TextView tv = (TextView)view;
		Log.i(TAG, "DrawerItem.Click="+position+", text"+tv.getText().toString());
		childAdapter.notifyDataSetChanged();
		//ListView lv = (ListView)view.getParent();
	}

}
