package cat.app.net.p2p;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import cat.app.net.p2p.core.ListenerIntentService;
import cat.app.net.p2p.core.Peer;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final String tag = MainActivity.class.getSimpleName();
	// Peer peer = Peer.getInstance();

	//private String android_id = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
	EditText msgBox;
	Ear ear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupInput();
		setupRecord();
		setupEar();
		startRecieverService();

		EventBus.getDefault().register(this);
	}

	/*private void setupNet() {
		mQueue = Volley.newRequestQueue(this);
	}*/
	private void setupEar() {
		ear = new Ear(this);
	}

	public void onEvent(ReceiveDataEvent event) {
		Log.i(tag, "EventBus received ReceiveData event:" + event.getMessage());
	}
	private void setupRecord() {

	}

	private void setupInput() {
		msgBox = (EditText) this.findViewById(R.id.input);
		msgBox.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(msgBox.getText().toString().length()>0){
					if (keyCode == 66) { // Enter
						Peer.getInstance().send(msgBox.getText().toString());
						Log.i(tag, "onKey()==66");
						closeKeyBoard();
						msgBox.setText("");
					}
				}
				return false;
			}
		});
	}

	@Override
	protected void onDestroy() {
		Ear.cleanupLocalSdp();
		super.onDestroy();
		
	}

	public void startRecieverService() {
		Intent intent = new Intent(this, ListenerIntentService.class);
		startService(intent);
	}
	public void closeKeyBoard() {
		EditText inputAddress = (EditText) this.findViewById(R.id.input);
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
	}
}
