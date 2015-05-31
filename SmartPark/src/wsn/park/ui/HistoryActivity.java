package wsn.park.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import wsn.park.R;
import wsn.park.maps.BaseActivity;
import wsn.park.util.DbHelper;

public class HistoryActivity extends BaseActivity { 
	private String tag = HistoryActivity.class.getSimpleName();
	DbHelper dbHelper = DbHelper.getInstance();
	private ListView lv_history_place;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        
        String[] names = dbHelper.getHistoryPlaceNames();
        lv_history_place = (ListView) findViewById(R.id.list_star_places);
        lv_history_place.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, names));
        lv_history_place.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				
			}
        });
	}
}
