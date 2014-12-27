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

import com.google.android.gms.maps.model.LatLng;

import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;
import android.os.AsyncTask;
import android.util.Log;

public class UploadTask extends AsyncTask<LatLng, Void, String> {
	MainActivity act;
	private static final String TAG = "GMap.UploadTask";
	private String website = "http://servicedata.net76.net/insert.php?";  //lat=0&lng=0&type=0&reporter=name
	HttpClient client;
	LatLng point;
	int type;
	String reporter;
	//SELECT DATE_SUB(now(), INTERVAL 1 HOUR);  --DATE_ADD/DATE_SUB
	public UploadTask(MainActivity act,LatLng point,int type, String reporter) {
		super();
		this.act = act;
		this.point=point;
		this.type = type;
		this.reporter=reporter;
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
		double lat = point.latitude;
		double lng = point.longitude;
		String tz_offset=""+Util.getTimezoneOffsetHour();
		String url = website+"lat="+lat+"&lng="+lng+"&type="+type+"&reporter="+reporter+"&tz_offset="+tz_offset;
		Log.i(TAG, "URL="+url);
		HttpGet get = new HttpGet(url);
		try {
            HttpResponse response = client.execute(get);
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200) {
                //
            }
        } catch (ClientProtocolException e) {  
            Log.i(TAG, "ClientProtocolException:"+e.getMessage());
        } catch (IOException e) {  
        	Log.i(TAG, "IOException:"+e.getMessage());
        }
		return null;
	}
	
	@Override
    protected void onPostExecute(String code) {
        Log.i(TAG, "code="+code);
    }
}
