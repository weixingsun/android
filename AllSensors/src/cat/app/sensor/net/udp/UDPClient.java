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
    private static Thread t = new Thread(new UDPClient());
    private static int PORT;
    private byte[] msg = new byte[1024];
    private static boolean listening = false;
    //private static DatagramSocket dSocket = null;
    public  static String ServerIP;
    
    public static void startListen(int port) {
        PORT = port;
        listening=true;
        t.start();
    }
    public static void startSend(String ip, int port) {
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
            while (!Thread.currentThread().isInterrupted()) {
                try {
                	if(listening){
                		listen(socket,packet);
                	}else{
                		send(socket,ServerIP,PORT,"sensor data in json");
                	}
                    
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
     
  private void listen(DatagramSocket socket,DatagramPacket packet) throws IOException, InterruptedException {
	  socket.receive(packet);//阻塞方法
      byte[] data = new byte[packet.getLength()];
      System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
      ServerIP=new String(data);
      //UDPClientMulticaster.interrupt();
      DbHelper db =DbHelper.getInstance();
      db.updateServerInfo(ServerIP,PORT);
      Log.i(TAG, "sever IP received:"+ServerIP);
      listening=false;
      Thread.sleep(1000);
	}
	//return 0 -> success, 1-> fail
    public static int send(DatagramSocket socket,String ip,int port,String msg) {
        //StringBuilder sb = new StringBuilder();
        System.out.println("ready to send message to: " + ip);
        InetAddress local = null;
        try {
            local = InetAddress.getByName(ip);
            //sb.append("Server found, connecting...").append("/n");
        } catch (UnknownHostException e) {
            //sb.append("Server not found.").append("/n");
            e.printStackTrace();
        }
        try {
        	socket = new DatagramSocket(); // 注意此处要先在配置文件里设置权限,否则会抛权限不足的异常
            //sb.append("正在连接服务器...").append("/n");
        } catch (SocketException e) {
            e.printStackTrace();
            //sb.append("Failed to connect to Server.").append("/n");
        }
        int msg_len = msg == null ? 0 : msg.length();
        DatagramPacket dPacket = new DatagramPacket(msg.getBytes(), msg_len,local, port);
        System.out.println("准备发送数据到："+ip);
        Log.i(TAG, "Sending sensor data to server:"+ip+":"+PORT);
        try {
        	socket.send(dPacket);
            //sb.append("消息发送成功!").append("/n");
        } catch (IOException e) {
            e.printStackTrace();
            //sb.append("消息发送失败.").append("/n");
        }
        socket.close();
        return 0;
    }
}

