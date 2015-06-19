package wsn.park.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import wsn.park.R;
import wsn.park.maps.BaseActivity;
import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.model.SavedPlace;
import wsn.park.ui.PlaceAdapter.PlaceHolder;
import wsn.park.util.DbHelper;

public class MyPlacesActivity extends BaseActivity { 
	private String tag = MyPlacesActivity.class.getSimpleName();
	DbHelper dbHelper = DbHelper.getInstance();
	private ListView lv_star_place;
	private OSM osm = OSM.getInstance();
	private Drawer drawer = Drawer.INSTANCE();
	private TextView tv_home;
	private TextView tv_home_address;
	private TextView tv_work;
	private TextView tv_work_address;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myplaces);
        
        SavedPlace[] places = dbHelper.getSavedPlaceNames();
        tv_home = (TextView) findViewById(R.id.tv_home);
        tv_home.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Mode.setID(Mode.HOME);
				drawer.close();
				finish();
			}});
        tv_home_address = (TextView) findViewById(R.id.tv_home_address);
        tv_work = (TextView) findViewById(R.id.tv_work);
        tv_work.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Mode.setID(Mode.WORK);
				drawer.close();
				finish();
			}});
        tv_work_address = (TextView) findViewById(R.id.tv_work_address);
        lv_star_place = (ListView) findViewById(R.id.list_star_places);
        lv_star_place.setAdapter(new PlaceAdapter(this,R.layout.list_item, places));
        lv_star_place.setOnItemClickListener(new OnItemClickListener(){ 
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PlaceHolder ph = (PlaceHolder) view.getTag();
				//Log.w(tag, "place.name="+ph.place.getName());
				SavedPlace sp = (SavedPlace) ph.place;
				osm.mks.updateDestinationOverlay(sp);
				drawer.close();
				finish();
			}
        });
	}
}
