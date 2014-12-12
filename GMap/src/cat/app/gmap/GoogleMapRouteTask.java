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
 * 自定义class通过AsyncTask机制异步请求获取导航数据
 * @author Weixing Sun 
 *  
 */  
public class GoogleMapRouteTask extends  
        AsyncTask<String, Void, List<LatLng>> {
    private static final String TAG = "GMap.GoogleMapRouteTask";
	HttpClient client;  
    String url;  
    static Polyline preRoute;
    List<LatLng> routes = null;  
    GMap gmap;
    public GoogleMapRouteTask(GMap gmap,String url) {  
    	this.gmap = gmap;
        this.url = url;  
    }
    @Override  
    protected List<LatLng> doInBackground(String... params) {
  
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);  
            int statusecode = response.getStatusLine().getStatusCode();  
            System.out.println("response:" + response + "      statuscode:"  
                    + statusecode);  
            if (statusecode == 200) {  
  
                String responseString = EntityUtils.toString(response  
                        .getEntity());  
  
                int status = responseString.indexOf("<status>OK</status>");  
                System.out.println("status:" + status);  
                if (-1 != status) {  
                    int pos = responseString.indexOf("<overview_polyline>");  
                    pos = responseString.indexOf("<points>", pos + 1);  
                    int pos2 = responseString.indexOf("</points>", pos);  
                    responseString = responseString  
                            .substring(pos + 8, pos2);  
                    routes = decodePoly(responseString);  
                } else {  
                    // 错误代码，  
                    return null;  
                }
            } else {
                // 请求失败  
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
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  
                15000);  
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
        	removePreviousRoute();
            PolylineOptions lineOptions = new PolylineOptions();  
            lineOptions.addAll(routes);  
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            preRoute = gmap.map.addPolyline(lineOptions);
            //定位到第0点经纬度  
            gmap.map.animateCamera(CameraUpdateFactory.newLatLng(routes.get(0)));
        }
    }
    
    public static void removePreviousRoute() {
		if(preRoute!=null){
			preRoute.remove();
			Log.i(TAG, "preRoute removed");
		}
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
            LatLng p = new LatLng((((double) lat / 1E5)),  
                    (((double) lng / 1E5)));  
            poly.add(p);  
        }  
        return poly;  
    }  
  
}