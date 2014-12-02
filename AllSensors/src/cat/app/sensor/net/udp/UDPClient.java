package cat.app.sensor.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import cat.app.sensor.db.DbHelper;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UDPClient extends IntentService {
	private static boolean connected=false;
	public UDPClient() {
		super(TAG);
	}

	private final  static String TAG = "UDPClient";
    //private static int PORT;
    private static byte[] msg = new byte[1024];
	//private static boolean running=true; //shared with all threads
    //private static boolean listening = false;
    private static int INTERVAL_MIN = 1;
    public  static String ServerIP;
    static DatagramPacket packet = new DatagramPacket(msg, msg.length);
    public static void startListen(int port) {
    	Log.i(TAG, "startListen ("+port+")");
        try {
			listen(port,packet);
			Thread.sleep(1000);
			connected=true;
		} catch (IOException e) {
			Log.i(TAG, "startListener failed: "+e.getMessage());
		} catch (InterruptedException e) {
			Log.i(TAG, "Listener interrupted: "+e.getMessage());
		}
    }
    
    public static void startSend(String ip, int port) {
    	Log.i(TAG, "startSend "+ip+"("+port+")");
        send(ServerIP,port,"sensor data in json");
        try {
        	Thread.sleep(INTERVAL_MIN*60*1000);
        } catch (InterruptedException e) {
		} 
    }
    /*public void run(int port) {
	    Log.i(TAG, "listener started: ");
	    while (running&&!Thread.currentThread().isInterrupted()) {
	        try {
	        	Thread.sleep(INTERVAL_MIN*60*1000);
	        } catch (InterruptedException e) {
	        	running=false;
			} 
	    }
    }
    public static void interrupt(){
		if(Thread.currentThread()!=null&& Thread.currentThread().isAlive()){
			Thread.currentThread().interrupt();
		}
    	running = false;
    }*/
     
  private static void listen(int port,DatagramPacket packet) throws IOException {
	  DatagramSocket socket = new DatagramSocket(port);
	  socket.receive(packet);//阻塞方法
	  ServerIP=new String(packet.getData(), packet.getOffset(), packet.getLength());
	  socket.close();
      //UDPClientMulticaster.interrupt();
      DbHelper db =DbHelper.getInstance();
      db.updateServerInfo(ServerIP,port);
      Log.i(TAG, "sever IP received:"+ServerIP);
	}
  //byte[] data = new byte[packet.getLength()];
  //System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
  //ServerIP=new String(data);
  
    public static void send(String ip,int port,String msg) {
        InetAddress net;
		try {
			net = InetAddress.getByName(ip);
	        DatagramSocket socket = new DatagramSocket(port);
	        int msg_len = msg == null ? 0 : msg.length();
	        DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len,net, port);
	        Log.i(TAG, "Sending sensor data to server:"+ip+":"+port);
	    	socket.send(dPacket);
	    	socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	@Override
	protected void onHandleIntent(Intent intent) {
		//i.putExtra("port", SERVER_PORT);
		int port = intent.getIntExtra("port", 0);
		Log.i(TAG, "startListen to port:"+port);
		if(!connected)
			startListen(port);
		startSend(ServerIP, port);
	}
	public boolean isServiceRunning(Context context){
		  ActivityManager am = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
		  List<RunningServiceInfo> list = am.getRunningServices(30);
		  for(RunningServiceInfo info : list){
			  if(info.service.getClassName().equals("service的全称（一般为包名+service类的名称）")){
				  return true;
			  }
		  }
		  return false;
		} 
}