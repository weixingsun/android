package cat.app.gmap.svc;

import java.io.IOException;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class Player extends Service implements MediaPlayer.OnPreparedListener,OnAudioFocusChangeListener{
	//private static final String ACTION_PLAY = "cat.app.action.PLAY";

	private final static String TAG = "GMap.Player";

	String fileName;
	private final int MSG_MP_RELEASE = 999;
	private final int TIME_TO_WAIT = 5000;
	private MediaPlayer player;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        //if (intent.getAction().equals(ACTION_PLAY)) {
		Log.i(TAG, "onStartCommand");
		fileName = intent.getStringExtra("file");
        	initMediaPlayer(fileName);
        	player.setOnPreparedListener(this);
        	player.prepareAsync(); // prepare async to not block main thread
        //}
    		Log.i(TAG, "onStartCommand");
		return startId;
    }

	private void initMediaPlayer(String fileName) {
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			player.setDataSource(fileName);
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			e.printStackTrace();
			Log.i(TAG, "Error: MediaPlayer.setDataSource("+fileName+")");
		}
		/*player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				mHandler.sendEmptyMessageDelayed(MSG_MP_RELEASE, TIME_TO_WAIT);
			}
		});*/
	}
	
	/*private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_MP_RELEASE) {
        		player.release();
        		player = null;
            }
        }
    };*/
	public void cleanup() {
		if (player == null) {
			return;
		}
		player.release();
		player = null;
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		player.start();
		Log.i(TAG, "onPrepared");
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
	    switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN:
            // resume playback
            if (player == null) initMediaPlayer(fileName);
            else if (!player.isPlaying()) player.start();
            player.setVolume(1.0f, 1.0f);
            break;

        case AudioManager.AUDIOFOCUS_LOSS:
            // Lost focus for an unbounded amount of time: stop playback and release media player
            if (player.isPlaying()) player.stop();
            player.release();
            player = null;
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            // Lost focus for a short time, but we have to stop playback. 
        	// We don't release the media player because playback is likely to resume
            if (player.isPlaying()) player.pause();
            break;

        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
            // Lost focus for a short time, but it's ok to keep playing at an attenuated level
            if (player.isPlaying()) player.setVolume(0.6f, 0.6f);
            Log.d(TAG, "Mute since received AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            break;
	    }

	}
}
