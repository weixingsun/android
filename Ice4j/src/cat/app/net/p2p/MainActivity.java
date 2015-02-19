package cat.app.net.p2p;

import java.util.ArrayList;
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
import cat.app.net.p2p.db.DbHelper;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import cat.app.net.p2p.ui.ListItem;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final String tag = MainActivity.class.getSimpleName();
	// Peer peer = Peer.getInstance();

	//private String android_id = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
	EditText msgBox;
	ListView records;
	private Button add;  
	public MyAdapter adapter;
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
		DbHelper.getInstance(this);
	}

	/*private void setupNet() {
		mQueue = Volley.newRequestQueue(this);
	}*/
	private void setupEar() {
		ear = new Ear(this);
	}

	public void onEventMainThread(ReceiveDataEvent event) {
		Log.i(tag, "EventBus received ReceiveData event:" + event.getMessage());
        adapter.items.add(new ListItem(event.getHost(),event.getMessage()));
        adapter.notifyDataSetChanged();  
	}
	private void setupRecord() {
		records = (ListView) findViewById(R.id.listView1);
		adapter = new MyAdapter(this);
		records.setAdapter(adapter);
	}

	private void setupInput() {
		msgBox = (EditText) this.findViewById(R.id.inputText);
		msgBox.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(msgBox.getText().toString().length()>0){
					if (keyCode == 66) { // Enter
						//Log.i(tag, "onKey()==66");
						closeKeyBoard();
					}
				}
				return false;
			}
		});
		add = (Button)  this.findViewById(R.id.send);
		add.setOnClickListener(new OnClickListener() {
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
            	}
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
		
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(msgBox.getWindowToken(), 0);
	}
	
	
    private class MyAdapter extends BaseAdapter {

    	private Context context;
    	private LayoutInflater inflater;
    	public ArrayList<ListItem> items = new ArrayList<ListItem>();
    	public MyAdapter(Context context) {
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
    		Log.d(tag, "position="+position+",sender="+items.get(position).text); 
    		if(items.get(position).sender.equals(Peer.getInstance().hostname))
    		edit.setGravity(Gravity.RIGHT);
    		/*edit.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(arr.size()>0){
						arr.set(position, edit.getText().toString());
					}
				}
			});
    		  Button del = (Button) view.findViewById(R.id.del);
    		  del.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//从集合中删除所删除项的EditText的内容
					arr.remove(position);
					adapter.notifyDataSetChanged();
				}
			});*/
    		return view;
    	}
    }
}
