package wsn.park.navi.task;

import java.io.UnsupportedEncodingException;
import java.util.List;

import wsn.park.LOC;

import android.location.Address;
import android.util.Log;

public class GoogleAPISearchByAddress {
    private static final String TAG = GoogleAPISearchByAddress.class.getSimpleName();

	public static List<Address> search(String name) {
		
		return null;
	}
    public String getLocationURL(String address) {
        String parsedValue = null;
		try {
			parsedValue = java.net.URLEncoder.encode(address, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        String sensor = "&sensor=false";
        String language = "&Accept-Language:zh-CN";
        String region = "&components=country:"+LOC.countryCode;
        String geoapi = "https://maps.googleapis.com/maps/api/geocode/";
        String format = "json";
        String addressURL = "?address="+parsedValue;
		String url = geoapi +format+addressURL+language+sensor+region;
        Log.i(TAG,"getLocationURL--->: " + url);
        return url;
    }
}
