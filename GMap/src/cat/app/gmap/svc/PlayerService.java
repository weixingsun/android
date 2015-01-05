package cat.app.gmap.svc;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener{
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		String fileName = intent.getStringExtra("file");
        return 0;
    }
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}

}
