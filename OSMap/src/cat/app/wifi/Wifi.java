package cat.app.wifi;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cat.app.osmap.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Wifi {
	private static final String tag = Wifi.class.getSimpleName();
	private static final long SCAN_DELAY = 1000;
	private String scanResult;
	private TextView wifiText;
	private DrawerLayout drawer;
	private Activity act;
	private WifiManager wm;
	private boolean running = false; // to avoid start two threads
	private Handler handler = new Handler();

	public Wifi(Activity act) {
		this.wifiText = (TextView) act.findViewById(R.id.wifi_info);
		this.wm = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
		drawer = (DrawerLayout) act.findViewById(R.id.drawer_layout);

		this.register();
	}

	public void register() {
		Log.w(tag, "Wifi.register()");
		if (!running)
			handler.postDelayed(runnable, SCAN_DELAY);
		running = true;
	}

	public void unregister() {
		Log.w(tag, "Wifi.unregister()");
		handler.removeCallbacks(runnable);
		running = false;
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {

			if (drawer.isDrawerOpen(GravityCompat.END)) {	//detect whether right drawer is opened.
				Log.i(tag, "wifi scanning signal");
				List<ScanResult> l = wm.getScanResults();
				Calendar c = Calendar.getInstance();
				StringBuilder sb = new StringBuilder("Scan Results:\n"+ c.getTime());
				sb.append("-----------------------\n");
				for (ScanResult r : l) {
					sb.append(r.SSID + " " + r.level + " dBM\n");
				}
				scanResult = sb.toString();
				wifiText.setText(scanResult);
			}
			/* and here comes the "trick" */
			handler.postDelayed(this, SCAN_DELAY);
		}
	};

	/*
	 * public void onReceive(Context context, Intent intent) { this.mContext =
	 * context; i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	 * showWifiInfo(); Log.i(tag, "wifi status changed"); } private String
	 * scanWifi(){ WifiManager wifiManager = (WifiManager)
	 * this.act.getSystemService(Context.WIFI_SERVICE); List<ScanResult> results
	 * = wifiManager.getScanResults(); String otherwifi = ""; for (ScanResult
	 * result : results) { otherwifi += result.SSID + ":" + result.level + "\n";
	 * } return otherwifi; } public void showWifiInfo(){ String wifiString =
	 * scanWifi(); //obtainWifiInfo(); Toast.makeText(this.act, wifiString,
	 * Toast.LENGTH_LONG).show(); } private String obtainWifiInfo() { //
	 * Wifi的连接速度及信号强度： // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	 * WifiManager wifiManager = (WifiManager)
	 * this.act.getSystemService(Context.WIFI_SERVICE); WifiInfo info =
	 * wifiManager.getConnectionInfo(); String output = null; if
	 * (info.getBSSID() != null) { // 链接信号强度 int dBm = info.getRssi(); int
	 * strength = WifiManager.calculateSignalLevel(dBm, 5); // 链接速度 int speed =
	 * info.getLinkSpeed(); // 链接速度单位 String units = WifiInfo.LINK_SPEED_UNITS;
	 * // Wifi源名称 String ssid = info.getSSID(); output =
	 * "ssid="+ssid+"\ndBm="+dBm
	 * +"\nlevel="+strength+"\nspeed="+speed+"\nunit="+units; } return output; }
	 */

}
