package cat.app.osmap.ui;

import cat.app.osmap.R;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class Drawer {

	private ListView mDrawerListParent;
    private String[] mMainSettings;
    private DrawerLayout mDrawerLayout;
    private Activity act;
    
	public void init(Activity act) {
		this.act=act;
		mMainSettings = act.getResources().getStringArray(R.array.menu_items);
		mDrawerListParent = (ListView) act.findViewById(R.id.left_drawer_parent);
		mDrawerListParent.setAdapter(new ArrayAdapter<String>(act,R.layout.drawer_list_item, mMainSettings));
		mDrawerListParent.setOnItemClickListener(new MenuItemClickListener(act));
		mDrawerLayout = (DrawerLayout)act.findViewById(R.id.drawer_layout);
		ImageView iv = (ImageView) act.findViewById(R.id.settings);
		iv.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				mDrawerLayout.openDrawer(Gravity.LEFT);	//Gravity.TOP / Gravity.BOTTOM
		    }
		});
	}
}
