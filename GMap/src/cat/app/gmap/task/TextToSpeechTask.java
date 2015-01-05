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

import cat.app.gmap.MapContent;
import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;
import cat.app.gmap.svc.Player;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;

public class TextToSpeechTask extends AsyncTask<String, Void, String> {
	SparseArray<String> startHintString;
	SparseArray<String> end500HintString;
	SparseArray<String> endHintString;
	public TextToSpeechTask(Player player) {
		super();
		this.startHintString = player.startHintMp3;
		this.endHintString = player.endHintMp3;
		this.end500HintString = player.end500HintMp3;
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
		int stepdId = 0;
		for(int i = 0; i < startHintString.size(); i++) {
		   stepdId = startHintString.keyAt(i);
		   proceedFile(startHintString.get(stepdId),Util.getVoiceFileName(Util.startHint, stepdId));//hint,path
		   sleep(300);
		}
		for(int i = 0; i < endHintString.size(); i++) {
			stepdId = endHintString.keyAt(i);
		   proceedFile(endHintString.get(stepdId),Util.getVoiceFileName(Util.endHint, stepdId));//hint,path
		   sleep(300);
		}
		for(int i = 0; i < end500HintString.size(); i++) {
			stepdId = end500HintString.keyAt(i);
			   proceedFile(end500HintString.get(stepdId),Util.getVoiceFileName(Util.end500Hint, stepdId));//hint,path
			   sleep(300);
			}
		return startHintString.size()+endHintString.size()+"";
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
