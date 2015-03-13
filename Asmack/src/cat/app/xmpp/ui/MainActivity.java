package cat.app.xmpp.ui;

import java.util.ArrayList;
import java.util.HashMap;

import cat.app.xmpp.Client;
import cat.app.xmpp.R;
import cat.app.xmpp.acct.Contact;
import cat.app.xmpp.acct.Message;
import cat.app.xmpp.db.DbHelper;
import cat.app.xmpp.db.DbTask;
import cat.app.xmpp.evt.MessageReceiveEvent;
import cat.app.xmpp.evt.PopulateContactsEvent;
import cat.app.xmpp.evt.UIEvent;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	protected static final String tag = MainActivity.class.getSimpleName();
	private ArrayList<Message> messages = new ArrayList<Message> ();
	private ArrayList<Contact> contacts = new ArrayList<Contact> ();
	private TextView recipient;
	private EditText textMessage;
	private ListView messagelistview;
	private ListView contactlistview;
	//private ContactAdapter cAdapter;
	private DrawerLayout mDrawerLayout;
	private String[] drawerMenu;
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
		DbTask task = new DbTask(DbTask.INIT);
		task.execute();
	}
	private void setupUI() {
		recipient = (TextView) this.findViewById(R.id.toTV);
		textMessage = (EditText) this.findViewById(R.id.chatET);
		messagelistview = (ListView) this.findViewById(R.id.listMessages);
		setMessageListAdapter();
		Button send = (Button) this.findViewById(R.id.sendBtn);
		send.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String to = recipient.getText().toString();
				String text = textMessage.getText().toString();
				Log.i(tag, "Sending text " + text + " to " + to);
				client.sendMsg(to,text);
				Message msg = new Message(Client.SELF,text);
				messages.add(msg);
				//messages.add(text);
				setMessageListAdapter();
			}
		});
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.chat_body);
		DrawerLayout frameLayout = (DrawerLayout) linearLayout.getParent(); // Get parent FrameLayout
		frameLayout.setOnClickListener(new OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	closeKeyBoard();
		    }
		});

		linearLayout.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
		    	closeKeyBoard();
		    }
		});
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		contactlistview = (ListView) this.findViewById(R.id.left_drawer_parent);
		contactlistview.setOnItemClickListener(new ContactClickListener(this));
	}

	private void setMessageListAdapter() {
		MessageAdapter adapter = new MessageAdapter(this, messages);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1);
		messagelistview.setAdapter(adapter);
	}
	
	public void onEventMainThread(MessageReceiveEvent event) {
		Message msg = new Message(event.getFrom(),event.getBody());
    	messages.add(msg);
		setMessageListAdapter();
    	//messagelistview.getAdapter().registerDataSetObserver(observer);
	}
	private void setContactListAdapter() {
		ContactAdapter adapter = new ContactAdapter(this, contacts);
		contactlistview.setAdapter(adapter);
	}
	public void onEventMainThread(PopulateContactsEvent event){
		Log.i(tag, "get contacts event : "+ event.getContacts().size());
		contacts = event.getContacts();
		setContactListAdapter();
	}
	public void onEventMainThread(UIEvent event) {
		if(event.getType().equals(UIEvent.DRAWER)){
			this.closeDrawer(); 
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			//if (client.getConnection()!= null)
				//client.getConnection().disconnect();
		} catch (Exception e) {
		}
	}

	public void closeDrawer(){
		mDrawerLayout.closeDrawers();
	}
	public void closeKeyBoard() {
		EditText msg = (EditText) findViewById(R.id.chatET);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(msg.getWindowToken(), 0);
	}
	
}
