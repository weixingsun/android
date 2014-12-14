package cat.app.gmap;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


/** 
 * �Զ���classͨ��AsyncTask�����첽�����ȡ��������
 * @author Weixing Sun 
 *  
 */  
public class GoogleMapRouteTask extends  
        AsyncTask<String, Void, List<LatLng>> {
    private static final String TAG = "GMap.GoogleMapRouteTask";
	HttpClient client;  
    String url;  
    static List<Polyline> preRoutes = new ArrayList<Polyline>();
    List<LatLng> routes = null;  
    GMap gmap;
    public GoogleMapRouteTask(GMap gmap,String url) {  
    	this.gmap = gmap;
        this.url = url;  
    }
    public GoogleMapRouteTask(GMap gmap, LatLng start, LatLng dest) {
    	this.gmap = gmap;
        this.url = getDirectionsUrl(start,dest);
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
                int status = responseString.indexOf("<status>OK</status>");  
                if (-1 != status) {
                    int pos = responseString.indexOf("<overview_polyline>");  
                    pos = responseString.indexOf("<points>", pos + 1);  
                    int pos2 = responseString.indexOf("</points>", pos);  
                    responseString = responseString.substring(pos + 8, pos2);  
                    routes = decodePoly(responseString);  
                } else {  
                    // ������룬  
                    return null;  
                }
            } else {
                // ����ʧ��  
                return null;  
            }
        } catch (ClientProtocolException e) {  
            Log.i(TAG, "ClientProtocolException:"+e.getMessage());
        } catch (IOException e) {  
        	Log.i(TAG, "IOException:"+e.getMessage());
        }  
        //Log.i(TAG,"doInBackground:"+routes);
        return routes;  
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
    protected void onPostExecute(List<LatLng> routes) {  
        super.onPostExecute(routes);  
        if (routes == null) {  
            //failed to navigate
            Toast.makeText(gmap.activity, "No route found.", Toast.LENGTH_LONG).show();
        }  
        else{
        	//removePreviousRoute();
            PolylineOptions lineOptions = new PolylineOptions();  
            lineOptions.addAll(routes);  
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            preRoutes.add(gmap.map.addPolyline(lineOptions)) ;
            //��λ����0�㾭γ��  
            //gmap.map.animateCamera(CameraUpdateFactory.newLatLng(routes.get(0)));
        }
    }

    public static void removePreviousRoute() {
    	if(preRoutes!=null){
	    	for(Polyline pl:preRoutes){
	    		pl.remove();
	    	}
    	}
	}
	/** 
     * ��������xml��overview_polyline��·�߱��� 
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
            LatLng p = new LatLng((((double) lat / 1E5)),  
                    (((double) lng / 1E5)));  
            poly.add(p);  
        }  
        return poly;  
    }  
    /** 
     * ��ϳ�googlemap direction����Ҫ��url
     *  
     * @param origin 
     * @param dest 
     * @return url 
     */  
    public static String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;  
        // Sensor enabled
        String sensor = "sensor=false";
        // Travelling Mode  
        String mode = "mode=driving";
        // String waypointLatLng = "waypoints="+"40.036675"+","+"116.32885";
        // ���ʹ��;���㣬��Ҫ��Ӵ��ֶ�
        // String waypoints = "waypoints=";
        String parameters = null;  
        // Building the parameters to the web service
        parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode+"&"+"alternatives=true";  
        // parameters = str_origin + "&" + str_dest + "&" + sensor + "&"  
        // + mode+"&"+waypoints;
        // String output = "json";
        String output = "xml";
        //String newIP = "http://173.194.72.31/maps/api/directions/"+output+"?"+parameters;
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters;
        //Log.i(TAG,"getDerectionsURL--->: " + url);
        return url;  
    }
    
    /** 
     * ��ϳ�googlemap direction����Ҫ��url
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
        // ���ʹ��;���㣬��Ҫ��Ӵ��ֶ�
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
    }*/
}