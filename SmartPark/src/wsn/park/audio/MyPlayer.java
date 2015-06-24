package wsn.park.audio;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.bonuspack.routing.RoadNode;

import wsn.park.R;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.ExoPlayer.Listener;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;

public class MyPlayer {
	private static final String tag = MyPlayer.class.getSimpleName();
	private static ExoPlayer player;
	public static String play(Activity act, int distance,int maneuverType) {
		String ret = null;
		String fileName = "type_"+maneuverType+"_dist_"+distance;	//+".mp3"
		/*File mp3  = new File(filePath);
		if(!mp3.exists()) {
			Log.w(tag, "file="+filePath);
			return;
		}
		Uri uri = Uri.fromFile(new File(filePath));*/
		Uri uri = getUriByName("raw/"+fileName);
		if(!checkRawList(fileName)) {
			ret = "not in raw list:"+fileName;
			return ret;
		}
	    FrameworkSampleSource sampleSource = new FrameworkSampleSource(act, uri, null, 1);
	    // Build the track renderers
	    TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
	    //new MediaCodecAudioTrackRenderer(sampleSource, null, true);
	    //format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
	    //A component of name 'OMX.qcom.audio.decoder.aac' already exists, ignoring this one.
	    //Log.w(tag, "init audioRenderer.");
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
					//Log.i(tag, "player released");
				}
			}});
	    return null;
	}
	public static void test(Activity act){
		//play(act,folder+"turn-left.mp3");
	}
	public static String play(Activity act, RoadNode node, int dist) {
		// "in 400 m" + "turn right"
		return play(act,dist, node.mManeuverType);  //distance+turn
	}
	public static Uri getUriByName(String res){
		return Uri.parse("android.resource://cat.app.osmap/"+res);  //drawable/icon
	}
	public static boolean checkRawList(String fileName){
		java.lang.reflect.Field[] fields=R.raw.class.getFields();
	    for(int count=0; count < fields.length; count++){
	    	String resName = fields[count].getName();
	    	if(resName.equals(fileName)){
		        Log.i(tag, "res="+resName+", file="+fileName);
	    		return true;
	    	}
	    }
	    Log.i("Raw Asset: ", "not in raw list, file="+fileName);
	    return false;
	}
}
/*
	static {
		MANEUVERS = new HashMap<String, Integer>();
		MANEUVERS.put("0", 0); //No instruction
		MANEUVERS.put("1", 1); //Continue(1)	(dist_0_type_1.mp3)
		MANEUVERS.put("2", 6); //Slight right
		MANEUVERS.put("3", 7); //Right			(dist_0_type_7.mp3)
		MANEUVERS.put("4", 8); //Sharp right
		MANEUVERS.put("5", 12); //U-turn
		MANEUVERS.put("6", 5); //Sharp left
		MANEUVERS.put("7", 4); //Left			(dist_0_type_4.mp3)
		MANEUVERS.put("8", 3); //Slight left	(dist_0_type_3.mp3)
		MANEUVERS.put("9", 24); //Arrived (at waypoint)
		MANEUVERS.put("10", 24); //"Head" => used by OSRM as the start node. Considered here as a "waypoint". 
		MANEUVERS.put("11-1", 27); //Round-about, 1st exit
		MANEUVERS.put("11-2", 28); //2nd exit, etc ...
		MANEUVERS.put("11-3", 29);
		MANEUVERS.put("11-4", 30);
		MANEUVERS.put("11-5", 31);
		MANEUVERS.put("11-6", 32);
		MANEUVERS.put("11-7", 33);
		MANEUVERS.put("11-8", 34); //Round-about, 8th exit
		MANEUVERS.put("15", 24); //Arrived
	}
*/