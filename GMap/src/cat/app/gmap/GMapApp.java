package cat.app.gmap;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

//formUri = "http://servicedata.vhostall.com/reportcrash"
@ReportsCrashes(
		formKey = "", 
		mailTo = "sun.app.service@gmail.com"
		)
public class GMapApp extends Application{

	@Override
	public void onCreate(){
		super.onCreate();
		ACRA.init(this);
	}
}
