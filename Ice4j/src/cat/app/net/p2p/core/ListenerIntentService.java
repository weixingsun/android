package cat.app.net.p2p.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import cat.app.net.p2p.Ear;
import cat.app.net.p2p.db.DbHelper;
import cat.app.net.p2p.db.DbTask;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import cat.app.net.p2p.eb.RemoteSdpEvent;
import cat.app.net.p2p.eb.SdpEvent;
import cat.app.net.p2p.eb.StatusEvent;
import cat.app.net.p2p.util.DateUtils;

import de.greenrobot.event.EventBus;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ListenerIntentService extends IntentService {

	private static final String tag = ListenerIntentService.class.getSimpleName();
	byte[] buf = new byte[1024];
	// IceClient client=null;
	Peer peer;
	String content;

	public ListenerIntentService() {
		super("");
		DbHelper.getInstance().createTables();
	}
	private void init(String group) {
		if (peer == null) {
			try {
				peer = Peer.getInstance();
				peer.group = group;
				peer.sdp = peer.client.localSdp;
				// this.socket = peer.client.getDatagramSocket();
				// Log.i(tag,">>>>>>>>>>>>>>>>>>>>>>>>>SOCKET:"+socket.toString());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		String group = intent.getStringExtra("group");
		init(group);
		//Log.i(tag, "sendLocalSdp()");
		sendLocalSdp(peer.hostname, peer.client.localSdp,peer.group);
		
		//Log.i(tag, "loopForRemoteSdp()");
		loopForRemoteSdp(peer.group);
		Log.i(tag, "initConnection():sdp="+peer.remoteSdp); 
		boolean flag = peer.client.initConnection(peer.remoteSdp);
		if(flag){
			EventBus.getDefault().post(new StatusEvent("connected"));
			Ear.cleanupLocalSdp();
			loopForNextMessage();
		}
	}
	public void loopForRemoteSdp(String group){
		while (!Ear.hearRemoteSdp || peer.remoteSdp==null){
			Log.i(tag, "waiting for sdp from group="+group+",sdp="+peer.remoteSdp);
			try {
				Thread.sleep(5000);
				Ear.downloadRemoteSdp(peer.group,peer.hostname,peer.sdp);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
	public void loopForNextMessage(){
		while (true) {
			try {
				this.content = receive();
				//Thread.sleep(5000);	// TimeUnit.MILLISECONDS.sleep(2000);
				Log.i(tag, "received message from host:"+peer.remoteHostname+","+this.content);
				receiveMsg(peer.remoteHostname,this.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
	private void sendLocalSdp(String host, String sdp, String group) {
		EventBus.getDefault().post(new SdpEvent(host, sdp, group));
	}
	private void receiveMsg(String host, String msg) {
		EventBus.getDefault().post(new ReceiveDataEvent(host, msg +",["+ formatTime()+"]"));
	}
    private JSONObject createMsgJSONObject(Peer peer, String type, String msg) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", peer.hostname);
        //jsonObject.put("to", peer.remoteHostname);
        jsonObject.put("send_time", DateUtils.formatTime());
        jsonObject.put("msg_type", type);	//control:1 , msg:2
        jsonObject.put("msg_content", msg);
        return jsonObject;
    }
    public void testStream() throws IOException, ClassNotFoundException{
        // Sender
        List list = new ArrayList();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(out);
        outputStream.writeObject(list);
        outputStream.close();

        byte[] listData = out.toByteArray();


        // Reciever
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(listData));
        list = (List) inputStream.readObject();
    }
/*	private void sendRemoteMsg(String host, String sdp) {
		EventBus.getDefault().post(new RemoteSdpEvent(host, sdp));
	}*/

	private String receive() {
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			peer.client.socket.receive(packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new String(packet.getData(), 0, packet.getLength());
	}
    private String formatTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }
}
/*
 * Messenger messenger = (Messenger) bundle.get("messenger"); Message msg =
 * Message.obtain(); //Bundle bundle = new Bundle(); bundle.putString("text",
 * this.content); msg.setData(bundle); //put the data here messenger.send(msg);
 */
