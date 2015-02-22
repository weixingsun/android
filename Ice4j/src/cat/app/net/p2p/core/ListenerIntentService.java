package cat.app.net.p2p.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cat.app.net.p2p.Ear;
import cat.app.net.p2p.db.DbHelper;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import cat.app.net.p2p.eb.RemoteSdpEvent;
import cat.app.net.p2p.eb.SdpEvent;
import cat.app.net.p2p.eb.StatusEvent;

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
			Log.i(tag, "waiting for sdp from group="+group);
			try {
				Thread.sleep(5000);
				Ear.triggerHearRemoteSdp(group);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
	public void loopForNextMessage(){
		while (true) {
			try {
				this.content = receive();
				Thread.sleep(5000);	// TimeUnit.MILLISECONDS.sleep(2000);
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
		EventBus.getDefault().post(new ReceiveDataEvent(host, msg +",received at:"+ formatTime()));
		DbHelper.getInstance().insertMsg(host, msg);
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
