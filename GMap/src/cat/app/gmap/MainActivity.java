package cat.app.gmap;

import java.util.List;

import com.google.android.gms.maps.model.Marker;

import cat.app.gmap.adapter.VoiceSuggestListAdapter;
import cat.app.gmap.listener.MenuItemClickListener;
import cat.app.gmap.listener.Voice;
import cat.app.gmap.model.MarkerPoint;
import cat.app.gmap.model.SuggestPoint;
import cat.app.gmap.task.GoogleSearchByAddressNameTask;
import cat.app.gmap.task.Player;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

//http://servicedata.net76.net/select.php
public class MainActivity extends FragmentActivity {

	private final String TAG = "GMap.MainActivity";
	public GMap gMap = new GMap();
	public ListView listSuggestion;
	public ListView listVoice ;
	public EditText inputAddress;
    public ImageView iconTravelMode;
    public TextView pointBrief;
    public TextView pointDetail;
    public TextView markerid;
    public ImageView iconRouteDelete;
	private ImageView iconPolice;
	private ImageView iconCCTV;
	private ImageView iconMedical;
	private ListView mDrawerListParent;
    private String[] mMainSettings;
    private DrawerLayout mDrawerLayout;
    PopupWindow popup;
	DisplayMetrics  dm;
	String mode;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		gMap.init(this);
		showUI();
	}
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	void showUI() {
		setText();
		setPopup();
		setList();
		setDrawer();
		setWindow();
		setImage();
	}

	private void setImage() {
		ImageView voiceInput = (ImageView) findViewById(R.id.voiceInput);
		voiceInput.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Voice.promptSpeechInput(MainActivity.this);
		    }
		});
	}
	private void setWindow() {
	     dm = new DisplayMetrics();
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
	}
	
	private void setDrawer() {
		mMainSettings = getResources().getStringArray(R.array.menu_items);
		mDrawerListParent = (ListView) findViewById(R.id.left_drawer_parent);
		mDrawerListParent.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mMainSettings));
		mDrawerListParent.setOnItemClickListener(new MenuItemClickListener(this));
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		ImageView iv = (ImageView) findViewById(R.id.settingsIcon);
		iv.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
				mDrawerLayout.openDrawer(Gravity.LEFT);	//Gravity.TOP / Gravity.BOTTOM
		    }
		});
	}

	private void setList() {
		this.listVoice = (ListView) findViewById(R.id.listVoiceSuggestion);
		this.listVoice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)view;
				inputAddress.setText(tv.getText());
				listVoice.setVisibility(View.INVISIBLE);
			}
		});
		this.listSuggestion = (ListView) findViewById(R.id.listSuggestion);
		this.listSuggestion.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SuggestPoint sp = gMap.suggestPoints.get(position);
				gMap.addRouteMarker(sp);
				listSuggestion.setVisibility(View.INVISIBLE);
				gMap.move(sp.getLatLng());
			}
		});
	}

	private void setText() {
		inputAddress = (EditText) findViewById(R.id.inputAddress);
		inputAddress.setTextColor(Color.BLACK);
		inputAddress.addTextChangedListener(new DelayedTextWatcher(1500) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(
						gMap, inputAddress.getText().toString());
				task.execute();
			}
		});
		
		inputAddress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) { // Enter
					GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(
							gMap, inputAddress.getText().toString());
					task.execute();
					Util.closeKeyBoard(MainActivity.this);
				}
				return false;
			}
		});
	}

	private void setPopup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupLayout = inflater.inflate(R.layout.popup, null);
        popup =new PopupWindow(popupLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popup.setOutsideTouchable(true);
        popup.setFocusable(false);
		iconTravelMode = (ImageView) popupLayout.findViewById(R.id.ic_travel_mode);
        iconTravelMode.setClickable(true);
        iconTravelMode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	gMap.refreshRoute(true);
            }
        });
        pointBrief = (TextView) popupLayout.findViewById(R.id.point_brief);
        pointDetail = (TextView) popupLayout.findViewById(R.id.point_detail);
        markerid = (TextView) popupLayout.findViewById(R.id.markerid);
        iconRouteDelete = (ImageView) popupLayout.findViewById(R.id.ic_route_delete);
        iconRouteDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String id = markerid.getText().toString();
            	Marker m = gMap.routeMarkers.get(id);
            	//if(m==null) m=gMap.debugMarkers.get(id);
            	gMap.removeMarker(m);
            	gMap.refreshRoute(true);
            }
        });
        iconPolice = (ImageView) popupLayout.findViewById(R.id.ic_police);
        iconPolice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String markerId = markerid.getText().toString();
            	MarkerPoint point = gMap.routeMarkerpoints.get(markerId);
            	if(point!=null){
            		gMap.addRemindMarker(point,R.drawable.ic_police_32);
            		Util.uploadRemind(MainActivity.this,point.getLatLng(),1,null);
            	}
            }
        });
        iconCCTV = (ImageView) popupLayout.findViewById(R.id.ic_cctv);
        iconCCTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String markerId = markerid.getText().toString();
            	MarkerPoint point = gMap.routeMarkerpoints.get(markerId);
            	if(point!=null){
            		gMap.addRemindMarker(point,R.drawable.ic_cctv_32);
            		Util.uploadRemind(MainActivity.this,point.getLatLng(),2,null);
            	}
            }
        });
        iconMedical= (ImageView) popupLayout.findViewById(R.id.ic_medical);
        iconMedical.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String markerId = markerid.getText().toString();
            	MarkerPoint point = gMap.routeMarkerpoints.get(markerId);
            	if(point!=null){
            		gMap.addRemindMarker(point,R.drawable.ic_medical_32);
            		Util.uploadRemind(MainActivity.this,point.getLatLng(),3,null);
            	}
            }
        });
	}
	
    public void openPopup(MarkerPoint mp) {
            popup.setAnimationStyle(R.style.AnimBottom);
            popup.showAtLocation(findViewById(R.id.btn_show), Gravity.BOTTOM, 0, 0);
            //popup.setFocusable(true);
            if(mp!=null){
	            pointBrief.setText(mp.getTitle());
	            pointDetail.setText(mp.getComment());
	            markerid.setText(mp.getId());
	            this.iconRouteDelete.setVisibility(View.VISIBLE);
            }else{
            	pointBrief.setText("");
                pointDetail.setText("");
                markerid.setText("");
                this.iconRouteDelete.setVisibility(View.INVISIBLE);
            }
            popup.update();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case Util.REQ_CODE_SPEECH_INPUT: {
				if (resultCode == RESULT_OK && null != data) {
					List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					ArrayAdapter<String> adapter = new VoiceSuggestListAdapter(this,
					        android.R.layout.simple_list_item_1, result);
					//new CustomListAdapter(YourActivity.this , R.layout.custom_list , mList);
					listVoice.setAdapter(adapter);
					listVoice.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	}
	
	/*@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		//View v = getCurrentFocus();
		Float x1 = (float) 0,y1 = (float) 0,x2,y2;
	    boolean ret = super.dispatchTouchEvent(event);
	    if(event.getAction() == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
    	    Log.i(TAG, "ACTION_DOWN called");
        }
	    if(event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();
            float distanceX = Math.abs(x1-x2);
            float distanceY = Math.abs(y1-y2);
    	    Log.i(TAG, "ACTION_UP x1="+x1+",x2="+x2+",y1="+y1+",y2="+y2);
            if(distanceX>10 ||distanceY>10){
            	
            }else{
            	//popup.dismiss();
            }
        }
		return false;
	}*/
}
