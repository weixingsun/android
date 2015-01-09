package cat.app.navi.task;

import java.io.IOException;
import java.util.List;

import org.osmdroid.bonuspack.location.GeocoderGisgraphy;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import cat.app.maps.OSM;

public class Geocoders {
	private static final String tag = Geocoders.class.getSimpleName();
	
	String provider;
	GeocoderNominatim gn;
	GeocoderGisgraphy gg;
	Geocoder gc;
	List<Address> list;
	Activity act;
	public Geocoders(Activity act) {
		this.act = act;
	}
	public List<Address> getFromLocationName(String provider, String name){
		this.provider = provider;
		try{
		switch(provider){
			case "google": {
				gc = new Geocoder(act); 
				return gc.getFromLocationName(name, 3);
			}
			case "osm": {
				gn = new GeocoderNominatim(act); 
				return gn.getFromLocationName(name, 3);
			}
			case "gisgraphy": {
				gg = new GeocoderGisgraphy(act); 
				return gg.getFromLocationName(name, 3);
			}
			default: 
				Log.i(tag, "default geocoder ?");
		}
		}catch(IOException e){
			Log.i(tag, "IOException"+e.getMessage());
		}
		return null;
	}
	public Address getFromLocation(String provider,GeoPoint position) {
		this.provider = provider;
		double lat = position.getLatitude();
		double lng = position.getLongitude();
		try{
		switch(provider){
			case "google": {
				gc = new Geocoder(act); 
				return gc.getFromLocation(lat,lng, 1).get(0);
			}
			case "osm": {
				gn = new GeocoderNominatim(act); 
				return gn.getFromLocation(lat,lng, 1).get(0);
			}
			case "gisgraphy": {
				gg = new GeocoderGisgraphy(act);
				return null; //gg.getFromLocation(lat,lng, 3); //why not implemented?
			}
			default: 
				Log.i(tag, "default geocoder ?");
		}
		}catch(IOException e){
			Log.i(tag, "IOException"+e.getMessage());
		}
		return null;
	}
}
