package cat.app.net.p2p.cloud;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import android.os.AsyncTask;
import android.util.Log;
/*
v=0
o=ice4j.org 0 0 IN IP4 10.32.26.15
s=-
t=0 0
a=ice-options:trickle
a=ice-ufrag:e9skp19e5crmnc
a=ice-pwd:7b5mmcckuo3fkcjfohddhd1ahf
m=text 8888 RTP/AVP 0
c=IN 10.32.26.15 IP4
a=mid:text
a=candidate:1 1 udp 2130706431 fe80::ae22:bff:fe3e:3be0 8888 typ host
a=candidate:2 1 udp 2130706431 10.32.26.15 8888 typ host
a=candidate:3 1 udp 1677724415 202.36.179.100 32035 typ srflx raddr 10.32.26.15 rport 8888
a=candidate:4 1 udp 1677724415 202.36.179.100 22065 typ srflx raddr 10.32.26.15 rport 8888
 */
public class UserDataUploadTask extends AsyncTask<String, Void, String> {
	private static final String TAG = UserDataUploadTask.class.getSimpleName();
	private String url;
	HttpClient client;
	String host;
	String sdp;
	List<BasicNameValuePair> httpParams = new ArrayList<BasicNameValuePair>();  
	public String getInsertURL() {
        String url = "http://servicedata.vhostall.com/insert_p2p.php?";
        return url;
    }
	//SELECT DATE_SUB(now(), INTERVAL 1 HOUR);  --DATE_ADD/DATE_SUB
	public UserDataUploadTask(String host,String sdp) {
		super();
		this.host = host;
		this.sdp=sdp;
		httpParams.add(new BasicNameValuePair("host", host));  
		httpParams.add(new BasicNameValuePair("sdp", sdp));  
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
	protected String doInBackground(String... params) {
		try {
			//HttpGet get = new HttpGet(url);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(httpParams, "utf-8"));
            HttpResponse response = client.execute(post);
            int statusecode = response.getStatusLine().getStatusCode();
            if (statusecode == 200) {
                return "success";
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
