package cat.app.sensor.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

import cat.app.sensor.db.DbHelper;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class UDPClient extends IntentService {
	private static boolean connected = false;
	//private static boolean multicasting=true; //shared with all threads
	private static String MULTICAST_IP = "224.0.0.1";//(224.0.0.0,239.255.255.255)
	//private static String MAC, localIP;
	private MulticastSocket multicastSocket;
	private InetAddress serverAddress;
	
	public UDPClient() {
		super(TAG);
	}

	private final static String TAG = "UDPClient";
	private static byte[] msg = new byte[128];
	byte[] data ;
	private static int INTERVAL_MIN = 1;
	private static String ServerIP;
	private static DatagramPacket packet = new DatagramPacket(msg, msg.length);

	public static void startListen(int port) {
		//Log.i(TAG, "startListen (" + port + ")");
		listen(port, packet);
	}
	public void multicastInit(int multiport) {
		try {
			multicastSocket = new MulticastSocket(multiport);
			multicastSocket.setTimeToLive(1);
			serverAddress = InetAddress.getByName(MULTICAST_IP);
			// MAC=getLocalMacAddressFromWifiInfo();
			//localIP = getLocalIpAddress();
			//data = ("C:"+getLocalIpAddress()).getBytes();
			multicastSocket.joinGroup(serverAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void multicastStop(){
		try {
			multicastSocket.leaveGroup(serverAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		multicastSocket.close();
	}
	public void multicast(int port) {
		multicastInit(port);
		data = ("C:"+getLocalIpAddress()).getBytes();
		DatagramPacket pack = new DatagramPacket(data, data.length, serverAddress, port);
		try {
			multicastSocket.send(pack);
			Log.i(TAG, "multicasting:"+new String(data)+" to "+MULTICAST_IP+":"+port);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			multicastStop();
		}
	}
	private static int TIMEOUT = 3*1000;
	public static void setTimeOut(int timeout){
		TIMEOUT = timeout;
	}
	private static void listen(int port, DatagramPacket packet) {
		DatagramSocket socket = null;
		try {
		socket = new DatagramSocket(port);
		socket.setSoTimeout(TIMEOUT); 
		}catch (SocketException e) {
			Log.i(TAG, "SocketException in listen():" + e.getMessage());
		}
		try {
			socket.receive(packet);
			ServerIP = new String(packet.getData(), packet.getOffset(),packet.getLength());
			connected = true;
			DbHelper db = DbHelper.getInstance();
			db.updateServerInfo(ServerIP, port);
			Log.i(TAG, "sever IP received:" + ServerIP);
		}catch (SocketTimeoutException e) {
			Log.w(TAG, "SocketTimeoutException in listen():" + e.getMessage());
			//e.printStackTrace();
		} catch (IOException e) {
			Log.w(TAG, "IOException in listen():" + e.getMessage());
			//e.printStackTrace();
		} finally{
			socket.close();
		}
	}

	public static void startSend(String ip, int port) {
		send(ServerIP, port, "sensor data in json");
	}

	public static void send(String ip, int port, String msg) {
		InetAddress net;
		try {
			net = InetAddress.getByName(ip);
			DatagramSocket socket = new DatagramSocket(port);
			int msg_len = msg == null ? 0 : msg.length();
			DatagramPacket dPacket = new DatagramPacket(msg.getBytes(),
					msg_len, net, port);
			Log.i(TAG, "Sending sensor data to server:" + ip + ":" + port);
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

	public boolean isServiceRunning(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = am.getRunningServices(30);
		for (RunningServiceInfo info : list) {
			if (info.service.getClassName().equals(
					"service的全称（一般为包名+service类的名称）")) {
				return true;
			}
		}
		return false;
	}
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}

		return null;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		int port = intent.getIntExtra("port", 0);
		int multiport = intent.getIntExtra("multiport",0);
		
		if(!connected){
			Log.i(TAG, "multicast to port:"+multiport+", startListen on port:" + port);
			multicast(multiport);
			startListen(port);
		}else{
			Log.i(TAG, "sending data to:"+ServerIP);
			startSend(ServerIP, port);
		}
	}

}