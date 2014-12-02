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
        running=true;
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
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        Log.i(TAG, "listener started: ");
            while (running&&!Thread.currentThread().isInterrupted()) {
                try {
                	if(listening){
                		listen(PORT,packet);
                	}else{
                		send(ServerIP,PORT,"sensor data in json");
                	}
                    Thread.sleep(INTERVAL_MIN*60*1000);
                } catch (IOException e) {
                    e.printStackTrace();
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
    }
     
  private void listen(int port,DatagramPacket packet) throws IOException, InterruptedException {
	  DatagramSocket socket = new DatagramSocket(port);
	  socket.receive(packet);//×èÈû·½·¨
	  ServerIP=new String(packet.getData(), packet.getOffset(), packet.getLength());
	  socket.close();
      //UDPClientMulticaster.interrupt();
      DbHelper db =DbHelper.getInstance();
      db.updateServerInfo(ServerIP,PORT);
      Log.i(TAG, "sever IP received:"+ServerIP);
      listening=false;
      Thread.sleep(500);
	}
  //byte[] data = new byte[packet.getLength()];
  //System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
  //ServerIP=new String(data);
  
	//return 0 -> success, 1-> fail
    public static int send(String ip,int port,String msg) throws IOException {
        InetAddress net = InetAddress.getByName(ip);
        DatagramSocket socket = new DatagramSocket(port);
        int msg_len = msg == null ? 0 : msg.length();
        DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len,net, port);
        Log.i(TAG, "Sending sensor data to server:"+ip+":"+PORT);
    	socket.send(dPacket);
    	socket.close();
        return 0;
    }
}