package cat.app.gmap.task;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class Player {
	private static final String TAG = "GMap.Player";
	private static MediaPlayer player ;


	public static void startPlaying(String fileName) {
		MediaPlayer mp = new MediaPlayer();
		try {
			mp.setDataSource(fileName);
			mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				// @Override
				public void onCompletion(MediaPlayer arg0) {
					try {
						arg0.release();
						arg0=null;
					} catch (Exception e) {
						System.out.println("onCompletion ERR:"+e.getMessage());
					}
				}
			});
			mp.prepare();
			mp.start();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
	}
	private void stopPlaying() {
		if (player == null) {
			return;
		}
		player.release();
		player = null;
	}
}
