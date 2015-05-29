package wsn.park;

import java.util.List;

import wsn.park.maps.OSM;
import wsn.park.ui.Drawer;
import wsn.park.util.MapOptions;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private static final String tag = MainActivity.class.getSimpleName();
	public OSM osm = new OSM();
	public Drawer dr = Drawer.INSTANCE();
	/*
		onCreate - onDestroy
		onResume - onPause
		onStart  - onStop
	 * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        dr.init(this);
        osm.init(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //osm.loc.wifi.register();
    }
    @Override
    protected void onPause() {
        super.onPause();
       // osm.loc.wifi.unregister();
    }

    @Override
    protected void onDestroy() {
        osm.loc.wifi.unregister();
        super.onDestroy();
    }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case MapOptions.REQ_CODE_SPEECH_INPUT: {
				if (resultCode == RESULT_OK && null != data) {
					List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					ArrayAdapter<String> adapter = new wsn.park.ui.VoiceSuggestListAdapter(this,
					        android.R.layout.simple_list_item_1, result);
					//new CustomListAdapter(YourActivity.this , R.layout.custom_list , mList);
					ListView listVoice = (ListView) this.findViewById(R.id.listVoiceSuggestion);
					listVoice.setAdapter(adapter);
					listVoice.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
    }
}