package cat.app.gmap.svc;

import java.io.File;
import java.io.IOException;

import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

public class Player extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, OnAudioFocusChangeListener{
	//private static final String ACTION_PLAY = "cat.app.action.PLAY";

	private final static String TAG = "GMap.Player";
	MainActivity activity;
	AudioManager am;
	String fileName;
	private final int MSG_MP_RELEASE = 999;
	private final int TIME_TO_WAIT = 5000;
	private static MediaPlayer player;
	public SparseArray<String> startHintMp3 = new SparseArray<String>();
	public SparseArray<String> end500HintMp3 = new SparseArray<String>();
	public SparseArray<String> endHintMp3 = new SparseArray<String>();
	
	public SparseArray<String> playedStartMp3 = new SparseArray<String>();
	public SparseArray<String> playedEndMp3 = new SparseArray<String>();
	//public SparseArray<String> playedEnd500Mp3 = new SparseArray<String>();
	
	public Player(MainActivity activity) {
		this.activity = activity;
		audioInit();
	}
	public Player() {
		//audioInit();
	}
	public void audioInit() {
		am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		int result = am.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
				    AudioManager.AUDIOFOCUS_GAIN);
		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			    Log.w(TAG, "Could not get audio focus.");
		}
		player = new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		fileName = intent.getStringExtra("file");
    	initMediaPlayer(fileName);
    	player.setOnPreparedListener(this);
    	//player.prepareAsync();
		Log.i(TAG, "onStartCommand");
		return startId;
    }
	public void initMediaPlayer(String fileName) {
		if(player==null)
		player = new MediaPlayer();
    	
		try {
			player.setDataSource(fileName);
			player.prepareAsync();
		} catch (IllegalArgumentException | SecurityException | IOException e) {
			e.printStackTrace();
			Log.i(TAG, "Error: MediaPlayer.setDataSource("+fileName+")");
		} catch (IllegalStateException e){
			//player.prepareAsync();
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

        am.abandonAudioFocus(this);
        arraysClear();
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

	public void arraysClear() {
		// TODO Auto-generated method stub
		startHintMp3.clear();
		endHintMp3.clear();
		end500HintMp3.clear();
		playedStartMp3.clear();
		playedEndMp3.clear();
		//playedEnd500Mp3.clear();
	}

	public void playStartHint(int stepId){
		if(playedStartMp3.get(stepId)==null){
			String fileName = Util.getVoiceFileName(Util.startHint, stepId);  //this.startHintMp3.get(stepId);
			File file = new File(fileName);
			if(file.exists() && file.length()>0){
				activity.playMusicIntent(fileName);
				playedStartMp3.append(stepId, fileName);
				Toast.makeText(activity, activity.gMap.pos.steps.get(stepId).getStartHint(), Toast.LENGTH_LONG).show();
			}
			Log.i(TAG, "playStartHint:"+fileName);
		}
	}
	public void playEndHint(int stepId){
		if(playedEndMp3.get(stepId)==null){
			String fileName = Util.getVoiceFileName(Util.endHint, stepId);
			File file = new File(fileName);
			if(file.exists() && file.length()>0 ){
				activity.playMusicIntent(fileName);
				playedEndMp3.append(stepId, fileName);
				Toast.makeText(activity, activity.gMap.pos.steps.get(stepId).getEndHint(), Toast.LENGTH_LONG).show();
			}
		}
	}
	public void playEnd500Hint(int stepId){
		if(playedEndMp3.get(stepId)==null){
			String fileName = Util.getVoiceFileName(Util.end500Hint, stepId);
			File file = new File(fileName);
			if(file.exists() && file.length()>0 ){
				activity.playMusicIntent(fileName);
				playedEndMp3.append(stepId, fileName);
				Toast.makeText(activity, activity.gMap.pos.steps.get(stepId).getEndHint(), Toast.LENGTH_LONG).show();
			}
		}
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.stop();
        mp.reset();
	}
}
