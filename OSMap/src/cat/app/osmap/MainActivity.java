package cat.app.osmap;

import java.util.List;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;
import cat.app.osmap.ui.Drawer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private static final String tag = MainActivity.class.getSimpleName();
	public OSM osm = new OSM();
	public LOC loc = new LOC();
	public Drawer dr = new Drawer();
	public Device dv = new Device();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        osm.onCreate(this);
        loc.init(this,osm);
        dr.init(this);
        dv.init(this);
    }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case MapOptions.REQ_CODE_SPEECH_INPUT: {
				if (resultCode == RESULT_OK && null != data) {
					List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					ArrayAdapter<String> adapter = new cat.app.osmap.ui.VoiceSuggestListAdapter(this,
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