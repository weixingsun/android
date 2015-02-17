package cat.app.net.p2p.cloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;


/** 
 * 自定义class通过AsyncTask机制异步请求获取位置数据
 * @author Weixing Sun
 *  
 */  
public class UserDataFectchTask extends
        AsyncTask<String, Void, String> {
    private static final String TAG = UserDataFectchTask.class.getSimpleName();
	HttpClient client;  
    String url;
    String baseurl = "http://www.servicedata.vhostall.com/select_all_p2p_json.php";
    String params = "?host=";
    String errMsg =null;
    RequestQueue mQueue;
    
    public UserDataFectchTask(Activity act,final String key) {
        Log.i(TAG, url);
        url=baseurl+params+key;
        mQueue = Volley.newRequestQueue(act);
        
        /*StringRequest request = new StringRequest(Method.POST, url,  listener, errorListener) {  
            @Override  
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("host", key);
                return map;
            }
        };*/
        JsonObjectRequest request = new JsonObjectRequest(url, null,  
                new Response.Listener<JSONObject>() {  
                    @Override  
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                    }  
                }, new Response.ErrorListener() {  
                    @Override  
                    public void onErrorResponse(VolleyError error) {  
                        Log.e("TAG", error.getMessage(), error);  
                    }  
                }
        );
        mQueue.add(request);
    }
    @Override  
    public String doInBackground(String... params) {
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
            	JSONArray posArray = new JSONObject(sb.toString()).getJSONArray("results");
            	//Log.i(TAG, sb.toString());
            	Log.i(TAG,"Array.length():" + posArray.length());
            	for(int i=0;i<posArray.length()-1;i++){
	            	JSONObject row = posArray.getJSONObject(i);
	            	//int id = row.getInt("ID");
	            	int report_time = row.getInt("report_time");
	            	//String tz_offset_hour = row.getString("tz_offset_hour");
	            	double lat = row.getDouble("lat");
	            	double lng = row.getDouble("lng");
	            	int type = row.getInt("type");
	            	//String reporter = row.getString("reporter");
	            	//String comment = row.getString("comment");
	            	String title="";
	            	if(report_time>60){
	            		title=report_time/60 +":" +report_time%60 +" ago";
	            		//Log.i(TAG, "time="+report_time+",title="+title);
	            	}else{
	            		title=report_time +" min ago";
	            	}
            	}
    			return "";
            } else {
            	Log.w(TAG,"doInBackground:statusecode="+statusecode);
                return null;
            }
        } catch (ClientProtocolException e) {  
        	errMsg= "ClientProtocolException:"+e.getMessage();
            Log.i(TAG, errMsg);
        } catch (JSONException e) {
        	errMsg="JSONException:"+e.getMessage();
        	if(sb.toString().startsWith("<!DOCTYPE")){
        		errMsg="Server temporary unavailable.";
        	}
            Log.i(TAG, errMsg);
            //Log.i(TAG, sb.toString());
			//e.printStackTrace();
		} catch(IOException e){
			errMsg = "IOException:"+e.getMessage();
            Log.i(TAG, errMsg);
		}
        return "";  
    }  
  
    @Override  
    protected void onPreExecute() {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,15000);
        super.onPreExecute();
    }
  
    @Override  
    protected void onPostExecute(String points) {
        super.onPostExecute(points);  
        if(errMsg!=null){
        	//Toast.makeText(gmap.activity, errMsg, Toast.LENGTH_LONG).show();
        	Log.i(TAG, errMsg);
        }else{
    		//Log.i(TAG, "markers.size="+gmap.remindMarkers.size()+"markerpoints.size="+gmap.remindMarkerPoints.size());
    		//Toast.makeText(gmap.activity, "markers.size="+gmap.remindMarkers.size()+"points.size="+gmap.remindMarkerPoints.size(), Toast.LENGTH_LONG).show();
        }
    }
    
}