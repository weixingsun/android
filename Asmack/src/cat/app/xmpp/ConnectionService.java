package cat.app.xmpp;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import cat.app.xmpp.acct.Account;
import cat.app.xmpp.acct.Contact;
import cat.app.xmpp.db.DbTask;
import cat.app.xmpp.evt.LoginEvent;
import cat.app.xmpp.evt.MessageReceiveEvent;
import cat.app.xmpp.evt.PopulateContactsEvent;
import cat.app.xmpp.evt.PopulateSettingsEvent;
import de.greenrobot.event.EventBus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectionService extends IntentService  {
	protected static final String tag = ConnectionService.class.getSimpleName();
	XMPPTCPConnection connection;
	//XMPPService service;
	Client client = Client.getInstance();
	//private ArrayList<String> messages = new ArrayList<String>();
	public ConnectionService() {
		super("");
		//EventBus.getDefault().register(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String host = intent.getStringExtra("host");
		String user = intent.getStringExtra("user");
		String pswd = intent.getStringExtra("pswd");
		
		if(client.getConnection()!=null && client.getConnection().isConnected()) return;
		ConnectionConfiguration config = getConfig(host);
		connection = new XMPPTCPConnection(config);
		try {
			connection.connect();
			connection.login(user, pswd);
			Log.i(tag,"Logged in to "+connection.getHost()+" as " + connection.getUser());
			client.setConnection(connection);
			// Set the status to available
			Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			setConnection();
			EventBus.getDefault().post(new LoginEvent(LoginEvent.SUCCESS));
			printContacts(connection.getRoster());
			
			Account account = new Account(host,user,pswd);
			DbTask task = new DbTask(DbTask.SETTINGS, PopulateSettingsEvent.LAST_LOGIN, account.getUsername()+"@"+account.getHostname());
			task.execute();
			task = new DbTask(DbTask.SETTINGS, PopulateSettingsEvent.LAST_PASSWORD, account.getPassword());
			task.execute();
			task = new DbTask(DbTask.ACCOUNT,account);
			task.execute();
		} catch (Exception e) {
			Log.e(tag, "Failed to log in as "+ user);
			Log.e(tag, e.toString());
			e.printStackTrace();
			EventBus.getDefault().post(new LoginEvent(LoginEvent.FAIL));
		}

	}
	private ConnectionConfiguration getConfig(String host) {
		Context context = getApplicationContext();
		//SmackAndroid.init(context);
		//Security.addProvider(new SaslProvider());
		SmackAndroid.init(context);
		SmackConfiguration.setDefaultPacketReplyTimeout(8*1000);
		SASLAuthentication.supportSASLMechanism("PLAIN",0);
		//SASLAuthentication.supportSASLMechanism("X-OAUTH2");
		//SASLAuthentication.supportSASLMechanism("SCRAM-SHA-1");
		/*SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLS");
		    sc.init(null, null, new SecureRandom());
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		*/
		ConnectionConfiguration config = new ConnectionConfiguration(host);
		//new ConnectionConfiguration(account.getHostname(), SERVICE);
		/*
		config.setCompressionEnabled(false);
		config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
		config.setKeystorePath("src/main/config/bogus_mina_tls.cert");
		config.setKeystoreType("BKS");
		config.setCallbackHandler(this);
		config.setDebuggerEnabled(false);
		config.setCustomSSLContext(sc);
		*/
		//config.setDebuggerEnabled(true);
		return config;
	}
 
	private void printContacts(Roster roster) {
		//Log.i(tag, "print contacts:"+roster.getEntries().size());
		ArrayList<Contact> cts = new ArrayList<Contact>();
		for (RosterEntry entry : roster.getEntries()) {
			String user = entry.getUser();
			String name = entry.getName();
			//String status = entry.getStatus().toString();
			//Log.d(tag,"Type: " + entry.getType());
			Presence entryPresence = roster.getPresence(entry.getUser());
			//Log.d(tag, "Presence Status: "+ entryPresence.getStatus());
			Presence.Type type = entryPresence.getType();
			//if (type == Presence.Type.available)
			//Log.d(tag, user + " : "+type.name());
			//Log.d(tag, "Presence : "+ entryPresence);
			Contact c = new Contact(user, name, type.name());
			cts.add(c);
		}
		EventBus.getDefault().post(new PopulateContactsEvent(cts));
	}

	/**
	 * Called by Settings dialog when a connection is establised with the XMPP server
	 * 
	 * @param connection
	 */
	public void setConnection() {
		if (connection != null) {
			// Add a packet listener to get messages sent to us
			//PacketFilter filter = new MessageTypeFilter(Message.Type.chat); //MessageTypeFilter.CHAT;
			PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
			connection.addPacketListener(new PacketListener() {
				@Override
				public void processPacket(Packet packet) {
					Message message = (Message) packet;
					if (message.getBody() != null) {
						String body = message.getBody().trim();
						String fromName = StringUtils.parseBareAddress(message.getFrom());
						Log.i(tag, "Text Recieved " + body + " from " + fromName );
						DbTask task = new DbTask(DbTask.MSG, fromName, body);
						task.execute();
						EventBus.getDefault().post(new MessageReceiveEvent(fromName,body));
					}
				}
			}, filter);
		}
	}


}
