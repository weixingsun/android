package cat.app.gmap;

import cat.app.gmap.listener.MenuItemClickListener;
import cat.app.gmap.model.SuggestPoint;
import cat.app.gmap.task.GoogleMapSearchByNameTask;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

//http://servicedata.net76.net/select.php
public class MainActivity extends FragmentActivity {

	private static final String TAG = "GMap.MainActivity";
	public GMap gMap = new GMap();
	public EditText inputAddress;
	public ListView listSuggestion;
    
	private ListView mDrawerListParent;
    private String[] mMainSettings;
    private DrawerLayout mDrawerLayout;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		gMap.init(this);
		showUI();
	}

	private void showUI() {
		setText();
		setButtons();
		setList();
		setDrawer();
	}

	private void setDrawer() {
		mMainSettings = getResources().getStringArray(R.array.menu_items);
		mDrawerListParent = (ListView) findViewById(R.id.left_drawer_parent);
		mDrawerListParent.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mMainSettings));
		mDrawerListParent.setOnItemClickListener(new MenuItemClickListener(this));
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		
		mDrawerLayout.closeDrawers();
		ImageView iv = (ImageView) findViewById(R.id.settingsIcon);
		iv.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        //Toast.makeText(MainActivity.this,"drawer is opening",Toast.LENGTH_LONG).show();
		    	Log.i(TAG, "drawer is opening");
				mDrawerLayout.openDrawer(Gravity.LEFT);
		    }
		});
	}

	private void setList() {
		this.listSuggestion = (ListView) findViewById(R.id.listSuggestion);
		this.listSuggestion.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SuggestPoint sp = gMap.suggestPoints.get(position);
				gMap.addMarker(sp);
				listSuggestion.setVisibility(View.INVISIBLE);
				gMap.move(sp.getLocation());
			}
		});
	}
	
	private void setText() {
		this.inputAddress = (EditText) findViewById(R.id.inputAddress);
		inputAddress.setTextColor(Color.BLACK);
		inputAddress.addTextChangedListener(new DelayedTextWatcher(1500) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				GoogleMapSearchByNameTask task = new GoogleMapSearchByNameTask(
						gMap, inputAddress.getText().toString());
				task.execute();
			}
		});
		inputAddress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) { // Enter
					GoogleMapSearchByNameTask task = new GoogleMapSearchByNameTask(
							gMap, inputAddress.getText().toString());
					task.execute();
					closeKeyBoard();
				}
				return false;
			}
		});
	}

	private void setButtons() {
	}
	private void closeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
	}
	
}
