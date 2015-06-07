package wsn.park.navi.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import wsn.park.maps.OSM;
import wsn.park.model.Data;
import wsn.park.model.ParkingPlace;
import wsn.park.ui.ParkingActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class NetDataTask extends AsyncTask<String, Void, List<ParkingPlace>>{
	private String tag = NetDataTask.class.getSimpleName();
	private HttpClient client;
	private String url;
	private String errMsg;
	private OSM osm = OSM.getInstance();
    @Override  
    protected void onPreExecute() {
        client = new DefaultHttpClient();
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,15000);
        super.onPreExecute();
    }
	@Override
	protected List<ParkingPlace> doInBackground(String... params) {
		this.url = params[0];
		return downloadJson();
	}
	private List<ParkingPlace> downloadJson() {
		HttpGet get = new HttpGet(url);
    	StringBuilder sb = new StringBuilder();
    	String str=null;
        try {
            HttpResponse response = client.execute(get);
            response.addHeader("Accept-Language", "zh-CN");
            HttpEntity entity = response.getEntity();
            BufferedReader bf=new BufferedReader(new InputStreamReader((entity.getContent()),"UTF-8"));
            String line = "";
            while((line=bf.readLine())!=null){
            	sb.append(line);
            }
            str=sb.toString();
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200 ) {
            	return getPlaceFronJson(str);
            }else{
            	Log.w(tag,"statusecode="+statusecode);
            }
        }catch (JSONException e) {
        	if(sb.toString().startsWith("<!DOCTYPE")){
        		errMsg="ServerError:"+str;
        	}else{
        		errMsg="JSONException:"+str;
        	}
            Log.e(tag, errMsg);
		}catch(Exception e){
        	Log.e(tag,"OtherError:"+e.getLocalizedMessage());
        }
		return null;
	}
	//SELECT id,status,lat,lng,operator,type,admin,country,comment
	private List<ParkingPlace> getPlaceFronJson(String body) throws JSONException{
		List<ParkingPlace> points = new ArrayList<ParkingPlace> ();
    	JSONArray posArray = new JSONObject(body).getJSONArray("results");
    	//Log.i(tag,"Array.length():" + posArray.length());
    	for(int i=0;i<posArray.length()-1;i++){
        	JSONObject row = posArray.getJSONObject(i);
        	int id = row.getInt("id");
        	int type = row.getInt("type");
        	int status = row.getInt("status");
        	double lat = row.getDouble("lat");
        	double lng = row.getDouble("lng");
        	String operator = row.getString("operator");
        	String admin = row.getString("admin");
        	String country = row.getString("country");
        	String comment = row.getString("comment");
        	ParkingPlace pp = new ParkingPlace(id,type,status,lat,lng,operator,admin,country,comment);
        	points.add(pp);
        	Log.i(tag, "parking="+pp.toString());
    	}
		return points;
	}
	@Override  
    protected void onPostExecute(List<ParkingPlace> points) {
        super.onPostExecute(points);
        //ParkingActivity pa = 
        if(points==null) return;
        Data.getInstance().setParkingPlaces(points);
        Intent intent = new Intent(osm.act, wsn.park.ui.ParkingActivity.class);
        osm.act.startActivity(intent);
	}
	
}
