package cat.app.wifi;

import java.util.List;

import cat.app.osmap.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Wifi  extends BroadcastReceiver {
	private static final String tag = Wifi.class.getSimpleName();
	private Context mContext;
	private String obtainWifiInfo() {
        // Wifi�������ٶȼ��ź�ǿ�ȣ�  
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
	    WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();  
        String output = null;
        if (info.getBSSID() != null) {  
            // �����ź�ǿ��  
        	int dBm = info.getRssi();
            int strength = WifiManager.calculateSignalLevel(dBm, 5);
            // �����ٶ�  
            int speed = info.getLinkSpeed();  
            // �����ٶȵ�λ  
            String units = WifiInfo.LINK_SPEED_UNITS;
            // WifiԴ����  
            String ssid = info.getSSID();
            output = "ssid="+ssid+"\ndBm="+dBm+"\nlevel="+strength+"\nspeed="+speed+"\nunit="+units;
        }
        return output;
   }
	private String scanWifi(){
	    WifiManager wifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = wifiManager.getScanResults();  
        String otherwifi = "";
        for (ScanResult result : results) {    
            otherwifi += result.SSID  + ":" + result.level + "\n";  
        }
        return otherwifi;
	}
	public void showWifiInfo(){
		String wifiString = scanWifi(); //obtainWifiInfo();
		//Activity act = (Activity)this.mContext;
        //TextView wifiText = (TextView) act.findViewById(R.id.wifi_info);
        //wifiText.setText(wifiString);
		Toast.makeText(this.mContext, wifiString, Toast.LENGTH_LONG).show();
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		this.mContext = context;
		showWifiInfo();
		Log.i(tag, "wifi status changed");
	}

}
