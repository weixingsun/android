package wsn.park.util;

import java.util.Locale;

import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.telephony.TelephonyManager;

public class CountryCode {

	//NZ.lat(-33,-47)
	//NZ.lng(165,179)
	//CN.lat(5,55)
	//CN.lng(73,134)
	public static String getByLatLng(double lat, double lng){
		if(		lat< -33 && lat > -47 
			&&  lng< 179 && lng > 165 ){
			return "nz";
		}else if (lat<55 && lat > 5
			&&  lng> 134 && lng > 73  ){
			return "cn";
		}
		return null;
	}
	public static String getByGeoPoint(GeoPoint p){
		return getByLatLng(p.getLatitude(),p.getLongitude());
	}


/**
 * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
 * @param context Context reference to get the TelephonyManager instance from
 * @return country code or null
 */
public static String getBySim(Context context) {
    try {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String simCountry = tm.getSimCountryIso();
        if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
            return simCountry.toLowerCase(Locale.US);
        }
        else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
            String networkCountry = tm.getNetworkCountryIso();
            if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                return networkCountry.toLowerCase(Locale.US);
            }
        }
    }
    catch (Exception e) { }
    return null;
}


}
