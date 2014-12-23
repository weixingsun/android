package cat.app.gmap.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import cat.app.gmap.MainActivity;

import android.os.AsyncTask;
import android.util.Log;

public class TextToSpeechTask extends AsyncTask<String, Void, String> {
	MainActivity act;
	public TextToSpeechTask(MainActivity act) {
		super();
		this.act = act;
	}
	private static final String TAG = "GMap.TextToSpeechTask";
	//http://translate.google.com/translate_tts? tl=en &q=Hello%20World
	String urlStr;
	String site="http://translate.google.com/translate_tts?";
	String language="tl=en";
	String q="&q=";
	
    @Override  
    protected void onPreExecute() {
        super.onPreExecute();
    }  
	@Override
	protected String doInBackground(String... params) {
		String parsedValue = null;
		try {
			parsedValue = java.net.URLEncoder.encode(params[0], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i(TAG, "URLEncoder.encode exception ");
		}
		urlStr=site+language+q+parsedValue;
		Log.i(TAG, "URL TextToSpeechTask: "+urlStr);
		try {
			URL url = new URL(urlStr);
			url.openStream();
			InputStream input = new BufferedInputStream(url.openStream(),1024);
			OutputStream output = new FileOutputStream(act.gMap.nextHintFile);
			byte[] data = new byte[1024];
			int count=0;
			while ((count=input.read(data)) > -1) {
                output.write(data, 0, count);
            }
			output.flush();
            output.close();
            input.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return act.gMap.nextHintFile;
	}
	@Override  
    protected void onPostExecute(String filePath) {
        //super.onPostExecute(filePath);
        Log.i(TAG, "Next Hint File="+filePath);
    }
}
