package wsn.park.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import wsn.park.R;
import wsn.park.maps.BaseActivity;
import wsn.park.maps.OSM;
import wsn.park.model.SavedPlace;
import wsn.park.ui.PlaceAdapter.PlaceHolder;
import wsn.park.util.DbHelper;

public class HistoryActivity extends BaseActivity { 
	private String tag = HistoryActivity.class.getSimpleName();
	DbHelper dbHelper = DbHelper.getInstance();
	private ListView lv_history_place;
	private OSM osm = OSM.getInstance();
	private Drawer drawer = Drawer.INSTANCE(); 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        SavedPlace[] places = dbHelper.getHistoryPlaceNames();
        lv_history_place = (ListView) findViewById(R.id.list_history_places);
        //Log.w(tag, "names=("+names[0]+"),lv_history_place="+lv_history_place);
        //String[] ns = {"test","test","test"};
        lv_history_place.setAdapter(new PlaceAdapter(this,R.layout.list_item, places ));
        lv_history_place.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PlaceHolder ph = (PlaceHolder) view.getTag();
				//Log.w(tag, "place.name="+ph.place.getName());
				//osm.mks.updateRouteMarker(ph.place);
				SavedPlace sp = (SavedPlace) ph.place;
				osm.mks.updateTargetMarker(sp);
				osm.dv.openPlacePopup(sp);
				drawer.close();
				finish();
			}
        });
	}
}