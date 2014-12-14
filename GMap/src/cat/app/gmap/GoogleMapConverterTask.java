package cat.app.gmap;

import java.io.IOException;
import java.io.InputStream;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
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
public class GoogleMapConverterTask extends
        AsyncTask<String, Void, List<SuggestPoint>> {
    private static final String TAG = "GMap.GoogleMapConverterTask";
	HttpClient client;  
    String url;
    
    GMap gmap;
    public GoogleMapConverterTask(GMap gmap,String address) {
    	this.gmap = gmap;
        this.url = getLocationURL(address);  
    }
    @Override  
    public List<SuggestPoint> doInBackground(String... params) {
    	gmap.points.clear();
    	StringBuilder sb = new StringBuilder();
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			// 循环读取服务器响应
			while ((b = stream.read()) != -1) {
				sb.append((char) b);
			}
            int statusecode = response.getStatusLine().getStatusCode();
            //Log.i(TAG,"response:" + sb.toString());  
            if (statusecode == 200 ) {
            	JSONObject jsonObject = new JSONObject(sb.toString());
            	JSONObject addressFull = jsonObject.getJSONArray("results").getJSONObject(0);
            	JSONObject location = addressFull.getJSONObject("geometry").getJSONObject("location");
            	String formatted_address = addressFull.getString("formatted_address");
    			LatLng ll = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
    			SuggestPoint sp = new SuggestPoint(ll,formatted_address);
    			gmap.points.add(sp);
    			return gmap.points;
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
        if (points == null) {  
            //failed to navigate
            Toast.makeText(gmap.activity, "No route found.", Toast.LENGTH_LONG).show();
        }  
        else{
        	//removePreviousRoute();
    		//gmap.activity.inputAddress.setText(points.get(0).getFormatted_address());
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
    }
    private ArrayList<Map<String, String>> buildData() {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for(SuggestPoint sp:gmap.points){
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
     * 解析返回xml中overview_polyline的路线编码 
     *  
     * @param encoded 
     * @return List<LatLng> 
     */  
    private List<LatLng> decodePoly(String encoded) {  
        List<LatLng> poly = new ArrayList<LatLng>();  
        int index = 0, len = encoded.length();  
        int lat = 0, lng = 0;  
        while (index < len) {  
            int b, shift = 0, result = 0;  
            do {  
                b = encoded.charAt(index++) - 63;  
                result |= (b & 0x1f) << shift;  
                shift += 5;  
            } while (b >= 0x20);  
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));  
            lat += dlat;  
            shift = 0;  
            result = 0;  
            do {  
                b = encoded.charAt(index++) - 63;  
                result |= (b & 0x1f) << shift;  
                shift += 5;  
            } while (b >= 0x20);  
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));  
            lng += dlng;  
            LatLng p = new LatLng((((double) lat / 1E5)),  (((double) lng / 1E5)));  
            poly.add(p);  
        }  
        return poly;  
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
        String url = "https://maps.googleapis.com/maps/api/geocode/"+format+"?address="+parsedValue;
        Log.i(TAG,"getLocationURL--->: " + url);
        return url;  
    }
}