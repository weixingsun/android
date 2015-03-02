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

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.video.VideoQuality;

import org.json.JSONException;
import org.json.JSONObject;

import cat.app.net.p2p.Ear;
import cat.app.net.p2p.R;
import cat.app.net.p2p.db.DbHelper;
import cat.app.net.p2p.db.DbTask;
import cat.app.net.p2p.eb.ReceiveDataEvent;
import cat.app.net.p2p.eb.RemoteSdpEvent;
import cat.app.net.p2p.eb.SdpEvent;
import cat.app.net.p2p.eb.StatusEvent;
import cat.app.net.p2p.util.DateUtils;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import net.majorkernelpanic.streaming.gl.SurfaceView;

public class VideoIntentService extends IntentService implements Session.Callback{

	private static final String tag = VideoIntentService.class.getSimpleName();
	byte[] buf = new byte[1024];
	// IceClient client=null;
	Peer peer;
	String content; 
	DatagramSocket socket;
	private SurfaceView mSurfaceView;
	
	public VideoIntentService(IceClient client,Activity act) {
		super("");
		this.socket = client.socket;
		this.mSurfaceView = (SurfaceView) act.findViewById(R.id.surface);
		DbHelper.getInstance().createTables();
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		//this.startService(new Intent(this,RtspServer.class));
		Session mSession = SessionBuilder.getInstance()
		        .setCallback(this)
		        .setSurfaceView(mSurfaceView)
		        .setPreviewOrientation(90)
		        .setContext(getApplicationContext())
		        .setAudioEncoder(SessionBuilder.AUDIO_NONE)
		        .setAudioQuality(new AudioQuality(16000, 32000))
		        .setVideoEncoder(SessionBuilder.VIDEO_H264)
		        .setVideoQuality(new VideoQuality(320,240,20,500000))
		        .build(this.socket);
		mSession.start();
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
	@Override
	public void onBitrateUpdate(long bitrate) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSessionError(int reason, int streamType, Exception e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onPreviewStarted() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSessionConfigured() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSessionStarted() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSessionStopped() {
		// TODO Auto-generated method stub
		
	}
}
