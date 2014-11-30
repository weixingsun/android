package cat.app.sensor.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import cat.app.sensor.db.DbHelper;

import android.util.Log;

public class UDPHandshakeListener implements Runnable {
	private final static String TAG = "AllSensors.UDPListener";
    public static String ServerIP;
    private static Thread t = new Thread(new UDPHandshakeListener());
    private static int PORT;
    private byte[] msg = new byte[1024];
    private static boolean listening = false;
    public static void startListen(int port) {
        PORT = port;
        listening=true;
        t.start();
    }

    @Override
    public void run() {
        DatagramSocket dSocket = null;
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        Log.i(TAG, "listener started: ");
        try {
            dSocket = new DatagramSocket(PORT);
            while (listening) {
                try {
                    dSocket.receive(packet);//×èÈû·½·¨
	                Thread.sleep(1000);
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                    ServerIP=new String(data);
                    UDPClientBroadcastThread.interrupt();
                    DbHelper db =DbHelper.getInstance();
                    db.updateServerInfo(ServerIP,PORT);
                    Log.i(TAG, "sever IP received:"+ServerIP);
                    listening=false;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally{
        	dSocket.close();
        }
    }

}

