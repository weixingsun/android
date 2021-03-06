package cat.app.net.p2p;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import cat.app.net.p2p.db.DbHelper;
import cat.app.net.p2p.db.DbTask;
import cat.app.net.p2p.eb.DatabaseEvent;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import cat.app.net.p2p.eb.RemoteSdpEvent;
import cat.app.net.p2p.eb.StatusEvent;
import cat.app.net.p2p.ui.DelayedTextWatcher;
import cat.app.net.p2p.ui.ListItem;
import de.greenrobot.event.EventBus;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity implements OnItemSelectedListener {

	protected static final String tag = MainActivity.class.getSimpleName();
	//private String android_id = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
	DbHelper db;
	Ear ear;
	EditText msgBox;
	ListView records;
	MyListAdapter adapter;
	Button send;  
	ImageView statusLight;
	EditText search;
	String searchValue;

	Spinner hosts;
	ArrayAdapter<String> adapterHosts;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupDB();
		setupActionBar();
		setupInput();
		setupHistoryListView();
		setupEar();
		startRecieverService();
		EventBus.getDefault().register(this);
		DbHelper.getInstance(this);
	}

	@Override
	protected void onDestroy() {
		Ear.cleanupLocalSdp();
		super.onDestroy();
	}
	private void setupDB() {
		db = DbHelper.getInstance(this);
	}
	@SuppressLint("NewApi")
	private void setupActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.actionview);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME| ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		search = (EditText) actionBar.getCustomView().findViewById(R.id.action_search);
		statusLight = (ImageView)actionBar.getCustomView().findViewById(R.id.status_light);
		searchValue = db.getSettings("group");
		if(searchValue==null){
			db.insertSettings("group","888");
			search.setText("888");
			searchValue = "888";
		}
		search.setText(searchValue);
		//Peer.getInstance().group = searchValue;  //network on main thread exception
		search.addTextChangedListener(new DelayedTextWatcher(3000) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				String value = search.getText().toString();
				if(!searchValue.equals(value) && value.length()>0){
					db.changeSettings("group", value);
					searchValue = value;
					Log.i(tag, "change settings group = "+value);
				}
			}
		});
		
	}
	/*@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.layout.actionmenu, menu);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    return true;
	  }*/
	/*private void setupNet() {
		mQueue = Volley.newRequestQueue(this); 
	}*/
	private void setupEar() {
		ear = new Ear(this);
	}
	public void onEventMainThread(ReceiveDataEvent event) {
		//Log.i(tag, "EventBus received ReceiveData event:" + event.getMessage());
        adapter.items.add(new ListItem(event.getHost(),event.getMessage()));
        adapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
		DbTask task = new DbTask(event.getHost(),event.getMessage());
		task.execute();
	}
	public void onEventMainThread(StatusEvent event) {
		//Log.i(tag, "EventBus received StatusEvent:" + event.getStatus());
		if(event.getStatus().equals(StatusEvent.TERMINATED)){
			statusLight.setImageResource(R.drawable.light_green_16);
		}else if(event.getStatus().equals(StatusEvent.FAILED)){
			statusLight.setImageResource(R.drawable.light_red_16);
		}else if(event.getStatus().equals(StatusEvent.FAILED)){
			
		}
	}
	public void onEventMainThread(DatabaseEvent event) {
		this.search.setText(event.getSettingsValue());
	}
	public void onEventMainThread(RemoteSdpEvent event) {
        if(event.getSdps() !=null && event.getSdps().size()>0) {
			Ear.hearRemoteSdp = true;
			Set<String> keys = event.remoteHosts.keySet();
			fillHostSpinner(keys.toArray(new String[keys.size()]));
		}
	}
	private void fillHostSpinner(String[] hostnames) {
		//Log.i(tag, "hostnames="+hostnames);
		hosts = (Spinner) findViewById(R.id.hosts);
		adapterHosts = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, hostnames);
		adapterHosts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		hosts.setAdapter(adapterHosts);
		hosts.setVisibility(View.VISIBLE);
		hosts.setOnItemSelectedListener(this);
	}
	private void setupHistoryListView() {
		records = (ListView) findViewById(R.id.listView1);
		adapter = new MyListAdapter(this);
		records.setAdapter(adapter);
	}
	private void setupInput() {
		msgBox = (EditText) this.findViewById(R.id.inputText);
		msgBox.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(msgBox.getText().toString().length()>0){
					if (keyCode == 66) { // Enter
						closeKeyBoard();
					}
				}
				return false;
			}
		});
		send = (Button)  this.findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {
            @Override  
            public void onClick(View arg0) {
            	String msg = msgBox.getText().toString();
            	if(msg.length()>0){
					Peer.getInstance().send(msg);
	                adapter.items.add(new ListItem(Peer.getInstance().hostname,msg));
	                adapter.notifyDataSetChanged();
					msgBox.setText("");
					String host = Peer.getInstance().hostname;
					DbHelper.getInstance().insertMsg(host, msg);
					scrollMyListViewToBottom();
            	}
            }  
        });
	}
	public void startRecieverService() {
		Intent intent = new Intent(this, ListenerIntentService.class);
		intent.putExtra("group", searchValue);
		startService(intent);
	}
	public void closeKeyBoard() {
		
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(msgBox.getWindowToken(), 0);
	}
	private void scrollMyListViewToBottom() {
		records.post(new Runnable() {
	        @Override
	        public void run() {
	        	records.setSelection(adapter.getCount() - 1);
	        }
	    });
	}
	@Override
	protected void onPause() {
	    // hide the keyboard in order to avoid getTextBeforeCursor on inactive InputConnection
	    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(this.search.getWindowToken(), 0);
	    super.onPause();
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String text = hosts.getSelectedItem().toString();
		//Log.i(tag, "selected="+text);
		String remotesdp = Ear.remoteHosts.get(text);
		Peer p = Peer.getInstance();
		p.remoteHostname = text;
		p.remoteSdp = remotesdp;
	}
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.i(tag, "ignore this action");
	}
    private class MyListAdapter extends BaseAdapter {
    	private Context context;
    	private LayoutInflater inflater;
    	public ArrayList<ListItem> items = new ArrayList<ListItem>();
    	public MyListAdapter(Context context) {
    		super();
    		this.context = context;
    		inflater = LayoutInflater.from(context);
    		/*for(int i=0;i<1;i++){    //listview初始化8个子项
    			arr.add("");
    		}*/
    	}
    	@Override
    	public int getCount() {
    		return items.size();
    	}
    	@Override
    	public Object getItem(int arg0) {
    		return arg0;
    	}
    	@Override
    	public long getItemId(int arg0) {
    		return arg0;
    	}
    	@Override
    	public View getView(final int position, View view, ViewGroup arg2) {
    		if(view == null){
    			view = inflater.inflate(R.layout.listviewitem, null);
    		}
    		String msg = items.get(position).text;
    		final TextView edit = (TextView) view.findViewById(R.id.edit);
    		edit.setText(msg);
    		String sender = items.get(position).sender;
    		String hostname = Peer.getInstance().hostname;
    		//Log.d(tag, "position="+position+",sender="+sender+",hostname="+hostname);
    		if(sender.equals(hostname)){
    			edit.setGravity(Gravity.RIGHT);
    		}else{
    			edit.setGravity(Gravity.LEFT);
    		}
    		return view;
    	}
    }

}
