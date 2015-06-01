package wsn.park;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;

import wsn.park.R;
import wsn.park.maps.Mode;
import wsn.park.maps.OSM;
import wsn.park.model.SavedPlace;
import wsn.park.ui.DelayedTextWatcher;
import wsn.park.ui.SuggestListAdapter;
import wsn.park.util.DbHelper;
import wsn.park.util.GeoOptions;
import wsn.park.util.MapOptions;
import wsn.park.util.RuntimeOptions;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class Device {
	Activity act;
	OSM osm;
	EditText inputAddress;
	ListView listVoice;
	ListView listSuggest;
	DbHelper dbHelper;
    PopupWindow popup;
	Mode mode;
	TextView pointBrief;
	TextView pointDetail;
	TextView lat;
	TextView lng;
	ImageView iconHome;
	ImageView iconWork;
	ImageView iconTravelBy;
	private String tag=Device.class.getSimpleName();
	public void init(Activity act, OSM osm){
		this.act=act;
		this.osm=osm;
		inputAddress = (EditText) act.findViewById(R.id.inputAddress);
		listVoice = (ListView) act.findViewById(R.id.listVoiceSuggestion);
		listSuggest = (ListView) act.findViewById(R.id.listSuggestion);
		

		setText();
		closeKeyBoard();
		setImage();
		setList();
		setPopup();
		dbHelper = DbHelper.getInstance();
	}
	
	private void setList() {
		listVoice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)view;
				inputAddress.setText(tv.getText());
				listVoice.setVisibility(View.INVISIBLE);
			}
		});
		listSuggest.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Address addr = osm.suggestPoints.get(position);
				//osm.mks.updateRouteMarker(addr);////////////////////offline navi no marker??????
				SavedPlace sp = GeoOptions.getMyPlace(addr);
				osm.mks.updatePointOverlay(sp);
				listSuggest.setVisibility(View.INVISIBLE);
				osm.move(addr.getLatitude(),addr.getLongitude());
				dbHelper.addHistoryPlace(addr);
				openPopup(sp);
				//Log.w(tag, "show popup window");
			}
		});
	}
	private void setImage() {
		ImageView voiceInput = (ImageView) act.findViewById(R.id.voiceInput);
		voiceInput.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	promptSpeechInput();
		    }
		});
		ImageView myloc = (ImageView) act.findViewById(R.id.my_loc);
		myloc.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	MapOptions.move();
		    }
		});
	}

	private void setText() {
		inputAddress.addTextChangedListener(new DelayedTextWatcher(2000) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				if(RuntimeOptions.getInstance(osm.act).isNetworkAvailable()){
					osm.startTask("geo",inputAddress.getText().toString());
				}else{
					Toast.makeText(osm.act, GeoOptions.NETWORK_UNAVAILABLE, Toast.LENGTH_LONG).show();
				}
			}
		});
		inputAddress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) { // Enter
					if(RuntimeOptions.getInstance(osm.act).isNetworkAvailable()){
						osm.startTask("geo",inputAddress.getText().toString());
					}else{
						Toast.makeText(osm.act, GeoOptions.NETWORK_UNAVAILABLE, Toast.LENGTH_LONG).show();
					}
					closeKeyBoard();
				}
				return false;
			}
		});
	}

		public void closeKeyBoard() {
			EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
			InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
		}
		public void promptSpeechInput() {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
					act.getString(R.string.speech_prompt));
			try {
				act.startActivityForResult(intent, MapOptions.REQ_CODE_SPEECH_INPUT);
			} catch (ActivityNotFoundException a) {
				Toast.makeText(act.getApplicationContext(),act.getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
			}
		}
		public void closeAllList() {
	    	listSuggest.setVisibility(View.INVISIBLE);
	    	listVoice.setVisibility(View.INVISIBLE);
	    	closeKeyBoard();
		}
	    public void fillList(List<Address> addrs){
	        ArrayList<Map<String, String>> list = buildData(addrs);
	        String[] from = { "name"};
	        int[] to = { android.R.id.text1 };
	        SimpleAdapter adapter = new SuggestListAdapter(osm.act, list,
	            android.R.layout.simple_list_item_2, from, to);
	        listSuggest.setAdapter(adapter);
	        listSuggest.setVisibility(View.VISIBLE);
	    }
	    private ArrayList<Map<String, String>> buildData(List<Address> addrs) {
	        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	        for(Address a:addrs){
	        	String display = GeoOptions.getAddressName(a);
	        	Log.w(tag, a.toString());
	        	list.add(putData(display));
	        	
	        }
	        return list;
	      }
	      private HashMap<String, String> putData(String name) {
	          HashMap<String, String> item = new HashMap<String, String>();
	          item.put("name", name);
	          return item;
	        }
	  	private void setPopup() {
	        LayoutInflater inflater = LayoutInflater.from(osm.act);
	        View popupLayout = inflater.inflate(R.layout.popup, null);
	        popup =new PopupWindow(popupLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	        popup.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	        popup.setOutsideTouchable(true);
	        popup.setFocusable(false);
			pointBrief = (TextView) popupLayout.findViewById(R.id.point_brief);
			pointDetail = (TextView) popupLayout.findViewById(R.id.point_detail);
			lat = (TextView) popupLayout.findViewById(R.id.lat);
			lng = (TextView) popupLayout.findViewById(R.id.lng);
			iconHome = (ImageView) popupLayout.findViewById(R.id.home);
			iconWork = (ImageView) popupLayout.findViewById(R.id.work);
			iconTravelBy  = (ImageView) popupLayout.findViewById(R.id.travel_mode);
			iconTravelBy.setClickable(true);
			iconTravelBy.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	            	//LayoutInflater inflater = LayoutInflater.from(osm.act);
	    	        //View popupLayout = inflater.inflate(R.layout.popup, null);
	    			//TextView tv_lat = (TextView) popupLayout.findViewById(R.id.lat);
	    			//TextView tv_lng = (TextView) popupLayout.findViewById(R.id.lng);
	    			String str_lat = lat.getText().toString();
	    			String str_lng = lng.getText().toString();
	                //Log.w(tag, "lat="+str_lat+", lng="+str_lng);
	    	        GeoPoint gp = new GeoPoint(Double.valueOf(str_lat),Double.valueOf(str_lng));
					osm.ro.setWayPoints(new GeoPoint(osm.loc.myPos),gp);
					osm.startTask("route", gp,"route");
	            	Mode.setID(Mode.NAVI);
	            }
	        });
	  	}
	    public void openPopup(SavedPlace sp) {
            popup.setAnimationStyle(R.style.AnimBottom);
            popup.showAtLocation(osm.act.findViewById(R.id.my_loc), Gravity.BOTTOM, 0, 0); //leaked window
            popup.setFocusable(true);
    		
            pointBrief.setText(sp.getBriefName());
            pointDetail.setText(sp.getAdmin());
            lat.setText(String.valueOf(sp.getLat()));
            lng.setText(String.valueOf(sp.getLng()));
            //Log.w(tag, "lat="+sp.getLat()+", lng="+sp.getLng());
            hidePopupIcons();
            popup.update();
            Log.w(tag, "openPopup");
    }

		private void hidePopupIcons() {
			switch(Mode.getID()){
			case Mode.NORMAL:
			case Mode.NAVI:
			case Mode.PRACTICE:
	            iconHome.setVisibility(View.INVISIBLE);
	            iconWork.setVisibility(View.INVISIBLE);
	            break;
			case Mode.HOME:
	            iconHome.setVisibility(View.VISIBLE);
	            iconWork.setVisibility(View.INVISIBLE);
	            break;
			case Mode.WORK:
	            iconHome.setVisibility(View.INVISIBLE);
	            iconWork.setVisibility(View.VISIBLE);
	            break;
			}
		}
}
