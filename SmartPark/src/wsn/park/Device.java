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
import wsn.park.model.DataBus;
import wsn.park.model.Place;
import wsn.park.model.SavedPlace;
import wsn.park.navi.task.ParkingAPI;
import wsn.park.ui.DelayedTextWatcher;
import wsn.park.ui.SuggestListAdapter;
import wsn.park.ui.marker.OsmMapsItemizedOverlay;
import wsn.park.ui.marker.PlaceMarker;
import wsn.park.util.DbHelper;
import wsn.park.util.GeoOptions;
import wsn.park.util.MapOptions;
import wsn.park.util.RuntimeOptions;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
	DataBus bus = DataBus.getInstance();
	EditText inputAddress;
	ListView listVoice,listSuggest;
	DbHelper dbHelper;
    PopupWindow placePop,naviPop;
	//Mode mode;
	TextView pointBrief,pointDetail,lat,lng,country_code,special,tv_id,tv_instruction,star;
	ImageView iconHome,iconWork,iconStar,iconPark,iconTravelBy,iconCloseNavi,iconNaviFlag;
	private String tag=Device.class.getSimpleName();
	private String STARRED = "true";
	private String NO_STAR = "false";
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
				SavedPlace addr = osm.suggestPoints.get(position);
				//osm.mks.updateRouteMarker(addr);////////////////////offline navi no marker??????
				//SavedPlace sp = GeoOptions.getMyPlace(addr);
				listSuggest.setVisibility(View.INVISIBLE);
				osm.move(addr.getLat(),addr.getLng());
				dbHelper.addHistoryPlace(addr);
				osm.mks.updateTargetMarker(addr);
				openPlacePopup(addr);
				//Log.w(tag, "show popup window");
				closeKeyBoard();
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
		inputAddress.addTextChangedListener(new DelayedTextWatcher(1000) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				osm.startTask("poi",inputAddress.getText().toString());
			}
		});
		inputAddress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) { // Enter
					osm.startTask("poi",inputAddress.getText().toString());
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
	    public void fillList(List<SavedPlace> addrs){
	        ArrayList<Map<String, String>> list = buildData(addrs);
	        String[] from = { "name"};
	        int[] to = { android.R.id.text1 };
	        SimpleAdapter adapter = new SuggestListAdapter(osm.act, list,
	            android.R.layout.simple_list_item_2, from, to);
	        listSuggest.setAdapter(adapter);
	        listSuggest.setVisibility(View.VISIBLE);
	    }
	    private ArrayList<Map<String, String>> buildData(List<SavedPlace> addrs) {
	        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
	        for(SavedPlace a:addrs){
	        	String display = a.getName(); //GeoOptions.getAddressName(a);
	        	//Log.w(tag, a.toString());
	        	if(a.getDist()>0)	display+=" ("+a.getDist()+"m)";
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
	        View popupLayout = inflater.inflate(R.layout.popup_place, null);
	        placePop =new PopupWindow(popupLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	        placePop.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	        placePop.setOutsideTouchable(true);
	        placePop.setFocusable(false);
			pointBrief = (TextView) popupLayout.findViewById(R.id.point_brief);
			pointDetail = (TextView) popupLayout.findViewById(R.id.point_detail);
			lat = (TextView) popupLayout.findViewById(R.id.lat);
			lng = (TextView) popupLayout.findViewById(R.id.lng);
			country_code = (TextView) popupLayout.findViewById(R.id.country_code);
			special = (TextView) popupLayout.findViewById(R.id.special);
			tv_id = (TextView) popupLayout.findViewById(R.id.tv_id);
			star = (TextView) popupLayout.findViewById(R.id.starred); 
			iconHome = (ImageView) popupLayout.findViewById(R.id.home);
			iconWork = (ImageView) popupLayout.findViewById(R.id.work);
			iconStar = (ImageView) popupLayout.findViewById(R.id.star);
			iconPark = (ImageView) popupLayout.findViewById(R.id.parking);
			iconPark.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SavedPlace sp = getPlaceFromPopupPage();
					ParkingAPI.getInstance().search(sp.getPosition(), 0);
				}
			});
			iconTravelBy  = (ImageView) popupLayout.findViewById(R.id.travel_mode);
			iconTravelBy.setClickable(true);
			iconTravelBy.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	        		if(naviPop!=null) naviPop.dismiss();
	            	SavedPlace sp = getPlaceFromPopupPage();
	            	if(bus.getMyPoint()==null && LOC.myPos==null) return; //wait for GPS aquire data
	            	GeoPoint gp = LOC.myPos==null?bus.getMyPoint():new GeoPoint(LOC.myPos);
					osm.ro.setWayPoints(gp,sp.getPosition());
					osm.startTask("route", sp.getPosition(),"route");
	            	Mode.setID(Mode.NAVI);
	            	openPopupNaviMode();
	            	placePop.dismiss();
	            }
	        });
			iconStar.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					SavedPlace sp = getPlaceFromPopupPage();
	    			//if(sp.getSpecial()>Place.NORMAL-1){
					if(sp.isStar()){
	    				osm.mks.savedPlaceMarkers.remove(osm.mks.selectedMarker);
	    				Drawable d = osm.act.getResources().getDrawable(R.drawable.transparent_16);
	    				d.setAlpha(0);
	    				osm.mks.selectedMarker.setIcon(d);
	    				osm.map.getOverlays().remove(osm.mks.selectedMarker);
	    				osm.mks.selectedMarker=null;
	    				osm.map.invalidate();
	    				placePop.dismiss();
	    				dbHelper.deleteMyPlaces(sp.getId());
	    				Log.w(tag, "delete place.star="+sp.isStar());
	    			}else{
	    				sp.setSpecial(Place.NORMAL);
	    				dbHelper.addMyPlace(sp);
	    				//osm.mks.selectedMarker.setSp(sp);
	    				star.setText(STARRED);
	    				Drawable d = osm.act.getResources().getDrawable( R.drawable.star_red_24 );
	    				osm.mks.selectedMarker.setIcon(d);
	    				osm.map.invalidate();
	    				sp.setStar(true);
	    				osm.mks.selectedMarker.setPlace(sp);
	    				osm.mks.savedPlaceMarkers.add(osm.mks.selectedMarker);
	    				iconStar.setImageResource(R.drawable.heart_broken_48);
	    				special.setText(String.valueOf(Place.NORMAL));
	    				osm.mks.tempMarker=null;
	    				Log.w(tag, "add place.star="+sp.isStar());
	    			}
				}});
	  	}

		public SavedPlace getPlaceFromPopupPage(){
	    	int str_id = Integer.valueOf(tv_id.getText().toString());
			String str_country_code = country_code.getText().toString();
			int i_special = Integer.valueOf(special.getText().toString());
			double dlat = Double.valueOf(lat.getText().toString());
			double dlng = Double.valueOf(lng.getText().toString());
	        String briefName = pointBrief.getText().toString();
	        String adminName = pointDetail.getText().toString();
	        boolean starB = star.getText().toString().equals(STARRED)?true:false;
	        //Log.w(tag, "getPlaceFromPopupPage.str_country_code="+str_country_code);
			SavedPlace sp = new SavedPlace(str_id,briefName, adminName, dlat,dlng,str_country_code,null,null,i_special,starB);
	        //Log.w(tag, "getPlaceFromPopupPage.sp.country_code="+sp.getCountryCode());
			return sp;
	    }

	    private void openPopupNaviMode() {
	        LayoutInflater inflater = LayoutInflater.from(osm.act);
	        View naviLayout = inflater.inflate(R.layout.popup_navi, null);
	        naviPop =new PopupWindow(naviLayout, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	        naviPop.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	        //navi.setOutsideTouchable(false);
	        naviPop.setFocusable(false);
	        naviPop.setAnimationStyle(R.style.AnimBottom);
	        naviPop.showAtLocation(osm.act.findViewById(R.id.my_loc), Gravity.BOTTOM, 0, 0); //leaked window
	        //navi.setFocusable(true);
	        naviPop.update();
	        tv_instruction = (TextView) naviLayout.findViewById(R.id.navi_instruction);
	        iconNaviFlag = (ImageView) naviLayout.findViewById(R.id.navi_flag); 
	        iconCloseNavi = (ImageView) naviLayout.findViewById(R.id.close_navi);
	        iconCloseNavi.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View v) {
	        		Mode.setID(Mode.NORMAL);
	        		naviPop.dismiss();
	        		osm.mks.removeAllRouteMarkers();
	        	}});
		}
	    public void updateNaviInstruction(String instruction,int resId){
	    	tv_instruction.setText(instruction);
	    	iconNaviFlag.setImageResource(resId);
	    }
	    public void openPlacePopup(Place p) {
	    	//act.getResources().getDrawable(R.drawable.hearts_48);
	    	//osm.mks.removeTempMarker(pin);
	    	//osm.mks.hideLastMarkerSecondIcon();
            placePop.setAnimationStyle(R.style.AnimBottom);
            placePop.showAtLocation(osm.act.findViewById(R.id.my_loc), Gravity.BOTTOM, 0, 0); //leaked window
            placePop.setFocusable(true);
            pointBrief.setText(p.getName());
            pointDetail.setText(p.getAdmin());
            lat.setText(String.valueOf(p.getLat()));
            lng.setText(String.valueOf(p.getLng()));
            country_code.setText(p.getCountryCode());
        	special.setText(String.valueOf(p.getSpecial()));
        	star.setText(String.valueOf(p.isStar()));
        	hidePopupIcons(p);
            if(p.getId()==0) p.setId(dbHelper.getMaxID(DbHelper.MY_PLACE_TABLE)+1);
            tv_id.setText(String.valueOf(p.getId()));
            placePop.update();
            //osm.mks.selectedMarker = pin;
	    }
	    public void openPlacePopup(GeoPoint gp) {
	    	if(osm.mks.selectedMarker!=null){
		    	GeoPoint a=osm.mks.selectedMarker.getPosition();
		    	if(wsn.park.util.MathUtil.compare(a, gp)){
		    		this.openPlacePopup(osm.mks.selectedMarker.getPlace());
		    		return;
		    	}
	    	}
	    	PlaceMarker pin = osm.mks.findMyPlace(gp);
	    	this.openPlacePopup(pin.getPlace());
	    }
		private void hidePopupIcons(Place sp) {
			switch(Mode.getID()){
			case Mode.NORMAL:
			case Mode.NAVI:
			case Mode.PRACTICE:
	            iconHome.setVisibility(View.GONE);//INVISIBLE is occupying the space
	            iconWork.setVisibility(View.GONE);
	            break;
//			case Mode.HOME:
//	            iconHome.setVisibility(View.VISIBLE);
//	            iconWork.setVisibility(View.GONE);
//	            break;
//			case Mode.WORK:
//	            iconHome.setVisibility(View.GONE);
//	            iconWork.setVisibility(View.VISIBLE);
//	            break;
			}
			switch(sp.getSpecial()){
			case Place.NORMAL: //cancel save
				iconStar.setImageResource(R.drawable.heart_broken_48);
				break;
			case Place.HOME:   //cancel home
				iconHome.setImageResource(R.drawable.ic_black_trash_64);
				break;
			case Place.WORK:   //cancel work
				iconWork.setImageResource(R.drawable.ic_black_trash_64);
				break;
			default: iconStar.setImageResource(R.drawable.heart_48);
			}
		}

		public void closePopupPlace() {
			this.placePop.dismiss();
		}
		public void closePopupNavi() {
			this.naviPop.dismiss();
		}
}

/*iconHome.setOnClickListener(new OnClickListener(){
	@Override
	public void onClick(View v) {
		//insert home place into sqlite: special=1
		SavedPlace sp = getPlaceFromPopupPage();
		if(sp.getSpecial()==Place.HOME){
			dbHelper.deleteMyPlaces(sp.getId());
		}else{
			dbHelper.addMyPlace(sp, Place.HOME);
		}
	}});
iconWork.setOnClickListener(new OnClickListener(){
	@Override
	public void onClick(View v) {
		//insert work place into sqlite: special=12
		SavedPlace sp = getPlaceFromPopupPage();
		if(sp.getSpecial()==Place.WORK){
			dbHelper.deleteMyPlaces(sp.getId());
		}else{
			dbHelper.addMyPlace(sp, Place.WORK);
		}
	}});*/