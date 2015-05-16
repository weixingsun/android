package wsn.park.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class RuntimeOptions {

	static Activity act;
	static RuntimeOptions rto;
	private RuntimeOptions(){
		init(act);
	}
	public static RuntimeOptions getInstance(Activity act) {
		RuntimeOptions.act = act;
		if(rto==null) rto = new  RuntimeOptions();
		return rto;
	}
	private boolean networkAvailable;
	public boolean isNetworkAvailable() {
		return networkAvailable;
	}
	public void setNetworkAvailable(boolean networkAvailable) {
		this.networkAvailable = networkAvailable;
	}
	public void init(Activity act){
		final ConnectivityManager conMgr = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		//.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		//.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.isAvailable()) {
			networkAvailable=true;
		} else {
			networkAvailable=false;
		}
	}
}
