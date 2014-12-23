package cat.app.gmap.task;

import java.io.FileInputStream;
import java.io.IOException;

import android.media.MediaPlayer;

public class Player {
	private static MediaPlayer mp = new MediaPlayer();
	
	public static void init(String fileName) {
        try {
            FileInputStream fis = new FileInputStream(fileName);
	        mp.setDataSource(fis.getFD());
			mp.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void play(){
		mp.start();
	}
	public static void play(String fileName){
		init(fileName);
		mp.start();
		//mp.stop();
	}
	public static void release(){
		mp.release();
	}
}
