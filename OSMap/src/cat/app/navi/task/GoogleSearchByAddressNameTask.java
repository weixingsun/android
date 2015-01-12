package cat.app.navi.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import cat.app.osmap.LOC;

import android.app.Activity;
import android.graphics.Color;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/** 
 * AsyncTask: search list(lat,lng) by given name 
 * @author Weixing Sun
 */  
public class GoogleSearchByAddressNameTask extends
        AsyncTask<String, Void, List<Address>> {
    private static final String TAG = GoogleSearchByAddressNameTask.class.getSimpleName();
	HttpClient client;
    String url;
    String address;
    List<Address> list = new ArrayList<Address>();
    public GoogleSearchByAddressNameTask(String address) {
    	this.address=address;
        this.url = getLocationURL(address);
    }
    @Override  
    public List<Address> doInBackground(String... params) {
    	if(this.address.length()<2) return null;
    	StringBuilder sb = new StringBuilder();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            response.addHeader("Accept-Language", "zh-CN");
            HttpEntity entity = response.getEntity();
            BufferedReader bf=new BufferedReader(new InputStreamReader((entity.getContent()),"UTF-8"));
            String line = "";
            while((line=bf.readLine())!=null){
            	sb.append(line);
            }
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200 ) {
            	JSONArray posArray = new JSONObject(sb.toString()).getJSONArray("results");
            	for(int i=0;i<posArray.length()&&i<3;i++){
	            	JSONObject addressFull = posArray.getJSONObject(i);
	            	JSONObject location = addressFull.getJSONObject("geometry").getJSONObject("location");
	            	String formatted_address = addressFull.getString("formatted_address");
	            	//Log.i(TAG,"formatted_address:" + formatted_address);
	    			GeoPoint ll = new GeoPoint(location.getDouble("lat"), location.getDouble("lng"));
	    			Address addr = new Address(Locale.getDefault());
	    			//addr.setCountryCode(countryCode);
	    			list.add(addr);
            	}
    			return list;
            } else {
            	Log.w(TAG,"doInBackground:statusecode="+statusecode);
                return null;
            }
        } catch (ClientProtocolException e) {  
            Log.i(TAG, "ClientProtocolException:"+e.getMessage());
        } catch (IOException e) {  
        	Log.i(TAG, "IOException:"+e.getMessage());
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        //Log.i(TAG,"doInBackground:"+routes);
        return list;  
    }  
  
    @Override  
    protected void onPreExecute() {
        client = new DefaultHttpClient();  
        client.getParams().setParameter(  
                CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);  
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,15000);  
        super.onPreExecute();
    }  
  
    @Override  
    protected void onPostExecute(List<Address> points) {
        super.onPostExecute(points);  
        if (points == null) {
            //Toast.makeText(gmap.activity, "No route found.", Toast.LENGTH_LONG).show();
        }else{
    		//fillList();
        }
    }
    
    /** 
     * 组合成googlemap direction所需要的url
     * @param origin
     * @param dest
     * @return url
     */
    public String getLocationURL(String address) {
        String parsedValue = null;
		try {
			parsedValue = java.net.URLEncoder.encode(address, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        String sensor = "&sensor=false";
        String language = "&Accept-Language:zh-CN";
        String region = "&components=country:"+LOC.countryCode;
        String geoapi = "https://maps.googleapis.com/maps/api/geocode/";
        String format = "json";
        String addressURL = "?address="+parsedValue;
		String url = geoapi +format+addressURL+language+sensor+region;
        Log.i(TAG,"getLocationURL--->: " + url);
        return url;
    }
}