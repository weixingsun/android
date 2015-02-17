package cat.app.net.p2p;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	public static String remoteSdp;
	public Ear(Activity act) {
		rQueue = Volley.newRequestQueue(act);
		EventBus.getDefault().register(this);
	}

	public void onEvent(SdpEvent event) {
		Log.i(tag, "EventBus received SDP event:" + event.getHost());
		sendLocalSdp(event.getHost(), event.getSdp());
	}
	public void onEvent(RemoteSdpEvent event) {
		//Log.i(tag, "EventBus received RemoteSDP event:" + event.getHost());
		if(event.getSdp() !=null && event.getSdp().length()>10) {
			Ear.hearRemoteSdp = true;
			//Log.e(tag, "sdp="+event.getSdp());
			remoteSdp = event.getSdp();
		}
	}

	public void sendLocalSdp(final String hostname, final String localSdp) {
		String baseurl = "http://www.servicedata.vhostall.com/p2p/insert_p2p.php";
		Response.Listener<String> listener = new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("TAG", response.toString());
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
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("host", hostname);
				map.put("sdp", localSdp);
				map.put("group", "1");
				return map;
			}
		};
		rQueue.add(request);
		//Log.w(tag, "hostname="+hostname);
	}

	public static void cleanupLocalSdp() {
		//String baseurl = "http://www.servicedata.vhostall.com/p2p/delete_p2p.php?host="+localHostname;
		
	}
	public static void triggerHearRemoteSdp(final String hostname) {
		String baseurl = "http://www.servicedata.vhostall.com/p2p/select_p2p_json.php";
		String params = "?host=";
		String encodedHostname = "";
		try {
			encodedHostname = URLEncoder.encode(hostname, "utf-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}
		String url = baseurl + params +encodedHostname; 
		//Log.i(tag, "url="+url);
		//JsonObjectRequest
		StringRequest request = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						//Log.w(tag, response.toString());
						String sdp = getSdpFromJSON(response);
						EventBus.getDefault().post(new RemoteSdpEvent(hostname,sdp));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(tag, error.getMessage(), error);
					}
				});
		rQueue.add(request);
	}

	private static String getSdpFromJSON(String response) {
		String sdp = "";
		try {
			JSONArray posArray = new JSONObject(response).getJSONArray("result");
	    	//Log.i(tag,"Array.length():" + posArray.length());  // last comma contains an array slot
	    	for(int i=0;i<posArray.length()-1;i++){
	        	JSONObject row = posArray.getJSONObject(i);
	        	//String hostname = row.getString("hostname");
	        	sdp = row.getString("sdp");
	    	}
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		return sdp;
	}


	/*
	 * public void onEvent(MessageEvent event) { 
	 * Log.i(tag,"EventBus received event:" + event.getMessage()); 
	 * }
	 */
}
