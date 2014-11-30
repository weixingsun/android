package cat.app.sensor.net.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UDPBroadcaster {

	private static String LOG_TAG = "WifiBroadcastActivity";
	private boolean start = true;
	private EditText IPAddress;
	private String address;
	public static final int DEFAULT_PORT = 43708;
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
	Button startButton;
	Button stopButton;

	public void onCreate(Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);

		// IPAddress = (EditText) this.findViewById(R.id.address );
		// startButton = (Button) this.findViewById(R.id.start);
		// stopButton = (Button) this.findViewById(R.id.stop);
		// startButton.setEnabled(true);
		// stopButton.setEnabled(false);

		address = getLocalIPAddress();
		if (address != null) {
			IPAddress.setText(address);
		} else {
			IPAddress.setText("Can not get IP address");

			return;
		}
		// EditText numberEdit = (EditText) findViewById(R.id.number);
		// numberEdit.setText(number);

		startButton.setOnClickListener(listener);
		stopButton.setOnClickListener(listener);
	}

	private String number = getRandomNumber();
	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == startButton) {
				start = true;
				new BroadCastUdp(number).start();
				startButton.setEnabled(false);
				stopButton.setEnabled(true);
			} else if (v == stopButton) {
				start = false;
				startButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
		}
	};

	private String getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {

				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e(LOG_TAG, ex.toString());
		}

		return null;
	}

	private String getRandomNumber() {
		int num = new Random().nextInt(65536);
		String numString = String.format("x", num);
		return numString;
	}

	public class BroadCastUdp extends Thread {
		private String dataString;
		private DatagramSocket udpSocket;

		public BroadCastUdp(String dataString) {
			this.dataString = dataString;
		}

		public void run() {
			DatagramPacket dataPacket = null;

			try {
				udpSocket = new DatagramSocket(DEFAULT_PORT);

				dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
				byte[] data = dataString.getBytes();
				dataPacket.setData(data);
				dataPacket.setLength(data.length);
				dataPacket.setPort(DEFAULT_PORT);

				InetAddress broadcastAddr;

				broadcastAddr = InetAddress.getByName("255.255.255.255");
				dataPacket.setAddress(broadcastAddr);
			} catch (Exception e) {
				Log.e(LOG_TAG, e.toString());
			}

			while (start) {
				try {
					udpSocket.send(dataPacket);
					sleep(10);
				} catch (Exception e) {
					Log.e(LOG_TAG, e.toString());
				}
			}

			udpSocket.close();
		}
	}
}