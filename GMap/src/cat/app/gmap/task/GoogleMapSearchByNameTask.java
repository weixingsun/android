package cat.app.gmap.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import cat.app.gmap.GMap;
import cat.app.gmap.adapter.SuggestListAdapter;
import cat.app.gmap.model.SuggestPoint;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;


/** 
 * 自定义class通过AsyncTask机制异步请求获取位置数据
 * @author Weixing Sun
 *  
 */  
public class GoogleMapSearchByNameTask extends
        AsyncTask<String, Void, List<SuggestPoint>> {
    private static final String TAG = "GMap.GoogleMapConverterTask";
	HttpClient client;  
    String url;
    String address;
    GMap gmap;
    public GoogleMapSearchByNameTask(GMap gmap,String address) {
    	this.address=address;
    	this.gmap = gmap;
        this.url = getLocationURL(address);
    }
    @Override  
    public List<SuggestPoint> doInBackground(String... params) {
    	gmap.suggestPoints.clear();
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
	    			LatLng ll = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
	    			SuggestPoint sp = new SuggestPoint(ll,formatted_address);
	    			gmap.suggestPoints.add(sp);
            	}
    			return gmap.suggestPoints;
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
        return gmap.suggestPoints;  
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
    protected void onPostExecute(List<SuggestPoint> points) {  
        super.onPostExecute(points);  
        if (points == null) {  
            //failed to navigate
            Toast.makeText(gmap.activity, "No route found.", Toast.LENGTH_LONG).show();
        }  
        else{
    		fillList();
        }
    }
    private void fillList(){
        ArrayList<Map<String, String>> list = buildData();
        String[] from = { "name"};
        int[] to = { android.R.id.text1 };
        SimpleAdapter adapter = new SuggestListAdapter(gmap.activity, list,
            android.R.layout.simple_list_item_2, from, to);
        gmap.activity.listSuggestion.setAdapter(adapter);
        gmap.activity.listSuggestion.setVisibility(View.VISIBLE);
    }
    private ArrayList<Map<String, String>> buildData() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for(SuggestPoint sp:gmap.suggestPoints){
        	list.add(putData(sp.getFormatted_address()));
        }
        return list;
      }

      private HashMap<String, String> putData(String name) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("name", name);
        return item;
      }
    /** 
     * 组合成googlemap direction所需要的url
     *  
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
        String sensor = "sensor=false";
        String format = "json";
        // String format = "xml";
        String url = "https://maps.googleapis.com/maps/api/geocode/"+format+"?address="+parsedValue+"&sensor=false&Accept-Language:zh-CN";//&region=es
        Log.i(TAG,"getLocationURL--->: " + url);
        return url;  
    }
}