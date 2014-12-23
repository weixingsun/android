package cat.app.gmap.task;

import java.io.IOException;

import android.media.MediaPlayer;

public class Player {
	private static MediaPlayer mp = new MediaPlayer();
	
	public static void init(String fileName) {
        try {
        	
        	mp.reset();
	        mp.setDataSource(fileName);
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void play(){
		mp.start();
	}
	public static void play(String fileName){
		if( mp.isPlaying() ){
			mp.stop();
		}
		init(fileName);
		mp.start();
	}
	public static void release(){
		mp.release();
	}
}
