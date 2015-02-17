package cat.app.net.p2p.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;


import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class SenderTask extends AsyncTask<String, Void, String> {

    private static final String tag = SenderTask.class.getSimpleName();
	byte[] buf = new byte[1024];
    DatagramSocket socket;
    SocketAddress remoteAddress;
	private String msg;
	public SenderTask(IceClient client, String msg) {
		try {
			this.msg = msg;
			this.socket = client.socket;
			this.remoteAddress = client.remoteAddress;
			//this.socket = client.getDatagramSocket();
			//this.remoteAddress = client.getRemotePeerSocketAddress();
	        //Log.w(tag,">>>>>>>>>>>>>>>>>>>>>>>>>SOCKET:"+socket.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		this.send(this.msg);
		return null;
	}
	public void send(String msg) {
             try {
                 byte[] buf = (msg).getBytes();
                 DatagramPacket packet = new DatagramPacket(buf,buf.length);
                 packet.setSocketAddress(remoteAddress);
                 socket.send(packet);
                 Log.i(tag,"sent "+remoteAddress+"======"+msg);
                 //TimeUnit.SECONDS.sleep(10);
             } catch (Exception e) {
                  e.printStackTrace();
             }
	}


}
