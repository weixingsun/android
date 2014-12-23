package cat.app.gmap.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cat.app.gmap.MainActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetAddressTask extends AsyncTask<Location, Void, String> {
	MainActivity act;
	private static final String TAG = "GMap.GetAddressTask";
	public GetAddressTask(MainActivity act) {
		super();
		this.act = act;
	}

	@Override
	protected String doInBackground(Location... params) {
		Geocoder geocoder = new Geocoder(act, Locale.getDefault());
        Location loc = params[0];
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(), 1);
        } catch (IOException e1) {
	        Log.e(TAG,"IO Exception in getFromLocation(): ");
	        return null;
        } catch (IllegalArgumentException e2) {
	        Log.e("LocationSampleActivity", e2.getMessage());
	        return null;
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getCountryCode();
        }
		return null;
	}
	
	@Override
    protected void onPostExecute(String code) {
        act.gMap.myCountryCode=code;
        //Toast.makeText(act, "myCountryCode:"+code, Toast.LENGTH_LONG).show();
        Log.i(TAG, "myCountryCode="+code);
    }
}
