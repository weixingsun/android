package cat.app.wifi;

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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Wifi {
	private static final String tag = Wifi.class.getSimpleName();
	private static final long SCAN_DELAY = 2000;
	private Activity act;
	private IntentFilter i = new IntentFilter();
	private String scanResult; 
	private Timer timer;
	private TextView wifiText;
	private WifiManager wm;
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		 public void onReceive(Context c, Intent i){
				Log.i(tag, "wifi receiver triggerred");
		  List<ScanResult> l = wm.getScanResults();
		  StringBuilder sb = new StringBuilder("Scan Results:\n");
		  sb.append("-----------------------\n");
		  for (ScanResult r : l) {
		   sb.append(r.SSID + " " + r.level + " dBM\n");
		  }
		  scanResult = sb.toString();
		  wifiText.setText(scanResult);
		 }
	};
	public Wifi(Activity act) {
		this.act = act;
		this.wifiText = (TextView) act.findViewById(R.id.wifi_info);
		this.wm = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
	}
	
	public void register(){
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask(){
			 @Override
			 public void run() {
			  WifiManager wm = (WifiManager) act.getSystemService(Context.WIFI_SERVICE);
			  if (wm.isWifiEnabled()) {
			   WifiInfo info = wm.getConnectionInfo();
			   if (info != null) {
			    //txtAssoc.setText("Associated with " + info.getSSID() +"\nat " + info.getLinkSpeed() + WifiInfo.LINK_SPEED_UNITS + " (" + info.getRssi() + " dBM)");
			   } else {
			    //txtAssoc.setText("Not currently associated.");
			   }
			   wm.startScan();
			  } else {
			   Toast.makeText(act, "WIFI is disabled.", Toast.LENGTH_LONG).show();
			  }
			 }
			}, 0, SCAN_DELAY);
			act.registerReceiver(receiver, i );
			Log.i(tag, "wifi receiver registerred");
	}
	
	public void unregister(){
		timer.cancel();
		act.unregisterReceiver(receiver);
	}
	/*public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		showWifiInfo();
		Log.i(tag, "wifi status changed");
	}
	private String scanWifi(){
	    WifiManager wifiManager = (WifiManager) this.act.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = wifiManager.getScanResults();  
        String otherwifi = "";
        for (ScanResult result : results) {    
            otherwifi += result.SSID  + ":" + result.level + "\n";  
        }
        return otherwifi;
	}
	public void showWifiInfo(){
		String wifiString = scanWifi(); //obtainWifiInfo();
		Toast.makeText(this.act, wifiString, Toast.LENGTH_LONG).show();
	}
	private String obtainWifiInfo() {
        // Wifi的连接速度及信号强度：  
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
	    WifiManager wifiManager = (WifiManager) this.act.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();  
        String output = null;
        if (info.getBSSID() != null) {  
            // 链接信号强度  
        	int dBm = info.getRssi();
            int strength = WifiManager.calculateSignalLevel(dBm, 5);
            // 链接速度  
            int speed = info.getLinkSpeed();  
            // 链接速度单位  
            String units = WifiInfo.LINK_SPEED_UNITS;
            // Wifi源名称  
            String ssid = info.getSSID();
            output = "ssid="+ssid+"\ndBm="+dBm+"\nlevel="+strength+"\nspeed="+speed+"\nunit="+units;
        }
        return output;
   }*/

}
