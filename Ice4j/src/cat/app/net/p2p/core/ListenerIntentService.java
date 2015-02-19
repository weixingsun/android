package cat.app.net.p2p.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import cat.app.net.p2p.Ear;
import cat.app.net.p2p.db.DbHelper;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import cat.app.net.p2p.eb.RemoteSdpEvent;
import cat.app.net.p2p.eb.SdpEvent;

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
	private void init() {
		if (peer == null) {
			try {
				peer = Peer.getInstance();
				// this.socket = peer.client.getDatagramSocket();
				// Log.i(tag,">>>>>>>>>>>>>>>>>>>>>>>>>SOCKET:"+socket.toString());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		init();
		sendLocalMsg(peer.hostname, peer.client.localSdp);
		loopForRemoteSdp(peer.remoteHostname);
		peer.client.initConnection(Ear.remoteSdp);
		loopForNextMessage();
	}
	public void loopForRemoteSdp(String host){
		Log.i(tag, "waiting for sdp from host="+host);
		while (!Ear.hearRemoteSdp) {
			try {
				Thread.sleep(5000);
				Ear.triggerHearRemoteSdp(host);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void loopForNextMessage(){
		while (true) {
			try {
				this.content = receive();
				Thread.sleep(5000);	// TimeUnit.MILLISECONDS.sleep(2000);
				//Log.i(tag, "received message:"+this.content);
				receiveMsg(peer.remoteHostname,this.content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void sendLocalMsg(String host, String sdp) {
		EventBus.getDefault().post(new SdpEvent(host, sdp));
	}
	private void receiveMsg(String host, String msg) {
		EventBus.getDefault().post(new ReceiveDataEvent(host, msg));
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

}
/*
 * Messenger messenger = (Messenger) bundle.get("messenger"); Message msg =
 * Message.obtain(); //Bundle bundle = new Bundle(); bundle.putString("text",
 * this.content); msg.setData(bundle); //put the data here messenger.send(msg);
 */
