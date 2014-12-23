package cat.app.gmap.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class TextToSpeechTask extends AsyncTask<String, Void, File> {
	private static final String TAG = "GMap.TextToSpeechTask";
	//http://translate.google.com/translate_tts? tl=en &q=Hello%20World
	String urlStr;
	String site="http://translate.google.com/translate_tts?";
	String language="tl=";
	String q="&q=";
	HttpClient client;
	
    @Override  
    protected void onPreExecute() {
        client = new DefaultHttpClient();  
        client.getParams().setParameter(  
                CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);  
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,15000);  
        super.onPreExecute();
    }  
	@Override
	protected File doInBackground(String... params) {
		String parsedValue = null;
		try {
			parsedValue = java.net.URLEncoder.encode(params[0], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i(TAG, "URLEncoder.encode exception ");
		}
		urlStr=site+language+q+parsedValue;
		try {
			URL url = new URL(urlStr);
			url.openStream();
			InputStream input = new BufferedInputStream(url.openStream(),1024);
			OutputStream output = new FileOutputStream(
					Environment.getExternalStorageDirectory().toString()+ "/GMap/");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	@Override  
    protected void onPostExecute(File file) {  
        super.onPostExecute(file);  
        
    }
}
