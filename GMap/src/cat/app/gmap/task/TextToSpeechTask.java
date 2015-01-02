package cat.app.gmap.task;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import cat.app.gmap.GMap;
import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;

public class TextToSpeechTask extends AsyncTask<String, Void, String> {
	GMap gmap;
	SparseArray<String> startHintFile;
	SparseArray<String> end500HintFile;
	SparseArray<String> endHintFile;
	public TextToSpeechTask(GMap gmap,SparseArray<String> startHintFile,SparseArray<String> endHintFile,SparseArray<String> end500HintFile) {
		super();
		this.gmap = gmap;
		this.startHintFile = startHintFile;
		this.endHintFile = endHintFile;
		this.end500HintFile = end500HintFile;
	}
	private static final String TAG = "GMap.TextToSpeechTask";
	//http://translate.google.com/translate_tts? tl=en &q=Hello%20World
	//http://translate.google.com/translate_tts?q=testing+1+2+3&tl=en_us
	//http://translate.google.com/translate_tts?q=testing+1+2+3&tl=en_gb
	//http://translate.google.com/translate_tts?q=testing+1+2+3&tl=en_au
	//Locale.getDefault().getLanguage() == "zh" / "en"
	String urlStr;
	String site="http://translate.google.com/translate_tts?";
	String language="tl=en_us";
	String q="&q=";
	
    @Override  
    protected void onPreExecute() {
        super.onPreExecute();
    }  
	@Override
	protected String doInBackground(String... params) {
		int key = 0;
		for(int i = 0; i < startHintFile.size(); i++) {
		   key = startHintFile.keyAt(i);
		   proceedFile(startHintFile.get(key),Util.getVoiceFileName(Util.startHint, key));//hint,path
		   sleep(300);
		}
		for(int i = 0; i < endHintFile.size(); i++) {
		   key = endHintFile.keyAt(i);
		   proceedFile(endHintFile.get(key),Util.getVoiceFileName(Util.endHint, key));//hint,path
		   sleep(300);
		}
		for(int i = 0; i < end500HintFile.size(); i++) {
			   key = end500HintFile.keyAt(i);
			   proceedFile(end500HintFile.get(key),Util.getVoiceFileName(Util.end500Hint, key));//hint,path
			   sleep(300);
			}
		return startHintFile.size()+endHintFile.size()+"";
	}
	private void sleep(int second){
		try {
			Thread.sleep(second);
		} catch (InterruptedException e) {
		}
	}
	@Override  
    protected void onPostExecute(String totalFileNumber) {
        //super.onPostExecute(filePath);
		Log.i(TAG, "files="+totalFileNumber);
    }
	private void proceedFile(String instruction, String fileName){
		String parsedValue = null;
		try {
			parsedValue = java.net.URLEncoder.encode(instruction, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.i(TAG, "URLEncoder.encode exception ");
		} catch (Exception e){
			return ;
		}
		urlStr=site+language+q+parsedValue;
		//Log.i(TAG, "URL TextToSpeechTask: "+urlStr);
		try {
			URL url = new URL(urlStr);
			url.openStream();
			InputStream input = new BufferedInputStream(url.openStream(),1024);
			OutputStream output = new FileOutputStream(fileName);
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
			e.printStackTrace();
		}
	}
}
