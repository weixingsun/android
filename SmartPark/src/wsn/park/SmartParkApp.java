package wsn.park;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

//formUri = "http://servicedata.vhostall.com/reportcrash"
@ReportsCrashes(
		mailTo = "sun.app.service@gmail.com"
		)
public class SmartParkApp extends Application{

	@Override
	public void onCreate(){
		ACRA.init(this);
		super.onCreate();
	}
}
