package cat.app.osmap;

import cat.app.maps.OSM;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	private static final String tag = MainActivity.class.getSimpleName();
	OSM osm = new OSM();
	LOC loc = new LOC();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        osm.onCreate(this);
        loc.init(this,osm);
    }

    
}
