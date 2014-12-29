package cat.app.gmap.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import cat.app.gmap.model.MarkerPoint;
import cat.app.gmap.model.SuggestPoint;

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
 * �Զ���classͨ��AsyncTask�����첽�����ȡλ������
 * @author Weixing Sun
 *  
 */  
public class FectchUserDataTask extends
        AsyncTask<String, Void, List<SuggestPoint>> {
    private static final String TAG = "GMap.FectchUserDataTask";
	HttpClient client;  
    String url;
    SuggestPoint foundPoint;
    GMap gmap;
    public FectchUserDataTask(GMap gmap,LatLng lu,LatLng rd) {
    	this.gmap = gmap;
        this.url = getLocationURL(lu,rd);
        Log.i(TAG, url);
    }
    @Override  
    public List<SuggestPoint> doInBackground(String... params) {
    	gmap.suggestPoints.clear();
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
            //
            if (statusecode == 200 ) {
            	List<SuggestPoint> points = new ArrayList<SuggestPoint> ();
            	JSONArray posArray = new JSONObject(sb.toString()).getJSONArray("results");
            	//Log.i(TAG, sb.toString());
            	Log.i(TAG,"Array.length():" + posArray.length());
            	for(int i=0;i<posArray.length()-1;i++){
	            	JSONObject row = posArray.getJSONObject(i);
	            	//int id = row.getInt("ID");
	            	String report_time = row.getString("report_time");
	            	//String tz_offset_hour = row.getString("tz_offset_hour");
	            	double lat = row.getDouble("lat");
	            	double lng = row.getDouble("lng");
	            	int type = row.getInt("type");
	            	//String reporter = row.getString("reporter");
	            	//String comment = row.getString("comment");
	            	String title="";
	            	this.foundPoint = new SuggestPoint(new LatLng(lat,lng),"Time:"+report_time,type);
	            	points.add(foundPoint);
	            	//gmap.addRemindMarker(foundPoint,type);
	    			//gmap.remindMarkers.put(foundPoint.get,point);
            	}
    			return points;
            } else {
            	Log.w(TAG,"doInBackground:statusecode="+statusecode);
                return null;
            }
        } catch (ClientProtocolException e) {  
            Log.i(TAG, "ClientProtocolException:"+e.getMessage());
        } catch (JSONException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
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
        if (foundPoint == null) {  
            //failed to navigate
            Toast.makeText(gmap.activity, "No location found.", Toast.LENGTH_LONG).show();
        } else{
        	for(SuggestPoint sp:points)
        		gmap.addRemindMarker(sp,sp.getType());
        }
    }
    /** 
     * ��ϳ�googlemap direction����Ҫ��url
     * @param origin 
     * @param dest 
     * @return url 
     */
    public String getLocationURL(LatLng lu,LatLng rd) {
        //String sensor = "&sensor=false";
        String format = "json";
        String url = "http://www.servicedata.net76.net/select_dl.php?"
        		    +"?latlng1="+lu.latitude+","+lu.longitude+"&latlng2="+rd.latitude+","+rd.longitude;
        //http://www.servicedata.net76.net/select_dl.php??latlng1=1,2&latlng2=3,4
        return url;
    }
}