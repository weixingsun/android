package cat.app.sensor.net.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import cat.app.sensor.db.DbHelper;

import android.util.Log;

public class UDPClient implements Runnable {
	private final  static String TAG = "AllSensors.UDPClient";
    private static Thread t = null;
    private static int PORT;
    private byte[] msg = new byte[1024];
	private static boolean running=true; //shared with all threads
    private static boolean listening = false;
    private static int INTERVAL_MIN = 1;
    public  static String ServerIP;
    
    public static void startListen(int port) {
    	t = new Thread(new UDPClient());
        PORT = port;
        listening=true;
        t.start();
    }
    public static void startSend(String ip, int port) {
    	t = new Thread(new UDPClient());
        PORT = port;
        listening=true;
        t.start();
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        Log.i(TAG, "listener started: ");
        try {
        	socket = new DatagramSocket(PORT);
            while (running&&!Thread.currentThread().isInterrupted()) {
                try {
                	if(listening){
                		listen(socket,packet);
                	}else{
                		send(socket,ServerIP,PORT,"sensor data in json");
                	}
                    Thread.sleep(INTERVAL_MIN*60*1000);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally{
        	socket.close();
        }
    }
    public static void interrupt(){
		if(Thread.currentThread()!=null&& Thread.currentThread().isAlive()){
			Thread.currentThread().interrupt();
		}
    	running = false;
    }
     
  private void listen(DatagramSocket socket,DatagramPacket packet) throws IOException, InterruptedException {
	  socket.receive(packet);//×èÈû·½·¨
	  ServerIP=new String(packet.getData(), packet.getOffset(), packet.getLength());
      //UDPClientMulticaster.interrupt();
      DbHelper db =DbHelper.getInstance();
      db.updateServerInfo(ServerIP,PORT);
      Log.i(TAG, "sever IP received:"+ServerIP);
      listening=false;
      Thread.sleep(1000);
	}
  //byte[] data = new byte[packet.getLength()];
  //System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
  //ServerIP=new String(data);
  
	//return 0 -> success, 1-> fail
    public static int send(DatagramSocket socket,String ip,int port,String msg) {
        InetAddress net = null;
        try {
            net = InetAddress.getByName(ip);
        	socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int msg_len = msg == null ? 0 : msg.length();
        DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len,net, port);
        Log.i(TAG, "Sending sensor data to server:"+ip+":"+PORT);
        try {
        	socket.send(dPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
        	socket.close();
        }
        return 0;
    }
}

