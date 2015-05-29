package wsn.park.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wsn.park.R;
import wsn.park.maps.BaseActivity;
import wsn.park.util.DbHelper;
import wsn.park.util.MapOptions;
import wsn.park.util.SavedOptions;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
	Spinner sp_geo;
	Spinner sp_map;
	Spinner sp_navi;
	Spinner sp_by;
	Button btn_save;
	Button btn_cancel;
    ArrayAdapter<String> adapter;
	DbHelper dbHelper = DbHelper.getInstance();
	private String tag = SettingsActivity.class.getSimpleName();
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        sp_by=(Spinner)findViewById(R.id.sp_by);
        sp_geo=(Spinner)findViewById(R.id.sp_geo);
        sp_map=(Spinner)findViewById(R.id.sp_map);
        sp_navi=(Spinner)findViewById(R.id.sp_navi);
        configMapSpinner();
        configBySpinner();
        configGeoSpinner();
        configNaviSpinner();
        configBtn();
	}
	private void configBySpinner() {
		String prev_by = dbHelper.getSettings(SavedOptions.BY);
		configSpinner(sp_by,R.array.by_items,prev_by);
	}
    
	private void configMapSpinner() {
		String prev_map = dbHelper.getSettings(SavedOptions.MAP);
        configSpinner(sp_map,R.array.map_items,prev_map);
	}
	private void configGeoSpinner() {
		String prev_geo = dbHelper.getSettings(SavedOptions.GEO);
        configSpinner(sp_geo,R.array.geo_items,prev_geo);
	}
	private void configNaviSpinner() {
		String prev_navi = dbHelper.getSettings(SavedOptions.NAVI);
        configSpinner(sp_navi,R.array.navi_items,prev_navi);
	}

	private void configBtn() {
        onSave();
        onExit();
	}

	private void configSpinner(Spinner sp,int array,String selectedText){
        String[] items = getResources().getStringArray(array);
        List<String> list=Arrays.asList(items);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        int pos = list.indexOf(selectedText);
        //Log.w(tag,"pos="+pos+",text="+selectedText);
        sp.setSelection(pos);
	}
	/*private void onChange(Spinner sp, final int sp_id){
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }
			@Override
			public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
				switch(sp_id){
				case R.id.sp_by: break;
				case R.id.sp_geo: break;
				case R.id.sp_map: break;
				case R.id.sp_navi: break;
				default:	break;
				}
				show(view.toString());
			}
		});
	}*/
	private void onSave(){
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                save();
            }
        });
	}
	private void onExit(){
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                exit();
            }
        });
	}
	private void save() {
		String text_by = sp_by.getSelectedItem().toString();
		String text_geo = sp_geo.getSelectedItem().toString();
		String text_map = sp_map.getSelectedItem().toString();
		String text_navi = sp_navi.getSelectedItem().toString();
		//String prev_map = dbHelper.getSettings(SavedOptions.MAP);
		dbHelper.changeSettings(SavedOptions.MAP, text_map);
		dbHelper.changeSettings(SavedOptions.BY, text_by);
		dbHelper.changeSettings(SavedOptions.NAVI, text_navi);
		dbHelper.changeSettings(SavedOptions.GEO, text_geo);
		//if(!prev_map.equals(text_map))
		MapOptions.changeTileProvider(MapOptions.MAP_TILES.get(text_map));
		finish();
	}
	private void exit(){
		finish();
	}
	private void show(String value){
		Toast.makeText(getApplicationContext(), value, Toast.LENGTH_LONG).show();
	}
}
