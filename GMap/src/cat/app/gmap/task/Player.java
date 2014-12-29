package cat.app.gmap.task;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Player {
	private static final String TAG = "GMap.Player";
	private static final int MSG_MP_RELEASE = 999;
	private static final int TIME_TO_WAIT = 5000;
	private static MediaPlayer player ;

	public static void startPlaying(String fileName) {
		player = new MediaPlayer();
		try {
			player.setDataSource(fileName);
			player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer arg0) {
					mHandler.sendEmptyMessageDelayed(MSG_MP_RELEASE, TIME_TO_WAIT);
				}
			});
			player.prepare();
			player.start();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
	}
	private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_MP_RELEASE) {
        		player.release();
        		player = null;
            }
        }
    };
	private void stopPlaying() {
		if (player == null) {
			return;
		}
		player.release();
		player = null;
	}
}
