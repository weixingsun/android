package cat.app.xmpp;

import java.util.ArrayList;

import cat.app.xmpp.db.DbHelper;
import cat.app.xmpp.db.DbTask;
import cat.app.xmpp.evt.MessageReceiveEvent;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
	protected static final String tag = MainActivity.class.getSimpleName();
	private ArrayList<String> messages = new ArrayList<String> ();
	private EditText recipient;
	private EditText textMessage;
	private ListView listview;
	DbHelper db;
	Client client = Client.getInstance();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		EventBus.getDefault().register(this);
		setupUI();
		setupDB();
		//connect();
		showLogin();
	}
	private void showLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	private void setupDB() {
		db = DbHelper.getInstance(this);
		DbTask task = new DbTask("init");
		task.execute();
	}
	private void setupUI() {
		// Set a listener to send a chat text message
		recipient = (EditText) this.findViewById(R.id.toET);
		textMessage = (EditText) this.findViewById(R.id.chatET);
		listview = (ListView) this.findViewById(R.id.listMessages);
		setListAdapter();
		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String to = recipient.getText().toString();
				String text = textMessage.getText().toString();
				Log.i(tag, "Sending text " + text + " to " + to);
				client.sendMsg(to,text);
				messages.add(Client.SELF + ":");
				messages.add(text);
				setListAdapter();
			}
		});
	}
	private void setListAdapter() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.listitem, messages);
		listview.setAdapter(adapter);
	}
	
	public void onEventMainThread(MessageReceiveEvent event) {
    	messages.add(event.getFrom() + ":");
		messages.add(event.getBody());
		setListAdapter();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			if (client.getConnection()!= null)
				client.getConnection().disconnect();
		} catch (Exception e) {
		}
	}

}
