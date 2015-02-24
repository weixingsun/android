package cat.app.net.p2p;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cat.app.net.p2p.core.Peer;
import cat.app.net.p2p.eb.MessageEvent;
import cat.app.net.p2p.eb.RemoteSdpEvent;
import cat.app.net.p2p.eb.SdpEvent;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.util.Log;

public class Ear {

	private static final String tag = Ear.class.getSimpleName();
	private static RequestQueue rQueue;
	
	public static boolean hearRemoteSdp = false;
	public static Map<String,String> remoteHosts = new HashMap<String,String>();
	public Ear(Activity act) {
		rQueue = Volley.newRequestQueue(act);
		EventBus.getDefault().register(this);
	}

	public void onEvent(SdpEvent event) {
		Log.i(tag, "EventBus received SDP event:" + event.getHost());
		removeAllSdps();//event.getHost(), event.getSdp(), event.getGroup()
	}

	public void removeAllSdps() {//final String hostname, final String localSdp, final String group
		//String baseurl = "http://www.servicedata.vhostall.com/p2p/insert_p2p.php";
		String baseurl = "http://www.servicedata.vhostall.com/p2p/delete_p2p.php";
		Response.Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				//Log.d("TAG", response.toString());
			}
		};
		Response.ErrorListener errorListener = new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("TAG", error.getMessage(), error);
			}
		};
		StringRequest request = new StringRequest(Method.POST, baseurl,
				listener, errorListener) {
			/*@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("host", hostname);
				map.put("sdp", localSdp);
				map.put("group", group);
				return map;
			}*/
		};
		rQueue.add(request);
		//Log.w(tag, "hostname="+hostname);
	}
	
	public static void cleanupLocalSdp() {
		final Peer p = Peer.getInstance();
		String encodedHostname="";
		try {
			encodedHostname = URLEncoder.encode(p.hostname, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = "http://www.servicedata.vhostall.com/p2p/delete_p2p.php?group="+p.group+"&host="+encodedHostname;
		Log.i(tag, "clean_url="+url);
		StringRequest request = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.i(tag, "host "+p.hostname+":"+response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(tag, error.getMessage(), error);
					}
				});
		rQueue.add(request);
		//Log.i(tag, "deleting host "+p.hostname);
	}
	public static void downloadRemoteSdp(final String group,final String localhost,final String localSdp) {
		String baseurl = "http://www.servicedata.vhostall.com/p2p/select_p2p_json.php";
		String url = baseurl;
		//Log.i(tag,"url="+url);
		StringRequest request = new StringRequest(Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//Log.i(tag,"response="+response);
						extractHostSdpFromJSON(response);
						EventBus.getDefault().post(new RemoteSdpEvent(remoteHosts));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(tag, error.getMessage(), error);
					}
				})
		{

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("host", localhost);
				map.put("sdp", localSdp);
				map.put("group", group);
				return map;
			}
		};
		//Log.i(tag, "url="+url);
		rQueue.add(request);
	}
	
	private static void extractHostSdpFromJSON(String response){
		remoteHosts.clear();
		Peer p = Peer.getInstance();
		try {
			JSONArray posArray = new JSONObject(response).getJSONArray("result");
	    	//Log.i(tag,"Array.length():" + posArray.length());  // last comma contains an array slot
	    	for(int i=0;i<posArray.length()-1;i++){
				//Log.i(tag, "posArray="+posArray.length());
	        	JSONObject row = posArray.getJSONObject(i);
	    		String host = row.getString("hostname");
	        	String sdp = row.getString("sdp");
	        	if(!p.hostname.equals(host)){
	        		remoteHosts.put(host, sdp);
	        	}
	    	}
	    	//Object myKey = remoteHosts.keySet().toArray()[0];
        	//Log.w(tag, "extractHostSdpFromJSON():host="+myKey+ ",sdp="+remoteHosts.get(myKey));
		} catch (JSONException e) {
			//e.printStackTrace();
		}
	}
}
