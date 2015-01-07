package cat.app.osmap;

import cat.app.maps.OSM;
import cat.app.osmap.ui.Drawer;

import android.app.Activity;
import android.os.Bundle;

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

}