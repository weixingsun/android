package cat.app.net.p2p.core;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.ice4j.pseudotcp.PseudoTcpSocket;
import org.ice4j.pseudotcp.PseudoTcpSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

import cat.app.net.p2p.util.DateUtils;


import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class SenderTask extends AsyncTask<String, Void, String> {

    private static final String tag = SenderTask.class.getSimpleName();
	byte[] buf = new byte[1024];
	//IceClient client;
    DatagramSocket socket;
    SocketAddress remoteAddress;
	private String msg;
	private File file;
	public SenderTask(IceClient client, String msg) {
		try {
			//this.client = client;
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
	public SenderTask(IceClient client, File file) {
		try {
			this.file = file;
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
		this.sendUDPMsg(this.msg);
		return null;
	}
	public void sendUDPMsg(String msg) {
             try {
                 byte[] buf = (msg+"["+DateUtils.formatTime()+"]").getBytes();
                 DatagramPacket packet = new DatagramPacket(buf,buf.length);
                 packet.setSocketAddress(remoteAddress);
                 socket.send(packet);
                 Log.i(tag,"sent "+remoteAddress+"======"+msg);
                 //TimeUnit.SECONDS.sleep(10);
             } catch (Exception e) {
                  e.printStackTrace();
             }
	}
	public void sendTCPMsg(String msg){
        try {
            byte[] buf = (msg).getBytes();
			PseudoTcpSocket tcpSocket = new PseudoTcpSocketFactory().createSocket(socket);
			tcpSocket.setConversationID(1073741824);
			tcpSocket.setMTU(1500);
            tcpSocket.setDebugName("Sender");
            tcpSocket.getOutputStream().write(buf);
            tcpSocket.getOutputStream().flush();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
