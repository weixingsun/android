package cat.app.osmap;

import java.util.Locale;

import cat.app.maps.MapOptions;
import cat.app.maps.OSM;
import cat.app.osmap.ui.DelayedTextWatcher;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Device {
	Activity act;
	OSM osm;
	public void init(Activity act, OSM osm){
		this.act=act;
		this.osm=osm;
		setText();
		closeKeyBoard();
		setImage();
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
		    	osm.setCenter();
		    }
		});
		final ListView listVoice = (ListView) act.findViewById(R.id.listVoiceSuggestion);
		listVoice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)view;
				EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
				inputAddress.setText(tv.getText());
				listVoice.setVisibility(View.INVISIBLE);
			}
		});
		
	}
	private void setText() {
		EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
		inputAddress.addTextChangedListener(new DelayedTextWatcher(2000) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				//GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(map, inputAddress.getText().toString());
				//task.execute();
			}
		});
		inputAddress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) { // Enter
					//GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(map, inputAddress.getText().toString());
					//task.execute();
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
			ListView listSuggestion = (ListView) act.findViewById(R.id.listSuggestion);
			listSuggestion.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//SuggestPoint sp = map.suggestPoints.get(position);
					//map.addRouteMarker(sp);
					//listSuggestion.setVisibility(View.INVISIBLE);
					//map.move(sp.getLatLng());
				}
			});
	    	listSuggestion.setVisibility(View.INVISIBLE);
	    	ListView listVoice = (ListView) act.findViewById(R.id.listVoiceSuggestion);
	    	listVoice.setVisibility(View.INVISIBLE);
	    	closeKeyBoard();
		}

		
}
