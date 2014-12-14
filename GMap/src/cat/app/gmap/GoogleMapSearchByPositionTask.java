package cat.app.gmap;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;


/** 
 * 自定义class通过AsyncTask机制异步请求获取位置数据
 * @author Weixing Sun
 *  
 */  
public class GoogleMapSearchByPositionTask extends
        AsyncTask<String, Void, List<SuggestPoint>> {
    private static final String TAG = "GMap.GoogleMapConverterTask";
	HttpClient client;  
    String url;
    LatLng position;
    SuggestPoint foundPoint;
    GMap gmap;
    public GoogleMapSearchByPositionTask(GMap gmap,LatLng position) {
    	this.position=position;
    	this.gmap = gmap;
        this.url = getLocationURL(position);
    }
    @Override  
    public List<SuggestPoint> doInBackground(String... params) {
    	gmap.points.clear();
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
            //Log.i(TAG,"response:" + sb.toString());
            if (statusecode == 200 ) {
            	JSONArray posArray = new JSONObject(sb.toString()).getJSONArray("results");
            	//for(int i=0;i<posArray.length()&&i<2;i++){
	            	JSONObject addressFull = posArray.getJSONObject(0);
	            	JSONObject location = addressFull.getJSONObject("geometry").getJSONObject("location");
	            	String formatted_address = addressFull.getString("formatted_address");
	            	this.position = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
	            	this.foundPoint = new SuggestPoint(this.position,formatted_address);
	    			//gmap.points.add(sp);
            	//}
    			return null;
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
        return gmap.points;  
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
        if (foundPoint == null) {  
            //failed to navigate
            Toast.makeText(gmap.activity, "No location found.", Toast.LENGTH_LONG).show();
        } else{
        	Marker marker = gmap.map.addMarker(new MarkerOptions()
            .title(foundPoint.getMarkerTitle())
            .snippet(foundPoint.getMarkerSnippet())
            .position(foundPoint.getLocation()));
        	
        	gmap.markers.put(marker.getId(), marker);
        	gmap.lastMarkerId = marker.getId();
            //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        	gmap.refreshRoute();
        }
    }
    /** 
     * 组合成googlemap direction所需要的url
     *  
     * @param origin 
     * @param dest 
     * @return url 
     */
    public String getLocationURL(LatLng position) {
        
        //String sensor = "&sensor=false";
        String format = "json";
        // String format = "xml";
        String url = "https://maps.googleapis.com/maps/api/geocode/"+format+"?latlng="+position.latitude+","+position.longitude+"&sensor=false&Accept-Language:zh-CN";
        //http://maps.google.com/maps/api/geocode/json?latlng=-43.5320544,172.6362254&sensor=false&ion=cn
        Log.i(TAG,"getLocationURL--->: " + url);
        return url;  
    }
}