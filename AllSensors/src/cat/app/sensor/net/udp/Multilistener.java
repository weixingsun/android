package cat.app.sensor.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

public class Multilistener {

	private static String TAG = "Multilistener";
	public static String BROADCAST_IP = "224.0.0.1"; //
	public static int PORT = 4444;
	public static String MAC, IP;
	MulticastSocket multicastSocket;
	InetAddress serverAddress;
	static Context context;
	byte[] data = new byte[1024];

	public Multilistener(Context context) {
		try {
			Multilistener.context = context;
			multicastSocket = new MulticastSocket(PORT);
			serverAddress = InetAddress.getByName(BROADCAST_IP);
			// MAC=getLocalMacAddressFromWifiInfo();
			IP = getLocalIpAddress();
			data = IP.getBytes();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		DatagramPacket pack = new DatagramPacket(data, data.length,
				serverAddress, PORT);
		try {
			multicastSocket.joinGroup(serverAddress);
			multicastSocket.receive(pack);
		} catch (IOException e) {
			Toast.makeText(context,"Multilistener Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		String serverIP = new String(pack.getData(), pack.getOffset(),
				pack.getLength());
		Log.i(TAG, "IP:" + serverIP);
	}

	public void stop() {
		try {
			multicastSocket.leaveGroup(serverAddress);
		} catch (IOException e) {
			Toast.makeText(context,"Multilistener Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public static String getLocalMacAddressFromWifiInfo() {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifi.getConnectionInfo().getMacAddress();
	}

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
}
