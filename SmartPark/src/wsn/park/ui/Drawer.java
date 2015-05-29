package wsn.park.ui;

import wsn.park.MainActivity;
import wsn.park.R;
import wsn.park.util.SavedOptions;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class Drawer {

	private static final String tag = Drawer.class.getSimpleName();
	static Drawer drawer;
	private Drawer(){}
	public static Drawer INSTANCE(){
		if(drawer==null) {
			//Log.w(tag, "create drawer");
			drawer=new Drawer();
		}
		return drawer;
	}
	private ListView mDrawerListParent;
    private String[] mMainSettings;
    private DrawerLayout mDrawerLayout;
    private Activity act;
    
	public void init(MainActivity act) {
		this.act=act;
		//Log.w(tag, "init drawer");
		mMainSettings = act.getResources().getStringArray(R.array.menu_items);
		mDrawerListParent = (ListView) act.findViewById(R.id.left_drawer_parent);
		mDrawerListParent.setAdapter(new ArrayAdapter<String>(act,R.layout.drawer_list_item, mMainSettings));
		mDrawerListParent.setOnItemClickListener(new MenuItemClickListener(act));
		mDrawerLayout = (DrawerLayout)act.findViewById(R.id.drawer_layout);
		ImageView iv = (ImageView) act.findViewById(R.id.settings);
		iv.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				show();
		    }
		});
	}
	public void show(){
		mDrawerLayout.openDrawer(Gravity.LEFT);	//Gravity.TOP / Gravity.BOTTOM
	}

	public void show(String name){
		mDrawerLayout.openDrawer(Gravity.LEFT);	//Gravity.TOP / Gravity.BOTTOM
		ListView settings = (ListView) this.act.findViewById(R.id.left_drawer_parent);
		int order = SavedOptions.getMainMenuIndex(name, act);
		Log.i(tag, "order="+order);
		//settings.setItemChecked(order, true);
		//settings.getAdapter().getView(order, null, null).performClick();
		settings.performItemClick(settings.getAdapter().getView(order, null, null), order, settings.getAdapter().getItemId(order));
	}
}
