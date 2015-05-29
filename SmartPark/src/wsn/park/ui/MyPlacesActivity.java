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

public class MyPlacesActivity extends BaseActivity { 
	private String tag = MyPlacesActivity.class.getSimpleName();
	DbHelper dbHelper = DbHelper.getInstance();
	private ListView lv_star_place;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places);
        
        String[] names = dbHelper.getSavedPlaceNames();
        lv_star_place = (ListView) findViewById(R.id.list_star_places);
        lv_star_place.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, names));
        lv_star_place.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}
        });
	}
}
