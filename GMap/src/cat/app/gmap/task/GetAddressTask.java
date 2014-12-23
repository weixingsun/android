package cat.app.gmap.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

import com.google.android.gms.maps.model.LatLng;

import cat.app.gmap.MainActivity;
import cat.app.gmap.nav.Route;
import cat.app.gmap.nav.RouteParser;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetAddressTask extends AsyncTask<LatLng, Void, String> {
	MainActivity act;
	private static final String TAG = "GMap.GetAddressTask";
	private String website = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";  //40.714224,-73.961452
	HttpClient client;
	LatLng point;
	public GetAddressTask(MainActivity act,LatLng point) {
		super();
		this.act = act;
		this.point=point;
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
	protected String doInBackground(LatLng... params) {
		String url = website+point.latitude+","+point.longitude;
		HttpGet get = new HttpGet(url);
		try {
            HttpResponse response = client.execute(get);  
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(responseString);
                if (object.getString("status").equals("OK")) {
                	JSONArray posArray = object.getJSONArray("results");
                	JSONObject addressFull = posArray.getJSONObject(0);
                	JSONArray components = addressFull.getJSONArray("address_components");
                	for(int i=0;i<components.length();i++){
                    	JSONObject obj = components.getJSONObject(i);
                    	JSONArray types=obj.getJSONArray("types");
                    	String country = types.getString(0);
                    	if(country.equals("country")){
                    		return obj.getString("short_name");
                    	}
                	}
                }
            }
        } catch (ClientProtocolException e) {  
            Log.i(TAG, "ClientProtocolException:"+e.getMessage());
        } catch (IOException e) {  
        	Log.i(TAG, "IOException:"+e.getMessage());
        } catch (JSONException e) {
        	Log.i(TAG, "JSONException:"+e.getMessage());
		}
		//Geocoder geocoder = new Geocoder(act, Locale.getDefault());
        //addresses.get(0).getCountryCode();
		return null;
	}
	
	@Override
    protected void onPostExecute(String code) {
        act.gMap.myCountryCode=code;
        //Toast.makeText(act, "myCountryCode:"+code, Toast.LENGTH_LONG).show();
        Log.i(TAG, "myCountryCode="+code);
    }
}
