package cat.app.gmap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends android.app.Activity {

	protected static final String TAG = "GMap.MainActivity";
	GMap gMap = new GMap();
	Button NaviBtn;
	EditText inputAddress;
	ListView listSuggestion;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);  
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        gMap.init(this);
        showUI();
    }
    private void showUI(){
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
				
			}});
	}
	private void setText() {
		this.inputAddress = (EditText) findViewById(R.id.inputAddress);
		inputAddress.setTextColor(Color.BLACK);
		inputAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(inputAddress.getText().toString().length()>10){
					//GoogleMapConverterTask task = new GoogleMapConverterTask(gMap,inputAddress.getText().toString());
		            //task.execute();
				}
			}
			@Override
			public void afterTextChanged(Editable s) {}
    });
	}
	private void setButtons() {
		//addBtn = (Button) findViewById(R.id.btnAdd);
		NaviBtn = (Button) findViewById(R.id.navigateBtn);
	    
	    NaviBtn.setOnClickListener(new OnClickListener() {         
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "button clicked", Toast.LENGTH_LONG).show();
                GoogleMapConverterTask task = new GoogleMapConverterTask(gMap,inputAddress.getText().toString());
	            task.execute();
	            listSuggestion.setVisibility(View.VISIBLE);
            }
        });
	}
	
	
}
