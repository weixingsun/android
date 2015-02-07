package cat.app.navi.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import cat.app.osmap.LOC;
import cat.app.osmap.util.GeoOptions;

import com.gisgraphy.gisgraphoid.GisgraphyGeocoder;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

public class Geocoders {
	private static final String tag = Geocoders.class.getSimpleName();
	
	String provider;
	List<Address> list;
	Activity act;
	public Geocoders(Activity act) {
		this.act = act;
	}
	public List<Address> getFromLocationNameGoogle(String name){
		List<Address> list = null;
		try {
			list = (new android.location.Geocoder(act)).getFromLocationName(name, 3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	public List<Address> getFromLocationName(String provider, String name){
		this.provider = provider;
		List<Address> list = null;
		try{
		switch(provider){
			case GeoOptions.GOOGLE: {
				android.location.Geocoder gc = new android.location.Geocoder(act);
				list = gc.getFromLocationName(name, 3);
			}
			case GeoOptions.OFFLINE:	//use OSM temporary
			case GeoOptions.OSM: {
				org.osmdroid.bonuspack.location.GeocoderNominatim gn = new org.osmdroid.bonuspack.location.GeocoderNominatim(act);  //nominatim.openstreetmap.org/
				list = gn.getFromLocationName(name, 3);
			}
			case GeoOptions.GISGRAPHY: {
				com.gisgraphy.gisgraphoid.GisgraphyGeocoder gg = new com.gisgraphy.gisgraphoid.GisgraphyGeocoder(act); 
				list = gg.getFromLocationName(name, 3);
			}
			case GeoOptions.MAPQUEST: {
				com.mapquest.android.Geocoder gg = new com.mapquest.android.Geocoder(act);
				list = gg.getFromLocationName(name, 3);
			}
			default: 
				Log.i(tag, "default geocoder ?");
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
		try{
		switch(provider){
			case GeoOptions.GOOGLE: {
				android.location.Geocoder gc = new android.location.Geocoder(act); 
				return gc.getFromLocation(lat,lng, 1).get(0);
			}
			case GeoOptions.OFFLINE:	//use OSM temporary
			case GeoOptions.OSM: {
				org.osmdroid.bonuspack.location.GeocoderNominatim gn = new org.osmdroid.bonuspack.location.GeocoderNominatim(act); 
				return gn.getFromLocation(lat,lng, 1).get(0);
			}
			case GeoOptions.GISGRAPHY: {
				com.gisgraphy.gisgraphoid.GisgraphyGeocoder gg = new com.gisgraphy.gisgraphoid.GisgraphyGeocoder(act);
				return gg.getFromLocation(lat, lng, 1).get(0);
			}
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
