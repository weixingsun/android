package cat.app.audio;

import java.io.File;

import org.osmdroid.bonuspack.routing.RoadNode;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import cat.app.osmap.SavedOptions;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.ExoPlayer.Listener;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;

public class MyPlayer {
	private static final String tag = MyPlayer.class.getSimpleName();
	private static final String folder = SavedOptions.HINT_FILE_PATH;
	private static ExoPlayer player;
	public static void play(Activity act, int distance,int maneuverType) {
		String filePath = folder+"dist_"+distance+"_type_"+maneuverType+".mp3";
		File mp3  = new File(filePath);
		if(!mp3.exists()) {
			Log.w(tag, "file="+filePath);
			return;
			}
		Uri uri = Uri.fromFile(new File(filePath));
	    FrameworkSampleSource sampleSource = new FrameworkSampleSource(act, uri, null, 1);
	    // Build the track renderers
	    TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
	    //new MediaCodecAudioTrackRenderer(sampleSource, null, true);
	    //format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
	    //A component of name 'OMX.qcom.audio.decoder.aac' already exists, ignoring this one.
	    Log.w(tag, "init audioRenderer.");
	    player = ExoPlayer.Factory.newInstance(1);
	    player.prepare(audioRenderer);
	    player.setPlayWhenReady(true);
	    player.addListener(new Listener(){
			@Override
			public void onPlayWhenReadyCommitted() {}
			@Override
			public void onPlayerError(ExoPlaybackException arg0) {}
			@Override
			public void onPlayerStateChanged(boolean playWhenReady, int state) {
				if(state==ExoPlayer.STATE_ENDED){
					player.release();
					Log.i(tag, "player released");
				}
			}});
	}
	public static void test(Activity act){
		//play(act,folder+"turn-left.mp3");
	}
	public static void play(Activity act, RoadNode node, int dist) {
		// "in 400 m" + "turn right"
		play(act,dist, node.mManeuverType);  //distance+turn
	}
	
}
