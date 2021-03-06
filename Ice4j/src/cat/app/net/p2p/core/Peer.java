package cat.app.net.p2p.core;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import cat.app.net.p2p.eb.MessageEvent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import de.greenrobot.event.EventBus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import android.provider.MediaStore.Video;
import android.provider.Settings.Secure;


public class Peer {
	private static Peer peer = new Peer();
	public static Peer getInstance(){
		return peer;
	}
    public IceClient client;
	public String hostname;
	public String sdp;
	//String deviceName;
	public String group;
	public String remoteHostname;
	public String remoteSdp;
    private Peer(){
        client = new IceClient(8888, "data");  //video/audio/data/text
        try {
			client.init();
	        //client.exchangeSdpWithPeer();
	        //client.startConnect();
	        //socket = client.getDatagramSocket();
	        //remoteAddress = client.getRemotePeerSocketAddress();
			hostname = getDeviceName();		//Asus_Nexus7_059b4562
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
    public void send(String msg){
    	SenderTask task = new SenderTask(client, msg);
		task.execute();
    }

    public void send(File file){
    	SenderTask task = new SenderTask(client, file.toString());
		task.execute();
    }
    
    /*public void startRecieverService(Activity act){
        //bind service  
        Intent intent = new Intent(act, ListenerIntentService.class);  
        act.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    	//act.startService(new Intent(act,ListenerIntentService.class));
    }
	public void startSenderTask(){
    	
    }*/
    @SuppressLint("NewApi") 
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = (Build.MODEL+"_"+Build.SERIAL).replaceAll(" ", "");
        
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + "_" + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    } 
}

