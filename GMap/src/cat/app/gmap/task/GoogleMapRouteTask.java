package cat.app.gmap.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import cat.app.gmap.Util;
import cat.app.gmap.nav.FindMyStepTask;
import cat.app.gmap.nav.Route;
import cat.app.gmap.nav.RouteParser;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;


/** 
 * 自定义class通过AsyncTask机制异步请求获取导航数据
 * @author Weixing Sun 
 *  
 */  
public class GoogleMapRouteTask extends  
        AsyncTask<String, Void, List<LatLng>> {
    private static final String TAG = "GMap.GoogleMapRouteTask";
	HttpClient client;  
    String url;  
    int old_size=0;
    List<LatLng> route = null;
    GMap gmap;
    public GoogleMapRouteTask(GMap gmap,String url) {  
    	this.gmap = gmap;
        this.url = url;  
    }
    public GoogleMapRouteTask(GMap gmap, LatLng start, LatLng dest) {
    	this.gmap = gmap;
        this.url = getDirectionsUrl(start,dest,"json");
        Log.i(TAG, "url="+url);
	}
	@Override  
    protected List<LatLng> doInBackground(String... params) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);  
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(responseString);
                if (object.getString("status").equals("OK")) {
                	Route r = RouteParser.parse(responseString).get(0);
                	gmap.routes.add(r);
                	old_size=gmap.steps.size();
                	gmap.steps.addAll(r.getSteps());
                	route = RouteParser.getWholeRoutePoints(r);
                } else {
                    return null;
                }
            } else {
                return null;  
            }
        } catch (ClientProtocolException e) {  
            Log.i(TAG, "ClientProtocolException:"+e.getMessage());
        } catch (IOException e) {  
        	Log.i(TAG, "IOException:"+e.getMessage());
        } catch (JSONException e) {
        	Log.i(TAG, "JSONException:"+e.getMessage());
		}
        //Log.i(TAG,"doInBackground:"+routes);
        return route;  
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
    protected void onPostExecute(List<LatLng> route) {  
        super.onPostExecute(route);  
        if (route == null) {
            Toast.makeText(gmap.activity, "No route found.", Toast.LENGTH_LONG).show();
        }  
        else{
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(route);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            Polyline pl = gmap.map.addPolyline(lineOptions);
            gmap.routesPolyLines.add(pl);
            gmap.in(old_size);
        }
    }

    
	
    /** 
     * 组合成googlemap direction所需要的url
     *  
     * @param origin 
     * @param dest 
     * @return url 
     */  
    public static String getDirectionsUrl(LatLng origin, LatLng dest, String format) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;  
        // Sensor enabled
        String sensor = "sensor=false";
        // Travelling Mode  
        String mode = "mode=driving";
        // String waypointLatLng = "waypoints="+"40.036675"+","+"116.32885"; // 如果使用途径点，需要添加此字段
        String parameters = null;  
        // Building the parameters to the web service
        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode; //+"&alternatives=true";
        // String format = "json"; "xml";
        String url = "https://maps.googleapis.com/maps/api/directions/"+ format + "?" + parameters;
        //Log.i(TAG,"getDerectionsURL--->: " + url);
        return url;  
    }
    
    /** 
     * 组合成googlemap direction所需要的url
     *  
     * @param origin 
     * @param dest 
     * @return url 
     */
    /*public static String getDirectionsUrl(LatLng origin, List<LatLng> waypointLatLng, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;  
        // Sensor enabled
        String sensor = "sensor=false";
        // Travelling Mode  
        String mode = "mode=driving";
        // 如果使用途径点，需要添加此字段
        String waypointsURL = "waypoints=";
        for(LatLng ll : waypointLatLng){
        	waypointsURL+= ll.latitude+","+ll.longitude + "%7C";
        }
        // Building the parameters to the web service
        //parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;  
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode+"&"+waypointsURL;
        // String output = "json";
        String output = "xml";
        // Building the url to the web service  
        String url = "https://maps.googleapis.com/maps/api/directions/"  
                + output + "?" + parameters;
        Log.i(TAG,"getDerectionsURL--->: " + url);  
        return url;  
    }
     * 
     * 解析返回xml中overview_polyline的路线编码 
     *  
     * @param encoded 
     * @return List<LatLng> 
       
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
            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);  
        }  
        return poly;  
    }  
    */
}