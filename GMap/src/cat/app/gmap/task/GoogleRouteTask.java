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

import cat.app.gmap.MapContent;
import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;
import cat.app.gmap.model.SuggestPoint;
import cat.app.gmap.nav.Leg;
import cat.app.gmap.nav.Route;
import cat.app.gmap.nav.RouteParser;
import cat.app.gmap.nav.Step;

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
 * AsyncTask: search route list(lat,lng) by start/end point
 * @author Weixing Sun
 */  
public class GoogleRouteTask extends  
        AsyncTask<String, Void, List<LatLng>> {
    private static final String TAG = "GMap.GoogleMapRouteTask";
	HttpClient client;  
    String url;  
    int old_size=0;
    List<LatLng> route = null;
    MainActivity activity;
    public GoogleRouteTask(MainActivity activity,String url) {
    	this.activity = activity;
        this.url = url;  
    }
    public GoogleRouteTask(MainActivity activity, LatLng start, LatLng dest,String mode) {
    	this.activity = activity;
        this.url = getDirectionsUrl(start,dest,"json",mode);
        //Log.i(TAG, "url="+url);
	}
	@Override  
    protected List<LatLng> doInBackground(String... params) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
                //Log.i(TAG,"Route JSON:"+responseString);
                JSONObject object = new JSONObject(responseString);
                if (object.getString("status").equals("OK")) {
                	if(responseString.length()<100){
                		return null;
                	}
                	Route r = RouteParser.parse(responseString).get(0);
                	Leg l = r.getLegs().get(0);
                	//if(l.getStartAddress()!=null)
                		//gmap.startPoint = new SuggestPoint(r.getLegs().get(0).getStartLocation(),r.getLegs().get(0).getStartAddress());
                	old_size=activity.gMap.pos.steps.size();
                	activity.gMap.pos.steps.addAll(r.getSteps());
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
            Toast.makeText(activity, "No route found.", Toast.LENGTH_LONG).show();
        }  
        else{
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(route);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            Polyline pl = activity.gMap.map.addPolyline(lineOptions);
            activity.gMap.routesPolyLines.add(pl);
            Util.reOrgHints(activity.gMap.pos.steps);
            activity.gMap.findNewRouteSpeech(old_size);
            //gmap.drawAllStepPoints();
        }
    }
	
    /** 
     * 组合成googlemap direction所需要的url
     * @param origin 
     * @param dest 
     * @param format 
     * @param travel_mode
     * @return url 
     */  
    public static String getDirectionsUrl(LatLng origin, LatLng dest, String format, String travelMode) {
        String str_origin = "?origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "&destination=" + dest.latitude + "," + dest.longitude;  
        String sensor = "&sensor=false";
        String mode = "&mode="+travelMode;
        // String waypointLatLng = "waypoints="+"40.036675"+","+"116.32885"; // 如果使用途径点，需要添加此字段
        String parameters = null;  
        // Building the parameters to the web service
        parameters = str_origin + str_dest + sensor + mode; //+"&alternatives=true";
        // String format = "json"; "xml";
        String url = "https://maps.googleapis.com/maps/api/directions/"+ format + parameters;
        //Log.i(TAG,"getDerectionsURL--->: " + url);
        return url;  
    }
}