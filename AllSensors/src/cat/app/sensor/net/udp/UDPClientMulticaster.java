package cat.app.sensor.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class UDPClientMulticaster implements Runnable{
	private final static String TAG = "AllSensors.UDPClientMulticaster";
    private static Thread t = null;
	private static int MULTICAST_NUMBER = 5;
	private static int MULTICAST_TRIED = 0;
	public static String MULTICAST_IP = "224.0.0.1";//(224.0.0.0,239.255.255.255)
	public static int MULTICAST_PORT = 4444;
	public static String MAC, localIP;
	MulticastSocket multicastSocket;
	InetAddress serverAddress;
	//static Context context;
	byte[] data; // = new byte[1024];
	//private byte[] msg = new byte[1024];

	boolean connected=false;
	private static boolean running=true;

	public UDPClientMulticaster(int port) {
		try {
			//UDPClientMulticaster.context = context;
			MULTICAST_PORT=port;
			multicastSocket = new MulticastSocket(port);
			multicastSocket.setTimeToLive(1);
			serverAddress = InetAddress.getByName(MULTICAST_IP);
			// MAC=getLocalMacAddressFromWifiInfo();
			localIP = getLocalIpAddress();
			data = ("C:"+localIP).getBytes();
			multicastSocket.joinGroup(serverAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void startMulticast(int port) {
    	t = new Thread(new UDPClientMulticaster(port));
        t.start();
    }
	public void multicast() {
		DatagramPacket pack = new DatagramPacket(data, data.length, serverAddress, MULTICAST_PORT);
		try {
			multicastSocket.send(pack);
			Log.i(TAG, "multicast("+MULTICAST_TRIED+"/"+MULTICAST_NUMBER+"):"+new String(data)+" to "+MULTICAST_IP+":"+MULTICAST_PORT);
		} catch (IOException e) {
			//Toast.makeText(context,"Multicaster Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			multicastSocket.leaveGroup(serverAddress);
			multicastSocket.close();
		} catch (IOException e) {
			//Toast.makeText(context,"Multicaster Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	/*public static String getLocalMacAddressFromWifiInfo() {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifi.getConnectionInfo().getMacAddress();
	}*/

	/*
	 * public String getIPAddress(Context context){ WifiManager wifi_service =
	 * (WifiManager) context.getSystemService(Context.WIFI_SERVICE); DhcpInfo
	 * dhcpInfo = wifi_service.getDhcpInfo(); WifiInfo wifiinfo =
	 * wifi_service.getConnectionInfo();
	 * System.out.println("Wifi info----->"+wifiinfo.getIpAddress());
	 * System.out.
	 * println("DHCP info gateway----->"+Formatter.formatIpAddress(dhcpInfo
	 * .gateway));
	 * System.out.println("DHCP info netmask----->"+Formatter.formatIpAddress
	 * (dhcpInfo.netmask));
	 * //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址 return
	 * Formatter.formatIpAddress(dhcpInfo.ipAddress); }
	 */
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
	public void run() {
		while(running&&!Thread.currentThread().isInterrupted()){
			if(!Thread.currentThread().isInterrupted()){
				try {
	                this.multicast();
	                Thread.sleep(10000);
	                if(MULTICAST_TRIED>MULTICAST_NUMBER) break;
	                MULTICAST_TRIED++;
	            } catch (InterruptedException e) {
	                Thread.currentThread().interrupt();
	            }
			}else{
				
			}
		}
		stop();
	}
	public static void interrupt(){
		if(Thread.currentThread()!=null&& Thread.currentThread().isAlive()){
			Thread.currentThread().interrupt();
		}
		running=false;
    }
}
