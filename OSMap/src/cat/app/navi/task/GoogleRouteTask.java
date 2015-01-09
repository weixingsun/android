package cat.app.navi.task;

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
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;

import cat.app.maps.OSM;
import cat.app.navi.RouteOptions;
import cat.app.navi.google.Leg;
import cat.app.navi.google.Route;
import cat.app.navi.google.RouteParser;
import cat.app.navi.google.Step;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

public class GoogleRouteTask extends AsyncTask<String, Void, Polyline> {
    private static final String TAG = GoogleRouteTask.class.getSimpleName();
	HttpClient client;
    String url;
    int old_size=0;
    List<GeoPoint> route = null;
    Activity activity;
    OSM osm;
    List<Step> steps = new ArrayList<Step>();
    public GoogleRouteTask(Activity act, OSM osm , GeoPoint start, GeoPoint dest,String mode) {
    	this.activity = act;
    	this.osm = osm;
        this.url = getDirectionsUrl(start,dest,"json",mode);
        //Log.i(TAG, "url="+url);
	}
	@Override  
    protected Polyline doInBackground(String... params) {
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
                //Log.i(TAG,"Route JSON:"+responseString);
                JSONObject object = new JSONObject(responseString);
                if (object.getString("status").equals("OK")) {
                	if(responseString.length()<100) return null;
                	Route r = RouteParser.parse(responseString).get(0);
                	steps.addAll(r.getSteps());
                	route = RouteParser.getWholeRoutePoints(r);
                    Polyline pl = new Polyline(activity);
                    pl.setColor(RouteOptions.getColor());
                    pl.setWidth(10);
                    pl.setPoints(route);
                    return pl;
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
        return null;  
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
    protected void onPostExecute(Polyline pl) {
        super.onPostExecute(pl);  
        if (route == null) {
            Toast.makeText(activity, "No route found.", Toast.LENGTH_LONG).show();
        }else{
        	osm.removeAllRouteMarkers();
        	if(route==null) return;
        	osm.addPolyline(pl);
        	osm.drawSteps(steps);
            //activity.map.routesPolyLines.add(pl);
            //Util.reOrgHints(activity.map.pos.steps);
            //activity.map.findNewRouteSpeech(old_size);
            //gmap.drawAllStepPoints();
        }
    }
	
    public static String getDirectionsUrl(GeoPoint origin, GeoPoint dest, String format, String travelMode) {
        String str_origin = "?origin=" + origin.getLatitude() + "," + origin.getLongitude();
        String str_dest = "&destination=" + dest.getLatitude() + "," + dest.getLongitude();  
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
