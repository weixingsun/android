package wsn.park.navi.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.model.SavedPlace;
import wsn.park.util.GeoOptions;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
//import com.gisgraphy.gisgraphoid.GisgraphyGeocoder;

public class Geocoders {
	private static final String tag = Geocoders.class.getSimpleName();
	
	String provider;
	List<Address> list;
	List<SavedPlace> splist;
	Activity act;
	public Geocoders(Activity act) {
		this.act = act;
	}

	public List<Address> getFromLocationName(String provider, String name){
		this.provider = provider;
		List<Address> list = null;
		Log.i(tag, "GeoFromName."+provider);
		try{
		switch(provider){
			case GeoOptions.GOOGLE: {
				android.location.Geocoder gc = new android.location.Geocoder(act);
				list = gc.getFromLocationName(name, 3);
				break;
			}
			//case GeoOptions.OFFLINE:	//not available
			case GeoOptions.OSM: {
				org.osmdroid.bonuspack.location.GeocoderNominatim gn = new org.osmdroid.bonuspack.location.GeocoderNominatim(act);  //nominatim.openstreetmap.org/
				list = gn.getFromLocationName(name, 3);
				break;
			}
			/*case GeoOptions.GISGRAPHY: {
				com.gisgraphy.gisgraphoid.GisgraphyGeocoder gg = new com.gisgraphy.gisgraphoid.GisgraphyGeocoder(act); 
				list = gg.getFromLocationName(name, 3);
			}*/
			case GeoOptions.MAPQUEST: {
				com.mapquest.android.Geocoder gg = new com.mapquest.android.Geocoder(act);
				list = gg.getFromLocationName(name, 3);
				break;
			}
			default: 
				Log.i(tag, "default geocoder ?"+provider);
		}
		}catch(IOException e){
			Log.i(tag, "IOException"+e.getMessage());
		}
		return list;
	}
	public Address getFromLocation(String provider,GeoPoint position) {
		this.provider = provider;
		double lat = position.getLatitude();
		double lng = position.getLongitude();
		Log.i(tag, "GeoFromLocation."+provider);
		try{
		switch(provider){
			case GeoOptions.GOOGLE: {
				android.location.Geocoder gc = new android.location.Geocoder(act); 
				return gc.getFromLocation(lat,lng, 1).get(0);
			}
			case GeoOptions.OFFLINE:	//not available
			case GeoOptions.OSM: {
				org.osmdroid.bonuspack.location.GeocoderNominatim gn = new org.osmdroid.bonuspack.location.GeocoderNominatim(act); 
				return gn.getFromLocation(lat,lng, 1).get(0);
			}
			/*case GeoOptions.GISGRAPHY: {
				com.gisgraphy.gisgraphoid.GisgraphyGeocoder gg = new com.gisgraphy.gisgraphoid.GisgraphyGeocoder(act);
				return gg.getFromLocation(lat, lng, 1).get(0);
			}*/
			case GeoOptions.MAPQUEST: {
				com.mapquest.android.Geocoder gc = new com.mapquest.android.Geocoder(act);
				return gc.getFromLocation(lat, lng, 1).get(0);
			}
			default: 
				Log.i(tag, "default geocoder ?");
		}
		}catch(Exception e){
			Log.i(tag, "Exception"+e.getMessage());
		}
		return null;
	}
}
