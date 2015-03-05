package cat.app.xmpp;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.SSLContext;

import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.TextInputCallback;
import org.apache.harmony.javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.apache.qpid.management.common.sasl.SaslProvider;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import de.greenrobot.event.EventBus;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectionService extends IntentService  {
	//private Account account = new Account("dukgo.com","weixingsun","ws206771");
	private Account account = new Account("gmail.com","weixing.sun","qweASD123");
	protected static final String tag = ConnectionService.class.getSimpleName();
	XMPPTCPConnection connection;
	Client client = Client.getInstance();
	private ArrayList<String> messages = new ArrayList<String>();
	public ConnectionService() {
		super("");
		//EventBus.getDefault().register(this);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ConnectionConfiguration config = getConfig();
		connection = new XMPPTCPConnection(config);
		try {
			connection.connect();
			connection.login(account.getUsername(), account.getPassword());
			Log.i("XMPPChatDemoActivity","Logged in to "+connection.getHost()+" as " + connection.getUser());
			client.setConnection(connection);
			// Set the status to available
			Presence presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			setConnection();
			EventBus.getDefault().post(new ConnectionStatusEvent(ConnectionStatusEvent.CONNECTED));
			//Roster roster = connection.getRoster();
			//printContacts(roster);
		} catch (Exception e) {
			Log.e("XMPPChatDemoActivity", "Failed to log in as "+ account.getUsername());
			Log.e("XMPPChatDemoActivity", e.toString());
			//setConnection();
			e.printStackTrace();
		}

	}
	private ConnectionConfiguration getConfig() {
		Context context = getApplicationContext();
		//SmackAndroid.init(context);
		//Security.addProvider(new SaslProvider());
		SmackAndroid.init(context);
		SASLAuthentication.supportSASLMechanism("PLAIN",0);
		//SASLAuthentication.supportSASLMechanism("X-OAUTH2");
		//SASLAuthentication.supportSASLMechanism("SCRAM-SHA-1");
		/*SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLS");
		    sc.init(null, null, new SecureRandom());
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		ConnectionConfiguration config = new ConnectionConfiguration(account.getHostname());
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
		for (RosterEntry entry : roster.getEntries()) {
			Log.d("XMPPChatDemoActivity","--------------------------------------");
			Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
			Log.d("XMPPChatDemoActivity","User: " + entry.getUser());
			Log.d("XMPPChatDemoActivity","Name: " + entry.getName());
			Log.d("XMPPChatDemoActivity","Status: " + entry.getStatus());
			Log.d("XMPPChatDemoActivity","Type: " + entry.getType());
			Presence entryPresence = roster.getPresence(entry.getUser());

			Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
			Log.d("XMPPChatDemoActivity", "Presence Type: "+ entryPresence.getType());
			Presence.Type type = entryPresence.getType();
			if (type == Presence.Type.available)
				Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
			Log.d("XMPPChatDemoActivity", "Presence : "+ entryPresence);
		}
		
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
						String fromName = message.getFrom(); //StringUtils.parseBareAddress(message.getFrom());
						Log.i(tag, "Text Recieved " + message.getBody() + " from " + fromName );
						messages.add(fromName + ":");
						messages.add(message.getBody());
						// Add the incoming message to the list view
						//setListAdapter();
						EventBus.getDefault().post(new MessageReceiveEvent(message.getFrom(),message.getBody()));
					}
				}
			}, filter);
		}
	}


}
