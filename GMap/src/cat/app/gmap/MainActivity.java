package cat.app.gmap;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends android.app.Activity {

	protected static final String TAG = "GMap.MainActivity";
	GMap gMap = new GMap();
	//Button NaviBtn;
	EditText inputAddress;
	ListView listSuggestion;

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
	}

	private void setList() {
		this.listSuggestion = (ListView) findViewById(R.id.listSuggestion);
		this.listSuggestion.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SuggestPoint sp = gMap.points.get(position);
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
		/*NaviBtn = (Button) findViewById(R.id.navigateBtn);
		NaviBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (inputAddress.getText().toString().trim().length() < 3) {
					listSuggestion.setVisibility(View.INVISIBLE);
					Toast.makeText(MainActivity.this,
							"Need a longer name.", Toast.LENGTH_LONG).show();
				}
				GoogleMapSearchByNameTask task = new GoogleMapSearchByNameTask(
						gMap, inputAddress.getText().toString());
				task.execute();
				listSuggestion.setVisibility(View.VISIBLE);
			}
		});*/
	}

	
	private void closeKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
	}

}
